## What is this?

* Generate the android app version name and version code automatically (via git).
* Append the version number to your APK artifacts.

## Preconditions

* [Git](https://git-scm.com/)
* At least one git tag which defines your current version (e.g. 1.0.2).
* We suggest to follow the [SemVer](http://semver.org/) specification for the versioning.

## Installation

Add the following to your project level build.gradle

```groovy
buildscript {
  repositories {
    jcenter()
  }
  dependencies {
    classpath 'eu.appcom.gradle:android-versioning:0.2.0'
  }
}
```

Add the following to the module level build.gradle

```groovy
apply plugin: 'eu.appcom.gradle.android-versioning'
```

## Usage

All usage scenarios take place in the app level build.gradle file.

### Version code and name

The android version code is represented by the git commit count.
The android version name is represented by the newest git tag of your repository.
To make use of the plugin, just remove the following lines from the build.gradle file.

```groovy
versionCode ...
versionName ...
```

### APK Artifact with versioned name

The name of the output APK will get an additional version number part which consists of the git tag (app version name) and the git commit count (app version code), e.g.:
* version name (git tag): 1.2.0
* version code (git commit count): 23
* APK version info: 1.2.0.23
* full APK name: appname-1.2.0.23-debug.apk

### Show the current versions

Use the gradle task 'printVersions' to print out all relevant version information to the gradle console.

## Experimental Gradle

When using experimental gradle the installation and configuration is slightly different.

### Installation

Add the following to your project level build.gradle
There is no need to apply the plugin on the module level build.gradle file(s).

```groovy
apply plugin: 'eu.appcom.gradle.android-versioning'

dependencies {
    classpath 'com.android.tools.build:gradle-experimental:0.11.1'
    classpath 'eu.appcom.gradle:android-versioning:0.2.0'
}
```

### Usage

To make use of the plugin, just change the following lines from the build.gradle file to this:

```groovy
versionName androidVersionName
versionCode androidVersionCode
```

## License

Copyright (c) 2017 appcom interactive GmbH

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
