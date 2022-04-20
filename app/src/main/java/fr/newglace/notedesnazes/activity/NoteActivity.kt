package fr.newglace.notedesnazes.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.*
import android.text.style.*
import android.util.DisplayMetrics
import android.util.Log
import android.util.Size
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.common.util.concurrent.ListenableFuture
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.multi.qrcode.QRCodeMultiReader
import fr.newglace.notedesnazes.R
import fr.newglace.notedesnazes.database.note.local.MyDatabaseHelper
import fr.newglace.notedesnazes.database.note.local.Note
import fr.newglace.notedesnazes.qrc.QRCodeFoundListener
import fr.newglace.notedesnazes.qrc.QRCodeImageAnalyzer
import fr.newglace.notedesnazes.styles.Colors
import fr.newglace.notedesnazes.styles.ReSize
import fr.newglace.notedesnazes.styles.dialog.ColorNote
import fr.newglace.notedesnazes.styles.dialog.OptionNoteEdit
import fr.newglace.notedesnazes.styles.dialog.PasswordNoteConfig
import fr.newglace.notedesnazes.styles.dialog.QRCode
import java.io.IOException
import java.util.*
import java.util.concurrent.ExecutionException

@Suppress("CAST_NEVER_SUCCEEDS")
class NoteActivity : AppCompatActivity() {
    private var title: EditText? = null
    private var desc: EditText? = null
    private val context: Context = this
    private val db = MyDatabaseHelper(this)
    private var id = -1
    private var visual = ""
    private var previewView: PreviewView? = null
    private var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>? = null
    private var qrCode: String? = null
    private var imageAnalysis: ImageAnalysis? = null
    private var activity: Activity? = null
    private var button: TextView? = null
    private var bold: TextView? = null
    private var italic: TextView? = null
    private var color: TextView? = null
    private var backgroundColor: TextView? = null
    private var underline: TextView? = null
    private var strike: TextView? = null
    private var super1: TextView? = null
    private var sub2: TextView? = null
    private var textView2: TextView? = null
    private var iconButton: TextView? = null
    private var blockMarkDown: TextView? = null
    private var textView5: TextView? = null
    private var textView6: TextView? = null
    private var star: ImageView? = null
    private var option: ImageView? = null
    private var addImage: ImageView? = null
    private var normal: ImageView? = null
    private var center: ImageView? = null
    private var opposite: ImageView? = null
    private var space: Space? = null
    private var space4: Space? = null
    private var space5: Space? = null
    private var space3: Space? = null
    private var space11: Space? = null
    private var seekBar: SeekBar? = null
    private var descHeight = 0
    private var descWidth = 0
    private val size = ReSize()
    private val finalFavorite = booleanArrayOf(false)
    private val finalPassword = arrayOf("")
    private var finalFolder: String? = null
    private var finalPosition = 0
    private var finalFolderPosition = 0
    private var finalColor = "#ff0000"
    private var filter = InputFilter { charSequence: CharSequence?, _: Int, _: Int, _: Spanned?, _: Int, _: Int ->
        val blockCharSet = "\n"
        if (charSequence != null && blockCharSet.contains(charSequence.toString())) return@InputFilter "" else if (charSequence != null && charSequence.toString().length > 18) return@InputFilter charSequence.toString().subSequence(0, 18) else return@InputFilter null
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun onBackPressed() {
        if (previewView!!.visibility == View.VISIBLE) {
            previewView!!.visibility = View.INVISIBLE
            imageAnalysis!!.clearAnalyzer()
            desc!!.visibility = View.VISIBLE
        } else {
            Log.d("TAG", "onBackPressed: ")
            var titleText = title!!.text.toString()
            val descText = desc!!.text.toString()
            if (titleText.isEmpty()) titleText = descText.substring(0, 19.coerceAtMost(descText.length))
            titleText = titleText.replace("\n".toRegex(), " ")
            if (descText.isNotEmpty()) {
                val span: Spannable = desc!!.text
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    visual = generatedSpan(span)
                }
                val note = Note(titleText, desc!!.text.toString(), finalFavorite[0], finalPassword[0], visual, finalFolder!!, finalPosition, finalFolderPosition, finalColor)
                if (id == -1) db.addNote(db.notesCount, note) else db.editNote(id, note)
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else super.onBackPressed()
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_note)
        val imageView = findViewById<TextView>(R.id.textView7)
        button = findViewById(R.id.add_notes)
        bold = findViewById(R.id.bold)
        italic = findViewById(R.id.italic)
        color = findViewById(R.id.color)
        backgroundColor = findViewById(R.id.bg_color)
        underline = findViewById(R.id.underline)
        strike = findViewById(R.id.strike)
        super1 = findViewById(R.id.super1)
        sub2 = findViewById(R.id.sub2)
        star = findViewById(R.id.star)
        option = findViewById(R.id.options)
        addImage = findViewById(R.id.add_image)
        normal = findViewById(R.id.normal)
        center = findViewById(R.id.center)
        opposite = findViewById(R.id.opposite)
        textView2 = findViewById(R.id.textView2)
        space = findViewById(R.id.space)
        space4 = findViewById(R.id.space4)
        space5 = findViewById(R.id.space5)
        space3 = findViewById(R.id.space3)
        space11 = findViewById(R.id.space11)
        previewView = findViewById(R.id.activity_main_previewView)
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        iconButton = findViewById(R.id.icon_button)
        blockMarkDown = findViewById(R.id.para)
        textView5 = findViewById(R.id.textView5)
        textView6 = findViewById(R.id.textView6)
        seekBar = findViewById(R.id.size)
        title = findViewById(R.id.note_title)
        desc = findViewById(R.id.note_desc)
        activity = this
        title!!.filters = arrayOf(filter)
        var spannableString = SpannableString("U")
        spannableString.setSpan(UnderlineSpan(), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        underline!!.text = (spannableString)
        spannableString = SpannableString("S")
        spannableString.setSpan(StrikethroughSpan(), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        strike!!.text = (spannableString)
        spannableString = SpannableString("A")
        spannableString.setSpan(BackgroundColorSpan(Color.parseColor("#CCFF0000")), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        backgroundColor!!.text = (spannableString)
        spannableString = SpannableString("A1")
        spannableString.setSpan(SuperscriptSpan(), 1, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        super1!!.text = (spannableString)
        spannableString = SpannableString("A2")
        spannableString.setSpan(SubscriptSpan(), 1, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        sub2!!.text = (spannableString)
        var password = ""
        val favorite = booleanArrayOf(false)
        var folderPosition = 0
        var position = 1
        val bundle = intent.extras!!
        id = bundle.getInt("id")
        var folder = bundle.getString("folder")
        reSize()
        val colors = Colors(activity)
        val draw = activity!!.getDrawable(R.drawable.button2)
        val draw2 = activity!!.getDrawable(R.drawable.button)
        colors.editColor(imageView!!, 0, null)
        colors.editColor(textView2!!, 1, null)
        colors.editColor(blockMarkDown!!, 2, draw)
        colors.editColor(button!!, 2, draw2)
        val window = window
        window.navigationBarColor = Color.parseColor(colors.getColor(1))
        window.statusBarColor = Color.parseColor(colors.getColor(1))
        if (id != -1) {
            title!!.setText(db.getNote(id).noteTitle)
            desc!!.setText(db.getNote(id).noteContent)
            favorite[0] = db.getNote(id).isFavorite
            password = db.getNote(id).password
            visual = db.getNote(id).visual
            folder = db.getNote(id).folder
            folderPosition = db.getNote(id).folderPosition
            position = db.getNote(id).position
            finalColor = db.getNote(id).colorFolder
            val visuals = visual.split("/".toRegex()).toTypedArray()
            val span: Spannable = desc!!.text
            for (v in visuals) {
                if (v.isEmpty()) continue
                val value = v.split("!".toRegex()).toTypedArray()
                val id = value[0].toInt()
                val id2 = value[1].toInt()
                for (`val` in value) {
                    if (`val` == "bold") span.setSpan(StyleSpan(Typeface.BOLD), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    if (`val` == "italic") span.setSpan(StyleSpan(Typeface.ITALIC), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    if (`val`.contains("color:")) span.setSpan(ForegroundColorSpan(`val`.replace("color:", "").toInt()), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    if (`val`.contains("backgroundColor:")) span.setSpan(BackgroundColorSpan(`val`.replace("backgroundColor:", "").toInt()), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    if (`val` == "underline") span.setSpan(UnderlineSpan(), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    if (`val` == "strike") span.setSpan(StrikethroughSpan(), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    if (`val`.contains("size:")) span.setSpan(AbsoluteSizeSpan(`val`.replace("size:", "").toInt(), true), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    //span.setSpan(new BulletSpan(40, Color.RED, 20), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    if (`val` == "opposite") span.setSpan(AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    if (`val` == "center") span.setSpan(AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    if (`val` == "super") span.setSpan(SuperscriptSpan(), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    if (`val` == "sub") span.setSpan(SubscriptSpan(), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
            desc!!.setText(span)
        }
        finalFavorite[0] = favorite[0]
        finalPassword[0] = password
        finalFolder = folder
        finalPosition = position
        finalFolderPosition = folderPosition
        option!!.setOnClickListener {
            if (previewView!!.visibility != View.INVISIBLE) return@setOnClickListener
            val dialog = OptionNoteEdit(activity!!)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.build()
            if (finalPassword[0].isNotEmpty()) {
                dialog.lockText.text = "Déverrouiller"
                dialog.imageView5.setImageDrawable(activity!!.getDrawable(R.drawable.password_open))
            }
            dialog.password.setOnClickListener {
                dialog.dismiss()
                if (finalPassword[0].isEmpty()) {
                    val dialog2 = PasswordNoteConfig(activity!!)
                    dialog2.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    dialog2.build()
                    val pass = dialog2.password
                    pass.setOnKeyListener { _: View?, keyCode: Int, event: KeyEvent ->
                        if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                            if (pass.length() > 0) {
                                Toast.makeText(activity, "La note a été verrouillée", Toast.LENGTH_SHORT).show()
                                finalPassword[0] = pass.text.toString()
                                dialog2.dismiss()
                                return@setOnKeyListener true
                            }
                        }
                        false
                    }
                } else {
                    finalPassword[0] = ""
                    Toast.makeText(activity, "La note a été déverrouillée", Toast.LENGTH_SHORT).show()
                }
            }
            dialog.delete.setOnClickListener {
                if (id != -1) db.deleteNote(id)
                dialog.dismiss()
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            dialog.loadGallery.setOnClickListener {
                dialog.dismiss()
                imageChooser()
            }
            dialog.loadCamera.setOnClickListener {
                if (previewView!!.visibility == View.INVISIBLE) {
                    previewView!!.visibility = View.VISIBLE
                    desc!!.visibility = View.INVISIBLE
                    dialog.dismiss()
                    requestCamera()
                }
            }
            dialog.qRCode.setOnClickListener {
                dialog.dismiss()
                val dialogQRCode = QRCode(activity!!)
                dialogQRCode.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialogQRCode.build()
                val manager = getSystemService(WINDOW_SERVICE) as WindowManager
                val display = manager.defaultDisplay
                val point = Point()
                display.getSize(point)
                val width = point.x
                val height = point.y
                val dimen = width.coerceAtMost(height) * 3 / 4
                val span: Spannable = desc!!.text
                visual = generatedSpan(span)
                val qrgEncoder = QRGEncoder(title!!.text.toString() + "ù____NEW____ù" + desc!!.text.toString() + "ù____NEW____ù" + visual, null, QRGContents.Type.TEXT, dimen)
                try {
                    val bitmap = qrgEncoder.encodeAsBitmap()
                    dialogQRCode.image.setImageBitmap(bitmap)
                } catch (e: WriterException) {
                    e.printStackTrace()
                }
            }
        }
        star!!.setOnClickListener {
            finalFavorite[0] = !finalFavorite[0]
            favorite[0] = !favorite[0]
            val colorStar = if (finalFavorite[0]) resources.getColor(R.color.gold) else Color.parseColor("#939393")
            if (finalFavorite[0]) {
                star!!.animate().rotation(500f).setDuration(250).withEndAction {
                    star!!.setColorFilter(colorStar)
                    star!!.animate().rotation(0f).duration = 250
                }
            } else star!!.setColorFilter(colorStar)
        }
        val colorStar = if (finalFavorite[0]) resources.getColor(R.color.gold) else Color.parseColor("#939393")
        star!!.setColorFilter(colorStar)
        button!!.setOnClickListener {
            var titleText = title!!.text.toString()
            val descText = desc!!.text.toString()
            if (titleText.isEmpty()) titleText = descText.substring(0, 19.coerceAtMost(descText.length))
            titleText = titleText.replace("\n".toRegex(), " ")
            if (descText.isEmpty()) Toast.makeText(context, "La description est vide", Toast.LENGTH_SHORT).show() else {
                val span: Spannable = desc!!.text
                visual = generatedSpan(span)
                val note = Note(titleText, desc!!.text.toString(), finalFavorite[0], finalPassword[0], visual, finalFolder!!, finalPosition, finalFolderPosition, finalColor)
                if (id == -1) db.addNote(db.notesCount, note) else db.editNote(id, note)
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        editSpanColor(backgroundColor, "backgroundColor")
        editSpanColor(color, "color")
        editSpan(bold, "bold")
        editSpan(italic, "italic")
        editSpan(underline, "underline")
        editSpan(strike, "strike")
        editSpan(center, "center")
        editSpan(normal, "normal")
        editSpan(opposite, "opposite")
        editSpan(super1, "super")
        editSpan(sub2, "sub")
        addImage!!.setOnClickListener {
            val i = Intent()
            i.type = "image/*"
            i.action = Intent.ACTION_GET_CONTENT
            activity!!.startActivityForResult(Intent.createChooser(i, "Select Picture"), 201)
        }
        seekBar!!.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                val startSelect = desc!!.selectionStart
                val endSelect = desc!!.selectionEnd
                if (startSelect != endSelect) {
                    val span: Spannable = desc!!.text
                    span.setSpan(AbsoluteSizeSpan(i + 11, true), startSelect, endSelect, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    desc!!.setText(span)
                    desc!!.setSelection(startSelect, endSelect)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        val check = booleanArrayOf(true)
        val mRootWindow = getWindow()
        val mRootView = mRootWindow.decorView.findViewById<View>(R.id.content)
        seekBar!!.viewTreeObserver.addOnGlobalLayoutListener {
            val heightDiff = seekBar!!.rootView.height - seekBar!!.height
            if (heightDiff < 250 && check[0]) {
                // KeyBoard Open
                size.reSizing(desc!!, descWidth, descHeight)
                size.reSizing(previewView!!, descWidth, descHeight)
                check[0] = false
            } else if (!check[0] && heightDiff >= 250) {
                // KeyBoard Close
                var navigationHeight = 0
                val dpi = resources.getIdentifier("navigation_bar_height", "dimen", "android")
                if (dpi > 0) navigationHeight = resources.getDimension(dpi).toInt()
                val r = Rect()
                bold!!.getWindowVisibleDisplayFrame(r)
                val screenHeight = bold!!.rootView.height
                val keyboardHeight = screenHeight - r.bottom
                size.reSizing(desc!!, descWidth, descHeight - keyboardHeight + navigationHeight)
                size.reSizing(previewView!!, descWidth, descHeight - keyboardHeight + navigationHeight)
                check[0] = true
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun editSpan2(type: String, vararg option: String?) {
        val startSelect = desc!!.selectionStart
        val endSelect = desc!!.selectionEnd
        if (startSelect != endSelect) {
            var span: Spannable = desc!!.text
            var alreadyEdit = false
            if (type == "color") span.setSpan(ForegroundColorSpan(Color.parseColor(option[0])), startSelect, endSelect, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE) else if (type == "backgroundColor") span.setSpan(BackgroundColorSpan(Color.parseColor(option[0])), startSelect, endSelect, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE) else {
                alreadyEdit = true
                for (i in startSelect until endSelect) {
                    val bolds = Arrays.stream(span.getSpans(i, i + 1, StyleSpan::class.java)).filter { x: StyleSpan -> x.style == Typeface.BOLD }.toArray()
                    val italics = Arrays.stream(span.getSpans(i, i + 1, StyleSpan::class.java)).filter { x: StyleSpan -> x.style == Typeface.ITALIC }.toArray()
                    if (bolds.isEmpty() && type == "bold") alreadyEdit = false
                    if (italics.isEmpty() && type == "italic") alreadyEdit = false
                    if (span.getSpans(i, i + 1, UnderlineSpan::class.java).isEmpty() && type == "underline") alreadyEdit = false
                    if (span.getSpans(i, i + 1, StrikethroughSpan::class.java).isEmpty() && type == "strike") alreadyEdit = false
                    if (span.getSpans(i, i + 1, SuperscriptSpan::class.java).isEmpty() && type == "super") alreadyEdit = false
                    if (span.getSpans(i, i + 1, SubscriptSpan::class.java).isEmpty() && type == "sub") alreadyEdit = false
                    if (!alreadyEdit) break
                }
            }
            if (alreadyEdit) {
                val newSpan: Spannable = SpannableStringBuilder(desc!!.text.toString())
                val visuals = generatedSpan(span).split("/".toRegex()).toTypedArray()
                for (v in visuals) {
                    if (v.isEmpty()) continue
                    val value = v.split("!".toRegex()).toTypedArray()
                    val id = value[0].toInt()
                    val id2 = value[1].toInt()
                    for (`val` in value) {
                        if (id != startSelect && id2 != endSelect) {
                            if (`val` == "bold") newSpan.setSpan(StyleSpan(Typeface.BOLD), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            if (`val` == "italic") newSpan.setSpan(StyleSpan(Typeface.ITALIC), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            if (`val` == "underline") newSpan.setSpan(UnderlineSpan(), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            if (`val` == "strike") newSpan.setSpan(StrikethroughSpan(), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            if (`val` == "super") newSpan.setSpan(SuperscriptSpan(), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            if (`val` == "sub") newSpan.setSpan(SubscriptSpan(), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        } else {
                            if (`val` == "bold" && type != "bold") newSpan.setSpan(StyleSpan(Typeface.BOLD), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            if (`val` == "italic" && type != "italic") newSpan.setSpan(StyleSpan(Typeface.ITALIC), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            if (`val` == "underline" && type != "underline") newSpan.setSpan(UnderlineSpan(), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            if (`val` == "strike" && type != "strike") newSpan.setSpan(StrikethroughSpan(), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            if (`val` == "super" && type != "super") newSpan.setSpan(SuperscriptSpan(), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            if (`val` == "sub" && type != "sub") newSpan.setSpan(SubscriptSpan(), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        }
                        if (`val`.contains("size:")) newSpan.setSpan(AbsoluteSizeSpan(`val`.replace("size:", "").toInt(), true), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        if (`val`.contains("backgroundColor:")) newSpan.setSpan(BackgroundColorSpan(`val`.replace("backgroundColor:", "").toInt()), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        if (`val`.contains("color:")) newSpan.setSpan(ForegroundColorSpan(`val`.replace("color:", "").toInt()), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                }
                span = newSpan
            } else {
                if (type == "bold") span.setSpan(StyleSpan(Typeface.BOLD), startSelect, endSelect, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                if (type == "italic") span.setSpan(StyleSpan(Typeface.ITALIC), startSelect, endSelect, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                if (type == "underline") span.setSpan(UnderlineSpan(), startSelect, endSelect, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                if (type == "strike") span.setSpan(StrikethroughSpan(), startSelect, endSelect, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                if (type == "strike") span.setSpan(StrikethroughSpan(), startSelect, endSelect, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                if (type == "super") span.setSpan(SuperscriptSpan(), startSelect, endSelect, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                if (type == "sub") span.setSpan(SubscriptSpan(), startSelect, endSelect, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            desc!!.text = (span as Editable)
            desc!!.setSelection(startSelect, endSelect)
        }
        var endLine = desc!!.text.toString().indexOf("\n", endSelect)
        if (endLine == -1) endLine = desc!!.text.toString().length
        var startLine = desc!!.text.toString().lastIndexOf("\n", startSelect)
        if (startLine == -1) startLine = 0
        if (endLine == startLine && startSelect != 0) {
            endLine = desc!!.text.toString().indexOf("\n", endSelect - 1)
            if (endLine == -1) endLine = desc!!.text.toString().length
            startLine = desc!!.text.toString().lastIndexOf("\n", startSelect - 1)
            if (startLine == -1) startLine = 0
        }
        val span: Spannable = desc!!.text
        when (type) {
            "normal" -> span.setSpan(AlignmentSpan.Standard(Layout.Alignment.ALIGN_NORMAL), startLine, endLine, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            "center" -> span.setSpan(AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), startLine, endLine, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            "opposite" -> span.setSpan(AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE), startLine, endLine, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        desc!!.text = (span as Editable)
        desc!!.setSelection(startSelect, endSelect)
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private fun editSpan(view: View?, type: String) {
        view!!.setOnClickListener { editSpan2(type) }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private fun editSpanColor(view: TextView?, type: String) {
        view!!.setOnClickListener {
            val dialog = ColorNote(activity!!)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.build()
            dialog.editColorPicker()
            dialog.update(this, type)
            dialog.hexColor.setOnEditorActionListener { _: TextView?, _: Int, _: KeyEvent? ->
                val hex = dialog.editColorHex()
                editSpan2(type, hex)
                false
            }
            dialog.valid.setOnClickListener {
                val hex = dialog.editColorHex()
                editSpan2(type, hex)
                dialog.dismiss()
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun generatedSpan(span: Spannable): String {
        var visual = StringBuilder()
        for (i in desc!!.text.toString().indices) {
            if (span.getSpans(i, i + 1, StyleSpan::class.java).isNotEmpty()) {
                visual.append(if (visual.isEmpty()) "" else "/").append(i).append("!").append(i + 1)
                for (style in span.getSpans(i, i + 1, StyleSpan::class.java)) {
                    if (style.style == Typeface.BOLD) visual.append("!bold")
                    if (style.style == Typeface.ITALIC) visual.append("!italic")
                }
            }
            if (span.getSpans(i, i + 1, ForegroundColorSpan::class.java).isNotEmpty()) {
                visual.append(if (visual.isEmpty()) "" else "/").append(i).append("!").append(i + 1).append("!color:").append(span.getSpans(i, i + 1, ForegroundColorSpan::class.java)[span.getSpans(i, i + 1, ForegroundColorSpan::class.java).size - 1].foregroundColor)
            }
            if (span.getSpans(i, i + 1, BackgroundColorSpan::class.java).isNotEmpty()) {
                visual.append(if (visual.isEmpty()) "" else "/").append(i).append("!").append(i + 1).append("!backgroundColor:").append(span.getSpans(i, i + 1, BackgroundColorSpan::class.java)[span.getSpans(i, i + 1, BackgroundColorSpan::class.java).size - 1].backgroundColor)
            }
            if (span.getSpans(i, i + 1, AbsoluteSizeSpan::class.java).isNotEmpty()) {
                visual.append(if (visual.isEmpty()) "" else "/").append(i).append("!").append(i + 1).append("!size:").append(span.getSpans(i, i + 1, AbsoluteSizeSpan::class.java)[span.getSpans(i, i + 1, AbsoluteSizeSpan::class.java).size - 1].size)
            }
            if (span.getSpans(i, i + 1, UnderlineSpan::class.java).isNotEmpty()) {
                visual.append(if (visual.isEmpty()) "" else "/").append(i).append("!").append(i + 1).append("!underline")
            }
            if (span.getSpans(i, i + 1, StrikethroughSpan::class.java).isNotEmpty()) {
                visual.append(if (visual.isEmpty()) "" else "/").append(i).append("!").append(i + 1).append("!strike")
            }
            val alignmentSpan = span.getSpans(i, i + 1, AlignmentSpan::class.java)
            if (alignmentSpan.isNotEmpty()) {
                if (alignmentSpan[alignmentSpan.size - 1].alignment == Layout.Alignment.ALIGN_CENTER) visual.append(if (visual.isEmpty()) "" else "/").append(i).append("!").append(i + 1).append("!center") else if (alignmentSpan[alignmentSpan.size - 1].alignment == Layout.Alignment.ALIGN_OPPOSITE) visual.append(if (visual.isEmpty()) "" else "/").append(i).append("!").append(i + 1).append("!opposite")
            }
            val subscriptSpan = span.getSpans(i, i + 1, SubscriptSpan::class.java)
            val superscriptSpans = span.getSpans(i, i + 1, SuperscriptSpan::class.java)
            if (subscriptSpan.size > superscriptSpans.size) visual.append(if (visual.isEmpty()) "" else "/").append(i).append("!").append(i + 1).append("!sub") else if (subscriptSpan.size < superscriptSpans.size) visual.append(if (visual.isEmpty()) "" else "/").append(i).append("!").append(i + 1).append("!super")
        }
        val visuals = visual.toString().split("/".toRegex()).toTypedArray()
        var y = 0
        while (y < visuals.size) {
            if (visuals[y].isEmpty()) {
                y++
                continue
            }
            val p = visuals[y].split("!".toRegex()).toTypedArray()[1]
            var position = -1
            for (u in visuals.indices) {
                if (visuals[u].split("!".toRegex()).toTypedArray()[0] == p) position = u
            }
            if (position != -1) {
                val options1 = visuals[y].split("!".toRegex()).toTypedArray().copyOfRange(2, visuals[y].split("!".toRegex()).toTypedArray().size)
                val options2 = visuals[position].split("!".toRegex()).toTypedArray().copyOfRange(2, visuals[position].split("!".toRegex()).toTypedArray().size)
                if (java.lang.String.join("!", *options1) == java.lang.String.join("!", *options2)) {
                    val newOption = visuals[y].split("!".toRegex()).toTypedArray()
                    newOption[1] = (newOption[1].toInt() + 1).toString() + ""
                    visuals[y] = java.lang.String.join("!", *newOption)
                    visuals[position] = ""
                } else y++
            } else y++
        }
        visual = StringBuilder()
        for (v in visuals) {
            if (v.isEmpty()) continue
            visual.append(if (visual.isEmpty()) "" else "/").append(v)
        }
        this.visual = visual.toString()
        return visual.toString()
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private fun requestCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST_CAMERA)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera()
            } else {
                Toast.makeText(context, "Camera Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private fun startCamera() {
        cameraProviderFuture!!.addListener({
            try {
                val cameraProvider = cameraProviderFuture!!.get()
                bindCameraPreview(cameraProvider)
            } catch (e: ExecutionException) {
                Toast.makeText(context, "Error starting camera " + e.message, Toast.LENGTH_SHORT).show()
            } catch (e: InterruptedException) {
                Toast.makeText(context, "Error starting camera " + e.message, Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(context))
    }

    private var preview: Preview? = null
    private var cameraSelector: CameraSelector? = null

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private fun bindCameraPreview(cameraProvider: ProcessCameraProvider) {
        previewView!!.implementationMode = PreviewView.ImplementationMode.PERFORMANCE
        if (preview == null) {
            preview = Preview.Builder()
                    .build()
            cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build()
            preview!!.setSurfaceProvider(previewView!!.surfaceProvider)
            imageAnalysis = ImageAnalysis.Builder()
                    .setTargetResolution(Size(descWidth, descHeight))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
        }
        imageAnalysis!!.setAnalyzer(ContextCompat.getMainExecutor(context), QRCodeImageAnalyzer(object : QRCodeFoundListener {
            override fun onQRCodeFound(qrCodeString: String?) {
                qrCode = qrCodeString
                val arrayQRCode = qrCode!!.split("ù____NEW____ù".toRegex()).toTypedArray()
                if (arrayQRCode.size == 3) {
                    editNote(arrayQRCode)
                    previewView!!.visibility = View.INVISIBLE
                    imageAnalysis!!.clearAnalyzer()
                    Toast.makeText(context, "La note a bien été modifié !", Toast.LENGTH_SHORT).show()
                    desc!!.visibility = View.VISIBLE
                }
            }

            override fun qrCodeNotFound() {}
        }))
        cameraProvider.bindToLifecycle(this, cameraSelector!!, imageAnalysis, preview)
    }

    private fun imageChooser() {
        val i = Intent()
        i.type = "image/*"
        i.action = Intent.ACTION_GET_CONTENT
        activity!!.startActivityForResult(Intent.createChooser(i, "Select Picture"), 200)
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == 200) {
                val selectedImageUri = data!!.data
                if (null != selectedImageUri) {
                    try {
                        val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, selectedImageUri)
                        val intArray = IntArray(bitmap.width * bitmap.height)
                        bitmap.getPixels(intArray, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
                        val source: LuminanceSource = RGBLuminanceSource(bitmap.width, bitmap.height, intArray)
                        val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
                        try {
                            val result = QRCodeMultiReader().decode(binaryBitmap)
                            qrCode = result.text
                            val arrayQRCode = qrCode!!.split("ù____NEW____ù".toRegex()).toTypedArray()
                            if (arrayQRCode.size == 3) {
                                editNote(arrayQRCode)
                                Toast.makeText(context, "La note a bien été modifié !", Toast.LENGTH_SHORT).show()
                            } else Toast.makeText(context, "Le Qrcode n'a pas été trouvé", Toast.LENGTH_SHORT).show()
                        } catch (ignored: FormatException) {
                            Toast.makeText(context, "Le Qrcode n'a pas été trouvé", Toast.LENGTH_SHORT).show()
                        } catch (ignored: ChecksumException) {
                            Toast.makeText(context, "Le Qrcode n'a pas été trouvé", Toast.LENGTH_SHORT).show()
                        } catch (ignored: NotFoundException) {
                            Toast.makeText(context, "Le Qrcode n'a pas été trouvé", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: IOException) {
                        Toast.makeText(context, "Le Qrcode n'a pas été trouvé", Toast.LENGTH_SHORT).show()
                    }
                }
            } else if (requestCode == 201) {
                val selectedImageUri = data!!.data
                if (null != selectedImageUri) {
                    try {
                        var bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, selectedImageUri)
                        if (bitmap.width > desc!!.width) {
                            val height = bitmap.width * desc!!.width / bitmap.height
                            bitmap = Bitmap.createScaledBitmap(bitmap, desc!!.width, height, false)
                        }
                        if (bitmap.height > desc!!.height / 2) {
                            val width = bitmap.height * (desc!!.width / 2) / bitmap.width
                            bitmap = Bitmap.createScaledBitmap(bitmap, width, desc!!.width / 2, false)
                        }
                        val startSelect = desc!!.selectionStart
                        val span: Spannable = desc!!.text
                        span.setSpan(ImageSpan(context, bitmap), startSelect - 1, startSelect, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                        desc!!.text = (span as Editable)
                        desc!!.setSelection(startSelect)
                    } catch (e: IOException) {
                        Toast.makeText(context, "L'image n'a pas été trouvé", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    fun editNote(arrayQRCode: Array<String>) {
        title!!.setText(arrayQRCode[0])
        desc!!.setText(arrayQRCode[1])
        val visuals = arrayQRCode[2].split("/".toRegex()).toTypedArray()
        val span: Spannable = desc!!.text
        for (v in visuals) {
            if (v.isEmpty()) continue
            val value = v.split("!".toRegex()).toTypedArray()
            val id = value[0].toInt()
            val id2 = value[1].toInt()
            for (`val` in value) {
                if (`val` == "bold") span.setSpan(StyleSpan(Typeface.BOLD), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                if (`val` == "italic") span.setSpan(StyleSpan(Typeface.ITALIC), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                if (`val`.contains("color:")) span.setSpan(ForegroundColorSpan(`val`.replace("color:", "").toInt()), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                if (`val`.contains("backgroundColor:")) span.setSpan(BackgroundColorSpan(`val`.replace("backgroundColor:", "").toInt()), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                if (`val` == "underline") span.setSpan(UnderlineSpan(), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                if (`val` == "strike") span.setSpan(StrikethroughSpan(), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                if (`val`.contains("size:")) span.setSpan(AbsoluteSizeSpan(`val`.replace("size:", "").toInt(), true), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                if (`val` == "opposite") span.setSpan(AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                if (`val` == "center") span.setSpan(AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                if (`val` == "super") span.setSpan(SuperscriptSpan(), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                if (`val` == "sub") span.setSpan(SubscriptSpan(), id, id2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        desc!!.text = (span as Editable)
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private fun reSize() {
        val metrics = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(metrics)
        val phoneWidth = metrics.widthPixels
        val phoneHeight = metrics.heightPixels
        val textView2Height = (phoneHeight / 12.0).toInt()
        val titleSize = (textView2Height / 2.5).toInt()
        val titleWidth = phoneWidth - titleSize * 2 - (phoneWidth / 10.0).toInt()
        size.reSizing(textView2!!, phoneWidth, textView2Height)
        size.reSizing(star!!, titleSize, titleSize)
        size.reSizing(space11!!, (titleSize / 2.0).toInt(), 0)
        size.reSizing(option!!, titleSize, titleSize)
        size.reSizing(title!!, titleWidth, titleSize, true)
        size.reSizing(space!!, phoneWidth - (phoneWidth * 0.1).toInt(), 0)
        size.reSizing(space5!!, (phoneHeight * 0.02).toInt(), (phoneHeight * 0.02).toInt())
        size.reSizing(space3!!, (phoneHeight * 0.02).toInt(), (phoneHeight * 0.02).toInt())
        size.reSizing(space4!!, 0, (phoneHeight * 0.02).toInt())
        val buttonWidth = (phoneWidth / 19.0 * 2.0).toInt()
        val buttonHeight = (phoneHeight / 16.0).toInt()
        val iconButtonSize = buttonWidth.coerceAtMost(buttonHeight) - (buttonWidth.coerceAtMost(buttonHeight) * 0.40).toInt()
        size.reSizing(button!!, buttonWidth.coerceAtMost(buttonHeight), buttonWidth.coerceAtMost(buttonHeight))
        size.reSizing(iconButton!!, iconButtonSize, iconButtonSize)
        var blockWidth = (phoneWidth / 19.0 * 11.0).toInt()
        val blockHeight = (phoneHeight / 8.0).toInt()
        descHeight = phoneHeight - titleSize - (phoneHeight * 0.02 * 7).toInt() - blockHeight
        descWidth = phoneWidth - (phoneWidth * 0.1).toInt()
        desc!!.scrollBarSize = 1
        size.reSizing(desc!!, descWidth, descHeight)
        size.reSizing(previewView!!, descWidth, descHeight)
        val bigButton = (blockWidth / 6.0).toInt().coerceAtMost((blockHeight / 2.5).toInt())
        val mediumButton = bigButton - (bigButton * 0.50).toInt()
        blockWidth = bigButton * 6
        size.reSizing(arrayOf(bold!!, italic!!, color!!, backgroundColor!!, underline!!, strike!!), bigButton, bigButton, true, true)
        size.reSizing(arrayOf(super1!!, sub2!!, addImage!!, normal!!, center!!, opposite!!, textView5!!, textView6!!), mediumButton, mediumButton, true, true)
        size.reSizing(blockMarkDown!!, blockWidth, blockHeight)
        size.reSizing(seekBar!!, blockWidth - mediumButton * 3, mediumButton)
    }

    companion object {
        private const val PERMISSION_REQUEST_CAMERA = 0
    }
}