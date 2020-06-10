/*
 * Created by NanoGiants GmbH on 06-07-2020.
 * Copyright © 2020 NanoGiants GmbH. All rights reserved.
 */

package eu.nanogiants.gradle.ext

import com.android.build.gradle.api.ApplicationVariant

internal fun ApplicationVariant.generateOutputName(baseName: String, extension: String): String {
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
    if (extension == "apk" && !isSigningReady) {
      append("-unsigned")
    } else if (extension == "txt") {
      append("-mapping")
    }
    append(".")
    append(extension)
  }.toString()
}