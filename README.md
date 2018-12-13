# Aoide

🎤 Aoide is an audio recorder for Android

## Features

- Support pause recording
- Multiple audio formats (MP3, AAC-ADTS, WAV)
- API Flow Based on RxJava

## Usage

### 0. Permission

- ``android.permission.RECORD_AUDIO``

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
