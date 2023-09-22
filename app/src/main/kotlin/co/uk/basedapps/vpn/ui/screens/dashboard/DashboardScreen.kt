package co.uk.basedapps.vpn.ui.screens.dashboard

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import co.uk.basedapps.domain_wireguard.core.init.getVpnPermissionRequest
import co.uk.basedapps.vpn.R
import co.uk.basedapps.vpn.common.compose.EffectHandler
import co.uk.basedapps.vpn.common.state.Status
import co.uk.basedapps.vpn.common.compose.TopBarIconsColorEffect
import co.uk.basedapps.vpn.common.flags.CountryFlag
import co.uk.basedapps.vpn.storage.SelectedCity
import co.uk.basedapps.vpn.ui.screens.dashboard.DashboardScreenEffect as Effect
import co.uk.basedapps.vpn.ui.screens.dashboard.DashboardScreenState as State
import co.uk.basedapps.vpn.ui.theme.BasedAppColor
import co.uk.basedapps.vpn.ui.theme.BasedVPNTheme
import co.uk.basedapps.vpn.ui.widget.BasedAlertDialog
import co.uk.basedapps.vpn.ui.widget.BasedButton
import co.uk.basedapps.vpn.ui.widget.ButtonStyle
import co.uk.basedapps.vpn.ui.widget.ErrorScreen
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun DashboardScreen(
  navigateToCountries: () -> Unit,
  navigateToSettings: () -> Unit,
) {
  val viewModel = hiltViewModel<DashboardScreenViewModel>()
  val state by viewModel.stateHolder.state.collectAsState()

  val context = LocalContext.current

  val scope = rememberCoroutineScope()
  val mapPositionState = rememberCameraPositionState()

  val vpnPermissionRequest = rememberLauncherForActivityResult(
    ActivityResultContracts.StartActivityForResult(),
  ) { result ->
    viewModel.onPermissionsResult(result.resultCode == Activity.RESULT_OK)
  }

  TopBarIconsColorEffect(isDark = true)

  EffectHandler(viewModel.stateHolder.effects) { effect ->
    when (effect) {
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

      is Effect.ChangeMapPosition -> {
        scope.launch(Dispatchers.Main) {
          mapPositionState.animate(
            update = CameraUpdateFactory.newCameraPosition(
              CameraPosition.fromLatLngZoom(
                LatLng(effect.latitude, effect.longitude),
                10f,
              ),
            ),
            durationMs = 2000,
          )
        }
      }
    }
  }

  DashboardScreenStateless(
    state = state,
    mapPositionState = mapPositionState,
    onConnectClick = viewModel::onConnectClick,
    onSelectServerClick = viewModel::onSelectServerClick,
    onSettingsClick = viewModel::onSettingsClick,
    onTryAgainClick = viewModel::onTryAgainClick,
    onAlertConfirmClick = viewModel::onAlertConfirmClick,
    onAlertDismissRequest = viewModel::onAlertDismissRequest,
  )
}

@Composable
fun DashboardScreenStateless(
  state: State,
  mapPositionState: CameraPositionState,
  onConnectClick: () -> Unit,
  onSelectServerClick: () -> Unit,
  onSettingsClick: () -> Unit,
  onTryAgainClick: () -> Unit,
  onAlertConfirmClick: () -> Unit,
  onAlertDismissRequest: () -> Unit,
) {
  when (state.status) {
    is Status.Error -> ErrorScreen(
      isLoading = state.status.isLoading,
      onButtonClick = onTryAgainClick,
    )

    else -> Content(
      state = state,
      mapPositionState = mapPositionState,
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
  mapPositionState: CameraPositionState,
  onConnectClick: () -> Unit,
  onSelectServerClick: () -> Unit,
  onSettingsClick: () -> Unit,
  onAlertConfirmClick: () -> Unit,
  onAlertDismissRequest: () -> Unit = {},
) {
  Box(
    modifier = Modifier
      .background(Color.White)
      .fillMaxSize(),
  ) {
    Column {
      TopBar(
        state = state,
        onSettingsClick = onSettingsClick,
      )
      Map(
        modifier = Modifier
          .weight(1f)
          .padding(horizontal = 16.dp)
          .clip(RoundedCornerShape(8.dp)),
        mapPositionState = mapPositionState,
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
      .background(Color.Black.copy(alpha = 0.3f))
      .fillMaxSize(),
  ) {
    CircularProgressIndicator(
      color = Color.White,
    )
  }
}

@Composable
private fun Map(
  modifier: Modifier = Modifier,
  mapPositionState: CameraPositionState,
) {
  val uiSettings by remember {
    mutableStateOf(
      MapUiSettings(
        myLocationButtonEnabled = false,
        zoomControlsEnabled = false,
        scrollGesturesEnabled = false,
        zoomGesturesEnabled = false,
        tiltGesturesEnabled = false,
        rotationGesturesEnabled = false,
      ),
    )
  }
  GoogleMap(
    uiSettings = uiSettings,
    cameraPositionState = mapPositionState,
    modifier = modifier,
  )
}

@Composable
private fun TopBar(
  state: State,
  onSettingsClick: () -> Unit,
) {
  val context = LocalContext.current
  Box {
    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier
        .statusBarsPadding()
        .padding(top = 8.dp, bottom = 24.dp)
        .padding(horizontal = 16.dp)
        .fillMaxWidth(),
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 50.dp),
      ) {
        val ipLabel = remember(state.ipAddress) {
          buildString {
            append(context.getString(R.string.dashboard_your_ip).uppercase())
            append(" • ")
            append(state.ipAddress)
          }
        }
        Text(
          text = ipLabel,
          color = when (state.isConnected) {
            true -> BasedAppColor.TextPrimary
            false -> BasedAppColor.Accent
          },
          fontSize = 12.sp,
          fontWeight = FontWeight.Medium,
        )
        val isConnectedLabel = remember(state.isConnected) {
          context.getString(
            when (state.isConnected) {
              true -> R.string.dashboard_connected_to_vpn
              false -> R.string.dashboard_disconnected_from_vpn
            },
          ).uppercase()
        }
        Text(
          text = isConnectedLabel,
          color = when (state.isConnected) {
            true -> BasedAppColor.OnLine
            false -> BasedAppColor.TextSecondary
          },
          fontSize = 16.sp,
          fontWeight = FontWeight.Medium,
          maxLines = 1,
        )
      }
      Button(
        colors = ButtonDefaults.buttonColors(
          containerColor = BasedAppColor.ButtonTertiary,
        ),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(0.dp),
        onClick = onSettingsClick,
        modifier = Modifier
          .size(44.dp)
          .align(Alignment.CenterEnd),
      ) {
        Icon(
          painter = painterResource(R.drawable.ic_settings),
          contentDescription = stringResource(R.string.dashboard_menu_settings),
          modifier = Modifier.size(24.dp),
          tint = BasedAppColor.ButtonTertiaryIcon,
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
  Box {
    Column(
      modifier = Modifier
        .padding(horizontal = 16.dp)
        .padding(top = 24.dp, bottom = 16.dp)
        .navigationBarsPadding(),
    ) {
      val selectedCity = state.selectedCity
      if (selectedCity != null) {
        SelectedCityRow(
          selectedCity = selectedCity,
          onClick = onSelectServerClick,
        )
        Spacer(modifier = Modifier.size(16.dp))
      }
      BasedButton(
        text = stringResource(
          when (state.isConnected) {
            true -> R.string.dashboard_disconnect_from_vpn
            false -> R.string.dashboard_connect_to_vpn
          },
        ),
        style = if (state.isConnected) ButtonStyle.Secondary else ButtonStyle.Primary,
        onClick = onConnectClick,
        iconRes = if (state.isConnected) null else R.drawable.ic_rocket,
        modifier = Modifier.fillMaxWidth(),
      )
    }
  }
}

@Composable
fun SelectedCityRow(
  selectedCity: SelectedCity,
  onClick: () -> Unit,
) {
  Row(
    modifier = Modifier
      .clip(RoundedCornerShape(8.dp))
      .clickable(onClick = onClick)
      .heightIn(min = 60.dp)
      .background(BasedAppColor.Elevation)
      .padding(16.dp)
      .fillMaxWidth(),
  ) {
    val flagRes = selectedCity.countryFlag?.res
    if (flagRes != null) {
      Image(
        painter = painterResource(flagRes),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
          .size(width = 36.dp, height = 24.dp)
          .clip(RoundedCornerShape(4.dp)),
      )
    } else {
      Box(
        modifier = Modifier
          .background(BasedAppColor.Divider)
          .size(width = 36.dp, height = 24.dp)
          .clip(RoundedCornerShape(4.dp)),
      )
    }
    Spacer(modifier = Modifier.size(16.dp))
    Text(
      text = buildAnnotatedString {
        withStyle(style = SpanStyle(BasedAppColor.TextPrimary)) {
          append(selectedCity.countryName)
        }
        withStyle(style = SpanStyle(BasedAppColor.TextSecondary)) {
          append(" • ")
          append(selectedCity.name)
        }
      },
      overflow = TextOverflow.Ellipsis,
      maxLines = 1,
      fontSize = 18.sp,
    )
  }
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
      mapPositionState = rememberCameraPositionState(),
      onConnectClick = {},
      onSelectServerClick = {},
      onSettingsClick = {},
      onTryAgainClick = {},
      onAlertConfirmClick = {},
      onAlertDismissRequest = {},
    )
  }
}
