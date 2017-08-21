package eu.appcom.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class VersioningPlugin implements Plugin<Project> {
    Project project

    @Override
    void apply(Project project) {

        this.project = project
        String artifactName = project.archivesBaseName

        project.android.defaultConfig.versionName = gitTag()
        project.android.defaultConfig.versionCode = gitCommitCount()
        project.archivesBaseName = generateArtifactName(artifactName)

        def printVersionInfo = project.tasks.create("printVersionInfo") {
            doLast {
                println "Version Name: " + gitTag()
                println "Version Code: " + gitCommitCount()
                println "Artifact name: " + project.archivesBaseName
            }
        }
        printVersionInfo.group = "appcom"
        printVersionInfo.description = "Prints the android version information"
    }

    def generateArtifactName(String basename) {
        return basename + getArtifactVersion()
    }

    /**
     * Provides the latest git tag.
     * @return the git tag
     */
    def gitTag() {
        def revlist = 'git rev-list --tags --max-count=1'.execute([], project.rootDir).text.trim()
        def versionName = ('git describe --tags ' + revlist.toString()).execute([], project.rootDir).text.trim()
        if (versionName != null) {
            return versionName
        } else {
            return "NO_VERSION"
        }
    }

    /**
     * Provides the number of git commits of the current branch since the latest tag.
     * @return the number of git commits
     */
    def gitCommitCountSinceLastTag() {
        def revlist = 'git rev-list --tags --max-count=1'.execute([], project.rootDir).text.trim()
        def versionName = ('git describe --tags ' + revlist.toString()).execute([], project.rootDir).text.trim()
        if (versionName != null) {
            def gitCommitCountSinceLastTag = ('git rev-list ' + versionName.toString() + '.. --count').execute([],
                    project.rootDir).text.trim().toInteger()
            return gitCommitCountSinceLastTag
        } else {
            return 0
        }
    }

    /**
     * Provides the number of git commits of the current branch
     * @return the number of git commits
     */
    def gitCommitCount() {
        return 'git rev-list --count HEAD'.execute([], project.rootDir).text.trim().toInteger()
    }

    /**
     * Generates the version name of the app artifact from the git tag and commit counter.
     * @return the apk version
     */
    def getArtifactVersion() {
        return "-" + gitTag() + "." + gitCommitCountSinceLastTag()
    }
}


