package co.uk.basedapps.vpn.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import co.uk.basedapps.vpn.R
import co.uk.basedapps.vpn.common.compose.EffectHandler
import co.uk.basedapps.vpn.common.ext.openWeb
import co.uk.basedapps.vpn.network.model.Protocol
import co.uk.basedapps.vpn.viewModel.settings.SettingsScreenState as State
import co.uk.basedapps.vpn.ui.screens.settings.widgets.DnsDialog
import co.uk.basedapps.vpn.ui.screens.settings.widgets.ProtocolDialog
import co.uk.basedapps.vpn.ui.theme.BasedAppColor
import co.uk.basedapps.vpn.ui.widget.BaseRow
import co.uk.basedapps.vpn.ui.widget.TopBar
import co.uk.basedapps.vpn.viewModel.settings.SettingsScreenEffect
import co.uk.basedapps.vpn.viewModel.settings.SettingsScreenViewModel
import co.uk.basedapps.vpn.vpn.DdsConfigurator
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
  navigateBack: () -> Unit,
) {

  val viewModel = hiltViewModel<SettingsScreenViewModel>()
  val state by viewModel.stateHolder.state.collectAsState()

  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current

  val snackbarHostState = remember { SnackbarHostState() }
  val scope = rememberCoroutineScope()

  EffectHandler(viewModel.stateHolder.effects) { effect ->
    when (effect) {
      is SettingsScreenEffect.OpenTelegram ->
        context.openWeb("https://t.me/bagimsizdvpn")

      is SettingsScreenEffect.CopyLogsToClipboard -> {
        clipboardManager.setText(AnnotatedString(effect.logs))
        scope.launch {
          snackbarHostState.showSnackbar(
            context.getString(R.string.settings_logs_success),
          )
        }
      }
    }
  }

  SettingsScreenStateless(
    state = state,
    snackbarHostState = snackbarHostState,
    navigateBack = navigateBack,
    onDnsRowClick = viewModel::onDnsRowClick,
    onDnsDialogConfirmClick = viewModel::onDnsSelected,
    onDnsDialogDismissClick = viewModel::onDnsDialogDismissClick,
    onTelegramClick = viewModel::onTelegramClick,
    onProtocolRowClick = viewModel::onProtocolRowClick,
    onProtocolDialogConfirmClick = viewModel::onProtocolSelected,
    onProtocolDialogDismissClick = viewModel::onProtocolDialogDismissClick,
    onLogsRowClick = viewModel::onLogsRowClick,
  )
}

@Composable
fun SettingsScreenStateless(
  state: State,
  snackbarHostState: SnackbarHostState,
  navigateBack: () -> Unit,
  onDnsRowClick: () -> Unit,
  onDnsDialogConfirmClick: (DdsConfigurator.Dns) -> Unit,
  onDnsDialogDismissClick: () -> Unit,
  onProtocolRowClick: () -> Unit,
  onProtocolDialogConfirmClick: (Protocol) -> Unit,
  onProtocolDialogDismissClick: () -> Unit,
  onLogsRowClick: () -> Unit,
  onTelegramClick: () -> Unit,
) {
  Scaffold(
    containerColor = Color.Transparent,
    topBar = {
      TopBar(
        title = stringResource(R.string.settings_title),
        navigateBack = navigateBack,
      )
    },
    snackbarHost = { SnackbarHost(snackbarHostState) },
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
        onLogsRowClick = onLogsRowClick,
        onTelegramClick = onTelegramClick,
      )
    },
    modifier = Modifier.background(
      brush = Brush.verticalGradient(
        colors = listOf(Color(0xFF22262E), Color(0xFF14161B)),
      ),
    ),
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
  onLogsRowClick: () -> Unit,
  onTelegramClick: () -> Unit,
) {
  Box(Modifier.padding(paddingValues)) {
    LazyColumn(
      verticalArrangement = Arrangement.spacedBy(10.dp),
      contentPadding = PaddingValues(
        horizontal = 32.dp,
        vertical = 16.dp,
      ),
      modifier = Modifier.fillMaxSize(),
    ) {
      item {
        BaseRow(
          title = stringResource(R.string.settings_row_dns),
          subtitle = state.currentDns
            ?.let { stringResource(it.getLabelRes()) } ?: "",
          iconRes = R.drawable.ic_server,
          onClick = onDnsRowClick,
        )
      }
      item {
        BaseRow(
          title = stringResource(R.string.settings_row_protocol),
          subtitle = state.currentProtocol?.labelRes
            ?.let { stringResource(it) } ?: "",
          iconRes = R.drawable.ic_server,
          onClick = onProtocolRowClick,
        )
      }
      item {
        BaseRow(
          title = stringResource(R.string.settings_row_logs),
          subtitle = stringResource(R.string.settings_logs_description),
          iconRes = R.drawable.ic_server,
          onClick = onLogsRowClick,
        )
      }
      item {
        BaseRow(
          title = stringResource(R.string.settings_row_telegram),
          subtitle = stringResource(R.string.settings_row_telegram_description),
          iconRes = R.drawable.ic_server,
          onClick = onTelegramClick,
        )
      }
    }
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier
        .padding(bottom = 16.dp)
        .align(Alignment.BottomCenter),
    ) {
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

fun DdsConfigurator.Dns.getLabelRes() =
  when (this) {
    DdsConfigurator.Dns.Cloudflare -> R.string.settings_dns_cloudflare
    DdsConfigurator.Dns.Google -> R.string.settings_dns_google
    DdsConfigurator.Dns.Handshake -> R.string.settings_dns_handshake
  }
