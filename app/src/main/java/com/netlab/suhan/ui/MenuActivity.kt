package com.netlab.suhan.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.netlab.suhan.R
import com.netlab.suhan.data.UserType
import com.netlab.suhan.databinding.ActivityMenuBinding
import com.netlab.suhan.ui.fragment.CashierFragment
import com.netlab.suhan.ui.fragment.TransactionFragment
import com.netlab.suhan.ui.fragment.stock.StockFragment
import com.netlab.suhan.utils.DialogCollection
import java.lang.IllegalArgumentException

class MenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMenuBinding

    companion object {
        const val EXTRA_TYPE = "extra_type"
        const val EXTRA_USERNAME = "extra_UserName"

        var USERID = 0
        var USERNAME = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = intent.getStringExtra(EXTRA_USERNAME)
        val intentType = intent.getStringExtra(EXTRA_TYPE)
        val type: UserType = try {
                if (intentType != null) {
                    UserType.valueOf(intentType)
                } else throw IllegalAccessException()
            } catch (e: IllegalArgumentException) {
                DialogCollection.displayErrorDialog(this, "ENUM Type Error! Received: $intentType")
                UserType.CASHIER
            }

        if (username != null) {
            USERNAME = username

            supportActionBar?.title = resources.getString(R.string.list_stock)
            supportFragmentManager.beginTransaction()
                .replace(binding.containerFragment.id, CashierFragment::class.java.newInstance())
                .commit()
            binding.bottomNavigation.selectedItemId = R.id.menu_kasir

            if (type != UserType.ADMIN) {
                binding.bottomNavigation.visibility = View.GONE
            } else {
                binding.bottomNavigation.visibility = View.VISIBLE
                binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
                    val fragmentClass = when (item.itemId) {
                        R.id.menu_list_stock -> StockFragment::class.java
                        R.id.menu_kasir -> CashierFragment::class.java
                        R.id.menu_transaksi -> TransactionFragment::class.java
                        else -> StockFragment::class.java
                    }

                    supportActionBar?.title = when (item.itemId) {
                        R.id.menu_list_stock -> resources.getString(R.string.list_stock)
                        R.id.menu_kasir -> resources.getString(R.string.kasir)
                        R.id.menu_transaksi -> resources.getString(R.string.riwayat_transaksi)
                        else -> resources.getString(R.string.list_stock)
                    }

                    try {
                        supportFragmentManager.beginTransaction()
                            .replace(binding.containerFragment.id, fragmentClass.newInstance())
                            .commit()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(
                            this,
                            "An error has unexpectedly occurred",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                    return@setOnNavigationItemSelectedListener true
                }
            }
        } else {
            DialogCollection.displayErrorDialog(this, "Internal App Transfer Data Error!")
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.tool_main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_log_out -> AlertDialog.Builder(this)
                .setTitle("Alert")
                .setMessage("Are you sure to logout from this account?")
                .setPositiveButton("Yes") { _, _ ->
                    startActivity(Intent(this, LoginActivity::class.java).also {
                        it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    })
                    finish()
                }
                .setNegativeButton("No") { _, _ -> }
                .show()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle("Alert")
            .setMessage("Are you sure to exit this application?")
            .setPositiveButton("Yes") { _, _ -> finish() }
            .setNegativeButton("No") { _, _ -> }
            .show()
    }
}