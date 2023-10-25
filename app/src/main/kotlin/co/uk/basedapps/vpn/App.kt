package co.uk.basedapps.vpn

import android.app.Application
import co.uk.basedapps.domain_wireguard.core.init.WireguardInitializer
import com.v2ray.ang.V2RayInitializer
import dagger.hilt.android.HiltAndroidApp
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
    V2RayInitializer.init(this)
    wireguardInitializer.init()
  }
}
