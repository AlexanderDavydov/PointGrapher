package com.example.pointgrapher.data.datasource

import com.example.pointgrapher.TestDataFactory
import com.example.pointgrapher.data.db.dao.BatchDao
import com.example.pointgrapher.data.db.dao.PointDao
import com.example.pointgrapher.data.db.entity.BatchEntity
import com.example.pointgrapher.data.db.entity.PointEntity
import com.example.pointgrapher.domain.exception.BatchNotFoundException
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PointLocalDataSourceTest {

    private val batchDao = mockk<BatchDao>()
    private val pointDao = mockk<PointDao>()
    private lateinit var dataSource: PointLocalDataSource

    @BeforeEach
    fun setUp() {
        dataSource = PointLocalDataSource(batchDao, pointDao)
    }

    @Test
    fun `when batch saved then can retrieve by id`() = runTest {
        // Given
        val batchId = "test-batch-id"
        val points = TestDataFactory.createPointDtoList(3)

        val batchSlot = slot<BatchEntity>()
        val pointsSlot = slot<List<PointEntity>>()

        coEvery { batchDao.insertBatch(capture(batchSlot)) } returns Unit
        coEvery { pointDao.insertPoints(capture(pointsSlot)) } returns Unit

        // When
        dataSource.saveBatch(batchId, points)

        // Then
        coVerify { batchDao.insertBatch(any()) }
        coVerify { pointDao.insertPoints(any()) }

        // Verify batch entity
        assertThat(batchSlot.captured.id).isEqualTo(batchId)
        assertThat(batchSlot.captured.actualCount).isEqualTo(3)
        assertThat(batchSlot.captured.timestamp).isGreaterThan(0L)

        // Verify point entities
        assertThat(pointsSlot.captured).hasSize(3)
        pointsSlot.captured.forEachIndexed { index, pointEntity ->
            assertThat(pointEntity.batchId).isEqualTo(batchId)
            assertThat(pointEntity.x).isEqualTo(points[index].x)
            assertThat(pointEntity.y).isEqualTo(points[index].y)
        }
    }

    @Test
    fun `when batch exists then returns correct points`() = runTest {
        // Given
        val batchId = "existing-batch"
        val batchEntity = BatchEntity(batchId, 2, System.currentTimeMillis())
        val pointEntities = listOf(
            PointEntity(1, batchId, 1.0, 2.0),
            PointEntity(2, batchId, 3.0, 4.0)
        )

        coEvery { batchDao.getBatchById(batchId) } returns batchEntity
        coEvery { pointDao.getPointsByBatchId(batchId) } returns pointEntities

        // When
        val result = dataSource.getBatch(batchId)

        // Then
        assertThat(result).hasSize(2)
        assertThat(result[0].x).isEqualTo(1.0)
        assertThat(result[0].y).isEqualTo(2.0)
        assertThat(result[1].x).isEqualTo(3.0)
        assertThat(result[1].y).isEqualTo(4.0)

        coVerify { batchDao.getBatchById(batchId) }
        coVerify { pointDao.getPointsByBatchId(batchId) }
    }

    @Test
    fun `when batch not exists then throws batch not found exception`() = runTest {
        // Given
        val nonExistentBatchId = TestDataFactory.Error.INVALID_BATCH_ID
        coEvery { batchDao.getBatchById(nonExistentBatchId) } returns null

        // When & Then
        try {
            dataSource.getBatch(nonExistentBatchId)
        } catch (e: BatchNotFoundException) {
            assertThat(e.message).contains(nonExistentBatchId)
        }

        coVerify { batchDao.getBatchById(nonExistentBatchId) }
        coVerify(exactly = 0) { pointDao.getPointsByBatchId(any()) }
    }

    @Test
    fun `when observing batches then emits all batches`() = runTest {
        // Given
        val batchEntities = listOf(
            BatchEntity("batch1", 5, 1000L),
            BatchEntity("batch2", 10, 2000L)
        )
        coEvery { batchDao.observeAllBatches() } returns flowOf(batchEntities)

        // When
        val result = dataSource.observeAllBatches()

        // Then
        result.collect { batches ->
            assertThat(batches).hasSize(2)
            assertThat(batches[0].id).isEqualTo("batch1")
            assertThat(batches[0].actualCount).isEqualTo(5)
            assertThat(batches[1].id).isEqualTo("batch2")
            assertThat(batches[1].actualCount).isEqualTo(10)
        }

        coVerify { batchDao.observeAllBatches() }
    }

    @Test
    fun `when batch deleted then removes from both tables`() = runTest {
        // Given
        val batchId = "batch-to-delete"
        val batchEntity = BatchEntity(batchId, 3, System.currentTimeMillis())

        coEvery { batchDao.getBatchById(batchId) } returns batchEntity
        coEvery { pointDao.deletePointsByBatchId(batchId) } returns Unit
        coEvery { batchDao.deleteBatchById(batchId) } returns Unit

        // When
        dataSource.deleteBatch(batchId)

        // Then
        coVerify { batchDao.getBatchById(batchId) }
        coVerify { pointDao.deletePointsByBatchId(batchId) }
        coVerify { batchDao.deleteBatchById(batchId) }
    }

    @Test
    fun `when deleting non existent batch then throws batch not found exception`() = runTest {
        // Given
        val nonExistentBatchId = "non-existent"
        coEvery { batchDao.getBatchById(nonExistentBatchId) } returns null

        // When & Then
        try {
            dataSource.deleteBatch(nonExistentBatchId)
        } catch (e: BatchNotFoundException) {
            assertThat(e.message).contains(nonExistentBatchId)
        }

        coVerify { batchDao.getBatchById(nonExistentBatchId) }
        coVerify(exactly = 0) { pointDao.deletePointsByBatchId(any()) }
        coVerify(exactly = 0) { batchDao.deleteBatchById(any()) }
    }
}