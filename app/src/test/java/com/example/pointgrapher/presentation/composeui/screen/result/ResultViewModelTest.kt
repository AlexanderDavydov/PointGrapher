package com.example.pointgrapher.presentation.composeui.screen.result

import app.cash.turbine.turbineScope
import com.example.pointgrapher.TestDataFactory
import com.example.pointgrapher.domain.exception.BatchNotFoundException
import com.example.pointgrapher.domain.model.PointBatch
import com.example.pointgrapher.domain.usecase.GetPointsBatchUseCase
import com.example.pointgrapher.presentation.composeui.screen.result.ResultState
import com.example.pointgrapher.presentation.composeui.screen.result.ResultViewModel
import com.example.pointgrapher.util.UnconfinedCoroutinesExtension
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(UnconfinedCoroutinesExtension::class)
class ResultViewModelTest {

    private val getPointsBatchUseCase = mockk<GetPointsBatchUseCase>()
    private lateinit var viewModel: ResultViewModel

    @Nested
    inner class StateManagement {

        @Test
        fun `when view model created then has loading state`() = runTest {
            // Given
            // mocked dependencies

            // When
            viewModel = ResultViewModel(getPointsBatchUseCase)

            // Then
            assertThat(viewModel.state.value).isEqualTo(ResultState.Loading)
        }

        @Test
        fun `when loading starts and data requested then state changes to success`() = runTest {
            turbineScope {
                // Given
                val batchId = "test-batch"
                val testBatch = TestDataFactory.createPointBatch(5)
                coEvery { getPointsBatchUseCase(batchId) } returns testBatch

                // When
                viewModel = ResultViewModel(getPointsBatchUseCase)
                val state = viewModel.state.testIn(backgroundScope)
                val initialState = state.awaitItem()
                viewModel.onBatchIdChanged(batchId)
                val successState = state.awaitItem()

                // Then
                assertThat(initialState).isEqualTo(ResultState.Loading)
                assertThat(successState).isInstanceOf(ResultState.Success::class.java)
            }
        }

        @Test
        fun `when repository throws exception then shows error state`() = runTest {
            // Given
            val batchId = "problematic-batch"
            val exception = RuntimeException("Database connection error")
            coEvery { getPointsBatchUseCase(batchId) } throws exception

            viewModel = ResultViewModel(getPointsBatchUseCase)

            // When
            viewModel.onBatchIdChanged(batchId)

            // Then
            assertThat(viewModel.state.value).isEqualTo(ResultState.Error)
            coVerify { getPointsBatchUseCase(batchId) }
        }
    }

    @Nested
    inner class BatchLoading {

        @Test
        fun `when valid batch id provided then loads points successfully`() = runTest {
            // Given
            val batchId = "valid-batch-id"
            val expectedPointBatch = TestDataFactory.createPointBatch(5)
            coEvery { getPointsBatchUseCase(batchId) } returns expectedPointBatch

            viewModel = ResultViewModel(getPointsBatchUseCase)

            // When
            viewModel.onBatchIdChanged(batchId)

            // Then
            val state = viewModel.state.value
            assertThat(state).isInstanceOf(ResultState.Success::class.java)

            val successState = state as ResultState.Success
            assertThat(successState.points.x).hasSize(5)
            assertThat(successState.points.y).hasSize(5)

            coVerify { getPointsBatchUseCase(batchId) }
        }

        @Test
        fun `when batch not found then shows error state`() = runTest {
            // Given
            val nonExistentBatchId = "non-existent-batch"
            val batchNotFoundException = BatchNotFoundException(nonExistentBatchId)
            coEvery { getPointsBatchUseCase(nonExistentBatchId) } throws batchNotFoundException

            viewModel = ResultViewModel(getPointsBatchUseCase)

            // When
            viewModel.onBatchIdChanged(nonExistentBatchId)

            // Then
            assertThat(viewModel.state.value).isEqualTo(ResultState.Error)
            coVerify { getPointsBatchUseCase(nonExistentBatchId) }
        }
    }

    @Nested
    inner class DataMapping {

        @Test
        fun `when points loaded then sorts by x coordinate`() = runTest {
            // Given
            val batchId = "unsorted-batch"
            val unsortedPointBatch = TestDataFactory.Edge.unsortedPoints()
            coEvery { getPointsBatchUseCase(batchId) } returns unsortedPointBatch

            viewModel = ResultViewModel(getPointsBatchUseCase)

            // When
            viewModel.onBatchIdChanged(batchId)

            // Then
            val state = viewModel.state.value as ResultState.Success

            // Should be sorted by X coordinate: (1,2), (2,4), (3,6), (4,8), (5,10)
            assertThat(state.points.x).containsExactly(1.0, 2.0, 3.0, 4.0, 5.0).inOrder()
            assertThat(state.points.y).containsExactly(2.0, 4.0, 6.0, 8.0, 10.0).inOrder()
        }

        @Test
        fun `when duplicate x values then preserves order`() = runTest {
            // Given
            val batchId = "duplicate-x-batch"
            val duplicateXBatch = TestDataFactory.Edge.duplicateXValues()
            coEvery { getPointsBatchUseCase(batchId) } returns duplicateXBatch

            viewModel = ResultViewModel(getPointsBatchUseCase)

            // When
            viewModel.onBatchIdChanged(batchId)

            // Then
            val state = viewModel.state.value as ResultState.Success

            // Original: (1,2), (1,3), (2,4) - should preserve order for same X values
            assertThat(state.points.x).containsExactly(1.0, 1.0, 2.0).inOrder()
            assertThat(state.points.y).containsExactly(2.0, 3.0, 4.0).inOrder()
        }

        @Test
        fun `when negative coordinates then handles correctly`() = runTest {
            // Given
            val batchId = "negative-coords-batch"
            val negativeCoordsBatch = PointBatch(
                x = listOf(-2.0, 0.0, 1.0, -1.0),
                y = listOf(-4.0, 0.0, 2.0, -2.0)
            )
            coEvery { getPointsBatchUseCase(batchId) } returns negativeCoordsBatch

            viewModel = ResultViewModel(getPointsBatchUseCase)

            // When
            viewModel.onBatchIdChanged(batchId)

            // Then
            val state = viewModel.state.value as ResultState.Success

            // Should sort by X: (-2,-4), (-1,-2), (0,0), (1,2)
            assertThat(state.points.x).containsExactly(-2.0, -1.0, 0.0, 1.0).inOrder()
            assertThat(state.points.y).containsExactly(-4.0, -2.0, 0.0, 2.0).inOrder()
        }
    }
}