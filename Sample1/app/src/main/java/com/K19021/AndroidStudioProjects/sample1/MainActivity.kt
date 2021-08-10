package com.K19021.AndroidStudioProjects.sample1

import OtherFileStorage
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.lang.Math.pow
import java.lang.Math.sqrt
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var AccSensor: Sensor? = null

    lateinit var OtherFileStorage: OtherFileStorage
    lateinit var SoundRevel: SoundRevel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        AccSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)

        OtherFileStorage = OtherFileStorage(this)
        SoundRevel = SoundRevel()

        val StartButton: Button = findViewById(R.id.Start)
        StartButton.setOnClickListener(clickListener_start)
    }

    //Startボタンが押された場合にセンサーの値を送り始める(resumeの中身と同じ)
    private val clickListener_start : View.OnClickListener = View.OnClickListener {
        val textView: TextView = findViewById(R.id.textView)
        textView.setText("5")
        // 100ms に一回 soundSize


        for (i in 1..4){
            Handler().postDelayed(
                {// カウントダウン
                    var count = 5-i
                    textView.setText(count.toString())
                },1000*i.toLong())
        }
        thread{
            SoundRevel.start()
        }
        Handler().postDelayed(Runnable
            { // 5秒後にセンサーの値の送信を開始
                //加速度センサー
                sensorManager.registerListener(this, AccSensor, SensorManager.SENSOR_DELAY_UI)
                textView.setText("Start!!")
                Log.d("log",SoundRevel.soundSize.toString())
            }, 5000)

        Handler().postDelayed(
            {// 15秒後にセンサーの値の送信を停止する
                sensorManager.unregisterListener(this)
            },15000)

    }


    var olddata =0.0
    var newdata = 0.0
    var diff = 0.0
    var oldtime = System.currentTimeMillis()
    var newtime = System.currentTimeMillis()
    var timeDiff = 0.0
    var oldSpeed = 0.0
    var speed = 0.0
    var oldAccel = 0.0


    // ローパスフィルターの係数(これは環境によって要調整。1に近づけるほど平滑化の度合いが大きくなる)
    var filterCoefficient = 0.92;
    var lowpassValue = 0.0;
    var highpassValue = 0.0;
    //センサーに何かしらのイベントが発生したときに呼ばれる
    override fun onSensorChanged(event: SensorEvent) {
        var sensorX: Float
        var sensorY: Float
        var sensorZ: Float
        // 全て
        if (event.sensor.type === Sensor.TYPE_LINEAR_ACCELERATION) {
            sensorX = event.values[0]
            if (sensorX<=0.05){
                sensorX = 0F
            }
            sensorY = event.values[1]
            if (sensorY<=0.05){
                sensorY = 0F
            }
            sensorZ = event.values[2]
            if (sensorZ<=0.05){
                sensorZ = 0F
            }

            //計測時間の間隔の計算
            oldtime = newtime
            newtime = System.currentTimeMillis()
            timeDiff = ((newtime - oldtime).toDouble()/1000)

            // ノルムの計算
            val nolm = sqrt(pow(sensorX.toDouble(), 2.0)+(pow(sensorY.toDouble(), 2.0))+(pow(sensorZ.toDouble(), 2.0)))

            //加速度の2重積分により移動距離を求める
            //ローパスフィルター(現在の値 = 係数 * ひとつ前の値 ＋ (1 - 係数) * センサの値)
            lowpassValue = lowpassValue * filterCoefficient + olddata * (1 - filterCoefficient)
            //ハイパスフィルター(センサの値 - ローパスフィルターの値)
            highpassValue = newdata - lowpassValue
            //速度計算(加速度を台形積分する)
            speed = ((highpassValue + oldAccel) * timeDiff) / 2 + speed;
            oldAccel = highpassValue;
            //変位計算(速度を台形積分する)
            diff = ((speed + oldSpeed) * timeDiff) / 2 + diff;
            oldSpeed = speed;


            olddata = newdata
            newdata = nolm

            val strTmp = """加速度センサー
                         X: $sensorX
                         Y: $sensorY
                         Z: $sensorZ
                         移動距離：　${diff.toDouble()} m
                         インターバル：　$timeDiff s
                         音の大きさ：　${SoundRevel.soundSize}"""

            val textView: TextView = findViewById(R.id.textView)
            textView.setText(strTmp)
            //追加
            val log:String = sensorX.toString().plus(",").plus(sensorY).plus(",").plus(sensorZ)
            OtherFileStorage.doLog(log)
            Log.d("log",log)
        }
    }

    //センサの精度が変更されたときに呼ばれる
    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }


}