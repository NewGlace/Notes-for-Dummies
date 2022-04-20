package fr.newglace.notedesnazes.styles.dialog

import android.app.Activity
import android.app.Dialog
import android.os.Build
import android.util.DisplayMetrics
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.RequiresApi
import fr.newglace.notedesnazes.R
import fr.newglace.notedesnazes.styles.Colors
import fr.newglace.notedesnazes.styles.ReSize

class PasswordNote @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) constructor(activity: Activity) : Dialog(activity, R.style.Theme_AppCompat_DayNight_Dialog) {
    val password: EditText

    fun build() {
        show()
    }

    init {
        setContentView(R.layout.dialog_password_note)
        password = findViewById(R.id.password)
        val imageView = findViewById<ImageView>(R.id.imageView)
        val size = ReSize()
        val metrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(metrics)
        val phoneWidth = metrics.widthPixels
        val phoneHeight = metrics.heightPixels
        val passwordWidth = (phoneWidth / 5.0 * 3.0).toInt()
        val passwordHeight = (phoneHeight / 50.0 * 3.0 * 0.8).toInt()
        size.reSizing(password, passwordWidth, passwordHeight, true, true)
        size.reSizing(imageView, (passwordWidth * 1.05).toInt(), (passwordHeight * 1.05).toInt())
        val colors = Colors(activity)
        val draw = activity.getDrawable(R.drawable.bg_notes)
        val draw2 = activity.getDrawable(R.drawable.bg_notes)
        colors.editColor(imageView, 0, draw2)
        colors.editColor(password, 1, draw)
    }
}