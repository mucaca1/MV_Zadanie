package com.example.tiktok.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.example.tiktok.ui.activities.MainActivity
import com.example.tiktok.ui.fragments.LoginFragment


class SessionManager(context: Context?) {
    // Shared pref mode
    var PRIVATE_MODE = 0

    // Sharedpref file name
    private val PREF_NAME = "AndroidHivePref"

    // All Shared Preferences Keys
    private val IS_LOGIN = "IsLoggedIn"

    // User name (make variable public to access from outside)
    val KEY_NAME = "name"

    // Email address (make variable public to access from outside)
    val KEY_EMAIL = "email"

    // Shared Preferences
    var pref: SharedPreferences? = context!!.getSharedPreferences(PREF_NAME, PRIVATE_MODE)

    // Editor for Shared preferences
    var editor: SharedPreferences.Editor? = pref?.edit()

    // Context
    var _context: Context? = context

    /**
     * Create login session
     */
    fun createLoginSession(name: String?, email: String?) {
        // Storing login value as TRUE
        editor?.putBoolean(IS_LOGIN, true)

        // Storing name in pref
        editor?.putString(KEY_NAME, name)

        // Storing email in pref
        editor?.putString(KEY_EMAIL, email)

        // commit changes
        editor?.commit()
    }

    /**
     * Get stored session data
     */
    fun getUserDetails(): HashMap<String, String?>? {
        val user = HashMap<String, String?>()
        // user name
        user[KEY_NAME] = pref!!.getString(KEY_NAME, null)

        // user email id
        user[KEY_EMAIL] = pref!!.getString(KEY_EMAIL, null)

        // return user
        return user
    }

    /**
     * Clear session details
     */
    fun logoutUser() {
        // Clearing all data from Shared Preferences
        editor?.clear()
        editor?.commit()

        // After logout redirect user to Loing Activity
        val i = Intent(_context, MainActivity::class.java)
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        // Add new Flag to start new Activity
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        // Staring Login Activity
        _context!!.startActivity(i)
    }

    /**
     * Quick check for login
     */
    // Get Login State
    fun isLoggedIn(): Boolean {
        return pref!!.getBoolean(IS_LOGIN, false)
    }
}