package com.yoavst.util

import kotlin.properties.ReadOnlyProperty
import android.view.View
import android.app.Fragment
import android.content.Context
import android.widget.Toast
import android.app.Activity
import android.util.Log
import java.lang.reflect.Type
import com.google.gson.reflect.TypeToken
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.graphics.Typeface
import android.content.Intent
import android.os.Build
import android.content.ComponentName
import com.mobsandgeeks.ake.shortToast
import android.view.Gravity
import android.service.notification.StatusBarNotification


public inline fun <reified T> typeToken(): Type = object : TypeToken<T>() {}.getType()

public inline fun r(inlineOptions(InlineOption.ONLY_LOCAL_RETURN) func: () -> Unit): Runnable = Runnable { func() }

public fun SpannableString.colorize(color: Int, start: Int, end: Int): SpannableString {
    this.setSpan(ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    return this
}

public fun SpannableString.setBigger(size: Float, start: Int, end: Int): SpannableString {
    this.setSpan(RelativeSizeSpan(size), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    return this
}

public fun SpannableString.bold(start: Int, end: Int): SpannableString {
    this.setSpan(StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    return this
}

public fun Activity.qCircleToast(text: Int): Unit = qCircleToast(getString(text))
public fun Fragment.qCircleToast(text: Int): Unit = getActivity().qCircleToast(text)
public fun Fragment.qCircleToast(text: String): Unit = getActivity().qCircleToast(text)

public fun Activity.qCircleToast(text: String) {
    val toast = shortToast(text);
    toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 0)
    toast.show()
}

public fun Intent.createExplicit(context: Context): Intent {
    if (Build.VERSION.SDK_INT >= 21) {
        val pm = context.getPackageManager()
        val resolveInfo = pm.queryIntentServices(this, 0)
        if (resolveInfo == null || resolveInfo.size() != 1) {
            return this
        }
        val serviceInfo = resolveInfo.get(0)
        val packageName = serviceInfo.serviceInfo.packageName
        val className = serviceInfo.serviceInfo.name
        val component = ComponentName(packageName, className)
        val explicitIntent = Intent(this)
        explicitIntent.setComponent(component)
        return explicitIntent
    } else
        return this
}


public fun StatusBarNotification?.equalsContent(any: Any?): Boolean {
    if (this == null)
        return any == null
    else if (any == null || any !is StatusBarNotification) return false
    try {
        return this.getPostTime() == any.getPostTime() && this.getId() == any.getId() && any.getPackageName().orEmpty() == any.getPackageName().orEmpty() &&
                this.getTag().orEmpty() == any.getTag().orEmpty()
    } catch (e: Exception) {
        return false
    }
}