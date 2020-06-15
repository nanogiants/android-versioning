/*
 * Created by NanoGiants GmbH on 06-02-2020.
 * Copyright © 2020 NanoGiants GmbH. All rights reserved.
 */

package de.nanogiants.gradle

open class VersioningPluginExtension {

  private val lazyVersionCode by lazy { Versioning.getVersionCode() }
  private val lazyVersionName by lazy { Versioning.getVersionName(false) }
  private val lazyVersionNameCheckBranch by lazy { Versioning.getVersionName(true) }

  fun getVersionCode() = lazyVersionCode

  @JvmOverloads
  fun getVersionName(checkBranch: Boolean = false) = if (checkBranch) lazyVersionNameCheckBranch else lazyVersionName

  var excludeBuildTypes: String? = null
  var keepOriginalArtifacts: Boolean = false
}