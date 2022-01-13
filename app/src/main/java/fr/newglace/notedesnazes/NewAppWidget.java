package fr.newglace.notedesnazes;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.RemoteViews;

import java.util.ArrayList;
import java.util.List;

import fr.newglace.notedesnazes.Activity.NoteActivity;
import fr.newglace.notedesnazes.Database.MyDatabaseHelper;
import fr.newglace.notedesnazes.Database.Note;

public class NewAppWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        MyDatabaseHelper db = new MyDatabaseHelper(context);
        Note note = db.getNote(0);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);

        views.setTextViewText(R.id.note_title_widget, note.getNoteTitle());
        views.setTextViewText(R.id.note_desc_widget, note.getNoteContent());
        views.setTextColor(R.id.note_desc_widget, 737683);

        appWidgetManager.updateAppWidget(appWidgetId, views);
        appWidgetManager.partiallyUpdateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        MyDatabaseHelper db = new MyDatabaseHelper(context);
        int noteNumber = 1;
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

        super.onReceive(context, intent);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
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

