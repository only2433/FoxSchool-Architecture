package com.littlefox.app.foxschool.main.viewmodel.factory

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.littlefox.app.foxschool.R
import com.littlefox.app.foxschool.`object`.data.crashtics.ErrorLoginData
import com.littlefox.app.foxschool.`object`.data.login.UserLoginData
import com.littlefox.app.foxschool.`object`.result.login.LoginInformationResult
import com.littlefox.app.foxschool.`object`.result.login.SchoolItemDataResult
import com.littlefox.app.foxschool.main.viewmodel.base.BaseFactoryViewModel
import com.littlefox.app.foxschool.api.enumerate.RequestCode
import com.littlefox.app.foxschool.main.viewmodel.api.LoginApiViewModel
import com.littlefox.app.foxschool.common.Common
import com.littlefox.app.foxschool.common.CommonUtils
import com.littlefox.app.foxschool.common.Feature
import com.littlefox.app.foxschool.crashtics.CrashlyticsHelper
import com.littlefox.app.foxschool.enc.SimpleCrypto
import com.littlefox.app.foxschool.enumerate.*
import com.littlefox.app.foxschool.management.IntentManagementFactory
import com.littlefox.app.foxschool.viewmodel.SingleLiveEvent
import com.littlefox.logmonitor.Log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginFactoryViewModel @Inject constructor(private val apiViewModel : LoginApiViewModel) : BaseFactoryViewModel()
{
    private val _schoolList = MutableLiveData<ArrayList<SchoolItemDataResult>>()
    val schoolList : LiveData<ArrayList<SchoolItemDataResult>> get() = _schoolList

    private val _inputEmptyMessage = MutableLiveData <String>()
    val inputEmptyMessage : LiveData<String> get() = _inputEmptyMessage

    private val _showDialogPasswordChange = MutableLiveData<PasswordGuideType>()
    val showDialogPasswordChange: LiveData<PasswordGuideType> get() = _showDialogPasswordChange

    private val _hideDialogPasswordChange = SingleLiveEvent <Void>()
    val hideDialogPasswordChange: LiveData<Void> get() = _hideDialogPasswordChange

    private val _finishActivity = SingleLiveEvent <Void>()
    val finishActivity: LiveData<Void> get() = _finishActivity

    private lateinit var mContext : Context

    private var mUserLoginData : UserLoginData? = null // 로그인 input
    private var isAutoLogin : Boolean = false // 자동로그인 체크 여부

    private var mUserInformationResult : LoginInformationResult? = null // 사용자 정보 (로그인 통신 응답)

    // 비밀번호 변경 안내 관련 변수
    private var mPassword : String  = ""
    private var mNewPassword : String = ""
    private var mConfirmPassword : String = ""

    override fun init(context : Context)
    {
        mContext = context
        setupViewModelObserver()
        requestSchoolList()
    }

    override fun setupViewModelObserver()
    {
        (mContext as AppCompatActivity).lifecycleScope.launchWhenResumed {
            apiViewModel.isLoading.collect { data ->
                data?.let {
                    if (data.first == RequestCode.CODE_SCHOOL_LIST)
                    {
                        if(data.second)
                        {
                            _isLoading.postValue(true)
                        }
                        else
                        {
                            _isLoading.postValue(false)
                        }
                    }
                }
            }
        }

        (mContext as AppCompatActivity).lifecycleScope.launchWhenResumed {
            apiViewModel.schoolListData.collect { data ->
                data?.let {
                    val items = ArrayList<SchoolItemDataResult>()
                    items.addAll(data)
                    _schoolList.value = items
                }
            }
        }

        (mContext as AppCompatActivity).lifecycleScope.launchWhenResumed {
            apiViewModel.loginData.collect { data ->
                data?.let {
                    Log.f("Login Complete")
                    mUserInformationResult = data
                    CommonUtils.getInstance(mContext).setSharedPreference(Common.PARAMS_IS_AUTO_LOGIN_DATA, if(isAutoLogin) "Y" else "N")
                    CommonUtils.getInstance(mContext).setPreferenceObject(Common.PARAMS_USER_LOGIN, mUserLoginData)
                    CommonUtils.getInstance(mContext).setPreferenceObject(Common.PARAMS_USER_API_INFORMATION, mUserInformationResult)
                    val isTeacher = mUserInformationResult!!.getUserInformation().getUserType() != Common.USER_TYPE_STUDENT
                    CommonUtils.getInstance(mContext).setSharedPreference(Common.PARAMS_IS_TEACHER_MODE, isTeacher)

                    if (mUserInformationResult!!.isNeedChangePassword())
                    {
                        // 비밀번호 변경 날짜가 90일을 넘어가는 경우 비밀번호 변경 안내 다이얼로그를 표시한다.
                        _showDialogPasswordChange.value = mUserInformationResult!!.getPasswordChangeType()
                    }
                    else
                    {
                        _finishActivity.call()
                    }
                }
            }
        }

        (mContext as AppCompatActivity).lifecycleScope.launchWhenResumed {
            apiViewModel.changePasswordData.collect { data ->
                data?.let {
                    // 비밀번호 변경 성공
                    Log.f("Password Change Complete")
                    changeUserLoginData()
                    _toast.value = mContext.getString(R.string.message_password_change_complete)
                    viewModelScope.launch(Dispatchers.Main) {
                        withContext(Dispatchers.IO){
                            delay(Common.DURATION_LONG)
                        }
                        _hideDialogPasswordChange.call()
                        _finishActivity.call()
                    }
                }
            }
        }

        (mContext as AppCompatActivity).lifecycleScope.launchWhenResumed {
            apiViewModel.changePasswordNextData.collect { data ->
                data?.let {
                    // 다음에 변경
                    _hideDialogPasswordChange.call()
                    _finishActivity.call()
                }
            }
        }

        (mContext as AppCompatActivity).lifecycleScope.launchWhenResumed {
            apiViewModel.changePasswordKeepData.collect { data ->
                data?.let {
                    // 현재 비밀번호 유지
                    _toast.value = mContext.getString(R.string.message_password_change_complete)
                    viewModelScope.launch(Dispatchers.Main) {
                        withContext(Dispatchers.IO){
                            delay(Common.DURATION_LONG)
                        }
                        _hideDialogPasswordChange.call()
                        _finishActivity.call()
                    }
                }
            }
        }

        (mContext as AppCompatActivity).lifecycleScope.launchWhenResumed {
            apiViewModel.errorReport.collect { data ->
                data?.let {
                    val result = data.first
                    val code = data.second

                    Log.f("status : ${result.status}, message : ${result.message} , code : $code")

                    if(result.isAuthenticationBroken)
                    {
                        Log.f("== isAuthenticationBroken ==")
                        (mContext as AppCompatActivity).finish()
                        IntentManagementFactory.getInstance().initScene()
                    }
                    else if (result.isNetworkError)
                    {
                        Toast.makeText(mContext, result.message, Toast.LENGTH_LONG).show()
                        (mContext as AppCompatActivity).finish()
                    }
                    else
                    {
                        if (code == RequestCode.CODE_LOGIN)
                        {
                            _errorMessage.value = result.message
                            if (Feature.IS_ENABLE_FIREBASE_CRASHLYTICS)
                            {
                                val errorData = ErrorLoginData(
                                    mUserLoginData!!.userID,
                                    result.status,
                                    result.message,
                                    java.lang.Exception()
                                )
                                CrashlyticsHelper.getInstance(mContext).sendCrashlytics(errorData)
                            }
                        }
                        else if (code == RequestCode.CODE_PASSWORD_CHANGE ||
                            code == RequestCode.CODE_PASSWORD_CHANGE_NEXT ||
                            code == RequestCode.CODE_PASSWORD_CHANGE_KEEP)
                        {
                            _toast.value = result.message
                        }
                        else
                        {
                            (mContext as AppCompatActivity).finish()
                        }
                    }
                }
            }
        }
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
    }

    /**
     * 학교 리스트 요청
     */
    private fun requestSchoolList()
    {
        Log.f("")
        apiViewModel.enqueueCommandStart(
            RequestCode.CODE_SCHOOL_LIST,
            Common.DURATION_SHORT_LONG
        )
    }

    /**
     * 비밀번호 변경하기 통신 요청
     */
    private fun requestPasswordChange()
    {
        Log.f("")
        apiViewModel.enqueueCommandStart(
            RequestCode.CODE_PASSWORD_CHANGE,
            mPassword,
            mNewPassword,
            mConfirmPassword)
    }

    /**
     * 비밀번호 변경하기 통신 요청 (다음에 변경)
     */
    private fun requestPasswordChangeNext()
    {
        Log.f("")
        apiViewModel.enqueueCommandStart(RequestCode.CODE_PASSWORD_CHANGE_NEXT)
    }

    /**
     * 비밀번호 변경하기 통신 요청 (비밀번호 유지)
     */
    private fun requestPasswordChangeKeep()
    {
        Log.f("")
        apiViewModel.enqueueCommandStart(RequestCode.CODE_PASSWORD_CHANGE_KEEP)
    }

    private fun requestLoginAsync(id : String, password : String, schoolCode : String)
    {
        if(id == "" || password == "" || schoolCode == "")
        {
            CommonUtils.getInstance(mContext).hideKeyboard()
            when
            {
                (schoolCode == "") ->
                {
                    _inputEmptyMessage.value = mContext.resources.getString(R.string.message_warning_empty_school)
                }
                (id == "") ->
                {
                    _inputEmptyMessage.value = mContext.resources.getString(R.string.message_warning_empty_id)
                }
                (password == "") ->
                {
                    _inputEmptyMessage.value = mContext.resources.getString(R.string.message_warning_empty_password)
                }
            }
            return
        }

        try
        {
            mUserLoginData = UserLoginData(id, SimpleCrypto.encode(password), schoolCode)
        } catch(e : Exception)
        {
            e.printStackTrace()
            Log.f("errorMessage : " + e.message)
        }

        apiViewModel.enqueueCommandStart(
            RequestCode.CODE_LOGIN,
            Common.DURATION_SHORT_LONG,
            id,
            password,
            schoolCode
        )
    }

    /**
     * 바뀐 비밀번호 저장
     */
    private fun changeUserLoginData()
    {
        mUserLoginData = UserLoginData(mUserLoginData!!.userID, SimpleCrypto.encode(mNewPassword), mUserLoginData!!.userSchoolCode)
        CommonUtils.getInstance(mContext).setPreferenceObject(Common.PARAMS_USER_LOGIN, mUserLoginData)
    }

    fun onCheckAutoLogin(autoLogin : Boolean)
    {
        isAutoLogin = autoLogin
        Log.f("change AutoLogin : $isAutoLogin")
    }

    fun onClickLogin(data : UserLoginData)
    {
        Log.f("Login")
        requestLoginAsync(data.userID, data.userPassword, data.userSchoolCode)
    }

    fun onClickFindID()
    {
        Log.f("findID")
        IntentManagementFactory.getInstance()
            .readyActivityMode(ActivityMode.WEBVIEW_USER_FIND_INFORMATION)
            .setData(FindType.ID)
            .setAnimationMode(AnimationMode.NORMAL_ANIMATION)
            .startActivity()
    }

    fun onClickFindPassword()
    {
        Log.f("findPassword")
        IntentManagementFactory.getInstance()
            .readyActivityMode(ActivityMode.WEBVIEW_USER_FIND_INFORMATION)
            .setData(FindType.PASSWORD)
            .setAnimationMode(AnimationMode.NORMAL_ANIMATION)
            .startActivity()
    }

    /**
     * [비밀번호 변경] 버튼 클릭 이벤트
     */
    fun onClickChangeButton(oldPassword : String, newPassword : String, confirmPassword : String)
    {
        mPassword = oldPassword
        mNewPassword = newPassword
        mConfirmPassword = confirmPassword

        requestPasswordChange()
    }

    /**
     * [다음에 변경] 버튼 클릭 이벤트
     */
    fun onClickLaterButton()
    {
        requestPasswordChangeNext()
    }

    /**
     * [현재 비밀번호로 유지하기] 버튼 클릭 이벤트
     */
    fun onClickKeepButton()
    {
        requestPasswordChangeKeep()
    }
}