package fr.newglace.notedesnazes.Styles.Notes;

import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import java.util.List;
import java.util.Objects;
import fr.newglace.notedesnazes.Activity.MainActivity;
import fr.newglace.notedesnazes.Database.MyDatabaseHelper;
import fr.newglace.notedesnazes.Database.Note;
import fr.newglace.notedesnazes.R;
import fr.newglace.notedesnazes.Styles.Dialog.OptionNote;
import fr.newglace.notedesnazes.Styles.Dialog.PasswordNote;
import fr.newglace.notedesnazes.Styles.Dialog.PasswordNoteConfig;

public class ManageNotes extends BaseAdapter {
    private List<noteArray> notes;
    private LayoutInflater inflater;
    private MainActivity activity;
    private MyDatabaseHelper db;
    private ListView listView;
    private boolean select;
    private int selectAll = 0;

    public ManageNotes(MainActivity activity, List<noteArray> notes, ListView listView, boolean select, int... selectAll) {
        this.notes = notes;
        this.inflater = LayoutInflater.from(activity);
        this.activity = activity;
        this.db = new MyDatabaseHelper(activity);
        this.listView = listView;
        this.select = select;
        if (selectAll.length == 1) this.selectAll = selectAll[0];
    }

    @Override
    public int getCount() {
        return notes.size();
    }

    @Override
    public noteArray getItem(int i) {
        return notes.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.note_layout, null);
        if (select) view = inflater.inflate(R.layout.note_layout2, null);

        noteArray item = getItem(i);
        String title = item.getTitle();
        String desc = item.getDesc();
        final String[] password = {item.getPassword()};
        final boolean[] favorite = {item.isFavorite()};
        String visual = item.getVisual();
        String folder = item.getFolder();
        int folderPosition = item.getFolderPosition();
        int p = item.getPosition();
        int position = item.getI();

        TextView titleNote = view.findViewById(R.id.title);
        TextView descNote = view.findViewById(R.id.desc);
        ImageView option = view.findViewById(R.id.options);
        ImageView background = view.findViewById(R.id.textView3);
        CheckBox checkBox = view.findViewById(R.id.checkbox);

        if (select) {
            if (selectAll == 1) checkBox.setChecked(true);
        }

        checkBox.setOnCheckedChangeListener((cb, b) -> {
            String value = (folder.length() > 0 && activity.isFolder() ? "f-"+folder : position+"");

            if (b) activity.addSelect(value);
            else activity.removeSelect(value);
        });

        boolean[] waiting = {false};
        background.setOnLongClickListener(v -> {
            if (!select) {
                checkBox.setChecked(true);
                select = true;
                waiting[0] = true;
            } else select = false;
            activity.setSelect(select);
            return false;
        });

        if (favorite[0]) {
            int color = Color.parseColor("#D5B200");
            background.setColorFilter(color);
        }

        final boolean[] optionUnClick = {true};

        titleNote.setText(title);
        descNote.setText(desc);

        String[] Alphabet = "abcdefghijklmnopqrstuvwxyz".split("");
        String[] oldText = {""};

        final Handler h = new Handler();
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                String value = (folder.length() > 0 && activity.isFolder() ? "f-"+folder : position+"");
                if (checkBox.isChecked() != activity.getSelect(value)) checkBox.setChecked(activity.getSelect(value));

                if (select && checkBox.getVisibility() == View.INVISIBLE) {
                    ViewGroup.LayoutParams params = background.getLayoutParams();
                    params.width = background.getWidth() - checkBox.getWidth();
                    background.setLayoutParams(params);
                    checkBox.setVisibility(View.VISIBLE);
                } else if (!select && checkBox.getVisibility() == View.VISIBLE) {
                    ViewGroup.LayoutParams params = background.getLayoutParams();
                    params.width = background.getWidth() + checkBox.getWidth();
                    background.setLayoutParams(params);
                    checkBox.setVisibility(View.INVISIBLE);
                    checkBox.setChecked(false);
                }

                if (password[0].length() > 0) {
                    StringBuilder fackText = new StringBuilder();
                    for (int i = 0; i < Math.min(desc.length(), 20); i++) {
                        if (desc.split("")[i].equals(" ")) {
                            fackText.append(" ");
                        } else {
                            if (Math.random() > 0.3 || oldText[0].equals("")) {
                                fackText.append(Alphabet[(int) (Math.random() * 26)]);
                            } else {
                                fackText.append(oldText[0].split("")[i]);
                            }
                        }
                    }
                    oldText[0] = fackText.toString();
                    descNote.setText(fackText.toString());
                } else {
                    descNote.setText(descNote.getText().toString());
                }
                h.postDelayed(this, 100);
            }
        };
        h.postDelayed(r, 0);

        if (descNote.getText().length() == 0) {
            view.setVisibility(View.INVISIBLE);
            return view;
        }
        if (folder.length() != 0 && activity.isFolder()) {
            titleNote.setText(folder);
            descNote.setText("");

            int color = Color.parseColor("#FF0000");
            background.setColorFilter(color);
            background.setOnClickListener(view1 -> {
                if (select) {
                    if (!waiting[0]) checkBox.setChecked(!checkBox.isChecked());
                    else waiting[0] = false;
                } else activity.getFolder(folder);
            });
            return view;
        }
        option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (password[0].length() == 0) {
                    if (optionUnClick[0]) startOptions();
                } else {
                    PasswordNote dialog = new PasswordNote(activity);
                    Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.build();

                    dialog.getPassword().setOnEditorActionListener((textView, i1, keyEvent) -> {
                        if (password[0].equals(dialog.getPassword().getText().toString().replace("\n", ""))) {
                            dialog.dismiss();
                            startOptions();
                        } else Toast.makeText(activity, "Mot de passe incorrect !", Toast.LENGTH_SHORT).show();
                        return false;
                    });
                }
            }

            private void startOptions() {
                optionUnClick[0] = false;
                OptionNote dialog = new OptionNote(activity);
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.build();
                optionUnClick[0] = true;

                if (password[0].length() > 0) {
                    dialog.getLockText().setText("Déverrouiller");
                    dialog.getImageView5().setImageDrawable(activity.getDrawable(R.drawable.password_open));
                }

                if (favorite[0]) {
                    int color = Color.parseColor("#D5B200");
                    dialog.getFavoriteColor().setColorFilter(color);
                }
                dialog.getDelete().setOnClickListener(view121 -> {
                    if (activity.getSelect(i+"")) activity.removeSelect(i+"");
                    db.deleteNote(i);
                    Toast.makeText(activity, "La note a été supprimé", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();

                    activity.noteList(activity.getSearchBar().getText().toString(), activity.getSelectFolder());
                });
                dialog.getFavorite().setOnClickListener(view1212 -> {
                    db.editNote(position, new Note(title, desc, !favorite[0], password[0], visual, folder, p, folderPosition));

                    if (favorite[0]) {
                        Toast.makeText(activity, "La note a été retiré des favoris", Toast.LENGTH_SHORT).show();
                        int color = Color.parseColor("#192241");
                        background.setColorFilter(color);
                    } else {
                        Toast.makeText(activity, "La note a été ajouté aux favoris", Toast.LENGTH_SHORT).show();
                        int color = Color.parseColor("#D5B200");
                        background.setColorFilter(color);
                    }

                    favorite[0] = !favorite[0];
                    dialog.dismiss();
                });
                dialog.getPassword().setOnClickListener(vv -> {
                    if (password[0].equals("")) {
                        PasswordNoteConfig dialog2 = new PasswordNoteConfig(activity);
                        Objects.requireNonNull(dialog2.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog2.build();

                        EditText pass = dialog2.getPassword();
                        pass.setOnKeyListener((v, keyCode, event) -> {
                            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                                if (pass.length() > 0) {
                                    Toast.makeText(activity, "La note a été verrouillée", Toast.LENGTH_SHORT).show();
                                    db.editNote(position, new Note(title, desc, favorite[0], pass.getText().toString(), visual, folder, p, folderPosition));

                                    password[0] = pass.getText().toString();
                                    dialog.dismiss();
                                    dialog2.dismiss();
                                    return true;
                                }
                            }
                            dialog.dismiss();
                            return false;
                        });
                    } else {
                        Toast.makeText(activity, "La note a été déverrouillée", Toast.LENGTH_SHORT).show();
                        password[0] = "";
                        descNote.setText(desc);
                        db.editNote(position, new Note(title, desc, favorite[0], password[0], visual, folder, p, folderPosition));
                        dialog.dismiss();
                    }
                });
            }
        });
        background.setOnClickListener(view1 -> {
            if (select) {
                if (!waiting[0]) checkBox.setChecked(!checkBox.isChecked());
                else waiting[0] = false;
                return;
            }
            if (password[0].length() == 0) {
                if (optionUnClick[0]) activity.launchActivity(position);
            } else {
                PasswordNote dialog = new PasswordNote(activity);
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.build();

                dialog.getPassword().setOnEditorActionListener((textView, i1, keyEvent) -> {
                    if (password[0].equals(dialog.getPassword().getText().toString().replace("\n", ""))) {
                        dialog.dismiss();
                        activity.launchActivity(position);
                    } else {
                        Toast.makeText(activity, "Mot de passe incorrect !", Toast.LENGTH_SHORT).show();
                    }
                    return false;
                });
            }
        });
        return view;
    }
}
