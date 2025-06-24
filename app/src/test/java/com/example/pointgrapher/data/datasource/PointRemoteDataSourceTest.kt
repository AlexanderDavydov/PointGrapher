package com.example.pointgrapher.data.datasource

import com.example.pointgrapher.TestDataFactory
import com.example.pointgrapher.data.api.PointApi
import com.example.pointgrapher.data.model.PointsResponse
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Test
import retrofit2.Response

class PointRemoteDataSourceTest {

    private val pointApi = mockk<PointApi>()
    private val dataSource by lazy { PointRemoteDataSource(pointApi) }

    @Test
    fun `when valid count provided then returns list of points`() = runTest {
        // Given
        val count = 5
        val expectedResponse = TestDataFactory.createPointsResponse(count)
        val response = Response.success(expectedResponse)
        coEvery { pointApi.getPoints(count) } returns response

        // When
        val result = dataSource.getPoints(count)

        // Then
        assertThat(result).hasSize(count)
        assertThat(result).containsExactlyElementsIn(expectedResponse.points)
        coVerify { pointApi.getPoints(count) }
    }

    @Test
    fun `when server returns 400 then throws illegal argument exception`() = runTest {
        // Given
        val count = -1
        val response = Response.error<PointsResponse>(
            400,
            "Bad Request".toResponseBody()
        )
        coEvery { pointApi.getPoints(count) } returns response

        // When & Then
        try {
            dataSource.getPoints(count)
        } catch (e: IllegalArgumentException) {
            assertThat(e.message).isEqualTo("Incorrect point number")
        }

        coVerify { pointApi.getPoints(count) }
    }

    @Test
    fun `when server returns 500 then throws generic exception`() = runTest {
        // Given
        val count = 10
        val response = Response.error<PointsResponse>(
            500,
            "Internal Server Error".toResponseBody()
        )
        coEvery { pointApi.getPoints(count) } returns response

        // When & Then
        try {
            dataSource.getPoints(count)
        } catch (e: Exception) {
            assertThat(e.message).isEqualTo("Failed to fetch points. Error code: 500")
            assertThat(e).isNotInstanceOf(IllegalArgumentException::class.java)
        }

        coVerify { pointApi.getPoints(count) }
    }

    @Test
    fun `when response body is null then returns empty list`() = runTest {
        // Given
        val count = 5
        val response = Response.success<PointsResponse>(null)
        coEvery { pointApi.getPoints(count) } returns response

        // When
        val result = dataSource.getPoints(count)

        // Then
        assertThat(result).isEmpty()
        coVerify { pointApi.getPoints(count) }
    }

    @Test
    fun `when network error occurs then throws exception`() = runTest {
        // Given
        val count = 5
        val networkException = RuntimeException("Network unavailable")
        coEvery { pointApi.getPoints(count) } throws networkException

        // When & Then
        try {
            dataSource.getPoints(count)
        } catch (e: RuntimeException) {
            assertThat(e.message).isEqualTo("Network unavailable")
        }

        coVerify { pointApi.getPoints(count) }
    }
}