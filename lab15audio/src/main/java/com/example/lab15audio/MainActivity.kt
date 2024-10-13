package com.example.lab15audio

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.*
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    private var recFile: File = File("")
    private var isPermissionGranted = false
    private var audioRecorder: AudioRecord? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            isPermissionGranted = isGranted
            if (!isGranted) {
                Log.e("Permission", "Audio recording permission denied")
            }
        }
        checkAndRequestPermissions(requestPermissionLauncher)

        setContent {
            var isRecording by remember { mutableStateOf(false) }
            val coroutineScope = rememberCoroutineScope()

            MainScreen(
                onPlayAudio = {
                    if (recFile.exists()) {
                        coroutineScope.launch {
                            val inputStream = FileInputStream(recFile)
                            playAudio(inputStream)
                        }
                    } else {
                        Log.e("Playback", "Audio file does not exist.")
                    }
                },
                onRecordAudio = {
                    if (isRecording) {
                        stopRecording()
                        isRecording = false
                    } else {
                        if (isPermissionGranted) {
                            isRecording = true
                            startRecording()
                        } else {
                            Log.e("Permission", "Audio recording permission denied")
                        }
                    }
                },
                isRecording = isRecording
            )
        }
    }

        private fun checkAndRequestPermissions(requestPermissionLauncher: androidx.activity.result.ActivityResultLauncher<String>) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        } else {
            isPermissionGranted = true
        }
    }

    private fun startRecording() {
        try {
            val recFileName = "testrecording.raw"
            val storageDir = getExternalFilesDir( Environment .DIRECTORY_MUSIC)
            try {
                recFile = File(storageDir.toString() + "/"+ recFileName )
            } catch (ex: IOException ) {
                Log.e("FYI", "Can't create audio file $ex")
            }
            val minBufferSize = AudioRecord.getMinBufferSize(
                44100,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )

            audioRecorder = AudioRecord.Builder()
                .setAudioSource(MediaRecorder.AudioSource.MIC)
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(44100)
                        .setChannelMask(AudioFormat.CHANNEL_IN_MONO)
                        .build()
                )
                .setBufferSizeInBytes(minBufferSize)
                .build()

            audioRecorder?.startRecording()

            val data = ByteArray(minBufferSize)
            val outputStream = FileOutputStream(recFile)

            Thread {
                while (audioRecorder?.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                    val read = audioRecorder?.read(data, 0, minBufferSize)
                    if (read != null && read > 0) {
                        outputStream.write(data)
                    }
                }
                outputStream.close()
            }.start()
        } catch (e: SecurityException) {
            Log.e("Permission", "Audio recording permission denied " + e.message)
        }
    }


    private fun stopRecording() {
        audioRecorder?.stop()
        audioRecorder?.release()
        audioRecorder = null
    }

    suspend fun playAudio(inputStream: FileInputStream) = withContext(Dispatchers.IO) {
        val minBufferSize = AudioTrack.getMinBufferSize(44100,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT)
        val aBuilder = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()
        val aFormat = AudioFormat.Builder()
            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
            .setSampleRate(44100)
            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
            .build()
        val audioTrack = AudioTrack.Builder()
            .setAudioAttributes(aBuilder)
            .setAudioFormat(aFormat)
            .setBufferSizeInBytes(minBufferSize)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()
        audioTrack.play()
        val audioData = ByteArray(minBufferSize)
        var bytesRead = 0
        while (bytesRead != -1) {
            bytesRead = inputStream.read(audioData, 0, minBufferSize)
            if (bytesRead != -1) {
                audioTrack.write(audioData, 0, bytesRead)
            }
        }
        audioTrack.stop()
        audioTrack.release()
        inputStream.close()
    }
}

@Composable
fun MainScreen(onPlayAudio: () -> Unit, onRecordAudio: () -> Unit, isRecording: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Audio Recorder", fontWeight = FontWeight.Bold, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Button(onClick = { onPlayAudio() }) {
                Text(text = "Play Audio")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = { onRecordAudio() }) {
                Text(text = if (isRecording) "Stop Recording" else "Record Audio")
            }
        }
    }
}
