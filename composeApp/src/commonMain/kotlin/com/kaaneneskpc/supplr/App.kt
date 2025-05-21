package com.kaaneneskpc.supplr

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kaaneneskpc.supplr.navigation.NavGraph
import com.kaaneneskpc.supplr.shared.fonts.Constants.WEB_CLIENT_ID
import com.mmk.kmpauth.google.GoogleAuthCredentials
import com.mmk.kmpauth.google.GoogleAuthProvider
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import supplr.composeapp.generated.resources.Res
import supplr.composeapp.generated.resources.compose_multiplatform

@Composable
@Preview
fun App() {
    MaterialTheme {
        var appReady by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            GoogleAuthProvider.create(credentials = GoogleAuthCredentials(serverId = WEB_CLIENT_ID))
            appReady = true
        }
        AnimatedVisibility(modifier = Modifier.fillMaxSize(), visible = appReady) {
            NavGraph()
        }
    }
}