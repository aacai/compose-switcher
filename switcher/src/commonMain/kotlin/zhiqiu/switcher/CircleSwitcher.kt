package zhiqiu.switcher

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CircleSwitcher(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: SwitcherColors = SwitcherDefaults.colors(),
    elevation: Dp = SwitcherDefaults.Elevation,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val state = rememberSwitcherState(checked, colors)
    val pressFraction by rememberPressFraction(interactionSource)

    Canvas(
        modifier = modifier
            .size(SwitcherDefaults.CircleSize)
            .switcherModifier(checked, onCheckedChange, enabled, interactionSource, elevation, state.color),
    ) {
        val radius = minOf(size.width, size.height) / 2f
        val center = Offset(size.width / 2f, size.height / 2f)
        val inset = 2.dp.toPx() * pressFraction

        drawCircle(color = state.color, radius = radius - inset, center = center)

        val iconRadius = radius * 0.5f
        val collapsedWidth = (iconRadius - iconRadius / 2.25f) * 1.1f

        drawSwitcherIcon(
            center = center,
            iconRadius = iconRadius,
            collapsedWidth = collapsedWidth,
            cornerRadius = radius,
            progress = state.progress,
            color = state.color,
            iconColor = colors.iconColor,
        )
    }
}
