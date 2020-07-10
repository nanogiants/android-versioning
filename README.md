# Android Versioning Gradle Plugin
This plugin allows you to automatically generate your Android versionName and versionCode using Git. It also appends the version and variant names to your APK/AAB and obfuscation mapping artifacts.
## Usage
[![gradlePluginPortal](https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/de/nanogiants/android-versioning/de.nanogiants.android-versioning.gradle.plugin/maven-metadata.xml.svg?label=gradlePluginPortal)](https://plugins.gradle.org/plugin/de.nanogiants.android-versioning) [![jcenter](https://img.shields.io/maven-metadata/v/https/appcom-interactive.bintray.com/android/de/nanogiants/android-versioning/maven-metadata.xml.svg?label=jcenter)](https://bintray.com/appcom-interactive/android/android-versioning-plugin/_latestVersion)

The plugin is available from the GradlePluginPortal (preferred) and jcenter.
### `plugins` block:
<details open>
  <summary>Kotlin</summary>

```kotlin
// app build.gradle.kts
plugins {
  id("de.nanogiants.android-versioning") version "2.4.0"
}
```
</details>

<details>
  <summary>Groovy</summary>

```groovy
// app build.gradle
plugins {
  id 'de.nanogiants.android-versioning' version '2.4.0'
}
```
</details>

or via the
### `buildscript` block (legacy):
<details open>
  <summary>Kotlin</summary>


```kotlin
// top-level build.gradle.kts
buildscript {
  dependencies {
    classpath("de.nanogiants:android-versioning:2.4.0")
  }
}
```
```kotlin
// app build.gradle.kts
apply(plugin = "de.nanogiants.android-versioning")
```
</details>

<details>
  <summary>Groovy</summary>

```groovy
// top-level build.gradle
buildscript {
  dependencies {
    classpath 'de.nanogiants:android-versioning:2.4.0'
  }
}
```
```groovy
// app build.gradle
apply plugin: 'de.nanogiants.android-versioning'
```
</details>

### Version code and name (optional)
```groovy
android {
  defaultConfig {
    versionCode versioning.getVersionCode()
    versionName versioning.getVersionName()
  }
}
```
#### Use the plugin by referencing the versioning extension:
* `versioning.getVersionCode()` returns the current Git commit count
* `versioning.getVersionName()` returns the latest Git tag

#### Customization:
* `versioning.getVersionName(checkBranch: Boolean)` if `checkBranch` is set to *true* the plugin will check if the current branch is `release/x.x.x` or `hotfix/x.x.x` and use the branch name instead the latest tag.

### Artifact naming
The plugin will automatically rename APK, AAB and Mapping.txt files for all assemble and bundle tasks. This will also work if you do not use the versioning extension in the defaultConfig. You can still use the default `archivesBaseName` property.

#### Example:
Build Variant `productionStoreRelease`
```groovy
android {
  defaultConfig {
    archivesBaseName = "myAppName"
  }
}
```
Artifacts:
```
myAppName-production-store-3.9.0-3272-release.apk
myAppName-production-store-3.9.0-3272-release.aab
myAppName-production-store-3.9.0-3272-release-mapping.txt
```
#### Note:
Because Android Studio does not know about the AAB renaming, the `locate` or `analyze` links in the event log and notifications will only work for APK files by default. You can set `keepOriginalArtifacts` to keep the original files. The plugin also prints the file URI for renamed artifacts. 

#### Customization:
* `excludeBuildTypes`: comma separated list of buildTypes (e.g. debug) to be excluded from the artifact naming
* `keepOriginalBundleFile`: copy ABB files instead of renaming them  (default *false*)
* `keepOriginalMappingFile`: copy mapping files instead of renaming them (default *true* to avoid caching issues)
```groovy
// app build.gradle
versioning {
  excludeBuildTypes = "debug" // default: null
  keepOriginalBundleFile = true // default: false
  keepOriginalMappingFile = false // default: true
}
```

## License
	Copyright (C) 2020 NanoGiants GmbH

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
