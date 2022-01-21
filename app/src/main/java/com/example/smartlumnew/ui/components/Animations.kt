package com.example.smartlumnew.ui.components

import androidx.compose.animation.*
import androidx.compose.runtime.Composable

/**
 * Этот Composable служит для быстрого анимирования смены двух элементов.
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnimatedContentSwitch(
    toggleContent: Boolean,
    enter: EnterTransition = fadeIn() + expandIn(),
    exit: ExitTransition = shrinkOut() + fadeOut(),
    contentFrom: @Composable () -> Unit,
    contentTo: @Composable () -> Unit,
) {
    AnimatedVisibility(
        visible = !toggleContent,
        enter = enter,
        exit = exit
    ) {
        contentFrom()
    }
    AnimatedVisibility(
        visible = toggleContent,
        enter = enter,
        exit = exit,
    ) {
        contentTo()
    }
}