package com.example.madam.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.example.madam.R
import com.example.madam.ui.adapters.PagerAdapter
import com.example.madam.ui.fragments.HomeFragment
import com.example.madam.ui.fragments.ProfileFragment
import com.example.madam.ui.fragments.VideoRecordFragment
import kotlinx.android.synthetic.main.activity_main.*


/**
 * MaDaM - My dáme momentku
 * contributors :
 * - Michal Roháček
 * - Matej Krč
 * - Daniel Vaník
 */
class MainActivity : AppCompatActivity() {

    var pagerAdapter: PagerAdapter = PagerAdapter(this)
    var isLogged: MutableLiveData<Boolean> = MutableLiveData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        isLogged.value = intent.extras?.getBoolean("login")

        if (view_main_pager != null) {
            pagerAdapter.addFragment(ProfileFragment())
            pagerAdapter.addFragment(HomeFragment())
            pagerAdapter.addFragment(VideoRecordFragment())
            view_main_pager.adapter = pagerAdapter
            view_main_pager.currentItem = 1
        }

        isLogged.observe(this, {
            if (!it) {
                goToActivity(LoginActivity::class.java)
            }
        })

        val policy =
            StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun <T> goToActivity(cls: Class<T>) {
        val myIntent = Intent(this, cls)
        this.startActivity(myIntent)
    }
}