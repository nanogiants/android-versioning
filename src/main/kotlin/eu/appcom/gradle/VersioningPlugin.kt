package eu.appcom.gradle

import com.android.build.gradle.*
import org.gradle.api.Plugin
import org.gradle.api.Project

class VersioningPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        println("apply versioning plugin")

        target.extensions.create("versioning", VersioningExtension::class.java, target)

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

        target.plugins.all {
            when (it) {
                is FeaturePlugin -> {
                    // add tasks
                }
                is LibraryPlugin -> {
                    // add tasks
                }
                is AppPlugin -> {
                    // add only if application
                    target.tasks.create("printApkNames").apply {
                        val names = mutableListOf<String>()
                        target.android.app.applicationVariants.all { variant ->
                            names.add(target.versioning().getApkName(variant))
                        }
                        doLast {
                            println("generated apk names:")
                            names.forEach { apkName ->
                                println(apkName)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun Project.versioning(): VersioningExtension = this.extensions.getByType(VersioningExtension::class.java)

    /**
     * Access the `android` extension of this project. If the project is not an
     * Android app or library module, this method will throw.
     */
    private val Project.android: BaseExtension
        get() = extensions.findByName("android") as? BaseExtension
                ?: error("Project '$name' is not an Android module. Can't " +
                        "access 'android' extension.")

    /**
     * Accesses the app module-specific extensions of an Android module.
     */
    private val BaseExtension.app: AppExtension
        get() = this as? AppExtension ?: error("Android module is not an app module. Can't " +
                "access 'android' app extension.")
}