/*
 * Created by NanoGiants GmbH on 06-07-2020.
 * Copyright © 2020 NanoGiants GmbH. All rights reserved.
 */

package de.nanogiants.gradle.ext

import org.gradle.api.Task
import java.io.File

internal fun Task.addRenameArtifactAction(
  oldOutput: String,
  newOutput: String,
  outputPath: String,
  keepOriginal: Boolean
) {
  println(newOutput)

  doLast {
    val newFile = File(outputPath + newOutput)
    val oldFile = File(outputPath + oldOutput)

    if (oldFile.exists()) {
      oldFile.copyTo(newFile, overwrite = true)
      if (newFile.exists()) {
        if (!keepOriginal) oldFile.delete()
        println("Created file://${newFile.absolutePath}")
      } else {
        logger.error("Creating $newOutput from $oldOutput failed.")
      }
    } else {
      if (newFile.exists()) {
        println("Cached output $oldOutput was already renamed. Set 'keepOriginalArtifacts' if you like to keep those files.")
      } else {
        logger.error("$oldOutput and $newOutput not found. Try a clean build.")
      }
    }
  }
}

internal fun Task.addRenameMappingAction(oldOutput: File, newOutput: String, keepOriginal: Boolean) {
  println(newOutput)

  doLast {
    val newFile = File(oldOutput.absolutePath.replaceAfterLast("/", newOutput))

    if (oldOutput.exists()) {
      oldOutput.copyTo(newFile, overwrite = true)
      if (newFile.exists()) {
        if (!keepOriginal) oldOutput.delete()
        println("Created $newOutput")
      } else {
        logger.error("Creating $newOutput from mapping.txt failed.")
      }
    } else {
      if (newFile.exists()) {
        println("Cached output mapping.txt was already renamed. Set 'keepOriginalArtifacts' if you like to keep those files.")
      } else {
        logger.error("mapping.txt and $newOutput not found. Try a clean build.")
      }
    }
  }
}

internal fun Task.addPrintOutputAction(filepath: String, filename: String) {
  println(filename)

  doLast {
    if (File(filepath).exists()) println("Created file://${filepath + filename}")
  }
}