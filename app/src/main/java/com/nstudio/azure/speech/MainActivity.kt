package com.nstudio.azure.speech

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.Manifest.permission.INTERNET
import android.Manifest.permission.RECORD_AUDIO
import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Handler
import androidx.core.app.ActivityCompat
import com.microsoft.cognitiveservices.speech.ResultReason
import com.microsoft.cognitiveservices.speech.SpeechConfig
import com.microsoft.cognitiveservices.speech.SpeechRecognitionResult
import com.microsoft.cognitiveservices.speech.SpeechRecognizer
import kotlinx.android.synthetic.main.activity_main.*



@SuppressLint("SetTextI18n")
class MainActivity : AppCompatActivity() {

    private val onVoiceResultListener = object : OnVoiceResultListener {
        override fun onResult(result: SpeechRecognitionResult) {
           showResult(result)
        }
    }


    private val recognizeTask = RecognizeTask(onVoiceResultListener)

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val requestCode = 5 // unique code for the permission request
        ActivityCompat.requestPermissions(this@MainActivity, arrayOf(RECORD_AUDIO, INTERNET), requestCode)

        recognizeTask.execute()
    }

    private fun showResult(result: SpeechRecognitionResult) {
        if (result.reason === ResultReason.RecognizedSpeech) {
            tvResult.text = result.toString()

            Handler().postDelayed({ recognizeTask.execute() },1000)

        } else {
            tvResult.text = "Error recognizing. Did you update the subscription info?" + System.lineSeparator() + result.toString()
        }
    }

    override fun onDestroy() {
        recognizeTask.isCanceled = true
        super.onDestroy()
    }

    private class RecognizeTask(val onVoiceResultListener: OnVoiceResultListener) : AsyncTask<Void,Void,SpeechRecognitionResult>(){

        private val speechSubscriptionKey = "55a097f92d044a2f8594fb322e8ce16b"
        private val serviceRegion = "westus"
        var isCanceled = false

        override fun doInBackground(vararg p0: Void?): SpeechRecognitionResult {
            val config = SpeechConfig.fromSubscription(speechSubscriptionKey, serviceRegion)!!
            val reco = SpeechRecognizer(config)
            val task = reco.recognizeOnceAsync()!!

            val result = task.get()!!

            reco.close()

            return result

        }

        @SuppressLint("SetTextI18n")
        override fun onPostExecute(result: SpeechRecognitionResult?) {
            super.onPostExecute(result)

            if (isCanceled){
                return
            }

            try {
                if (result != null) {
                    onVoiceResultListener.onResult(result)
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    private interface OnVoiceResultListener{
        fun onResult(result: SpeechRecognitionResult)
    }

}
