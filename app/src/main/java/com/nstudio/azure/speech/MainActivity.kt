package com.nstudio.azure.speech

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.Manifest.permission.INTERNET
import android.Manifest.permission.RECORD_AUDIO
import android.annotation.SuppressLint
import android.util.Log
import androidx.core.app.ActivityCompat
import com.microsoft.cognitiveservices.speech.ResultReason
import com.microsoft.cognitiveservices.speech.SpeechConfig
import com.microsoft.cognitiveservices.speech.SpeechRecognitionResult
import com.microsoft.cognitiveservices.speech.SpeechRecognizer
import kotlinx.android.synthetic.main.activity_main.*



@SuppressLint("SetTextI18n")
class MainActivity : AppCompatActivity() {

    private val tag = "MainActivity"

    private val speechSubscriptionKey = "55a097f92d044a2f8594fb322e8ce16b"
    private val serviceRegion = "westus"
    private val config = SpeechConfig.fromSubscription(speechSubscriptionKey, serviceRegion)!!
    private var isRunning = false

    private val runnable = Runnable {
        Log.e(tag,"Start Recognition >> ")
        Thread.sleep(2000)
        val reco = SpeechRecognizer(config)
        val task = reco.recognizeOnceAsync()!!
        val result = task.get()!!
        Log.e(tag,"Got Result >> ")
        showResult(result)
        reco.close()
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val requestCode = 5 // unique code for the permission request
        ActivityCompat.requestPermissions(this@MainActivity, arrayOf(RECORD_AUDIO, INTERNET), requestCode)

        btn.setOnClickListener {
            if (isRunning){
                isRunning = false
                btn.text = "Start Recognition"
            }else{
                Log.d(tag,"Recognition Stopped >> ")
                isRunning = true
                btn.text = "Stop Recognition"
                Thread(runnable).start()
            }

        }

    }


    override fun onPause() {
        super.onPause()
        isRunning = false
    }

    private fun showResult(result: SpeechRecognitionResult) {
        Log.d(tag,"Result >> ")
        runOnUiThread {
            if (result.reason === ResultReason.RecognizedSpeech) {
                tvResult.text = result.toString()

            } else {
                tvResult.text = "Error recognizing. Did you update the subscription info?" + System.lineSeparator() + result.toString()
            }
        }
        if (isRunning){
            Thread(runnable).start()
        }

    }


}
