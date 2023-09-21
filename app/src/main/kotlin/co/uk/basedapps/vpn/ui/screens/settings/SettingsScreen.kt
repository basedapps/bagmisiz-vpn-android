package co.uk.basedapps.vpn.ui.screens.settings

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import co.uk.basedapps.vpn.R
import co.uk.basedapps.vpn.common.EffectHandler
import co.uk.basedapps.vpn.common.openWeb
import co.uk.basedapps.vpn.network.model.Protocol
import co.uk.basedapps.vpn.ui.screens.settings.SettingsScreenState as State
import co.uk.basedapps.vpn.ui.screens.settings.widgets.DnsDialog
import co.uk.basedapps.vpn.ui.screens.settings.widgets.ProtocolDialog
import co.uk.basedapps.vpn.ui.theme.BasedAppColor
import co.uk.basedapps.vpn.ui.widget.TelegramButton
import co.uk.basedapps.vpn.ui.widget.TopBar
import co.uk.basedapps.vpn.vpn.DdsConfigurator

@Composable
fun SettingsScreen(
  navigateBack: () -> Unit,
) {

  val viewModel = hiltViewModel<SettingsScreenViewModel>()
  val state by viewModel.stateHolder.state.collectAsState()
  val context = LocalContext.current

  EffectHandler(viewModel.stateHolder.effects) { effect ->
    when (effect) {
      is SettingsScreenEffect.OpenTelegram ->
        context.openWeb("https://t.me/bagimsizdvpn")
    }
  }

  SettingsScreenStateless(
    state = state,
    navigateBack = navigateBack,
    onDnsRowClick = viewModel::onDnsRowClick,
    onDnsDialogConfirmClick = viewModel::onDnsSelected,
    onDnsDialogDismissClick = viewModel::onDnsDialogDismissClick,
    onTelegramClick = viewModel::onTelegramClick,
    onProtocolRowClick = viewModel::onProtocolRowClick,
    onProtocolDialogConfirmClick = viewModel::onProtocolSelected,
    onProtocolDialogDismissClick = viewModel::onProtocolDialogDismissClick,
  )
}

@Composable
fun SettingsScreenStateless(
  state: State,
  navigateBack: () -> Unit,
  onDnsRowClick: () -> Unit,
  onDnsDialogConfirmClick: (DdsConfigurator.Dns) -> Unit,
  onDnsDialogDismissClick: () -> Unit,
  onProtocolRowClick: () -> Unit,
  onProtocolDialogConfirmClick: (Protocol) -> Unit,
  onProtocolDialogDismissClick: () -> Unit,
  onTelegramClick: () -> Unit,
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
        onProtocolRowClick = onProtocolRowClick,
        onProtocolDialogConfirmClick = onProtocolDialogConfirmClick,
        onProtocolDialogDismissClick = onProtocolDialogDismissClick,
        onTelegramClick = onTelegramClick,
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
  onProtocolRowClick: () -> Unit,
  onProtocolDialogConfirmClick: (Protocol) -> Unit,
  onProtocolDialogDismissClick: () -> Unit,
  onTelegramClick: () -> Unit,
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
      item {
        SettingsRow(
          title = stringResource(R.string.settings_row_protocol),
          value = state.currentProtocol?.labelRes
            ?.let { stringResource(it) } ?: "",
          onItemClick = onProtocolRowClick,
        )
      }
    }
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier
        .padding(bottom = 16.dp)
        .align(Alignment.BottomCenter),
    ) {
      TelegramButton(onTelegramClick)
      Spacer(modifier = Modifier.size(16.dp))
      Text(
        text = stringResource(R.string.settings_app_version, state.appVersion),
        fontSize = 16.sp,
        color = BasedAppColor.TextSecondary,
      )
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

  if (state.isProtocolSelectorVisible) {
    ProtocolDialog(
      state = state,
      onConfirmClick = onProtocolDialogConfirmClick,
      onDismissClick = onProtocolDialogDismissClick,
      onDismissRequest = onProtocolDialogDismissClick,
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
      fontSize = 16.sp,
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

fun DdsConfigurator.Dns.getLabelRes() =
  when (this) {
    DdsConfigurator.Dns.Cloudflare -> R.string.settings_dns_cloudflare
    DdsConfigurator.Dns.Google -> R.string.settings_dns_google
    DdsConfigurator.Dns.Handshake -> R.string.settings_dns_handshake
  }
