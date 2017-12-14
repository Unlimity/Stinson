# Stinson
[![Github tag version badge](https://img.shields.io/github/tag/alviere/stinson.svg)](https://bintray.com/alviere/maven/stinson)
[![Kotlin version badge](https://img.shields.io/badge/kotlin-1.2.10-blue.svg)](http://kotlinlang.org/)

Elm architecture on Kotlin steroids for Android

# Introduction

This library is built to provide lightweight and easy-to-use implementation of [Elm Architecture](https://guide.elm-lang.org/architecture/)
for Kotlin developers in general and Android particularly.

Development of this library was inspired by [@sgrekov](https://github.com/sgrekov) and his series of [articles](https://proandroiddev.com/taming-state-in-android-with-elm-architecture-and-kotlin-part-1-566caae0f706)

# How to use

In general, Stinson is classic MVP pattern framework with application of Elm architecture on the presentation layer. In this how-to we will use the `rx` and `android` implementations to showcase the capabilities of this library.

### Elm architecture

Elm architecture in a nutshell is a system that is based on messages, states and commands and guarantees state preserving and unidirectional data flow. The base components of Elm architecture are:

 - Main component (`Stinson`), which is responsible for managing message queue, passing updates through presenter and executing commands
 - Messages, base components which represent certain interactions or events
 - Commands, base components which represent an action that need to be taken
 
 The current pipeline of Stinson library is presented below:
 
 ```
 View -> Presenter.interaction() -> Stinson.accept(message) -> Presenter.update(message, state): Pair<Command, State> ->
 Presenter.render(state) -> Presenter.subscribe(state) -> Presenter.executor(command): Executor ->
 Executor.execute(): Message -> Stinson.accept(message)
 ```

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

State in Stinson can be referred as `ViewModel` in other popular MVP framework libraries. These classes represent the current state of your view. In Kotlin, it is very convinient to use data classes for that purpose. In `android` implementation, there is a `ParcelableState` interface which extends Stinson's `State` and Android's `Parcelable` interfaces to enable state saving through Android components' lifecycle:

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

`Stinson` class is the main component of Elm architecture. It can be described as message queue manager and should be provided for every presenter that is built on Stinson library. For `RxPresenter` it is done automatically, but for `AndroidPresenter`, which is built to support different implementation of `Stinson` it still should be provided manually. Classically, this is done by DI frameworks. In our case, we're using [Koin](https://github.com/Ekito/koin): 

```kotlin
class LoginModule : AndroidModule() {
    override fun context() = applicationContext {
        context(CONTEXT_NAME) {
            provide {
                RxStinson<LoginState>(AndroidSchedulers.mainThread())
            }

            provide {
                LoginPresenter(get())
            }
        }
    }

    companion object {
        const val CONTEXT_NAME = "LoginActivity"
    }
}
```

`Stinson` instance will be available in your presenter as a `stinson` protected property.

### Messages

Messages are one of the main components of Elm architecture. These are indicators of certain UI or data interactions that is going on through your presenter's lifecycle. There are 2 predefined message objects: `Init`, `Idle` and class `Error` as well as base class `Message`. In our sample project we have defined these messages:

```kotlin
    class LoginTextMessage(val text: String) : Message()
    class PasswordTextMessage(val text: String) : Message()
    class LoginButtonClickMessage : Message()
    class LoginResultMessage(val success: Boolean) : Message()
```

### Commands

Commands are another main component of Elm architecture. These classes represent an action that is need to be executed and provide all necessary parameters for that. There is only one predefined command object: `None`. In out sample project we have defined only one additional command:

```kotlin
    class LoginCommand(val login: String, val password: String) : Command()
```

### Executors

Executor is an additional abstraction layer that is built to enable support of different asynchronous libraries. In our example, we are using RxJava, but you are free to implement whatever approach you like. In `rx` implementation, `RxExecutor` is used. It takes lambda function which returns `Single<Message>` and used inside `RxStinson` class to execute your actions:

```kotlin
    return RxExecutor {
        Single.create<Message> {
            Thread.sleep(5000L)
            it.onSuccess(LoginResultMessage(Random().nextBoolean()))
        }.subscribeOn(Schedulers.io())
    }
```

### Subsriptions

Subscription is another component of Elm architecture. It allows to execute certain tasks that produce messages after every change in presenter's state based on provided parameters. In our sample project we are using `rx` implementation, so our class is `RxSubscription`. Most popular case of using this component is filtering:

```kotlin
val subscription = RxSubscription<String> { query ->
    Single.create<Message> {
        it.onSuccess(FilterMessage(provider.filter(query)))
    }
}

override fun subscribe(state: State) {
    stinson.subscribe(subscription, state.query)
}
```

Subscription will be executed only after first access to instance and after every change of parameters.

### Presenter

Presenters that use Stinson should extend from `Presenter<View, State, Executor>`, `RxPresenter<View, State>` or `AndroidPresenter<View, State, Executor>` classes based on what implementation are you using and provide `Stinson` instance:

```kotlin
class LoginPresenter(val stinson: RxStinson<LoginState>)
    : AndroidPresenter<LoginView, LoginState, RxExecutor>(stinson) {
```

There are several methods that should be overriden:

```kotlin
    abstract fun initialState(): State // From core framework's Presenter class
    fun update(message: Message, state: State): Pair<Command, State> // From Component interface
    fun render(state: State) // From Component interface
    fun subscribe(state: State) // From Component interface
    fun executor(command: Command): Executor // From Component interface
```

`initialState` function is pretty straightforward. Here you just return the initial state of your view:

```kotlin
    override fun initialState() = LoginState()
```

`update` function is used to process incoming messages from `Stinson` and make corresponding changes to the state plus provide command that should be executed in response, if necessary:

```kotlin
    override fun update(message: Message, state: LoginState): Pair<Command, LoginState> {
        return when (message) {
            is LoginTextMessage -> Pair(None, state.copy(login = message.text))
            is PasswordTextMessage -> Pair(None, state.copy(password = message.text))
            is LoginButtonClickMessage -> tryLogin(state)
            is LoginResultMessage -> processLogin(message, state)
            else -> Pair(None, state)
        }
    }
```

`render` function is invoked by `Stinson` on every message that has been processed. Here you interact with your `View` and apply your current view state. Please note, that `view` property of `Presenter` is mutable and nullable. Thus it should be accessed via safe call operator `?`:

```kotlin
    override fun render(state: LoginState) {
        view?.run {
            setLoginText(state.login)
            setLoginErrorText(state.loginError)
            setLoginErrorEnabled(state.loginErrorEnabled)

            setPasswordText(state.password)
            setPasswordErrorText(state.passwordError)
            setPasswordErrorEnabled(state.passwordErrorEnabled)

            setLoading(state.isLoading)
        }
    }
```

`subscribe` function is invoked by `Stinson` on every message that has been processed. Here you can provide new parameters for your subsription, thus triggering execution of asynchronous tasks that you may have:

```kotlin
override fun subscribe(state: State) {
    stinson.subscribe(subscription, state.query)
}
```

`executor` function is invoked by `Stinson` on every command that is passed through `update` function and which is not instance of `None`. This function must return corresponding executor for possible command that your presenter can issue:

```kotlin
    override fun executor(command: Command): RxExecutor {
        return when (command) {
            is LoginCommand -> executeLogin(command)
            else -> RxExecutor { Single.just(Idle) }
        }
    }
```

There is also Android specific functions defined in `AndroidPresenter` class. Those are not abstract and not obligated to be overriden:

```kotlin
    @CallSuper
    fun onCreate(savedInstanceState: Bundle?) {}

    fun onStart() {}
    fun onResume() {}
    fun onPause() {}
    fun onStop() {}

    @CallSuper
    fun onSaveInstanceState(savedInstanceState: Bundle) {}

    @CallSuper
    fun onDestroy() {}
```

All these functions are invoked automatically for you if you use provided activities and fragments as `View` implementations.

### View implementation

Finally, when everything is done you are ready to implement the view and get things going. `android` implementation provides you with four classes that can help you concentrate on writing logic, instead of paying attention to state saving, dealing with configuration changes and binding to your presenter. These classes are: `StinsonActivity`, `StinsonAppCompatActivity`, `StinsonFragment` and `StinsonSupportFragment`. Here's the example using `StinsonActivity` as base class:

```kotlin
class LoginActivity : StinsonRxActivity<LoginView, LoginState, LoginPresenter>(), LoginView {
    private val presenter by inject<LoginPresenter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        registerTextWatchers()

        passwordEdit.isPasswordVisibilityToggleEnabled = true
        loginButton.setOnClickListener { presenter.loginButtonClicked() }
    }

    override fun onPause() {
        super.onPause()

        if (isFinishing) {
            releaseContext(LoginModule.CONTEXT_NAME)
        }
    }

    override fun setLoginText(text: String) {
        loginEdit.editText?.takeIf { text != loginEdit.editText?.text.toString() }?.setText(text)
    }

    override fun setLoginErrorText(text: Int) {
        loginEdit.error = getString(text)
    }

    override fun setLoginErrorEnabled(enabled: Boolean) {
        loginEdit.isErrorEnabled = enabled
    }

    override fun setPasswordText(text: String) {
        passwordEdit.editText?.takeIf { text != passwordEdit.editText?.text.toString() }?.setText(text)
    }

    override fun setPasswordErrorText(text: Int) {
        passwordEdit.error = getString(text)
    }

    override fun setPasswordErrorEnabled(enabled: Boolean) {
        passwordEdit.isErrorEnabled = enabled
    }

    override fun setLoading(loading: Boolean) {
        loginEdit.isEnabled = !loading
        passwordEdit.isEnabled = !loading
        loginButton.isEnabled = !loading
        loginButton.text = if (loading) "" else getString(R.string.login_button_text)
        progressBar.visibility = if (loading) View.VISIBLE else View.GONE
    }

    override fun showSnackbar(text: Int) {
        Snackbar.make(contentView, text, Snackbar.LENGTH_LONG).show()
    }

    override fun providePresenter() = presenter

    override fun provideView() = this
}
```

So, when using one of the four classes you need to specify type of `View`, `State` and `Presenter` that you're interacting with, as well as override two abstract methods: `providePresenter()` and `provideView()`.

### Important

When all of the components are used correctly, all view attaching/detaching, state saving and lifecycle methods invocations are done automatically, which allows us to concentrate on implementing interesting parts of our apps. Please refer to the sample for further understanding of how to use this library.

# Modules

### Core

This module contains core framework of Elm Architecture.

### Rx

This module includes Rx implementation of main component (Stinson), as well as `RxExecutor`, `RxSubscription` and `RxPresenter` thus giving ability to use RxJava 2 as library for executing commands.

### Android

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

// Android support
<dependency>
  <groupId>com.alviere.stinson</groupId>
  <artifactId>android</artifactId>
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
    compile 'com.alviere.stinson:android:x.y.z' // Android support

    // For Gradle Version 3.0.0 or above
    implementation 'com.alviere.stinson:core:x.y.z' // Framework only
    implementation 'com.alviere.stinson:rx:x.y.z' // Rx implementation
    implementation 'com.alviere.stinson:android:x.y.z' // Android support
}
```

### License

Stinson is open source and available under the [Apache License, Version 2.0](https://github.com/alviere/stinson/blob/master/LICENSE).
