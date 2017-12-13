# Stinson
[![Github tag version badge](https://img.shields.io/github/tag/alviere/stinson.svg)](https://bintray.com/alviere/maven/stinson)
[![Kotlin version badge](https://img.shields.io/badge/kotlin-1.2.0-blue.svg)](http://kotlinlang.org/)

Elm architecture on Kotlin steroids for Android

# Introduction

This library is built to provide lightweight and easy-to-use implementation of [Elm Architecture](https://guide.elm-lang.org/architecture/)
for Kotlin developers in general and Android particularly.

Development of this library was inspired by [@sgrekov](https://github.com/sgrekov) and his series of [articles](https://proandroiddev.com/taming-state-in-android-with-elm-architecture-and-kotlin-part-1-566caae0f706)

# How to use

In general, Stinson is classic MVP pattern framework with application of Elm architecture on the presentation layer. In this how-to we will use the `rx-android` implementation to showcase the capabilities of this library.

### View

Defining a view is very simple. Just create an interface that extends Stinson's `View` interface:

```kotlin
interface LoginView : View {
    fun setLoginText(text: String)
    fun setLoginErrorText(@StringRes text: Int)
    fun setLoginErrorEnabled(enabled: Boolean)

    fun setPasswordText(text: String)
    fun setPasswordErrorText(@StringRes text: Int)
    fun setPasswordErrorEnabled(enabled: Boolean)

    fun setLoading(loading: Boolean)

    fun showSnackbar(@StringRes text: Int)
}
```

### State

State in Stinson can be referred as `ViewModel` in other popular MVP framework libraries. This classes represent the current state of your view. In Kotlin, it is very convinient to use data classes for that purpose. In `rx-android` implementation, there is `ParcelableState` interface which extends Stinson's `State` and Android's `Parcelable` interface to enable state saving through Android components' lifecycle:

```kotlin
@Parcelize
@SuppressLint("ParcelCreator")
data class LoginState(
        val login: String = "",
        val loginError: Int = R.string.empty_string,
        val loginErrorEnabled: Boolean = false,
        val password: String = "",
        val passwordError: Int = R.string.empty_string,
        val passwordErrorEnabled: Boolean = false,
        val isLoading: Boolean = false
) : ParcelableState
```

### Stinson

`Stinson` class is the main component of Elm architecture. It can be described as message queue manager and should be provided to every presenter that is built on Stinson library. Classicaly, these kind of dependencies are provided through DI libraries. In our case, we are using [Koin](https://github.com/Ekito/koin):

```kotlin
class LoginModule : AndroidModule() {
    override fun context() = applicationContext {
        context(CONTEXT_NAME) {
            provide {
                RxStinson<LoginState>(AndroidSchedulers.mainThread())
            }
        }
    }
}
```

As you can see, `Stinson` class takes in generic type of `State` interface. So for every state that you have in your app, you should provide corresponding `Stinson` instance. It is not very comfortable, but it is done to avoid unnecessary state instance casting in your presenters.

### Messages

Messages are one of the main components of Elm architecture. These are indicators of certain UI or data interactions that is going on through your presenter's lifecycle. There are 3 predefined message classes: `Init`, `Idle` and `Error` as well as base class `Message`. In our sample project we have defined these messages:

```kotlin
    class LoginTextMessage(val text: String) : Message()
    class PasswordTextMessage(val text: String) : Message()
    class LoginButtonClickMessage : Message()
    class LoginResultMessage(val success: Boolean) : Message()
```

### Commands

Commands are another main component of Elm architecture. These classes represent an action that is need to be executed and provide all necessary parameters for that. There is only one predefined command: `None`. In out sample project we have defined only one additional command:

```kotlin
    class LoginCommand(val login: String, val password: String) : Command()
```

### Executors

Executor is an additional abstraction layer that is built to enable support of different asynchronous libraries. In our example, we are using RxJava, but you are free to implement whatever approach you like. In `rx-android` implementation, `RxExecutor` is used. It takes lambda function which returns `Single<Message>` and used inside `Stinson` class to execute your actions:

```kotlin
    return RxExecutor {
        Single.create<Message> {
            Thread.sleep(5000L)
            it.onSuccess(LoginResultMessage(Random().nextBoolean()))
        }.subscribeOn(Schedulers.io())
    }
```

### Presenter

Presenters that use Stinson should extend from `AndroidRxPresenter<View, State>` class and provide instance of `Stinson` class:

```kotlin
class LoginPresenter(stinson: RxStinson<LoginState>)
    : AndroidRxPresenter<LoginView, LoginState>(stinson) {
```

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
