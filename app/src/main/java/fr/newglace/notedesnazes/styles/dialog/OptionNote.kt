package fr.newglace.notedesnazes.styles.dialog

import android.app.Activity
import android.app.Dialog
import android.os.Build
import android.util.DisplayMetrics
import android.widget.ImageView
import android.widget.Space
import android.widget.TextView
import androidx.annotation.RequiresApi
import fr.newglace.notedesnazes.R
import fr.newglace.notedesnazes.styles.Colors
import fr.newglace.notedesnazes.styles.ReSize

class OptionNote @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) constructor(activity: Activity) : Dialog(activity, R.style.Theme_AppCompat_DayNight_Dialog) {
    val delete: ImageView
    val password: ImageView
    val favorite: ImageView
    val favoriteColor: ImageView
    val imageView5: ImageView
    val lockText: TextView

    fun build() {
        show()
    }

    init {
        setContentView(R.layout.dialog_options_note)
        delete = findViewById(R.id.imageView4)
        password = findViewById(R.id.imageView3)
        favorite = findViewById(R.id.imageView2)
        favoriteColor = findViewById(R.id.textView4)
        imageView5 = findViewById(R.id.imageView5)
        lockText = findViewById(R.id.lock_text)
        val lockText2 = findViewById<TextView>(R.id.lock_text2)
        val lockText3 = findViewById<TextView>(R.id.lock_text3)
        val imageView5 = findViewById<ImageView>(R.id.imageView5)
        val imageView6 = findViewById<ImageView>(R.id.imageView6)
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
        size.reSizing(arrayOf(password, delete, favorite), passwordWidth, passwordHeight)
        size.reSizing(arrayOf(lockText, lockText2, lockText3), passwordWidth, (passwordHeight * 0.8).toInt(), true, true)
        size.reSizing(arrayOf(favoriteColor, imageView6, imageView5), (spaceSize * 1.5).toInt(), (spaceSize * 1.5).toInt())
        size.reSizing(imageView, (passwordWidth * 1.05).toInt(), (passwordHeight * 3.0 + spaceSize * 4).toInt())
        val colors = Colors(activity)
        val draw = activity.getDrawable(R.drawable.bg_notes)
        val draw2 = activity.getDrawable(R.drawable.bg_notes)
        colors.editColor(imageView, 0, draw2)
        colors.editColor(delete, 1, draw)
        colors.editColor(password, 1, draw)
        colors.editColor(favorite, 1, draw)
    }
}