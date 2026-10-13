package com.example.ui.screens.splash

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.di.RepositoryModule
import com.example.domain.model.UserRole
import com.example.domain.repository.UserRepository
import com.example.ui.navigation.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SplashViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _navigationEvent = MutableSharedFlow<String>(replay = 1)
    val navigationEvent: SharedFlow<String> = _navigationEvent.asSharedFlow()

    init {
        checkUserSession()
    }

    private fun checkUserSession() {
        viewModelScope.launch {
            // Memberikan waktu jeda branding splash screen (2 detik)
            delay(2000)

            // Cek apakah ada data siswa/pengguna aktif
            val users = runCatching { userRepository.getAllUsers().first() }.getOrDefault(emptyList())
            val activeUser = users.firstOrNull()
            if (activeUser != null) {
                if (activeUser.role == UserRole.TEACHER) {
                    _navigationEvent.emit(Screen.TeacherDashboard.route)
                } else {
                    _navigationEvent.emit(Screen.Home.route)
                }
            } else {
                // Pengguna baru, arahkan ke layar Identitas & Pemilihan Peran
                _navigationEvent.emit(Screen.Identity.route)
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                SplashViewModel(
                    userRepository = RepositoryModule.provideUserRepository(app)
                )
            }
        }
    }
}
