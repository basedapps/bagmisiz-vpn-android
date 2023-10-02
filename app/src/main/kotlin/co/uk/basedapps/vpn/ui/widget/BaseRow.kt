package co.uk.basedapps.vpn.ui.widget

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.uk.basedapps.vpn.R

@Composable
fun BaseRow(
  title: String,
  subtitle: String? = null,
  @DrawableRes imageRes: Int? = null,
  @DrawableRes iconRes: Int? = null,
  onClick: () -> Unit,
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .clip(RoundedCornerShape(100))
      .clickable(onClick = { onClick() })
      .height(54.dp)
      .background(Color(0xFF262932))
      .padding(horizontal = 16.dp)
      .fillMaxWidth(),
  ) {
    if (imageRes != null) {
      Image(
        painter = painterResource(imageRes),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
          .size(width = 28.dp, height = 28.dp)
          .clip(CircleShape),
      )
    } else if (iconRes != null) {
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .clip(CircleShape)
          .background(Color(0xFF2C3037))
          .size(28.dp),
      ) {
        Icon(
          painter = painterResource(iconRes),
          contentDescription = null,
          tint = Color.White,
          modifier = Modifier.size(12.dp),
        )
      }
    }
    Spacer(modifier = Modifier.size(16.dp))
    Column {
      Text(
        text = title,
        color = Color.White,
        fontWeight = FontWeight.W600,
        fontSize = 16.sp,
      )
      if (subtitle != null) {
        Spacer(modifier = Modifier.size(2.dp))
        Text(
          text = subtitle,
          color = Color.White.copy(alpha = 0.6f),
          fontWeight = FontWeight.W400,
          fontSize = 12.sp,
        )
      }
    }
    Spacer(modifier = Modifier.weight(1f))
    Icon(
      painter = painterResource(R.drawable.ic_arrow),
      contentDescription = null,
      tint = Color.White,
      modifier = Modifier
        .size(24.dp)
        .padding(5.dp),
    )
  }
}
