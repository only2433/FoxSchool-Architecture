package com.littlefox.app.foxschool.main.presenter

import android.content.Context
import android.content.Intent
import android.os.Message
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.littlefox.app.foxschool.R
import com.littlefox.app.foxschool.`object`.data.player.PlayerIntentParamsObject
import com.littlefox.app.foxschool.`object`.data.quiz.QuizIntentParamsObject
import com.littlefox.app.foxschool.`object`.data.record.RecordIntentParamsObject
import com.littlefox.app.foxschool.`object`.result.HomeworkCalenderBaseObject
import com.littlefox.app.foxschool.`object`.result.HomeworkDetailListBaseObject
import com.littlefox.app.foxschool.`object`.result.HomeworkStatusBaseObject
import com.littlefox.app.foxschool.`object`.result.TeacherClassListBaseObject
import com.littlefox.app.foxschool.`object`.result.base.BaseResult
import com.littlefox.app.foxschool.`object`.result.content.ContentsBaseResult
import com.littlefox.app.foxschool.`object`.result.homework.*
import com.littlefox.app.foxschool.`object`.result.homework.calendar.HomeworkCalendarItemData
import com.littlefox.app.foxschool.`object`.result.homework.detail.HomeworkDetailItemData
import com.littlefox.app.foxschool.adapter.TeacherHomeworkPagerAdapter
import com.littlefox.app.foxschool.common.Common
import com.littlefox.app.foxschool.common.Common.Companion.DURATION_NORMAL
import com.littlefox.app.foxschool.common.CommonUtils
import com.littlefox.app.foxschool.coroutine.*
import com.littlefox.app.foxschool.dialog.AudioPlayDialog
import com.littlefox.app.foxschool.enumerate.*
import com.littlefox.app.foxschool.main.contract.TeacherHomeworkContract
import com.littlefox.app.foxschool.management.IntentManagementFactory
import com.littlefox.app.foxschool.viewmodel.*
import com.littlefox.library.system.async.listener.AsyncListener
import com.littlefox.library.system.handler.WeakReferenceHandler
import com.littlefox.library.system.handler.callback.MessageHandlerCallback
import com.littlefox.logmonitor.Log
import kotlin.collections.ArrayList

/**
 * 선생님 숙제관리 Presenter
 * @author 김태은
 */
class TeacherHomeworkManagePresenter : TeacherHomeworkContract.Presenter
{
    companion object
    {
        private const val MESSAGE_LIST_SET_COMPLETE : Int   = 100
        private const val MESSAGE_PAGE_CHANGE : Int         = 101

        private const val REQUEST_CODE_NOTIFY : Int         = 1000
        private const val REQUEST_CODE_CHECKING : Int       = 1001
    }

    private lateinit var mContext : Context
    private lateinit var mTeacherHomeworkContractView : TeacherHomeworkContract.View
    private var mMainHandler : WeakReferenceHandler? = null

    // 숙제 페이지 Adapter 관련 데이터 (숙제관리, 숙제현황, 코멘트)
    private val mHomeworkFragmentList : ArrayList<Fragment> = ArrayList<Fragment>()
    private var mHomeworkPagerAdapter : TeacherHomeworkPagerAdapter? = null

    private lateinit var mHomeworkManagePresenterObserver : HomeworkManagePresenterObserver
    private lateinit var mHomeworkCalendarFragmentObserver : HomeworkCalendarFragmentObserver
    private lateinit var mHomeworkStatusFragmentObserver : TeacherHomeworkStatusFragmentObserver
    private lateinit var mHomeworkListFragmentObserver : HomeworkListFragmentObserver

    // 통신
    private var mTeacherClassListCoroutine : TeacherClassListCoroutine? = null
    private var mTeacherHomeworkCalenderCoroutine : TeacherHomeworkCalenderCoroutine? = null
    private var mTeacherHomeworkStatusCoroutine : TeacherHomeworkStatusCoroutine? = null
    private var mTeacherHomeworkDetailListCoroutine : TeacherHomeworkDetailListCoroutine? = null
    private var mTeacherHomeworkContentsCoroutine : TeacherHomeworkContentsCoroutine? = null

    // 통신 응답 데이터
    private var mClassListBaseResult : ArrayList<TeacherClassItemData>? = null
    private var mHomeworkCalendarBaseResult : HomeworkCalendarBaseResult? = null
    private var mHomeworkStatusBaseResult : HomeworkStatusBaseResult? = null
    private var mHomeworkDetailBaseResult : HomeworkDetailBaseResult? = null

    private var mBeforePagePosition : Int = Common.PAGE_HOMEWORK_CALENDAR // 이전 페이지 포지션
    private var mPagePosition : Int = Common.PAGE_HOMEWORK_CALENDAR // 현재 보여지고있는 페이지 포지션

    private var mCommentType : HomeworkCommentType = HomeworkCommentType.COMMENT_STUDENT        // 코멘트 화면 타입
    private var mDetailType : HomeworkDetailType = HomeworkDetailType.PAGE_TYPE_HOMEWORK_DETAIL // 리스트 상세 화면 타입

    // 통신에 입력되는 년도, 월
    private var mYear : String  = ""
    private var mMonth : String = ""

    private var mClassIndex : Int = 0 // 선택한 학급 인덱스

    private var mSelectedHomeworkPosition : Int = -1 // 숙제관리에서 선택한 숙제 포지션 (List/Comment 화면 공동 사용)
    private var mSelectedStudentPosition : Int  = -1 // 숙제현황에서 선택한 학생 포지션 (상세화면으로 이동)

    private var mAudioPlayDialog : AudioPlayDialog? = null // 학생 녹음파일 재생 다이얼로그

    private var mSelectHomeworkData : HomeworkCalendarItemData? = null // 선택한 숙제 아이템

    constructor(context : Context)
    {
        mContext = context
        mMainHandler = WeakReferenceHandler(mContext as MessageHandlerCallback)
        mTeacherHomeworkContractView = mContext as TeacherHomeworkContract.View
        mTeacherHomeworkContractView.initView()
        mTeacherHomeworkContractView.initFont()

        Log.f("")
        init()
    }

    private fun init()
    {
        Log.f("")

        // set ViewPager
        mHomeworkPagerAdapter = TeacherHomeworkPagerAdapter((mContext as AppCompatActivity).supportFragmentManager, mHomeworkFragmentList)
        mHomeworkPagerAdapter!!.setFragment()
        mTeacherHomeworkContractView.initViewPager(mHomeworkPagerAdapter!!)

        // set Observer
        mHomeworkManagePresenterObserver = ViewModelProviders.of(mContext as AppCompatActivity).get(HomeworkManagePresenterObserver::class.java)
        mHomeworkCalendarFragmentObserver = ViewModelProviders.of(mContext as AppCompatActivity).get(HomeworkCalendarFragmentObserver::class.java)
        mHomeworkStatusFragmentObserver = ViewModelProviders.of(mContext as AppCompatActivity).get(TeacherHomeworkStatusFragmentObserver::class.java)
        mHomeworkListFragmentObserver = ViewModelProviders.of(mContext as AppCompatActivity).get(HomeworkListFragmentObserver::class.java)
        setupCalendarFragmentListener()
        setupStatusFragmentListener()
        setupListFragmentListener()

        onPageChanged(Common.PAGE_HOMEWORK_CALENDAR)
    }

    override fun resume()
    {
        Log.f("")
    }

    override fun pause()
    {
        Log.f("")
    }

    override fun destroy()
    {
        Log.f("")
        mTeacherClassListCoroutine?.cancel()
        mTeacherClassListCoroutine = null
        mTeacherHomeworkCalenderCoroutine?.cancel()
        mTeacherHomeworkCalenderCoroutine = null
        mTeacherHomeworkStatusCoroutine?.cancel()
        mTeacherHomeworkStatusCoroutine = null
        mTeacherHomeworkDetailListCoroutine?.cancel()
        mTeacherHomeworkDetailListCoroutine = null
        mTeacherHomeworkContentsCoroutine?.cancel()
        mTeacherHomeworkContentsCoroutine = null
    }

    override fun activityResult(requestCode : Int, resultCode : Int, data : Intent?)
    {
        Log.f("requestCode : $requestCode, resultCode : $resultCode")
        when(requestCode)
        {
            REQUEST_CODE_NOTIFY ->
            {
                mTeacherHomeworkContractView.showLoading()
                onPageChanged(Common.PAGE_HOMEWORK_DETAIL)
            }
            REQUEST_CODE_CHECKING ->
            {
                onPageChanged(Common.PAGE_HOMEWORK_STATUS)
            }
        }
    }

    override fun sendMessageEvent(msg : Message)
    {
        when(msg.what)
        {
            MESSAGE_LIST_SET_COMPLETE -> mTeacherHomeworkContractView.hideLoading()
            MESSAGE_PAGE_CHANGE ->
            {
                when(msg.obj)
                {
                    Common.PAGE_HOMEWORK_CALENDAR -> requestClassList()             // 클래스 리스트 통신 요청
                    Common.PAGE_HOMEWORK_STATUS -> requestStatusList()              // 숙제 현황 통신 요청
                    Common.PAGE_HOMEWORK_DETAIL ->
                    {
                        if (mDetailType == HomeworkDetailType.PAGE_TYPE_STATUS_DETAIL)
                        {
                            requestStudentHomework()    // 숙제 현황 상세 보기 통신 요청
                        }
                        else if (mDetailType == HomeworkDetailType.PAGE_TYPE_HOMEWORK_DETAIL)
                        {
                            requestHomeworkDetail()   // 숙제 내용 통신 요청
                        }
                    }
                }
            }
        }
    }

    /**
     * 뒤로가기 버튼 클릭 이벤트
     */
    override fun onClickBackButton()
    {
        Log.f("")
        if (mPagePosition != Common.PAGE_HOMEWORK_CALENDAR)
        {
            // 화면에 표시되는 데이터 초기화
            if (mPagePosition == Common.PAGE_HOMEWORK_STATUS)
            {
                mHomeworkManagePresenterObserver.clearStatusList()
            }
            else if (mPagePosition == Common.PAGE_HOMEWORK_DETAIL)
            {
                mHomeworkManagePresenterObserver.clearHomeworkList(true) // 숙제 리스트 초기화
            }

            if (mPagePosition == Common.PAGE_HOMEWORK_COMMENT)
            {
                // 이전 화면에 대한 포지션을 들고있다가 세팅
                mPagePosition = mBeforePagePosition
                mTeacherHomeworkContractView.setCurrentViewPage(mPagePosition, detailType = mDetailType)
            }
            else
            {
                mPagePosition -= 1
                mTeacherHomeworkContractView.setCurrentViewPage(mPagePosition)
            }
        }
    }

    /**
     * 페이지 이동 이벤트
     */
    override fun onPageChanged(position : Int)
    {
        Log.f("")
        val msg = Message.obtain()
        msg.what = MESSAGE_PAGE_CHANGE
        msg.obj = position
        mMainHandler!!.sendMessageDelayed(msg, DURATION_NORMAL)
    }

    /**
     * ======================================================================================
     *                              숙제 - 다른 화면으로 이동
     * ======================================================================================
     */

    /**
     * 숙제내용 리스트 클릭 이벤트
     */
    private fun onClickHomeworkItem(item : HomeworkDetailItemData)
    {
        val content = ContentsBaseResult()
        content.setID(item.getContentID())
        content.setTitle(CommonUtils.getInstance(mContext).getSubStringTitleName(item.getTitle()))
        content.setThumbnailUrl(item.getThumbnailUrl())

        when(item.getHomeworkType())
        {
            HomeworkType.ANIMATION -> startPlayerActivity(content)
            HomeworkType.EBOOK -> startEBookActivity()
            HomeworkType.QUIZ -> startQuizActivity(item.getContentID())
            HomeworkType.CROSSWORD -> startCrosswordActivity(item.getContentID())
            HomeworkType.STARWORDS -> startStarWordsActivity(item.getContentID())
            HomeworkType.RECORDER -> startRecordActivity(content)
        }
    }

    /**
     * 오디오 플레이어 다이얼로그
     */
    private fun showAudioPlayDialog(item : HomeworkDetailItemData)
    {
        mAudioPlayDialog = AudioPlayDialog(mContext, item.getTitle(), item.getThumbnailUrl(), item.getMp3Path())
        mAudioPlayDialog!!.show()
    }

    /**
     * 동화/동요 플레이어로 이동
     */
    private fun startPlayerActivity(content : ContentsBaseResult)
    {
        Log.f("")

        val playerIntentParamsObject = PlayerIntentParamsObject(
            arrayListOf(content),
            mSelectHomeworkData!!.getHomeworkNumber())

        IntentManagementFactory.getInstance()
            .readyActivityMode(ActivityMode.PLAYER)
            .setData(playerIntentParamsObject)
            .setAnimationMode(AnimationMode.NORMAL_ANIMATION)
            .setRequestCode(REQUEST_CODE_NOTIFY)
            .startActivity()
    }

    /**
     * eBook 학습화면으로 이동
     * TODO 김태은 EBOOK 화면 추가 후 연결하기
     */
    private fun startEBookActivity()
    {
        Log.f("")
    }

    /**
     * 퀴즈 학습화면으로 이동
     * TODO 김태은 퀴즈 API 완성된 후 확인화기
     */
    private fun startQuizActivity(contentID : String)
    {
        Log.f("")
        var quizIntentParamsObject : QuizIntentParamsObject = QuizIntentParamsObject(
            contentID,
            mSelectHomeworkData!!.getHomeworkNumber())

        IntentManagementFactory.getInstance()
            .readyActivityMode(ActivityMode.QUIZ)
            .setData(quizIntentParamsObject)
            .setAnimationMode(AnimationMode.NORMAL_ANIMATION)
            .setRequestCode(REQUEST_CODE_NOTIFY)
            .startActivity()
    }

    /**
     * 크로스워드 학습화면으로 이동
     * TODO 김태은 크로스워드 API 완성된 후 확인화기
     */
    private fun startCrosswordActivity(contentID : String)
    {
        Log.f("")
        IntentManagementFactory.getInstance()
            .readyActivityMode(ActivityMode.WEBVIEW_GAME_CROSSWORD)
            .setData(contentID)
            .setAnimationMode(AnimationMode.NORMAL_ANIMATION)
            .startActivity()
    }

    /**
     * 스타워즈 학습화면으로 이동
     */
    private fun startStarWordsActivity(contentID : String)
    {
        Log.f("")
        IntentManagementFactory.getInstance()
            .readyActivityMode(ActivityMode.WEBVIEW_GAME_STARWORDS)
            .setData(contentID)
            .setAnimationMode(AnimationMode.NORMAL_ANIMATION)
            .startActivity()
    }

    /**
     * 녹음기 화면으로 이동
     */
    private fun startRecordActivity(content : ContentsBaseResult)
    {
        Log.f("")

        val recordIntentParamsObject = RecordIntentParamsObject(
            content,
            mSelectHomeworkData!!.getHomeworkNumber())

        IntentManagementFactory.getInstance()
            .readyActivityMode(ActivityMode.RECORD_PLAYER)
            .setData(recordIntentParamsObject)
            .setAnimationMode(AnimationMode.NORMAL_ANIMATION)
            .setRequestCode(REQUEST_CODE_NOTIFY)
            .startActivity()
    }

    /**
     * 숙제 평가 화면으로 이동
     */
    private fun startHomeworkCheckingActivity(intent : HomeworkCheckingIntentParamsObject)
    {
        Log.f("")
        IntentManagementFactory.getInstance()
            .readyActivityMode(ActivityMode.HOMEWORK_CHECKING)
            .setData(intent)
            .setAnimationMode(AnimationMode.NORMAL_ANIMATION)
            .setRequestCode(REQUEST_CODE_CHECKING)
            .startActivity()
    }

    /**
     * ======================================================================================
     *                                    통신 요청
     * ======================================================================================
     */

    /**
     * 숙제관리 클래스 리스트 요청
     */
    private fun requestClassList()
    {
        Log.f("")
        mTeacherHomeworkContractView.showLoading()
        mTeacherClassListCoroutine = TeacherClassListCoroutine(mContext)
        mTeacherClassListCoroutine!!.asyncListener = mAsyncListener
        mTeacherClassListCoroutine!!.execute()
    }

    /**
     * 숙제관리 달력 요청
     */
    private fun requestClassCalendar(showLoading : Boolean = true)
    {
        Log.f("")
        if (showLoading) mTeacherHomeworkContractView.showLoading()
        mTeacherHomeworkCalenderCoroutine = TeacherHomeworkCalenderCoroutine(mContext)
        mTeacherHomeworkCalenderCoroutine!!.setData(mClassListBaseResult!!.get(mClassIndex).getClassID().toString(), mYear, mMonth)
        mTeacherHomeworkCalenderCoroutine!!.asyncListener = mAsyncListener
        mTeacherHomeworkCalenderCoroutine!!.execute()
    }

    /**
     * 숙제관리 학생 리스트 요청
     */
    private fun requestStatusList()
    {
        Log.f("")
        mTeacherHomeworkContractView.showLoading()
        mTeacherHomeworkStatusCoroutine = TeacherHomeworkStatusCoroutine(mContext)
        mTeacherHomeworkStatusCoroutine!!.setData(
            mClassListBaseResult!!.get(mClassIndex).getClassID(),
            mHomeworkCalendarBaseResult!!.getHomeworkDataList().get(mSelectedHomeworkPosition).getHomeworkNumber()
        )
        mTeacherHomeworkStatusCoroutine!!.asyncListener = mAsyncListener
        mTeacherHomeworkStatusCoroutine!!.execute()
    }

    /**
     * 숙제관리 통신 요청 (학생)
     */
    private fun requestStudentHomework()
    {
        Log.f("")
        mTeacherHomeworkDetailListCoroutine = TeacherHomeworkDetailListCoroutine(mContext)
        mTeacherHomeworkDetailListCoroutine!!.setData(
            mClassListBaseResult!!.get(mClassIndex).getClassID(),
            mHomeworkCalendarBaseResult!!.getHomeworkDataList().get(mSelectedHomeworkPosition).getHomeworkNumber(),
            mHomeworkStatusBaseResult!!.getStudentStatusItemList()!!.get(mSelectedStudentPosition).getUserID()
        )
        mTeacherHomeworkDetailListCoroutine!!.asyncListener = mAsyncListener
        mTeacherHomeworkDetailListCoroutine!!.execute()
    }

    /**
     * 숙제내용 통신 요청
     */
    private fun requestHomeworkDetail()
    {
        Log.f("")

        // 숙제 아이템 정보 요청
        mSelectHomeworkData = mHomeworkCalendarBaseResult!!.getHomeworkDataList()[mSelectedHomeworkPosition]
        mTeacherHomeworkContentsCoroutine = TeacherHomeworkContentsCoroutine(mContext)
        mTeacherHomeworkContentsCoroutine!!.setData(
            mClassListBaseResult!!.get(mClassIndex).getClassID(),
            mSelectHomeworkData!!.getHomeworkNumber()
        )
        mTeacherHomeworkContentsCoroutine!!.asyncListener = mAsyncListener
        mTeacherHomeworkContentsCoroutine!!.execute()
    }

    /**
     * ======================================================================================
     *                                    Listeners
     * ======================================================================================
     */
    private fun setupCalendarFragmentListener()
    {
        // 이전 화살표 클릭 이벤트
        mHomeworkCalendarFragmentObserver.onClickCalendarBefore.observe(mContext as AppCompatActivity, {
            mYear = mHomeworkCalendarBaseResult!!.getPrevYear()
            mMonth = mHomeworkCalendarBaseResult!!.getPrevMonth()
            requestClassCalendar()
        })

        // 다음 화살표 클릭 이벤트
        mHomeworkCalendarFragmentObserver.onClickCalendarAfter.observe(mContext as AppCompatActivity, {
            mYear = mHomeworkCalendarBaseResult!!.getNextYear()
            mMonth = mHomeworkCalendarBaseResult!!.getNextMonth()
            requestClassCalendar()
        })

        // 달력 아이템 클릭 이벤트
        mHomeworkCalendarFragmentObserver.onClickCalendarItem.observe(mContext as AppCompatActivity, { homeworkPosition ->
            mSelectedHomeworkPosition = homeworkPosition // 선택한 숙제 인덱스 저장

            // 숙제 현황 페이지로 이동
            mPagePosition = Common.PAGE_HOMEWORK_STATUS
            mTeacherHomeworkContractView.setCurrentViewPage(mPagePosition)
        })

        // 달력 세팅 완료 (Activity 로딩 다이얼로그 닫기)
        mHomeworkCalendarFragmentObserver.onCompletedCalendarSet.observe(mContext as AppCompatActivity, {
            mMainHandler!!.sendEmptyMessage(MESSAGE_LIST_SET_COMPLETE)
        })

        // 반 선택
        mHomeworkCalendarFragmentObserver.onClickClassPicker.observe(mContext as AppCompatActivity, { index ->
            mClassIndex = index
            requestClassCalendar()
        })
    }

    private fun setupStatusFragmentListener()
    {
        // [숙제 현황 상세 보기] 클릭 이벤트
        mHomeworkStatusFragmentObserver.onClickShowDetailButton.observe(mContext as AppCompatActivity, { index ->
            mSelectedStudentPosition = index

            // 숙제 현황 상세 페이지로 이동
            mPagePosition = Common.PAGE_HOMEWORK_DETAIL
            mDetailType = HomeworkDetailType.PAGE_TYPE_STATUS_DETAIL
            mTeacherHomeworkContractView.setCurrentViewPage(mPagePosition, detailType = mDetailType)
        })

        // [숙제 내용] 클릭 이벤트
        mHomeworkStatusFragmentObserver.onClickHomeworkContents.observe(mContext as AppCompatActivity, {
            // 숙제 내용 페이지로 이동
            mPagePosition = Common.PAGE_HOMEWORK_DETAIL
            mDetailType = HomeworkDetailType.PAGE_TYPE_HOMEWORK_DETAIL
            mTeacherHomeworkContractView.setCurrentViewPage(mPagePosition, detailType = mDetailType)
        })

        // [숙제 검사] 클릭 이벤트 (1건)
        mHomeworkStatusFragmentObserver.onClickHomeworkChecking.observe(mContext as AppCompatActivity, { index ->
            val intent = HomeworkCheckingIntentParamsObject(
                mHomeworkCalendarBaseResult!!.getHomeworkDataList().get(mSelectedHomeworkPosition).getHomeworkNumber(),
                mClassListBaseResult!!.get(mClassIndex).getClassID(),
                mHomeworkStatusBaseResult!!.getStudentStatusItemList()!!.get(index)
            )

            startHomeworkCheckingActivity(intent)
        })

        // [일괄 숙제 검사] 클릭 이벤트
        mHomeworkStatusFragmentObserver.onClickHomeworkBundleChecking.observe(mContext as AppCompatActivity, { data ->
            if (data.isEmpty())
            {
                mTeacherHomeworkContractView.showErrorMessage(mContext.getString(R.string.message_warning_choose_student))
                mHomeworkManagePresenterObserver.setClickEnable()
            }
            else
            {
                val intent = HomeworkCheckingIntentParamsObject(
                    mHomeworkCalendarBaseResult!!.getHomeworkDataList().get(mSelectedHomeworkPosition).getHomeworkNumber(),
                    mClassListBaseResult!!.get(mClassIndex).getClassID(),
                    data
                )

                startHomeworkCheckingActivity(intent)
            }
        })
    }

    private fun setupListFragmentListener()
    {
        // 학습자 한마디 클릭 이벤트
        mHomeworkListFragmentObserver.onClickStudentCommentButton.observe(mContext as AppCompatActivity, {
            mBeforePagePosition = mPagePosition
            mPagePosition = Common.PAGE_HOMEWORK_COMMENT
            mCommentType = HomeworkCommentType.COMMENT_STUDENT
            mTeacherHomeworkContractView.setCurrentViewPage(mPagePosition, commentType = mCommentType)
            mHomeworkManagePresenterObserver.setCommentData(mHomeworkDetailBaseResult!!.getStudentComment())
            mHomeworkManagePresenterObserver.setPageType(mCommentType, true)
        })

        // 선생님 한마디 클릭 이벤트
        mHomeworkListFragmentObserver.onClickTeacherCommentButton.observe(mContext as AppCompatActivity, {
            mBeforePagePosition = mPagePosition
            mPagePosition = Common.PAGE_HOMEWORK_COMMENT
            mCommentType = HomeworkCommentType.COMMENT_TEACHER
            mTeacherHomeworkContractView.setCurrentViewPage(mPagePosition, commentType = mCommentType)
            mHomeworkManagePresenterObserver.setCommentData(mHomeworkDetailBaseResult!!.getTeacherComment())
            mHomeworkManagePresenterObserver.setPageType(mCommentType, true)
        })

        // 숙제목록 클릭 이벤트 (컨텐츠 이동)
        mHomeworkListFragmentObserver.onClickHomeworkItem.observe(mContext as AppCompatActivity, { item ->
            // 숙제내용 화면 인 경우에만 학습 가능
            if (mDetailType == HomeworkDetailType.PAGE_TYPE_HOMEWORK_DETAIL)
            {
                onClickHomeworkItem(item)
            }
            else if (mDetailType == HomeworkDetailType.PAGE_TYPE_STATUS_DETAIL &&
                    item.getHomeworkType() == HomeworkType.RECORDER &&
                    item.isComplete && item.getExpired() > 0)
            {
                // 숙제 현황 상세 보기 화면
                // 녹음 데이터가 있는 경우
                showAudioPlayDialog(item)
            }
        })
    }

    /**
     * 통신 응답 Listener
     */
    private val mAsyncListener : AsyncListener = object : AsyncListener
    {
        override fun onRunningStart(code : String?) { }

        override fun onRunningEnd(code : String?, mObject : Any?)
        {
            val result : BaseResult? = mObject as BaseResult?

            if (result == null) return

            Log.f("code : $code, status : ${result.getStatus()}")

            if (result.getStatus() == BaseResult.SUCCESS_CODE_OK)
            {
                // 통신 성공
                if (code == Common.COROUTINE_CODE_TEACHER_CLASS_LIST)
                {
                    // 학급 리스트
                    mClassListBaseResult = (result as TeacherClassListBaseObject).getClassList()
                    mHomeworkManagePresenterObserver.setClassData(mClassListBaseResult!!)
                    requestClassCalendar(false)
                }
                else if (code == Common.COROUTINE_CODE_TEACHER_HOMEWORK_CALENDER)
                {
                    // 숙제관리 (달력)
                    mHomeworkCalendarBaseResult = (result as HomeworkCalenderBaseObject).getData()
                    mHomeworkManagePresenterObserver.setCalendarData(mHomeworkCalendarBaseResult!!)
                }
                else if (code == Common.COROUTINE_CODE_TEACHER_HOMEWORK_STATUS)
                {
                    // 숙제관리 학생리스트
                    mTeacherHomeworkContractView.hideLoading()
                    mHomeworkStatusBaseResult = (result as HomeworkStatusBaseObject).getData()
                    mHomeworkManagePresenterObserver.setClassName(mClassListBaseResult!![mClassIndex].getClassName())
                    mHomeworkManagePresenterObserver.setStatusListData(mHomeworkStatusBaseResult!!)
                }
                else if (code == Common.COROUTINE_CODE_TEACHER_HOMEWORK_DETAIL_LIST)
                {
                    // 숙제현황 상세리스트
                    mTeacherHomeworkContractView.hideLoading()
                    mHomeworkDetailBaseResult = (result as HomeworkDetailListBaseObject).getData()
                    val name = mHomeworkStatusBaseResult!!.getStudentStatusItemList()!![mSelectedStudentPosition].getUserName()
                    mHomeworkDetailBaseResult!!.setFragmentTitle("$name 학생")
                    mHomeworkDetailBaseResult!!.setFragmentType(mDetailType)
                    mHomeworkManagePresenterObserver.updateHomeworkListData(mHomeworkDetailBaseResult!!)
                }
                else if (code == Common.COROUTINE_CODE_TEACHER_HOMEWORK_CONTENTS)
                {
                    // 숙제내용 리스트
                    mTeacherHomeworkContractView.hideLoading()
                    mHomeworkDetailBaseResult = (result as HomeworkDetailListBaseObject).getData()
                    mHomeworkDetailBaseResult!!.setFragmentTitle(mClassListBaseResult!!.get(mClassIndex).getClassName())
                    mHomeworkDetailBaseResult!!.setFragmentType(mDetailType)
                    mHomeworkManagePresenterObserver.updateHomeworkListData(mHomeworkDetailBaseResult!!)
                }
            }
            else
            {
                mTeacherHomeworkContractView.hideLoading()
                // 통신 실패
                if (result.isAuthenticationBroken)
                {
                    Log.f("== isAuthenticationBroken ==")
                    (mContext as AppCompatActivity).finish()
                    IntentManagementFactory.getInstance().initScene()
                }
                else
                {
                    if (code == Common.COROUTINE_CODE_TEACHER_CLASS_LIST ||
                        code == Common.COROUTINE_CODE_TEACHER_HOMEWORK_CALENDER)
                    {
                        Toast.makeText(mContext, result.getMessage(), Toast.LENGTH_LONG).show()
                        (mContext as AppCompatActivity).onBackPressed()
                    }
                    else if (code == Common.COROUTINE_CODE_TEACHER_HOMEWORK_STATUS ||
                             code == Common.COROUTINE_CODE_TEACHER_HOMEWORK_DETAIL_LIST ||
                             code == Common.COROUTINE_CODE_TEACHER_HOMEWORK_CONTENTS)
                    {
                        mTeacherHomeworkContractView.showErrorMessage(result.getMessage())
                        (mContext as AppCompatActivity).onBackPressed()
                    }
                }
            }
        }

        override fun onRunningCanceled(code : String?) { }

        override fun onRunningProgress(code : String?, progress : Int?) { }

        override fun onRunningAdvanceInformation(code : String?, `object` : Any?) { }

        override fun onErrorListener(code : String?, message : String?) { }
    }


}