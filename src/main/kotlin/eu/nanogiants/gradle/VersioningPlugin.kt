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
              val newBundleName = variant.generateOutputName(baseName, "aab")
              val bundleOutputPath = "${project.buildDir.absolutePath}/outputs/bundle/$variantName/"

              task.addRenameArtifactAction(bundleName, newBundleName, bundleOutputPath)

              val newMappingName = variant.generateOutputName(baseName, "txt", "aab")
              task.addRenameMappingAction(variant, newMappingName)
            }
          }
        } else if (task.name.matches(assembleRegex)) {
          val variantName = task.name.substringAfter("assemble").decapitalize(Locale.ROOT)

          appExtension.applicationVariants.configureEach { variant ->
            if (variant.name == variantName && !extension.excludeBuildTypes.listContains(variant.buildType.name)) {
              val apkOutputPath = "${project.buildDir.absolutePath}/outputs/apk/$variantName/"

              variant.outputs.configureEach {
                val apkName = (it as BaseVariantOutputImpl).outputFileName
                val newApkName = variant.generateOutputName(baseName, "apk")

                task.addRenameArtifactAction(apkName, newApkName, apkOutputPath)
              }

              val newMappingName = variant.generateOutputName(baseName, "txt", "apk")
              task.addRenameMappingAction(variant, newMappingName)
            }
          }
        }
      }
    }
  }
}