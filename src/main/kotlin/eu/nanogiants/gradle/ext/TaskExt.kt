/*
 * Created by NanoGiants GmbH on 06-06-2020.
 * Copyright © 2020 NanoGiants GmbH. All rights reserved.
 */

package eu.nanogiants.gradle.ext

import com.android.build.gradle.api.ApplicationVariant
import org.gradle.api.Project
import org.gradle.api.Task
import java.io.File

internal fun Task.addRenameArtifactAction(project: Project, oldOutput: String, newOutput: String, outputPath: String) {
  println(newOutput)

  doLast {
    val newFile = File(outputPath + newOutput)
    val success = File(outputPath + oldOutput).renameTo(newFile)
    if (success) {
      println("Created file://${newFile.absolutePath}")
    } else {
      project.logger.error("Renaming $oldOutput to $newOutput failed.")
    }
  }
}

internal fun Task.addRenameMappingAction(project: Project, variant: ApplicationVariant, newOutput: String) {
  if (variant.buildType.isMinifyEnabled) {
    variant.mappingFileProvider.orNull?.firstOrNull()?.let { mappingFile ->
      println(newOutput)

      doLast {
        val newFile = File(mappingFile.absolutePath.replaceAfterLast("/", newOutput))
        val success = mappingFile.renameTo(newFile)
        if (success) {
          println("Created file://${newFile.absolutePath}")
        } else {
          project.logger.error("Renaming mapping.txt to $newOutput failed.")
        }
      }
    }
  }
}