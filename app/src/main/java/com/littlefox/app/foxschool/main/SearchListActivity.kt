package com.littlefox.app.foxschool.main

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.Rect
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.activity.viewModels
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.*
import butterknife.OnClick
import com.littlefox.app.foxschool.R
import com.littlefox.app.foxschool.adapter.SearchListItemPagingAdapter
import com.littlefox.app.foxschool.api.viewmodel.factory.SearchFactoryViewModel
import com.littlefox.app.foxschool.base.BaseActivity
import com.littlefox.app.foxschool.common.Common
import com.littlefox.app.foxschool.common.CommonUtils
import com.littlefox.app.foxschool.common.Font
import com.littlefox.app.foxschool.dialog.BottomBookAddDialog
import com.littlefox.app.foxschool.dialog.BottomContentItemOptionDialog
import com.littlefox.app.foxschool.dialog.TemplateAlertDialog
import com.littlefox.app.foxschool.dialog.listener.BookAddListener
import com.littlefox.app.foxschool.dialog.listener.DialogListener
import com.littlefox.app.foxschool.dialog.listener.ItemOptionListener
import com.littlefox.app.foxschool.enumerate.DialogButtonType
import com.littlefox.app.foxschool.`object`.result.content.ContentsBaseResult
import com.littlefox.app.foxschool.`object`.result.main.MyBookshelfResult

import com.littlefox.library.view.dialog.MaterialLoadingDialog
import com.littlefox.library.view.scroller.SmoothListviewScroller
import com.littlefox.logmonitor.Log
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection
import com.ssomai.android.scalablelayout.ScalableLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.util.ArrayList

@AndroidEntryPoint
class SearchListActivity : BaseActivity()
{
    @BindView(R.id._mainBaseLayout)
    lateinit var _MainBaseLayout : CoordinatorLayout

    @BindView(R.id._titleBaselayout)
    lateinit var _TitleBaselayout : ScalableLayout

    @BindView(R.id._titleText)
    lateinit var _TitleText : TextView

    @BindView(R.id._closeButton)
    lateinit var _CloseButton : ImageView

    @BindView(R.id._closeButtonRect)
    lateinit var _CloseButtonRect : ImageView

    @BindView(R.id._searchAllIcon)
    lateinit var _SearchAllIcon : ImageView

    @BindView(R.id._searchAllText)
    lateinit var _SearchAllText : TextView

    @BindView(R.id._searchStoryIcon)
    lateinit var _SearchStoryIcon : ImageView

    @BindView(R.id._searchStoryText)
    lateinit var _SearchStoryText : TextView

    @BindView(R.id._searchSongIcon)
    lateinit var _SearchSongIcon : ImageView

    @BindView(R.id._searchSongText)
    lateinit var _SearchSongText : TextView

    @BindView(R.id._searchEditBackgroundImage)
    lateinit var _SearchEditBackgroundImage : ImageView

    @BindView(R.id._searchCancelIcon)
    lateinit var _SearchCancelIcon : ImageView

    @BindView(R.id._searchSwipeRefreshLayout)
    lateinit var _SearchSwipeRefreshLayout : SwipyRefreshLayout

    @BindView(R.id._searchItemList)
    lateinit var _SearchItemListView : RecyclerView

    @JvmField
    @BindView(R.id._searchConfirmIcon)
    var _SearchConfirmIcon : ImageView? = null

    @JvmField
    @BindView(R.id._searchConfirmTabletIcon)
    var _SearchConfirmTabletIcon : TextView? = null

    @BindView(R.id._searchEditText)
    lateinit var _SearchEditText : EditText

    @BindView(R.id._progressWheelLayout)
    lateinit var _ProgressWheelLayout : ScalableLayout

    private var mMaterialLoadingDialog : MaterialLoadingDialog? = null
    private var isSearching : Boolean = false // 검색중인 상태인지 (통신진행중)

    private lateinit var mTemplateAlertDialog : TemplateAlertDialog
    private var mBottomContentItemOptionDialog: BottomContentItemOptionDialog? = null
    private var mBottomBookAddDialog: BottomBookAddDialog? = null
    private lateinit var mFixedSpeedScroller : SmoothListviewScroller
    private val factoryViewModel : SearchFactoryViewModel by viewModels()

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState : Bundle?)
    {
        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        super.onCreate(savedInstanceState)
        if(CommonUtils.getInstance(this).checkTablet)
        {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            setContentView(R.layout.activity_search_tablet)
        }
        else
        {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            setContentView(R.layout.activity_search)
        }
        ButterKnife.bind(this)

        initView()
        initFont()
        setupObserverViewModel()
        factoryViewModel.init(this)
    }

    override fun onResume()
    {
        super.onResume()
        factoryViewModel.resume()
    }

    override fun onPause()
    {
        super.onPause()
        factoryViewModel.pause()
    }

    override fun onDestroy()
    {
        super.onDestroy()
        factoryViewModel.destroy()
    }

    override fun finish()
    {
        super.finish()
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out)
    }

    override fun initView()
    {
        settingLayoutColor()
        _TitleText.text = resources.getString(R.string.text_search)
        _CloseButton.visibility = View.VISIBLE
        _CloseButtonRect.visibility = View.VISIBLE

        mFixedSpeedScroller = SmoothListviewScroller(this)
        mFixedSpeedScroller.targetPosition = 0

        if(CommonUtils.getInstance(this).checkTablet)
        {
            val TABLET_LIST_WIDTH = 960
            val params = (_SearchSwipeRefreshLayout.layoutParams as RelativeLayout.LayoutParams).apply {
                width = CommonUtils.getInstance(baseContext).getPixel(TABLET_LIST_WIDTH)
                addRule(RelativeLayout.CENTER_HORIZONTAL)
            }
            _SearchSwipeRefreshLayout.layoutParams = params
        }

        _SearchEditText.onFocusChangeListener = mEditFocusListener
        _SearchSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener)
        _SearchEditText.setOnEditorActionListener(mEditorActionListener)
    }

    override fun initFont()
    {
        _TitleText.typeface = Font.getInstance(this).getTypefaceBold()
        _SearchAllText.typeface = Font.getInstance(this).getTypefaceRegular()
        _SearchStoryText.typeface = Font.getInstance(this).getTypefaceRegular()
        _SearchSongText.typeface = Font.getInstance(this).getTypefaceRegular()
        _SearchEditText.typeface = Font.getInstance(this).getTypefaceRegular()

        if(CommonUtils.getInstance(this).checkTablet)
        {
            _SearchConfirmTabletIcon?.typeface = Font.getInstance(this).getTypefaceMedium()
        }
    }

    override fun setupObserverViewModel()
    {
        factoryViewModel.isLoading.observe(this){ loading ->
            if(loading)
            {
                showLoading()
            }
            else
            {
                hideLoading()
            }
        }
        factoryViewModel.toast.observe(this){ message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
        factoryViewModel.successMessage.observe(this) { message ->
            showSuccessMessage(message)
        }
        factoryViewModel.errorMessage.observe(this){ message ->
            showErrorMessage(message)
        }
        factoryViewModel.showSearchListView.observe(this){ adapter ->
            showSearchListView(adapter)
        }
        factoryViewModel.dialogBottomOption.observe(this){ data ->
            showBottomContentItemDialog(data)
        }
        factoryViewModel.dialogBottomBookshelfContentAdd.observe(this){ list ->
            showBottomBookAddDialog(list)
        }
        factoryViewModel.dialogRecordPermission.observe(this){
            showChangeRecordPermissionDialog()
        }
        factoryViewModel.showContentsLoading.observe(this){
            showContentsListLoading()
        }
        factoryViewModel.hideContentsLoading.observe(this){
            hideContentsListLoading()
        }

        factoryViewModel.enableRefreshLoading.observe(this){ enable ->
            if(enable)
            {
                _SearchSwipeRefreshLayout.isRefreshing = true
            }
            else
            {
                _SearchSwipeRefreshLayout.isRefreshing = false
            }
        }
    }

    /**
     * 상단바 색상 설정
     */
    private fun settingLayoutColor()
    {
        val statusBarColor : Int = CommonUtils.getInstance(this).getTopBarStatusBarColor()
        val backgroundColor : Int = CommonUtils.getInstance(this).getTopBarBackgroundColor()
        CommonUtils.getInstance(this).setStatusBar(resources.getColor(statusBarColor))
        _TitleBaselayout.setBackgroundColor(resources.getColor(backgroundColor))
    }


    fun showContentsListLoading()
    {
        if(_ProgressWheelLayout.visibility == View.GONE)
        {
            _ProgressWheelLayout.visibility = View.VISIBLE
            _SearchItemListView.visibility = View.INVISIBLE
            isSearching = true
            setSearchButtonEnable(false)
        }
    }

    fun hideContentsListLoading()
    {
        if(_ProgressWheelLayout.visibility == View.VISIBLE)
        {
            _ProgressWheelLayout.visibility = View.GONE
            _SearchItemListView.visibility = View.VISIBLE
            isSearching = false
            setSearchButtonEnable(true)
        }
    }


    fun showSuccessMessage(message : String)
    {
        CommonUtils.getInstance(this).showSuccessSnackMessage(_MainBaseLayout, message)
    }

    fun showErrorMessage(message : String)
    {
        CommonUtils.getInstance(this).showErrorSnackMessage(_MainBaseLayout, message)
    }

    private fun showChangeRecordPermissionDialog()
    {
        mTemplateAlertDialog = TemplateAlertDialog(this).apply {
            setMessage(resources.getString(R.string.message_record_permission))
            setDialogEventType(SearchFactoryViewModel.DIALOG_TYPE_WARNING_RECORD_PERMISSION)
            setButtonType(DialogButtonType.BUTTON_2)
            setButtonText(
                resources.getString(R.string.text_cancel),
                resources.getString(R.string.text_change_permission))
            setDialogListener(mDialogListener)
            show()
        }
    }

    private fun showBottomContentItemDialog(result : ContentsBaseResult)
    {
        mBottomContentItemOptionDialog = BottomContentItemOptionDialog(this, result)
            ?.setItemOptionListener(mItemOptionListener)
            ?.setFullName()
            ?.setView()
        mBottomContentItemOptionDialog?.show()
    }

    private fun showBottomBookAddDialog(list: ArrayList<MyBookshelfResult>)
    {
        mBottomContentItemOptionDialog?.dismiss()

        mBottomBookAddDialog = BottomBookAddDialog(this).apply {
            setCancelable(true)
            setBookshelfData(list)
            setBookSelectListener(mBookAddListener)
            show()
        }
    }


    /**
     * 리스트 표시 애니메이션
     */
    fun showSearchListView(detailListItemAdapter : SearchListItemPagingAdapter)
    {
        val animationController = AnimationUtils.loadLayoutAnimation(this, R.anim.listview_layoutanimation)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        _SearchItemListView.run {
            layoutManager = linearLayoutManager
            layoutAnimation = animationController
            adapter = detailListItemAdapter
        }
    }

    fun cancelRefreshView()
    {
        Log.f("")
        if(_SearchSwipeRefreshLayout.isRefreshing)
        {
            _SearchSwipeRefreshLayout.isRefreshing = false
        }
    }

    /**
     * 키보드 밖 영역 터치시 키보드 닫기 위한 이벤트
     */
    override fun dispatchTouchEvent(ev : MotionEvent) : Boolean
    {
        if(ev.action == MotionEvent.ACTION_UP)
        {
            val view = currentFocus

            if(view != null)
            {
                val consumed = super.dispatchTouchEvent(ev)

                val viewTmp = currentFocus
                val viewNew : View = viewTmp ?: view

                if(viewNew == view) {
                    val coordinates = IntArray(2)

                    view.getLocationOnScreen(coordinates)

                    val rect = Rect(coordinates[0], coordinates[1], coordinates[0] + view.width, coordinates[1] + view.height)

                    val x = ev.x.toInt()
                    val y = ev.y.toInt()

                    if(rect.contains(x, y)) {
                        return consumed
                    }
                }
                else if(viewNew is EditText) {
                    return consumed
                }
                CommonUtils.getInstance(this@SearchListActivity).hideKeyboard()
                viewNew.clearFocus()

                return consumed
            }
        }

        return super.dispatchTouchEvent(ev)
    }

    @Optional
    @OnClick(
        R.id._closeButtonRect, R.id._searchAllRect, R.id._searchStoryRect, R.id._searchSongRect,
        R.id._searchConfirmIcon, R.id._searchConfirmTabletIcon, R.id._searchCancelIcon
    )
    fun onClickView(view: View)
    {
        // 검색 통신이 진행중인 경우 클릭이벤트 막기
        if (isSearching) return

        when(view.id)
        {
            R.id._closeButtonRect ->
            {
                super.onBackPressed()
            }
            R.id._searchAllRect ->
            {
                switchSearchTypeIcon(Common.CONTENT_TYPE_ALL)
                factoryViewModel.onClickSearchType(Common.CONTENT_TYPE_ALL)
            }
            R.id._searchStoryRect ->
            {
                switchSearchTypeIcon(Common.CONTENT_TYPE_STORY)
                factoryViewModel.onClickSearchType(Common.CONTENT_TYPE_STORY)
            }
            R.id._searchSongRect ->
            {
                switchSearchTypeIcon(Common.CONTENT_TYPE_SONG)
                factoryViewModel.onClickSearchType(Common.CONTENT_TYPE_SONG)
            }
            R.id._searchCancelIcon ->
            {
                _SearchEditText.setText("")
            }
            R.id._searchConfirmIcon, R.id._searchConfirmTabletIcon ->
            {
                mFixedSpeedScroller.targetPosition = 0
                CoroutineScope(Dispatchers.Main).launch {
                    _SearchItemListView.layoutManager?.startSmoothScroll(mFixedSpeedScroller)
                    withContext(Dispatchers.IO){
                        delay(Common.DURATION_SHORT)
                    }
                    factoryViewModel.onClickSearchExecute(_SearchEditText.text.toString())
                }
            }
        }
    }

    /**
     * 검색 체크 아이콘 변경
     */
    private fun switchSearchTypeIcon(type : String)
    {
        when(type)
        {
            Common.CONTENT_TYPE_ALL ->
            {
                _SearchAllIcon.setImageResource(R.drawable.check_on)
                _SearchStoryIcon.setImageResource(R.drawable.check_off)
                _SearchSongIcon.setImageResource(R.drawable.check_off)
            }
            Common.CONTENT_TYPE_STORY ->
            {
                _SearchAllIcon.setImageResource(R.drawable.check_off)
                _SearchStoryIcon.setImageResource(R.drawable.check_on)
                _SearchSongIcon.setImageResource(R.drawable.check_off)
            }
            Common.CONTENT_TYPE_SONG ->
            {
                _SearchAllIcon.setImageResource(R.drawable.check_off)
                _SearchStoryIcon.setImageResource(R.drawable.check_off)
                _SearchSongIcon.setImageResource(R.drawable.check_on)
            }
        }
    }

    private fun setSearchButtonEnable(isEnable : Boolean)
    {
        val alpha = if(isEnable) 1.0f else 0.5f
        if(CommonUtils.getInstance(this).checkTablet)
        {
            _SearchConfirmTabletIcon?.isEnabled = isEnable
            _SearchConfirmTabletIcon?.alpha = alpha
        }
        else
        {
            _SearchConfirmIcon?.isEnabled = isEnable
            _SearchConfirmIcon?.alpha = alpha
        }
    }

    /** ====================== set Listener ====================== */

    /**
     * 키보드 이벤트 리스너 (검색)
     */
    private val mEditorActionListener = object : TextView.OnEditorActionListener
    {
        override fun onEditorAction(textView : TextView, id : Int, keyEvent : KeyEvent?) : Boolean
        {
            when(id)
            {
                EditorInfo.IME_ACTION_SEARCH ->
                {
                    CommonUtils.getInstance(this@SearchListActivity).hideKeyboard()
                    _SearchEditText.clearFocus()
                    mFixedSpeedScroller.targetPosition = 0
                    CoroutineScope(Dispatchers.Main).launch {
                        _SearchItemListView.layoutManager?.startSmoothScroll(mFixedSpeedScroller)
                        withContext(Dispatchers.IO){
                            delay(Common.DURATION_SHORT)
                        }
                        factoryViewModel.onClickSearchExecute(_SearchEditText.text.toString())
                    }
                }
            }
            return true
        }
    }

    /**
     * 검색영역 포커싱 이벤트 (백그라운드 이미지 변경용, 선택 시 파란색상)
     */
    private val mEditFocusListener = object : View.OnFocusChangeListener
    {
        override fun onFocusChange(view : View, hasFocus : Boolean)
        {
            Log.f("hasFocus : $hasFocus")
            when(view.id)
            {
                R.id._searchEditText ->
                {
                    if(hasFocus)
                    {
                        if(CommonUtils.getInstance(this@SearchListActivity).checkTablet)
                        {
                            _SearchEditBackgroundImage.setBackgroundResource(R.drawable.text_box_b)
                        }
                        else
                        {
                            _SearchEditBackgroundImage.setBackgroundResource(R.drawable.text_box_b_search)
                        }

                        _SearchEditText.isCursorVisible = true
                    }
                    else
                    {
                        if(CommonUtils.getInstance(this@SearchListActivity).checkTablet)
                        {
                            _SearchEditBackgroundImage.setBackgroundResource(R.drawable.box_list)
                        }
                        else
                        {
                            _SearchEditBackgroundImage.setBackgroundResource(R.drawable.search_box1)
                        }

                        _SearchEditText.isCursorVisible = false
                    }
                }
            }
        }
    }

    private val mOnRefreshListener = object : SwipyRefreshLayout.OnRefreshListener
    {
        override fun onRefresh(direction : SwipyRefreshLayoutDirection?)
        {
            Log.f("direction : $direction")
           // mSearchListPresenter.requestRefresh()
        }
    }

    private val mItemOptionListener : ItemOptionListener = object : ItemOptionListener
    {
        override fun onClickQuiz()
        {
            factoryViewModel.onClickQuizButton()
        }

        override fun onClickTranslate()
        {
            factoryViewModel.onClickTranslateButton()
        }

        override fun onClickVocabulary()
        {
            factoryViewModel.onClickVocabularyButton()
        }

        override fun onClickBookshelf()
        {
            factoryViewModel.onClickAddBookshelfButton()
        }

        override fun onClickEbook()
        {
            factoryViewModel.onClickEbookButton()
        }

        override fun onClickGameStarwords()
        {
            factoryViewModel.onClickStarwordsButton()
        }

        override fun onClickGameCrossword()
        {
            factoryViewModel.onClickCrosswordButton()
        }

        override fun onClickFlashCard()
        {
            factoryViewModel.onClickFlashcardButton()
        }

        override fun onClickRecordPlayer()
        {
            factoryViewModel.onClickRecordPlayerButton()
        }
    }

    private val mBookAddListener : BookAddListener = object : BookAddListener
    {
        override fun onClickBook(index : Int)
        {
            factoryViewModel.onDialogAddBookshelfClick(index)
        }
    }

    private val mDialogListener : DialogListener = object : DialogListener
    {
        override fun onConfirmButtonClick(eventType : Int)
        {
            factoryViewModel.onDialogClick(eventType)
        }

        override fun onChoiceButtonClick(buttonType : DialogButtonType, eventType : Int)
        {
            factoryViewModel.onDialogChoiceClick(buttonType, eventType)
        }
    }
}