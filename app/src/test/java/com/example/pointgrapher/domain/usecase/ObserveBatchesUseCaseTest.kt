package com.example.pointgrapher.domain.usecase

import com.example.pointgrapher.TestDataFactory
import com.example.pointgrapher.domain.repository.PointProviderRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ObserveBatchesUseCaseTest {

    private val repository = mockk<PointProviderRepository>()

    private val useCase by lazy { ObserveBatchesUseCase(repository) }

    @Test
    fun `when repository emits batches then returns same flow`() = runTest {
        // Given
        val batchInfos = listOf(
            TestDataFactory.createBatchInfo("batch1", 5, 1000L),
            TestDataFactory.createBatchInfo("batch2", 10, 2000L),
            TestDataFactory.createBatchInfo("batch3", 3, 3000L)
        )
        every { repository.observeAllBatches() } returns flowOf(batchInfos)

        // When
        val result = useCase()

        // Then
        result.collect { batches ->
            assertThat(batches).hasSize(3)
            assertThat(batches[0].id).isEqualTo("batch1")
            assertThat(batches[0].numberOfPoints).isEqualTo(5)
            assertThat(batches[1].id).isEqualTo("batch2")
            assertThat(batches[1].numberOfPoints).isEqualTo(10)
            assertThat(batches[2].id).isEqualTo("batch3")
            assertThat(batches[2].numberOfPoints).isEqualTo(3)
        }

        verify { repository.observeAllBatches() }
    }

    @Test
    fun `when repository emits empty list then returns empty flow`() = runTest {
        // Given
        every { repository.observeAllBatches() } returns flowOf(emptyList())

        // When
        val result = useCase()

        // Then
        result.collect { batches ->
            assertThat(batches).isEmpty()
        }

        verify { repository.observeAllBatches() }
    }

    @Test
    fun `when repository emits single batch then returns single item flow`() = runTest {
        // Given
        val singleBatch = listOf(
            TestDataFactory.createBatchInfo("only-batch", 7, 5000L)
        )
        every { repository.observeAllBatches() } returns flowOf(singleBatch)

        // When
        val result = useCase()

        // Then
        result.collect { batches ->
            assertThat(batches).hasSize(1)
            assertThat(batches[0].id).isEqualTo("only-batch")
            assertThat(batches[0].numberOfPoints).isEqualTo(7)
            assertThat(batches[0].timestamp).isEqualTo(5000L)
        }

        verify { repository.observeAllBatches() }
    }

    @Test
    fun `when repository emits multiple emissions then each emission is received`() = runTest {
        // Given
        val firstEmission = listOf(TestDataFactory.createBatchInfo("batch1", 5))
        val secondEmission = listOf(
            TestDataFactory.createBatchInfo("batch1", 5),
            TestDataFactory.createBatchInfo("batch2", 8)
        )

        every { repository.observeAllBatches() } returns flowOf(firstEmission, secondEmission)

        // When
        val result = useCase()

        // Then
        val emissions = mutableListOf<List<com.example.pointgrapher.domain.model.BatchInfo>>()
        result.collect { batches ->
            emissions.add(batches)
        }

        assertThat(emissions).hasSize(2)
        assertThat(emissions[0]).hasSize(1)
        assertThat(emissions[1]).hasSize(2)

        verify { repository.observeAllBatches() }
    }

    @Test
    fun `when use case called multiple times then repository is called each time`() = runTest {
        // Given
        val batchInfos = listOf(TestDataFactory.createBatchInfo())
        every { repository.observeAllBatches() } returns flowOf(batchInfos)

        // When
        val firstCall = useCase()
        val secondCall = useCase()

        // Then - Both flows should work independently
        firstCall.collect { batches ->
            assertThat(batches).hasSize(1)
        }

        secondCall.collect { batches ->
            assertThat(batches).hasSize(1)
        }

        verify(exactly = 2) { repository.observeAllBatches() }
    }

    @Test
    fun `when repository flow contains batches with different timestamps then preserves order`() =
        runTest {
            // Given
            val batchInfos = listOf(
                TestDataFactory.createBatchInfo("newest", 5, 3000L),
                TestDataFactory.createBatchInfo("middle", 10, 2000L),
                TestDataFactory.createBatchInfo("oldest", 3, 1000L)
            )
            every { repository.observeAllBatches() } returns flowOf(batchInfos)

            // When
            val result = useCase()

            // Then
            result.collect { batches ->
                assertThat(batches).hasSize(3)
                // Verify order is preserved (as returned from repository)
                assertThat(batches[0].timestamp).isEqualTo(3000L)
                assertThat(batches[1].timestamp).isEqualTo(2000L)
                assertThat(batches[2].timestamp).isEqualTo(1000L)
            }

            verify { repository.observeAllBatches() }
        }
}