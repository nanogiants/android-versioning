package eu.appcom.gradle

import eu.appcom.gradle.git.Versioning
import org.gradle.api.Project

open class VersioningExtension constructor(val project: Project) {

    var baseName: String? = null
    var prefix: String? = null

    fun getVersionCode(): Int = Versioning.getVersionCode()

    fun getVersionName(): String = if (project.hasProperty("customTag")) {
        project.properties["customTag"] as String
    } else {
        Versioning.getVersionName()
    }

    fun getApkName(): String = "app.apk"

//            if (target.plugins.hasPlugin('com.android.application')) {
//                String artifactName = target.archivesBaseName
//                        target.archivesBaseName = generateArtifactName(artifactName)
//            }

}