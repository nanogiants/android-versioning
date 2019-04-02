package eu.appcom.gradle

import com.android.build.gradle.api.ApplicationVariant
import eu.appcom.gradle.git.Versioning
import org.gradle.api.Project

open class VersioningExtension constructor(private val project: Project) {

    var baseName: String? = null

    fun getVersionCode(): Int = Versioning.getVersionCode()

    fun getVersionName(): String = if (project.hasProperty("customTag")) {
        project.properties["customTag"] as String
    } else {
        Versioning.getVersionName()
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

    fun getMappingName(): String {
        // TODO
        return ""
    }
}