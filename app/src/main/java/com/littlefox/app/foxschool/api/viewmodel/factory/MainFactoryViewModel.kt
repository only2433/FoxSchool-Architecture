package com.littlefox.app.foxschool.api.viewmodel.factory

import android.content.Context
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import com.littlefox.app.foxschool.`object`.data.bookshelf.ManagementBooksData
import com.littlefox.app.foxschool.`object`.data.iac.AwakeItemData
import com.littlefox.app.foxschool.`object`.result.login.LoginInformationResult
import com.littlefox.app.foxschool.`object`.result.main.InAppCompaignResult
import com.littlefox.app.foxschool.`object`.result.main.MainInformationResult
import com.littlefox.app.foxschool.`object`.result.story.SeriesInformationResult
import com.littlefox.app.foxschool.adapter.MainFragmentSelectionPagerAdapter
import com.littlefox.app.foxschool.api.base.BaseFactoryViewModel
import com.littlefox.app.foxschool.api.viewmodel.api.MainApiViewModel
import com.littlefox.app.foxschool.common.Common
import com.littlefox.app.foxschool.common.CommonUtils
import com.littlefox.app.foxschool.enumerate.*
import com.littlefox.app.foxschool.fragment.MainTestFragment
import com.littlefox.app.foxschool.iac.IACController
import com.littlefox.app.foxschool.management.IntentManagementFactory
import com.littlefox.app.foxschool.observer.MainObserver
import com.littlefox.app.foxschool.viewmodel.SingleLiveEvent
import com.littlefox.logmonitor.Log
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainFactoryViewModel @Inject constructor(private val apiViewModel : MainApiViewModel) : BaseFactoryViewModel()
{
    companion object
    {
        const val DIALOG_TYPE_IAC : Int                        = 10001
        const val DIALOG_TYPE_LOGOUT : Int                     = 10002
        const val DIALOG_TYPE_APP_END : Int                    = 10003
        const val DIALOG_TYPE_NOT_HAVE_CLASS : Int             = 10004
    }

    private val _settingViewPager = SingleLiveEvent<MainFragmentSelectionPagerAdapter>()
    val settingViewPager: LiveData<MainFragmentSelectionPagerAdapter> get() = _settingViewPager

    private val _settingMenuView = SingleLiveEvent<Pair<Boolean, Boolean>>()
    val settingMenuView: LiveData<Pair<Boolean, Boolean>> get() = _settingMenuView

    private val _settingUserInformation = SingleLiveEvent<LoginInformationResult?>()
    val settingUserInformation: LiveData<LoginInformationResult?> get() = _settingUserInformation

    private val _showAppEndDialog = SingleLiveEvent<Void>()
    val showAppEndDialog: LiveData<Void> get() = _showAppEndDialog

    private val _showNoClassStudentDialog = SingleLiveEvent<Void>()
    val showNoClassStudentDialog: LiveData<Void> get() = _showNoClassStudentDialog

    private val _showNoClassTeacherDialog = SingleLiveEvent<Void>()
    val showNoClassTeacherDialog: LiveData<Void> get() = _showNoClassTeacherDialog

    private val _showIACInformationDialog = SingleLiveEvent<InAppCompaignResult>()
    val showIACInformationDialog: LiveData<InAppCompaignResult> get() = _showIACInformationDialog

    // [동화 Fragment] 업데이트
    private val _updateStoryData = SingleLiveEvent<MainInformationResult>()
    val updateStoryData : LiveData<MainInformationResult> get() = _updateStoryData

    // [동요 Fragment] 업데이트
    private val _updateSongData = SingleLiveEvent<MainInformationResult>()
    val updateSongData : LiveData<MainInformationResult> get() = _updateSongData

    // [책장 Fragment] 업데이트
    private val _updateMyBooksData = SingleLiveEvent<MainInformationResult>()
    val updateMyBooksData : LiveData<MainInformationResult> get() = _updateMyBooksData

    private lateinit var mContext : Context

    private lateinit var mMainFragmentSelectionPagerAdapter : MainFragmentSelectionPagerAdapter
    private lateinit var mFragmentList : List<Fragment>

    private var mLoginInformationResult : LoginInformationResult? = null
    private lateinit var mMainInformationResult : MainInformationResult
    private var mIACController : IACController? = null
    private lateinit var mAwakeItemData : AwakeItemData
    private var mManagementBooksData : ManagementBooksData? = null

    override fun init(context : Context)
    {
        mContext = context
        setupViewModelObserver()

        MainObserver.clearAll()

        mMainInformationResult = CommonUtils.getInstance(mContext).loadMainData()
        mLoginInformationResult = CommonUtils.getInstance(mContext).getPreferenceObject(Common.PARAMS_USER_API_INFORMATION, LoginInformationResult::class.java) as LoginInformationResult?

        mMainFragmentSelectionPagerAdapter = MainFragmentSelectionPagerAdapter((mContext as AppCompatActivity).getSupportFragmentManager())
        mMainFragmentSelectionPagerAdapter.addFragment(MainTestFragment("동화"))
        mMainFragmentSelectionPagerAdapter.addFragment(MainTestFragment("동요"))
        mMainFragmentSelectionPagerAdapter.addFragment(MainTestFragment("책장/단어장"))

        mFragmentList = mMainFragmentSelectionPagerAdapter.pagerFragmentList

        _settingViewPager.value = mMainFragmentSelectionPagerAdapter
        _settingMenuView.value = Pair(mMainInformationResult.isUpdateHomework, mMainInformationResult.isUpdateNews)
        _settingUserInformation.value = mLoginInformationResult
        initIACInformation()

        setAppExecuteDate()
    }

    override fun setupViewModelObserver() { }

    override fun resume()
    {
        Log.f("")
        updateUserInformation()
        updateFragment()
    }

    override fun pause()
    {
        Log.f("")
    }

    override fun destroy()
    {
        Log.f("")
    }

    fun onBackPressed()
    {
        Log.f("Check End App")
        _showAppEndDialog.call()
    }

    private fun setAppExecuteDate()
    {
        val date : String = CommonUtils.getInstance(mContext).getTodayDateText()
        CommonUtils.getInstance(mContext).setSharedPreference(Common.PARAMS_APP_EXECUTE_DATE, date)
        Log.f("date : $date")
    }

    private fun updateUserInformation()
    {
        Log.f("update Status : " + MainObserver.isUpdateUserStatus())
        if(MainObserver.isUpdateUserStatus())
        {
            mLoginInformationResult = CommonUtils.getInstance(mContext).getPreferenceObject(Common.PARAMS_USER_API_INFORMATION, LoginInformationResult::class.java) as LoginInformationResult
            _settingUserInformation.value = mLoginInformationResult
            _settingMenuView.value = Pair(mMainInformationResult.isUpdateHomework, mMainInformationResult.isUpdateNews)
            MainObserver.clearUserStatus()
        }
    }

    private fun updateFragment()
    {
        Log.i("size : " + MainObserver.getUpdatePageList().size)
        if(MainObserver.getUpdatePageList().size > 0)
        {
            mMainInformationResult = CommonUtils.getInstance(mContext).loadMainData()
            for(page in MainObserver.getUpdatePageList())
            {
                Log.f("update page : $page")
                when(page)
                {
                    Common.PAGE_STORY -> _updateStoryData.value = mMainInformationResult
                    Common.PAGE_SONG -> _updateSongData.value = mMainInformationResult
                    Common.PAGE_MY_BOOKS -> _updateMyBooksData.value = mMainInformationResult
                }
            }
            MainObserver.clearAll()
        }
    }

    private fun initIACInformation()
    {
        if(isVisibleIACData)
        {
            Log.f("IAC VISIBLE")
            _showIACInformationDialog.value = mMainInformationResult.getInAppCompaignInformation()!!
        }
    }

    private val isVisibleIACData : Boolean
        private get()
        {
            var result = false
            try
            {
                if(mMainInformationResult.getInAppCompaignInformation() != null)
                {
                    mIACController = CommonUtils.getInstance(mContext).getPreferenceObject(
                        Common.PARAMS_IAC_CONTROLLER_INFORMATION,
                        IACController::class.java
                    ) as IACController

                    if(mIACController == null)
                    {
                        Log.f("IACController == null")
                        mIACController = IACController()
                    }

                    if(mMainInformationResult.getInAppCompaignInformation()!!.isButton2Use)
                    {
                        if(mMainInformationResult.getInAppCompaignInformation()!!.getButton2Mode()
                                .equals(Common.IAC_AWAKE_CODE_ALWAYS_VISIBLE))
                        {
                            mAwakeItemData = AwakeItemData(
                                mMainInformationResult.getInAppCompaignInformation()!!.getID(),
                                System.currentTimeMillis(),
                                Common.IAC_AWAKE_CODE_ALWAYS_VISIBLE,
                                0
                            )
                        }
                        else if(mMainInformationResult.getInAppCompaignInformation()!!.getButton2Mode()
                                .equals(Common.IAC_AWAKE_CODE_SPECIAL_DATE_VISIBLE))
                        {
                            mAwakeItemData = AwakeItemData(
                                mMainInformationResult.getInAppCompaignInformation()!!.getID(),
                                System.currentTimeMillis(),
                                Common.IAC_AWAKE_CODE_SPECIAL_DATE_VISIBLE,
                                mMainInformationResult.getInAppCompaignInformation()!!.getNotDisplayDays()
                            )
                        }
                        else if(mMainInformationResult.getInAppCompaignInformation()!!.getButton2Mode()
                                .equals(Common.IAC_AWAKE_CODE_ONCE_VISIBLE))
                        {
                            mAwakeItemData = AwakeItemData(
                                mMainInformationResult.getInAppCompaignInformation()!!.getID(),
                                System.currentTimeMillis(),
                                Common.IAC_AWAKE_CODE_ONCE_VISIBLE,
                                0
                            )
                        }
                    }
                    else
                    {
                        mAwakeItemData = AwakeItemData(
                            mMainInformationResult.getInAppCompaignInformation()!!.getID(),
                            System.currentTimeMillis(),
                            Common.IAC_AWAKE_CODE_ONCE_VISIBLE,
                            0
                        )
                    }
                }
                else
                {
                    return false
                }
            } catch(e : NullPointerException)
            {
                return result
            }
            result = mIACController?.isAwake(mAwakeItemData)!!
            return result
        }

    fun setLogout()
    {
        Log.f("============ LOGOUT COMPLETE ============")
        IntentManagementFactory.getInstance().initScene()
    }

    fun onClickIACLink(articleID : String)
    {
        IntentManagementFactory.getInstance()
            .readyActivityMode(ActivityMode.FOXSCHOOL_NEWS)
            .setData(articleID)
            .setAnimationMode(AnimationMode.NORMAL_ANIMATION)
            .startActivity()
    }

    fun onClickIACPositiveButton()
    {
        mIACController?.setPositiveButtonClick()
        CommonUtils.getInstance(mContext).setPreferenceObject(Common.PARAMS_IAC_CONTROLLER_INFORMATION, mIACController)
    }

    fun onClickIACCloseButton()
    {
        mIACController?.setCloseButtonClick()
        mIACController?.setSaveIACInformation(mAwakeItemData)
        CommonUtils.getInstance(mContext).setPreferenceObject(Common.PARAMS_IAC_CONTROLLER_INFORMATION, mIACController)
    }

    fun onClickMenuMyInformation()
    {
        Log.f("")
    }

    fun onClickMenuLearningLog()
    {
        Log.f("")
    }

    fun onClickRecordHistory()
    {
        Log.f("")
    }

    fun onClickMenuHomeworkManage()
    {
        Log.f("")
    }

    fun onClickFoxschoolNews()
    {
        Log.f("")
    }

    fun onClickMenuFAQ()
    {
        Log.f("")
    }

    fun onClickMenu1On1Ask()
    {
        Log.f("")
    }

    fun onClickMenuAppUseGuide()
    {
        Log.f("")
    }

    fun onClickMenuTeacherManual()
    {
        Log.f("")
    }

    fun onClickMenuHomeNewsPaper()
    {
        Log.f("")
    }

    fun onClickSearch()
    {
        Log.f("")
        IntentManagementFactory.getInstance()
            .readyActivityMode(ActivityMode.SEARCH)
            .setAnimationMode(AnimationMode.NORMAL_ANIMATION)
            .startActivity()
    }

    /** =============== [동화 Fragment] 이벤트 =============== */
    fun onClickStoryLevelsItem(seriesInformationResult : SeriesInformationResult, selectView : View)
    {
        Log.f("onClick StoryLevelsItem")
    }

    fun onClickStoryCategoriesItem(seriesInformationResult : SeriesInformationResult, selectView : View)
    {
        Log.f("onClick StoryCategoryItem")
    }

    /** =============== [동요 Fragment] 이벤트 =============== */
    fun onClickSongCategoriesItem(seriesInformationResult : SeriesInformationResult, selectView : View)
    {
        Log.f("onClick SongCategoriesItem")
    }

    /** =============== [책장 Fragment] 이벤트 =============== */
    fun onAddBookshelf()
    {
        Log.f("onAddBookshelf")
    }

    fun onAddVocabulary()
    {
        Log.f("onAddVocabulary")
    }

    fun onSettingBookshelf(index : Int)
    {
        Log.f("onSettingBookshelf : $index")
    }

    fun onSettingVocabulary(index : Int)
    {
        Log.f("onSettingVocabulary : $index")
    }

    fun onEnterBookshelfList(index : Int)
    {
        Log.f("onEnterBookshelfList : $index")
    }

    fun onEnterVocabularyList(index : Int)
    {
        Log.f("onEnterVocabularyList : $index")
    }
}