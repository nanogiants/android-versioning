@file:Suppress("UnstableApiUsage")

plugins {
  kotlin("jvm") version "1.3.72"
  `java-gradle-plugin`
  `maven-publish`
}

group = "eu.nanogiants"
version = "2.0.0"

repositories {
  google()
  mavenCentral()
  jcenter()
}

dependencies {
  implementation("com.android.tools.build:gradle:4.0.0")
}

tasks {
  compileKotlin {
    kotlinOptions.jvmTarget = "1.8"
  }
}

gradlePlugin {
  plugins {
    create("versioningPlugin") {
      id = "eu.nanogiants.android-versioning"
      implementationClass = "eu.nanogiants.gradle.VersioningPlugin"
    }
  }
}

java {
  withSourcesJar()
}

tasks.withType<GenerateModuleMetadata> {
  enabled = false
}

// publish task: publishMavenPublicationToBintrayRepository -Pbintray_user=name -Pbintray_key=key
publishing {
  publications {
    create<MavenPublication>("maven") {
      from(components["java"])
      pom {
        name.set(project.name)
        description.set("Android Versioning Gradle Plugin")
        url.set("https://github.com/nanogiants/android-versioning")
        licenses {
          license {
            name.set("The Apache Software License, Version 2.0")
            url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
          }
        }
        developers {
          developer {
            name.set("NanoGiants GmbH")
          }
        }
        scm {
          connection.set("https://github.com/nanogiants/android-versioning.git")
          developerConnection.set("https://github.com/nanogiants/android-versioning.git")
          url.set("https://github.com/nanogiants/android-versioning")
        }
      }
    }
  }
  repositories {
    maven {
      name = "bintray"
      url = uri("https://api.bintray.com/maven/appcom-interactive/android/${project.name}/;publish=1;")
      credentials {
        username = findProperty("bintray_user") as String?
        password = findProperty("bintray_key") as String?
      }
    }
  }
}