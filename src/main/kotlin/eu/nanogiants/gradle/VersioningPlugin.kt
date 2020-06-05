/*
 * Created by NanoGiants GmbH on 06-02-2020.
 * Copyright © 2020 NanoGiants GmbH. All rights reserved.
 */

package eu.nanogiants.gradle

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import com.android.build.gradle.internal.dsl.DefaultConfig
import eu.nanogiants.gradle.ext.listContains
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.UnknownDomainObjectException
import org.gradle.api.plugins.BasePluginConvention
import java.io.File
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

    project.tasks.all { task ->
      if (task.name.matches(bundleRegex)) {
        val variantName = task.name.substringAfter("bundle").decapitalize(Locale.ROOT)

        appExtension.applicationVariants.all { variant ->
          if (variant.name == variantName && !extension.excludeBuildTypes.listContains(variant.buildType.name)) {
            val artifactBaseName = project.convention.findPlugin(BasePluginConvention::class.java)?.archivesBaseName
                ?: project.name
            val bundleName = "$artifactBaseName-${variant.baseName}.aab"
            val bundleOutputPath = "${project.buildDir.absolutePath}/outputs/bundle/$variantName/"
            val newOutputName = getOutputName(artifactBaseName, variant, appExtension.defaultConfig, "aab")
            println(newOutputName)

            val renameAabTask =
                project.tasks.register("versioningPluginRename${variant.name.capitalize(Locale.ROOT)}Aab") {
                  it.doLast {
                    val success = File(bundleOutputPath + bundleName).renameTo(File(bundleOutputPath + newOutputName))
                    if (success) {
                      println("Renamed $bundleName to $newOutputName")
                    } else {
                      project.logger.error("Renaming $bundleName to $newOutputName failed.")
                    }
                  }
                }
            task.finalizedBy(renameAabTask)
          }
        }
      } else if (task.name.matches(assembleRegex)) {
        val variantName = task.name.substringAfter("assemble").decapitalize(Locale.ROOT)

        appExtension.applicationVariants.all { variant ->
          if (variant.name == variantName && !extension.excludeBuildTypes.listContains(variant.buildType.name)) {
            val artifactBaseName = project.convention.findPlugin(BasePluginConvention::class.java)?.archivesBaseName
                ?: project.name
            val newOutputName = getOutputName(artifactBaseName, variant, appExtension.defaultConfig, "apk")
            println(newOutputName)
            variant.outputs.all {
              (it as BaseVariantOutputImpl).outputFileName = newOutputName
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
      extension: String
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
      append(".")
      append(extension)
    }.toString()
  }
}