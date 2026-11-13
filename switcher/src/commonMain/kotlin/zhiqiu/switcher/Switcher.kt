package zhiqiu.switcher

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.exp

data class SwitcherColors(
    val onColor: Color,
    val offColor: Color,
    val iconColor: Color,
)

object SwitcherDefaults {
    val OnColor: Color = Color(0xFF48EA8B)
    val OffColor: Color = Color(0xFFFF4651)
    val IconColor: Color = Color.White
    val Elevation: Dp = 4.dp
    val Width: Dp = 46.dp
    val Height: Dp = 26.dp
    val CircleSize: Dp = 26.dp

    fun colors(
        onColor: Color = OnColor,
        offColor: Color = OffColor,
        iconColor: Color = IconColor,
    ): SwitcherColors = SwitcherColors(onColor, offColor, iconColor)
}

internal data class SwitcherState(
    val color: Color,
    val progress: Float,
    val slide: Float,
)

@Composable
internal fun rememberSwitcherState(checked: Boolean, colors: SwitcherColors): SwitcherState {
    val progress by animateFloatAsState(
        targetValue = if (checked) 0f else 1f,
        animationSpec = tween(800, easing = if (checked) BounceOut else BounceIn),
        label = "SwitcherIcon",
    )
    val colorFraction by animateFloatAsState(
        targetValue = if (checked) 0f else 1f,
        animationSpec = tween(300, easing = LinearEasing),
        label = "SwitcherColor",
    )
    val color = lerp(colors.onColor, colors.offColor, colorFraction)
    val slide by animateFloatAsState(
        targetValue = if (checked) 0f else 1f,
        animationSpec = tween(200, easing = FastOutSlowInEasing),
        label = "SwitcherSlide",
    )
    return SwitcherState(color, progress, slide)
}

private val BounceIn = BounceEasing(amplitude = 0.2, frequency = 14.5)
private val BounceOut = BounceEasing(amplitude = 0.15, frequency = 12.0)

private class BounceEasing(
    private val amplitude: Double,
    private val frequency: Double,
) : Easing {
    override fun transform(fraction: Float): Float =
        (1.0 - exp(-fraction / amplitude) * cos(frequency * fraction)).toFloat()
}

@Composable
internal fun rememberPressFraction(interactionSource: InteractionSource): State<Float> {
    val pressed by interactionSource.collectIsPressedAsState()
    return animateFloatAsState(
        targetValue = if (pressed) 1f else 0f,
        animationSpec = tween(200),
        label = "SwitcherPress",
    )
}

internal fun Modifier.switcherModifier(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean,
    interactionSource: MutableInteractionSource,
    elevation: Dp,
    color: Color,
): Modifier = this
    .shadow(
        elevation = elevation,
        shape = RoundedCornerShape(50),
        ambientColor = color.copy(alpha = 0.22f),
        spotColor = color.copy(alpha = 0.40f),
    )
    .alpha(if (enabled) 1f else 0.38f)
    .toggleable(
        value = checked,
        onValueChange = onCheckedChange,
        enabled = enabled,
        role = Role.Switch,
        interactionSource = interactionSource,
        indication = null,
    )

internal fun DrawScope.drawSwitcherIcon(
    center: Offset,
    iconRadius: Float,
    collapsedWidth: Float,
    cornerRadius: Float,
    progress: Float,
    color: Color,
    iconColor: Color,
) {
    val expand = (iconRadius - collapsedWidth / 2f) * progress
    val left = center.x - collapsedWidth / 2f - expand
    val top = center.y - iconRadius
    val width = collapsedWidth + expand * 2f

    drawRoundRect(
        color = iconColor,
        topLeft = Offset(left, top),
        size = Size(width, iconRadius * 2f),
        cornerRadius = CornerRadius(cornerRadius),
    )

    val clipHalf = (iconRadius / 2.25f) * progress
    if (clipHalf * 2f > collapsedWidth) {
        drawRoundRect(
            color = color,
            topLeft = Offset(center.x - clipHalf, center.y - clipHalf),
            size = Size(clipHalf * 2f, clipHalf * 2f),
            cornerRadius = CornerRadius(iconRadius),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFEEEEEE)
@Composable
private fun SwitcherPreview() {
    Column(
        modifier = Modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        var checked by remember { mutableStateOf(true) }
        PillSwitcher(checked = checked, onCheckedChange = { checked = it })
        PillSwitcher(checked = !checked, onCheckedChange = { checked = !it })
        CircleSwitcher(checked = checked, onCheckedChange = { checked = it })
        CircleSwitcher(checked = !checked, onCheckedChange = { checked = !it })
    }
}
