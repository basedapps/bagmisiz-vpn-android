package co.uk.basedapps.vpn.ui.screens.intro.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BulletBlock(text: String) {
  Row {
    Box(
      modifier = Modifier
        .padding(top = 10.dp, end = 10.dp)
        .clip(CircleShape)
        .size(5.dp)
        .background(color = Color(0xFFCC2229)),
    ) {}
    Text(
      text = text,
      color = Color(0xFF868D9B),
      fontSize = 16.sp,
      fontWeight = FontWeight.W400,
    )
  }
}
