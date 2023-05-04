package com.littlefox.app.foxschool.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.activity.viewModels
import androidx.annotation.Nullable
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.drawerlayout.widget.DrawerLayout
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import butterknife.*
import butterknife.Optional
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import com.littlefox.app.foxschool.R
import com.littlefox.app.foxschool.`object`.result.login.LoginInformationResult
import com.littlefox.app.foxschool.`object`.result.main.InAppCompaignResult
import com.littlefox.app.foxschool.`object`.result.main.MainInformationResult
import com.littlefox.app.foxschool.adapter.MainFragmentSelectionPagerAdapter
import com.littlefox.app.foxschool.main.viewmodel.factory.MainFactoryViewModel
import com.littlefox.app.foxschool.base.BaseActivity
import com.littlefox.app.foxschool.common.Common
import com.littlefox.app.foxschool.common.CommonUtils
import com.littlefox.app.foxschool.common.Font
import com.littlefox.app.foxschool.dialog.TemplateAlertDialog
import com.littlefox.app.foxschool.dialog.listener.DialogListener
import com.littlefox.app.foxschool.enumerate.DialogButtonType
import com.littlefox.library.view.extra.SwipeDisableViewPager
import com.littlefox.library.view.scroller.FixedSpeedScroller
import com.littlefox.library.view.text.SeparateTextView
import com.littlefox.logmonitor.Log
import com.ssomai.android.scalablelayout.ScalableLayout
import dagger.hilt.android.AndroidEntryPoint

import java.lang.reflect.Field

@AndroidEntryPoint
class MainActivity() : BaseActivity()
{
    @BindView(R.id._mainDrawLayout)
    lateinit var _MainDrawLayout : DrawerLayout

    @BindView(R.id._mainContent)
    lateinit var _MainContentCoordinatorLayout : CoordinatorLayout

    @JvmField
    @BindView(R.id._mainAppbarLayout)
    var _MainAppbarLayout : AppBarLayout? = null

    @BindView(R.id._mainBackgroundView)
    lateinit var _MainBackgroundView : ImageView

    @BindView(R.id._mainBackgroundAnimationLayout)
    lateinit var _MainBackgroundAnimationLayout : FrameLayout

    @JvmField
    @BindView(R.id._topMenuSetting)
    var _TopMenuSetting : ImageView? = null

    @JvmField
    @BindView(R.id._topMenuSearch)
    var _TopMenuSearch : ImageView? = null

    @JvmField
    @BindView(R.id._topMenuSchoolName)
    var _TopMenuSchoolName : TextView? = null

    @JvmField
    @BindView(R.id._mainToolBar)
    var _MainToolbar : Toolbar? = null

    @BindView(R.id._mainTabLayout)
    lateinit var _MainTabsLayout : TabLayout

    @BindView(R.id._mainViewPager)
    lateinit var _MainViewPager : ViewPager

    @BindView(R.id._navigationBaseLayout)
    lateinit var _NavigationBaseLayout : RelativeLayout

    /*
    DRAWER LAYOUT
     */
    @BindView(R.id._userStatusLayout)
    lateinit var _UserStatusLayout : ScalableLayout


    @BindView(R.id._userNameText)
    lateinit var _UserNameText : SeparateTextView

    @BindView(R.id._userInfoButtonText)
    lateinit var _UserInfoButtonText : TextView


    @BindView(R.id._studyInfoLayout)
    lateinit var _StudyInfoLayout : ScalableLayout

    @BindView(R.id._leaningLogMenuButton)
    lateinit var _LeaningLogMenuButton : ImageView

    @BindView(R.id._leaningLogMenuText)
    lateinit var _LeaningLogMenuText : TextView

    @BindView(R.id._recordLogText)
    lateinit var _RecordLogText : TextView

    @BindView(R.id._homeworkManageMenuButton)
    lateinit var _HomeworkManageMenuButton : ImageView

    @BindView(R.id._homeworkManageText)
    lateinit var _HomeworkManageText : TextView

    @BindView(R.id._menuHomeworkManageNewIcon)
    lateinit var _MenuHomeworkManageNewIcon : ImageView

    @BindView(R.id._menuItemScrollView)
    lateinit var _MenuItemScrollView : ScrollView

    @BindView(R.id._menuItemLayout)
    lateinit var _MenuItemLayout : LinearLayout


    @BindView(R.id._menuLogoutLayout)
    lateinit var _MenuLogoutLayout : ScalableLayout

    @BindView(R.id._menuLogoutText)
    lateinit var _MenuLogoutText : TextView

    companion object
    {
        private val TAB_IMAGE_ICONS_STUDENT = intArrayOf(
            R.drawable.choice_top_bar_icon_story_student,
            R.drawable.choice_top_bar_icon_song_student,
            R.drawable.choice_top_bar_icon_my_books_student
        )
        private val TAB_IMAGE_ICONS_TEACHER = intArrayOf(
            R.drawable.choice_top_bar_icon_story_teacher,
            R.drawable.choice_top_bar_icon_song_teacher,
            R.drawable.choice_top_bar_icon_my_books_teacher
        )
        private val TAB_IMAGE_ICONS_TABLET = intArrayOf(
            R.drawable.choice_top_bar_icon_story_tablet,
            R.drawable.choice_top_bar_icon_song_tablet,
            R.drawable.choice_top_bar_icon_my_books_tablet
        )
    }

    private lateinit var _SchoolNameText : TextView
    private lateinit var _SettingButton : ImageView
    private lateinit var _SearchButton : ImageView

    private var mLoginInformationResult : LoginInformationResult? = null
    private lateinit var mMainInformationResult : MainInformationResult

    private lateinit var mTemplateAlertDialog : TemplateAlertDialog

    private val factoryViewModel : MainFactoryViewModel by viewModels()

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState : Bundle?)
    {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        super.onCreate(savedInstanceState)


        if(CommonUtils.getInstance(this).checkTablet)
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE)
            setContentView(R.layout.activity_main_tablet)
        } else
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            setContentView(R.layout.activity_main)
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
        Log.f("")
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

    override fun initView()
    {
        setStatusBarColor()
        setIndicatorBarColor()
        _MainDrawLayout.run {
            setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            isFocusableInTouchMode = false
            addDrawerListener(mDrawerListener)
        }

        _NavigationBaseLayout.setOnTouchListener(object : View.OnTouchListener
        {
            override fun onTouch(v : View, event : MotionEvent) : Boolean
            {
                return true
            }
        })

        if(CommonUtils.getInstance(this).checkTablet == false)
        {
            settingToolbar()
        }
        else
        {
            val TABLET_DRAWER_MENU_WIDTH = 650
            val params : DrawerLayout.LayoutParams = _NavigationBaseLayout.layoutParams as DrawerLayout.LayoutParams
            params.width = CommonUtils.getInstance(this).getPixel(TABLET_DRAWER_MENU_WIDTH)
            _NavigationBaseLayout.layoutParams = params
        }
    }

    override fun initFont()
    {
        _UserNameText.typeface = Font.getInstance(this).getTypefaceMedium()
        _UserInfoButtonText.typeface = Font.getInstance(this).getTypefaceMedium()
        _LeaningLogMenuText.typeface = Font.getInstance(this).getTypefaceMedium()
        _RecordLogText.typeface = Font.getInstance(this).getTypefaceMedium()
        _HomeworkManageText.typeface = Font.getInstance(this).getTypefaceMedium()
        if (CommonUtils.getInstance(this).checkTablet)
        {
            _TopMenuSchoolName?.typeface = Font.getInstance(this).getTypefaceBold()
        }
    }

    private fun initViewPager(mainFragmentSelectionPagerAdapter : MainFragmentSelectionPagerAdapter)
    {
        settingViewPagerInformation(mainFragmentSelectionPagerAdapter)
        if(CommonUtils.getInstance(this).checkTablet == false)
        {
            checkToolbarAnimationLayoutSize()
        }
    }

    override fun onBackPressed()
    {
        if(_MainDrawLayout.isDrawerOpen(_NavigationBaseLayout))
        {
            _MainDrawLayout.closeDrawer(_NavigationBaseLayout)
        }
        else
        {
            factoryViewModel.onBackPressed()
        }
    }

    override fun setupObserverViewModel()
    {
        factoryViewModel.isLoading.observe(this, Observer<Boolean> { loading ->
            if (loading)
            {
                showLoading()
            }
            else
            {
                hideLoading()
            }
        })

        factoryViewModel.toast.observe(this, Observer<String> { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        })

        factoryViewModel.successMessage.observe(this, Observer<String> { message ->
            CommonUtils.getInstance(this).showSuccessSnackMessage(_MainContentCoordinatorLayout, message, Gravity.CENTER)
        })

        factoryViewModel.errorMessage.observe(this, Observer<String> { message ->
            CommonUtils.getInstance(this).showErrorSnackMessage(_MainContentCoordinatorLayout, message, Gravity.CENTER)
        })

        factoryViewModel.showIACInformationDialog.observe(this, Observer { result ->
            showIACInformationDialog(result)
        })

        factoryViewModel.showNoClassStudentDialog.observe(this, Observer {
            showStudentNoClassDialog()
        })

        factoryViewModel.showNoClassTeacherDialog.observe(this, Observer {
            showTeacherNoClassDialog()
        })

        factoryViewModel.showAppEndDialog.observe(this, Observer {
            showAppEndDialog()
        })

        factoryViewModel.settingViewPager.observe(this, Observer { adapter ->
            initViewPager(adapter)
        })

        factoryViewModel.settingMenuView.observe(this, Observer { pair ->
            val isUpdateHomework = pair.first
            val isUpdateNews = pair.second

            Log.f("isUpdateHomework : $isUpdateHomework, isUpdateNews : $isUpdateNews")
            initMenuView(isUpdateHomework, isUpdateNews)
        })

        factoryViewModel.settingUserInformation.observe(this, Observer { userInformation ->
            mLoginInformationResult = userInformation

            settingUserLayout()
            settingLayoutColor()
            settingSchoolName()
        })
    }

    private fun showDownloadMessage(message : String)
    {
        CommonUtils.getInstance(this).showSnackMessage(
            _MainContentCoordinatorLayout,
            message,
            resources.getColor(R.color.color_white))
    }

    /**
     * 사용자 영역 세팅 (이름, 반)
     */
    private fun settingUserLayout()
    {
        // 최대 글자 수
        // 폰 : 18자, 태블릿 : 23자
        val NAME_TEXT_MAX_LENGTH : Int = if (CommonUtils.getInstance(this).checkTablet) 23 else 18

        var name : String? = mLoginInformationResult?.getUserInformation()?.getName()
        var className : String = ""
        if (CommonUtils.getInstance(this).isTeacherMode)
        {
            // 선생님은 이름 뒤에 "선생님" 붙이고, 반은 표시하지 않는다.
            name += " 선생님"
        }
        else
        {
            // 학생은 이름 뒤에 "학생"을 붙이지 않고, 반을 표시한다.
            className = CommonUtils.getInstance(this).getClassName(mLoginInformationResult!!.getSchoolInformation())
        }


        // 이름/반 글자 크기
        val nameSize : Int
        val classSize : Int
        if (CommonUtils.getInstance(this).checkTablet)
        {
            nameSize = 27
            classSize = 22
        }
        else
        {
            nameSize = 50
            classSize = 40
        }

       _UserNameText.setSeparateText(name, " $className")
            .setSeparateColor(resources.getColor(R.color.color_ffffff), resources.getColor(R.color.color_ffffff))
            .setSeparateTextSize(CommonUtils.getInstance(this).getPixel(nameSize), CommonUtils.getInstance(this).getPixel(classSize))
            .setSeparateTextStyle((Font.getInstance(this).getTypefaceMedium()), (Font.getInstance(this).getTypefaceMedium()))
            .showView()
    }

    /**
     * 상단바 & 메뉴화면 색상 설정
     */
    private fun settingLayoutColor()
    {
        val statusBarColor : Int = CommonUtils.getInstance(this).getTopBarStatusBarColor()
        val backgroundColor : Int = CommonUtils.getInstance(this).getTopBarBackgroundColor()
        CommonUtils.getInstance(this).setStatusBar(resources.getColor(statusBarColor))
        _MainBackgroundView.setBackgroundColor(resources.getColor(backgroundColor))
        _UserStatusLayout.setBackgroundColor(resources.getColor(backgroundColor))
        _UserInfoButtonText.setTextColor(resources.getColor(backgroundColor))

        if (CommonUtils.getInstance(this).isTeacherMode)
        {
            _UserInfoButtonText.setBackgroundResource(R.drawable.round_box_empty_light_blue_60)
        }
        else
        {
            _UserInfoButtonText.setBackgroundResource(R.drawable.round_box_empty_green_60)
        }
    }

    /**
     * 학교명 설정
     */
    private fun settingSchoolName()
    {
        var schoolName : String = ""
        if(CommonUtils.getInstance(this).isTeacherMode)
        {
            schoolName = mLoginInformationResult!!.getTeacherInformation().getOrganizationName()
        }
        else
        {
            schoolName = mLoginInformationResult!!.getSchoolInformation().getOrganizationName()
        }

        // 학교명 16자 초과 시 말줄임표 처리
        if (schoolName.length > 16)
        {
            schoolName = schoolName.substring(0, 16)
            schoolName += "..."
        }

        if (CommonUtils.getInstance(this).checkTablet)
        {
            _TopMenuSchoolName?.setText(schoolName)
        }
        else
        {
            _SchoolNameText.setText(schoolName)
        }
    }

    private fun showAppEndDialog()
    {
        Log.f("Check End App")
        showTemplateAlertDialog(
            this.resources.getString(R.string.message_check_end_app),
            MainFactoryViewModel.DIALOG_TYPE_APP_END,
            DialogButtonType.BUTTON_2
        )
    }

    private fun showTemplateAlertDialog(message : String, eventType : Int, buttonType : DialogButtonType)
    {
        mTemplateAlertDialog = TemplateAlertDialog(this).apply {
            setMessage(message)
            setDialogEventType(eventType)
            setButtonType(buttonType)
            setDialogListener(mDialogListener)
            setGravity(Gravity.LEFT)
            show()
        }
    }

    private fun showStudentNoClassDialog()
    {
        showTemplateAlertDialog(
            getString(R.string.message_warning_not_have_class_student),
            MainFactoryViewModel.DIALOG_TYPE_NOT_HAVE_CLASS,
            DialogButtonType.BUTTON_1
        )
    }

    private fun showTeacherNoClassDialog()
    {
        showTemplateAlertDialog(
            getString(R.string.message_warning_not_have_class_teacher),
            MainFactoryViewModel.DIALOG_TYPE_NOT_HAVE_CLASS,
            DialogButtonType.BUTTON_1
        )
    }

    private fun showLogoutDialog()
    {
        showTemplateAlertDialog(
            getString(R.string.message_try_logout),
            MainFactoryViewModel.DIALOG_TYPE_LOGOUT,
            DialogButtonType.BUTTON_2
        )
    }

    private fun showIACInformationDialog(result : InAppCompaignResult)
    {
        mTemplateAlertDialog = TemplateAlertDialog(this).apply {
            setTitle(result.getTitle())
            setMessage(result.getContent())
            if(result.isButton1Use == false)
            {
                setButtonText(result.getButton2Text())
            } else
            {
                setButtonText(result.getButton1Text(), result.getButton2Text())
            }
            setDialogEventType(MainFactoryViewModel.DIALOG_TYPE_IAC)
            setDialogListener(mDialogListener)
            show()
        }
    }

    @Optional
    @OnClick(
        R.id._userInfoButtonText, R.id._leaningLogMenuButton, R.id._menuLogoutLayout, R.id._homeworkManageMenuButton, R.id._recordLogMenuButton,
        R.id._topMenuSetting, R.id._topMenuSearch
    )
    fun onClickView(view : View)
    {
        when(view.id)
        {
            R.id._userInfoButtonText ->
            {
                _MainDrawLayout.closeDrawer(_NavigationBaseLayout)
                factoryViewModel.onClickMenuMyInformation()
            }
            R.id._leaningLogMenuButton ->
            {
                _MainDrawLayout.closeDrawer(_NavigationBaseLayout)
                factoryViewModel.onClickMenuLearningLog()
            }
            R.id._recordLogMenuButton ->
            {
                _MainDrawLayout.closeDrawer(_NavigationBaseLayout)
                factoryViewModel.onClickRecordHistory()
            }
            R.id._homeworkManageMenuButton ->
            {
                _MainDrawLayout.closeDrawer(_NavigationBaseLayout)
                factoryViewModel.onClickMenuHomeworkManage()
            }
            R.id._menuLogoutLayout ->
            {
                _MainDrawLayout.closeDrawer(_NavigationBaseLayout)
                showLogoutDialog()
            }
            R.id._topMenuSetting ->
            {
                Log.f("")
                _MainDrawLayout.openDrawer(_NavigationBaseLayout)
            }
            R.id._topMenuSearch ->
            {
                Log.f("")
                factoryViewModel.onClickSearch()
            }
        }
    }

    private fun checkToolbarAnimationLayoutSize()
    {
        _MainToolbar?.viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener
        {
            override fun onGlobalLayout()
            {
                _MainToolbar?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
                Log.f("Toolbar Height :${_MainToolbar?.height}, _MainTabsLayout height : ${_MainTabsLayout.measuredHeight}")
                _MainToolbar?.let {
                    var params : RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, it.height)
                    _MainBackgroundView.layoutParams = params
                    params = RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        it.height
                    )
                    _MainBackgroundAnimationLayout.layoutParams = params
                }
            }
        })
    }

    /**
     * 메뉴화면 생성 (팍스스쿨 소식 ~ 로그아웃)
     */
    private fun initMenuView(isUpdateHomework : Boolean, isUpdateNews : Boolean)
    {
        val MENU_TEXTVIEW_ID_LIST = intArrayOf(
            R.id._menuFoxschoolNewsText,
            R.id._menuFAQsText,
            R.id._menu1on1AskText,
            R.id._menuAboutAppText,
            R.id._menuTeacherManualText,
            R.id._menuHomeNewspaperText
        )
        val MENU_IMAGEVIEW_ID_LIST = intArrayOf(
            R.id._menuFoxschoolNewsButtonRect,
            R.id._menuFAQsButtonRect,
            R.id._menu1on1AskButtonRect,
            R.id._menuAboutAppButtonRect,
            R.id._menuTeacherManualButtonRect,
            R.id._menuHomeNewspaperButtonRect
        )
        var addLayout : LinearLayout? = null
        _MenuItemLayout.removeAllViews()

        if(CommonUtils.getInstance(this).checkTablet)
        {
            addLayout = View.inflate(this, R.layout.drawer_item_user_tablet, null) as LinearLayout
        } else
        {
            addLayout = View.inflate(this, R.layout.drawer_item_user_phone, null) as LinearLayout
        }

        _MenuItemLayout.addView(addLayout)
        for(i in MENU_TEXTVIEW_ID_LIST.indices)
        {
            val textView : TextView = addLayout.findViewById<View>(MENU_TEXTVIEW_ID_LIST[i]) as TextView
            textView.typeface = Font.getInstance(this).getTypefaceMedium()
            val imageView = addLayout.findViewById<View>(MENU_IMAGEVIEW_ID_LIST[i]) as ImageView
            imageView.setOnClickListener(mMenuClickListener)

            // 학생의 경우 교사 메뉴얼, 가정 통신문 항목 숨김
            if (CommonUtils.getInstance(this).isTeacherMode == false)
            {
                if ((i > MENU_TEXTVIEW_ID_LIST.size - 3))
                {
                    textView.visibility = View.GONE
                    imageView.visibility = View.GONE
                }
            }
        }

        val iconUpdateNewsView = addLayout.findViewById<View>(R.id._menuFoxschoolNewsNewIcon) as ImageView
        if(isUpdateNews)
        {
            iconUpdateNewsView.visibility = View.VISIBLE
        }
        else
        {
            iconUpdateNewsView.visibility = View.GONE
        }

        if(isUpdateHomework)
        {
            _MenuHomeworkManageNewIcon.visibility = View.VISIBLE
        }
        else
        {
            _MenuHomeworkManageNewIcon.visibility = View.GONE
        }

        // 학생의 경우 교사 메뉴얼, 가정 통신문 항목 숨김
        if (CommonUtils.getInstance(this).isTeacherMode == false)
        {
            val iconTeacherManual = addLayout.findViewById<View>(R.id._menuTeacherManualIcon) as ImageView
            val iconNewspaper = addLayout.findViewById<View>(R.id._menuHomeNewspaperIcon) as ImageView
            iconTeacherManual.visibility = View.GONE
            iconNewspaper.visibility = View.GONE
        }

        _MenuLogoutText.typeface = Font.getInstance(this).getTypefaceMedium()
    }

    private fun settingViewPagerInformation(mainFragmentSelectionPagerAdapter : MainFragmentSelectionPagerAdapter)
    {
        Log.f("Screen Page Count : " + mainFragmentSelectionPagerAdapter.getCount())
        _MainViewPager.run {
            setAdapter(mainFragmentSelectionPagerAdapter)
            setOffscreenPageLimit(mainFragmentSelectionPagerAdapter.getCount())
            addOnPageChangeListener(mOnPageChangeListener)
        }
        _MainTabsLayout.setTabGravity(TabLayout.GRAVITY_FILL)
        _MainTabsLayout.setupWithViewPager(_MainViewPager)
        settingTabsLayout(mainFragmentSelectionPagerAdapter.getCount())
    }

    private fun settingToolbar()
    {
        val customView : View
        val inflater : LayoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        setSupportActionBar(_MainToolbar)
        getSupportActionBar()?.setDisplayShowHomeEnabled(false)
        getSupportActionBar()?.setDisplayShowCustomEnabled(true)
        getSupportActionBar()?.setDisplayShowTitleEnabled(false)
        if(CommonUtils.getInstance(this).checkTablet)
        {
            customView = inflater.inflate(R.layout.topbar_main_menu_tablet, null)
        } else
        {
            customView = inflater.inflate(R.layout.topbar_main_menu, null)
        }

        _SettingButton = customView.findViewById<View>(R.id._topMenuSetting) as ImageView
        _SettingButton.setOnClickListener(View.OnClickListener { // TODO: 드로우 레이아웃을 연다.
            Log.f("")
            _MainDrawLayout.openDrawer(_NavigationBaseLayout)
        })
        _SchoolNameText = customView.findViewById<View>(R.id._topMenuSchoolName) as TextView
        _SchoolNameText.typeface = Font.getInstance(this).getTypefaceBold()
        _SearchButton = customView.findViewById<View>(R.id._topMenuSearch) as ImageView
        _SearchButton.setOnClickListener(object : View.OnClickListener
        {
            override fun onClick(view : View)
            {
                Log.f("")
                factoryViewModel.onClickSearch()
            }
        })
        getSupportActionBar()?.setCustomView(customView)
        val parent = customView.parent as Toolbar
        parent.setContentInsetsAbsolute(0, 0)
    }

    private fun settingTabsLayout(tabsSize : Int)
    {
        var params : LinearLayout.LayoutParams? = null
        val viewGroup : View? = null
        for(i in 0 until tabsSize)
        {
            val index = i
            if(CommonUtils.getInstance(this).checkTablet)
            {
                val image = ImageView(this)
                params = LinearLayout.LayoutParams(
                    CommonUtils.getInstance(this).getPixel(40),
                    CommonUtils.getInstance(this).getHeightPixel(36))
                image.layoutParams = params
                image.setImageResource(TAB_IMAGE_ICONS_TABLET[i])
                _MainTabsLayout.getTabAt(i)?.setCustomView(image)
            }
            else
            {
                val image = ImageView(this)
                params = LinearLayout.LayoutParams(
                    CommonUtils.getInstance(this).getPixel(72),
                    CommonUtils.getInstance(this).getHeightPixel(68))
                image.layoutParams = params
                if (CommonUtils.getInstance(this).isTeacherMode)
                {
                    image.setImageResource(TAB_IMAGE_ICONS_TEACHER[i])
                }
                else
                {
                    image.setImageResource(TAB_IMAGE_ICONS_STUDENT[i])
                }
                _MainTabsLayout.getTabAt(i)?.setCustomView(image)
            }
        }
        _MainTabsLayout.getTabAt(Common.PAGE_STORY)?.getCustomView()?.setSelected(true)
    }

    private fun setStatusBarColor()
    {
        val color : Int = CommonUtils.getInstance(this).getTopBarStatusBarColor()
        CommonUtils.getInstance(this).setStatusBar(resources.getColor(color))
    }

    private fun setIndicatorBarColor()
    {
        val color : Int = CommonUtils.getInstance(this).getTopBarIndicatorColor()
        _MainTabsLayout.setSelectedTabIndicatorColor(resources.getColor(color))
    }

    private val mOnPageChangeListener : ViewPager.OnPageChangeListener = object : ViewPager.OnPageChangeListener
    {
        override fun onPageScrolled(position : Int, positionOffset : Float, positionOffsetPixels : Int) {}

        override fun onPageSelected(position : Int)
        {
            Log.f("position : $position")
        }

        override fun onPageScrollStateChanged(state : Int) {}
    }

    private val mDrawerListener : DrawerLayout.DrawerListener = object : DrawerLayout.DrawerListener
    {
        override fun onDrawerSlide(drawerView : View, slideOffset : Float) {}

        override fun onDrawerOpened(drawerView : View) {}

        override fun onDrawerClosed(drawerView : View)
        {
            Log.f("")
        }

        override fun onDrawerStateChanged(newState : Int) {}
    }

    private val mMenuClickListener : View.OnClickListener = object : View.OnClickListener
    {
        override fun onClick(view : View)
        {
            when(view.id)
            {
                R.id._menuFoxschoolNewsButtonRect ->
                {
                    _MainDrawLayout.closeDrawer(_NavigationBaseLayout)
                    factoryViewModel.onClickFoxschoolNews()
                }
                R.id._menuFAQsButtonRect ->
                {
                    _MainDrawLayout.closeDrawer(_NavigationBaseLayout)
                    factoryViewModel.onClickMenuFAQ()
                }
                R.id._menu1on1AskButtonRect ->
                {
                    _MainDrawLayout.closeDrawer(_NavigationBaseLayout)
                    factoryViewModel.onClickMenu1On1Ask()
                }
                R.id._menuAboutAppButtonRect ->
                {
                    _MainDrawLayout.closeDrawer(_NavigationBaseLayout)
                    factoryViewModel.onClickMenuAppUseGuide()
                }
                R.id._menuTeacherManualButtonRect ->
                {
                    _MainDrawLayout.closeDrawer(_NavigationBaseLayout)
                    showDownloadMessage(getString(R.string.message_download_teacher_manual))
                    factoryViewModel.onClickMenuTeacherManual()
                }
                R.id._menuHomeNewspaperButtonRect ->
                {
                    _MainDrawLayout.closeDrawer(_NavigationBaseLayout)
                    showDownloadMessage(getString(R.string.message_download_home_newspaper))
                    factoryViewModel.onClickMenuHomeNewsPaper()
                }
            }
        }
    }

    private val mDialogListener : DialogListener = object : DialogListener
    {
        override fun onConfirmButtonClick(eventType : Int)
        {
            when(eventType)
            {
                MainFactoryViewModel.DIALOG_TYPE_IAC ->
                {
                    Log.f("IAC Link move")
                    if(mMainInformationResult.getInAppCompaignInformation()?.getButton1Mode().equals(Common.INAPP_CAMPAIGN_MODE_NEWS))
                    {
                        Log.f("새소식 articleID : " + mMainInformationResult.getInAppCompaignInformation()?.getArticleID())
                        factoryViewModel.onClickIACLink(java.lang.String.valueOf(mMainInformationResult.getInAppCompaignInformation()?.getArticleID()))
                    }
                    else
                    {
                        CommonUtils.getInstance(this@MainActivity).startLinkMove(mMainInformationResult.getInAppCompaignInformation()?.getButton1Link())
                        factoryViewModel.onClickIACPositiveButton()
                    }
                }
            }
        }

        override fun onChoiceButtonClick(buttonType : DialogButtonType, eventType : Int)
        {
            Log.f("eventType : $eventType, buttonType : $buttonType")
            when(eventType)
            {
                MainFactoryViewModel.DIALOG_TYPE_IAC ->
                    if(buttonType == DialogButtonType.BUTTON_1)
                    {
                        Log.f("IAC Link move")
                        if(mMainInformationResult.getInAppCompaignInformation()?.getButton1Mode().equals(Common.INAPP_CAMPAIGN_MODE_NEWS))
                        {
                            Log.f("articleID : " + mMainInformationResult.getInAppCompaignInformation()?.getArticleID())
                            factoryViewModel.onClickIACLink(java.lang.String.valueOf(mMainInformationResult.getInAppCompaignInformation()?.getArticleID()))
                        }
                        else
                        {
                            CommonUtils.getInstance(this@MainActivity).startLinkMove(mMainInformationResult.getInAppCompaignInformation()?.getButton1Link())
                            factoryViewModel.onClickIACPositiveButton()
                        }
                    }
                    else if(buttonType == DialogButtonType.BUTTON_2)
                    {
                        Log.f("IAC Cancel")
                        factoryViewModel.onClickIACCloseButton()
                    }
                MainFactoryViewModel.DIALOG_TYPE_LOGOUT ->
                    if(buttonType == DialogButtonType.BUTTON_2)
                    {
                        factoryViewModel.setLogout()
                    }
                MainFactoryViewModel.DIALOG_TYPE_APP_END ->
                    if(buttonType == DialogButtonType.BUTTON_2)
                    {
                        Log.f("============ APP END ============")
                        finish()
                    }
            }
        }
    }
}