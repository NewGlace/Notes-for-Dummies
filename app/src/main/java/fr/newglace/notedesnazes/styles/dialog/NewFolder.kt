package fr.newglace.notedesnazes.styles.dialog

import android.app.Activity
import android.app.Dialog
import android.os.Build
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.RequiresApi
import fr.newglace.notedesnazes.R
import fr.newglace.notedesnazes.styles.Colors

class NewFolder @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) constructor(activity: Activity) : Dialog(activity, R.style.Theme_AppCompat_DayNight_Dialog) {
    val newFolder: EditText

    fun build() {
        show()
    }

    init {
        setContentView(R.layout.dialog_new_folder)
        newFolder = findViewById(R.id.new_folder)
        val imageView = findViewById<ImageView>(R.id.imageView)
        val colors = Colors(activity)
        val draw = activity.getDrawable(R.drawable.bg_notes)
        val draw2 = activity.getDrawable(R.drawable.bg_notes)
        colors.editColor(imageView, 0, draw2)
        colors.editColor(newFolder, 1, draw)
    }
}