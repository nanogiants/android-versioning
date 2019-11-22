package eu.appcom.gradle

import com.android.build.gradle.api.ApplicationVariant
import eu.appcom.gradle.git.Versioning
import org.gradle.api.Project

open class VersioningExtension constructor(private val project: Project) {

  var baseName: String? = null

  private val versionCodeLazy by lazy {
    Versioning.getVersionCode()
  }
  private val versionNameLazy by lazy {
    Versioning.getVersionName()
  }

  fun getVersionCode(): Int = versionCodeLazy

  fun getVersionName(): String = if (project.hasProperty("customTag")) {
    project.properties["customTag"] as String
  } else {
    versionNameLazy
  }

  fun getApkName(variant: ApplicationVariant): String {
    val flavors = StringBuilder()

    variant.productFlavors.forEach {
      flavors.append("-${it.name}")
    }

    val apkFileName = "app$flavors-${getVersionName()}-${getVersionCode()}-${variant.buildType.name}.apk"

    baseName?.let {
      return apkFileName.replace("app", it)
    }
    return apkFileName
  }
}