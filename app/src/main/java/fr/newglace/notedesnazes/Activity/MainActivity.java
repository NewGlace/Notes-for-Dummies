package fr.newglace.notedesnazes.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.KeyboardShortcutGroup;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Space;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import fr.newglace.notedesnazes.Database.MyDatabaseHelper;
import fr.newglace.notedesnazes.Database.Note;
import fr.newglace.notedesnazes.Styles.Colors;
import fr.newglace.notedesnazes.Styles.Dialog.NewFolder;
import fr.newglace.notedesnazes.Styles.Dialog.OptionMove;
import fr.newglace.notedesnazes.Styles.Notes.ManageNotes;
import fr.newglace.notedesnazes.R;
import fr.newglace.notedesnazes.Styles.Notes.noteArray;
import fr.newglace.notedesnazes.Styles.reSize2;

public class MainActivity extends AppCompatActivity {
    //text
    private ImageView search, dustbin, favorite, selectAll, move, falseSearch, option;
    private String selects = "!", selectString = selects, selectFolder = "";
    private MyDatabaseHelper db = new MyDatabaseHelper(this);
    private Space space3, space2, space, space4, space5, space6;
    private TextView title, footer, cross, button, textView2;
    private reSize2 size = new reSize2();
    private final int[] check = {0, 0};
    private boolean select = false;
    private EditText searchBar;
    private ListView notes1;

    public EditText getSearchBar() {
        return searchBar;
    }
    public String getSelectFolder() {
        return selectFolder;
    }
    public boolean getSelect(String id) {
        return selects.contains("!"+id+"!");
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void addSelect(String id) {
        if (!selects.contains("!"+id+"!")) selects += id+"!";

        if (id.contains("f-")) {
            int color = Color.parseColor("#606060");
            move.setColorFilter(color);
        }

        if (selects.equals(selectString)) {
            Drawable drawable = getDrawable(R.drawable.select_all2);
            if (selectAll.getDrawable() != drawable) selectAll.setImageDrawable(drawable);
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void removeSelect(String id) {
        selects = selects.replace(id+"!", "");

        Drawable drawable = getDrawable(R.drawable.select_all);
        if (!selects.equals(selectString)) selectAll.setImageDrawable(drawable);

        if (!selects.contains("f-")) {
            int color = Color.parseColor("#ffffff");
            move.setColorFilter(color);
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setSelect(boolean select) {
        this.select = select;

        if (select) {
            footer.setVisibility(View.VISIBLE);
            dustbin.setVisibility(View.VISIBLE);
            favorite.setVisibility(View.VISIBLE);
            selectAll.setVisibility(View.VISIBLE);
            move.setVisibility(View.VISIBLE);

            cross.setVisibility(View.INVISIBLE);
            button.setVisibility(View.INVISIBLE);

            if (!selects.contains("f-")) {
                int color = Color.parseColor("#ffffff");
                move.setColorFilter(color);
            }
        } else {
            footer.setVisibility(View.INVISIBLE);
            dustbin.setVisibility(View.INVISIBLE);
            favorite.setVisibility(View.INVISIBLE);
            selectAll.setVisibility(View.INVISIBLE);
            move.setVisibility(View.INVISIBLE);

            cross.setVisibility(View.VISIBLE);
            button.setVisibility(View.VISIBLE);
            selects = "!";

            Drawable drawable = getDrawable(R.drawable.select_all);
            if (selectAll.getDrawable() != drawable) selectAll.setImageDrawable(drawable);
        }
    }
    private InputFilter filter = (charSequence, i, i1, spanned, i2, i3) -> {
        String blockCharSet = "\n";
        if (charSequence != null && blockCharSet.contains(charSequence.toString())) return "";
        else if (charSequence != null && charSequence.toString().length() > 16) return charSequence.toString().subSequence(0, 16);
        else return null;
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBackPressed() {
        if (check[0] != 0) {
            check[0] = 0;
            searchBar.setText("");
            searchBar.setVisibility(View.INVISIBLE);
            search.animate().setDuration(500).x(falseSearch.getX()).withEndAction(() -> title.setVisibility(View.VISIBLE));
        } else if (select) {
            noteList(searchBar.getText().toString(), selectFolder);
            footer.setVisibility(View.INVISIBLE);
            dustbin.setVisibility(View.INVISIBLE);
            favorite.setVisibility(View.INVISIBLE);
            selectAll.setVisibility(View.INVISIBLE);
            move.setVisibility(View.INVISIBLE);

            cross.setVisibility(View.VISIBLE);
            button.setVisibility(View.VISIBLE);
            selects = "!";
            select = false;

            noteList("", selectFolder);
            Drawable drawable = getDrawable(R.drawable.select_all);
            if (selectAll.getDrawable() != drawable) selectAll.setImageDrawable(drawable);
        } else if (selectFolder.length() != 0) {
            getFolder("");
        } else super.onBackPressed();
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        falseSearch = findViewById(R.id.false_magnifying_glass);
        notes1 = findViewById(R.id.note_list);
        button = findViewById(R.id.add_notes);
        title = findViewById(R.id.textView);
        search = findViewById(R.id.magnifying_glass);
        searchBar = findViewById(R.id.search_bar);
        footer = findViewById(R.id.textView4);
        dustbin = findViewById(R.id.dustbin);
        favorite = findViewById(R.id.favorite);
        selectAll = findViewById(R.id.select_all);
        move = findViewById(R.id.move);
        cross = findViewById(R.id.cross);
        space2 = findViewById(R.id.space2);
        space3 = findViewById(R.id.space3);
        space4 = findViewById(R.id.space4);
        space5 = findViewById(R.id.space5);
        space6 = findViewById(R.id.space6);
        textView2 = findViewById(R.id.textView2);
        space = findViewById(R.id.space);
        option = findViewById(R.id.options);
        TextView textView7 = findViewById(R.id.textView7);

        final Colors colors = new Colors();
        colors.editColor(footer, 1, null);
        colors.editColor(textView2, 1, null);
        colors.editColor(textView7, 0, null);
        Drawable draw2 = getDrawable(R.drawable.button);
        colors.editColor(button, 2, draw2);
        Window window = getWindow();
        window.setNavigationBarColor(Color.parseColor(colors.getColor(1)));
        window.setStatusBarColor(Color.parseColor(colors.getColor(1)));
        reSize();
        final String[] oldSearch = {""};
        noteList(oldSearch[0], selectFolder);

        move.setOnClickListener(view -> {
            if (selects.length() > 1 && !selects.contains("f-")) {
                OptionMove dialog = new OptionMove(this);
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.build();

                dialog.getBackFolder().setOnClickListener(v -> {
                    dialog.dismiss();

                    for (String id : selects.subSequence(1, selects.length() - 1).toString().split("!")) {
                        int i = Integer.parseInt(id);

                        db.editNote(i, new Note(db.getNote(i).getNoteTitle(), db.getNote(i).getNoteContent(), db.getNote(i).isFavorite(), db.getNote(i).getPassword(), db.getNote(i).getVisual(), "",db.getNote(i).getPosition(), 0, db.getNote(i).getColorFolder()));
                    }
                    setSelect(false);
                    noteList(searchBar.getText().toString(), selectFolder);
                });
                dialog.getNewFolder().setOnClickListener(v -> {
                    dialog.dismiss();

                    NewFolder d = new NewFolder(this);
                    Objects.requireNonNull(d.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    d.build();

                    EditText folder = d.getNewFolder();
                    folder.setFilters(new InputFilter[] {filter});
                    folder.setOnEditorActionListener((textView, i1, keyEvent) -> {
                        if (folder.getText().toString().length() != 0) {
                            selectFolder = folder.getText().toString();

                            for (String id : selects.subSequence(1, selects.length() - 1).toString().split("!")) {
                                int i = Integer.parseInt(id);

                                db.editNote(i, new Note(db.getNote(i).getNoteTitle(), db.getNote(i).getNoteContent(), db.getNote(i).isFavorite(), db.getNote(i).getPassword(), db.getNote(i).getVisual(), selectFolder,db.getNote(i).getPosition(), 0, db.getNote(i).getColorFolder()));
                            }
                            setSelect(false);
                            noteList(searchBar.getText().toString(), selectFolder);
                            d.dismiss();
                        }
                        return false;
                    });
                });
            }
        });
        dustbin.setOnClickListener(view -> {
            if (selects.length() > 1) {
                int deleteNumber = 0;
                int dbAll = db.getNotesCount();
                for (int i = 0; i < dbAll; i++) {
                    if (selects.contains("!"+i+"!")) {
                        db.deleteNote(i- deleteNumber);
                        deleteNumber++;
                    }
                }

                String[] arrayString = selects.substring(1, selects.length()-1).split("!");
                for (int i = arrayString.length-1 ; i > -1 ; i--) {
                    if (arrayString[i].contains("f-")) {
                        Log.d("TAG", "Delete Folder");
                    }
                }
                noteList(searchBar.getText().toString(), selectFolder);
                footer.setVisibility(View.INVISIBLE);
                dustbin.setVisibility(View.INVISIBLE);
                favorite.setVisibility(View.INVISIBLE);
                selectAll.setVisibility(View.INVISIBLE);
                move.setVisibility(View.INVISIBLE);

                cross.setVisibility(View.VISIBLE);
                button.setVisibility(View.VISIBLE);
                selects = "!";
                select = false;

                noteList(searchBar.getText().toString(), selectFolder);
                Drawable drawable = getDrawable(R.drawable.select_all);
                if (selectAll.getDrawable() != drawable) selectAll.setImageDrawable(drawable);
            }
        });
        selectAll.setOnClickListener(view -> {
            Drawable drawable = getDrawable(R.drawable.select_all);
            if (selects.length() == 1 || !selects.equals(selectString)) {
                noteList(searchBar.getText().toString(), selectFolder,1);
                selects = selectString;
                selectAll.setImageDrawable(getDrawable(R.drawable.select_all2));
                if (selectString.contains("f-")) move.setColorFilter(Color.parseColor("#606060"));
            } else {
                noteList(searchBar.getText().toString(), selectFolder, 0);
                selects = "!";
                selectAll.setImageDrawable(drawable);
                move.setColorFilter(Color.parseColor("#ffffff"));
            }

        });
        button.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), NoteActivity.class);

            Bundle bundle = new Bundle();
            bundle.putInt("id", -1);
            bundle.putString("folder", selectFolder);
            intent.putExtras(bundle);

            startActivity(intent);
        });
        searchBar.setFilters(new InputFilter[] {filter});
        searchBar.setOnFocusChangeListener((view, b) -> {
            if (b) check[1] = 1;
            else check[1] = 0;

            if (select) {
                if (!b) {
                    footer.setVisibility(View.VISIBLE);
                    dustbin.setVisibility(View.VISIBLE);
                    favorite.setVisibility(View.VISIBLE);
                    selectAll.setVisibility(View.VISIBLE);
                    move.setVisibility(View.VISIBLE);
                } else {
                    footer.setVisibility(View.INVISIBLE);
                    dustbin.setVisibility(View.INVISIBLE);
                    favorite.setVisibility(View.INVISIBLE);
                    selectAll.setVisibility(View.INVISIBLE);
                    move.setVisibility(View.INVISIBLE);
                }
            }
        });
        final int[] screenHeight = {0};
        final int[] keypadHeight = {0};
        final boolean[] updateSearch = {false};
        final Handler h = new Handler();
        final Runnable r2 = new Runnable() {
            @Override
            public void run() {
                if (keypadHeight[0] > screenHeight[0] * 0.15d) {
                     if (!oldSearch[0].equals(searchBar.getText().toString().toLowerCase())) {
                        oldSearch[0] = searchBar.getText().toString().toLowerCase();
                        noteList(oldSearch[0], selectFolder);
                    }
                    h.postDelayed(this, 50);
                } else updateSearch[0] = false;
            }
        };

        searchBar.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            searchBar.getWindowVisibleDisplayFrame(r);

            screenHeight[0] = searchBar.getRootView().getHeight();
            keypadHeight[0] = screenHeight[0] - r.bottom;
            if ((screenHeight[0] * 0.15d) < keypadHeight[0] && !updateSearch[0]) {
                h.postDelayed(r2, 0);
                updateSearch[0] = true;
            }
            if (keypadHeight[0] > screenHeight[0] * 0.15d) {
                if (check[0] == 1) check[0] = 0;
            } else {
                if (check[1] == 1) {
                    if (check[0] > 10) {
                        if (title.getVisibility() == View.INVISIBLE) {
                            if (searchBar.getText().toString().length() == 0) {
                                check[0] = 0;
                                searchBar.setText("");
                                searchBar.setVisibility(View.INVISIBLE);
                                search.animate().setDuration(500).x(falseSearch.getX()).withEndAction(() -> title.setVisibility(View.VISIBLE));
                                } else {
                                if (select) {
                                    footer.setVisibility(View.VISIBLE);
                                    dustbin.setVisibility(View.VISIBLE);
                                    favorite.setVisibility(View.VISIBLE);
                                    selectAll.setVisibility(View.VISIBLE);
                                    move.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    } else {
                        check[0]++;
                    }
                }
            }
        });
        search.setOnClickListener(view -> {
            if (check[1] == 0) {
                title.setVisibility(View.INVISIBLE);
                searchBar.setVisibility(View.VISIBLE);

                search.animate().setDuration(500).x(title.getX()).withEndAction(() -> {
                    searchBar.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(searchBar, InputMethodManager.SHOW_IMPLICIT);
                });
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void getFolder(String folder) {
        selectFolder = folder;
        noteList(searchBar.getText().toString(), selectFolder);
    }
    public void launchActivity(int id) {
        Intent intent = new Intent(getApplicationContext(), NoteActivity.class);

        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        intent.putExtras(bundle);

        startActivity(intent);
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void noteList(String filter, String stringFolder, int... options) {
        if (stringFolder.length() == 0) title.setText(this.getString(R.string.your_notes));
        else title.setText(stringFolder);

        List<noteArray> notes = new ArrayList<>();
        StringBuilder note = new StringBuilder("!");
        StringBuilder folder = new StringBuilder("!");
        boolean edit = true;

        for (int i = 0; i < db.getNotesCount(); i++) {
            Note thisNote = db.getNote(i);
            if (thisNote.getNoteTitle().toLowerCase().contains(filter) ||
               (thisNote.getFolder().length() != 0 && thisNote.getFolder().toLowerCase().contains(filter) && selectFolder.equals("")) ||
               (thisNote.getNoteContent().toLowerCase().contains(filter) && thisNote.getPassword().length() == 0)) {

                if ((stringFolder.length() == 0 && !folder.toString().contains("!"+thisNote.getFolder()+"!")) || stringFolder.equals(thisNote.getFolder())) {
                    notes.add(new noteArray(thisNote.getNoteTitle(), thisNote.getNoteContent(), i, thisNote.isFavorite(), thisNote.getPassword(),
                            thisNote.getVisual(), thisNote.getFolder(), thisNote.getPosition(), thisNote.getFolderPosition(), thisNote.getColorFolder()));

                    if (thisNote.getFolder().length() != 0 && isFolder()) note.append("f-").append(thisNote.getFolder()).append("!");
                    else note.append(i).append("!");

                    if (thisNote.getFolder().length() != 0) folder.append(thisNote.getFolder()).append("!");
                }
            } else {
                removeSelect(i+"");
                edit = false;
            }
        }
        if (notes.size() == 0) notes.add(new noteArray("", "", 0, false, "", "", "", 0, 0, "#ffffff"));
        if (edit && !selects.equals(note.toString())) selectAll.setImageDrawable(getDrawable(R.drawable.select_all));

        selectString = note.toString();
        notes1.setAdapter(new ManageNotes(this, notes, notes1, select, options));
    }

    public boolean isFolder() {
        return selectFolder.length() == 0;
    }

    private void reSize() {
        final DisplayMetrics metrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int phoneWidth = metrics.widthPixels;
        int phoneHeight = metrics.heightPixels;

        int textView2Height = (int) (phoneHeight/12d);
        int titleSize = (int) (textView2Height/2.5d);
        int titleWidth = phoneWidth - (titleSize * 2) - (int) (phoneWidth/10d);
        int searchBarWidth = phoneWidth - (titleSize * 2) - (int) (phoneWidth/7d);

        size.reSize2(textView2, phoneWidth, textView2Height);
        size.reSize2(footer, phoneWidth, textView2Height);
        size.reSize2(new View[]{search, falseSearch, option, favorite, selectAll, dustbin, move}, titleSize, titleSize);
        size.reSize2(new Space[]{space2, space5, space6}, (int) (titleSize/2d), 0);
        size.reSize2(title, titleWidth, titleSize, true);
        size.reSize2(searchBar, searchBarWidth, titleSize, true);
        size.reSize2(space, phoneWidth - (int) (phoneWidth*0.1), 0);
        size.reSize2(space3, (int) (phoneHeight*0.02), (int) (phoneHeight*0.02));
        size.reSize2(space4, 0, (int) (phoneHeight*0.02));

        int buttonWidth = (int) ((phoneWidth/19d) * 2d);
        int buttonHeight = (int) (phoneHeight/16d);
        int iconButtonSize = Math.min(buttonWidth, buttonHeight) - (int) (Math.min(buttonWidth, buttonHeight) * 0.40d);

        size.reSize2(button, Math.min(buttonWidth, buttonHeight), Math.min(buttonWidth, buttonHeight));
        size.reSize2(cross, iconButtonSize, iconButtonSize);

        int descHeight = phoneHeight - textView2Height*2 - (int) ((phoneHeight*0.02) * 2);
        int descWidth = phoneWidth - (int) (phoneWidth*0.1);

        size.reSize2(notes1, descWidth, descHeight);
    }
}