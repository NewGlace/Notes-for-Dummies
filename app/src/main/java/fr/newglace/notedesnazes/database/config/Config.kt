package fr.newglace.notedesnazes.database.config

import java.io.Serializable

class Config(val language: String,
             val username: String,
             val password: String,
             var backgroundDarkColor: String,
             var backgroundColor: String,
             var backgroundLightColor: String,
             val backgroundImage: String) : Serializable {

    fun setColor(hex: String, position: Int) {
        if (position == 0) backgroundDarkColor = hex else if (position == 1) backgroundColor = hex else backgroundLightColor = hex
    }
}
