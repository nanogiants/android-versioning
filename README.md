# Android Versioning Gradle Plugin [ ![Download](https://api.bintray.com/packages/appcom-interactive/android/android-versioning/images/download.svg) ](https://bintray.com/appcom-interactive/android/android-versioning/_latestVersion)

This plugin automatically generates your Android versionName and versionCode using Git. It also appends the version and variant names to your APK/AAB and obfuscation mapping artifacts.

## Usage

### `buildscript` block:
```groovy
// top-level build.gradle
buildscript {
  repositories {
    jcenter()
  }
  dependencies {
    classpath 'eu.nanogiants:android-versioning:2.2.0'
  }
}
```
```groovy
// app build.gradle
apply plugin: 'eu.nanogiants.android-versioning'
```

### Version code and name
```groovy
android {
  defaultConfig {
    versionCode versioning.getVersionCode()
    versionName versioning.getVersionName()
  }
}
```
#### Use the plugin by referencing the versioning extension.
* `versioning.getVersionCode()` returns the current Git commit count
* `versioning.getVersionName()` returns the latest Git tag

#### Optional:
* `versioning.getVersionName(checkBranch: Boolean)` if `checkBranch` is set to *true* the plugin will check if the current branch is `release/x.x.x` or `hotfix/x.x.x` and use the branch name instead the latest tag.

### Artifact naming
The plugin will automatically rename APK, AAB and Mapping.txt files for all assemble and bundle tasks. This will also work if you do not use the versioning extension in the defaultConfig. You can still use the default `archivesBaseName` property.

#### Example:
Build Variant `productionStoreDebug`
```groovy
android {
  defaultConfig {
    archivesBaseName = "myAppName"
  }
}
```
Artifacts:
```
myAppName-production-store-3.9.0-3272-debug.apk
myAppName-production-store-3.9.0-3272-debug.aab
myAppName-production-store-3.9.0-3272-mapping.txt
```
#### Note:
Because Android Studio does not know about the AAB renaming, the `locate` or `analyze` links in the event log and notifications will only work for APK files by default. You can set `keepOriginalArtifacts` to keep the original files. The plugin also prints the file URI for renamed artifacts. 

#### Optional:
* `excludeBuildTypes`: comma separated list of buildTypes (e.g. debug) to be excluded from the artifact naming
* `keepOriginalArtifacts`: copy artifact files instead of renaming them
```groovy
// app build.gradle
versioning {
  excludeBuildTypes = "debug" // default: null
  keepOriginalArtifacts = true // default: false
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
