package eu.appcom.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class VersioningPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        println("apply versioning plugin")

        target.extensions.create("Versioning", VersioningExtension::class.java, target)

        target.tasks.create("versioningGitVersionName").apply {
            doLast {
                println(target.versioning().getVersionName())
            }
        }

        target.tasks.create("versioningGitVersionCode").apply {
            doLast {
                println(target.versioning().getVersionCode())
            }
        }

        target.tasks.create("printVersions").apply {
            group = "appcom"
            description = "Prints the android version information"
            doLast {
                println("Version Name: ${target.versioning().getVersionName()}")
                println("Version Code: ${target.versioning().getVersionCode()}")
            }
        }
    }

    private fun Project.versioning(): VersioningExtension = this.extensions.getByType(VersioningExtension::class.java)

}