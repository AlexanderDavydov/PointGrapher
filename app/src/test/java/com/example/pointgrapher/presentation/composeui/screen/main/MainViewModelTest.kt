package com.example.pointgrapher.presentation.composeui.screen.main

import app.cash.turbine.turbineScope
import com.example.pointgrapher.TestDataFactory
import com.example.pointgrapher.domain.exception.NetworkError
import com.example.pointgrapher.domain.usecase.DeletePointBatchUseCase
import com.example.pointgrapher.domain.usecase.ObserveBatchesUseCase
import com.example.pointgrapher.domain.usecase.RequestPointsBatchUseCase
import com.example.pointgrapher.presentation.composeui.screen.main.MainViewModel
import com.example.pointgrapher.presentation.composeui.screen.main.viewdata.MainScreenErrorTypeViewData
import com.example.pointgrapher.util.UnconfinedCoroutinesExtension
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.Date

@ExtendWith(UnconfinedCoroutinesExtension::class)
class MainViewModelTest {

    private val requestPointsBatchUseCase = mockk<RequestPointsBatchUseCase>()
    private val deletePointBatchUseCase = mockk<DeletePointBatchUseCase>()
    private val observeBatchesUseCase = mockk<ObserveBatchesUseCase>()

    private lateinit var viewModel: MainViewModel

    @BeforeEach
    fun setUp() {
        // Default empty flow to prevent collection issues
        every { observeBatchesUseCase() } returns flowOf(emptyList())
    }

    @Nested
    inner class StateInitialization {

        @Test
        fun `when view model created then has default state`() = runTest {
            // Given
            // mocked dependencies

            // When
            viewModel = MainViewModel(
                requestPointsBatchUseCase,
                deletePointBatchUseCase,
                observeBatchesUseCase
            )

            // Then
            val initialState = viewModel.state.value
            assertThat(initialState.requiredPointNumber).isEqualTo("10")
            assertThat(initialState.isLoading).isFalse()
            assertThat(initialState.errorTypeViewData).isEqualTo(MainScreenErrorTypeViewData.None)
            assertThat(initialState.batches).isEmpty()
        }

        @Test
        fun `when view model created then subscribes to batches flow`() = runTest {
            // Given
            val batchInfos = listOf(
                TestDataFactory.createBatchInfo("batch1", 5, 1000L),
                TestDataFactory.createBatchInfo("batch2", 10, 2000L)
            )
            every { observeBatchesUseCase() } returns flowOf(batchInfos)

            // When
            viewModel = MainViewModel(
                requestPointsBatchUseCase,
                deletePointBatchUseCase,
                observeBatchesUseCase
            )

            // Then
            val state = viewModel.state.value
            assertThat(state.batches).hasSize(2)
            assertThat(state.batches[0].id).isEqualTo("batch1")
            assertThat(state.batches[0].numberOfPoints).isEqualTo(5)
            assertThat(state.batches[0].time).isEqualTo(Date(1000L))
            assertThat(state.batches[1].id).isEqualTo("batch2")
            assertThat(state.batches[1].numberOfPoints).isEqualTo(10)
            assertThat(state.batches[1].time).isEqualTo(Date(2000L))
        }

        @Test
        fun `when batches flow emits then updates state with batch list`() = runTest {
            // Given
            val initialBatches = listOf(TestDataFactory.createBatchInfo("initial", 3, 1000L))
            val updatedBatches = listOf(
                TestDataFactory.createBatchInfo("batch1", 7, 2000L),
                TestDataFactory.createBatchInfo("batch2", 12, 3000L),
                TestDataFactory.createBatchInfo("batch3", 1, 4000L)
            )

            val batchFlow = flowOf(initialBatches, updatedBatches)
            every { observeBatchesUseCase() } returns batchFlow

            // When
            viewModel = MainViewModel(
                requestPointsBatchUseCase,
                deletePointBatchUseCase,
                observeBatchesUseCase
            )

            // Then - should have the latest emission
            val state = viewModel.state.value
            assertThat(state.batches).hasSize(3)
            assertThat(state.batches[0].id).isEqualTo("batch1")
            assertThat(state.batches[0].numberOfPoints).isEqualTo(7)
            assertThat(state.batches[1].id).isEqualTo("batch2")
            assertThat(state.batches[1].numberOfPoints).isEqualTo(12)
            assertThat(state.batches[2].id).isEqualTo("batch3")
            assertThat(state.batches[2].numberOfPoints).isEqualTo(1)
        }
    }

    @Nested
    inner class PointNumberInput {

        @Test
        fun `when valid digits entered then updates required point number`() = runTest {
            // Given
            viewModel = MainViewModel(
                requestPointsBatchUseCase,
                deletePointBatchUseCase,
                observeBatchesUseCase
            )

            // When
            viewModel.onPointNumberChanged("123")

            // Then
            assertThat(viewModel.state.value.requiredPointNumber).isEqualTo("123")
        }

        @Test
        fun `when letters entered then ignores input`() = runTest {
            // Given
            viewModel = MainViewModel(
                requestPointsBatchUseCase,
                deletePointBatchUseCase,
                observeBatchesUseCase
            )
            val initialValue = viewModel.state.value.requiredPointNumber

            // When
            viewModel.onPointNumberChanged("abc")

            // Then
            assertThat(viewModel.state.value.requiredPointNumber).isEqualTo(initialValue)
        }

        @Test
        fun `when special characters entered then ignores input`() = runTest {
            // Given
            viewModel = MainViewModel(
                requestPointsBatchUseCase,
                deletePointBatchUseCase,
                observeBatchesUseCase
            )
            val initialValue = viewModel.state.value.requiredPointNumber

            // When
            viewModel.onPointNumberChanged("!@#$%")

            // Then
            assertThat(viewModel.state.value.requiredPointNumber).isEqualTo(initialValue)
        }

        @Test
        fun `when empty string entered then allows empty value`() = runTest {
            // Given
            viewModel = MainViewModel(
                requestPointsBatchUseCase,
                deletePointBatchUseCase,
                observeBatchesUseCase
            )

            // When
            viewModel.onPointNumberChanged("")

            // Then
            assertThat(viewModel.state.value.requiredPointNumber).isEqualTo("")
        }

        @Test
        fun `when mixed input entered then filters only digits`() = runTest {
            // Given
            viewModel = MainViewModel(
                requestPointsBatchUseCase,
                deletePointBatchUseCase,
                observeBatchesUseCase
            )
            val initialValue = viewModel.state.value.requiredPointNumber

            // When
            viewModel.onPointNumberChanged("123abc456!@#")

            // Then
            // Should ignore the mixed input and keep initial value
            assertThat(viewModel.state.value.requiredPointNumber).isEqualTo(initialValue)
        }

        @Test
        fun `when leading zeros entered then accepts input`() = runTest {
            // Given
            viewModel = MainViewModel(
                requestPointsBatchUseCase,
                deletePointBatchUseCase,
                observeBatchesUseCase
            )

            // When
            viewModel.onPointNumberChanged("00123")

            // Then
            assertThat(viewModel.state.value.requiredPointNumber).isEqualTo("00123")
        }
    }

    @Nested
    inner class RequestPoints {

        @Test
        fun `when valid number provided then requests points successfully`() = runTest {
            // Given
            val expectedBatchId = "generated-batch-id"
            coEvery { requestPointsBatchUseCase(10) } returns expectedBatchId

            viewModel = MainViewModel(
                requestPointsBatchUseCase,
                deletePointBatchUseCase,
                observeBatchesUseCase
            )

            // When
            viewModel.requestPoints()

            // Then
            val state = viewModel.state.value
            assertThat(state.isLoading).isFalse()
            assertThat(state.errorTypeViewData).isEqualTo(MainScreenErrorTypeViewData.None)

            coVerify { requestPointsBatchUseCase(10) }
        }

        @Test
        fun `when empty number provided then shows empty number error`() = runTest {
            turbineScope {
                // Given
                viewModel = MainViewModel(
                    requestPointsBatchUseCase,
                    deletePointBatchUseCase,
                    observeBatchesUseCase
                )
                viewModel.onPointNumberChanged("")

                // When
                viewModel.requestPoints()
                val state = viewModel.state.testIn(backgroundScope)
                val item = state.awaitItem()

                // Then
                assertThat(item.isLoading).isFalse()
                assertThat(item.errorTypeViewData).isEqualTo(MainScreenErrorTypeViewData.EmptyNumber)

                coVerify(exactly = 0) { requestPointsBatchUseCase(any()) }
            }
        }

        @Test
        fun `when zero provided then shows negative number error`() = runTest {
            // Given
            viewModel = MainViewModel(
                requestPointsBatchUseCase,
                deletePointBatchUseCase,
                observeBatchesUseCase
            )
            viewModel.onPointNumberChanged("0")

            // When
            viewModel.requestPoints()

            // Then
            val state = viewModel.state.value
            assertThat(state.isLoading).isFalse()
            assertThat(state.errorTypeViewData).isEqualTo(MainScreenErrorTypeViewData.NegativeNumber)

            coVerify(exactly = 0) { requestPointsBatchUseCase(any()) }
        }

        @Test
        fun `when network error occurs then shows request error`() = runTest {
            // Given
            val networkError = NetworkError(RuntimeException("Network unavailable"))
            coEvery { requestPointsBatchUseCase(10) } throws networkError

            viewModel = MainViewModel(
                requestPointsBatchUseCase,
                deletePointBatchUseCase,
                observeBatchesUseCase
            )

            // When
            viewModel.requestPoints()

            // Then
            val state = viewModel.state.value
            assertThat(state.isLoading).isFalse()
            assertThat(state.errorTypeViewData).isEqualTo(MainScreenErrorTypeViewData.RequestError)
        }

        @Test
        fun `when server returns incorrect points error then shows requested incorrectness error`() =
            runTest {
                // Given
                val serverError = NetworkError(IllegalArgumentException("Incorrect point number"))
                coEvery { requestPointsBatchUseCase(10) } throws serverError

                viewModel = MainViewModel(
                    requestPointsBatchUseCase,
                    deletePointBatchUseCase,
                    observeBatchesUseCase
                )

                // When
                viewModel.requestPoints()

                // Then
                val state = viewModel.state.value
                assertThat(state.isLoading).isFalse()
                assertThat(state.errorTypeViewData).isEqualTo(MainScreenErrorTypeViewData.RequestedIncorrectnessError)
            }

        @Test
        fun `when unknown error occurs then shows unspecified error`() = runTest {
            // Given
            val unknownError = RuntimeException("Something unexpected happened")
            coEvery { requestPointsBatchUseCase(10) } throws unknownError

            viewModel = MainViewModel(
                requestPointsBatchUseCase,
                deletePointBatchUseCase,
                observeBatchesUseCase
            )

            // When
            viewModel.requestPoints()

            // Then
            val state = viewModel.state.value
            assertThat(state.isLoading).isFalse()
            assertThat(state.errorTypeViewData).isEqualTo(MainScreenErrorTypeViewData.Unspecified)
        }

    }

    @Nested
    inner class Navigation {

        @Test
        fun `when batch clicked then navigates to result screen`() = runTest {
            turbineScope {
                // Given
                val batchId = "test-batch-id"
                viewModel = MainViewModel(
                    requestPointsBatchUseCase,
                    deletePointBatchUseCase,
                    observeBatchesUseCase
                )

                // When
                val navigation = viewModel.navigation.testIn(backgroundScope)
                viewModel.onBatchClicked(batchId)

                // Then
                assertThat(navigation.awaitItem().batchId).isEqualTo(batchId)
            }
        }

        @Test
        fun `when request succeeds then emits navigation event`() = runTest {
            turbineScope {
                // Given
                val expectedBatchId = "successful-batch-id"
                coEvery { requestPointsBatchUseCase(5) } returns expectedBatchId

                viewModel = MainViewModel(
                    requestPointsBatchUseCase,
                    deletePointBatchUseCase,
                    observeBatchesUseCase
                )
                viewModel.onPointNumberChanged("5")

                // When
                val navigation = viewModel.navigation.testIn(backgroundScope)
                viewModel.requestPoints()

                // Then
                assertThat(navigation.awaitItem().batchId).isEqualTo(expectedBatchId)
            }
        }
    }

    @Nested
    inner class BatchManagement {

        @Test
        fun `when batch deleted successfully then removes from list`() = runTest {
            // Given
            val batchId = "batch-to-delete"
            coEvery { deletePointBatchUseCase(batchId) } returns Unit

            viewModel = MainViewModel(
                requestPointsBatchUseCase,
                deletePointBatchUseCase,
                observeBatchesUseCase
            )

            // When
            viewModel.onBatchDeleted(batchId)

            // Then
            coVerify { deletePointBatchUseCase(batchId) }
        }
    }
}