package dev.leonardom.detectposesmlandroid

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment

fun Activity.displayToast(message: String){
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Fragment.displayToast(context: Context, message: String){
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}