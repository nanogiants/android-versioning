/*
 * Created by NanoGiants GmbH on 06-07-2020.
 * Copyright © 2020 NanoGiants GmbH. All rights reserved.
 */

package eu.nanogiants.gradle.ext

import org.gradle.api.Task
import java.io.File

internal fun Task.addRenameArtifactAction(oldOutput: String, newOutput: String, outputPath: String) {
  println(newOutput)

  doLast {
    val newFile = File(outputPath + newOutput)
    val success = File(outputPath + oldOutput).renameTo(newFile)
    if (success) {
      println("Created file://${newFile.absolutePath}")
    } else {
      logger.error("Renaming $oldOutput to $newOutput failed.")
    }
  }
}

internal fun Task.addRenameMappingAction(oldOutput: File, newOutput: String) {
  println(newOutput)

  doLast {
    val newFile = File(oldOutput.absolutePath.replaceAfterLast("/", newOutput))
    val success = oldOutput.renameTo(newFile)
    if (success) {
      println("Created file://${newFile.absolutePath}")
    } else {
      logger.error("Renaming mapping.txt to $newOutput failed.")
    }
  }
}