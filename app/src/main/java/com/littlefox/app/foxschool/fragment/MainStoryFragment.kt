package com.littlefox.app.foxschool.fragment

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.*
import com.littlefox.app.foxschool.R
import com.littlefox.app.foxschool.`object`.result.main.MainInformationResult
import com.littlefox.app.foxschool.`object`.result.story.SeriesInformationResult
import com.littlefox.app.foxschool.adapter.SeriesCardViewAdapter
import com.littlefox.app.foxschool.adapter.listener.SeriesCardItemListener
import com.littlefox.app.foxschool.common.Common
import com.littlefox.app.foxschool.common.CommonUtils
import com.littlefox.app.foxschool.common.Font
import com.littlefox.app.foxschool.enumerate.SeriesType
import com.littlefox.app.foxschool.main.viewmodel.factory.MainFactoryViewModel
import com.littlefox.app.foxschool.view.decoration.GridSpacingItemDecoration
import com.littlefox.library.view.animator.ViewAnimator
import com.littlefox.logmonitor.Log
import com.ssomai.android.scalablelayout.ScalableLayout

import java.util.*

class MainStoryFragment : Fragment()
{
    @BindView(R.id._navigationControllerLayout)
    lateinit var _NavigationControllerLayout : ScalableLayout

    @BindView(R.id._switchAnimationButton)
    lateinit var _SwitchAnimationButton : ImageView

    @BindView(R.id._levelsTextButton)
    lateinit var _LevelsTextButton : TextView

    @BindView(R.id._categoriesTextButton)
    lateinit var _CategoriesTextButton : TextView

    @BindView(R.id._storyGridView)
    lateinit var _StoryGridView : RecyclerView

    @BindViews(
        R.id._navigationLevel1,
        R.id._navigationLevel2,
        R.id._navigationLevel3,
        R.id._navigationLevel4,
        R.id._navigationLevel5,
        R.id._navigationLevel6,
        R.id._navigationLevel7,
        R.id._navigationLevel8,
        R.id._navigationLevel9
    )
    lateinit var _NavigationLevelButtonList : List<@JvmSuppressWildcards TextView>

    var mAnimationHandler : Handler? = object : Handler()
    {
        override fun handleMessage(msg : Message)
        {
            if(msg.what == MESSAGE_HIDE_CONTROLLER)
            {
                hideNavigationController()
            }
        }
    }

    companion object
    {
        private const val MESSAGE_HIDE_CONTROLLER = 100
        private const val DURATION_NAVIGATION_CONTROLLER_HIDE = 2500

        private var COLUMN_COUNT = 0
        private var SWITCH_TAB_WIDTH : Float = 0.0f
        private var COLUMN_MARGIN = 0
        val instance : MainStoryFragment
            get() = MainStoryFragment()
    }

    private lateinit var mContext : Context
    private lateinit var mUnbinder : Unbinder
    private var mCurrentSeriesType : SeriesType = SeriesType.LEVEL
    private lateinit var mMainInformationResult : MainInformationResult
    private lateinit var mCurrentSeriesBaseResultList : ArrayList<SeriesInformationResult>
    private var mSeriesCardViewAdapter : SeriesCardViewAdapter? = null
    private var mMoveItemColumnPosition = 0
    private var mStoryGridViewItemHeight = 0
    private var mNewScrollState = 0
    private var mCurrentScrollState = 0
    private var mCurrentScrollDy = 0
    private var mSelectColor : Int = -1

    private val factoryViewModel : MainFactoryViewModel by activityViewModels()

    override fun onAttach(context : Context)
    {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater : LayoutInflater, container : ViewGroup?, savedInstanceState : Bundle?) : View?
    {
        Log.f("")
        var view : View? = null
        if(CommonUtils.getInstance(mContext).checkTablet)
        {
            view = inflater.inflate(R.layout.fragment_main_story_tablet, container, false)
        }
        else
        {
            view = inflater.inflate(R.layout.fragment_main_story, container, false)
        }
        mUnbinder = ButterKnife.bind(this, view)
        mMainInformationResult = CommonUtils.getInstance(mContext).loadMainData()
        return view
    }

    override fun onViewCreated(view : View, savedInstanceState : Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        Log.f("")
        initView()
        initFont()
        initNavigationControllView()
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
        mSeriesCardViewAdapter?.setIndexImageVisible()
    }

    override fun onStop()
    {
        super.onStop()
    }

    override fun onDestroy()
    {
        super.onDestroy()
        mAnimationHandler?.removeMessages(MESSAGE_HIDE_CONTROLLER)
        mAnimationHandler = null
    }

    override fun onDestroyView()
    {
        super.onDestroyView()
        mUnbinder.unbind()
        Log.f("")
    }

    private fun setupObserverViewModel()
    {
        factoryViewModel.updateStoryData.observe(viewLifecycleOwner, Observer<Any> { mainInformationResult ->
            updateData(mainInformationResult as MainInformationResult)
        })
    }

    private fun updateData(mainInformationResult : MainInformationResult)
    {
        mMainInformationResult = mainInformationResult
        mCurrentSeriesType = SeriesType.LEVEL
        mCurrentScrollDy = 0
        _StoryGridView.scrollToPosition(0)
        switchTabsTextColor(mCurrentSeriesType)
        switchTabsAnimation(mCurrentSeriesType, 0)
        switchTabData(mCurrentSeriesType)
        mSeriesCardViewAdapter?.setSeriesType(mCurrentSeriesType)
        mSeriesCardViewAdapter?.setData(mCurrentSeriesBaseResultList)
        mSeriesCardViewAdapter?.notifyDataSetChanged()
    }

    @OnClick(R.id._levelsTextButton, R.id._categoriesTextButton)
    fun onClickView(view : View)
    {
        when(view.id)
        {
            R.id._levelsTextButton ->
            {
                if(mCurrentSeriesType === SeriesType.CATEGORY)
                {
                    mCurrentSeriesType = SeriesType.LEVEL
                }
            }
            R.id._categoriesTextButton ->
            {
                if(mCurrentSeriesType === SeriesType.LEVEL)
                {
                    mCurrentSeriesType = SeriesType.CATEGORY
                }
                _NavigationControllerLayout.setVisibility(View.GONE)
            }
        }
        mCurrentScrollDy = 0
        _StoryGridView.scrollToPosition(0)
        switchTabsAnimation(mCurrentSeriesType, Common.DURATION_SHORT)
        switchTabsTextColor(mCurrentSeriesType)
        switchTabData(mCurrentSeriesType)
        mSeriesCardViewAdapter?.setSeriesType(mCurrentSeriesType)
        mSeriesCardViewAdapter?.setData(mCurrentSeriesBaseResultList)
        mSeriesCardViewAdapter?.notifyDataSetChanged()
    }

    private fun initView()
    {
        if(CommonUtils.getInstance(mContext).checkTablet)
        {
            COLUMN_COUNT = 5
            SWITCH_TAB_WIDTH = 232.0f
            COLUMN_MARGIN = 20
        } else
        {
            COLUMN_COUNT = 2
            SWITCH_TAB_WIDTH = 330.0f
            COLUMN_MARGIN = 24
        }

        // 선생님/학생에 따른 셀렉터 on 이미지, 컬러 설정
        if (CommonUtils.getInstance(mContext).isTeacherMode)
        {
            mSelectColor = mContext.resources.getColor(R.color.color_29c8e6)
            _SwitchAnimationButton.setImageResource(R.drawable.tab_main_on_teacher)
        }
        else
        {
            mSelectColor = mContext.resources.getColor(R.color.color_23cc8a)
            _SwitchAnimationButton.setImageResource(R.drawable.tab_main_on_student)
        }
        switchTabsTextColor(mCurrentSeriesType)
    }

    private fun initFont()
    {
        _LevelsTextButton.setTypeface(Font.getInstance(mContext).getTypefaceBold())
        _CategoriesTextButton.setTypeface(Font.getInstance(mContext).getTypefaceBold())
        for(i in _NavigationLevelButtonList.indices)
        {
            _NavigationLevelButtonList[i].setTypeface(Font.getInstance(mContext).getTypefaceMedium())
        }
    }

    private fun initNavigationControllView()
    {
        for(i in _NavigationLevelButtonList.indices)
        {
            _NavigationLevelButtonList[i].setOnClickListener(View.OnClickListener {
                forceScrollStoryGridView(i)
            })
        }
    }

    private val sortBasicList : ArrayList<SeriesInformationResult>
        private get()
        {
            val result : ArrayList<SeriesInformationResult> =
                mMainInformationResult.getMainStoryInformation().getContentByLevelToList()
            Collections.sort(result, Comparator<SeriesInformationResult> {s0, s1 ->
                if(s0.getBasicSortNumber() < s1.getBasicSortNumber())
                {
                    -1
                } else if(s0.getBasicSortNumber() > s1.getBasicSortNumber())
                {
                    1
                } else
                {
                    0
                }
            })
            return result
        }

    private fun initRecyclerView()
    {
        switchTabData(mCurrentSeriesType)
        mSeriesCardViewAdapter = SeriesCardViewAdapter(mContext, mCurrentSeriesBaseResultList)
        mSeriesCardViewAdapter?.setSeriesCardItemListener(mSeriesCardItemListener)
        val gridLayoutManager = GridLayoutManager(mContext, COLUMN_COUNT)
        gridLayoutManager.setSpanSizeLookup(object : GridLayoutManager.SpanSizeLookup()
        {
            override fun getSpanSize(position : Int) : Int
            {
                return 1
            }
        })
        _StoryGridView.setLayoutManager(gridLayoutManager)
        _StoryGridView.addItemDecoration(
            GridSpacingItemDecoration(mContext,
                COLUMN_COUNT,
                CommonUtils.getInstance(mContext).getPixel(COLUMN_MARGIN),
                CommonUtils.getInstance(mContext).checkTablet)
        )
        _StoryGridView.setAdapter(mSeriesCardViewAdapter)
        _StoryGridView.addOnScrollListener(mStoryGridViewListener)
        _StoryGridView.getViewTreeObserver()
            .addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener
            {
                override fun onGlobalLayout()
                {
                    _StoryGridView.getViewTreeObserver().removeOnGlobalLayoutListener(this)
                    mStoryGridViewItemHeight = _StoryGridView.getChildAt(0).getHeight()
                }
            })
    }

    /**
     * 선택한 레벨의 포지션 ( 해당 레벨의 단편이 최상단에 노출되게 이동 시킨다. )
     * @param position
     */
    private fun forceScrollStoryGridView(position : Int)
    {
        val selectLevel = position + 1
        Log.i("selectLevel : $selectLevel")
        for(i in mCurrentSeriesBaseResultList.indices)
        {
            if(selectLevel == mCurrentSeriesBaseResultList[i].getLevel() && mCurrentSeriesBaseResultList[i].isSingle)
            {
                mMoveItemColumnPosition = i / COLUMN_COUNT
            }
        }
        _StoryGridView.smoothScrollBy(
            0,
            mMoveItemColumnPosition * mStoryGridViewItemHeight - mCurrentScrollDy
        )
    }

    private fun switchTabData(tab : SeriesType)
    {
        if(tab === SeriesType.LEVEL)
        {
            mCurrentSeriesBaseResultList = mMainInformationResult.getMainStoryInformation().getContentByLevelToList()
        } else
        {
            mCurrentSeriesBaseResultList = mMainInformationResult.getMainStoryInformation().getContentByCategoriesToList()
        }
        Log.f("size : " + mCurrentSeriesBaseResultList.size)
    }

    private fun switchTabsAnimation(tab : SeriesType, duration : Long)
    {
        if(tab === SeriesType.LEVEL)
        {
            ViewAnimator.animate(_SwitchAnimationButton)
                .translationX(CommonUtils.getInstance(mContext).getPixel(SWITCH_TAB_WIDTH), 0f)
                .duration(duration)
                .start()
        } else
        {
            ViewAnimator.animate(_SwitchAnimationButton)
                .translationX(0f, CommonUtils.getInstance(mContext).getPixel(SWITCH_TAB_WIDTH))
                .duration(duration)
                .start()
        }
    }

    private fun switchTabsTextColor(tab : SeriesType)
    {
        if(tab === SeriesType.LEVEL)
        {
            _LevelsTextButton.setTextColor(mSelectColor)
            _CategoriesTextButton.setTextColor(mContext.resources.getColor(R.color.color_a0a0a0))
        }
        else
        {
            _LevelsTextButton.setTextColor(mContext.resources.getColor(R.color.color_a0a0a0))
            _CategoriesTextButton.setTextColor(mSelectColor)
        }
    }

    private fun showNavigationController()
    {
        Log.f("mCurrentSeriesType : $mCurrentSeriesType")
        if(mCurrentSeriesType !== SeriesType.LEVEL)
        {
            return
        }
        if(_NavigationControllerLayout.getVisibility() == View.GONE)
        {
            ViewAnimator.animate(_NavigationControllerLayout).alpha(0.0f, 1.0f)
                .duration(Common.DURATION_SHORT)
                .onStart {_NavigationControllerLayout.setVisibility(View.VISIBLE)}
                .start()
        } else
        {
            mAnimationHandler?.removeMessages(MESSAGE_HIDE_CONTROLLER)
        }
    }

    private fun hideNavigationController()
    {
        if(mCurrentSeriesType !== SeriesType.LEVEL)
        {
            return
        }
        if(_NavigationControllerLayout.getVisibility() == View.VISIBLE)
        {
            ViewAnimator.animate(_NavigationControllerLayout).alpha(1.0f, 0.0f)
                .duration(Common.DURATION_SHORT)
                .onStop {_NavigationControllerLayout.setVisibility(View.GONE)}
                .start()
        }
    }

    private val mSeriesCardItemListener : SeriesCardItemListener = object : SeriesCardItemListener
    {
        override fun onClickItem(seriesInformationResult : SeriesInformationResult, selectView : View)
        {
            if(mCurrentSeriesType === SeriesType.LEVEL)
            {
                factoryViewModel.onClickStoryLevelsItem(seriesInformationResult, selectView)
            }
            else
            {
                factoryViewModel.onClickStoryCategoriesItem(seriesInformationResult, selectView)
            }
        }
    }

    var mStoryGridViewListener : RecyclerView.OnScrollListener =
        object : RecyclerView.OnScrollListener()
        {
            override fun onScrollStateChanged(recyclerView : RecyclerView, newState : Int)
            {
                mNewScrollState = newState
                if(newState == RecyclerView.SCROLL_STATE_DRAGGING)
                {
                    mCurrentScrollState = mNewScrollState
//                    showNavigationController()
                } else if(newState == RecyclerView.SCROLL_STATE_IDLE)
                {
                    if(mCurrentScrollState != RecyclerView.SCROLL_STATE_DRAGGING)
                    {
                        return
                    }
                    mCurrentScrollState = mNewScrollState
                    mAnimationHandler!!.sendEmptyMessageDelayed(
                        MESSAGE_HIDE_CONTROLLER,
                        DURATION_NAVIGATION_CONTROLLER_HIDE.toLong()
                    )
                }
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView : RecyclerView, dx : Int, dy : Int)
            {
                super.onScrolled(recyclerView, dx, dy)
                mCurrentScrollDy += dy
            }
        }
}