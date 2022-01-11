package com.netlab.suhan.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Menu (
    var id   : Int,
    var type : MenuType,
    var name : String,
    var stock: Int,
    var price: Double
) : Parcelable