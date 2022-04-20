package fr.newglace.notedesnazes.database.note.local

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import fr.newglace.notedesnazes.BuildConfig

class MyDatabaseHelper(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        val script = ("CREATE TABLE " + TABLE_NOTE + "("
                + COLUMN_NOTE_ID + " INTEGER PRIMARY KEY," + COLUMN_NOTE_TITLE + " TEXT,"
                + COLUMN_NOTE_CONTENT + " TEXT," + COLUMN_NOTE_FAVORITE + " TEXT,"
                + COLUMN_NOTE_PASSWORD + " TEXT," + COLUMN_NOTE_VISUAL + " TEXT,"
                + COLUMN_NOTE_FOLDER + " TEXT," + COLUMN_NOTE_POSITION + " TEXT,"
                + COLUMN_NOTE_FOLDER_POSITION + " TEXT," + COLUMN_NOTE_COLOR_FOLDER + " TEXT" + ")")
        db.execSQL(script)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NOTE")
        onCreate(db)
    }

    fun editNote(id: Int, note: Note) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_NOTE_TITLE, note.noteTitle)
        values.put(COLUMN_NOTE_CONTENT, note.noteContent)
        values.put(COLUMN_NOTE_FAVORITE, note.isFavorite.toString() + "")
        values.put(COLUMN_NOTE_PASSWORD, note.password)
        values.put(COLUMN_NOTE_VISUAL, note.visual)
        values.put(COLUMN_NOTE_FOLDER, note.folder)
        values.put(COLUMN_NOTE_POSITION, note.position)
        values.put(COLUMN_NOTE_FOLDER_POSITION, note.folderPosition)
        values.put(COLUMN_NOTE_COLOR_FOLDER, note.colorFolder)
        db.update(TABLE_NOTE, values, "$COLUMN_NOTE_ID = ?", arrayOf(id.toString()))
    }

    fun addNote(id: Int, note: Note) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_NOTE_ID, id)
        values.put(COLUMN_NOTE_TITLE, note.noteTitle)
        values.put(COLUMN_NOTE_CONTENT, note.noteContent)
        values.put(COLUMN_NOTE_FAVORITE, note.isFavorite.toString() + "")
        values.put(COLUMN_NOTE_PASSWORD, note.password)
        values.put(COLUMN_NOTE_VISUAL, note.visual)
        values.put(COLUMN_NOTE_FOLDER, note.folder)
        values.put(COLUMN_NOTE_POSITION, note.position)
        values.put(COLUMN_NOTE_FOLDER_POSITION, note.folderPosition)
        values.put(COLUMN_NOTE_COLOR_FOLDER, note.colorFolder)
        db.insert(TABLE_NOTE, null, values)
        db.close()
    }

    fun getNote(id: Int): Note {
        val db = this.readableDatabase
        @SuppressLint("Recycle") val cursor = db.query(TABLE_NOTE, arrayOf(COLUMN_NOTE_ID,
                COLUMN_NOTE_TITLE, COLUMN_NOTE_CONTENT, COLUMN_NOTE_FAVORITE, COLUMN_NOTE_PASSWORD, COLUMN_NOTE_VISUAL,
                COLUMN_NOTE_FOLDER, COLUMN_NOTE_POSITION, COLUMN_NOTE_FOLDER_POSITION, COLUMN_NOTE_COLOR_FOLDER), "$COLUMN_NOTE_ID=?", arrayOf(id.toString()), null, null, null, null)
        cursor?.moveToFirst()
        if (BuildConfig.DEBUG && cursor == null) {
            error("Assertion failed")
        }

        //db.delete(TABLE_NOTE, COLUMN_NOTE_ID + " != ?", new String[] { String.valueOf(999999) });
        //db.close();
        return Note(cursor!!.getString(1), cursor.getString(2), cursor.getString(3) == "true",
                cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7).toInt(), cursor.getString(8).toInt(), cursor.getString(9))
    }

    val notesCount: Int
        get() {
            val countQuery = "SELECT  * FROM $TABLE_NOTE"
            val db = this.readableDatabase
            val cursor = db.rawQuery(countQuery, null)
            val count = cursor.count
            cursor.close()
            return count
        }

    fun deleteNote(id: Int) {
        var i = id
        while (i + 1 < notesCount) {
            editNote(i, getNote(i + 1))
            i++
        }
        Log.d(".....................", ": " + id + " aka " + (notesCount - 1))
        val db = this.writableDatabase
        db.delete(TABLE_NOTE, "$COLUMN_NOTE_ID = ?", arrayOf((notesCount - 1).toString()))
        db.close()
    }

    companion object {
        private const val DATABASE_VERSION = 8
        private const val DATABASE_NAME = "IPv4"
        private const val TABLE_NOTE = "Note"
        private const val COLUMN_NOTE_ID = "Note_Id"
        private const val COLUMN_NOTE_TITLE = "Note_Title"
        private const val COLUMN_NOTE_CONTENT = "Note_Content"
        private const val COLUMN_NOTE_PASSWORD = "Note_Password"
        private const val COLUMN_NOTE_FAVORITE = "Note_Favorite"
        private const val COLUMN_NOTE_VISUAL = "Note_Visual"
        private const val COLUMN_NOTE_FOLDER = "Note_Folder"
        private const val COLUMN_NOTE_POSITION = "Note_Position"
        private const val COLUMN_NOTE_FOLDER_POSITION = "Note_Folder_Position"
        private const val COLUMN_NOTE_COLOR_FOLDER = "Note_Color_Folder"
    }
}