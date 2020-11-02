package com.dac.gapp.andac.util.chatUtils


import android.annotation.TargetApi
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Build
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import com.dac.gapp.andac.R

class ChatMessageView : RelativeLayout {
    private var arrowImage: ImageView? = null
    private var containerLayout: RelativeLayout? = null

    private var normalDrawable: TintedBitmapDrawable? = null
    private var pressedDrawable: TintedBitmapDrawable? = null

    private var cornerRadius: Float = 0.toFloat()
    private var contentPadding: Float = 0.toFloat()
    private var arrowMargin: Float = 0.toFloat()
    private var showArrow: Boolean = false
    private var arrowPosition: ArrowPosition? = null
    private var arrowGravity: ArrowGravity? = null
    internal var backgroundColor: Int = 0
    private var backgroundColorPressed: Int = 0

    constructor(context: Context) : super(context) {
        initialize(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initialize(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initialize(attrs, defStyleAttr)
    }

    private fun initialize(attrs: AttributeSet?, defStyleAttr: Int) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ChatMessageView, defStyleAttr, 0)

        showArrow = a.getBoolean(R.styleable.ChatMessageView_cmv_showArrow, true)
        arrowMargin = a.getDimension(R.styleable.ChatMessageView_cmv_arrowMargin, dip2px(0f).toFloat())
        cornerRadius = a.getDimension(R.styleable.ChatMessageView_cmv_cornerRadius, 0f)
        contentPadding = a.getDimension(R.styleable.ChatMessageView_cmv_contentPadding, dip2px(10f).toFloat())
        backgroundColor = a.getColor(R.styleable.ChatMessageView_cmv_backgroundColor, 0)
        backgroundColorPressed = a.getColor(R.styleable.ChatMessageView_cmv_backgroundColorPressed, 0)

        val intPosition = a.getInt(R.styleable.ChatMessageView_cmv_arrowPosition, ArrowPosition.LEFT.value)
        arrowPosition = ArrowPosition.getEnum(intPosition)

        val intGravity = a.getInt(R.styleable.ChatMessageView_cmv_arrowGravity, ArrowGravity.START.value)
        arrowGravity = ArrowGravity.getEnum(intGravity)

        a.recycle()
        initContent()
    }


    fun setArrowPosition(position: ArrowPosition) {
        arrowPosition = position
    }

    fun setArrowGravity(gravity: ArrowGravity) {
        arrowGravity = gravity
    }

    fun setBackgroundColorRes(@ColorRes bgColorRes: Int, @ColorRes bgPressedColorRes: Int) {
        backgroundColorPressed = ContextCompat.getColor(context, bgPressedColorRes)
        backgroundColor = ContextCompat.getColor(context, bgColorRes)
        updateColors()
    }

    fun setBackgroundColor(@ColorInt bgColorRes: Int, @ColorInt bgPressedColorRes: Int) {
        backgroundColorPressed = bgPressedColorRes
        backgroundColor = bgColorRes
        updateColors()
    }

    override fun onViewAdded(child: View) {
        super.onViewAdded(child)
        if (child !== arrowImage && child !== containerLayout) {
            removeView(child)
            containerLayout!!.addView(child)
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun initContent() {

        arrowImage = ImageView(context)
        arrowImage!!.id = ViewUtil.generateViewId()

        if (!showArrow) {
            arrowImage!!.visibility = View.INVISIBLE
        }

        containerLayout = RelativeLayout(context)
        containerLayout!!.id = ViewUtil.generateViewId()
        containerLayout!!.setPadding(contentPadding.toInt(), contentPadding.toInt(), contentPadding.toInt(), contentPadding.toInt())
        val conRlParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)


        val arrowParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)

        val arrowRotation: Int
        when (arrowPosition) {
            ChatMessageView.ArrowPosition.LEFT -> {
                arrowRotation = 180
                arrowParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                arrowParams.setMargins(0, arrowMargin.toInt(), 0, arrowMargin.toInt())
                conRlParams.addRule(RelativeLayout.RIGHT_OF, arrowImage!!.id)
            }
            ChatMessageView.ArrowPosition.TOP -> {
                arrowRotation = 270
                arrowParams.setMargins(arrowMargin.toInt(), 0, arrowMargin.toInt(), 0)
                conRlParams.addRule(RelativeLayout.BELOW, arrowImage!!.id)
            }
            ChatMessageView.ArrowPosition.BOTTOM -> {
                arrowRotation = 90
                arrowParams.setMargins(arrowMargin.toInt(), 0, arrowMargin.toInt(), 0)
                arrowParams.addRule(RelativeLayout.BELOW, containerLayout!!.id)
            }
            else -> {
                arrowRotation = 0
                arrowParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                arrowParams.setMargins(0, arrowMargin.toInt(), 0, arrowMargin.toInt())
                conRlParams.addRule(RelativeLayout.LEFT_OF, arrowImage!!.id)
            }
        }

        when (arrowGravity) {
            ChatMessageView.ArrowGravity.START -> if (arrowPosition == ArrowPosition.TOP || arrowPosition == ArrowPosition.BOTTOM) {
                arrowParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
            } else {
                arrowParams.addRule(RelativeLayout.ALIGN_PARENT_TOP)
            }
            ChatMessageView.ArrowGravity.CENTER -> if (arrowPosition == ArrowPosition.TOP || arrowPosition == ArrowPosition.BOTTOM) {
                arrowParams.addRule(RelativeLayout.CENTER_HORIZONTAL)
            } else {
                arrowParams.addRule(RelativeLayout.CENTER_VERTICAL)
            }
            ChatMessageView.ArrowGravity.END -> if (arrowPosition == ArrowPosition.TOP || arrowPosition == ArrowPosition.BOTTOM) {
                arrowParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
            } else {
                arrowParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            }
            else -> {
            }
        }

        val arrowRes = R.drawable.cmv_arrow
        val source = BitmapFactory.decodeResource(this.resources, arrowRes)
        val rotateBitmap = rotateBitmap(source, arrowRotation.toFloat())

        normalDrawable = TintedBitmapDrawable(resources, rotateBitmap, backgroundColor)
        pressedDrawable = TintedBitmapDrawable(resources, rotateBitmap, backgroundColorPressed)

        arrowImage!!.setImageDrawable(normalDrawable)
        super.addView(arrowImage, arrowParams)
        super.addView(containerLayout, conRlParams)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            arrowImage!!.imageTintList = ColorStateList.valueOf(backgroundColor)
        }
        updateColors()
        this.isClickable = true
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun updateColors() {
        val roundRectDrawable = ChatMessageDrawable(backgroundColor, cornerRadius)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            containerLayout!!.setBackground(roundRectDrawable)
        } else {
            containerLayout!!.setBackgroundDrawable(roundRectDrawable)
        }

        normalDrawable!!.setTint(backgroundColor)
        pressedDrawable!!.setTint(backgroundColorPressed)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            arrowImage!!.imageTintList = ColorStateList.valueOf(backgroundColor)
        }


        /*
        Drawable stateDrawable = new ChatMessageStateDrawable(Color.TRANSPARENT) {
            @Override
            protected void onIsPressed(boolean isPressed) {
                Toast.makeText(getContext(),"dsad",Toast.LENGTH_LONG).show();
                ChatMessageDrawable conRlBackground = (ChatMessageDrawable) containerLayout.getBackground();
                if (isPressed) {
                    conRlBackground.setColor(backgroundColorPressed);
                    arrowImage.setImageDrawable(pressedDrawable);
                } else {
                    conRlBackground.setColor(backgroundColor);
                    arrowImage.setImageDrawable(normalDrawable);
                }
                containerLayout.invalidate();
                arrowImage.invalidate();
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(stateDrawable);
        } else {
            setBackgroundDrawable(stateDrawable);
        }*/

    }

    fun rotateBitmap(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    private fun dip2px(dpValue: Float): Int {
        val scale = resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    enum class ArrowPosition private constructor(value: Int) {

        LEFT(0), RIGHT(1), TOP(2), BOTTOM(3);

        var value: Int = 0
            internal set

        init {
            this.value = value
        }

        companion object {

            fun getEnum(value: Int): ArrowPosition {
                when (value) {
                    0 -> return LEFT
                    1 -> return RIGHT
                    2 -> return TOP
                    3 -> return BOTTOM
                    else -> return LEFT
                }
            }
        }
    }

    enum class ArrowGravity private constructor(value: Int) {
        START(0), CENTER(1), END(2);

        var value: Int = 0
            internal set

        init {
            this.value = value
        }

        companion object {

            fun getEnum(value: Int): ArrowGravity {
                when (value) {
                    0 -> return START
                    1 -> return CENTER
                    2 -> return END
                    else -> return START
                }
            }
        }

    }
}
