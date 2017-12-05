# Stinson
[![Github tag version badge](https://img.shields.io/github/tag/alviere/stinson.svg)](https://bintray.com/alviere/maven/stinson)
[![Kotlin version badge](https://img.shields.io/badge/kotlin-1.2.0-blue.svg)](http://kotlinlang.org/)

Elm architecture on Kotlin steroids for Android

# Introduction

This library is built to provide lightweight and easy-to-use implementation of [Elm Architecture](https://guide.elm-lang.org/architecture/)
for Kotlin developers in general and Android particularly.

Development of this library was inspired by [@sgrekov](https://github.com/sgrekov) and his series of [articles](https://proandroiddev.com/taming-state-in-android-with-elm-architecture-and-kotlin-part-1-566caae0f706)

# Modules

### Core

This module contains core framework of Elm Architecture.

### Rx

This module includes Rx implementation of main component (Stinson), thus giving ability to use RxJava 2 as
library for executing commands.

### Rx-Android

This module includes extended version of presenter and state with support of Android lifecycle and `Parcelable` interface.
Also, this module contains prepared classes for `AppCompatActivity`, `Activity` and both `Fragment` classes, from support library and normal SDK.
With help of these classes you can concentrate on writing logic, as all binding between activities and presenters is already implemented.

# Setup

To use this library, for Maven users add these lines to your `.pom` file:
```xml
// Framework only
<dependency>
  <groupId>com.alviere.stinson</groupId>
  <artifactId>core</artifactId>
  <version>x.y.z</version>
  <type>pom</type>
</dependency>

// Rx implementation
<dependency>
  <groupId>com.alviere.stinson</groupId>
  <artifactId>rx</artifactId>
  <version>x.y.z</version>
  <type>pom</type>
</dependency>

// Rx with Android support implementation
<dependency>
  <groupId>com.alviere.stinson</groupId>
  <artifactId>rx-android</artifactId>
  <version>x.y.z</version>
  <type>pom</type>
</dependency>
```
or if you are using Gradle, add following lines to your `build.gradle`:
```gradle
repositories {
    jcenter()
}

dependencies {
    // For Gradle Version below 3.0.0
    compile 'com.alviere.stinson:core:x.y.z' // Framework only
    compile 'com.alviere.stinson:rx:x.y.z' // Rx implementation
    compile 'com.alviere.stinson:rx-android:x.y.z' // Rx with Android support implementation

    // For Gradle Version 3.0.0 or above
    implementation 'com.alviere.stinson:core:x.y.z' // Framework only
    implementation 'com.alviere.stinson:rx:x.y.z' // Rx implementation
    implementation 'com.alviere.stinson:rx-android:x.y.z' // Rx with Android support implementation
}
```

### License

Stinson is open source and available under the [Apache License, Version 2.0](https://github.com/alviere/stinson/blob/master/LICENSE).