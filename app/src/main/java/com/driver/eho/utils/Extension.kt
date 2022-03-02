package com.driver.eho.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.driver.eho.R
import com.google.android.material.textview.MaterialTextView

fun ContentResolver.getFileName(fileUri: Uri?): String {
    var name = ""
    val returnCursor = fileUri?.let { this.query(it, null, null, null, null) }
    if (returnCursor != null) {
        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        name = returnCursor.getString(nameIndex)
        returnCursor.close()
    }
    return name

}

fun LinearLayout.setOnline(
    context: Context,
    viewToChange: MaterialTextView,
    viewtoHide: ImageView,
    viewToShow: ImageView
): Boolean {
    this.background.setTint(this.resources.getColor(R.color.blue_lite))
    viewToChange.text = context.getString(R.string.online)
    this.setOffline(context, viewToChange, viewToShow, viewtoHide)
    return true
}

fun LinearLayout.setOffline(
    context: Context,
    viewToChange: MaterialTextView,
    viewtoHide: ImageView,
    viewToShow: ImageView
): Boolean {
    this.background.setTint(this.resources.getColor(android.R.color.holo_orange_dark))
    viewToChange.text = context.getString(R.string.offline)
    viewtoHide.visibility = View.VISIBLE
    viewToShow.visibility = View.GONE
    return false
}