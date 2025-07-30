package com.diceroller.forboardgames

import android.view.View

import com.google.android.material.snackbar.Snackbar

object CustomMessage {
    private var snackbar: Snackbar? = null
    fun showSnackBar(view: View, message: String){
        snackbar = Snackbar.make(
            view,
            message,
            Snackbar.LENGTH_LONG)
        snackbar!!.show()
    }
    fun cancelSnackBar(){
        if (snackbar != null){
            snackbar!!.dismiss()
            snackbar = null
        }
    }
}