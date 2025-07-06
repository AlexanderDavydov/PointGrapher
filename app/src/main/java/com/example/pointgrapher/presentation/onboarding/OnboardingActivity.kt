package com.example.pointgrapher.presentation.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.pointgrapher.databinding.ActivityOnboardingBinding
import com.example.pointgrapher.presentation.composeui.MainComposeActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OnboardingActivity : AppCompatActivity() {

    private val viewModel: OnboardingViewModel by viewModels()

    private lateinit var binding: ActivityOnboardingBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            viewModel.navigationFlow
                .collectLatest {
                    navigateToMainScreen(it)
                }
        }

        viewModel.checkNavigation()

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.cardCompose.setOnClickListener {
            viewModel.onComposeClick()
        }

        binding.cardXml.setOnClickListener {
            viewModel.onXMLClick()
        }
    }

    private fun navigateToMainScreen(navigation: OnboardingNavigation) {
        val intent = when (navigation) {
            OnboardingNavigation.ComposeScreen -> Intent(this, MainComposeActivity::class.java)
            OnboardingNavigation.XmlScreen -> TODO() // Intent(this, MainXMLActivity::class.java)
        }

        startActivity(intent)
        finish()
    }
}