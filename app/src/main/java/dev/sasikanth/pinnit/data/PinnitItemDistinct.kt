package dev.sasikanth.pinnit.data

data class PinnitItemDistinct(
    val title: String = "",
    val content: String = "",
    val messages: List<Message> = emptyList(),
    val packageName: String,
    val appLabel: String,
    val template: TemplateStyle = TemplateStyle.DefaultStyle
) {

  companion object {
    fun createFrom(pinnitItem: PinnitItem): PinnitItemDistinct =
        PinnitItemDistinct(
            pinnitItem.title,
            pinnitItem.content,
            pinnitItem.messages,
            pinnitItem.packageName,
            pinnitItem.appLabel,
            pinnitItem.template
        )
  }
}
