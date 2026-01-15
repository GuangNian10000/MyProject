package com.guangnian.demo.widget.base

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.Observer
import com.guangnian.demo.R
import com.guangnian.demo.app.ext.extractColorFromBackgroundDrawable
import com.guangnian.demo.app.theme.AppTheme.appTheme
import com.guangnian.demo.data.model.bean.BgData
import com.guangnian.demo.util.AppMK

/**
 * @author GuangNian
 * @description:
 * @date : 2023/11/16 15:50
 */
class AppTextView @JvmOverloads constructor(context: Context, var attrs: AttributeSet? = null,
                                            defStyleAttr: Int =  android.R.attr.textViewStyle) : AppCompatTextView(context, attrs, defStyleAttr){
    private var strokeWidth: Float = 1F
    private var isTheme :String = ""
    private var mActive :Boolean = false
    private var extraActive :Boolean = false
    private var sizeWeight :Int = 0
    var extraColor: ColorStateList ? = null
    var extraColorNight: ColorStateList ? = null

    private var activeSizeColor : ColorStateList ? = null
    private var activeBgColor : Int = 0
    private var activeDrawable : Drawable? = null
    private var activeImg : Drawable? = null

    private var activeSizeColorNight : ColorStateList ? = null
    private var activeBgColorNight : Int = 0
    private var activeDrawableNight : Drawable? = null
    private var activeImgNight : Int = 0

    private var textColorNight : ColorStateList ? = null
    private var bgColorNight : Int = 0
    private var drawableNight : Drawable? = null
    private var imgNight : Drawable? = null

    private var extraTextColor: ColorStateList ? = null
    private var extraTextColorNight: ColorStateList ? = null

    private var mInitialBackgroundColor : Int = 0 //初始化的颜色
    private var mInitialTextColor : ColorStateList ? =null
    private var mInitialBackground : Drawable? = null
    private var mInitialImg : Drawable? = null

    private var typedArray : TypedArray?= null

    private val mStyles = R.styleable.BaseStyles


    private val mThemeObserver = Observer<BgData> { bgData ->   //invalidate()  可能需要
        notifyUI(bgData.ordinal)
    }

    fun setExtraActive(b:Boolean){
        extraActive = b
        val indexTheme = AppMK.getAppTheme()
        notifyUI(indexTheme)
    }

    fun setSwitch(b:Boolean){
        mActive = b
        val indexTheme = AppMK.getAppTheme()
        if(""==isTheme){
            notifyUI(indexTheme)
        }else{
            //更新样式
            notifyUI(indexTheme)
        }
    }

    override fun onAttachedToWindow(){
        super.onAttachedToWindow()
        appTheme.observeForever(mThemeObserver)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        // 在View从窗口中移除时移除观察者
        appTheme.removeObserver(mThemeObserver)
    }

    private fun setCustomDrawable(drawable: Drawable?) {
        // 设置图片资源到 EditText 中
        drawable?.let {  background = it }
    }

    private fun setBgColor(color: Int){
        if(0!=color) setBackgroundColor(color)
    }

//    private fun setTextSizeColor(color: Int){
//        if(0!=color) setTextColor(getColor(com.guangnian.mvvm.R.color.red))
//    }

    private fun setTextSizeColor(color:ColorStateList?){
        color?.let {  setTextColor(it) }
    }

    //显示高亮样式
    private fun setActiveUI(b:Boolean){
        if(b){
            setBgColor(activeBgColor)
            setCustomDrawable(activeDrawable)
            setTextSizeColor(activeSizeColor)
        }else{
            setBgColor(mInitialBackgroundColor)
            setCustomDrawable(mInitialBackground)
            if(extraActive){
                setTextSizeColor(extraTextColor)
            }else{
                setTextSizeColor(mInitialTextColor)
            }
        }

    }

    private fun setActiveNightUI(b:Boolean){
        if(b){
            setBgColor(activeBgColorNight)
            setCustomDrawable(activeDrawableNight)
            setTextSizeColor(activeSizeColorNight)
        }else{
            setBgColor(bgColorNight)
            setCustomDrawable(drawableNight)
            if(extraActive){
                setTextSizeColor(extraTextColorNight)
            }else{
                setTextSizeColor(textColorNight)
            }
        }
    }

    private fun notifyUI(theme: Int){
        if(""==isTheme){
            if(theme==BgData.BG_night.ordinal){
                setActiveNightUI(mActive)
            }else{
                setActiveUI(mActive)
            }
        }else{
            initView(theme)
            setActiveUI(mActive)
        }

    }

    init {
        typedArray = context.obtainStyledAttributes(attrs, mStyles)
        isTheme =  typedArray?.getString(R.styleable.BaseStyles_mTheme)?:""

        //获取当前主题
        val indexTheme = AppMK.getAppTheme()
        if(""==isTheme){
            initView(indexTheme)
            notifyUI(indexTheme)
        }else{
            //更新样式
            notifyUI(indexTheme)
        }
        includeFontPadding = false //去内边距
    }

    private fun initView(theme: Int){
        typedArray = context.obtainStyledAttributes(attrs, mStyles)
        typedArray?.apply {
            if(""!=isTheme){//支持主题
                val themeId = resources.getIdentifier(isTheme + theme, "style", context.packageName)
                if (themeId != 0) {//获取样式值
                    typedArray = context.obtainStyledAttributes(themeId, mStyles)
                }

                val themeTextColor = context.obtainStyledAttributes(themeId, intArrayOf(R.attr.textColor)).getColor(0, 0)
                if (themeTextColor != 0) {
                    // 设置主题样式中的文本颜色
                    setTextColor(themeTextColor)
                }

                val themeBackground = context.obtainStyledAttributes(themeId, intArrayOf(android.R.attr.background)).getDrawable(0)
                if (themeBackground != null) {
                    // 设置主题样式中的背景
                    background = themeBackground
                }
            }

            getString(R.styleable.BaseStyles_mTheme)?.let { isTheme = it }
            mActive = getBoolean(R.styleable.BaseStyles_mActive, false)
            sizeWeight = getInt(R.styleable.BaseStyles_sizeWeight, 0)
            extraColor = getColorStateList(R.styleable.BaseStyles_extraColor)
            extraColorNight = getColorStateList(R.styleable.BaseStyles_extraColorNight)

            activeSizeColor = getColorStateList(R.styleable.BaseStyles_activeSizeColor)
            activeBgColor = getColor(R.styleable.BaseStyles_activeBgColor,0)
            activeDrawable = getDrawable(R.styleable.BaseStyles_activeDrawable)
            activeImg = getDrawable(R.styleable.BaseStyles_activeImg)

            activeSizeColorNight = getColorStateList(R.styleable.BaseStyles_activeSizeColorNight)
            activeBgColorNight = getColor(R.styleable.BaseStyles_activeBgColorNight,0)
            activeDrawableNight = getDrawable(R.styleable.BaseStyles_activeDrawableNight)
            activeImgNight = getColor(R.styleable.BaseStyles_activeImgNight,0)

            textColorNight = getColorStateList(R.styleable.BaseStyles_textColorNight)
            bgColorNight = getColor(R.styleable.BaseStyles_bgColorNight,0)
            drawableNight = getDrawable(R.styleable.BaseStyles_drawableNight)
            imgNight = getDrawable(R.styleable.BaseStyles_activeImg)

            extraTextColor = getColorStateList(R.styleable.BaseStyles_extraTextColor)
            extraTextColorNight = getColorStateList(R.styleable.BaseStyles_extraTextColorNight)
            extraActive = getBoolean(R.styleable.BaseStyles_extraActive, false)

            if (background != null&&""==isTheme) {
                mInitialBackgroundColor = attrs?.getAttributeResourceValue(android.R.attr.background, 0) ?: 0
                if (0 == mInitialBackgroundColor) {
                    mInitialBackgroundColor = extractColorFromBackgroundDrawable(background)
                }
            }
            mInitialTextColor = textColors
            mInitialBackground = background


            recycle()
        }
    }

    fun setSizeWeight(b:Boolean){
        sizeWeight = 1
        if(b){
            strokeWidth = 1f
        }else{
            strokeWidth = 0f
        }
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        if(sizeWeight==1){
            val paint = paint
            //设置画笔的描边宽度值
            paint.strokeWidth = strokeWidth
            paint.style = Paint.Style.FILL_AND_STROKE
        }
        super.onDraw(canvas)
    }
}

