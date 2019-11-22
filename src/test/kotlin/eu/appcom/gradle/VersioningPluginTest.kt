package eu.appcom.gradle

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.revwalk.RevCommit
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

class VersioningPluginTest {

  companion object {

    @TempDir
    @JvmStatic
    lateinit var tempProjectDir: Path

    private val project: Project by lazy {
      ProjectBuilder.builder()
        .withProjectDir(tempProjectDir.toFile())
        .build()
    }

    private val plugin: VersioningExtension by lazy {
      project.pluginManager.apply("eu.appcom.gradle.android-versioning")
      val extension = project.extensions.getByName("versioning")
      assertTrue(extension is VersioningExtension)
      extension as VersioningExtension
    }

    lateinit var git: Git

    @BeforeAll
    @JvmStatic
    fun setup() {
      Files.createFile(tempProjectDir.resolve("build.gradle"))

      git = Git.init().setDirectory(tempProjectDir.toFile()).call()
      println("init git ${git.repository.directory} ${plugin.getVersionName()} - ${plugin.getVersionCode()}")
    }

    @AfterAll
    @JvmStatic
    fun cleanup() {
      println("cleanup test directory")
      tempProjectDir.toFile().delete()
    }
  }

  @Test
  fun `commit count higher 0`() {
    addCommit()
    assert(0 < plugin.getVersionCode())
  }

  @Test
  fun `get apk names`() {
    // TODO
//        GradleRunner.create()
//                .withProjectDir(testProjectDir.root)
//                .withArguments("${taskName}")
//                .build()
  }

  // Utility methods

  @Throws(IOException::class)
  private fun writeFile(destination: File, content: String) {
    var output: BufferedWriter? = null
    try {
      output = BufferedWriter(FileWriter(destination))
      output.write(content)
    } finally {
      output?.close()
    }
  }

  private fun addCommit(): RevCommit {
    File(tempProjectDir.toFile(), "build.gradle").appendText("// addition")
    git.add().addFilepattern("build.gradle").call()
    return git.commit().setSign(false).setMessage("addition").call()
  }

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