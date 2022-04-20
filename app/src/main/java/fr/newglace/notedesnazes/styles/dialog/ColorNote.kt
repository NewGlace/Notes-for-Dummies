package fr.newglace.notedesnazes.styles.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.EditText
import android.widget.ImageView
import android.widget.Space
import android.widget.TextView
import androidx.annotation.RequiresApi
import fr.newglace.notedesnazes.R
import fr.newglace.notedesnazes.activity.MainActivity
import fr.newglace.notedesnazes.activity.NoteActivity
import fr.newglace.notedesnazes.styles.Colors
import fr.newglace.notedesnazes.styles.ReSize

class ColorNote @RequiresApi(api = Build.VERSION_CODES.Q) constructor(activity: Activity, vararg mainActivity: Boolean) : Dialog(activity, R.style.Theme_AppCompat_DayNight_Dialog) {
    private val colorPicker: ImageView
    private val imageView: ImageView
    private val hue: ImageView
    private val hueSelect: ImageView
    private val colorSelect: ImageView
    val hexColor: EditText
    val valid: TextView
    private val activity: Activity
    private val space8: Space
    private val space9: Space
    private val space10: Space
    private var colorPickerWidth = 0
    private var colorPickerHeight = 0
    private var colorPickerY = 0f
    private var colorPickerX = 0f
    private var paint: Paint? = null
    private var shader: Shader? = null
    private val color = floatArrayOf(1f, 1f, 1f)
    private var space = 0f
    private var cursorSize = 0f
    private val colors: Colors
    private var strokePaint: Paint? = null
    private var mainActivity = false
    private var hueWidth = 0

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun dismiss() {
        super.dismiss()
        if (mainActivity) (activity as MainActivity).editColor(true)
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun setHexColor(hexColor: String?) {
        this.hexColor.setText(hexColor)
        editColorHex()
    }

    fun editColorPicker() {
        val red = Color.red(getColor())
        val green = Color.green(getColor())
        val blue = Color.blue(getColor())
        colorSelect.setColorFilter(Color.rgb(255 - red, 255 - green, 255 - blue))
        val hex = String.format("#%02x%02x%02x", red, green, blue)
        hexColor.setText(hex)
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun editColorHex(): String {
        var hex = hexColor.text.toString().replace("#", "")
        val red = Integer.valueOf(if (hex.length >= 2) hex.substring(0, 2) else "ff", 16)
        val green = Integer.valueOf(if (hex.length >= 4) hex.substring(2, 4) else "ff", 16)
        val blue = Integer.valueOf(if (hex.length >= 6) hex.substring(4, 6) else "ff", 16)
        hex = String.format("#%02x%02x%02x", red, green, blue)
        Color.RGBToHSV(red, green, blue, color)
        editColorPickerHue(color[0])

        //float hueFloat = 360.f / hueWidth * color[0];
        //if (hueFloat == 360.f) hueFloat = 0.f;
        //editColorPickerHue(hueFloat);
        hueSelect.x = (hueWidth / 360.0 * color[0]).toFloat() // + space
        val x = colorPickerWidth * (color[1] * 1f)
        val y = colorPickerHeight * (color[2] * 1f) + 1f
        var setX = colorPickerX + x - cursorSize
        var setY = colorPickerY + colorPickerHeight - y - cursorSize
        if (setX < colorPickerX) setX = colorPickerX
        if (setY < colorPickerY) setY = colorPickerY
        if (setX > colorPickerWidth + colorPickerX - cursorSize) setX = colorPickerWidth + colorPickerX - 0.001f - cursorSize
        if (setY > colorPickerHeight + colorPickerY - cursorSize) setY = colorPickerHeight + colorPickerY - 0.001f - cursorSize
        colorSelect.x = setX
        colorSelect.y = setY
        return hex
    }

    fun build() {
        show()
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private fun editColorPickerHue(f: Float) {
        val color = floatArrayOf(f, 1f, 1f)
        this.color[0] = f
        val bitmap = Bitmap.createBitmap(colorPickerWidth, colorPickerHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        strokePaint = Paint()
        strokePaint!!.style = Paint.Style.FILL
        strokePaint!!.color = Color.parseColor(colors.getColor(1))
        if (paint == null) {
            paint = Paint()
            shader = LinearGradient(5f, 5f, 5f, (colorPickerHeight - 5).toFloat(), -0x1, -0x1000000, Shader.TileMode.CLAMP)
        }
        val rgb = Color.HSVToColor(color)
        val shader2: Shader = LinearGradient(5f, 5f, (colorPickerWidth - 5).toFloat(), 5f, -0x1, rgb, Shader.TileMode.CLAMP)
        val composeShader = ComposeShader(shader!!, shader2, PorterDuff.Mode.MULTIPLY)
        paint!!.shader = composeShader
        canvas.drawRoundRect(0f, 0f, colorPickerWidth.toFloat(), colorPickerHeight.toFloat(), 10f, 10f, strokePaint!!)
        canvas.drawRoundRect(5f, 5f, (colorPickerWidth - 5).toFloat(), (colorPickerHeight - 5).toFloat(), 10f, 10f, paint!!)
        val drawable = BitmapDrawable(activity.resources, bitmap)
        colorPicker.background = drawable
    }

    private fun getColor(): Int {
        val argb = Color.HSVToColor(color)
        return argb and 0x00ffffff
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("ClickableViewAccessibility")
    fun update(activity: NoteActivity, type: String?) {
        hue.setOnTouchListener { _: View?, event: MotionEvent ->
            if (event.action == MotionEvent.ACTION_MOVE || event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_UP) {
                var x = event.x
                if (x < 0f) x = 0f
                if (x > hueWidth) x = hueWidth - 0.001f
                var hueFloat = 360f / hueWidth * x
                if (hueFloat == 360f) hueFloat = 0f
                editColorPickerHue(hueFloat)
                hueSelect.x = (hueWidth / 360.0 * hueFloat).toFloat() + space
                editColorPicker()
                activity.editSpan2(type!!, hexColor.text.toString())
                return@setOnTouchListener true
            }
            false
        }
        colorPicker.setOnTouchListener { _: View?, event: MotionEvent ->
            if (event.action == MotionEvent.ACTION_MOVE || event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_UP) {
                var x = event.x
                var y = event.y
                if (x < colorPicker.x) x = colorPicker.x
                if (y < colorPicker.y) y = colorPicker.y
                if (x > colorPickerWidth + colorPicker.x - cursorSize) x = colorPickerWidth + colorPicker.x - 0.001f - cursorSize
                if (y > colorPickerHeight + colorPicker.y - cursorSize) y = colorPickerHeight + colorPicker.y - 0.001f - cursorSize
                colorSelect.x = x
                colorSelect.y = y
                x = event.x
                y = event.y
                if (x < 0f) x = 0f
                if (x > colorPickerWidth) x = colorPickerWidth.toFloat()
                if (y < 0f) y = 0f
                if (y > colorPickerHeight) y = colorPickerHeight.toFloat()
                color[1] = 1f / colorPickerWidth * x
                color[2] = 1f - 1f / colorPickerHeight * y
                editColorPicker()
                activity.editSpan2(type!!, hexColor.text.toString())
                return@setOnTouchListener true
            }
            false
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("ClickableViewAccessibility")
    fun update2(activity: MainActivity, position: Int) {
        hue.setOnTouchListener { _: View?, event: MotionEvent ->
            if (event.action == MotionEvent.ACTION_MOVE || event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_UP) {
                var x = event.x
                if (x < 0f) x = 0f
                if (x > hueWidth) x = hueWidth - 0.001f
                var hueFloat = 360f / hueWidth * x
                if (hueFloat == 360f) hueFloat = 0f
                editColorPickerHue(hueFloat)
                hueSelect.x = (hueWidth / 360.0 * hueFloat).toFloat() + space
                editColorPicker()
                activity.editColor(hexColor.text.toString(), position)
                if (position == 0) {
                    colors.editView(hexColor.text.toString(), position)
                    val draw2 = activity.getDrawable(R.drawable.bg_notes)
                    colors.editColor(imageView, 0, draw2)
                } else if (position == 1) {
                    colors.editView(hexColor.text.toString(), position)
                    strokePaint!!.color = Color.parseColor(colors.getColor(1))
                    val draw = activity.getDrawable(R.drawable.bg_notes)
                    colors.editColor(hexColor, 1, draw)
                    colors.editColor(valid, 1, draw)
                }
                return@setOnTouchListener true
            }
            false
        }
        colorPicker.setOnTouchListener { _: View?, event: MotionEvent ->
            if (event.action == MotionEvent.ACTION_MOVE || event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_UP) {
                var x = event.x
                var y = event.y
                if (x < colorPicker.x) x = colorPicker.x
                if (y < colorPicker.y) y = colorPicker.y
                if (x > colorPickerWidth + colorPicker.x - cursorSize) x = colorPickerWidth + colorPicker.x - 0.001f - cursorSize
                if (y > colorPickerHeight + colorPicker.y - cursorSize) y = colorPickerHeight + colorPicker.y - 0.001f - cursorSize
                colorSelect.x = x
                colorSelect.y = y
                x = event.x
                y = event.y
                if (x < 0f) x = 0f
                if (x > colorPickerWidth) x = colorPickerWidth.toFloat()
                if (y < 0f) y = 0f
                if (y > colorPickerHeight) y = colorPickerHeight.toFloat()
                color[1] = 1f / colorPickerWidth * x
                color[2] = 1f - 1f / colorPickerHeight * y
                editColorPicker()
                activity.editColor(hexColor.text.toString(), position)
                if (position == 0) {
                    colors.editView(hexColor.text.toString(), position)
                    val draw2 = activity.getDrawable(R.drawable.bg_notes)
                    colors.editColor(imageView, 0, draw2)
                } else if (position == 1) {
                    colors.editView(hexColor.text.toString(), position)
                    strokePaint!!.color = Color.parseColor(colors.getColor(1))
                    val draw = activity.getDrawable(R.drawable.bg_notes)
                    colors.editColor(hexColor, 1, draw)
                    colors.editColor(valid, 1, draw)
                }
                return@setOnTouchListener true
            }
            false
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private fun reSize() {
        val size = ReSize()
        val metrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(metrics)
        val phoneWidth = metrics.widthPixels
        val phoneHeight = metrics.heightPixels
        colorPicker.scaleType = ImageView.ScaleType.FIT_XY
        colorSelect.scaleType = ImageView.ScaleType.FIT_XY
        cursorSize = 18f
        space = ((phoneWidth / 10.0 * 7.0 - (phoneWidth / 5.0 * 3.0).toInt()) / 2.0 - phoneWidth / 25.0 / 2).toFloat()
        size.reSizing(imageView, (phoneWidth / 10.0 * 7.0).toInt(), (phoneHeight / 19.0 * 7.0).toInt())
        size.reSizing(colorPicker, (phoneWidth / 5.0 * 3.0).toInt(), (phoneHeight / 16.0 * 3.0).toInt())
        size.reSizing(valid, (phoneWidth / 5.0).toInt(), (phoneHeight / 20.0).toInt(), true, true)
        size.reSizing(hexColor, (phoneWidth / 5.0).toInt(), (phoneHeight / 20.0).toInt(), true, true)
        size.reSizing(arrayOf(space8, space9, space10), 0, (phoneHeight / 40.0).toInt())
        size.reSizing(hue, (phoneWidth / 5.0 * 3.0).toInt(), (phoneHeight / 30.0).toInt())
        size.reSizing(hueSelect, (phoneWidth / 25.0).toInt(), (phoneHeight / 30.0).toInt())
        size.reSizing(colorSelect, cursorSize.toInt(), cursorSize.toInt())
        colorPickerWidth = (phoneWidth / 5.0 * 3.0).toInt()
        colorPickerHeight = (phoneHeight / 16.0 * 3.0).toInt()
        hueWidth = (phoneWidth / 5.0 * 3.0).toInt()
        colorPickerWidth = (phoneWidth / 5.0 * 3.0).toInt()
        colorPickerHeight = (phoneHeight / 16.0 * 3.0).toInt()
        colorPicker.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                colorPicker.viewTreeObserver.removeOnGlobalLayoutListener(this)
                colorPickerX = colorPicker.x
                colorPickerY = colorPicker.y
            }
        })
    }

    init {
        setContentView(R.layout.color_picker)
        if (mainActivity.size == 1) this.mainActivity = true
        this.activity = activity
        colorPicker = findViewById(R.id.colorPicker)
        hexColor = findViewById(R.id.hexColor)
        valid = findViewById(R.id.valid)
        imageView = findViewById(R.id.imageView)
        space8 = findViewById(R.id.space8)
        space9 = findViewById(R.id.space9)
        space10 = findViewById(R.id.space10)
        hue = findViewById(R.id.hue)
        hueSelect = findViewById(R.id.hue_select)
        colorSelect = findViewById(R.id.color_select)
        reSize()
        colors = Colors(activity)
        editColorPickerHue(1f)
        hueSelect.x = (hueWidth / 360.0 * 0.001f).toFloat()
        val draw = activity.getDrawable(R.drawable.bg_notes)
        val draw2 = activity.getDrawable(R.drawable.bg_notes)
        colors.editColor(imageView, 0, draw2)
        colors.editColor(hexColor, 1, draw)
        colors.editColor(valid, 1, draw)
    }
}