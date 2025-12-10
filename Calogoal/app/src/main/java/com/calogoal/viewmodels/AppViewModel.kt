package com.calogoal.viewmodels

import com.calogoal.interfaces.AccountService
import javax.inject.Inject

class AppViewModel @Inject constructor(
    accountService: AccountService
){
    val user = accountService.currentUser
}