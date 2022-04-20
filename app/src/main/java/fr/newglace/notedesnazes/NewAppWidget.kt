package fr.newglace.notedesnazes

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import fr.newglace.notedesnazes.database.note.local.MyDatabaseHelper

class NewAppWidget : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        val db = MyDatabaseHelper(context)
        val noteNumber = 1
        if (db.notesCount > noteNumber && noteNumber != -1) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val views = RemoteViews(context.packageName, R.layout.new_app_widget)
            val watchWidget = ComponentName(context, NewAppWidget::class.java)
            val note = db.getNote(noteNumber)
            views.setTextViewText(R.id.note_title, note.noteTitle)
            views.setTextViewText(R.id.note_desc, note.noteContent)
            appWidgetManager.updateAppWidget(watchWidget, views)
            updateAppWidget(context, appWidgetManager, 0)
        }
        super.onReceive(context, intent)
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    companion object {
        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val db = MyDatabaseHelper(context)
            val note = db.getNote(0)
            val views = RemoteViews(context.packageName, R.layout.new_app_widget)
            views.setTextViewText(R.id.note_title_widget, note.noteTitle)
            views.setTextViewText(R.id.note_desc_widget, note.noteContent)
            views.setTextColor(R.id.note_desc_widget, 737683)
            appWidgetManager.updateAppWidget(appWidgetId, views)
            appWidgetManager.partiallyUpdateAppWidget(appWidgetId, views)
        }
    }
}


/*
*     private static int noteNumber = 0;
    private static RemoteViews views;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        update(context, appWidgetManager, appWidgetId);
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        MyDatabaseHelper db = new MyDatabaseHelper(context);

        views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
        if (db.getNotesCount() > noteNumber && noteNumber != -1) {
            Note note = db.getNote(noteNumber);

            views.setTextViewText(R.id.note_title, note.getNoteTitle());
            views.setTextViewText(R.id.note_desc, note.getNoteContent());

            appWidgetManager.updateAppWidget(appWidgetId, views);
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        MyDatabaseHelper db = new MyDatabaseHelper(context);

        if (db.getNotesCount() <= noteNumber) noteNumber = -1;

        for (int appWidgetId : appWidgetIds) {
            Intent intent = new Intent(context, NoteActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("id", noteNumber);
            intent.putExtras(bundle);

            //          PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
            //      PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
//            views.setOnClickPendingIntent(R.id.note_desc, pendingIntent);

            views.setOnClickPendingIntent(R.id.button_down, nextNote(context));
            views.setOnClickPendingIntent(R.id.button_up, previousNote(context));

            appWidgetManager.updateAppWidget(appWidgetId, views);
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {

    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        Log.d("TAG", "onAppWidgetOptionsChanged: ");
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("nextNote") || intent.getAction().equals("nextNote")) {
            MyDatabaseHelper db = new MyDatabaseHelper(context);
            noteNumber = 1;
            if (db.getNotesCount() > noteNumber && noteNumber != -1) {
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
                ComponentName watchWidget = new ComponentName(context, NewAppWidget.class);

                Note note = db.getNote(noteNumber);
                views.setTextViewText(R.id.note_title, note.getNoteTitle());
                views.setTextViewText(R.id.note_desc, note.getNoteContent());

                appWidgetManager.updateAppWidget(watchWidget, views);
                updateAppWidget(context, appWidgetManager, 0);
            }
        }
    }*/