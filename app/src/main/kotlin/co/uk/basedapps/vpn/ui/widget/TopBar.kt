package co.uk.basedapps.vpn.ui.widget

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import co.uk.basedapps.vpn.common.TopBarIconsColorEffect
import co.uk.basedapps.vpn.ui.theme.BasedAppColor

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TopBar(
  title: String,
  navigateBack: () -> Unit,
  isAccented: Boolean = false,
) {
  TopBarIconsColorEffect(isDark = !isAccented)

  Surface(
    shadowElevation = 4.dp,
  ) {
    val colors = when (isAccented) {
      true -> TopAppBarDefaults.topAppBarColors(
        containerColor = BasedAppColor.Accent,
        navigationIconContentColor = Color.White,
        titleContentColor = Color.White,
      )

      false ->
        TopAppBarDefaults.topAppBarColors(
          containerColor = BasedAppColor.Background,
          navigationIconContentColor = BasedAppColor.Accent,
          titleContentColor = BasedAppColor.TextPrimary,
        )
    }
    TopAppBar(
      title = { Text(text = title) },
      colors = colors,
      navigationIcon = {
        IconButton(
          onClick = navigateBack,
        ) {
          Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = "Go back",
          )
        }
      },
    )
  }
}
