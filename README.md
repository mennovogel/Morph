# Morph

Morph is an Android library, written in Kotlin, built to work together with Jetpack Compose. It 
allows you to transition any view to another view by animating the size and the opacity, 
resulting in a crossfade animation. 

This library is currently supported on Android 5.0 Lollipop (21) and higher. 

![](https://github.com/mennovogel/Morph/raw/master/preview.gif)

## Usage

1. Add the Imagin library to your `build.gradle` file:

```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
    implementation 'com.github.nos-digital:imagin:${imagin.version}'
}
```

2. Usage in your project:

    Load an image into an ImageView like usual.

```kotlin
Imagin.with(imageWrapper, imageView)
    // enable double tap to zoom functionality
    .enableDoubleTapToZoom()
    // enable pinch to zoom functionality
    .enablePinchToZoom()
    // add an event listener when the user does a single tap
    .enableSingleTap(object : SingleTapHandler.OnSingleTapListener {
        override fun onSingleTap() {
            Toast.makeText(imageView.context, picture.name, Toast.LENGTH_SHORT).show()
        }
    })
    // this allows us to do an action when the user swipes the ImageView vertically and/or horizontally
    .enableScroll(
        allowScrollOutOfBoundsHorizontally = false,
        allowScrollOutOfBoundsVertically = true,
        scrollDistanceToCloseInPx = distanceToClose
    ) {
        onSwipedToCloseListener?.onSwipeToClose()
    }
```

## Licence

Imagin is available under the MIT license.