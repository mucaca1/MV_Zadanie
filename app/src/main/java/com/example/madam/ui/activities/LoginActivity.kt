package com.example.madam.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.example.madam.R
import com.example.madam.ui.adapters.PagerAdapter
import com.example.madam.ui.fragments.LoginFragment
import com.example.madam.ui.fragments.RegistrationFragment
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    var pagerAdapter: PagerAdapter = PagerAdapter(this)
    var isLogged: MutableLiveData<Boolean> = MutableLiveData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (view_login_pager != null) {
            pagerAdapter.addFragment(LoginFragment())
            pagerAdapter.addFragment(RegistrationFragment())
            view_login_pager.adapter = pagerAdapter
        }

        isLogged.observe(this, {
            if (it) {
                val myIntent = Intent(this, MainActivity::class.java)
                myIntent.putExtra("login", it)
                this.startActivity(myIntent)
            }
        })
    }

    override fun onBackPressed() {
        finish()
    }

}