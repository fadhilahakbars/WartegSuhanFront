package com.netlab.suhan.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.netlab.suhan.R
import com.netlab.suhan.data.UserType
import com.netlab.suhan.databinding.ActivityLoginBinding
import com.netlab.suhan.utils.DialogCollection
import com.netlab.suhan.utils.WebRequest
import java.lang.IllegalArgumentException

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    companion object {
        private const val SHARED_PREF_URL = "com.netlab.suhan"
        private const val SP_URL_KEY = "url"
        private const val SP_USER_KEY = "username_k"
        var URL = "http://10.0.2.2:3000"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = resources.getString(R.string.login)

        val sharedPreferences = getSharedPreferences(SHARED_PREF_URL, Context.MODE_PRIVATE)
        val url = sharedPreferences.getString(SP_URL_KEY, "10.0.2.2:3000")
        binding.etURL.setText(url)
        binding.etUsername.setText(sharedPreferences.getString(SP_USER_KEY, ""))

        URL = "http://$url"

        binding.btnCheck.setOnClickListener {
            if (binding.etURL.text.toString().isNotBlank()) {
                binding.layoutURL.error = ""
                val newUrl = binding.etURL.text.toString()
                URL = "http://$newUrl"

                with(binding) {
                    etURL.isEnabled = false
                    btnCheck.isEnabled = false
                    progressBarCheck.visibility = View.VISIBLE
                }

                val responseListener = Response.Listener<String> { response ->
                    if (response != null && response.isNotBlank()) {
                        Toast.makeText(this, response, Toast.LENGTH_SHORT)
                            .show()

                        sharedPreferences.edit {
                            putString(SP_URL_KEY, newUrl)
                        }
                    } else DialogCollection.displayErrorDialog(this, "Unable to find the server")

                    with(binding) {
                        etURL.isEnabled = true
                        btnCheck.isEnabled = true
                        progressBarCheck.visibility = View.GONE
                    }
                }

                val errorListener = Response.ErrorListener { error ->
                    DialogCollection.displayErrorDialog(
                        this,
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            when (error.networkResponse.statusCode) {
                                404 -> "Unable to find the server"
                                500 -> "Internal Server Error"
                                else -> "Unknown Error has occurred"
                            }
                        } else "Unable to find the server"
                    )

                    with(binding) {
                        etURL.isEnabled = true
                        btnCheck.isEnabled = true
                        progressBarCheck.visibility = View.GONE
                    }
                }

                val queue = Volley.newRequestQueue(this)
                val request =
                    WebRequest(Request.Method.GET, URL, responseListener, errorListener, HashMap())
                queue.add(request)
            } else {
                binding.layoutURL.error = "Required!"
            }
        }

        binding.btnLogin.setOnClickListener {
            if (checkRequirement()) {
                val username = binding.etUsername.text.toString()
                val password = binding.etPassword.text.toString()

                with(binding) {
                    etURL.isEnabled = false
                    etUsername.isEnabled = false
                    etPassword.isEnabled = false
                    btnCheck.isEnabled = false
                    btnLogin.isEnabled = false
                    progressBar.visibility = View.VISIBLE
                }

                val responseListener = Response.Listener<String> { response ->
                    if (response != null && response.isNotBlank()) {
                        sharedPreferences.edit {
                            putString(SP_USER_KEY, username)
                        }

                        startActivity(Intent(this, MenuActivity::class.java).also {
                            it.putExtra(MenuActivity.EXTRA_USERNAME, username)
                            it.putExtra(MenuActivity.EXTRA_TYPE, response)
                        })
                        finish()
                    } else {
                        DialogCollection.displayErrorDialog(this, "Unknown error has occurred")
                        with(binding) {
                            etURL.isEnabled = true
                            etUsername.isEnabled = true
                            etPassword.isEnabled = true
                            btnCheck.isEnabled = true
                            btnLogin.isEnabled = true
                            progressBar.visibility = View.GONE
                        }
                    }
                }

                val errorListener = Response.ErrorListener { error ->
                    DialogCollection.displayErrorDialog(
                        this,
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            when (error.networkResponse.statusCode) {
                                404 -> error.networkResponse.data.toString(Charsets.UTF_8)
                                500 -> "Internal Server Error"
                                else -> "Unknown Error has occurred"
                            }
                        } else "Unknown Error has occurred. Data has not been received from server"
                    )

                    with(binding) {
                        etURL.isEnabled = true
                        etUsername.isEnabled = true
                        etPassword.isEnabled = true
                        btnCheck.isEnabled = true
                        btnLogin.isEnabled = true
                        progressBar.visibility = View.GONE
                    }
                }

                val queue = Volley.newRequestQueue(this)
                val request = WebRequest(
                    Request.Method.GET,
                    "$URL/user?user=${username.lowercase()}&pass=${password.lowercase()}",
                    responseListener,
                    errorListener,
                    HashMap()
                )
                queue.add(request)
            }
        }
    }

    private fun checkRequirement(): Boolean {
        with(binding) {
            val map = mapOf(
                etUsername to layoutUsername,
                etPassword to layoutPassword
            )

            map.forEach { (_, value) -> value.error = "" }
            val result = map.filter { (key, _) -> key.text.toString().isBlank() }

            return if (result.isNotEmpty()) {
                val error = "Required!"
                result.forEach { (_, value) -> value.error = error }
                false
            } else true
        }
    }
}