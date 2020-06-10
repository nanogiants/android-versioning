/*
 * Created by NanoGiants GmbH on 06-02-2020.
 * Copyright © 2020 NanoGiants GmbH. All rights reserved.
 */

package eu.nanogiants.gradle

import com.android.build.gradle.AppExtension
import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import eu.nanogiants.gradle.ext.addRenameArtifactAction
import eu.nanogiants.gradle.ext.addRenameMappingAction
import eu.nanogiants.gradle.ext.generateOutputName
import eu.nanogiants.gradle.ext.listContains
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
      val extension = extensions.create("versioning", VersioningPluginExtension::class.java)

      tasks.register("printVersions") {
        it.group = "Versioning"
        it.description = "Prints the Android version information."
        it.doLast {
          extension.getVersionName()
          extension.getVersionCode()
        }
      }

      pluginManager.withPlugin("com.android.application") {
        val appExtension = extensions.getByType(AppExtension::class.java)
        val baseName = convention.findPlugin(BasePluginConvention::class.java)?.archivesBaseName ?: name

        tasks.configureEach { task ->
          if (task.name.matches(bundleRegex)) {
            val variantName = task.name.substringAfter("bundle").decapitalize(Locale.ROOT)

            appExtension.applicationVariants.configureEach { variant ->
              if (variant.name == variantName && !extension.excludeBuildTypes.listContains(variant.buildType.name)) {
                val bundleName = "$baseName-${variant.baseName}.aab"
                val newBundleName = variant.generateOutputName(baseName, "aab")
                val bundleOutputPath = "${buildDir.absolutePath}/outputs/bundle/$variantName/"

                task.addRenameArtifactAction(bundleName, newBundleName, bundleOutputPath)

                if (variant.buildType.isMinifyEnabled) {
                  variant.mappingFileProvider.orNull?.firstOrNull()?.let {
                    val newMappingName = variant.generateOutputName(baseName, "txt", "aab")
                    task.addRenameMappingAction(it, newMappingName)
                  }
                }
              }
            }
          } else if (task.name.matches(assembleRegex)) {
            val variantName = task.name.substringAfter("assemble").decapitalize(Locale.ROOT)

            appExtension.applicationVariants.configureEach { variant ->
              if (variant.name == variantName && !extension.excludeBuildTypes.listContains(variant.buildType.name)) {
                val apkOutputPath = "${buildDir.absolutePath}/outputs/apk/$variantName/"

                variant.outputs.configureEach {
                  val apkName = (it as BaseVariantOutputImpl).outputFileName
                  val newApkName = variant.generateOutputName(baseName, "apk")

                  task.addRenameArtifactAction(apkName, newApkName, apkOutputPath)
                }

                if (variant.buildType.isMinifyEnabled) {
                  variant.mappingFileProvider.orNull?.firstOrNull()?.let {
                    val newMappingName = variant.generateOutputName(baseName, "txt", "apk")
                    task.addRenameMappingAction(it, newMappingName)
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