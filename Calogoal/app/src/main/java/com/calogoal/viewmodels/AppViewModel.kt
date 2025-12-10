package com.calogoal.viewmodels

import com.calogoal.interfaces.AccountService
import com.calogoal.models.accounts.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class AppViewModel @Inject constructor(
    accountService: AccountService
){
    private val _currentUser = MutableStateFlow(User())
    val currentUser = _currentUser.asStateFlow()
}