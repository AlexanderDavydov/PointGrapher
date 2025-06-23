package com.example.pointgrapher.domain.usecase

import com.example.pointgrapher.TestDataFactory
import com.example.pointgrapher.domain.exception.NetworkError
import com.example.pointgrapher.domain.repository.PointProviderRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class RequestPointsBatchUseCaseTest {

    private val repository = mockk<PointProviderRepository>()

    private val useCase by lazy { RequestPointsBatchUseCase(repository) }

    @Test
    fun `when valid count provided then returns batch id`() = runTest {
        // Given
        val count = 10
        val expectedBatchId = "generated-batch-id"
        coEvery { repository.requestPoints(count) } returns expectedBatchId

        // When
        val result = useCase(count)

        // Then
        assertThat(result).isEqualTo(expectedBatchId)
        coVerify { repository.requestPoints(count) }
    }

    @Test
    fun `when repository throws exception then throw network error`() = runTest {
        // Given
        val count = 5
        val originalException = RuntimeException(TestDataFactory.Error.NETWORK_ERROR_MESSAGE)
        coEvery { repository.requestPoints(count) } throws originalException

        // When & Then
        try {
            useCase(count)
        } catch (e: NetworkError) {
            assertThat(e.cause).isEqualTo(originalException)
        }

        coVerify { repository.requestPoints(count) }
    }

    @Test
    fun `when repository throws illegal argument exception then wraps in network error`() =
        runTest {
            // Given
            val count = -1
            val illegalArgumentException = IllegalArgumentException()
            coEvery { repository.requestPoints(count) } throws illegalArgumentException

            // When & Then
            try {
                useCase(count)
            } catch (e: NetworkError) {
                assertThat(e.cause).isEqualTo(illegalArgumentException)
                assertThat(e.cause).isInstanceOf(IllegalArgumentException::class.java)
            }

            coVerify { repository.requestPoints(count) }
        }

    @Test
    fun `when repository throws runtime exception then wraps in network error`() = runTest {
        // Given
        val count = 100
        val runtimeException = RuntimeException()
        coEvery { repository.requestPoints(count) } throws runtimeException

        // When & Then
        try {
            useCase(count)
        } catch (e: NetworkError) {
            assertThat(e.cause).isEqualTo(runtimeException)
        }

        coVerify { repository.requestPoints(count) }
    }

    @Test
    fun `when different count values then passes correct parameter to repository`() = runTest {
        // Given
        val testCounts = listOf(1, 50, 999)

        for (count in testCounts) {
            val expectedBatchId = "batch-for-$count"
            coEvery { repository.requestPoints(count) } returns expectedBatchId

            // When
            val result = useCase(count)

            // Then
            assertThat(result).isEqualTo(expectedBatchId)
            coVerify { repository.requestPoints(count) }
        }
    }
}