package co.uk.basedapps.vpn.ui.widget

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.uk.basedapps.vpn.R
import co.uk.basedapps.vpn.ui.theme.BasedAppColor

@Composable
fun TelegramButton(
  onClick: () -> Unit,
) {
  OutlinedButton(
    onClick = onClick,
    colors = ButtonDefaults.outlinedButtonColors(
      contentColor = BasedAppColor.TextPrimary,
    ),
    border = BorderStroke(1.dp, BasedAppColor.Divider),
    shape = RoundedCornerShape(8.dp),
    modifier = Modifier.heightIn(min = 52.dp),
  ) {
    Icon(
      painter = painterResource(R.drawable.ic_rocket),
      contentDescription = null,
      tint = BasedAppColor.Telegram,
      modifier = Modifier.size(24.dp),
    )
    Spacer(modifier = Modifier.size(16.dp))
    Text(
      text = stringResource(R.string.settings_telegram_btn),
      fontSize = 16.sp,
    )
  }
}
