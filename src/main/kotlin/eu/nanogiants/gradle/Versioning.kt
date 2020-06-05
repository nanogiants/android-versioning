/*
 * Created by NanoGiants GmbH on 06-02-2020.
 * Copyright © 2020 NanoGiants GmbH. All rights reserved.
 */

package eu.nanogiants.gradle

import eu.nanogiants.gradle.ext.getOutput

object Versioning {

  internal fun getVersionCode() = getCommitCount().apply { println("VersionCode $this") }

  internal fun getVersionName(checkBranch: Boolean = false): String {
    return (if (checkBranch) getBranchNameOrTag() else getTag()).apply { println("VersionName $this") }
  }

  private fun getCommitCount(): Int = "git rev-list --count HEAD".getOutput().toInt()

  private fun getTag(): String {
    val revList = "git rev-list --tags --max-count=1".getOutput()
    return "git describe --tags $revList".getOutput()
  }

  private fun getBranch(): String = "git rev-parse --abbrev-ref HEAD".getOutput()

  private fun getBranchNameOrTag(): String {
    val branch = getBranch()
    return if (branch.startsWith("release/") || branch.startsWith("hotfix/")) {
      println("Generate versionName from branch $branch")
      branch.split("/")[1]
    } else {
      getTag()
    }
  }
}