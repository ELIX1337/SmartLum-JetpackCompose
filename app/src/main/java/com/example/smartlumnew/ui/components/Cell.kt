package com.example.smartlumnew.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Модель Ячейки (карточки), которая используется на экране устройства.
 * Имеет главный контент (например, слайдер или свитч),
 * и, при необходимости, дополнительный (например, текстовое пояснение).
 *
 * На основе этой модели создаются конкретные ячейки.
 */
@Composable
fun Cell(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    backgroundColor: Color = MaterialTheme.colors.surface,
    elevation: Dp = 1.dp,
    onClick: (() -> Unit)? = null,
    mainContent: @Composable (() -> Unit),
    additionalContent: @Composable (() -> Unit)? = null,
) {
    // Здесь мы стилизуем текст внутри Composable
    val styledContent: @Composable (() -> Unit)? = additionalContent?.let {
        @Composable {
            val style = MaterialTheme.typography.body2.copy()
            ProvideTextStyle(style, content = additionalContent)
        }
    }

    // На момент написания этого кода, в Compose есть 2 типа Card: кликабельная (с методом onClick) и некликабельная
    // Кликабельная по дефолту требует реализацию onClick и при нажатии на нее сработает ripple (эффект клика), даже если onClick пустой
    // чтобы не вводить пользователя в заблуждение о том, что это что-то кликабельное, я реализовал сразу оба варианта Card,
    // в зависимости от того, кликабельная карта или нет, отрендерится нужная
    if (onClick != null) {
        ClickableCell(
            modifier = modifier,
            shape = shape,
            backgroundColor = backgroundColor,
            elevation = elevation,
            onClick = onClick,
            mainContent = mainContent,
            additionalContent = styledContent
        )
    } else {
        Cell(
            modifier = modifier,
            shape = shape,
            backgroundColor = backgroundColor,
            elevation = elevation,
            mainContent = mainContent,
            additionalContent = styledContent
        )
    }
}

// Некликабельная Card
@Composable
fun Cell(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    backgroundColor: Color = MaterialTheme.colors.surface,
    elevation: Dp = 1.dp,
    mainContent: @Composable (() -> Unit),
    additionalContent: @Composable (() -> Unit)? = null,
) {
    Card(
        modifier = modifier.defaultMinSize(minHeight = 44.dp),
        shape = shape,
        backgroundColor = backgroundColor,
        elevation = elevation,
    ) {
        Column(
            modifier = Modifier.padding(16.dp, 12.dp)
        ) {
            Box {
                mainContent()
            }
            additionalContent?.invoke()
        }
    }
}

// Кликабельная Card
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ClickableCell(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    backgroundColor: Color = MaterialTheme.colors.surface,
    elevation: Dp = 1.dp,
    onClick: () -> Unit,
    mainContent: @Composable (() -> Unit),
    additionalContent: @Composable (() -> Unit)? = null,
) {
    Card(
        modifier = modifier.defaultMinSize(minHeight = 44.dp),
        shape = shape,
        backgroundColor = backgroundColor,
        elevation = elevation,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp, 12.dp)
        ) {
            Box {
                mainContent()
            }
            additionalContent?.invoke()
        }
    }
}