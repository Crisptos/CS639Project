package com.calogoal.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.calogoal.interfaces.FirestoreService
import com.calogoal.models.accounts.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    var name: String = "",
    var age: Int = 0,
    var weight: Int = 0,
    var height: Int = 0
)

@HiltViewModel
class ProfilePageViewModel @Inject constructor(
    private val firestoreService: FirestoreService
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = firestoreService.getProfile()
        }
    }
}