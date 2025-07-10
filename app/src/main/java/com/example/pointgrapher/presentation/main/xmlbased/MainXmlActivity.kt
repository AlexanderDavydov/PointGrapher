package com.example.pointgrapher.presentation.main.xmlbased

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.example.pointgrapher.databinding.ActivityMainXmlBinding
import com.example.pointgrapher.presentation.main.MainScreenNavigation
import com.example.pointgrapher.presentation.main.MainScreenState
import com.example.pointgrapher.presentation.main.MainViewModel
import com.example.pointgrapher.presentation.main.viewdata.BatchInfoViewData
import com.example.pointgrapher.presentation.main.viewdata.MainScreenErrorTypeViewData
import com.example.pointgrapher.presentation.main.viewdata.ValidationErrorTypeViewData
import com.example.pointgrapher.presentation.onboarding.OnboardingActivity
import com.example.pointgrapher.presentation.result.xmlbased.ResultActivityXml
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivityXml : AppCompatActivity() {

    private lateinit var binding: ActivityMainXmlBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var batchAdapter: BatchListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainXmlBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        observeViewModel()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_switch_ui -> {
                viewModel.onOpenOnboarding()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupViews() {
        setupAppBar()
        setupTextInput()
        setupButton()
        setupRecyclerView()
    }

    private fun setupAppBar() {
        setSupportActionBar(binding.toolbar)
    }

    private fun setupTextInput() {
        binding.pointCountTextField.setText(viewModel.state.value.requiredPointNumber)

        binding.pointCountTextField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                viewModel.onPointNumberChanged(s.toString())
            }
        })
    }

    private fun setupButton() {
        binding.requestButton.setOnClickListener {
            viewModel.requestPoints()
        }
    }

    private fun setupRecyclerView() {
        batchAdapter = BatchListAdapter(
            onBatchClick = { batchId ->
                viewModel.onBatchClicked(batchId)
            },
            onBatchDelete = { batchId ->
                viewModel.onBatchDeleted(batchId)
            }
        )

        binding.rvBatches.apply {
            layoutManager = LinearLayoutManager(this@MainActivityXml)
            adapter = batchAdapter

            batchAdapter.attachSwipeToDelete(this)
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
                    viewModel.navigation.collect { navigation ->
                        when (navigation) {
                            is MainScreenNavigation.GoToResult -> navigateToResult(navigation.batchId)
                            is MainScreenNavigation.GoToOnboarding -> navigateToOnboarding()
                        }
                    }
                }
            }
        }
    }

    private fun updateUI(state: MainScreenState) {
        if (binding.pointCountTextField.text.toString() != state.requiredPointNumber) {
            binding.pointCountTextField.setText(state.requiredPointNumber)
            binding.pointCountTextField.setSelection(state.requiredPointNumber.length)
        }

        binding.requestButton.isEnabled = !state.isLoading
        binding.requestButton.text = if (state.isLoading) {
            "Loading..."
        } else if (state.isError) {
            getString(R.string.main_screen_button_go_again)
        } else {
            getString(R.string.main_screen_button_go)
        }

        updateValidationError(state.errorTypeViewData)
        updateBatchesList(state.batches)
    }

    private fun updateValidationError(errorType: MainScreenErrorTypeViewData) {
        binding.pointCountTextLayout.error = getErrorString(errorType)
    }

    private fun getErrorString(errorTypeViewData: MainScreenErrorTypeViewData): String? {
        val id = when (errorTypeViewData) {
            MainScreenErrorTypeViewData.None -> return null
            is MainScreenErrorTypeViewData.ValidationError -> getValidationErrorString(
                errorTypeViewData.type
            )

            MainScreenErrorTypeViewData.RequestError -> R.string.main_screen_error_something_happened_during_request
            MainScreenErrorTypeViewData.RequestedIncorrectnessError -> R.string.main_screen_error_server_incorrect_points
            MainScreenErrorTypeViewData.Unspecified -> R.string.main_screen_error_general
        }
        return getString(id)
    }

    private fun getValidationErrorString(type: ValidationErrorTypeViewData): Int {
        return when (type) {
            ValidationErrorTypeViewData.Empty -> R.string.main_screen_error_empty_points_number
            ValidationErrorTypeViewData.InvalidFormat -> R.string.main_screen_error_invalid_format
            ValidationErrorTypeViewData.MustBeNumber -> R.string.main_screen_error_must_be_number
            ValidationErrorTypeViewData.TooHighValue -> R.string.main_screen_error_too_high_value
            ValidationErrorTypeViewData.TooLowValue -> R.string.main_screen_error_too_low_value
        }
    }

    private fun updateBatchesList(batches: List<BatchInfoViewData>) {
        batchAdapter.submitList(batches)
        binding.batchesTitleText.visibility = if (batches.isNotEmpty()) {
            android.view.View.VISIBLE
        } else {
            android.view.View.GONE
        }
    }

    private fun navigateToResult(batchId: String) {
        val intent = Intent(this, ResultActivityXml::class.java).apply {
            putExtra(ResultActivityXml.EXTRA_BATCH_ID, batchId)
        }
        startActivity(intent)
    }

    private fun navigateToOnboarding() {
        val intent = Intent(this, OnboardingActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}