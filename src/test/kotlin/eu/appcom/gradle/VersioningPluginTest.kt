package eu.appcom.gradle

import junit.framework.Assert.assertTrue
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.revwalk.RevCommit
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testkit.runner.GradleRunner
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class VersioningPluginTest {

    companion object {

        val projectFolder: TemporaryFolder by lazy {
            val folder = TemporaryFolder()
            folder.create()
            folder.newFile("build.gradle")
            folder
        }

        private val project: Project by lazy {
            ProjectBuilder.builder()
                    .withProjectDir(projectFolder.root)
                    .build()
        }

        private val plugin: VersioningExtension by lazy {
            project.pluginManager.apply("eu.appcom.gradle.android-versioning")
            val extension = project.extensions.getByName("versioning")
            assertTrue(extension is VersioningExtension)
            extension as VersioningExtension
        }

        lateinit var git: Git

        @BeforeClass
        @JvmStatic
        fun setup() {
            git = Git.init().setDirectory(projectFolder.root).call()
            println("init git ${git.repository.directory} ${plugin.getVersionName()} - ${plugin.getVersionCode()}")
        }

        @AfterClass
        @JvmStatic
        fun cleanup() {
            println("cleanup test directory")
            projectFolder.delete()
        }
    }

    @Test
    fun `commit count higher 0`() {
        addCommit()
        assert(0 < plugin.getVersionCode())
    }

    @Test
    fun `get apk names`() {
//        addCommit()
//        assert(0 < plugin.getVersionCode())
        // TODO
//        GradleRunner.create()
//                .withProjectDir(testProjectDir.root)
//                .withArguments("${taskName}")
//                .build()
    }

    // Utility methods

    private fun addCommit(): RevCommit {
        File(projectFolder.root, "build.gradle").appendText("// addition")
        git.add().addFilepattern("build.gradle").call()
        return git.commit().setMessage("addition").call()
    }

//    private RevCommit addCommit() {
//        new File(projectFolder.root, "build.gradle").append("// addition")
//        git.add().addFilepattern("build.gradle").call()
//        git.commit().setMessage("addition").call()
//    }
//
//    private void addTag(String tagName) {
//        git.tag().setName(tagName).call()
//    }
//
//    private void addLightweightTag(String tagName) {
//        git.tag().setName(tagName).setAnnotated(false).call()
//    }
//
//    private void addBranch(String branchName) {
//        git.checkout().setCreateBranch(true).setName(branchName).call()
//    }
//
//    private void checkout(String branchName) {
//        git.checkout().setName(branchName).call()
//    }
//
//    private void merge(AnyObjectId from) {
//        git.merge().setCommit(true).include(from).call()
//    }

}