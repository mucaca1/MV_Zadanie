package com.example.tiktok.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.tiktok.R
import com.example.tiktok.utils.PasswordUtils
import com.example.tiktok.utils.SessionManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var passwordUtils: PasswordUtils
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sessionManager = SessionManager(getApplicationContext())

        NavigationUI.setupWithNavController(
            nav_view, Navigation.findNavController(
                this,
                R.id.nav_host_fragment
            )
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        return Navigation.findNavController(this, R.id.nav_host_fragment).navigateUp()
    }

    override fun onStop() {
        super.onStop()
        sessionManager.logoutUser()
    }
}