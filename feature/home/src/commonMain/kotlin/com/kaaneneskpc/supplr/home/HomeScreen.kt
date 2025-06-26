package com.kaaneneskpc.supplr.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kaaneneskpc.supplr.home.component.BottomBar
import com.kaaneneskpc.supplr.shared.fonts.Surface

@Composable
fun HomeScreen() {
    Scaffold(
        containerColor = Surface
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(
                top = paddingValues.calculateTopPadding(),
                bottom = paddingValues.calculateBottomPadding()
            )
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier.padding(all = 12.dp)
            ) {
                BottomBar(
                    selected = false,
                    onSelect = {

                    }
                )
            }
        }
    }
}