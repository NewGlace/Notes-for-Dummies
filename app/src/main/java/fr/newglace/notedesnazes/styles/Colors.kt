package fr.newglace.notedesnazes.styles

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.view.View
import fr.newglace.notedesnazes.database.config.DatabaseConfig

class Colors(context: Context?) {
    private val colorList = arrayOf("#0f0f22", "#181834", "#004596")
    fun editColors(context: Context?, color: String, position: Int) {
        val databaseConfig = DatabaseConfig(context)
        val config = databaseConfig.config
        config.setColor(color, position)
        databaseConfig.editConfig(config)
        colorList[position] = color
    }

    fun editView(color: String, position: Int) {
        colorList[position] = color
    }

    fun editColor(view: View, idColor: Int, draw: Drawable?) {
        val color = Color.parseColor(colorList[idColor])
        if (draw != null) {
            draw.setColorFilter(color, PorterDuff.Mode.LIGHTEN)
            view.background = draw
        } else {
            view.setBackgroundColor(color)
        }
    }

    fun getColor(idColor: Int): String {
        return colorList[idColor]
    }

    init {
        val databaseConfig = DatabaseConfig(context)
        val config = databaseConfig.config
        colorList[0] = config.backgroundDarkColor
        colorList[1] = config.backgroundColor
        colorList[2] = config.backgroundLightColor
    }
}