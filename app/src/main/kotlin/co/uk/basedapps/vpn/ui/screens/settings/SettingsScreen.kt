package co.uk.basedapps.vpn.ui.screens.settings

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import co.uk.basedapps.vpn.R
import co.uk.basedapps.vpn.ui.screens.settings.SettingsScreenState as State
import co.uk.basedapps.vpn.ui.theme.BasedAppColor
import co.uk.basedapps.vpn.ui.widget.TopBar
import co.uk.basedapps.vpn.vpn.DdsConfigurator

@Composable
fun SettingsScreen(
  navigateBack: () -> Unit,
) {

  val viewModel = hiltViewModel<SettingsScreenViewModel>()
  val state by viewModel.stateHolder.state.collectAsState()

  SettingsScreenStateless(
    state = state,
    navigateBack = navigateBack,
    onDnsRowClick = viewModel::onDnsRowClick,
    onDnsDialogConfirmClick = viewModel::onDnsSelected,
    onDnsDialogDismissClick = viewModel::onDnsDialogDismissClick,
  )
}

@Composable
fun SettingsScreenStateless(
  state: State,
  navigateBack: () -> Unit,
  onDnsRowClick: () -> Unit,
  onDnsDialogConfirmClick: (DdsConfigurator.Dns) -> Unit,
  onDnsDialogDismissClick: () -> Unit,
) {
  Scaffold(
    containerColor = BasedAppColor.Background,
    topBar = {
      TopBar(
        title = stringResource(R.string.settings_title),
        navigateBack = navigateBack,
      )
    },
    content = { paddingValues ->
      Content(
        paddingValues = paddingValues,
        state = state,
        onDnsRowClick = onDnsRowClick,
        onDnsDialogConfirmClick = onDnsDialogConfirmClick,
        onDnsDialogDismissClick = onDnsDialogDismissClick,
      )
    },
  )
}

@Composable
fun Content(
  paddingValues: PaddingValues,
  state: State,
  onDnsRowClick: () -> Unit,
  onDnsDialogConfirmClick: (DdsConfigurator.Dns) -> Unit,
  onDnsDialogDismissClick: () -> Unit,
) {
  Box(Modifier.padding(paddingValues)) {
    LazyVerticalGrid(
      columns = GridCells.Fixed(2),
      verticalArrangement = Arrangement.spacedBy(12.dp),
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      contentPadding = PaddingValues(16.dp),
      modifier = Modifier.fillMaxSize(),
    ) {
      item {
        SettingsRow(
          title = stringResource(R.string.settings_row_dns),
          value = state.currentDns
            ?.let { stringResource(it.getLabelRes()) } ?: "",
          onItemClick = onDnsRowClick,
        )
      }
    }
  }
  if (state.isDnsSelectorVisible) {
    DnsDialog(
      state = state,
      onConfirmClick = onDnsDialogConfirmClick,
      onDismissClick = onDnsDialogDismissClick,
      onDismissRequest = onDnsDialogDismissClick,
    )
  }
}

@Composable
private fun SettingsRow(
  title: String,
  value: String,
  onItemClick: () -> Unit,
) {
  Column(
    modifier = Modifier
      .clip(RoundedCornerShape(8.dp))
      .clickable(onClick = onItemClick)
      .border(
        width = 1.dp,
        color = BasedAppColor.Divider,
        shape = RoundedCornerShape(8.dp),
      )
      .padding(16.dp),
  ) {
    Text(
      text = title,
      overflow = TextOverflow.Ellipsis,
      maxLines = 1,
      fontSize = 18.sp,
      color = BasedAppColor.TextPrimary,
    )
    Spacer(modifier = Modifier.size(2.dp))
    Text(
      text = value,
      overflow = TextOverflow.Ellipsis,
      maxLines = 1,
      fontSize = 12.sp,
      color = BasedAppColor.TextSecondary,
    )
  }
}

@Composable
private fun DnsDialog(
  state: State,
  onConfirmClick: (DdsConfigurator.Dns) -> Unit,
  onDismissClick: () -> Unit,
  onDismissRequest: () -> Unit = {},
) {
  var radioState by remember { mutableStateOf(state.currentDns) }
  AlertDialog(
    onDismissRequest = onDismissRequest,
    containerColor = BasedAppColor.Background,
    title = { Text(stringResource(R.string.settings_dns_change_title)) },
    text = {
      Column {
        state.dnsOptions.forEach { dns ->
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .fillMaxWidth()
              .selectable(
                selected = dns == radioState,
                onClick = { radioState = dns },
                role = Role.RadioButton,
              )
              .padding(vertical = 8.dp),
          ) {
            RadioButton(
              selected = dns == radioState,
              onClick = null,
              colors = RadioButtonDefaults.colors(
                selectedColor = BasedAppColor.Accent,
              ),
              modifier = Modifier.padding(end = 8.dp),
            )
            Text(
              text = stringResource(dns.getLabelRes()),
              maxLines = 1,
            )
          }
        }
      }
    },
    confirmButton = {
      Button(
        colors = ButtonDefaults.buttonColors(
          containerColor = BasedAppColor.ButtonPrimary,
          contentColor = BasedAppColor.ButtonPrimaryText,
        ),
        onClick = { radioState?.let(onConfirmClick) },
      ) { Text(stringResource(R.string.common_ok)) }
    },
    dismissButton = {
      Button(
        colors = ButtonDefaults.buttonColors(
          containerColor = BasedAppColor.ButtonPrimary,
          contentColor = BasedAppColor.ButtonPrimaryText,
        ),
        onClick = onDismissClick,
      ) {
        Text(stringResource(R.string.common_cancel))
      }
    },
  )
}

@Composable
@Preview
private fun DnsDialogPreview() {
  DnsDialog(
    state = State(
      currentDns = DdsConfigurator.Dns.Cloudflare,
    ),
    onConfirmClick = {},
    onDismissClick = {},
    onDismissRequest = {},
  )
}

fun DdsConfigurator.Dns.getLabelRes() =
  when (this) {
    DdsConfigurator.Dns.Cloudflare -> R.string.settings_dns_cloudflare
    DdsConfigurator.Dns.Google -> R.string.settings_dns_google
    DdsConfigurator.Dns.Handshake -> R.string.settings_dns_handshake
  }
