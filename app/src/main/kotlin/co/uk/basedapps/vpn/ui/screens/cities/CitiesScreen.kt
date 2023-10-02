package co.uk.basedapps.vpn.ui.screens.cities

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import co.uk.basedapps.vpn.R
import co.uk.basedapps.vpn.common.compose.EffectHandler
import co.uk.basedapps.vpn.common.state.Status
import co.uk.basedapps.vpn.network.model.City
import co.uk.basedapps.vpn.ui.screens.cities.CitiesScreenEffect as Effect
import co.uk.basedapps.vpn.ui.screens.cities.CitiesScreenState as State
import co.uk.basedapps.vpn.ui.widget.BaseRow
import co.uk.basedapps.vpn.ui.widget.ErrorScreen
import co.uk.basedapps.vpn.ui.widget.TopBar

@Composable
fun CitiesScreen(
  countryId: Int?,
  navigateBack: () -> Unit,
  navigateBackToRoot: () -> Unit,
) {
  val viewModel = hiltViewModel<CitiesScreenViewModel>()
  val state by viewModel.stateHolder.state.collectAsState()

  LaunchedEffect(countryId) {
    viewModel.setCountryId(countryId)
  }

  EffectHandler(viewModel.stateHolder.effects) { effect ->
    when (effect) {
      is Effect.GoBackToRoot -> navigateBackToRoot()
    }
  }

  CitiesScreenStateless(
    state = state,
    navigateBack = navigateBack,
    onItemClick = viewModel::onCityClick,
    onTryAgainClick = viewModel::onTryAgainClick,
  )
}

@Composable
fun CitiesScreenStateless(
  state: State,
  navigateBack: () -> Unit,
  onItemClick: (City) -> Unit,
  onTryAgainClick: () -> Unit,
) {
  Scaffold(
    containerColor = Color.Transparent,
    topBar = {
      TopBar(
        title = stringResource(R.string.cities_title),
        navigateBack = navigateBack,
      )
    },
    content = { paddingValues ->
      Content(
        paddingValues = paddingValues,
        state = state,
        onItemClick = onItemClick,
        onTryAgainClick = onTryAgainClick,
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
private fun Content(
  paddingValues: PaddingValues,
  state: State,
  onItemClick: (City) -> Unit,
  onTryAgainClick: () -> Unit,
) {
  Box(
    modifier = Modifier
      .padding(paddingValues)
      .fillMaxSize(),
  ) {
    when (state.status) {
      is Status.Loading -> Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize(),
      ) {
        CircularProgressIndicator()
      }

      is Status.Error -> ErrorScreen(
        isLoading = state.status.isLoading,
        onButtonClick = onTryAgainClick,
      )

      is Status.Data -> Data(state, onItemClick)
    }
  }
}

@Composable
private fun Data(
  state: State,
  onItemClick: (City) -> Unit,
) {
  LazyColumn(
    verticalArrangement = Arrangement.spacedBy(10.dp),
    contentPadding = PaddingValues(
      horizontal = 32.dp,
      vertical = 16.dp,
    ),
    modifier = Modifier.fillMaxSize(),
  ) {
    items(state.cities) { city ->
      BaseRow(
        title = city.name,
        subtitle = stringResource(R.string.cities_servers_number, city.serversAvailable),
        iconRes = R.drawable.ic_server,
        onClick = { onItemClick(city) },
      )
    }
  }
}
