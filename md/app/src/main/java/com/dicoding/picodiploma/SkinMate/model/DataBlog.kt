package com.dicoding.picodiploma.SkinMate.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DataBlog (
    var title: String,
    var description: String,
    var photo: Int
) : Parcelable