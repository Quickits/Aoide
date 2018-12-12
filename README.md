# Aoide

🎤 Aoide is an audio recorder for Android

## Features

- Support pause recording
- Multiple audio formats (WAV, AAC-ADTS)
- API Flow Based on RxJava

## Usage

### 0. Permission

- ``android.permission.RECORD_AUDIO``

If you are targeting Android 6.0+, you need to handle runtime permission request before next step.

### 1. Create recorder task and Listening state

```kotlin
Aoide.with(this).fileEncoder(TaskCreator.TYPE_FILE_ENCODER_AAC)
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
                println(status.filePath)
            }

            is Error -> {
                println(status.message)
            }
        }
    }
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
