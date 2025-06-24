package com.example.pointgrapher.domain.usecase

import com.example.pointgrapher.TestDataFactory
import com.example.pointgrapher.domain.exception.BatchNotFoundException
import com.example.pointgrapher.domain.repository.PointProviderRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class GetPointsBatchUseCaseTest {

    private val repository = mockk<PointProviderRepository>()

    private val useCase by lazy { GetPointsBatchUseCase(repository) }

    @Test
    fun `when valid batch id then returns point batch`() = runTest {
        // Given
        val batchId = "valid-batch-id"
        val expectedPointBatch = TestDataFactory.createPointBatch(5)
        coEvery { repository.getPointsBatch(batchId) } returns expectedPointBatch

        // When
        val result = useCase(batchId)

        // Then
        assertThat(result).isEqualTo(expectedPointBatch)
        assertThat(result.x).hasSize(5)
        assertThat(result.y).hasSize(5)
        coVerify { repository.getPointsBatch(batchId) }
    }

    @Test
    fun `when batch not found then throws batch not found exception`() = runTest {
        // Given
        val nonExistentBatchId = TestDataFactory.Error.INVALID_BATCH_ID
        val exception = BatchNotFoundException(nonExistentBatchId)
        coEvery { repository.getPointsBatch(nonExistentBatchId) } throws exception

        // When & Then
        try {
            useCase(nonExistentBatchId)
        } catch (e: BatchNotFoundException) {
            assertThat(e.message).contains(nonExistentBatchId)
        }

        coVerify { repository.getPointsBatch(nonExistentBatchId) }
    }

    @Test
    fun `when empty batch returned then returns empty point batch`() = runTest {
        // Given
        val batchId = "empty-batch-id"
        val emptyPointBatch = TestDataFactory.Edge.emptyPointBatch()
        coEvery { repository.getPointsBatch(batchId) } returns emptyPointBatch

        // When
        val result = useCase(batchId)

        // Then
        assertThat(result.x).isEmpty()
        assertThat(result.y).isEmpty()
        coVerify { repository.getPointsBatch(batchId) }
    }

    @Test
    fun `when single point batch returned then returns single point batch`() = runTest {
        // Given
        val batchId = "single-point-batch"
        val singlePointBatch = TestDataFactory.Edge.singlePoint()
        coEvery { repository.getPointsBatch(batchId) } returns singlePointBatch

        // When
        val result = useCase(batchId)

        // Then
        assertThat(result.x).hasSize(1)
        assertThat(result.y).hasSize(1)
        assertThat(result.x[0]).isEqualTo(1.0)
        assertThat(result.y[0]).isEqualTo(2.0)
        coVerify { repository.getPointsBatch(batchId) }
    }

    @Test
    fun `when different batch ids then passes correct parameter to repository`() = runTest {
        // Given
        val testBatchIds = listOf("batch-1", "batch-2", "batch-3")

        for (batchId in testBatchIds) {
            val pointBatch = TestDataFactory.createPointBatch(3)
            coEvery { repository.getPointsBatch(batchId) } returns pointBatch

            // When
            val result = useCase(batchId)

            // Then
            assertThat(result).isEqualTo(pointBatch)
            coVerify { repository.getPointsBatch(batchId) }
        }
    }

    @Test
    fun `when repository throws runtime exception then propagates exception`() = runTest {
        // Given
        val batchId = "problematic-batch"
        val errorMassage = "db connection error"
        coEvery { repository.getPointsBatch(batchId) } throws RuntimeException(errorMassage)

        // When & Then
        try {
            useCase(batchId)
        } catch (e: RuntimeException) {
            assertThat(e.message).isEqualTo(errorMassage)
            assertThat(e).isNotInstanceOf(BatchNotFoundException::class.java)
        }

        coVerify { repository.getPointsBatch(batchId) }
    }
}