/*
 * Created by NanoGiants GmbH on 06-02-2020.
 * Copyright © 2020 NanoGiants GmbH. All rights reserved.
 */

package de.nanogiants.gradle

import com.android.build.gradle.AppExtension
import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import de.nanogiants.gradle.ext.addPrintOutputAction
import de.nanogiants.gradle.ext.addRenameArtifactAction
import de.nanogiants.gradle.ext.addRenameMappingAction
import de.nanogiants.gradle.ext.generateOutputName
import de.nanogiants.gradle.ext.getAPKPath
import de.nanogiants.gradle.ext.getBundlePath
import de.nanogiants.gradle.ext.listContains
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.BasePluginConvention
import java.util.Locale

@ExperimentalStdlibApi
class VersioningPlugin : Plugin<Project> {

  private val bundleRegex = "^(:.*:)*bundle.*".toRegex()
  private val assembleRegex = "^(:.*:)*assemble.*".toRegex()

  override fun apply(project: Project) {
    with(project) {
      val ext = extensions.create("versioning", VersioningPluginExtension::class.java)

      tasks.register("printVersions") {
        it.group = "Versioning"
        it.description = "Prints the Android version information."
        it.doLast {
          ext.getVersionName()
          ext.getVersionCode()
        }
      }

      pluginManager.withPlugin("com.android.application") {
        val appExt = extensions.getByType(AppExtension::class.java)

        appExt.applicationVariants.configureEach { variant ->
          if (!ext.excludeBuildTypes.listContains(variant.buildType.name)) {
            val baseName = convention.findPlugin(BasePluginConvention::class.java)?.archivesBaseName ?: name
            variant.outputs.configureEach {
              val newApkName = variant.generateOutputName(baseName, "apk")
              (it as BaseVariantOutputImpl).outputFileName = newApkName
            }
          }
        }

        afterEvaluate {
          val baseName = convention.findPlugin(BasePluginConvention::class.java)?.archivesBaseName ?: name
          tasks.configureEach { task ->
            if (task.name.matches(bundleRegex)) {
              val variantName = task.name.substringAfter("bundle").decapitalize(Locale.ROOT)

              appExt.applicationVariants.configureEach { variant ->
                if (variant.name == variantName && !ext.excludeBuildTypes.listContains(variant.buildType.name)) {
                  val bundleName = "$baseName-${variant.baseName}.aab"
                  val newBundleName = variant.generateOutputName(baseName, "aab")
                  val bundleOutputPath = variant.getBundlePath(buildDir)

                  task.addRenameArtifactAction(bundleName, newBundleName, bundleOutputPath, ext.keepOriginalArtifacts)

                  if (variant.buildType.isMinifyEnabled) {
                    variant.mappingFileProvider.orNull?.firstOrNull()?.let {
                      val newMappingName = variant.generateOutputName(baseName, "txt")
                      task.addRenameMappingAction(it, newMappingName, ext.keepOriginalArtifacts)
                    }
                  }
                }
              }
            } else if (task.name.matches(assembleRegex)) {
              val variantName = task.name.substringAfter("assemble").decapitalize(Locale.ROOT)

              appExt.applicationVariants.configureEach { variant ->
                if (variant.name == variantName && !ext.excludeBuildTypes.listContains(variant.buildType.name)) {
                  variant.outputs.configureEach {
                    val apkName = (it as BaseVariantOutputImpl).outputFileName
                    val apkOutputPath = variant.getAPKPath(buildDir)
                    task.addPrintOutputAction(apkOutputPath, apkName)
                  }

                  if (variant.buildType.isMinifyEnabled) {
                    variant.mappingFileProvider.orNull?.firstOrNull()?.let {
                      val newMappingName = variant.generateOutputName(baseName, "txt")
                      task.addRenameMappingAction(it, newMappingName, ext.keepOriginalArtifacts)
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}