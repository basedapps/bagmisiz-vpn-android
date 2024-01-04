package co.uk.basedapps.vpn.ad

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlin.concurrent.Volatile
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber

class AdManager(
  private val context: Context,
  private val isDebug: Boolean,
  private val interstitialAdId: String,
) {

  @Volatile
  private var interstitialAd: InterstitialAd? = null
  private val mutex = Mutex()

  fun initialize(applicationContext: Context) {
    MobileAds.initialize(applicationContext)
  }

  suspend fun preloadInterstitialAd() {
    interstitialAd = loadInterstitialAd()
    Timber.d("InterstitialAd preloaded")
  }

  suspend fun showInterstitialAd(activity: Activity): Boolean {
    if (interstitialAd == null) {
      preloadInterstitialAd()
    }
    return suspendCoroutine { continuation ->
      if (interstitialAd != null) {
        interstitialAd?.fullScreenContentCallback =
          object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
              interstitialAd = null
              Timber.d("onAdDismissedFullScreenContent")
              continuation.resume(true)
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
              interstitialAd = null
              Timber.d("onAdFailedToShowFullScreenContent")
              continuation.resume(false)
            }
          }
        interstitialAd?.show(activity)
      } else {
        Timber.d("InterstitialAd is null")
        continuation.resume(false)
      }
    }
  }

  private suspend fun loadInterstitialAd(): InterstitialAd? = mutex.withLock {
    if (interstitialAd != null) {
      return interstitialAd
    }
    return suspendCoroutine { continuation ->
      val adId = when (isDebug) {
        true -> "ca-app-pub-3940256099942544/1033173712" // debug Ad
        false -> interstitialAdId
      }
      InterstitialAd.load(
        context,
        adId,
        AdRequest.Builder().build(),
        object : InterstitialAdLoadCallback() {
          override fun onAdLoaded(interstitial: InterstitialAd) {
            Timber.d("onAdLoaded")
            continuation.resume(interstitial)
          }

          override fun onAdFailedToLoad(error: LoadAdError) {
            Timber.e(error.toString())
            continuation.resume(null)
          }
        },
      )
    }
  }
}
