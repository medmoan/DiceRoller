package com.diceroller.forboardgames

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.appodeal.ads.Appodeal
import com.appodeal.ads.initializing.ApdInitializationError
import com.appodeal.ads.utils.Log
import com.diceroller.forboardgames.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), SoundEvent, Message {
    private lateinit var activityMain: ActivityMainBinding
    private var dice: Dice? = null
    private var playDiceRollingSound: MediaPlayer? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        activityMain = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMain.root)
        Dice.setDiceStateBundle(activityMain.dic, savedInstanceState)
        activityMain.dic.setOnClickListener {
            playDiceRollingSound = MediaPlayer.create(this, R.raw.dicerolling)
            dice = Dice(activityMain.dic, this, this).apply {
                start()
            }
        }
        setUpAppodealSDK()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Dice.saveDiceStateBundle(activityMain.dic, outState)
    }


    override fun onPause() {
        super.onPause()
        dice?.let {
            if (it.isAlive){
                println("lylo thread interrupted")
                it.interrupt()
                return
            }
        }

        println("lylo dice is null or not alive")
    }

    override fun onDestroy() {
        super.onDestroy()
        dice = null
        dismissCancel()
        println("lylo destroyed!")
    }

    override fun startSound() {
        playDiceRollingSound!!.start()
    }
    override fun stopSound() {
        try {
            playDiceRollingSound?.let {
                if (it.isPlaying) it.stop()
                it.release()
            }
            playDiceRollingSound = null
        }
        catch (e: Exception){
            e.printStackTrace()
        }
    }
    override fun show() {
        
        CustomMessage.showSnackBar(activityMain.root,
            "${getString(R.string.dice_face)} ${dice?.pickedDice}")
    }

    override fun dismissCancel() {
        CustomMessage.cancelSnackBar()
    }
    private fun setUpAppodealSDK() {
        Appodeal.setLogLevel(Log.LogLevel.verbose)
        Appodeal.setTesting(true)
        Appodeal.initialize(
            this,
            APP_KEY,
            Appodeal.BANNER
        ) { errors: List<ApdInitializationError>? ->
            errors?.forEach {
                Log.debug(TAG, "onInitializationFinished: ", it.message)
            }
        }
        Appodeal.show(this@MainActivity, Appodeal.BANNER_TOP, PLACEMENT)
    }

    companion object {
        private const val PLACEMENT = "default"
        private const val APP_KEY = "bab1a9fc9712f29c37154b522147c8844e6462595d32ad08"
        private const val TAG = "MainActivity"
    }



}
