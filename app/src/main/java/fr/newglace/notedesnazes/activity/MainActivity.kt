package fr.newglace.notedesnazes.activity

import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.InputFilter
import android.text.Spanned
import android.util.DisplayMetrics
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import fr.newglace.notedesnazes.R
import fr.newglace.notedesnazes.database.note.local.MyDatabaseHelper
import fr.newglace.notedesnazes.database.note.local.Note
import fr.newglace.notedesnazes.styles.Colors
import fr.newglace.notedesnazes.styles.ReSize
import fr.newglace.notedesnazes.styles.dialog.*
import fr.newglace.notedesnazes.styles.notes.ManageNotes
import fr.newglace.notedesnazes.styles.notes.NoteArray
import java.util.*

class MainActivity : AppCompatActivity() {
    //text
    private var search: ImageView? = null
    private var dustbin: ImageView? = null
    private var favorite: ImageView? = null
    private var selectAll: ImageView? = null
    private var move: ImageView? = null
    private var falseSearch: ImageView? = null
    private var option: ImageView? = null
    private var selects = "!"
    private var selectString = selects
    var selectFolder = ""
        private set
    private val db = MyDatabaseHelper(this)
    private var space3: Space? = null
    private var space2: Space? = null
    private var space: Space? = null
    private var space4: Space? = null
    private var space5: Space? = null
    private var space6: Space? = null
    private var title: TextView? = null
    private var footer: TextView? = null
    private var cross: TextView? = null
    private var button: TextView? = null
    private var textView2: TextView? = null
    private var textView7: TextView? = null
    private val size = ReSize()
    private val check = intArrayOf(0, 0)
    private var select = false
    var searchBar: EditText? = null
        private set
    private var notes1: ListView? = null
    private var colors: Colors? = null
    fun getSelect(id: String): Boolean {
        return selects.contains("!$id!")
    }

    private var manageNotes: ManageNotes? = null

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun addSelect(id: String) {
        if (!selects.contains("!$id!")) selects += "$id!"
        if (id.contains("f-")) {
            val color = Color.parseColor("#606060")
            move!!.setColorFilter(color)
        }
        if (selects == selectString) {
            val drawable = getDrawable(R.drawable.select_all2)
            if (selectAll!!.drawable !== drawable) selectAll!!.setImageDrawable(drawable)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun removeSelect(id: String) {
        selects = selects.replace("$id!", "")
        val drawable = getDrawable(R.drawable.select_all)
        if (selects != selectString) selectAll!!.setImageDrawable(drawable)
        if (!selects.contains("f-")) {
            val color = Color.parseColor("#ffffff")
            move!!.setColorFilter(color)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun setSelect(select: Boolean) {
        this.select = select
        if (select) {
            footer!!.visibility = View.VISIBLE
            dustbin!!.visibility = View.VISIBLE
            favorite!!.visibility = View.VISIBLE
            selectAll!!.visibility = View.VISIBLE
            move!!.visibility = View.VISIBLE
            cross!!.visibility = View.INVISIBLE
            button!!.visibility = View.INVISIBLE
            if (!selects.contains("f-")) {
                val color = Color.parseColor("#ffffff")
                move!!.setColorFilter(color)
            }
        } else {
            footer!!.visibility = View.INVISIBLE
            dustbin!!.visibility = View.INVISIBLE
            favorite!!.visibility = View.INVISIBLE
            selectAll!!.visibility = View.INVISIBLE
            move!!.visibility = View.INVISIBLE
            cross!!.visibility = View.VISIBLE
            button!!.visibility = View.VISIBLE
            selects = "!"
            val drawable = getDrawable(R.drawable.select_all)
            if (selectAll!!.drawable !== drawable) selectAll!!.setImageDrawable(drawable)
        }
    }

    private val filter = InputFilter { charSequence: CharSequence?, _: Int, _: Int, _: Spanned?, _: Int, _: Int ->
        val blockCharSet = "\n"
        if (charSequence != null && blockCharSet.contains(charSequence.toString())) return@InputFilter "" else if (charSequence != null && charSequence.toString().length > 16) return@InputFilter charSequence.toString().subSequence(0, 16) else return@InputFilter null
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun onBackPressed() {
        if (check[0] != 0) {
            check[0] = 0
            searchBar!!.setText("")
            searchBar!!.visibility = View.INVISIBLE
            search!!.animate().setDuration(500).x(falseSearch!!.x).withEndAction { title!!.visibility = View.VISIBLE }
        } else if (select) {
            noteList(searchBar!!.text.toString(), selectFolder)
            footer!!.visibility = View.INVISIBLE
            dustbin!!.visibility = View.INVISIBLE
            favorite!!.visibility = View.INVISIBLE
            selectAll!!.visibility = View.INVISIBLE
            move!!.visibility = View.INVISIBLE
            cross!!.visibility = View.VISIBLE
            button!!.visibility = View.VISIBLE
            selects = "!"
            select = false
            noteList("", selectFolder)
            val drawable = getDrawable(R.drawable.select_all)
            if (selectAll!!.drawable !== drawable) selectAll!!.setImageDrawable(drawable)
        } else if (selectFolder.isNotEmpty()) {
            getFolder("")
        } else super.onBackPressed()
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun editColor(bool: Boolean) {
        if (bool) {
            colors = Colors(this)
            editColor()
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private fun editColor() {
        colors!!.editColor(footer!!, 1, null)
        colors!!.editColor(textView2!!, 1, null)
        colors!!.editColor(textView7!!, 0, null)
        val draw2 = getDrawable(R.drawable.button)
        colors!!.editColor(button!!, 2, draw2)
        val window = window
        window.navigationBarColor = Color.parseColor(colors!!.getColor(1))
        window.statusBarColor = Color.parseColor(colors!!.getColor(1))
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun editColor(color: String?, position: Int) {
        if (position == 1) {
            if (color != null) {
                manageNotes!!.setColor(color)
            }
            //manageNotes.setColor("");
        }
        if (color != null) {
            colors!!.editView(color, position)
        }
        editColor()
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        falseSearch = findViewById(R.id.false_magnifying_glass)
        notes1 = findViewById(R.id.note_list)
        button = findViewById(R.id.add_notes)
        title = findViewById(R.id.textView)
        search = findViewById(R.id.magnifying_glass)
        searchBar = findViewById(R.id.search_bar)
        footer = findViewById(R.id.textView4)
        dustbin = findViewById(R.id.dustbin)
        favorite = findViewById(R.id.favorite)
        selectAll = findViewById(R.id.select_all)
        move = findViewById(R.id.move)
        cross = findViewById(R.id.cross)
        space2 = findViewById(R.id.space2)
        space3 = findViewById(R.id.space3)
        space4 = findViewById(R.id.space4)
        space5 = findViewById(R.id.space5)
        space6 = findViewById(R.id.space6)
        textView2 = findViewById(R.id.textView2)
        space = findViewById(R.id.space)
        option = findViewById(R.id.options)
        textView7 = findViewById(R.id.textView7)
        colors = Colors(this)
        colors!!.editColor(footer!!, 1, null)
        colors!!.editColor(textView2!!, 1, null)
        colors!!.editColor(textView7!!, 0, null)
        val draw2 = getDrawable(R.drawable.button)
        colors!!.editColor(button!!, 2, draw2)
        val window = window
        window.navigationBarColor = Color.parseColor(colors!!.getColor(1))
        window.statusBarColor = Color.parseColor(colors!!.getColor(1))
        reSize()
        val oldSearch = arrayOf("")
        noteList(oldSearch[0], selectFolder)
        option!!.setOnClickListener {
            val configNote = ConfigNote(this)
            configNote.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            configNote.build()
            configNote.editColor.setOnClickListener {
                configNote.dismiss()
                val backgroundStyle = BackgroundStyle(this)
                backgroundStyle.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                backgroundStyle.build()
                for (i in 0..2) {
                    (if (i == 0) backgroundStyle.darkColor else if (i == 1) backgroundStyle.mediumColor else backgroundStyle.lightColor).setOnClickListener {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            val colorNote = ColorNote(this, true)
                            colorNote.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                            colorNote.build()
                            colorNote.editColorPicker()
                            colorNote.update2(this, i)
                            option!!.animate().xBy(1f).setDuration(1).withEndAction { colorNote.setHexColor(colors!!.getColor(i)) }
                            colorNote.hexColor.setOnEditorActionListener { _: TextView?, _: Int, _: KeyEvent? ->
                                val hex = colorNote.editColorHex()
                                editColor(hex, i)
                                false
                            }
                            colorNote.valid.setOnClickListener {
                                val hex = colorNote.editColorHex()
                                editColor(hex, i)
                                colors!!.editColors(this, hex, i)
                                backgroundStyle.editColors()
                                colorNote.dismiss()
                            }
                        }
                    }
                }
            }
        }
        move!!.setOnClickListener {
            if (selects.length > 1 && !selects.contains("f-")) {
                val dialog = OptionMove(this)
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.build()
                dialog.backFolder.setOnClickListener {
                    dialog.dismiss()
                    for (id in selects.subSequence(1, selects.length - 1).toString().split("!".toRegex()).toTypedArray()) {
                        val i = id.toInt()
                        db.editNote(i, Note(db.getNote(i).noteTitle, db.getNote(i).noteContent, db.getNote(i).isFavorite, db.getNote(i).password, db.getNote(i).visual, "", db.getNote(i).position, 0, db.getNote(i).colorFolder))
                    }
                    setSelect(false)
                    noteList(searchBar!!.text.toString(), selectFolder)
                }
                dialog.newFolder.setOnClickListener {
                    dialog.dismiss()
                    val d = NewFolder(this)
                    d.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    d.build()
                    val folder = d.newFolder
                    folder.filters = arrayOf(filter)
                    folder.setOnEditorActionListener { _: TextView?, _: Int, _: KeyEvent? ->
                        if (folder.text.toString().isNotEmpty()) {
                            selectFolder = folder.text.toString()
                            for (id in selects.subSequence(1, selects.length - 1).toString().split("!".toRegex()).toTypedArray()) {
                                val i = id.toInt()
                                db.editNote(i, Note(db.getNote(i).noteTitle, db.getNote(i).noteContent, db.getNote(i).isFavorite, db.getNote(i).password, db.getNote(i).visual, selectFolder, db.getNote(i).position, 0, db.getNote(i).colorFolder))
                            }
                            setSelect(false)
                            noteList(searchBar!!.text.toString(), selectFolder)
                            d.dismiss()
                        }
                        false
                    }
                }
            }
        }
        dustbin!!.setOnClickListener {
            if (selects.length > 1) {
                var deleteNumber = 0
                val dbAll = db.notesCount
                for (i in 0 until dbAll) {
                    if (selects.contains("!$i!")) {
                        db.deleteNote(i - deleteNumber)
                        deleteNumber++
                    }
                }
                val arrayString = selects.substring(1, selects.length - 1).split("!".toRegex()).toTypedArray()
                for (i in arrayString.size - 1 downTo -1 + 1) {
                    if (arrayString[i].contains("f-")) {
                        Log.d("TAG", "Delete Folder")
                    }
                }
                noteList(searchBar!!.text.toString(), selectFolder)
                footer!!.visibility = View.INVISIBLE
                dustbin!!.visibility = View.INVISIBLE
                favorite!!.visibility = View.INVISIBLE
                selectAll!!.visibility = View.INVISIBLE
                move!!.visibility = View.INVISIBLE
                cross!!.visibility = View.VISIBLE
                button!!.visibility = View.VISIBLE
                selects = "!"
                select = false
                noteList(searchBar!!.text.toString(), selectFolder)
                val drawable = getDrawable(R.drawable.select_all)
                if (selectAll!!.drawable !== drawable) selectAll!!.setImageDrawable(drawable)
            }
        }
        selectAll!!.setOnClickListener {
            val drawable = getDrawable(R.drawable.select_all)
            if (selects.length == 1 || selects != selectString) {
                noteList(searchBar!!.text.toString(), selectFolder, 1)
                selects = selectString
                selectAll!!.setImageDrawable(getDrawable(R.drawable.select_all2))
                if (selectString.contains("f-")) move!!.setColorFilter(Color.parseColor("#606060"))
            } else {
                noteList(searchBar!!.text.toString(), selectFolder, 0)
                selects = "!"
                selectAll!!.setImageDrawable(drawable)
                move!!.setColorFilter(Color.parseColor("#ffffff"))
            }
        }
        button!!.setOnClickListener {
            val intent = Intent(applicationContext, NoteActivity::class.java)
            val bundle = Bundle()
            bundle.putInt("id", -1)
            bundle.putString("folder", selectFolder)
            intent.putExtras(bundle)
            startActivity(intent)
        }
        searchBar!!.filters = arrayOf(filter)
        searchBar!!.onFocusChangeListener = OnFocusChangeListener { _: View?, b: Boolean ->
            if (b) check[1] = 1 else check[1] = 0
            if (select) {
                if (!b) {
                    footer!!.visibility = (View.VISIBLE)
                    dustbin!!.visibility = (View.VISIBLE)
                    favorite!!.visibility = (View.VISIBLE)
                    selectAll!!.visibility = (View.VISIBLE)
                    move!!.visibility = (View.VISIBLE)
                } else {
                    footer!!.visibility = (View.INVISIBLE)
                    dustbin!!.visibility = (View.INVISIBLE)
                    favorite!!.visibility = (View.INVISIBLE)
                    selectAll!!.visibility = (View.INVISIBLE)
                    move!!.visibility = (View.INVISIBLE)
                }
            }
        }
        val screenHeight = intArrayOf(0)
        val keypadHeight = intArrayOf(0)
        val updateSearch = booleanArrayOf(false)
        val h = Handler()
        val r2: Runnable = object : Runnable {
            override fun run() {
                if (keypadHeight[0] > screenHeight[0] * 0.15) {
                    if (oldSearch[0] != searchBar!!.text.toString().toLowerCase(Locale.ROOT)) {
                        oldSearch[0] = searchBar!!.text.toString().toLowerCase(Locale.ROOT)
                        noteList(oldSearch[0], selectFolder)
                    }
                    h.postDelayed(this, 50)
                } else updateSearch[0] = false
            }
        }
        searchBar!!.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            searchBar!!.getWindowVisibleDisplayFrame(r)
            screenHeight[0] = searchBar!!.rootView.height
            keypadHeight[0] = screenHeight[0] - r.bottom
            if (screenHeight[0] * 0.15 < keypadHeight[0] && !updateSearch[0]) {
                h.postDelayed(r2, 0)
                updateSearch[0] = true
            }
            if (keypadHeight[0] > screenHeight[0] * 0.15) {
                if (check[0] == 1) check[0] = 0
            } else {
                if (check[1] == 1) {
                    if (check[0] > 10) {
                        if (title!!.visibility == View.INVISIBLE) {
                            if (searchBar!!.text.toString().isEmpty()) {
                                check[0] = 0
                                searchBar!!.setText("")
                                searchBar!!.visibility = View.INVISIBLE
                                search!!.animate().setDuration(500).x(falseSearch!!.x).withEndAction { title!!.visibility = View.VISIBLE }
                            } else {
                                if (select) {
                                    footer!!.visibility = View.VISIBLE
                                    dustbin!!.visibility = View.VISIBLE
                                    favorite!!.visibility = View.VISIBLE
                                    selectAll!!.visibility = View.VISIBLE
                                    move!!.visibility = View.VISIBLE
                                }
                            }
                        }
                    } else {
                        check[0]++
                    }
                }
            }
        }
        search!!.setOnClickListener {
            if (check[1] == 0) {
                title!!.visibility = View.INVISIBLE
                searchBar!!.visibility = View.VISIBLE
                search!!.animate().setDuration(500).x(title!!.x).withEndAction {
                    searchBar!!.requestFocus()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(searchBar, InputMethodManager.SHOW_IMPLICIT)
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun getFolder(folder: String) {
        selectFolder = folder
        noteList(searchBar!!.text.toString(), selectFolder)
    }

    fun launchActivity(id: Int) {
        val intent = Intent(applicationContext, NoteActivity::class.java)
        val bundle = Bundle()
        bundle.putInt("id", id)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun noteList(filter: String?, stringFolder: String, vararg options: Int) {
        if (stringFolder.isEmpty()) title!!.text = this.getString(R.string.your_notes) else title!!.text = stringFolder
        val notes: MutableList<NoteArray> = ArrayList()
        val note = StringBuilder("!")
        val folder = StringBuilder("!")
        var edit = true
        for (i in 0 until db.notesCount) {
            val thisNote = db.getNote(i)
            if (thisNote.noteTitle.toLowerCase(Locale.ROOT).contains(filter!!) ||
                    thisNote.folder.isNotEmpty() && thisNote.folder.toLowerCase(Locale.ROOT).contains(filter) && selectFolder == "" ||
                    thisNote.noteContent.toLowerCase(Locale.ROOT).contains(filter) && thisNote.password.isEmpty()) {
                if (stringFolder.isEmpty() && !folder.toString().contains("!" + thisNote.folder + "!") || stringFolder == thisNote.folder) {
                    notes.add(NoteArray(thisNote.noteTitle, thisNote.noteContent, i, thisNote.isFavorite, thisNote.password,
                            thisNote.visual, thisNote.folder, thisNote.position, thisNote.folderPosition, thisNote.colorFolder))
                    if (thisNote.folder.isNotEmpty() && isFolder) note.append("f-").append(thisNote.folder).append("!") else note.append(i).append("!")
                    if (thisNote.folder.isNotEmpty()) folder.append(thisNote.folder).append("!")
                }
            } else {
                removeSelect(i.toString() + "")
                edit = false
            }
        }
        if (notes.size != 0) {
            if (edit && selects != note.toString()) selectAll!!.setImageDrawable(getDrawable(R.drawable.select_all))
            selectString = note.toString()
            manageNotes = ManageNotes(this, notes, notes1, select, *options)
            notes1!!.adapter = manageNotes
        }
    }

    val isFolder: Boolean
        get() = selectFolder.isEmpty()

    private fun reSize() {
        val metrics = DisplayMetrics()
        this.windowManager.defaultDisplay.getMetrics(metrics)
        val phoneWidth = metrics.widthPixels
        val phoneHeight = metrics.heightPixels
        val textView2Height = (phoneHeight / 12.0).toInt()
        val titleSize = (textView2Height / 2.5).toInt()
        val titleWidth = phoneWidth - titleSize * 2 - (phoneWidth / 10.0).toInt()
        val searchBarWidth = phoneWidth - titleSize * 2 - (phoneWidth / 7.0).toInt()

        size.reSizing(textView2!!, phoneWidth, textView2Height)
        size.reSizing(footer!!, phoneWidth, textView2Height)
        size.reSizing(arrayOf(search!!, falseSearch!!, option!!, favorite!!, selectAll!!, dustbin!!, move!!), titleSize, titleSize)
        size.reSizing(arrayOf(space2!!, space5!!, space6!!), (titleSize / 2.0).toInt(), 0)
        size.reSizing(title!!, titleWidth, titleSize, true)
        size.reSizing(searchBar!!, searchBarWidth, titleSize, true)
        size.reSizing(space!!, phoneWidth - (phoneWidth * 0.1).toInt(), 0)
        size.reSizing(space3!!, (phoneHeight * 0.02).toInt(), (phoneHeight * 0.02).toInt())
        size.reSizing(space4!!, 0, (phoneHeight * 0.02).toInt())
        val buttonWidth = (phoneWidth / 19.0 * 2.0).toInt()
        val buttonHeight = (phoneHeight / 16.0).toInt()
        val iconButtonSize = buttonWidth.coerceAtMost(buttonHeight) - (buttonWidth.coerceAtMost(buttonHeight) * 0.40).toInt()
        size.reSizing(button!!, buttonWidth.coerceAtMost(buttonHeight), buttonWidth.coerceAtMost(buttonHeight))
        size.reSizing(cross!!, iconButtonSize, iconButtonSize)
        val descHeight = phoneHeight - textView2Height * 2 - (phoneHeight * 0.02 * 2).toInt()
        val descWidth = phoneWidth - (phoneWidth * 0.1).toInt()
        size.reSizing(notes1!!, descWidth, descHeight)
    }
}