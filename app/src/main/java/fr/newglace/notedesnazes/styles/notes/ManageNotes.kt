package fr.newglace.notedesnazes.styles.notes

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.text.InputFilter
import android.text.Spanned
import android.util.DisplayMetrics
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import fr.newglace.notedesnazes.R
import fr.newglace.notedesnazes.activity.MainActivity
import fr.newglace.notedesnazes.database.note.local.MyDatabaseHelper
import fr.newglace.notedesnazes.database.note.local.Note
import fr.newglace.notedesnazes.styles.Colors
import fr.newglace.notedesnazes.styles.ReSize
import fr.newglace.notedesnazes.styles.dialog.NewFolder
import fr.newglace.notedesnazes.styles.dialog.OptionNote
import fr.newglace.notedesnazes.styles.dialog.PasswordNote
import fr.newglace.notedesnazes.styles.dialog.PasswordNoteConfig
import java.util.*

class ManageNotes(private val activity: MainActivity, private val notes: List<NoteArray>, listView: ListView?, select: Boolean, vararg selectAll: Int) : BaseAdapter() {
    private val inflater: LayoutInflater = LayoutInflater.from(activity)
    private val db: MyDatabaseHelper = MyDatabaseHelper(activity)
    private var select: Boolean
    private var selectAll = 0
    private var color = ""
    fun setColor(color: String) {
        this.color = color
    }

    private val filter = InputFilter { charSequence: CharSequence?, _: Int, _: Int, _: Spanned?, _: Int, _: Int ->
        val blockCharSet = "\n"
        if (charSequence != null && blockCharSet.contains(charSequence.toString())) return@InputFilter "" else if (charSequence != null && charSequence.toString().length > 16) return@InputFilter charSequence.toString().subSequence(0, 16) else return@InputFilter null
    }

    override fun getCount(): Int {
        return notes.size
    }

    override fun getItem(i: Int): NoteArray {
        return notes[i]
    }

    override fun getItemId(i: Int): Long {
        return 0
    }

    @SuppressLint("InflateParams")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun getView(i: Int, convertView: View?, parent: ViewGroup): View? {
        val item = getItem(i)
        val folder = item.folder
        val isFolder = folder.isNotEmpty() && activity.isFolder
        val view = convertView
                ?: if (isFolder && select) inflater.inflate(R.layout.folder_layout2, parent, false) else if (isFolder) inflater.inflate(R.layout.folder_layout, parent, false) else if (select) inflater.inflate(R.layout.note_layout2, parent, false) else inflater.inflate(R.layout.note_layout, parent, false)

        val size = ReSize()
        val title = item.title
        val desc = item.desc
        val password = arrayOf(item.password)
        val favorite = booleanArrayOf(item.isFavorite)
        val visual = item.visual
        val folderPosition = item.folderPosition
        val p = item.position
        val position = item.i
        val titleNote = view.findViewById<TextView>(R.id.title)
        val descNote = view.findViewById<TextView>(R.id.desc)
        val option = view.findViewById<ImageView>(R.id.options)
        val background = view.findViewById<ImageView>(R.id.textView3)
        val checkBox = view.findViewById<CheckBox>(R.id.checkbox)
        val space5 = view.findViewById<Space>(R.id.space5)
        val space6 = view.findViewById<Space>(R.id.space6)
        val space7 = view.findViewById<Space>(R.id.space7)
        val draw = activity.getDrawable(R.drawable.bg_notes)
        val draw2 = activity.getDrawable(R.drawable.bg_folder)
        val metrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(metrics)
        val phoneWidth = metrics.widthPixels
        val phoneHeight = metrics.heightPixels
        val titleSize = (phoneHeight / 30.0).toInt()
        val objectSize = (phoneHeight / 36.0).toInt()
        val backWidth = (phoneWidth - (phoneWidth * 0.13)).toInt()
        val backHeight = ((phoneHeight - titleSize - phoneHeight * 0.06 - phoneHeight / 8.0) / 9.0).toInt()
        val spaceSize = (backHeight / 12.0).toInt()
        val backWidthOpenBox = backWidth - spaceSize * 5
        val newObjectSize = (objectSize * 1.4 / 80.0).toFloat()
        checkBox.scaleX = newObjectSize
        checkBox.scaleY = newObjectSize
        size.reSizing(option, objectSize, objectSize)
        size.reSizing(titleNote, (backWidth / 1.5).toInt(), (backHeight / 1.5).toInt(), true, true)
        size.reSizing(descNote, (backWidth / 1.5).toInt(), (backHeight / 2.5).toInt(), true, true)
        size.reSizing(space5, spaceSize * 2, spaceSize)
        size.reSizing(space6, spaceSize * 2, spaceSize)
        size.reSizing(space7, spaceSize * 2, spaceSize)
        if (select) size.reSizing(background, backWidthOpenBox, backHeight) else size.reSizing(background, backWidth, backHeight)
        if (select && selectAll == 1) checkBox.isChecked = true
        checkBox.setOnCheckedChangeListener { _: CompoundButton?, b: Boolean ->
            val value = if (folder.isNotEmpty() && activity.isFolder) "f-$folder" else position.toString() + ""
            if (b) activity.addSelect(value) else activity.removeSelect(value)
        }
        val waiting = booleanArrayOf(false)
        background.setOnLongClickListener {
            if (!select) {
                checkBox.isChecked = true
                select = true
                waiting[0] = true
            } else select = false
            activity.setSelect(select)
            false
        }
        val colors = Colors(activity)
        colors.editColor(background, 1, draw)
        if (isFolder) {
            val background2 = view.findViewById<ImageView>(R.id.textView4)
            size.reSizing(background2, (backHeight / 2.0).toInt(), backHeight)
            editColor(item.colorFolder, draw2, background2)
        }
        if (favorite[0]) editColor("#D5B200", draw, background)
        val optionUnClick = booleanArrayOf(true)
        titleNote.text = title
        descNote.text = desc
        val alphabet = "abcdefghijklmnopqrstuvwxyz".split("".toRegex()).toTypedArray()
        val oldText = arrayOf("")
        val h = Handler()
        val finalView = view
        val r: Runnable = object : Runnable {
            override fun run() {
                if (color != "") {
                    colors.editView(color, 1)
                    colors.editColor(background, 1, draw)
                    if (isFolder) {
                        val background2 = finalView.findViewById<ImageView>(R.id.textView4)
                        editColor(item.colorFolder, draw2, background2)
                    }
                    color = ""
                }
                val value = if (folder.isNotEmpty() && activity.isFolder) "f-$folder" else position.toString() + ""
                if (checkBox.isChecked != activity.getSelect(value)) checkBox.isChecked = activity.getSelect(value)
                if (select && checkBox.visibility == View.INVISIBLE) {
                    val params = background.layoutParams
                    params.width = backWidthOpenBox
                    background.layoutParams = params
                    checkBox.visibility = View.VISIBLE
                } else if (!select && checkBox.visibility == View.VISIBLE) {
                    val params = background.layoutParams
                    params.width = backWidth
                    background.layoutParams = params
                    checkBox.visibility = View.INVISIBLE
                    checkBox.isChecked = false
                }
                if (!(folder.isNotEmpty() && activity.isFolder)) {
                    if (password[0].isNotEmpty()) {
                        val falseText = StringBuilder()
                        for (i in 0 until desc.length.coerceAtMost(20)) {
                            if (desc.split("".toRegex()).toTypedArray()[i] == " ") {
                                falseText.append(" ")
                            } else {
                                if (Math.random() > 0.3 || oldText[0] == "") {
                                    falseText.append(alphabet[(Math.random() * 26).toInt()])
                                } else {
                                    falseText.append(oldText[0].split("".toRegex()).toTypedArray()[i])
                                }
                            }
                        }
                        oldText[0] = falseText.toString()
                        descNote.text = falseText.toString()
                    } else {
                        descNote.text = descNote.text.toString()
                    }
                }
                h.postDelayed(this, 100)
            }
        }
        h.postDelayed(r, 0)
        if (descNote.text.isEmpty()) {
            view.visibility = View.INVISIBLE
            return view
        }
        if (folder.isNotEmpty() && activity.isFolder) {
            titleNote.text = folder
            descNote.text = ""
            background.setOnClickListener {
                if (select) {
                    if (!waiting[0]) checkBox.isChecked = !checkBox.isChecked else waiting[0] = false
                } else activity.getFolder(titleNote.text.toString())
            }
            option.setOnClickListener { v: View? ->
                val dialog = OptionNote(activity)
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.build()
                dialog.lockText.text = "Renommer"
                dialog.imageView5.setImageDrawable(activity.getDrawable(R.drawable.pen_icon))
                dialog.delete.setOnClickListener {
                    var deleteNumber = 0
                    val dbAll = db.notesCount
                    val delete = StringBuilder("!")
                    for (id in 0 until dbAll) {
                        val note = db.getNote(id)
                        if (note.folder == titleNote.text.toString()) delete.append(id).append("!")
                    }
                    if (delete.toString().length > 1) {
                        for (id in 0 until dbAll) {
                            if (delete.toString().contains("!$id!")) {
                                db.deleteNote(id - deleteNumber)
                                deleteNumber++
                            }
                        }
                        activity.noteList(activity.searchBar!!.text.toString(), activity.selectFolder)
                    }
                    dialog.dismiss()
                }
                dialog.password.setOnClickListener {
                    dialog.dismiss()
                    val d = NewFolder(activity)
                    d.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    d.build()
                    val f = d.newFolder
                    f.filters = arrayOf(filter)
                    f.setOnEditorActionListener { _: TextView?, _: Int, _: KeyEvent? ->
                        if (f.text.toString().isNotEmpty()) {
                            for (id in 0 until db.notesCount) {
                                val note = db.getNote(id)
                                if (note.folder == titleNote.text.toString()) {
                                    db.editNote(id, Note(note.noteTitle, note.noteContent, note.isFavorite, note.password, note.visual, f.text.toString(), note.position, 0, note.colorFolder))
                                }
                            }
                            titleNote.text = f.text.toString()
                            d.dismiss()
                        }
                        false
                    }
                }
            }
            return view
        }
        option.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                if (password[0].isEmpty()) {
                    if (optionUnClick[0]) startOptions()
                } else {
                    val dialog = PasswordNote(activity)
                    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    dialog.build()
                    dialog.password.setOnEditorActionListener { _: TextView?, _: Int, _: KeyEvent? ->
                        if (password[0] == dialog.password.text.toString().replace("\n", "")) {
                            dialog.dismiss()
                            startOptions()
                        } else Toast.makeText(activity, "Mot de passe incorrect !", Toast.LENGTH_SHORT).show()
                        false
                    }
                }
            }

            private fun startOptions() {
                optionUnClick[0] = false
                val dialog = OptionNote(activity)
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.build()
                optionUnClick[0] = true
                if (password[0].isNotEmpty()) {
                    dialog.lockText.text = "Déverrouiller"
                    dialog.imageView5.setImageDrawable(activity.getDrawable(R.drawable.password_open))
                }
                colors.editColor(background, 1, draw)
                if (favorite[0]) editColor("#D5B200", draw, background)
                dialog.delete.setOnClickListener {
                    if (activity.getSelect(i.toString() + "")) activity.removeSelect(i.toString() + "")
                    db.deleteNote(i)
                    Toast.makeText(activity, "La note a été supprimé", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    activity.noteList(activity.searchBar!!.text.toString(), activity.selectFolder)
                }
                dialog.favorite.setOnClickListener {
                    db.editNote(position, Note(title, desc, !favorite[0], password[0], visual, folder, p, folderPosition, item.colorFolder))
                    if (favorite[0]) {
                        Toast.makeText(activity, "La note a été retiré des favoris", Toast.LENGTH_SHORT).show()
                        editColor(colors.getColor(1), draw, background)
                    } else {
                        Toast.makeText(activity, "La note a été ajouté aux favoris", Toast.LENGTH_SHORT).show()
                        editColor("#D5B200", draw, background)
                    }
                    favorite[0] = !favorite[0]
                    dialog.dismiss()
                }
                dialog.password.setOnClickListener {
                    if (password[0] == "") {
                        val dialog2 = PasswordNoteConfig(activity)
                        dialog2.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        dialog2.build()
                        val pass = dialog2.password
                        pass.setOnKeyListener { _: View?, keyCode: Int, event: KeyEvent ->
                            if (event.action == KeyEvent.ACTION_DOWN &&
                                    keyCode == KeyEvent.KEYCODE_ENTER) {
                                if (pass.length() > 0) {
                                    Toast.makeText(activity, "La note a été verrouillée", Toast.LENGTH_SHORT).show()
                                    db.editNote(position, Note(title, desc, favorite[0], pass.text.toString(), visual, folder, p, folderPosition, item.colorFolder))
                                    password[0] = pass.text.toString()
                                    dialog.dismiss()
                                    dialog2.dismiss()
                                    return@setOnKeyListener true
                                }
                            }
                            dialog.dismiss()
                            false
                        }
                    } else {
                        Toast.makeText(activity, "La note a été déverrouillée", Toast.LENGTH_SHORT).show()
                        password[0] = ""
                        descNote.text = desc
                        db.editNote(position, Note(title, desc, favorite[0], password[0], visual, folder, p, folderPosition, item.colorFolder))
                        dialog.dismiss()
                    }
                }
            }
        })
        background.setOnClickListener {
            if (select) {
                if (!waiting[0]) checkBox.isChecked = !checkBox.isChecked else waiting[0] = false
                return@setOnClickListener
            }
            if (password[0].isEmpty()) {
                if (optionUnClick[0]) activity.launchActivity(position)
            } else {
                val dialog = PasswordNote(activity)
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.build()
                dialog.password.setOnEditorActionListener { _: TextView?, _: Int, _: KeyEvent? ->
                    if (password[0] == dialog.password.text.toString().replace("\n", "")) {
                        dialog.dismiss()
                        activity.launchActivity(position)
                    } else {
                        Toast.makeText(activity, "Mot de passe incorrect !", Toast.LENGTH_SHORT).show()
                    }
                    false
                }
            }
        }
        return view
    }

    private fun editColor(c: String, draw: Drawable?, view: View) {
        val color = Color.parseColor(c)
        if (draw != null) {
            draw.setColorFilter(color, PorterDuff.Mode.LIGHTEN)
            view.background = draw
        }
    }

    init {
        this.select = select
        if (selectAll.size == 1) this.selectAll = selectAll[0]
    }
}