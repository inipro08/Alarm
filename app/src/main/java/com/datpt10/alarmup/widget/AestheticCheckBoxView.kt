package com.datpt10.alarmup.widget

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.widget.CompoundButtonCompat
import com.afollestad.aesthetic.Aesthetic
import com.datpt10.alarmup.view.event.Subscribblable
import io.reactivex.disposables.Disposable

/**
 * An AppCompatCheckBox extension class that
 * implements Aesthetic theming.
 */
class AestheticCheckBoxView : AppCompatCheckBox, Subscribblable {

    private var colorAccentSubscription: Disposable? = null
    private var textColorPrimarySubscription: Disposable? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun subscribe() {
        colorAccentSubscription = Aesthetic.get().colorAccent()
                .subscribe { integer ->
                    ColorStateList(
                            arrayOf(intArrayOf(-android.R.attr.state_checked), intArrayOf(android.R.attr.state_checked)),
                            intArrayOf(Color.parseColor("#009FDA"), integer)
                    )
                    CompoundButtonCompat.setButtonTintList(this, ColorStateList.valueOf(Color.parseColor("#009FDA")))
                }

        textColorPrimarySubscription = Aesthetic.get().textColorPrimary()
                .subscribe { setTextColor(ColorStateList.valueOf(Color.parseColor("#ffffff"))) }
    }

    override fun unsubscribe() {
        colorAccentSubscription?.dispose()
        textColorPrimarySubscription?.dispose()
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
