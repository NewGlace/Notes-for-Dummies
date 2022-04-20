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

class OptionNoteEdit @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) constructor(activity: Activity) : Dialog(activity, R.style.Theme_AppCompat_DayNight_Dialog) {
    val delete: ImageView
    val password: ImageView
    val qRCode: ImageView
    val loadGallery: ImageView
    val loadCamera: ImageView
    val imageView5: ImageView
    val lockText: TextView

    fun build() {
        show()
    }

    init {
        setContentView(R.layout.dialog_options_note_edit)
        delete = findViewById(R.id.imageView4)
        password = findViewById(R.id.imageView3)
        qRCode = findViewById(R.id.create_qr_code)
        loadGallery = findViewById(R.id.load_qr_code_gallery)
        loadCamera = findViewById(R.id.load_qr_code_camera)
        lockText = findViewById(R.id.lock)
        imageView5 = findViewById(R.id.imageView5)
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
        val imageView = findViewById<ImageView>(R.id.imageView)
        val size = ReSize()
        val metrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(metrics)
        val phoneWidth = metrics.widthPixels
        val phoneHeight = metrics.heightPixels
        val passwordWidth = (phoneWidth / 5.0 * 3.0).toInt()
        val passwordHeight = (phoneHeight / 50.0 * 3.0 * 0.8).toInt()
        val spaceSize = (passwordWidth / 20.0).toInt().coerceAtMost((passwordHeight / 3.0).toInt())
        size.reSizing(arrayOf(space, space2, space3, space4, space5, space6, space7), spaceSize, spaceSize)
        size.reSizing(arrayOf(password, delete, qRCode, loadGallery, loadCamera), passwordWidth, passwordHeight)
        size.reSizing(arrayOf(lockText, textView2, textView3, textView5, textView6), passwordWidth, (passwordHeight * 0.8).toInt(), true, true)
        size.reSizing(arrayOf(textView4, imageView6, imageView7, imageView8, imageView5), (spaceSize * 1.5).toInt(), (spaceSize * 1.5).toInt())
        size.reSizing(imageView, (passwordWidth * 1.05).toInt(), (passwordHeight * 5.0 + spaceSize * 6).toInt())
        val colors = Colors(activity)
        val draw = activity.getDrawable(R.drawable.bg_notes)
        val draw2 = activity.getDrawable(R.drawable.bg_dialog)
        colors.editColor(imageView, 0, draw2)
        colors.editColor(delete, 1, draw)
        colors.editColor(password, 1, draw)
        colors.editColor(loadCamera, 1, draw)
        colors.editColor(loadGallery, 1, draw)
        colors.editColor(qRCode, 1, draw)
    }
}