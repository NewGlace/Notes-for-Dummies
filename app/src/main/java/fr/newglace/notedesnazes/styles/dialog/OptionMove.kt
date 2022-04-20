package fr.newglace.notedesnazes.styles.dialog

import android.app.Activity
import android.app.Dialog
import android.os.Build
import android.util.DisplayMetrics
import android.widget.ImageView
import android.widget.Space
import android.widget.Spinner
import android.widget.TextView
import androidx.annotation.RequiresApi
import fr.newglace.notedesnazes.R
import fr.newglace.notedesnazes.styles.Colors
import fr.newglace.notedesnazes.styles.ReSize

class OptionMove @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) constructor(activity: Activity) : Dialog(activity, R.style.Theme_AppCompat_DayNight_Dialog) {
    val newFolder: ImageView
    val backFolder: ImageView
    val moveFolder: Spinner

    fun build() {
        show()
    }

    init {
        setContentView(R.layout.dialog_options_move)
        backFolder = findViewById(R.id.imageView2)
        newFolder = findViewById(R.id.imageView3)
        moveFolder = findViewById(R.id.spinner)
        val lockText2 = findViewById<TextView>(R.id.lock_text2)
        val lockText = findViewById<TextView>(R.id.lock_text)
        val imageView4 = findViewById<ImageView>(R.id.imageView4)
        val imageView5 = findViewById<ImageView>(R.id.imageView5)
        val imageView6 = findViewById<ImageView>(R.id.imageView6)
        val textView4 = findViewById<ImageView>(R.id.textView4)
        val space = findViewById<Space>(R.id.space)
        val space2 = findViewById<Space>(R.id.space2)
        val space3 = findViewById<Space>(R.id.space3)
        val space4 = findViewById<Space>(R.id.space4)
        val space5 = findViewById<Space>(R.id.space5)
        val imageView = findViewById<ImageView>(R.id.imageView)
        val size = ReSize()
        val metrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(metrics)
        val phoneWidth = metrics.widthPixels
        val phoneHeight = metrics.heightPixels
        val passwordWidth = (phoneWidth / 5.0 * 3.0).toInt()
        val passwordHeight = (phoneHeight / 50.0 * 3.0 * 0.8).toInt()
        val spaceSize = (passwordWidth / 20.0).toInt().coerceAtMost((passwordHeight / 3.0).toInt())
        size.reSizing(arrayOf(space, space2, space3, space4, space5), spaceSize, spaceSize)
        size.reSizing(arrayOf(imageView4, newFolder, backFolder), passwordWidth, passwordHeight)
        size.reSizing(arrayOf(lockText2, lockText, moveFolder), passwordWidth - (spaceSize * 3.5).toInt(), (passwordHeight * 0.8).toInt(), true, true)
        size.reSizing(arrayOf(textView4, imageView6, imageView5), (spaceSize * 1.5).toInt(), (spaceSize * 1.5).toInt())
        size.reSizing(imageView, (passwordWidth * 1.05).toInt(), (passwordHeight * 3.0 + spaceSize * 4).toInt())
        val colors = Colors(activity)
        val draw = activity.getDrawable(R.drawable.bg_notes)
        val draw2 = activity.getDrawable(R.drawable.bg_notes)
        colors.editColor(imageView, 0, draw2)
        colors.editColor(backFolder, 1, draw)
        colors.editColor(newFolder, 1, draw)
        colors.editColor(imageView4, 1, draw)
        colors.editColor(backFolder, 1, draw)
    }
}