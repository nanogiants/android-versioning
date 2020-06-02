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