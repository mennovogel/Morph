# Morph

Morph is an Android library, written in Kotlin, built to work together with Jetpack Compose. It 
allows you to transition any view to another view by animating the size and the opacity, 
resulting in a crossfade animation. 

This library is currently supported on Android 5.0 Lollipop (21) and higher. 

![](https://github.com/mennovogel/Morph/raw/master/preview.gif)

## Usage

Add it to your project:

```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
    implementation 'com.github.mennovogel:morph:${morph.version}'
}
```

Usage in your project:

```kotlin
// Morph a box into another box when it is clicked.
@Preview
@Composable
fun MorphingBox() {
    var boxState by remember { mutableStateOf(BoxState.LARGE) }

    Morph(
        targetState = boxState,
        contentAlignment = Alignment.Center,
        // It is recommended to turn off fadePreviousState when using the same shape for all states
        fadePreviousState = false
    ) { crossFadeState ->
        when (crossFadeState) {
            BoxState.LARGE -> {
                Box(modifier = Modifier
                    .size(300.dp)
                    .background(color = MaterialTheme.colors.primary)
                    .clickable {
                        boxState = BoxState.SMALL
                    }
                )
            }
            BoxState.SMALL -> {
                Box(modifier = Modifier
                    .size(100.dp)
                    .background(color = MaterialTheme.colors.secondary)
                    .clickable {
                        boxState = BoxState.LARGE
                    }
                )
            }
        }
    }
}

private enum class BoxState {
    LARGE, SMALL
}
```

## Licence

Morph is available under the MIT license.