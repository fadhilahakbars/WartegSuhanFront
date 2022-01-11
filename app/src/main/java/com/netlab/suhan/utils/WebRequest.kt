package com.netlab.suhan.utils

import com.android.volley.Response
import com.android.volley.toolbox.StringRequest

class WebRequest(
    method: Int,
    url: String,
    listener: Response.Listener<String>,
    errorListener: Response.ErrorListener,
    params: Map<String, String>
) :
    StringRequest(method, url, listener, errorListener) {

    private val getParams: Map<String, String> = params

    override fun getParams(): Map<String, String> {
        return getParams
    }
}