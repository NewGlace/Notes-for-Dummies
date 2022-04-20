package fr.newglace.notedesnazes.styles.dialog

import android.app.Activity
import android.app.Dialog
import android.os.Build
import android.util.DisplayMetrics
import android.widget.ImageView
import androidx.annotation.RequiresApi
import fr.newglace.notedesnazes.R
import fr.newglace.notedesnazes.styles.Colors
import fr.newglace.notedesnazes.styles.ReSize

class QRCode @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) constructor(activity: Activity) : Dialog(activity, R.style.Theme_AppCompat_DayNight_Dialog) {
    val image: ImageView

    fun build() {
        show()
    }

    init {
        setContentView(R.layout.dialog_qrcode_image)
        image = findViewById(R.id.qr_code)
        val imageView = findViewById<ImageView>(R.id.imageView)
        val size = ReSize()
        val metrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(metrics)
        val phoneWidth = metrics.widthPixels
        val phoneHeight = metrics.heightPixels
        val qrCodeValue = Math.min((phoneWidth / 2.0).toInt(), (phoneHeight / 3.0).toInt())
        size.reSizing(imageView, (qrCodeValue * 1.2).toInt(), (qrCodeValue * 1.2).toInt())
        size.reSizing(image, qrCodeValue, qrCodeValue)
        val colors = Colors(activity)
        val draw2 = activity.getDrawable(R.drawable.bg_notes)
        colors.editColor(imageView, 0, draw2)
    }
}