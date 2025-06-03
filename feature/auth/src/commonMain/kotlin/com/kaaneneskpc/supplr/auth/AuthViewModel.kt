package com.kaaneneskpc.supplr.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaaneneskpc.supplr.customer.CustomerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch

class AuthViewModel(private val customerRepository: CustomerRepository) : ViewModel() {

    fun createCustomer(
        user: dev.gitlive.firebase.auth.FirebaseUser?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            customerRepository.createCustomer(user, onSuccess, onError)
        }
    }
}