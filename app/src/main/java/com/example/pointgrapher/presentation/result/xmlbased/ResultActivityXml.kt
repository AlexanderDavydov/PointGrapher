package com.example.pointgrapher.presentation.result.xmlbased

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pointgrapher.R
import com.example.pointgrapher.databinding.ActivityResultXmlBinding
import com.example.pointgrapher.presentation.result.ResultState
import com.example.pointgrapher.presentation.result.ResultUINotification
import com.example.pointgrapher.presentation.result.ResultViewModel
import com.example.pointgrapher.presentation.result.model.PointViewData
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ResultActivityXml : AppCompatActivity() {

    private lateinit var binding: ActivityResultXmlBinding
    private val viewModel: ResultViewModel by viewModels()

    @Inject
    lateinit var graphManager: GraphManager

    private lateinit var pointsAdapter: PointsTableAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityResultXmlBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupViews()
        observeViewModel()

        val batchId = intent.getStringExtra(EXTRA_BATCH_ID)
        if (batchId != null) {
            viewModel.onBatchIdChanged(batchId)
        } else {
            showError("Invalid batch ID")
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.result_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.action_save_chart) {
            saveChart()
            true
        } else super.onOptionsItemSelected(item)
    }

    private fun setupViews() {
        setupChart()
        setupTable()
    }

    private fun setupChart() {
        graphManager.setupChart(binding.lineChart)
    }

    private fun setupTable() {
        pointsAdapter = PointsTableAdapter()
        binding.rvPointsTable.apply {
            layoutManager = LinearLayoutManager(this@ResultActivityXml)
            adapter = pointsAdapter
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.state.collect { state ->
                        updateUI(state)
                    }
                }

                launch {
                    viewModel.notification.collect { notification ->
                        val message = when (notification) {
                            is ResultUINotification.ChartSaved -> "Chart Saved to ${notification.path}"
                            is ResultUINotification.Error -> notification.message
                        }

                        Snackbar
                            .make(binding.root, message, Snackbar.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }
    }

    private fun updateUI(state: ResultState) {
        when (state) {
            is ResultState.Loading -> showLoading()
            is ResultState.Success -> showData(state.points)
            is ResultState.Error -> showError("Failed to load points")
        }
    }

    private fun showLoading() {
        binding.loadingContainer.visibility = android.view.View.VISIBLE
        binding.contentContainer.visibility = android.view.View.GONE
        binding.errorContainer.visibility = android.view.View.GONE
    }

    private fun hideLoading() {
        binding.loadingContainer.visibility = android.view.View.GONE
    }

    private fun showData(points: PointViewData) {
        hideLoading()
        binding.contentContainer.visibility = android.view.View.VISIBLE

        graphManager.updateData(binding.lineChart, points)

        pointsAdapter.updatePoints(points)
    }

    private fun showError(message: String) {
        hideLoading()
        binding.contentContainer.visibility = android.view.View.GONE
        binding.errorContainer.visibility = android.view.View.VISIBLE

        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    private fun saveChart() {
        val bitmap = graphManager.getChartBitmap(binding.lineChart)
        if (bitmap != null) {
            viewModel.saveChartImage(bitmap = bitmap, chartType = "xml_chart")
        } else {
            showError("Failed to capture chart")
        }
    }

    companion object {
        const val EXTRA_BATCH_ID = "batch_id"
    }
}