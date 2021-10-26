package com.example.smartlumnew.ui.components

import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.smartlumnew.R
import com.example.smartlumnew.navigation.HomeGraphDestinations
import com.example.smartlumnew.ui.theme.themeTransparent
import com.example.smartlumnew.ui.theme.withAlpha
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun TransparentTopBar(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    backgroundColor: Color = themeTransparent(),
    contentColor: Color = contentColorFor(backgroundColor),
    elevation: Dp = 0.dp
) {
    Column() {
        TopAppBar(
            title = title,
            modifier = modifier
                .blur(radius = 12.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
                .statusBarsPadding()
                .navigationBarsPadding(bottom = false),
            navigationIcon = navigationIcon,
            actions = actions,
            backgroundColor = backgroundColor,
            contentColor = contentColor,
            elevation = elevation
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DrawerContent(
    modifier: Modifier = Modifier,
    scope: CoroutineScope = rememberCoroutineScope(),
    scaffoldState: ScaffoldState,
    navController: NavController
) {
    val items = HomeGraphDestinations.values()
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route
    val er = painterResource(id = R.drawable.smartlum_logo)
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.smartlum_logo),
            contentDescription = "SmartLum logo",
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 8.dp, 16.dp, 0.dp),
            contentScale = ContentScale.Fit
        )
        Divider()
        items.forEach { item ->
            DrawerItem(
                icon = { Icon(
                    painter = painterResource(id = item.icon),
                    tint = it,
                    contentDescription = stringResource(item.title)) },
                label = { Text(stringResource(item.title)) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        navController.graph.startDestinationRoute?.let {
                            popUpTo(HomeGraphDestinations.SCANNER.route) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                    scope.launch {
                        scaffoldState.drawerState.animateTo(DrawerValue.Closed, tween(200))
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DrawerItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable (selectionTint: Color) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: @Composable (() -> Unit)? = null,
    alwaysShowLabel: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    selectedContentColor: Color = LocalContentColor.current,
    unselectedContentColor: Color = selectedContentColor.copy(alpha = ContentAlpha.medium)
) {
    val styledLabel: @Composable (() -> Unit)? = label?.let {
        @Composable {
            val style = MaterialTheme.typography.body2.copy(
                textAlign = TextAlign.Center,
                color = if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface
            )
            ProvideTextStyle(style, content = label)
        }
    }

    val ripple = rememberRipple(bounded = false, color = selectedContentColor)

    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(48.dp),
        elevation = 0.dp,
        shape = MaterialTheme.shapes.small,
        onClick = onClick,
        backgroundColor = if (selected) MaterialTheme.colors.primary.withAlpha(0.15f) else Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .selectable(
                    selected = selected,
                    onClick = onClick,
                    enabled = enabled,
                    role = Role.Tab,
                    interactionSource = interactionSource,
                    indication = ripple
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .height(35.dp)
                    .width(35.dp),
                contentAlignment = Alignment.Center
            ) {
                icon(if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface)
            }
            Spacer(modifier = Modifier.width(8.dp))
            if (styledLabel != null) {
                styledLabel()
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = HomeGraphDestinations.values()
    BottomNavigation {
        val navBackStackEntry = navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry.value?.destination?.route
        items.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(painter = painterResource(id = item.icon), contentDescription = stringResource(item.title)) },
                label = { Text(stringResource(item.title)) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        navController.graph.startDestinationRoute?.let {
                            popUpTo(it) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}