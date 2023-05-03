package com.littlefox.app.foxschool.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.littlefox.app.foxschool.R

class MainTestFragment : Fragment
{
    @BindView(R.id._titleText)
    lateinit var _TitleText : TextView


    private lateinit var mContext : Context
    private lateinit var mUnbinder : Unbinder

    var mTitleText : String? = null;

    constructor(title : String) : super()
    {
        mTitleText = title
    }

    override fun onAttach(context : Context)
    {
        super.onAttach(context)
        mContext = context
    }

    override fun onViewCreated(view : View, savedInstanceState : Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater : LayoutInflater, container : ViewGroup?, savedInstanceState : Bundle?) : View?
    {
        val view = inflater.inflate(R.layout.fragment_test, container, false)
        mUnbinder = ButterKnife.bind(this, view)
        return view
    }

    override fun onStart()
    {
        super.onStart()
    }

    override fun onResume()
    {
        super.onResume()
    }

    override fun onPause()
    {
        super.onPause()
    }

    override fun onStop()
    {
        super.onStop()
    }

    override fun onDestroyView()
    {
        super.onDestroyView()
        mUnbinder.unbind()
    }

    private fun initView()
    {
        _TitleText.setText(mTitleText)
    }

}