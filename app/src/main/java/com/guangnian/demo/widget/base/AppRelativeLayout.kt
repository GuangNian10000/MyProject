package com.guangnian.demo.widget.base

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.RelativeLayout
import androidx.lifecycle.Observer
import com.guangnian.demo.R
import com.guangnian.demo.app.ext.extractColorFromBackgroundDrawable
import com.guangnian.demo.app.theme.AppTheme
import com.guangnian.demo.data.model.bean.BgData
import com.guangnian.demo.util.AppMK

/**
 * @author GuangNian
 * @description:
 * @date : 2023/11/16 15:50
 */
class AppRelativeLayout @JvmOverloads constructor(
    context: Context,var attrs: AttributeSet? = null) : RelativeLayout(context, attrs){
    private var isTheme :String = ""
    private var mActive :Boolean = false

    private var activeSizeColor : Int = 0
    private var activeBgColor : Int = 0
    private var activeDrawable : Drawable? = null
    private var activeImg : Drawable? = null

    private var activeSizeColorNight : Int = 0
    private var activeBgColorNight : Int = 0
    private var activeDrawableNight : Drawable? = null
    private var activeImgNight : Int = 0

    private var textColorNight : Int = 0
    private var bgColorNight : Int = 0
    private var drawableNight : Drawable? = null
    private var imgNight : Drawable? = null

    private var mInitialBackgroundColor : Int = 0 //初始化的颜色
    private var mInitialTextColor : ColorStateList? =null
    private var mInitialBackground : Drawable? = null
    private var mInitialImg : Drawable? = null

    private var typedArray : TypedArray?= null

    private val mStyles = R.styleable.BaseStyles


    private val mThemeObserver = Observer<BgData> { bgData ->   //invalidate()  可能需要
        notifyUI(bgData.ordinal)
    }
    override fun onAttachedToWindow(){
        super.onAttachedToWindow()
        AppTheme.appTheme.observeForever(mThemeObserver)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        // 在View从窗口中移除时移除观察者
        AppTheme.appTheme.removeObserver(mThemeObserver)
    }


    // 设置图片资源
    private fun setCustomDrawable(drawable: Drawable?) {
        // 设置图片资源到 EditText 中
        drawable?.let {  background = it }
    }

    private fun setBgColor(color: Int){
        if(0!=color) setBackgroundColor(color)
    }

    //显示高亮样式
    private fun setActiveUI(b:Boolean){
        if(b){
            setBgColor(activeBgColor)
            setCustomDrawable(activeDrawable)
        }else{
            setBgColor(mInitialBackgroundColor)
            setCustomDrawable(mInitialBackground)
        }

    }

    private fun setActiveNightUI(b:Boolean){
        if(b){
            setBgColor(activeBgColorNight)
            setCustomDrawable(activeDrawableNight)
        }else{
            setBgColor(bgColorNight)
            setCustomDrawable(drawableNight)
        }
    }


    private fun notifyUI(theme: Int){
        if(""==isTheme){
            if(theme== BgData.BG_night.ordinal){
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
    }

    private fun initView(theme: Int){
        typedArray = context.obtainStyledAttributes(attrs, mStyles)
        typedArray?.apply {
            if(""!=isTheme){//支持主题
                val themeId = resources.getIdentifier(isTheme + theme, "style", context.packageName)
                if (themeId != 0) {//获取样式值
                    typedArray = context.obtainStyledAttributes(themeId, mStyles)
                }

                val themeBackground = context.obtainStyledAttributes(themeId, intArrayOf(android.R.attr.background)).getDrawable(0)
                if (themeBackground != null) {
                    // 设置主题样式中的背景
                    background = themeBackground
                }
            }

            getString(R.styleable.BaseStyles_mTheme)?.let { isTheme = it }
            mActive = getBoolean(R.styleable.BaseStyles_mActive, false)

            activeSizeColor = getColor(R.styleable.BaseStyles_activeSizeColor,0)
            activeBgColor = getColor(R.styleable.BaseStyles_activeBgColor,0)
            activeDrawable = getDrawable(R.styleable.BaseStyles_activeDrawable)
            activeImg = getDrawable(R.styleable.BaseStyles_activeImg)

            activeSizeColorNight = getColor(R.styleable.BaseStyles_activeSizeColorNight,0)
            activeBgColorNight = getColor(R.styleable.BaseStyles_activeBgColorNight,0)
            activeDrawableNight = getDrawable(R.styleable.BaseStyles_activeDrawableNight)
            activeImgNight = getColor(R.styleable.BaseStyles_activeImgNight,0)

            textColorNight = getColor(R.styleable.BaseStyles_textColorNight,0)
            bgColorNight = getColor(R.styleable.BaseStyles_bgColorNight,0)
            drawableNight = getDrawable(R.styleable.BaseStyles_drawableNight)
            imgNight = getDrawable(R.styleable.BaseStyles_activeImg)

            if (background != null&&""==isTheme) {
                mInitialBackgroundColor = attrs?.getAttributeResourceValue(android.R.attr.background, 0) ?: 0
                if (0 == mInitialBackgroundColor) {
                    mInitialBackgroundColor = extractColorFromBackgroundDrawable(background)
                }
            }
            mInitialBackground = background

            recycle()
        }
    }
}


