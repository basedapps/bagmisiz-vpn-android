package co.uk.basedapps.vpn.ui.screens.dashboard.widget

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mapbox.maps.MapInitOptions
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.plugin.attribution.generated.AttributionSettings
import com.mapbox.maps.plugin.compass.generated.CompassSettings
import com.mapbox.maps.plugin.gestures.generated.GesturesSettings
import com.mapbox.maps.plugin.scalebar.generated.ScaleBarSettings

@OptIn(MapboxExperimental::class)
@Composable
fun MapboxConfiguredMap(
  mapViewportState: MapViewportState,
) {
  MapboxMap(
    mapViewportState = mapViewportState,
    mapInitOptionsFactory = { context ->
      MapInitOptions(
        context = context,
        styleUri = MapBoxStyleUrl,
      )
    },
    gesturesSettings = GesturesSettings {
      pitchEnabled = false
      scrollEnabled = false
      pinchToZoomEnabled = false
      rotateEnabled = false
    },
    attributionSettings = AttributionSettings { enabled = false },
    compassSettings = CompassSettings { enabled = false },
    scaleBarSettings = ScaleBarSettings { enabled = false },
    modifier = Modifier.fillMaxSize(),
  )
}

private const val MapBoxStyleUrl = "mapbox://styles/basedapps/cln90jstc00bg01p93ae4f4om"