/*
 * Created by NanoGiants GmbH on 06-07-2020.
 * Copyright © 2020 NanoGiants GmbH. All rights reserved.
 */

package eu.nanogiants.gradle.ext

import org.gradle.api.GradleException
import java.util.Locale
import java.util.concurrent.TimeUnit

internal fun String.runCommand(): String {
  return try {
    ProcessBuilder(this.split("\\s".toRegex())).start().run {
      waitFor(10, TimeUnit.SECONDS)
      inputStream.bufferedReader().use { it.readText().trim() }
    }
  } catch (e: Exception) {
    throw GradleException(e.message ?: "Error executing command :$this")
  }
}

internal fun String?.listContains(element: String) =
  this != null && this.split(",").map { it.toLowerCase(Locale.ROOT).trim() }.contains(element.toLowerCase(Locale.ROOT))