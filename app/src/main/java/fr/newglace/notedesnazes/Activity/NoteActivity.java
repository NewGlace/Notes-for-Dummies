package fr.newglace.notedesnazes.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.*;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.Size;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.multi.qrcode.QRCodeMultiReader;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import fr.newglace.notedesnazes.Database.MyDatabaseHelper;
import fr.newglace.notedesnazes.Database.Note;
import fr.newglace.notedesnazes.QRCode.QRCodeFoundListener;
import fr.newglace.notedesnazes.QRCode.QRCodeImageAnalyzer;
import fr.newglace.notedesnazes.R;
import fr.newglace.notedesnazes.Styles.Dialog.ColorNote;
import fr.newglace.notedesnazes.Styles.Dialog.OptionNoteEdit;
import fr.newglace.notedesnazes.Styles.Dialog.QRCode;

public class NoteActivity extends AppCompatActivity {
    private EditText title, desc;
    private Context context = this;
    private MyDatabaseHelper db = new MyDatabaseHelper(this);
    private int id = -1;
    private String visual = "";
    private static final int PERMISSION_REQUEST_CAMERA = 0;
    private PreviewView previewView;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private String qrCode;
    private ImageAnalysis imageAnalysis;
    private Activity activity;

    public InputFilter filter = (charSequence, i, i1, spanned, i2, i3) -> {
        String blockCharSet = "\n";

        if (charSequence != null && blockCharSet.contains(charSequence.toString())) return "";
        else if (charSequence != null && charSequence.toString().length() > 18) return charSequence.toString().subSequence(0, 18);
        else return null;
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBackPressed() {
        if (previewView.getVisibility() == View.VISIBLE) {
            previewView.setVisibility(View.INVISIBLE);
            imageAnalysis.clearAnalyzer();
            desc.setVisibility(View.VISIBLE);
        } else super.onBackPressed();
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        TextView button = findViewById(R.id.add_notes);
        TextView bold = findViewById(R.id.bold);
        TextView italic = findViewById(R.id.italic);
        TextView color = findViewById(R.id.color);
        TextView backgroundColor = findViewById(R.id.bg_color);
        TextView underline = findViewById(R.id.underline);
        TextView strike = findViewById(R.id.strike);
        TextView super1 = findViewById(R.id.super1);
        TextView sub2 = findViewById(R.id.sub2);
        ImageView star = findViewById(R.id.star);
        ImageView option = findViewById(R.id.options);
        ImageView addImage = findViewById(R.id.add_image);
        ImageView normal = findViewById(R.id.normal);
        ImageView center = findViewById(R.id.center);
        ImageView opposite = findViewById(R.id.opposite);

        previewView = findViewById(R.id.activity_main_previewView);
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        this.title = findViewById(R.id.note_title);
        this.desc = findViewById(R.id.note_desc);
        this.activity = this;

        this.title.setFilters(new InputFilter[] {filter});

        SpannableString spannableString = new SpannableString("U");
        spannableString.setSpan(new UnderlineSpan(), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        underline.setText(spannableString);

        spannableString = new SpannableString("S");
        spannableString.setSpan(new StrikethroughSpan(), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        strike.setText(spannableString);

        spannableString = new SpannableString("A");
        spannableString.setSpan(new BackgroundColorSpan(Color.parseColor("#CCFF0000")), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        backgroundColor.setText(spannableString);

        spannableString = new SpannableString("A1");
        spannableString.setSpan(new SuperscriptSpan(), 1, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        super1.setText(spannableString);

        spannableString = new SpannableString("A2");
        spannableString.setSpan(new SubscriptSpan(), 1, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        sub2.setText(spannableString);

        SeekBar seekBar = findViewById(R.id.size);
        String password = "";
        final boolean[] favorite = {false};
        int folderPosition = 0;
        int position = 1;

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;

        id = bundle.getInt("id");
        String folder = bundle.getString("folder");

        if (id != -1) {
            this.title.setText(db.getNote(id).getNoteTitle());
            this.desc.setText(db.getNote(id).getNoteContent());
            favorite[0] = db.getNote(id).isFavorite();
            password = db.getNote(id).getPassword();
            visual = db.getNote(id).getVisual();
            folder = db.getNote(id).getFolder();
            folderPosition = db.getNote(id).getFolderPosition();
            position = db.getNote(id).getPosition();

            String[] visuals = visual.split("/");
            Spannable span = this.desc.getText();
            for (String v : visuals) {
                if (v.length() == 0) continue;

                String[] value = v.split("!");
                int id = Integer.parseInt(value[0]);
                int id2 = Integer.parseInt(value[1]);
                for (String val : value) {
                    if (val.equals("bold")) span.setSpan(new StyleSpan(Typeface.BOLD), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    if (val.equals("italic")) span.setSpan(new StyleSpan(Typeface.ITALIC), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    if (val.contains("color:")) span.setSpan(new ForegroundColorSpan(Integer.parseInt(val.replace("color:", ""))), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    if (val.contains("backgroundColor:")) span.setSpan(new BackgroundColorSpan(Integer.parseInt(val.replace("backgroundColor:", ""))), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    if (val.equals("underline")) span.setSpan(new UnderlineSpan(), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    if (val.equals("strike")) span.setSpan(new StrikethroughSpan(), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    if (val.contains("size:")) span.setSpan(new AbsoluteSizeSpan(Integer.parseInt(val.replace("size:", "")), true), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    //span.setSpan(new BulletSpan(40, Color.RED, 20), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    if (val.equals("opposite")) span.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    if (val.equals("center")) span.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    if (val.equals("super")) span.setSpan(new SuperscriptSpan(), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    if (val.equals("sub")) span.setSpan(new SubscriptSpan(), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }

            this.desc.setText(span);
        }

        option.setOnClickListener(view12 -> {
            if (previewView.getVisibility() != View.INVISIBLE) return;

            OptionNoteEdit dialog = new OptionNoteEdit(activity);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.build();

            dialog.getLoadGallery().setOnClickListener(v -> {
                dialog.dismiss();
                imageChooser();
            });
            dialog.getLoadCamera().setOnClickListener(v -> {
                if (previewView.getVisibility() == View.INVISIBLE) {
                    previewView.setVisibility(View.VISIBLE);
                    desc.setVisibility(View.INVISIBLE);
                    dialog.dismiss();
                    requestCamera();
                }
            });

            dialog.getQRCode().setOnClickListener(v -> {
                dialog.dismiss();

                QRCode dialogQRCode = new QRCode(activity);
                Objects.requireNonNull(dialogQRCode.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogQRCode.build();

                WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
                Display display = manager.getDefaultDisplay();

                Point point = new Point();
                display.getSize(point);

                int width = point.x;
                int height = point.y;

                int dimen = Math.min(width, height) * 3 / 4;

                Spannable span = desc.getText();
                visual = generatedSpan(span);

                QRGEncoder qrgEncoder = new QRGEncoder(this.title.getText().toString()+"ù____NEW____ù"+this.desc.getText().toString()+"ù____NEW____ù"+visual, null, QRGContents.Type.TEXT, dimen);
                try {
                    Bitmap bitmap = qrgEncoder.encodeAsBitmap();
                    dialogQRCode.getImage().setImageBitmap(bitmap);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            });
        });

        final boolean[] finalFavorite = {favorite[0]};
        String finalPassword = password;
        String finalFolder = folder;
        int finalPosition = position;
        int finalFolderPosition = folderPosition;

        star.setOnClickListener(v -> {
            finalFavorite[0] = !finalFavorite[0];
            favorite[0] = !favorite[0];

            int colorStar = finalFavorite[0] ? getResources().getColor(R.color.gold) : Color.parseColor("#939393");
            if (finalFavorite[0]) {
                star.animate().rotation(500f).setDuration(250).withEndAction(() -> {
                    star.setColorFilter(colorStar);
                    star.animate().rotation(0f).setDuration(250);
                });
            } else star.setColorFilter(colorStar);
        });
        int colorStar = finalFavorite[0] ? getResources().getColor(R.color.gold) : Color.parseColor("#939393");
        star.setColorFilter(colorStar);

        button.setOnClickListener(view -> {
            String titleText = title.getText().toString();
            String descText = desc.getText().toString();

            if (titleText.length() == 0) titleText = descText.substring(0, Math.min(19, descText.length()));
            titleText = titleText.replaceAll("\n", " ");

            if (descText.length() == 0) Toast.makeText(context, "La description est vide", Toast.LENGTH_SHORT).show();
            else {
                Spannable span = desc.getText();
                visual = generatedSpan(span);

                Note note = new Note(titleText, desc.getText().toString(), finalFavorite[0], finalPassword, visual, finalFolder, finalPosition, finalFolderPosition);
                if (id == -1) db.addNote(db.getNotesCount(), note);
                else db.editNote(id, note);

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        editSpanColor(backgroundColor, "backgroundColor");
        editSpanColor(color, "color");
        editSpan(bold, "bold");
        editSpan(italic, "italic");
        editSpan(underline, "underline");
        editSpan(strike, "strike");
        editSpan(center, "center");
        editSpan(normal, "normal");
        editSpan(opposite, "opposite");
        editSpan(super1, "super");
        editSpan(sub2, "sub");

        addImage.setOnClickListener(v -> {
            Intent i = new Intent();
            i.setType("image/*");
            i.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(i, "Select Picture"), 201);
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int startSelect = desc.getSelectionStart(),
                        endSelect = desc.getSelectionEnd();

                if (startSelect != endSelect) {
                    Spannable span = desc.getText();
                    span.setSpan(new AbsoluteSizeSpan(i+11, true), startSelect, endSelect, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    desc.setText(span);
                    desc.setSelection(startSelect, endSelect);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void editSpan2(String type, String... option) {
        int startSelect = desc.getSelectionStart(),
                endSelect = desc.getSelectionEnd();

        if (startSelect != endSelect) {
            Spannable span = desc.getText();

            boolean alreadyEdit = false;
            if (type.equals("color"))
                span.setSpan(new ForegroundColorSpan(Color.parseColor(option[0])), startSelect, endSelect, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            else if (type.equals("backgroundColor"))
                span.setSpan(new BackgroundColorSpan(Color.parseColor(option[0])), startSelect, endSelect, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            else {
                alreadyEdit = true;
                for (int i = startSelect; i < endSelect; i++) {
                    Object[] bolds = Arrays.stream(span.getSpans(i, i+1, StyleSpan.class)).filter(x -> x.getStyle() == Typeface.BOLD).toArray();
                    Object[] italics = Arrays.stream(span.getSpans(i, i+1, StyleSpan.class)).filter(x -> x.getStyle() == Typeface.ITALIC).toArray();

                    if (bolds.length == 0 && type.equals("bold")) alreadyEdit = false;
                    if (italics.length == 0 && type.equals("italic")) alreadyEdit = false;
                    if (span.getSpans(i, i+1, UnderlineSpan.class).length == 0 && type.equals("underline")) alreadyEdit = false;
                    if (span.getSpans(i, i+1, StrikethroughSpan.class).length == 0 && type.equals("strike")) alreadyEdit = false;
                    if (span.getSpans(i, i+1, SuperscriptSpan.class).length == 0 && type.equals("super")) alreadyEdit = false;
                    if (span.getSpans(i, i+1, SubscriptSpan.class).length == 0 && type.equals("sub")) alreadyEdit = false;
                    if (!alreadyEdit) break;
                }
            }
            if (alreadyEdit) {
                Spannable newSpan = new SpannableStringBuilder(desc.getText().toString());
                String[] visuals = generatedSpan(span).split("/");
                for (String v : visuals) {
                    if (v.length() == 0) continue;

                    String[] value = v.split("!");
                    int id = Integer.parseInt(value[0]);
                    int id2 = Integer.parseInt(value[1]);
                    for (String val : value) {
                        if (id != startSelect && id2 != endSelect) {
                            if (val.equals("bold")) newSpan.setSpan(new StyleSpan(Typeface.BOLD), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            if (val.equals("italic")) newSpan.setSpan(new StyleSpan(Typeface.ITALIC), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            if (val.equals("underline")) newSpan.setSpan(new UnderlineSpan(), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            if (val.equals("strike")) newSpan.setSpan(new StrikethroughSpan(), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            if (val.equals("super")) newSpan.setSpan(new SuperscriptSpan(), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            if (val.equals("sub")) newSpan.setSpan(new SubscriptSpan(), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        } else {
                            if (val.equals("bold") && !type.equals("bold")) newSpan.setSpan(new StyleSpan(Typeface.BOLD), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            if (val.equals("italic") && !type.equals("italic")) newSpan.setSpan(new StyleSpan(Typeface.ITALIC), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            if (val.equals("underline") && !type.equals("underline")) newSpan.setSpan(new UnderlineSpan(), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            if (val.equals("strike") && !type.equals("strike")) newSpan.setSpan(new StrikethroughSpan(), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            if (val.equals("super") && !type.equals("super")) newSpan.setSpan(new SuperscriptSpan(), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            if (val.equals("sub") && !type.equals("sub")) newSpan.setSpan(new SubscriptSpan(), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }

                        if (val.contains("size:")) newSpan.setSpan(new AbsoluteSizeSpan(Integer.parseInt(val.replace("size:", "")), true), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        if (val.contains("backgroundColor:")) newSpan.setSpan(new BackgroundColorSpan(Integer.parseInt(val.replace("backgroundColor:", ""))), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        if (val.contains("color:")) newSpan.setSpan(new ForegroundColorSpan(Integer.parseInt(val.replace("color:", ""))), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
                span = newSpan;
            } else {
                if (type.equals("bold"))
                    span.setSpan(new StyleSpan(Typeface.BOLD), startSelect, endSelect, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                if (type.equals("italic"))
                    span.setSpan(new StyleSpan(Typeface.ITALIC), startSelect, endSelect, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                if (type.equals("underline"))
                    span.setSpan(new UnderlineSpan(), startSelect, endSelect, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                if (type.equals("strike"))
                    span.setSpan(new StrikethroughSpan(), startSelect, endSelect, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                if (type.equals("strike")) span.setSpan(new StrikethroughSpan(), startSelect, endSelect, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                if (type.equals("super")) span.setSpan(new SuperscriptSpan(), startSelect, endSelect, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                if (type.equals("sub")) span.setSpan(new SubscriptSpan(), startSelect, endSelect, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            desc.setText(span);
            desc.setSelection(startSelect, endSelect);
        }
        int endLine = desc.getText().toString().indexOf("\n", endSelect);
        if (endLine == -1) endLine = desc.getText().toString().length();
        int startLine = desc.getText().toString().lastIndexOf("\n", startSelect);
        if (startLine == -1) startLine = 0;

        if (endLine == startLine && startSelect != 0) {
            endLine = desc.getText().toString().indexOf("\n", endSelect-1);
            if (endLine == -1) endLine = desc.getText().toString().length();
            startLine = desc.getText().toString().lastIndexOf("\n", startSelect-1);
            if (startLine == -1) startLine = 0;
        }
        Spannable span = desc.getText();
        if (type.equals("normal"))
            span.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_NORMAL), startLine, endLine, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        else if (type.equals("center"))
            span.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), startLine, endLine, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        else if (type.equals("opposite"))
            span.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE), startLine, endLine, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        desc.setText(span);
        desc.setSelection(startSelect, endSelect);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void editSpan(View view, String type) {
        view.setOnClickListener(v -> editSpan2(type));
    }
    private void editSpanColor(TextView view, String type) {
        view.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                ColorNote dialog = new ColorNote(activity);
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.build();
                dialog.editColorPicker();

                update(dialog.getR(), dialog);
                update(dialog.getG(), dialog);
                update(dialog.getB(), dialog);

                dialog.getHexColor().setOnEditorActionListener((textView, i, keyEvent) -> {
                    dialog.editColorHex();
                    editSpan2(type, dialog.getHexColor().getText().toString());
                    return false;
                });
                dialog.getValid().setOnClickListener(view1 -> {
                    dialog.editColorHex();
                    String hex = dialog.getHexColor().getText().toString();
                    editSpan2(type, hex);
                    dialog.dismiss();
                });
            }

            private void update(SeekBar seekBar, ColorNote dialog) {
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        dialog.editColorPicker();
                        editSpan2(type, dialog.getHexColor().getText().toString());
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String generatedSpan(Spannable span) {
        StringBuilder visual = new StringBuilder();
        for (int i = 0; i < desc.getText().toString().length(); i++) {
            if (span.getSpans(i, i+1, StyleSpan.class).length != 0) {
                visual.append(visual.length() == 0 ? "" : "/").append(i).append("!").append(i + 1);
                for (StyleSpan style : span.getSpans(i, i+1, StyleSpan.class)) {
                    if (style.getStyle() == Typeface.BOLD) visual.append("!bold");
                    if (style.getStyle() == Typeface.ITALIC) visual.append("!italic");
                }
            }
            if (span.getSpans(i, i+1, ForegroundColorSpan.class).length != 0) {
                visual.append(visual.length() == 0 ? "" : "/").append(i).append("!").append(i + 1).append("!color:").append(span.getSpans(i, i + 1, ForegroundColorSpan.class)[span.getSpans(i, i + 1, ForegroundColorSpan.class).length - 1].getForegroundColor());
            }
            if (span.getSpans(i, i+1, BackgroundColorSpan.class).length != 0) {
                visual.append(visual.length() == 0 ? "" : "/").append(i).append("!").append(i + 1).append("!backgroundColor:").append(span.getSpans(i, i + 1, BackgroundColorSpan.class)[span.getSpans(i, i + 1, BackgroundColorSpan.class).length - 1].getBackgroundColor());
            }
            if (span.getSpans(i, i+1, AbsoluteSizeSpan.class).length != 0) {
                visual.append(visual.length() == 0 ? "" : "/").append(i).append("!").append(i + 1).append("!size:").append(span.getSpans(i, i + 1, AbsoluteSizeSpan.class)[span.getSpans(i, i + 1, AbsoluteSizeSpan.class).length - 1].getSize());
            }
            if (span.getSpans(i, i+1, UnderlineSpan.class).length != 0) {
                visual.append(visual.length() == 0 ? "" : "/").append(i).append("!").append(i + 1).append("!underline");
            }
            if (span.getSpans(i, i+1, StrikethroughSpan.class).length != 0) {
                visual.append(visual.length() == 0 ? "" : "/").append(i).append("!").append(i + 1).append("!strike");
            }
            AlignmentSpan[] alignmentSpan = span.getSpans(i, i+1, AlignmentSpan.class);
            if (alignmentSpan.length != 0) {
                if (alignmentSpan[alignmentSpan.length-1].getAlignment() == Layout.Alignment.ALIGN_CENTER)
                    visual.append(visual.length() == 0 ? "" : "/").append(i).append("!").append(i + 1).append("!center");
                else if (alignmentSpan[alignmentSpan.length-1].getAlignment() == Layout.Alignment.ALIGN_OPPOSITE)
                    visual.append(visual.length() == 0 ? "" : "/").append(i).append("!").append(i + 1).append("!opposite");
            }
            SubscriptSpan[] subscriptSpan = span.getSpans(i, i+1, SubscriptSpan.class);
            SuperscriptSpan[] superscriptSpans = span.getSpans(i, i+1, SuperscriptSpan.class);
            if (subscriptSpan.length > superscriptSpans.length)
                visual.append(visual.length() == 0 ? "" : "/").append(i).append("!").append(i + 1).append("!sub");
            else if (subscriptSpan.length < superscriptSpans.length)
                visual.append(visual.length() == 0 ? "" : "/").append(i).append("!").append(i + 1).append("!super");
        }
        String[] visuals = visual.toString().split("/");
        for (int y = 0; y < visuals.length;) {
            if (visuals[y].length() == 0) {
                y++;
                continue;
            }

            String p = visuals[y].split("!")[1];
            int position = -1;
            for (int u = 0; u < visuals.length; u++) {
                if (visuals[u].split("!")[0].equals(p)) position = u;
            }
            if (position != -1) {
                String[] options1 = Arrays.copyOfRange(visuals[y].split("!"), 2, visuals[y].split("!").length);
                String[] options2 = Arrays.copyOfRange(visuals[position].split("!"), 2, visuals[position].split("!").length);

                if (String.join("!", options1).equals(String.join("!", options2))) {
                    String[] newOption = visuals[y].split("!");
                    newOption[1] = (Integer.parseInt(newOption[1])+1) + "";

                    visuals[y] = String.join("!", newOption);
                    visuals[position] = "";
                } else y++;
            } else y++;
        }
        visual = new StringBuilder();

        for (String v : visuals) {
            if (v.length() == 0) continue;
            visual.append(visual.length() == 0 ? "" : "/").append(v);
        }
        this.visual = visual.toString();
        return visual.toString();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void requestCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(context, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startCamera() {
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindCameraPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                Toast.makeText(context, "Error starting camera " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(context));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void bindCameraPreview(@NonNull ProcessCameraProvider cameraProvider) {
        previewView.setImplementationMode(PreviewView.ImplementationMode.PERFORMANCE);
        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        imageAnalysis =
                new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(1280, 720))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();


        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(context), new QRCodeImageAnalyzer(new QRCodeFoundListener() {
            @Override
            public void onQRCodeFound(String _qrCode) {
                qrCode = _qrCode;
                String[] arrayQRCode = qrCode.split("ù____NEW____ù");
                if (arrayQRCode.length == 3) {
                    editNote(arrayQRCode);

                    previewView.setVisibility(View.INVISIBLE);
                    imageAnalysis.clearAnalyzer();
                    Toast.makeText(context, "La note a bien été modifié !", Toast.LENGTH_SHORT).show();
                    desc.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void qrCodeNotFound() {

            }
        }));

        cameraProvider.bindToLifecycle(this, cameraSelector, imageAnalysis, preview);
    }
    void imageChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select Picture"), 200);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 200) {
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), selectedImageUri);

                        int[] intArray = new int[bitmap.getWidth()*bitmap.getHeight()];
                        bitmap.getPixels(intArray, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
                        LuminanceSource source = new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(),intArray);
                        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));

                        try {
                            Result result = new QRCodeMultiReader().decode(binaryBitmap);

                            qrCode = result.getText();
                            String[] arrayQRCode = qrCode.split("ù____NEW____ù");
                            if (arrayQRCode.length == 3) {
                                editNote(arrayQRCode);
                                Toast.makeText(context, "La note a bien été modifié !", Toast.LENGTH_SHORT).show();
                            } else Toast.makeText(context, "Le Qrcode n'a pas été trouvé", Toast.LENGTH_SHORT).show();
                        } catch (FormatException | ChecksumException | NotFoundException ignored) {
                            Toast.makeText(context, "Le Qrcode n'a pas été trouvé", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        Toast.makeText(context, "Le Qrcode n'a pas été trouvé", Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (requestCode == 201) {
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), selectedImageUri);
                        if (bitmap.getWidth() > desc.getWidth()) {
                            int height = bitmap.getWidth() * desc.getWidth() / bitmap.getHeight();
                            bitmap = Bitmap.createScaledBitmap(bitmap, desc.getWidth(), height, false);
                        }
                        if (bitmap.getHeight() > (desc.getHeight()/2)) {
                            int width = bitmap.getHeight() * (desc.getWidth()/2) / bitmap.getWidth();
                            bitmap = Bitmap.createScaledBitmap(bitmap, width, (desc.getWidth()/2), false);
                        }

                        int startSelect = desc.getSelectionStart();
                        Spannable span = desc.getText();

                        span.setSpan(new ImageSpan(context, bitmap), startSelect-1, startSelect, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        desc.setText(span);
                        desc.setSelection(startSelect);
                    } catch (IOException e) {
                        Toast.makeText(context, "L'image n'a pas été trouvé", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    public void editNote(String[] arrayQRCode) {
        title.setText(arrayQRCode[0]);
        desc.setText(arrayQRCode[1]);

        String[] visuals = arrayQRCode[2].split("/");
        Spannable span = desc.getText();
        for (String v : visuals) {
            if (v.length() == 0) continue;

            String[] value = v.split("!");
            int id = Integer.parseInt(value[0]);
            int id2 = Integer.parseInt(value[1]);
            for (String val : value) {
                if (val.equals("bold")) span.setSpan(new StyleSpan(Typeface.BOLD), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                if (val.equals("italic")) span.setSpan(new StyleSpan(Typeface.ITALIC), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                if (val.contains("color:")) span.setSpan(new ForegroundColorSpan(Integer.parseInt(val.replace("color:", ""))), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                if (val.contains("backgroundColor:")) span.setSpan(new BackgroundColorSpan(Integer.parseInt(val.replace("backgroundColor:", ""))), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                if (val.equals("underline")) span.setSpan(new UnderlineSpan(), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                if (val.equals("strike")) span.setSpan(new StrikethroughSpan(), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                if (val.contains("size:")) span.setSpan(new AbsoluteSizeSpan(Integer.parseInt(val.replace("size:", "")), true), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                if (val.equals("opposite")) span.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                if (val.equals("center")) span.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                if (val.equals("super")) span.setSpan(new SuperscriptSpan(), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                if (val.equals("sub")) span.setSpan(new SubscriptSpan(), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        desc.setText(span);
    }
}
