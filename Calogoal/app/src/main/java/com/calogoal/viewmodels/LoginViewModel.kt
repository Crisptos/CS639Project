package com.calogoal.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.calogoal.models.accounts.LoginStatus
import com.calogoal.models.accounts.User
import com.calogoal.services.AccountServiceImpl
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoginViewModel : ViewModel() {
    private val _currentUser = MutableStateFlow(User())
    val currentUser = _currentUser.asStateFlow()

    private val _loginStatus = MutableStateFlow(LoginStatus())
    val loginStatus = _loginStatus.asStateFlow()

    private val auth = FirebaseAuth.getInstance()
    private val accountService = AccountServiceImpl(auth)

    fun updateEmail(newEmail: String) {
        _currentUser.value = _currentUser.value.copy(email = newEmail)
    }

    fun updatePassword(newPassword: String) {
        _currentUser.value = _currentUser.value.copy(password = newPassword)
    }

    suspend fun login(email: String, password: String)
    {
        _loginStatus.value = LoginStatus(loading = true)
        try{
            val user = _currentUser.value
            accountService.authenticate(email, password)

            _loginStatus.value = LoginStatus(success = true)
        } catch (e: Exception)
        {
            _loginStatus.value = LoginStatus(error = e.message)
        }
    }
}