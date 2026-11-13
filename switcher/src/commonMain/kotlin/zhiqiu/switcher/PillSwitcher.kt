package zhiqiu.switcher

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun PillSwitcher(
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
            .size(SwitcherDefaults.Width, SwitcherDefaults.Height)
            .switcherModifier(checked, onCheckedChange, enabled, interactionSource, elevation, state.color),
    ) {
        val corner = size.height / 2f
        val inset = 2.dp.toPx() * pressFraction

        drawRoundRect(
            color = state.color,
            topLeft = Offset(inset, inset),
            size = Size(size.width - inset * 2f, size.height - inset * 2f),
            cornerRadius = CornerRadius(corner),
        )

        val iconRadius = corner * 0.6f
        val collapsedWidth = iconRadius - iconRadius / 2.25f
        val shift = -(size.width - corner * 2f) * state.slide

        withTransform({ translate(left = shift) }) {
            drawSwitcherIcon(
                center = Offset(size.width - corner, size.height / 2f),
                iconRadius = iconRadius,
                collapsedWidth = collapsedWidth,
                cornerRadius = corner,
                progress = state.progress,
                color = state.color,
                iconColor = colors.iconColor,
            )
        }
    }
}
