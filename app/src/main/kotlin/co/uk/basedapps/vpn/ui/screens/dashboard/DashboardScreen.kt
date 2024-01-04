@file:OptIn(MapboxExperimental::class)

package co.uk.basedapps.vpn.ui.screens.dashboard

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import co.uk.basedapps.vpn.R
import co.uk.basedapps.vpn.common.compose.EffectHandler
import co.uk.basedapps.vpn.common.ext.goToGooglePlay
import co.uk.basedapps.vpn.common.flags.CountryFlag
import co.uk.basedapps.vpn.common.state.Status
import co.uk.basedapps.vpn.storage.SelectedCity
import co.uk.basedapps.vpn.ui.screens.dashboard.widget.MapboxConfiguredMap
import co.uk.basedapps.vpn.ui.screens.dashboard.widget.VpnButton
import co.uk.basedapps.vpn.ui.screens.dashboard.widget.VpnButtonState
import co.uk.basedapps.vpn.ui.theme.BasedAppColor
import co.uk.basedapps.vpn.ui.theme.BasedVPNTheme
import co.uk.basedapps.vpn.ui.widget.BasedAlertDialog
import co.uk.basedapps.vpn.ui.widget.ErrorScreen
import co.uk.basedapps.vpn.viewModel.dashboard.DashboardScreenEffect as Effect
import co.uk.basedapps.vpn.viewModel.dashboard.DashboardScreenState as State
import co.uk.basedapps.vpn.viewModel.dashboard.DashboardScreenViewModel
import co.uk.basedapps.vpn.viewModel.dashboard.VpnStatus
import co.uk.basedapps.vpn.vpn.getVpnPermissionRequest
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun DashboardScreen(
  navigateToCountries: () -> Unit,
  navigateToSettings: () -> Unit,
  showAd: suspend () -> Boolean,
) {
  val viewModel = hiltViewModel<DashboardScreenViewModel>()
  val state by viewModel.stateHolder.state.collectAsState()

  val context = LocalContext.current

  val scope = rememberCoroutineScope()
  val mapViewportState = rememberMapViewportState {}

  val vpnPermissionRequest = rememberLauncherForActivityResult(
    ActivityResultContracts.StartActivityForResult(),
  ) { result ->
    viewModel.onPermissionsResult(result.resultCode == Activity.RESULT_OK)
  }

  EffectHandler(viewModel.stateHolder.effects) { effect ->
    when (effect) {
      is Effect.ShowAd -> scope.launch {
        showAd()
        viewModel.onAdShown()
      }

      is Effect.ShowSelectServer -> navigateToCountries()

      is Effect.CheckVpnPermission -> {
        val intent = getVpnPermissionRequest(context)
        if (intent != null) {
          vpnPermissionRequest.launch(intent)
        } else {
          viewModel.onPermissionsResult(true)
        }
      }

      is Effect.ShowSettings -> navigateToSettings()

      is Effect.ShowGooglePlay -> context.goToGooglePlay()

      is Effect.ChangeMapPosition -> {
        scope.launch(Dispatchers.Main) {
          mapViewportState.flyTo(
            animationOptions = MapAnimationOptions.mapAnimationOptions {
              duration(2000)
            },
            cameraOptions = CameraOptions.Builder()
              .center(Point.fromLngLat(effect.longitude, effect.latitude))
              .zoom(9.0)
              .build(),
          )
        }
      }
    }
  }

  DashboardScreenStateless(
    state = state,
    mapViewportState = mapViewportState,
    onConnectClick = viewModel::onConnectClick,
    onSelectServerClick = viewModel::onSelectServerClick,
    onSettingsClick = viewModel::onSettingsClick,
    onTryAgainClick = viewModel::onTryAgainClick,
    onUpdateClick = viewModel::onUpdateClick,
    onAlertConfirmClick = viewModel::onAlertConfirmClick,
    onAlertDismissRequest = viewModel::onAlertDismissRequest,
  )
}

@Composable
fun DashboardScreenStateless(
  state: State,
  mapViewportState: MapViewportState,
  onConnectClick: () -> Unit,
  onSelectServerClick: () -> Unit,
  onSettingsClick: () -> Unit,
  onTryAgainClick: () -> Unit,
  onUpdateClick: () -> Unit,
  onAlertConfirmClick: () -> Unit,
  onAlertDismissRequest: () -> Unit,
) {
  when {
    state.isOutdated -> ErrorScreen(
      title = stringResource(R.string.update_required_title),
      description = stringResource(R.string.update_required_description),
      buttonLabel = stringResource(R.string.update_required_button),
      imageResId = R.drawable.ic_update,
      onButtonClick = onUpdateClick,
    )

    state.isBanned -> ErrorScreen(
      title = null,
      description = stringResource(R.string.error_banned_title),
      onButtonClick = null,
    )

    state.status is Status.Error -> ErrorScreen(
      isLoading = (state.status as Status.Error).isLoading,
      onButtonClick = onTryAgainClick,
    )

    else -> Content(
      state = state,
      mapViewportState = mapViewportState,
      onConnectClick = onConnectClick,
      onSelectServerClick = onSelectServerClick,
      onSettingsClick = onSettingsClick,
      onAlertConfirmClick = onAlertConfirmClick,
      onAlertDismissRequest = onAlertDismissRequest,
    )
  }
}

@Composable
private fun Content(
  state: State,
  mapViewportState: MapViewportState,
  onConnectClick: () -> Unit,
  onSelectServerClick: () -> Unit,
  onSettingsClick: () -> Unit,
  onAlertConfirmClick: () -> Unit,
  onAlertDismissRequest: () -> Unit = {},
) {
  Box(
    modifier = Modifier
      .background(
        brush = Brush.verticalGradient(
          colors = listOf(Color(0xFF22262E), Color(0xFF14161B)),
        ),
      )
      .fillMaxSize(),
  ) {
    Column {
      TopBar(
        state = state,
        onSettingsClick = onSettingsClick,
      )
      Map(
        state = state,
        mapViewportState = mapViewportState,
        modifier = Modifier
          .weight(1f)
          .padding(horizontal = 32.dp)
          .clip(RoundedCornerShape(8.dp)),
      )
      BottomBar(
        state = state,
        onConnectClick = onConnectClick,
        onSelectServerClick = onSelectServerClick,
      )
    }
    if (state.status is Status.Loading) {
      LoadingOverlay()
    }
    if (state.isErrorAlertVisible) {
      BasedAlertDialog(
        title = stringResource(R.string.dashboard_error_connection_title),
        description = stringResource(R.string.dashboard_error_connection_description),
        onConfirmClick = onAlertConfirmClick,
        onDismissRequest = onAlertDismissRequest,
      )
    }
  }
}

@Composable
private fun Map(
  state: State,
  mapViewportState: MapViewportState,
  modifier: Modifier = Modifier,
) {
  val context = LocalContext.current
  val countryLabel = remember(state.selectedCity, state.vpnStatus) {
    if (state.vpnStatus == VpnStatus.Connected) {
      state.selectedCity?.countryName ?: ""
    } else {
      context.getString(R.string.dashboard_current_location)
    }
  }
  Box(
    modifier = modifier,
  ) {
    MapboxConfiguredMap(mapViewportState)
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier
        .padding(top = 12.dp)
        .fillMaxWidth(),
    ) {
      Text(
        text = countryLabel,
        color = Color.White,
        fontSize = 18.sp,
        fontWeight = FontWeight.W700,
      )
      Spacer(modifier = Modifier.size(8.dp))
      Text(
        text = state.ipAddress,
        color = Color(0xFFACB3BD),
        fontSize = 14.sp,
        fontWeight = FontWeight.W400,
      )
    }
  }
}

@Composable
private fun TopBar(
  state: State,
  onSettingsClick: () -> Unit,
) {
  val context = LocalContext.current
  Box(
    modifier = Modifier
      .statusBarsPadding()
      .padding(horizontal = 20.dp)
      .padding(top = 20.dp, bottom = 32.dp)
      .fillMaxWidth(),
  ) {
    Button(
      colors = ButtonDefaults.buttonColors(
        containerColor = Color(0xFF1F2B36),
      ),
      shape = CircleShape,
      contentPadding = PaddingValues(0.dp),
      onClick = onSettingsClick,
      modifier = Modifier.size(40.dp),
    ) {
      Icon(
        painter = painterResource(R.drawable.ic_settings),
        contentDescription = stringResource(R.string.dashboard_menu_settings),
        modifier = Modifier.size(16.dp),
        tint = Color.White,
      )
    }
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxWidth(),
    ) {
      val protectedLabel = remember(state.vpnStatus) {
        context.getString(
          when (state.vpnStatus == VpnStatus.Connected) {
            true -> R.string.dashboard_protected
            false -> R.string.dashboard_not_protected
          },
        )
      }
      Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .clip(RoundedCornerShape(100.dp))
          .background(color = Color(0xFF101921))
          .fillMaxWidth(0.6f),
      ) {
        Image(
          painter = painterResource(
            when (state.vpnStatus == VpnStatus.Connected) {
              true -> R.drawable.ic_connected
              false -> R.drawable.ic_disconnected
            },
          ),
          contentDescription = protectedLabel,
          modifier = Modifier.size(16.dp),
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
          text = protectedLabel,
          color = when (state.vpnStatus == VpnStatus.Connected) {
            true -> Color.White
            false -> Color(0xFF54687A)
          },
          fontSize = 14.sp,
          fontWeight = FontWeight.W500,
          maxLines = 1,
          modifier = Modifier.padding(vertical = 10.dp),
        )
      }
    }
  }
}

@Composable
fun BottomBar(
  state: State,
  onConnectClick: () -> Unit,
  onSelectServerClick: () -> Unit,
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 45.dp)
      .padding(top = 24.dp, bottom = 24.dp)
      .navigationBarsPadding(),
  ) {
    val buttonState = state.vpnStatus.toButtonState()
    VpnButton(buttonState, onConnectClick)
    Spacer(modifier = Modifier.size(12.dp))
    val stateLabelRes = when (state.vpnStatus) {
      VpnStatus.Connected -> R.string.dashboard_state_connected
      VpnStatus.Connecting -> R.string.dashboard_state_connecting
      VpnStatus.Disconnected -> R.string.dashboard_state_disconnected
      VpnStatus.Disconnecting -> R.string.dashboard_state_disconnecting
    }
    Text(
      text = stringResource(stateLabelRes),
      color = Color.White,
      fontWeight = FontWeight.W600,
      fontSize = 16.sp,
    )
    val infoText = when (state.vpnStatus) {
      VpnStatus.Connecting -> stringResource(R.string.dashboard_state_connecting_info)
      else -> ""
    }
    Text(
      text = infoText,
      color = BasedAppColor.TextSecondary,
      fontSize = 12.sp,
    )
    val selectedCity = state.selectedCity
    if (selectedCity != null) {
      Spacer(modifier = Modifier.size(12.dp))
      SelectedCountryRow(
        selectedCity = selectedCity,
        onClick = onSelectServerClick,
        isEnabled = state.status != Status.Loading,
      )
    }
  }
}

@Composable
fun SelectedCountryRow(
  selectedCity: SelectedCity,
  onClick: () -> Unit,
  isEnabled: Boolean,
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .clip(RoundedCornerShape(100))
      .clickable(isEnabled, onClick = onClick)
      .height(54.dp)
      .background(Color(0xFF262932))
      .padding(horizontal = 16.dp)
      .fillMaxWidth(),
  ) {
    val flagRes = selectedCity.countryFlag?.res
    if (flagRes != null) {
      Image(
        painter = painterResource(flagRes),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
          .size(width = 28.dp, height = 28.dp)
          .clip(CircleShape),
      )
    }
    Spacer(modifier = Modifier.size(16.dp))
    Text(
      text = selectedCity.countryName,
      color = Color.White,
      fontWeight = FontWeight.W500,
      fontSize = 16.sp,
    )
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

@Composable
private fun LoadingOverlay() {
  Box(
    contentAlignment = Alignment.Center,
    modifier = Modifier
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = {},
      )
      .navigationBarsPadding()
      .background(Color.Black.copy(alpha = 0.5f))
      .fillMaxSize(),
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      CircularProgressIndicator(
        color = Color.White,
      )
      Spacer(modifier = Modifier.size(24.dp))
      Text(
        text = stringResource(R.string.dashboard_loading),
        fontSize = 16.sp,
        color = Color.White,
        textAlign = TextAlign.Center,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 48.dp),
      )
    }
  }
}

fun VpnStatus.toButtonState(): VpnButtonState =
  when (this) {
    VpnStatus.Connected -> VpnButtonState.Connected
    VpnStatus.Connecting -> VpnButtonState.Connecting
    VpnStatus.Disconnected -> VpnButtonState.Disconnected
    VpnStatus.Disconnecting -> VpnButtonState.Disconnecting
  }

@Preview
@Composable
fun DashboardScreenPreview() {
  BasedVPNTheme {
    DashboardScreenStateless(
      state = State(
        selectedCity = SelectedCity(
          id = 0,
          name = "Buenos Aires",
          countryId = 0,
          countryName = "Argentina",
          countryFlag = CountryFlag.AR,
        ),
        ipAddress = "91.208.132.23",
      ),
      mapViewportState = rememberMapViewportState(),
      onConnectClick = {},
      onSelectServerClick = {},
      onSettingsClick = {},
      onTryAgainClick = {},
      onUpdateClick = {},
      onAlertConfirmClick = {},
      onAlertDismissRequest = {},
    )
  }
}
