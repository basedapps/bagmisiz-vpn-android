package co.uk.basedapps.vpn.ui.widget

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import co.uk.basedapps.vpn.ui.theme.BasedAppColor

@Composable
fun BasedAlertDialog(
  title: String,
  description: String,
  onConfirmClick: () -> Unit,
  onDismissClick: (() -> Unit)? = null,
  onDismissRequest: () -> Unit = {},
) {
  AlertDialog(
    onDismissRequest = onDismissRequest,
    title = {
      Text(
        text = title,
        color = BasedAppColor.TextPrimary,
      )
    },
    text = {
      Text(
        text = description,
        color = BasedAppColor.TextSecondary,
      )
    },
    containerColor = BasedAppColor.Background,
    confirmButton = {
      Button(
        colors = ButtonDefaults.buttonColors(
          containerColor = BasedAppColor.ButtonPrimary,
          contentColor = BasedAppColor.ButtonPrimaryText,
        ),
        onClick = onConfirmClick,
      ) {
        Text("Ok")
      }
    },
    dismissButton = onDismissClick?.let {
      {
        Button(
          colors = ButtonDefaults.buttonColors(
            containerColor = BasedAppColor.ButtonPrimary,
            contentColor = BasedAppColor.ButtonPrimaryText,
          ),
          onClick = onDismissClick,
        ) {
          Text("Cancel")
        }
      }
    },
  )
}

@Composable
@Preview
fun BasedAlertDialogPreview() {
  BasedAlertDialog(
    title = "Hello World",
    description = "Lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum",
    onConfirmClick = {},
    onDismissClick = {},
  )
}
