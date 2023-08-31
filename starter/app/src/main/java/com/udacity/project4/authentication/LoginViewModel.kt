package com.udacity.project4.authentication

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.udacity.project4.locationreminders.RemindersActivity

class LoginViewModel : ViewModel() {

    enum class AuthenticationState {
        AUTHENTICATED, UNAUTHENTICATED, INVALID_AUTHENTICATION
    }

    val authenticationState = FirebaseUserLiveData().map {  user ->
        if (user != null){
            AuthenticationState.AUTHENTICATED
        }else{
            AuthenticationState.UNAUTHENTICATED
        }
    }


}