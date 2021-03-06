# Aoide

[![](https://jitpack.io/v/cn.quickits/Aoide.svg)](https://jitpack.io/#cn.quickits/Aoide)

🎤 Aoide is an audio recorder for Android

## Features

- Support pause recording
- Multiple audio formats (MP3, AAC-ADTS, WAV, PCM)
- API Flow Based on RxJava

## Sample App

|                Home                |               Recording                |                  Play                  |
| :--------------------------------: | :------------------------------------: | :------------------------------------: |
| ![Images](./art/screenshots_1.png) | ![FileSystem](./art/screenshots_2.png) | ![FileSystem](./art/screenshots_3.png) |

## Download

- Add it in your root build.gradle at the end of repositories:

```
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

- Add the dependency

```
dependencies {
    implementation 'cn.quickits:Aoide:0.2.1'
}
```

## Usage

### 0. Permission

- `android.permission.RECORD_AUDIO`

If you are targeting Android 6.0+, you need to handle runtime permission request before next step.

### 1. Create mp3 recorder task and Listening state

```kotlin
Aoide.with(this).mp3()
    .create()
    .subscribe { status ->
        when (status) {
            is Prepared -> {
                println("Prepared")
            }

            is Recording -> {
                println("Recording")
            }

            is Paused -> {
                println("Paused")
            }

            is Completed ->
                println("Completed: " + status.filePath)
            }

            is Error -> {
                println("Error")
                status.throwable.printStackTrace()
            }
        }
    }
```

or other audio format

```
Aoide.with(this).aac()
Aoide.with(this).wav()
Aoide.with(this).pcm()
```

### 2. Start recording

```kotlin
Aoide.start()?.subscribe()
```

### 3. Pause recording

```kotlin
Aoide.pause()?.subscribe()
```

### 4. Stop recording

```kotlin
Aoide.stop()?.subscribe()
```

## License

Apache License Version 2.0

Copyright (c) 2018-present, GavinLiu
