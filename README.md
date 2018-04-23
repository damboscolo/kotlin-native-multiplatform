# Kotlin Native multiplatform


This is an example of multiplatform Kotlin/Native project. The whole business logic is written in Kotlin and shared between iOS and Android apps.

The project contains the common module named `common` and have support for iOS by `common-ios` and Android platforms.

# How to create a Kotlin Native Multiplatform project

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

After all, do the same to create the iOS framework, you could named as `common-ios`

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

```bash
./gradlew tasks
```

To compile `common-ios` framework

```bash
./gradlew compileKonan
```

# iOS project

In your iOS Project, add a Run Script in Build Phases
```ruby
case "$PLATFORM_NAME" in
iphoneos)
NAME=ios_arm64
;;
iphonesimulator)
NAME=ios_x64
;;
*)
echo "Unknown platform: $PLATFORN_NAME"
exit 1
;;
esac

"$SRCROOT/../../gradlew" -p "$SRCROOT/../../shared/common-ios" "build"
rm -rf "$SRCROOT/build/"
mkdir "$SRCROOT/build/"
cp -a "$SRCROOT/../../shared/common-ios/build/konan/bin/$NAME/" "$SRCROOT/build/"
```
