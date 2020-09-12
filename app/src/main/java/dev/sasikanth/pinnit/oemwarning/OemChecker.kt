package dev.sasikanth.pinnit.oemwarning

private const val BRAND_XIAOMI = "xiaomi"
private const val BRAND_REDMI = "redmi"
private const val BRAND_HONOR = "honor"
private const val BRAND_HUAWEI = "huawei"
private const val BRAND_OPPO = "oppo"
private const val BRAND_VIVO = "vivo"
private const val BRAND_ONEPLUS = "oneplus"
private const val BRAND_MEIZU = "meizu"

private val warningOems = arrayOf(
  BRAND_XIAOMI,
  BRAND_REDMI,
  BRAND_HONOR,
  BRAND_HUAWEI,
  BRAND_OPPO,
  BRAND_VIVO,
  BRAND_ONEPLUS,
  BRAND_MEIZU
)

fun shouldShowWarningForOEM(oemName: String): Boolean {
  return warningOems.contains(oemName)
}
