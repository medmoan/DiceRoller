package com.diceroller.forboardgames

import android.content.Context
import android.content.res.TypedArray
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.annotation.DrawableRes
import kotlinx.coroutines.sync.Mutex
import java.lang.ref.Reference
import java.lang.ref.WeakReference


class Dice(
    imgView: ImageView,
    private val soundEvent: SoundEvent,
    private val message: Message
): Thread() {
    private val handler = Handler(Looper.getMainLooper())
    private val imageViewReference = WeakReference(imgView).get()
    private val context = imageViewReference?.context
    @Volatile var pickedDice = 1
        private set
    //var m = Mutex()

    private fun loadingDice(){
        var rotation = 0f

        handler.post {
            imageViewReference?.isEnabled = false
            soundEvent.startSound()
            message.dismissCancel()
        }
        for (i in 1..6){
            val diceDrRes = getDrawableResFromId(context, i)

            handler.post {
                diceDrRes?.run {
                    imageViewReference?.setImageResource(this)
                }
            }
            println("lylo $i $diceDrRes")
            try {
                sleep(100)
                rotation += 30f
                handler.post {
                    imageViewReference?.rotation = rotation
                }
                sleep(100)
                handler.post {
                    imageViewReference?.rotation = 0f
                }

            }
            catch (ie: InterruptedException){
                ie.printStackTrace()
                break
            }


        }
        handler.post {
            soundEvent.stopSound()
            println("lyly launched after interrupted")
        }
    }

    @DrawableRes
    private fun pickRandom(): Int? {
        val r = (1..6).random()
        println("lylo random : $r")
        pickedDice = r
        return getDrawableResFromId(context, r)
    }
    private fun setDice(@DrawableRes diceDrRes: Int){
        handler.post {
            imageViewReference?.let {
                it.setImageResource(diceDrRes)
                it.tag = pickedDice
                it.contentDescription = "${context?.getString(R.string.dice_face)} $pickedDice"
                it.isEnabled = true
                message.show()
            }

        }
    }
    override fun run() {
        if (interrupted()) return
        val diceChosenDrRes = pickRandom()
        loadingDice()
        diceChosenDrRes?.run {
            setDice(this)
        }

    }
    companion object {
        private const val PICKED_DICE = "pickedDice"

        @DrawableRes
        private fun getDrawableResFromId(context: Context?, resId: Int): Int?{
            context?.let {
                val icons: TypedArray = it.resources.obtainTypedArray(R.array.icons)
                val drawableRes = icons.getResourceId(resId - 1, 0)
                icons.recycle()
                return drawableRes
            }
            return null
        }
        fun setDiceStateBundle(imgView: ImageView, savedStateBundle: Bundle?){
            savedStateBundle?.let { state ->
                val pickedDiceNm = state.getInt(PICKED_DICE)
                if (pickedDiceNm == 0) return
                println("lylo $pickedDiceNm")
                val context = imgView.context
                val diceDrRes = getDrawableResFromId(context, pickedDiceNm)
                diceDrRes?.run {
                    imgView.setImageResource(this)
                    imgView.tag = pickedDiceNm
                    imgView.contentDescription = "${context.getString(R.string.dice_face)} $pickedDiceNm"
                }

            }
        }

        fun saveDiceStateBundle(imgView: ImageView, outStateBundle: Bundle) {
            when(val diceNm = imgView.tag as? Int){
                null, 1 -> return
                else -> {
                    outStateBundle.putInt(PICKED_DICE, diceNm)
                    println("lylo dicename $diceNm")
                }
            }
        }

    }
}