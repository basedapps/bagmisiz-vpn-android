package co.uk.basedapps.vpn.ui.screens.settings

import co.uk.basedapps.vpn.BuildConfig
import co.uk.basedapps.vpn.common.ViewStateHolder
import co.uk.basedapps.vpn.network.model.Protocol
import co.uk.basedapps.vpn.vpn.DdsConfigurator
import javax.inject.Inject

class SettingsScreenStateHolder
@Inject
constructor() : ViewStateHolder<SettingsScreenState, SettingsScreenEffect>(
  SettingsScreenState(),
)

data class SettingsScreenState(
  val currentDns: DdsConfigurator.Dns? = null,
  val dnsOptions: List<DdsConfigurator.Dns> = listOf(
    DdsConfigurator.Dns.Cloudflare,
    DdsConfigurator.Dns.Google,
    DdsConfigurator.Dns.Handshake,
  ),
  val isDnsSelectorVisible: Boolean = false,

  val currentProtocol: Protocol? = null,
  val protocolOptions: List<Protocol> = listOf(
    Protocol.WIREGUARD,
    Protocol.V2RAY,
    Protocol.NONE,
  ),
  val isProtocolSelectorVisible: Boolean = false,

  val appVersion: String = BuildConfig.VERSION_NAME,
)

sealed interface SettingsScreenEffect {
  data object OpenTelegram : SettingsScreenEffect
}
