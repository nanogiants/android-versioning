package eu.nanogiants.gradle.ext

import com.android.build.gradle.api.ApplicationVariant

internal fun ApplicationVariant.generateOutputName(baseName: String, extension: String, suffix: String = ""): String {
  return StringBuilder().apply {
    append(baseName)
    productFlavors.forEach {
      append("-")
      append(it.name)
    }
    append("-")
    append(versionName)
    append("-")
    append(versionCode.toString())
    append("-")
    append(buildType.name)
    if (suffix.isNotEmpty()) {
      append("-")
      append(suffix)
    }
    if (extension == "apk" && !isSigningReady) {
      append("-unsigned")
    } else if (extension == "txt") {
      append("-mapping")
    }
    append(".")
    append(extension)
  }.toString()
}