package com.datpt10.alarmnow.widget

import android.content.Context
import android.util.AttributeSet
import com.afollestad.aesthetic.Aesthetic
import com.datpt10.alarmup.view.event.Subscribblable
import io.reactivex.disposables.Disposable
import me.jfenn.slideactionview.SlideActionView

/**
 * A SlideActionView extension class that implements
 * Aesthetic theming.
 */
class AestheticSlideActionView : SlideActionView, Subscribblable {

    private var textColorPrimarySubscription: Disposable? = null
    private var textColorPrimaryInverseSubscription: Disposable? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun subscribe() {
        textColorPrimarySubscription = Aesthetic.get()
                .textColorPrimary()
                .subscribe { integer ->
                    touchHandleColor = integer
                    outlineColor = integer
                    iconColor = integer
                    postInvalidate()
                }

        textColorPrimaryInverseSubscription = Aesthetic.get()
                .textColorPrimaryInverse()
                .subscribe { integer -> setBackgroundColor((100 shl 24) or (integer and 0x00ffffff)) }
    }

    override fun unsubscribe() {
        textColorPrimarySubscription?.dispose()
        textColorPrimaryInverseSubscription?.dispose()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        subscribe()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        unsubscribe()
    }
}
