package com.K19021.AndroidStudioProjects.sample1

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class SoundRevel()  {

    private val sampleRate = 4000
    private val bufferSize = 1000

    var soundSize: Int = 0

    private lateinit var audioRecord: AudioRecord
    private var buffer:ShortArray = ShortArray(bufferSize)
    private var isRecoding: Boolean = false


    fun start(){
        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )

        audioRecord.startRecording()
        isRecoding = true

        Log.d("log", isRecoding.toString())

        while (isRecoding){
            var bufferReadResult = audioRecord.read(buffer,0,bufferSize)
            var max=0
            var num=0
            for (i in 0..bufferSize step 15) {
                num= Math.abs(buffer[i].toInt())
                //最大値の更新
                if (max<=num) {
                    max=num
                }
            }
            soundSize = max
                Log.d("TAG", max.toString())
        }
    }


}