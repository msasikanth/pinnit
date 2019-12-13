package dev.sasikanth.pinnit.pages.apps

import com.airbnb.epoxy.AsyncEpoxyController
import dev.sasikanth.pinnit.appItemLayout
import dev.sasikanth.pinnit.data.AppItem

class AppItemListener(val onClick: (appItem: AppItem) -> Unit) {
  fun click(appItem: AppItem) {
    onClick(appItem)
  }
}

class AppsEpoxyController(
    private val appItemListener: AppItemListener
) : AsyncEpoxyController() {

  var appsList: List<AppItem>? = emptyList()
    set(value) {
      field = value.orEmpty()
      requestDelayedModelBuild(0)
    }

  override fun buildModels() {
    appsList?.forEach { appItem ->
      appItemLayout {
        id(appItem.packageName.hashCode())
        appItem(appItem)
        appItemListener(appItemListener)
      }
    }
  }
}
