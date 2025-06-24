package com.example.pointgrapher.data.repository

import com.example.pointgrapher.TestDataFactory
import com.example.pointgrapher.data.datasource.PointLocalDataSource
import com.example.pointgrapher.data.datasource.PointRemoteDataSource
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class PointProviderRepositoryTest {

    private val remoteDataSource = mockk<PointRemoteDataSource>()
    private val localDataSource = mockk<PointLocalDataSource>()

    private val repository by lazy {
        PointProviderRepositoryImpl(remoteDataSource, localDataSource)
    }

    @Test
    fun `when requesting points then fetches from remote and saves to local`() = runTest {
        // Given
        val count = 5
        val pointDtos = TestDataFactory.createPointDtoList(count)
        val batchIdSlot = slot<String>()

        coEvery { remoteDataSource.getPoints(count) } returns pointDtos
        coEvery { localDataSource.saveBatch(capture(batchIdSlot), pointDtos) } returns Unit

        // When
        val result = repository.requestPoints(count)

        // Then
        assertThat(result).isNotEmpty()
        assertThat(result).isEqualTo(batchIdSlot.captured)

        coVerify { remoteDataSource.getPoints(count) }
        coVerify { localDataSource.saveBatch(result, pointDtos) }
    }

    @Test
    fun `when getting batch then retrieves from local and maps to point batch`() = runTest {
        // Given
        val batchId = "test-batch-id"
        val pointDtos = TestDataFactory.createPointDtoList(3)

        coEvery { localDataSource.getBatch(batchId) } returns pointDtos

        // When
        val result = repository.getPointsBatch(batchId)

        // Then
        assertThat(result.x).hasSize(3)
        assertThat(result.y).hasSize(3)
        assertThat(result.x).containsExactly(1.0, 2.0, 3.0).inOrder()
        assertThat(result.y).containsExactly(2.0, 4.0, 6.0).inOrder()

        coVerify { localDataSource.getBatch(batchId) }
    }

    @Test
    fun `when observing batches then maps to info and emits flow`() = runTest {
        // Given
        val batchDtos = listOf(
            TestDataFactory.createBatchDto("batch1", 5, 1000L),
            TestDataFactory.createBatchDto("batch2", 10, 2000L)
        )
        coEvery { localDataSource.observeAllBatches() } returns flowOf(batchDtos)

        // When
        val result = repository.observeAllBatches()

        // Then
        result.collect { batchInfos ->
            assertThat(batchInfos).hasSize(2)

            assertThat(batchInfos[0].id).isEqualTo("batch1")
            assertThat(batchInfos[0].numberOfPoints).isEqualTo(5)
            assertThat(batchInfos[0].timestamp).isEqualTo(1000L)

            assertThat(batchInfos[1].id).isEqualTo("batch2")
            assertThat(batchInfos[1].numberOfPoints).isEqualTo(10)
            assertThat(batchInfos[1].timestamp).isEqualTo(2000L)
        }

        coVerify { localDataSource.observeAllBatches() }
    }

    @Test
    fun `when deleting batch then delegates to local data source`() = runTest {
        // Given
        val batchId = "batch-to-delete"
        coEvery { localDataSource.deleteBatch(batchId) } returns Unit

        // When
        repository.deleteBatch(batchId)

        // Then
        coVerify { localDataSource.deleteBatch(batchId) }
    }

    @Test
    fun `when remote source throws exception then propagates error`() = runTest {
        // Given
        val count = 5
        val networkException = RuntimeException(TestDataFactory.Error.NETWORK_ERROR_MESSAGE)
        coEvery { remoteDataSource.getPoints(count) } throws networkException

        // When & Then
        try {
            repository.requestPoints(count)
        } catch (e: RuntimeException) {
            assertThat(e.message).isEqualTo(TestDataFactory.Error.NETWORK_ERROR_MESSAGE)
        }

        coVerify { remoteDataSource.getPoints(count) }
        coVerify(exactly = 0) { localDataSource.saveBatch(any(), any()) }
    }

    @Test
    fun `when empty points list received then creates empty point batch`() = runTest {
        // Given
        val batchId = "empty-batch"
        val emptyPointDtos = emptyList<com.example.pointgrapher.data.model.PointDto>()

        coEvery { localDataSource.getBatch(batchId) } returns emptyPointDtos

        // When
        val result = repository.getPointsBatch(batchId)

        // Then
        assertThat(result.x).isEmpty()
        assertThat(result.y).isEmpty()

        coVerify { localDataSource.getBatch(batchId) }
    }

    @Test
    fun `when single point received then creates single point batch`() = runTest {
        // Given
        val batchId = "single-point-batch"
        val singlePointDto = listOf(TestDataFactory.createPointDto(5.5, 7.7))

        coEvery { localDataSource.getBatch(batchId) } returns singlePointDto

        // When
        val result = repository.getPointsBatch(batchId)

        // Then
        assertThat(result.x).hasSize(1)
        assertThat(result.y).hasSize(1)
        assertThat(result.x[0]).isEqualTo(5.5)
        assertThat(result.y[0]).isEqualTo(7.7)

        coVerify { localDataSource.getBatch(batchId) }
    }
}