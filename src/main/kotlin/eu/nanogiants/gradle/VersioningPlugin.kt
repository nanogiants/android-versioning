/*
 * Created by NanoGiants GmbH on 06-02-2020.
 * Copyright © 2020 NanoGiants GmbH. All rights reserved.
 */

package eu.nanogiants.gradle

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import com.android.build.gradle.internal.dsl.DefaultConfig
import eu.nanogiants.gradle.ext.addRenameArtifactAction
import eu.nanogiants.gradle.ext.addRenameMappingAction
import eu.nanogiants.gradle.ext.listContains
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.UnknownDomainObjectException
import org.gradle.api.plugins.BasePluginConvention
import java.util.Locale

@ExperimentalStdlibApi
class VersioningPlugin : Plugin<Project> {

  private val bundleRegex = "^(:.*:)*bundle.*".toRegex()
  private val assembleRegex = "^(:.*:)*assemble.*".toRegex()

  override fun apply(project: Project) {
    val appExtension: AppExtension
    try {
      appExtension = project.extensions.getByType(AppExtension::class.java)
    } catch (e: UnknownDomainObjectException) {
      throw GradleException("Project '${project.name}' is not an Android module. Can't access 'android' extension.")
    }

    val extension = project.extensions.create("versioning", VersioningPluginExtension::class.java)

    project.tasks.register("printVersions") {
      it.group = "Versioning"
      it.description = "Prints the Android version information."
      it.doLast {
        extension.getVersionName()
        extension.getVersionCode()
      }
    }

    project.afterEvaluate {
      val baseName = project.convention.findPlugin(BasePluginConvention::class.java)?.archivesBaseName ?: project.name

      project.tasks.configureEach { task ->
        if (task.name.matches(bundleRegex)) {
          val variantName = task.name.substringAfter("bundle").decapitalize(Locale.ROOT)

          appExtension.applicationVariants.configureEach { variant ->
            if (variant.name == variantName && !extension.excludeBuildTypes.listContains(variant.buildType.name)) {
              val bundleName = "$baseName-${variant.baseName}.aab"
              val newBundleName = getOutputName(baseName, variant, appExtension.defaultConfig, "aab")
              val bundleOutputPath = "${project.buildDir.absolutePath}/outputs/bundle/$variantName/"

              task.addRenameArtifactAction(project, bundleName, newBundleName, bundleOutputPath)

              val newMappingName = getOutputName(baseName, variant, appExtension.defaultConfig, "txt", "aab")
              task.addRenameMappingAction(project, variant, newMappingName)
            }
          }
        } else if (task.name.matches(assembleRegex)) {
          val variantName = task.name.substringAfter("assemble").decapitalize(Locale.ROOT)

          appExtension.applicationVariants.configureEach { variant ->
            if (variant.name == variantName && !extension.excludeBuildTypes.listContains(variant.buildType.name)) {
              val apkOutputPath = "${project.buildDir.absolutePath}/outputs/apk/$variantName/"

              variant.outputs.configureEach {
                val apkName = (it as BaseVariantOutputImpl).outputFileName
                val newApkName = getOutputName(baseName, variant, appExtension.defaultConfig, "apk")

                task.addRenameArtifactAction(project, apkName, newApkName, apkOutputPath)
              }

              val newMappingName = getOutputName(baseName, variant, appExtension.defaultConfig, "txt", "apk")
              task.addRenameMappingAction(project, variant, newMappingName)
            }
          }
        }
      }
    }
  }

  private fun getOutputName(
      artifactBaseName: String,
      variant: ApplicationVariant,
      defaultConfig: DefaultConfig,
      extension: String,
      suffix: String = ""
  ): String {
    return StringBuilder().apply {
      append(artifactBaseName)
      variant.productFlavors.forEach {
        append("-")
        append(it.name)
      }
      append("-")
      append(defaultConfig.versionName)
      append("-")
      append(defaultConfig.versionCode.toString())
      append("-")
      append(variant.buildType.name)
      if (suffix.isNotEmpty()) {
        append("-")
        append(suffix)
      }
      if (extension == "apk" && !variant.isSigningReady) {
        append("-unsigned.apk")
      } else if (extension == "txt") {
        append("-mapping.txt")
      } else {
        append(".")
        append(extension)
      }
    }.toString()
  }
}