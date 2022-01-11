package com.netlab.suhan.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Customer(
    var id          : Int,
    var balance     : Double,
    var totalExpense: Double
) : Parcelable
