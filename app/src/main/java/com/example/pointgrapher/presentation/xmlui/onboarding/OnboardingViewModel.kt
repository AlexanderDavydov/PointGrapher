package com.example.pointgrapher.presentation.xmlui.onboarding

import androidx.lifecycle.ViewModel
import com.example.pointgrapher.domain.model.UIFramework
import com.example.pointgrapher.domain.usecase.GetUIFrameworkUseCase
import com.example.pointgrapher.domain.usecase.SetUIFrameworkUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val getUIFrameworksUseCase: GetUIFrameworkUseCase,
    private val setUIFrameworkUseCase: SetUIFrameworkUseCase,
) : ViewModel() {

    private val _navigationFlow = MutableSharedFlow<OnboardingNavigation>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val navigationFlow = _navigationFlow.asSharedFlow()

    fun checkNavigation() {
        navigate(getUIFrameworksUseCase())
    }

    fun onComposeClick() {
        UIFramework.COMPOSE.apply {
            setUIFrameworkUseCase(this)
            navigate(this)
        }
    }

    fun onXMLClick() {
        UIFramework.XML.apply {
            setUIFrameworkUseCase(this)
            navigate(this)
        }
    }

    private fun navigate(framework: UIFramework) {
        when (framework) {
            UIFramework.COMPOSE -> _navigationFlow.tryEmit(OnboardingNavigation.ComposeScreen)
            UIFramework.XML -> _navigationFlow.tryEmit(OnboardingNavigation.XmlScreen)
            UIFramework.NONE -> {
                // user is npt selecting a framework, do nothing
            }
        }
    }
}