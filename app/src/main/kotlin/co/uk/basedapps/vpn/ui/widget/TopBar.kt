package co.uk.basedapps.vpn.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.uk.basedapps.vpn.R

@Composable
fun TopBar(
  title: String,
  navigateBack: () -> Unit,
) {
  Box(
    modifier = Modifier
      .statusBarsPadding()
      .padding(horizontal = 20.dp)
      .padding(top = 20.dp, bottom = 8.dp)
      .fillMaxWidth(),
  ) {
    Button(
      colors = ButtonDefaults.buttonColors(
        containerColor = Color(0xFF1F2B36),
      ),
      shape = CircleShape,
      contentPadding = PaddingValues(0.dp),
      onClick = navigateBack,
      modifier = Modifier.size(40.dp),
    ) {
      Icon(
        painter = painterResource(R.drawable.ic_back),
        contentDescription = "Go back",
        modifier = Modifier.size(16.dp),
        tint = Color.White,
      )
    }
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxWidth(),
    ) {
      Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .clip(RoundedCornerShape(100.dp))
          .background(color = Color(0xFF101921))
          .fillMaxWidth(0.6f),
      ) {
        Text(
          text = title,
          color = Color.White,
          fontSize = 14.sp,
          fontWeight = FontWeight.W500,
          maxLines = 1,
          modifier = Modifier.padding(vertical = 10.dp),
        )
      }
    }
  }
}
