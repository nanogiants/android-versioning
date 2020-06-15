/*
 * Created by NanoGiants GmbH on 06-02-2020.
 * Copyright © 2020 NanoGiants GmbH. All rights reserved.
 */

package de.nanogiants.gradle

import de.nanogiants.gradle.ext.runCommand
import org.gradle.api.GradleException

object Versioning {

  internal fun getVersionCode() = getCommitCount().apply { println("VersionCode $this") }

  internal fun getVersionName(checkBranch: Boolean): String {
    return (if (checkBranch) getBranchNameOrTag() else getTag()).apply { println("VersionName $this") }
  }

  private fun getCommitCount() = "git rev-list --count HEAD".runCommand().toIntOrNull()
    ?: throw GradleException("Error reading current commit count.")

  private fun getTag(): String {
    val revList = "git rev-list --tags --max-count=1".runCommand()
    val result = "git describe --tags $revList".runCommand()
    return if (result.isNotEmpty()) result else throw GradleException("Error reading current tag")
  }

  private fun getBranchNameOrTag(): String {
    val branch = "git rev-parse --abbrev-ref HEAD".runCommand()
    return if (branch.matches("^release/\\d+.*".toRegex()) || branch.matches("^hotfix/\\d+.*".toRegex())) {
      println("Generate versionName from branch $branch")
      branch.split("/")[1]
    } else {
      getTag()
    }
  }
}