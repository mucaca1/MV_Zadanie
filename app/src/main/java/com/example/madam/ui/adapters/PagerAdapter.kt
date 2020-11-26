package com.example.madam.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.madam.ui.fragments.HomeFragment
import com.example.madam.ui.fragments.ProfileFragment
import com.example.madam.ui.fragments.VideoRecordFragment

class PagerAdapter(manager: FragmentManager): FragmentPagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val fragmentList: MutableList<Fragment> = ArrayList()

    override fun getCount(): Int {
        return fragmentList.size
    }

    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    fun addFragment(fragment: Fragment) {
        fragmentList.add(fragment)
    }

    fun removeFragment(fragment: Fragment) {
        fragmentList.remove(fragment)
    }

    fun removeAllFragments() {
        fragmentList.removeAll(fragmentList)
    }

    fun addAfterSignFragments() {
        fragmentList.add(ProfileFragment())
        fragmentList.add(HomeFragment())
        fragmentList.add(VideoRecordFragment())
    }

}