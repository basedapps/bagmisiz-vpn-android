package co.uk.basedapps.vpn.common.ext

import android.content.Context
import android.content.Intent
import android.net.Uri

fun Context.openWeb(url: String) {
  val uri = url.parseUrl() ?: return
  val intent = Intent(Intent.ACTION_VIEW, uri)
  startActivity(intent)
}

private fun String.parseUrl(): Uri? = try {
  Uri.parse(this)
} catch (e: Throwable) {
  null
}
