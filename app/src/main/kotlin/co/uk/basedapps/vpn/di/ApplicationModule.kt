package co.uk.basedapps.vpn.di

import android.content.Context
import co.uk.basedapps.vpn.BuildConfig
import co.uk.basedapps.vpn.ad.AdManager
import co.uk.basedapps.vpn.common.provider.AppDetailsProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

  @Provides
  @Singleton
  fun provideAppDetailsProvider(): AppDetailsProvider =
    object : AppDetailsProvider {
      override fun getAppVersion() = BuildConfig.VERSION_NAME
      override fun getPackage() = "co.uk.basedapps.vpn"
      override fun getBaseUrl() = BuildConfig.API_URL
      override fun getBasedAppVersion(): Long = 1
      override fun getBasedApiVersion(): Long = 1
    }

  @Provides
  @Singleton
  fun provideAdManager(
    @ApplicationContext context: Context,
  ): AdManager = AdManager(
    context = context,
    isDebug = BuildConfig.DEBUG,
    interstitialAdId = "ca-app-pub-3116534589278613/4897546375",
  )
}
