package co.uk.basedapps.vpn

import android.app.Application
import co.uk.basedapps.domain_wireguard.core.init.WireguardInitializer
import dagger.hilt.android.HiltAndroidApp
import dev.dev7.lib.v2ray.V2rayController
import javax.inject.Inject
import timber.log.Timber

@HiltAndroidApp
class App : Application() {

  @Inject
  lateinit var wireguardInitializer: WireguardInitializer

  override fun onCreate() {
    super.onCreate()
    setupTimber()
    setupVPN()
  }

  private fun setupTimber() {
    if (BuildConfig.DEBUG) {
      Timber.plant(Timber.DebugTree())
    }
  }

  private fun setupVPN() {
    V2rayController.init(applicationContext, R.drawable.ic_settings, getString(R.string.app_name))
    wireguardInitializer.init()
  }
}
