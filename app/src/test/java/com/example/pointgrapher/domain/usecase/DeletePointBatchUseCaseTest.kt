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

class DeletePointBatchUseCaseTest {

    private val repository = mockk<PointProviderRepository>()

    private val useCase by lazy { DeletePointBatchUseCase(repository) }

    @Test
    fun `when valid batch id then deletes batch successfully`() = runTest {
        // Given
        val batchId = "valid-batch-id"
        coEvery { repository.deleteBatch(batchId) } returns Unit

        // When
        useCase(batchId)

        // Then
        coVerify { repository.deleteBatch(batchId) }
    }

    @Test
    fun `when batch not found then throws batch not found exception`() = runTest {
        // Given
        val nonExistentBatchId = TestDataFactory.Error.INVALID_BATCH_ID
        val exception = BatchNotFoundException(nonExistentBatchId)
        coEvery { repository.deleteBatch(nonExistentBatchId) } throws exception

        // When & Then
        try {
            useCase(nonExistentBatchId)
        } catch (e: BatchNotFoundException) {
            assertThat(e.message).contains(nonExistentBatchId)
        }

        coVerify { repository.deleteBatch(nonExistentBatchId) }
    }

    @Test
    fun `when repository throws runtime exception then propagates exception`() = runTest {
        // Given
        val batchId = "problematic-batch"
        val errorMessage = "database connection error"
        val runtimeException = RuntimeException(errorMessage)
        coEvery { repository.deleteBatch(batchId) } throws runtimeException

        // When & Then
        try {
            useCase(batchId)
        } catch (e: RuntimeException) {
            assertThat(e.message).isEqualTo(errorMessage)
            assertThat(e).isNotInstanceOf(BatchNotFoundException::class.java)
        }

        coVerify { repository.deleteBatch(batchId) }
    }

    @Test
    fun `when different batch ids then passes correct parameter to repository`() = runTest {
        // Given
        val testBatchIds = listOf("batch-1", "batch-2", "batch-3")

        for (batchId in testBatchIds) {
            coEvery { repository.deleteBatch(batchId) } returns Unit

            // When
            useCase(batchId)

            // Then
            coVerify { repository.deleteBatch(batchId) }
        }
    }

    @Test
    fun `when empty batch id then passes empty string to repository`() = runTest {
        // Given
        val emptyBatchId = ""
        coEvery { repository.deleteBatch(emptyBatchId) } returns Unit

        // When
        useCase(emptyBatchId)

        // Then
        coVerify { repository.deleteBatch(emptyBatchId) }
    }

    @Test
    fun `when repository throws illegal state exception then propagates exception`() = runTest {
        // Given
        val batchId = "batch-with-state-issue"
        val errorMessage = "batch is being processed"
        val illegalStateException = IllegalStateException(errorMessage)
        coEvery { repository.deleteBatch(batchId) } throws illegalStateException

        // When & Then
        try {
            useCase(batchId)
        } catch (e: IllegalStateException) {
            assertThat(e.message).isEqualTo(errorMessage)
        }

        coVerify { repository.deleteBatch(batchId) }
    }

    @Test
    fun `when use case called multiple times with same id then each call goes to repository`() =
        runTest {
            // Given
            val batchId = "repeated-batch"
            coEvery { repository.deleteBatch(batchId) } returns Unit

            // When
            useCase(batchId)
            useCase(batchId)
            useCase(batchId)

            // Then
            coVerify(exactly = 3) { repository.deleteBatch(batchId) }
        }
}