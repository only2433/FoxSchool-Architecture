package com.littlefox.app.foxschool.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.littlefox.app.foxschool.R
import com.littlefox.app.foxschool.`object`.result.main.MainInformationResult
import com.littlefox.app.foxschool.`object`.result.story.SeriesInformationResult
import com.littlefox.app.foxschool.adapter.SeriesCardViewAdapter
import com.littlefox.app.foxschool.adapter.listener.SeriesCardItemListener
import com.littlefox.app.foxschool.common.CommonUtils
import com.littlefox.app.foxschool.enumerate.SeriesType
import com.littlefox.app.foxschool.main.viewmodel.factory.MainFactoryViewModel
import com.littlefox.app.foxschool.view.decoration.GridSpacingItemDecoration
import com.littlefox.logmonitor.Log
import java.util.*

class MainSongFragment : Fragment()
{
    @BindView(R.id._songGridView)
    lateinit var _SongGridView : RecyclerView

    companion object
    {
        private var COLUMN_COUNT = 0
        private var COLUMN_MARGIN = 0
        val instance : MainSongFragment
            get() = MainSongFragment()
    }

    private lateinit var mContext : Context
    private lateinit var mSeriesCardViewAdapter : SeriesCardViewAdapter
    private var mUnbinder : Unbinder? = null
    private var mCurrentSeriesBaseResultList : ArrayList<SeriesInformationResult>? = null

    private val factoryViewModel : MainFactoryViewModel by activityViewModels()

    override fun onAttach(context : Context)
    {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater : LayoutInflater, container : ViewGroup?, savedInstanceState : Bundle?) : View?
    {
        Log.f("")
        val view : View = inflater.inflate(R.layout.fragment_main_song, container, false)
        mUnbinder = ButterKnife.bind(this, view)
        mCurrentSeriesBaseResultList = CommonUtils.getInstance(mContext).loadMainData().getMainSongInformationList()
        return view
    }

    override fun onViewCreated(view : View, savedInstanceState : Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        Log.f("")
        initView()
        initRecyclerView()
    }

    override fun onActivityCreated(savedInstanceState : Bundle?)
    {
        super.onActivityCreated(savedInstanceState)
        setupObserverViewModel()
    }

    override fun onPause()
    {
        super.onPause()
    }

    override fun onResume()
    {
        super.onResume()
        Log.f("")
    }

    override fun onStop()
    {
        super.onStop()
    }

    override fun onDestroy()
    {
        super.onDestroy()
    }

    override fun onDestroyView()
    {
        super.onDestroyView()
        mUnbinder!!.unbind()
        Log.f("")
    }

    private fun setupObserverViewModel()
    {
        factoryViewModel.updateSongData.observe(viewLifecycleOwner, Observer<Any> { mainInformationResult ->
            updateData(mainInformationResult as MainInformationResult)
        })
    }

    private fun updateData(mainInformationResult : MainInformationResult)
    {
        mCurrentSeriesBaseResultList = mainInformationResult.getMainSongInformationList()
        mSeriesCardViewAdapter!!.notifyDataSetChanged()
        _SongGridView.scrollToPosition(0)
    }

    private fun initView()
    {
        if(CommonUtils.getInstance(mContext).checkTablet)
        {
            COLUMN_COUNT = 5
            COLUMN_MARGIN = 20
        } else
        {
            COLUMN_COUNT = 2
            COLUMN_MARGIN = 24
        }
    }

    private fun initRecyclerView()
    {
        mSeriesCardViewAdapter = SeriesCardViewAdapter(mContext!!, mCurrentSeriesBaseResultList!!)
        mSeriesCardViewAdapter.setSeriesType(SeriesType.SONG)
        mSeriesCardViewAdapter.setSeriesCardItemListener(mSeriesCardItemListener)
        val gridLayoutManager = GridLayoutManager(mContext, COLUMN_COUNT)
        gridLayoutManager.setSpanSizeLookup(object : GridLayoutManager.SpanSizeLookup()
        {
            override fun getSpanSize(position : Int) : Int
            {
                return 1
            }
        })
        _SongGridView.layoutManager = gridLayoutManager
        _SongGridView.addItemDecoration(
            GridSpacingItemDecoration(mContext,
                COLUMN_COUNT,
                CommonUtils.getInstance(mContext).getPixel(COLUMN_MARGIN),
                CommonUtils.getInstance(mContext).checkTablet)
        )
        _SongGridView.adapter = mSeriesCardViewAdapter
    }

    private val mSeriesCardItemListener : SeriesCardItemListener = object : SeriesCardItemListener
    {
        override fun onClickItem(seriesInformationResult : SeriesInformationResult, selectView : View)
        {
            factoryViewModel.onClickSongCategoriesItem(
                seriesInformationResult,
                selectView
            )
        }
    }


}