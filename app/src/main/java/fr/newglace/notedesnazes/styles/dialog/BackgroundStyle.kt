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

class BackgroundStyle @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) constructor(activity: Activity) : Dialog(activity, R.style.Theme_AppCompat_DayNight_Dialog) {
    val value1: ImageView
    val darkColor: ImageView
    val mediumColor: ImageView
    val value2: ImageView
    val lightColor: ImageView
    val imageView5: ImageView
    private val imageView: ImageView
    private val activity: Activity

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun editColors() {
        val colors = Colors(activity)
        val draw = activity.getDrawable(R.drawable.bg_notes)
        val draw2 = activity.getDrawable(R.drawable.bg_dialog)
        colors.editColor(imageView, 0, draw2)
        colors.editColor(value1, 1, draw)
        colors.editColor(darkColor, 1, draw)
        colors.editColor(lightColor, 1, draw)
        colors.editColor(value2, 1, draw)
        colors.editColor(mediumColor, 1, draw)
    }

    fun build() {
        show()
    }

    init {
        setContentView(R.layout.dialog_background_style)
        this.activity = activity
        value1 = findViewById(R.id.imageView4)
        darkColor = findViewById(R.id.imageView3)
        mediumColor = findViewById(R.id.create_qr_code)
        value2 = findViewById(R.id.load_qr_code_gallery)
        lightColor = findViewById(R.id.load_qr_code_camera)
        imageView5 = findViewById(R.id.imageView5)
        imageView = findViewById(R.id.imageView)
        val textView2 = findViewById<TextView>(R.id.textView2)
        val textView3 = findViewById<TextView>(R.id.textView3)
        val textView5 = findViewById<TextView>(R.id.textView5)
        val textView6 = findViewById<TextView>(R.id.textView6)
        val textView4 = findViewById<ImageView>(R.id.textView4)
        val imageView6 = findViewById<ImageView>(R.id.imageView6)
        val imageView7 = findViewById<ImageView>(R.id.imageView7)
        val imageView8 = findViewById<ImageView>(R.id.imageView8)
        val space = findViewById<Space>(R.id.space)
        val space2 = findViewById<Space>(R.id.space2)
        val space3 = findViewById<Space>(R.id.space3)
        val space4 = findViewById<Space>(R.id.space4)
        val space5 = findViewById<Space>(R.id.space5)
        val space6 = findViewById<Space>(R.id.space6)
        val space7 = findViewById<Space>(R.id.space7)
        val size = ReSize()
        val metrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(metrics)
        val phoneWidth = metrics.widthPixels
        val phoneHeight = metrics.heightPixels
        val passwordWidth = (phoneWidth / 5.0 * 3.0).toInt()
        val passwordHeight = (phoneHeight / 50.0 * 3.0 * 0.8).toInt()
        val spaceSize = (passwordWidth / 20.0).toInt().coerceAtMost((passwordHeight / 3.0).toInt())
        size.reSizing(arrayOf(space, space2, space3, space4, space5, space6, space7), spaceSize, spaceSize)
        size.reSizing(arrayOf(darkColor, value1, mediumColor, value2, lightColor), passwordWidth, passwordHeight)
        size.reSizing(arrayOf(textView2, textView3, textView5, textView6), passwordWidth, (passwordHeight * 0.8).toInt(), true, true)
        size.reSizing(arrayOf(textView4, imageView6, imageView7, imageView8, imageView5), (spaceSize * 1.5).toInt(), (spaceSize * 1.5).toInt())
        size.reSizing(imageView, (passwordWidth * 1.05).toInt(), (passwordHeight * 5.0 + spaceSize * 6).toInt())
        editColors()
    }
}