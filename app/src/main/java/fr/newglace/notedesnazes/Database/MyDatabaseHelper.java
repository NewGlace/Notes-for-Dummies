package fr.newglace.notedesnazes.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 5;
    private static final String DATABASE_NAME = "IPv4";
    private static final String TABLE_NOTE = "Note";
    private static final String COLUMN_NOTE_ID ="Note_Id";
    private static final String COLUMN_NOTE_TITLE ="Note_Title";
    private static final String COLUMN_NOTE_CONTENT = "Note_Content";
    private static final String COLUMN_NOTE_PASSWORD = "Note_Password";
    private static final String COLUMN_NOTE_FAVORITE = "Note_Favorite";
    private static final String COLUMN_NOTE_VISUAL = "Note_Visual";
    private static final String COLUMN_NOTE_FOLDER = "Note_Folder";
    private static final String COLUMN_NOTE_POSITION = "Note_Position";
    private static final String COLUMN_NOTE_FOLDER_POSITION = "Note_Folder_Position";
    private static final String COLUMN_NOTE_COLOR_FOLDER = "Note_Color_Folder";

    public MyDatabaseHelper(Context context)  {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String script = "CREATE TABLE " + TABLE_NOTE + "("
                + COLUMN_NOTE_ID + " INTEGER PRIMARY KEY," + COLUMN_NOTE_TITLE + " TEXT,"
                + COLUMN_NOTE_CONTENT + " TEXT," + COLUMN_NOTE_FAVORITE + " TEXT,"
                + COLUMN_NOTE_PASSWORD + " TEXT,"+  COLUMN_NOTE_VISUAL + " TEXT,"
                +  COLUMN_NOTE_FOLDER + " TEXT,"+  COLUMN_NOTE_POSITION + " TEXT,"
                +  COLUMN_NOTE_FOLDER_POSITION + " TEXT,"+ COLUMN_NOTE_COLOR_FOLDER +"TEXT"+")";
        db.execSQL(script);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTE);
        onCreate(db);
    }

    public void editNote(int id, Note note) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE_TITLE, note.getNoteTitle());
        values.put(COLUMN_NOTE_CONTENT, note.getNoteContent());
        values.put(COLUMN_NOTE_FAVORITE, note.isFavorite()+"");
        values.put(COLUMN_NOTE_PASSWORD, note.getPassword());
        values.put(COLUMN_NOTE_VISUAL, note.getVisual());
        values.put(COLUMN_NOTE_FOLDER, note.getFolder());
        values.put(COLUMN_NOTE_POSITION, note.getPosition());
        values.put(COLUMN_NOTE_FOLDER_POSITION, note.getFolderPosition());
        values.put(COLUMN_NOTE_COLOR_FOLDER, note.getColorFolder());

        db.update(TABLE_NOTE, values, COLUMN_NOTE_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public void addNote(int id, Note note) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE_ID, id);
        values.put(COLUMN_NOTE_TITLE, note.getNoteTitle());
        values.put(COLUMN_NOTE_CONTENT, note.getNoteContent());
        values.put(COLUMN_NOTE_FAVORITE, note.isFavorite()+"");
        values.put(COLUMN_NOTE_PASSWORD, note.getPassword());
        values.put(COLUMN_NOTE_VISUAL, note.getVisual());
        values.put(COLUMN_NOTE_FOLDER, note.getFolder());
        values.put(COLUMN_NOTE_POSITION, note.getPosition());
        values.put(COLUMN_NOTE_FOLDER_POSITION, note.getFolderPosition());
        values.put(COLUMN_NOTE_COLOR_FOLDER, note.getColorFolder());

        db.insert(TABLE_NOTE, null, values);

        db.close();
    }

    public Note getNote(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        @SuppressLint("Recycle") Cursor cursor = db.query(TABLE_NOTE, new String[] { COLUMN_NOTE_ID,
                        COLUMN_NOTE_TITLE, COLUMN_NOTE_CONTENT, COLUMN_NOTE_FAVORITE, COLUMN_NOTE_PASSWORD,COLUMN_NOTE_VISUAL,
                COLUMN_NOTE_FOLDER, COLUMN_NOTE_POSITION, COLUMN_NOTE_FOLDER_POSITION, COLUMN_NOTE_COLOR_FOLDER}, COLUMN_NOTE_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        assert cursor != null;

        //db.delete(TABLE_NOTE, COLUMN_NOTE_ID + " != ?", new String[] { String.valueOf(999999) });
        //db.close();

        return new Note(cursor.getString(1), cursor.getString(2), cursor.getString(3).equals("true"),
                cursor.getString(4), cursor.getString(5), cursor.getString(6), Integer.parseInt(cursor.getString(7)),
                Integer.parseInt(cursor.getString(8)), cursor.getString(9));
    }

    public int getNotesCount() {
        String countQuery = "SELECT  * FROM " + TABLE_NOTE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

   public void deleteNote(int id) {
       for (int i = id; i+1 < getNotesCount(); i++) editNote(i,getNote(i+1));
       Log.d(".....................", ": "+id + " aka " +(getNotesCount()-1));

       SQLiteDatabase db = this.getWritableDatabase();
       db.delete(TABLE_NOTE, COLUMN_NOTE_ID + " = ?", new String[] { String.valueOf(getNotesCount()-1) });
       db.close();
    }
}