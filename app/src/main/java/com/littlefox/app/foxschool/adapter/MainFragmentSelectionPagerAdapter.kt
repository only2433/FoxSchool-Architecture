package com.littlefox.app.foxschool.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import java.util.ArrayList

class MainFragmentSelectionPagerAdapter : FragmentStatePagerAdapter
{
    private val mFragmentList : MutableList<Fragment>
    constructor(fragmentManager : FragmentManager) : super(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
    {
        mFragmentList = ArrayList()
    }

    fun addFragment(fragment : Fragment)
    {
        mFragmentList.add(fragment)
    }

    fun setFragment(position : Int, fragment : Fragment)
    {
        mFragmentList[position] = fragment
    }

    override fun getItem(position : Int) : Fragment
    {
        return mFragmentList[position]
    }

    override fun getCount() : Int
    {
        return mFragmentList.size
    }

    override fun getPageTitle(position : Int) : CharSequence?
    {
        return null
    }

    override fun getItemPosition(`object` : Any) : Int
    {
        return PagerAdapter.POSITION_NONE
    }

    val pagerFragmentList : List<Fragment>
        get() = mFragmentList


}