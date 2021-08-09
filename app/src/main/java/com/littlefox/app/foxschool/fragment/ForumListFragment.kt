package com.littlefox.app.foxschool.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.littlefox.app.foxschool.R
import com.littlefox.app.foxschool.`object`.result.ForumListBaseObject
import com.littlefox.app.foxschool.`object`.result.forum.ForumBaseResult
import com.littlefox.app.foxschool.adapter.ForumListAdapter
import com.littlefox.app.foxschool.adapter.listener.base.OnItemViewClickListener
import com.littlefox.app.foxschool.common.CommonUtils
import com.littlefox.app.foxschool.enumerate.ForumType
import com.littlefox.app.foxschool.viewmodel.ForumFragmentObserver
import com.littlefox.app.foxschool.viewmodel.ForumPresenterObserver
import com.littlefox.logmonitor.Log
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection
import com.ssomai.android.scalablelayout.ScalableLayout

/**
 * [팍스스쿨 소식], [자주 묻는 질문] List Fragment
 */
class ForumListFragment : Fragment()
{
    @BindView(R.id._forumSwipeRefreshLayout)
    lateinit var _ForumSwipeRefreshLayout : SwipyRefreshLayout

    @BindView(R.id._forumListView)
    lateinit var _ForumListView : RecyclerView

    @BindView(R.id._progressWheelLayout)
    lateinit var _ProgressWheelLayout : ScalableLayout

    companion object
    {
        val instance : ForumListFragment
            get() = ForumListFragment()
    }

    private lateinit var mContext : Context
    private lateinit var mUnbinder : Unbinder
    private var mForumListAdapter : ForumListAdapter? = null
    private val mTotalDataList : ArrayList<ForumBaseResult> = ArrayList<ForumBaseResult>()

    private lateinit var mForumFragmentObserver : ForumFragmentObserver
    private lateinit var mForumPresenterObserver : ForumPresenterObserver

    private var mForumType : ForumType = ForumType.FOXSCHOOL_NEWS

    /** ========== LifeCycle ========== */
    override fun onAttach(context : Context)
    {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater : LayoutInflater, container : ViewGroup?, savedInstanceState : Bundle?) : View?
    {
        Log.f("")
        val view : View
        if(CommonUtils.getInstance(mContext).checkTablet)
        {
            view = inflater.inflate(R.layout.fragment_forum_list_tablet, container, false)
        }
        else
        {
            view = inflater.inflate(R.layout.fragment_forum_list, container, false)
        }
        mUnbinder = ButterKnife.bind(this, view)
        return view
    }

    override fun onViewCreated(view : View, savedInstanceState : Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setupObserverViewModel()
    }

    override fun onResume()
    {
        Log.f("")
        super.onResume()
    }

    override fun onPause()
    {
        Log.f("")
        super.onPause()
    }

    override fun onDestroyView()
    {
        super.onDestroyView()
        mForumListAdapter = null
        mUnbinder.unbind()
    }

    override fun onDestroy()
    {
        Log.f("")
        super.onDestroy()
    }
    /** ========== LifeCycle ========== */

    /** ========== Init ========== */
    private fun initView()
    {
        _ProgressWheelLayout.setVisibility(View.VISIBLE)
        _ForumSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener)
        if(CommonUtils.getInstance(mContext).checkTablet)
        {
            val TABLET_LIST_WIDTH = 960
            val params : RelativeLayout.LayoutParams = _ForumSwipeRefreshLayout.getLayoutParams() as RelativeLayout.LayoutParams
            params.width = CommonUtils.getInstance(mContext).getPixel(TABLET_LIST_WIDTH)
            params.addRule(RelativeLayout.CENTER_HORIZONTAL)
            _ForumSwipeRefreshLayout.setLayoutParams(params)
        }
    }

    private fun initRecyclerView()
    {
        if(mForumListAdapter == null)
        {
            mForumListAdapter = ForumListAdapter(mContext, mForumType)
            mForumListAdapter?.setData(mTotalDataList)
            mForumListAdapter?.setOnItemViewClickListener(mForumListItemListener)

            val linearLayoutManager = LinearLayoutManager(mContext)
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL)
            _ForumListView.setLayoutManager(linearLayoutManager)

            val animationController : LayoutAnimationController = AnimationUtils.loadLayoutAnimation(mContext, R.anim.listview_layoutanimation)
            _ForumListView.setLayoutAnimation(animationController)
            _ForumListView.setAdapter(mForumListAdapter)
        }
        else
        {
            Log.f("mTextNormalItemListAdapter  notifyDataSetChanged")
            mForumListAdapter?.notifyDataSetChanged()
        }
    }
    /** ========== Init ========== */
    private fun setupObserverViewModel()
    {
        mForumFragmentObserver = ViewModelProviders.of(mContext as AppCompatActivity).get(ForumFragmentObserver::class.java)
        mForumPresenterObserver = ViewModelProviders.of(mContext as AppCompatActivity).get(ForumPresenterObserver::class.java)

        mForumPresenterObserver.setForumType.observe(mContext as AppCompatActivity, { type ->
            mForumType = type
        })

        mForumPresenterObserver.settingForumListData.observe(mContext as AppCompatActivity, Observer<Any> { newsListBaseObject ->
            if(viewLifecycleOwner.lifecycle.currentState != Lifecycle.State.CREATED)
            {
                setData(newsListBaseObject as ForumListBaseObject)
            }
        })

        // 재조회 취소
        mForumPresenterObserver.cancelRefreshData.observe(mContext as AppCompatActivity, Observer<Boolean?> {
            if(viewLifecycleOwner.lifecycle.currentState != Lifecycle.State.CREATED)
            {
                cancelRefreshData()
            }
        })
    }

    private fun setData(result : ForumListBaseObject)
    {
        Log.f("setData size : " + result.getData().getNewsList().size)
        if(_ForumSwipeRefreshLayout.isRefreshing())
        {
            _ForumSwipeRefreshLayout.setRefreshing(false)
        }
        if(_ProgressWheelLayout.getVisibility() == View.VISIBLE)
        {
            _ProgressWheelLayout.setVisibility(View.GONE)
        }
        mTotalDataList.addAll(result.getData().getNewsList())
        initRecyclerView()
    }

    /**
     * 재조회 취소
     */
    private fun cancelRefreshData()
    {
        Log.f("")
        if(_ForumSwipeRefreshLayout.isRefreshing())
        {
            _ForumSwipeRefreshLayout.setRefreshing(false)
        }
    }

    /**
     * 하단 당겨서 조회 리스너
     */
    private val mOnRefreshListener : SwipyRefreshLayout.OnRefreshListener = object : SwipyRefreshLayout.OnRefreshListener
    {
        override fun onRefresh(direction : SwipyRefreshLayoutDirection)
        {
            Log.f("direction : $direction")
            /**
             * 메인으로 전달하여 API 통신 시도
             */
            mForumFragmentObserver.onRequestRefresh()
        }
    }

    /**
     * 리스트 클릭 이벤트 리스너
     */
    private val mForumListItemListener : OnItemViewClickListener = object : OnItemViewClickListener
    {
        override fun onItemClick(position : Int)
        {
            mForumFragmentObserver.onShowWebView(mTotalDataList[position].getForumId())
        }
    }
}