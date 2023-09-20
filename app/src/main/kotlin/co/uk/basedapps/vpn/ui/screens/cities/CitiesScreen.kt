package co.uk.basedapps.vpn.ui.screens.cities

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
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import co.uk.basedapps.vpn.R
import co.uk.basedapps.vpn.common.EffectHandler
import co.uk.basedapps.vpn.common.Status
import co.uk.basedapps.vpn.network.model.City
import co.uk.basedapps.vpn.ui.screens.cities.CitiesScreenEffect as Effect
import co.uk.basedapps.vpn.ui.screens.cities.CitiesScreenState as State
import co.uk.basedapps.vpn.ui.theme.BasedAppColor
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
    containerColor = BasedAppColor.Background,
    topBar = {
      TopBar(
        title = stringResource(R.string.cities_title),
        navigateBack = navigateBack,
        isAccented = true,
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
  LazyVerticalGrid(
    columns = GridCells.Fixed(2),
    verticalArrangement = Arrangement.spacedBy(12.dp),
    horizontalArrangement = Arrangement.spacedBy(12.dp),
    contentPadding = PaddingValues(16.dp),
    modifier = Modifier.fillMaxSize(),
  ) {
    items(state.cities) { country ->
      CityRow(country, onItemClick)
    }
  }
}

@Composable
private fun CityRow(
  city: City,
  onItemClick: (City) -> Unit,
) {
  Column(
    modifier = Modifier
      .clip(RoundedCornerShape(8.dp))
      .clickable(onClick = { onItemClick(city) })
      .border(
        width = 1.dp,
        color = BasedAppColor.Divider,
        shape = RoundedCornerShape(8.dp),
      )
      .padding(16.dp),
  ) {
    Text(
      text = city.name,
      color = BasedAppColor.TextPrimary,
      overflow = TextOverflow.Ellipsis,
      maxLines = 1,
      fontSize = 18.sp,
    )
    Spacer(modifier = Modifier.size(2.dp))
    Text(
      text = stringResource(R.string.cities_servers_number, city.serversAvailable),
      color = BasedAppColor.TextSecondary,
      overflow = TextOverflow.Ellipsis,
      maxLines = 1,
      fontSize = 12.sp,
    )
  }
}
