package fr.newglace.notedesnazes.database.config

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import fr.newglace.notedesnazes.BuildConfig

class DatabaseConfig(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        val script = ("CREATE TABLE " + TABLE_CONFIG + "("
                + COLUMN_CONFIG_ID + " INTEGER PRIMARY KEY," + COLUMN_CONFIG_LANGUAGE + " TEXT,"
                + COLUMN_CONFIG_USERNAME + " TEXT," + COLUMN_CONFIG_BACKGROUND_DARK_COLOR + " TEXT,"
                + COLUMN_CONFIG_PASSWORD + " TEXT," + COLUMN_CONFIG_BACKGROUND_COLOR + " TEXT,"
                + COLUMN_CONFIG_BACKGROUND_LIGHT_COLOR + " TEXT," + COLUMN_CONFIG_BACKGROUND_IMAGE + " TEXT" + ")")
        db.execSQL(script)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CONFIG")
        onCreate(db)
    }

    fun editConfig(config: Config) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_CONFIG_LANGUAGE, config.language)
        values.put(COLUMN_CONFIG_USERNAME, config.username)
        values.put(COLUMN_CONFIG_PASSWORD, config.password)
        values.put(COLUMN_CONFIG_BACKGROUND_DARK_COLOR, config.backgroundDarkColor)
        values.put(COLUMN_CONFIG_BACKGROUND_COLOR, config.backgroundColor)
        values.put(COLUMN_CONFIG_BACKGROUND_LIGHT_COLOR, config.backgroundLightColor)
        values.put(COLUMN_CONFIG_BACKGROUND_IMAGE, config.backgroundImage)
        db.update(TABLE_CONFIG, values, "$COLUMN_CONFIG_ID = ?", arrayOf(0.toString()))
    }

    private fun createConfig() {
        val db2 = this.writableDatabase
        val countQuery = "SELECT  * FROM $TABLE_CONFIG"
        val cursor = db2.rawQuery(countQuery, null)
        val count = cursor.count
        cursor.close()
        if (count == 0) {
            Log.d("[NFD] Note for dummies", "Config created !")
            val db = this.writableDatabase
            val values = ContentValues()
            values.put(COLUMN_CONFIG_ID, 0)
            values.put(COLUMN_CONFIG_LANGUAGE, "FR_fr")
            values.put(COLUMN_CONFIG_USERNAME, "none")
            values.put(COLUMN_CONFIG_PASSWORD, "none")
            values.put(COLUMN_CONFIG_BACKGROUND_DARK_COLOR, "#0f0f22")
            values.put(COLUMN_CONFIG_BACKGROUND_COLOR, "#181834")
            values.put(COLUMN_CONFIG_BACKGROUND_LIGHT_COLOR, "#004596")
            values.put(COLUMN_CONFIG_BACKGROUND_IMAGE, "none")
            db.insert(TABLE_CONFIG, null, values)
            db.close()
        }
    }

    val config: Config
        get() {
            createConfig()
            val db = this.readableDatabase
            @SuppressLint("Recycle") val cursor = db.query(TABLE_CONFIG, arrayOf(COLUMN_CONFIG_ID,
                    COLUMN_CONFIG_LANGUAGE, COLUMN_CONFIG_USERNAME, COLUMN_CONFIG_PASSWORD, COLUMN_CONFIG_BACKGROUND_DARK_COLOR, COLUMN_CONFIG_BACKGROUND_COLOR,
                    COLUMN_CONFIG_BACKGROUND_LIGHT_COLOR, COLUMN_CONFIG_BACKGROUND_IMAGE), "$COLUMN_CONFIG_ID=?", arrayOf(0.toString()), null, null, null, null)
            cursor?.moveToFirst()
            if (BuildConfig.DEBUG && cursor == null) {
                error("Assertion failed")
            }
            return Config(cursor!!.getString(1), cursor.getString(2), cursor.getString(3),
                    cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7))
        }

    companion object {
        private const val DATABASE_VERSION = 8
        private const val DATABASE_NAME = "IPv4"
        private const val TABLE_CONFIG = "Config"
        private const val COLUMN_CONFIG_ID = "Config_Id"
        private const val COLUMN_CONFIG_LANGUAGE = "Config_Language"
        private const val COLUMN_CONFIG_USERNAME = "Config_Username"
        private const val COLUMN_CONFIG_PASSWORD = "Config_Password"
        private const val COLUMN_CONFIG_BACKGROUND_DARK_COLOR = "Config_Background_Dark_Color"
        private const val COLUMN_CONFIG_BACKGROUND_COLOR = "Config_Background_Color"
        private const val COLUMN_CONFIG_BACKGROUND_LIGHT_COLOR = "Config_Background_Light_Color"
        private const val COLUMN_CONFIG_BACKGROUND_IMAGE = "Config_Background_Image"
    }
}