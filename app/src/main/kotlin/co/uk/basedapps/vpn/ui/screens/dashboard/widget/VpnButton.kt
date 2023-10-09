package co.uk.basedapps.vpn.ui.screens.dashboard.widget

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import co.uk.basedapps.vpn.R

@Composable
fun VpnButton(
  state: VpnButtonState,
  onClick: () -> Unit,
) {
  val offset by animateDpAsState(
    targetValue = when (state) {
      VpnButtonState.Disconnected,
      VpnButtonState.Connecting,
      -> 0.dp

      VpnButtonState.Connected,
      VpnButtonState.Disconnecting,
      -> 56.dp
    },
    label = "Button offset",
  )
  val buttonColor by animateColorAsState(
    targetValue = when (state) {
      VpnButtonState.Connected -> Color(0xFFCC2229)
      else -> Color(0x80CC2229)
    },
    label = "Button color",
  )
  val iconColor by animateColorAsState(
    targetValue = when (state) {
      VpnButtonState.Connected -> Color.White
      else -> Color(0x33EEB5B5)
    },
    label = "Button icon color",
  )
  Box(
    modifier = Modifier
      .clickable(
        enabled = state != VpnButtonState.Connecting,
        onClick = onClick,
      )
      .clip(RoundedCornerShape(100))
      .size(width = 120.dp, height = 64.dp)
      .background(color = Color(0xFF22262D))
      .offset(x = offset),
  ) {
    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier
        .padding(4.dp)
        .clip(CircleShape)
        .background(buttonColor)
        .size(56.dp),
    ) {
      if (state == VpnButtonState.Connected || state == VpnButtonState.Disconnected) {
        Icon(
          painter = painterResource(R.drawable.ic_power),
          contentDescription = null,
          tint = iconColor,
          modifier = Modifier.size(28.dp),
        )
      } else {
        CircularProgressIndicator(
          color = iconColor,
        )
      }
    }
  }
}

@Stable
enum class VpnButtonState {
  Disconnected,
  Connecting,
  Connected,
  Disconnecting,
}
