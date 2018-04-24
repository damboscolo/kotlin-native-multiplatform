# Kotlin Native Multiplatform

This is an example of [multiplatform Kotlin/Native project](https://github.com/JetBrains/kotlin-native/blob/master/MULTIPLATFORM.md). The whole business logic is written in Kotlin and shared between iOS and Android apps.

The project contains the common module named `common` and have support for iOS by `common-ios` and Android platforms.

# How to create a Kotlin/Native Multiplatform project

## Gradle Installation

Install [Gradle](https://gradle.org/) for macOS with the following command or read manual installation [here](https://docs.gradle.org/current/userguide/installation.html).
``` bash
$ brew install gradle
```

## Project Setup

Create a folder to be your project with any name, in my case is `kotlin-native-multiplatform`. Open folder in shell and run
``` bash
$ gradle init
```

Then create another folder for your common framework, it could be named `common`.

Inside the `common` folder:
* Create another `build.gradle`
* A tree of folders like `src/main/kotlin/com/example`

After all, do the same to create the iOS and the Android framework, you could named as `common-ios` and `common-android`

The result should be as following:
```bash
├── build.gradle
├── gradle
├── gradlew
├── gradlew.bat
├── settings.gradle
└── shared
    ├── common
    │   ├── build.gradle
    │   └── src
    │       └── main
    │           └── kotlin
    │               └── com
    │                   └── example
    ├── common-android
    │   ├── build.gradle
    │   └── src
    │       └── main
    │           └── kotlin
    │               └── com
    │                   └── example
    └── common-ios
        ├── build.gradle
        └── src
            └── main
               └── kotlin
                   └── com
                       └── example
```

## Configuring project

Add the following code to `settings.gradle` file
```bash
include ':shared:common'
include ':shared:common-ios'
include ':shared:common-android'
```

To configure your project `build.gradle`

```bash
// Set up a buildscript dependency on the Kotlin plugin.
buildscript {
    // Specify a Kotlin version you need.
    ext.kotlin_version = '1.2.31'

    repositories {
        jcenter()
        maven { url "https://dl.bintray.com/jetbrains/kotlin-native-dependencies" }
    }

    // Specify all the plugins used as dependencies
    dependencies {
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
        classpath "org.jetbrains.kotlin:kotlin-native-gradle-plugin:0.7-dev-1613"

    }
}

// Set up compilation dependency repositories for all projects.
subprojects {
    repositories {
        jcenter()
    }
}
```

## Configuring `common` framework

Add this code to `shared/common/build.gradle`

```bash
apply plugin: 'kotlin-platform-common'

group = 'com.example'
version = 1.0

dependencies {
    compile "org.jetbrains.kotlin:kotlin-stdlib-common:$kotlin_version"
}

```

## Configuring `common-ios` framework

The `konan` plugin creates a nice interface between gradle and the Kotlin/Native compiler.

Add this code to `shared/common-ios/build.gradle`

```bash
apply plugin: 'konan'

// Specify targets to build the framework: iOS and iOS simulator
konan.targets = ['iphone', 'iphone_sim']

konanArtifacts {
    framework('Common') {
        enableDebug true
        enableMultiplatform true
    }
}

dependencies {
    expectedBy project(':shared:common')
}
```

## Compiling

To compile and test if everything is running fine, go to root directory project and run this code to output a number of tasks preconfigured by Gradle

```bash
./gradlew tasks
```

After that, compile `common-ios` framework

```bash
./gradlew compileKonan
```

# iOS application

1. Create a new Xcode project in the root directory of project.

2. Add a new framework in the project with the same framework name as in `shared/common-ios/build.gradle`: `Common`

  > Go `File` -> `New` -> `Target` -> `Cocoa Touch Framework`

3. Choose the new framework in the `Project Navigator` and open the `Build Settings` tab and add the following `User-Defined`:

  * KONAN_ENABLE_OPTIMIZATIONS
    * `Debug`: `NO`
    * `Release`: `YES`

  * KONAN_TASK
    * `Any iOS simulator SDK`: `compileKonan<framework name>Ios_x64`
    * `Any iOS SDK`: `compileKonan<framework name>Ios_arm64`

  Replace `<framework name>` with the name you specified in the library's `common-ios/build.gradle`. It will be like that:

  <div style="text-align:center"><img src="https://raw.githubusercontent.com/damboscolo/kotlin-native-multiplatform/master/assets/common-user-defined.png" width="700" height="whatever" alignment="center"></div>

4. Ensure that the framework is still selected in the `Project Navigator` and open the `Build phases` tab. Remove all
default phases except `Target Dependencies`.
5. Add a new `Run Script` build phase and put the following code into the script field:

  ```ruby
  "$SRCROOT/../../gradlew" -p "$SRCROOT/../../shared/common-ios" "$KONAN_TASK" \
  -Pkonan.configuration.build.dir="$SRCROOT/build" \
  -Pkonan.debugging.symbols="$DEBUGGING_SYMBOLS" \
  -Pkonan.optimizations.enable="$KONAN_ENABLE_OPTIMIZATIONS"
  ```

  This script executes gradle build to compile `common-ios` library into a framework, copy the framework from origin build folder and paste to ios project root directory.

6. Add Kotlin sources into the framework
  > `File` -> `Add files to <name of your Xcode project>`

  * Choose the directory with Kotlin sources (`shared` folder in this sample).
  * Add `Header` folder created by `common-ios`framework as sources.

  <div style="text-align:center"><img src="https://raw.githubusercontent.com/damboscolo/kotlin-native-multiplatform/master/assets/common-ios-app-structure.png" width="whatever" height="500" alignment="center"></div>

Now the framework is added and all Kotlin API are available from Swift code (note that you need to build the
framework in order to get code completion).

To use Kotlin code import your framework, in our case is `Common`. Look that all Kotlin classes have the framework name as prefix:

```swift
import UIKit
import Common

class ViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        print(CommonMain().sayHello())
    }
}
```

## Android Application

```bash
apply plugin: 'kotlin-platform-jvm'

group = 'com.example'
version = 1.0

dependencies {
    implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlin_version"

    expectedBy project(':shared:common')
}
```

If something went wrong or you want to study more about, you may follow the instructions [here](https://github.com/JetBrains/kotlin-native/blob/master/MULTIPLATFORM.md#4-ios-application).
