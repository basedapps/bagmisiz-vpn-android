package co.uk.basedapps.vpn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.LaunchedEffect
import androidx.core.view.WindowCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import co.uk.basedapps.domain_wireguard.core.init.WireguardInitializer
import co.uk.basedapps.vpn.storage.BasedStorage
import co.uk.basedapps.vpn.ui.screens.cities.CitiesScreen
import co.uk.basedapps.vpn.ui.screens.countries.CountriesScreen
import co.uk.basedapps.vpn.ui.screens.dashboard.DashboardScreen
import co.uk.basedapps.vpn.ui.screens.intro.IntroScreen
import co.uk.basedapps.vpn.ui.screens.settings.SettingsScreen
import co.uk.basedapps.vpn.ui.theme.BasedVPNTheme
import dagger.hilt.android.AndroidEntryPoint
import dev.dev7.lib.v2ray.V2rayController
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

  @Inject
  lateinit var wireguardInitializer: WireguardInitializer

  @Inject
  lateinit var basedStorage: BasedStorage

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setupVPN()
    setFullScreen()
    setContent {
      BasedVPNTheme {
        val navController = rememberNavController()
        NavHost(
          navController = navController,
          startDestination = when {
            basedStorage.isOnboardingShown().not() -> Destination.Intro
            else -> Destination.Dashboard
          },
          enterTransition = { EnterTransition.None },
          exitTransition = { ExitTransition.None },
        ) {
          composable(Destination.Intro) {
            LaunchedEffect(Unit) {
              basedStorage.onOnboardingShown()
            }
            IntroScreen(
              navigateToDashboard = {
                navController.popBackStack()
                navController.navigate(Destination.Dashboard)
              },
            )
          }
          composable(Destination.Dashboard) {
            DashboardScreen(
              navigateToCountries = { navController.navigate(Destination.Countries) },
              navigateToSettings = { navController.navigate(Destination.Settings) },
            )
          }
          composable(Destination.Countries) {
            CountriesScreen(
              navigateBack = { navController.popBackStack() },
              navigateToCities = { country ->
                navController.navigate("countries/${country.id}/cities")
              },
            )
          }
          composable(
            route = Destination.Cities,
            arguments = listOf(navArgument(Args.CountryId) { type = NavType.IntType }),
          ) { backStackEntry ->
            CitiesScreen(
              countryId = backStackEntry.arguments?.getInt(Args.CountryId),
              navigateBack = { navController.popBackStack() },
              navigateBackToRoot = {
                navController.popBackStack(
                  route = Destination.Dashboard,
                  inclusive = false,
                )
              },
            )
          }
          composable(Destination.Settings) {
            SettingsScreen(
              navigateBack = { navController.popBackStack() },
            )
          }
        }
      }
    }
  }

  private fun setupVPN() {
    V2rayController.init(applicationContext, R.drawable.ic_settings, getString(R.string.app_name))
    wireguardInitializer.init()
  }
}

fun ComponentActivity.setFullScreen() {
  WindowCompat.setDecorFitsSystemWindows(window, false)
}

object Destination {
  const val Intro = "intro"
  const val Dashboard = "dashboard"
  const val Countries = "countries"
  const val Cities = "countries/{${Args.CountryId}}/cities"
  const val Settings = "settings"
}

object Args {
  const val CountryId = "countryId"
}
