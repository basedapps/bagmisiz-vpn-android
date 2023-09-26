package co.uk.basedapps.vpn.ui.screens.intro.widget

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import co.uk.basedapps.vpn.R

@Composable
fun SentinelLink(
  modifier: Modifier = Modifier,
) {
  Text(
    modifier = modifier,
    fontSize = 14.sp,
    fontWeight = FontWeight.W400,
    text = buildAnnotatedString {
      withStyle(SpanStyle(color = Color(0xFF444B57))) {
        append(stringResource(R.string.intro_sentinel_text))
      }
      append(" ")
      withStyle(
        SpanStyle(
          color = Color(0xFFAAB2BE),
          textDecoration = TextDecoration.Underline,
        ),
      ) {
        append(stringResource(R.string.intro_sentinel_link))
      }
    },
  )
}
