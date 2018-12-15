package com.stepango.okspam

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


const val KEY_SPAM = "KEY_SPAM"

@Parcelize
data class SpamRequest(
    val imageUrl: String,
    val audioUrl: String? = null,
    val message: String? = null
) : Parcelable
