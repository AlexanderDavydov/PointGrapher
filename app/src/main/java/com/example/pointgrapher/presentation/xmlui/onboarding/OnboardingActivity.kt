package com.example.pointgrapher.presentation.xmlui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pointgrapher.databinding.ActivityOnboardingBinding
import com.example.pointgrapher.presentation.composeui.MainComposeActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.cardCompose.setOnClickListener {
            selectFrameworkAndNavigate(UIFramework.COMPOSE)
        }

        binding.cardXml.setOnClickListener {
            selectFrameworkAndNavigate(UIFramework.XML)
        }
    }

    private fun selectFrameworkAndNavigate(framework: UIFramework) {
        navigateToMainScreen(framework)
    }

    private fun navigateToMainScreen(framework: UIFramework) {
        val intent = when (framework) {
            UIFramework.COMPOSE -> Intent(this, MainComposeActivity::class.java)
            UIFramework.XML -> {
                // Intent(this, MainXMLActivity::class.java)
                TODO("show xml flow")
            }
        }

        startActivity(intent)
        finish()
    }
}