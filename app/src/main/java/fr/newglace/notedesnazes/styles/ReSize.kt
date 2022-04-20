package fr.newglace.notedesnazes.styles

import android.util.TypedValue
import android.view.View
import android.widget.TextView

class ReSize() {
    fun reSizing(view: View, width: Int, height: Int, vararg options: Boolean) {
        val params = view.layoutParams
        params.width = if (width == -1) params.width else width
        params.height = if (height == -1) params.height else height
        view.layoutParams = params
        if (view is TextView && options.isNotEmpty() && options[0]) {
            if (options.size > 1) {
                view.setTextSize(TypedValue.COMPLEX_UNIT_PX, ((height / 2.2).toFloat()))
            } else view.setTextSize(TypedValue.COMPLEX_UNIT_PX, ((height / 1.2).toFloat()))
        }
    }

    fun reSizing(view: Array<View>, width: Int, height: Int, vararg options: Boolean) {
        for (v in view) {
            reSizing(v, width, height, *options)
        }
    }
}