package com.kaaneneskpc.supplr

import androidx.compose.ui.window.ComposeUIViewController
import com.kaaneneskpc.supplr.di.initializeKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        initializeKoin()
    }
) { App() }