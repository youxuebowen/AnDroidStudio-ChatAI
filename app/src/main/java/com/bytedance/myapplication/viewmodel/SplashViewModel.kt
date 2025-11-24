package com.bytedance.myapplication.viewmodel
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.bytedance.myapplication.MVI.SplashIntent
import com.bytedance.myapplication.MVI.SplashState
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
class SplashViewModel : ViewModel() {
    private val _state = MutableStateFlow(SplashState())
    val state: StateFlow<SplashState> = _state.asStateFlow()


    // Start a timer (2s) then set loading=false
    fun dispatch(intent: SplashIntent) {
        when (intent) {
            SplashIntent.StartTimer -> startTimer()
        }
    }


    private fun startTimer() {
        viewModelScope.launch {
            delay(2000L) // 2 seconds
            _state.value = SplashState(loading = false)
        }
    }
}