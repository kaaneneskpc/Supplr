package com.kaaneneskpc.supplr

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.kaaneneskpc.supplr.data.CustomerRepository
import com.kaaneneskpc.supplr.navigation.NavGraph
import com.kaaneneskpc.supplr.shared.navigation.Screen
import com.kaaneneskpc.supplr.shared.Consts.WEB_CLIENT_ID
import com.mmk.kmpauth.google.GoogleAuthCredentials
import com.mmk.kmpauth.google.GoogleAuthProvider
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

@Composable
@Preview
fun App() {
    MaterialTheme {
        val customerRepository = koinInject<CustomerRepository>()
        var appReady by remember { mutableStateOf(false) }
        val isUserAuthenticated = remember { customerRepository.getCurrentUserId() != null }
        val startDestination = remember {
            if (isUserAuthenticated) Screen.Home else Screen.Auth
        }
        LaunchedEffect(Unit) {
            GoogleAuthProvider.create(credentials = GoogleAuthCredentials(serverId = WEB_CLIENT_ID))
            appReady = true
        }
        AnimatedVisibility(modifier = Modifier.fillMaxSize(), visible = appReady) {
            NavGraph(
                startDestination = startDestination
            )
        }
    }
}