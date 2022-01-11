package com.netlab.suhan.utils

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

object PriceFormatter {
    fun format(value: Int) : String {
        val formatter: DecimalFormat = NumberFormat.getInstance(Locale.US) as DecimalFormat
        formatter.applyPattern("#,###,###,###")
        var formattedString: String = formatter.format(value)

        //Setting text after format to EditText
        //Replace from 8,000,000.80 to 8,000,000%80
        formattedString = formattedString.replace(".", "%")
        //Replace from 8,000,000%80 to 8.000.000%80
        formattedString = formattedString.replace(",", ".")
        //Replace from 8.000.000%80 to 8.000.000,80
        formattedString = formattedString.replace("%", ",")

        return formattedString
    }

    fun format(value: Double) : String {
        val formatter: DecimalFormat = NumberFormat.getInstance(Locale.US) as DecimalFormat
        formatter.applyPattern("#,###,###,###")
        var formattedString: String = formatter.format(value)

        //Setting text after format to EditText
        //Replace from 8,000,000.80 to 8,000,000%80
        formattedString = formattedString.replace(".", "%")
        //Replace from 8,000,000%80 to 8.000.000%80
        formattedString = formattedString.replace(",", ".")
        //Replace from 8.000.000%80 to 8.000.000,80
        formattedString = formattedString.replace("%", ",")

        return formattedString
    }
}