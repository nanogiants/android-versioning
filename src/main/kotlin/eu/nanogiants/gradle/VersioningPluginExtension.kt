/*
 * Created by NanoGiants GmbH on 06-02-2020.
 * Copyright © 2020 NanoGiants GmbH. All rights reserved.
 */

package eu.nanogiants.gradle

open class VersioningPluginExtension {

  fun getVersionCode() = Versioning.getVersionCode()

  @JvmOverloads
  fun getVersionName(checkBranch: Boolean = false) = Versioning.getVersionName(checkBranch)

  var excludeBuildTypes: String? = null
  var keepOriginalArtifacts: Boolean = false
}