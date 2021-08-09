package com.littlefox.app.foxschool.fragment

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import butterknife.*
import com.littlefox.app.foxschool.R
import com.littlefox.app.foxschool.`object`.result.login.LoginInformationResult
import com.littlefox.app.foxschool.common.Common
import com.littlefox.app.foxschool.common.CommonUtils
import com.littlefox.app.foxschool.common.Font
import com.littlefox.app.foxschool.enumerate.InputDataType
import com.littlefox.app.foxschool.viewmodel.MyInfoChangeFragmentDataObserver
import com.littlefox.app.foxschool.viewmodel.MyInfoPresenterDataObserver
import com.littlefox.logmonitor.Log
import com.ssomai.android.scalablelayout.ScalableLayout

/**
 * 나의 정보 수정 화면 + 비밀번호 변경 화면
 * @author 김태은
 */
class MyInfoChangeFragment : Fragment()
{
    @BindView(R.id._mainBaseLayout)
    lateinit var _MainBaseLayout : RelativeLayout

    @BindView(R.id._mainBackgroundView)
    lateinit var _MainBackgroundView : ScalableLayout

    /** 나의 정보 수정 화면 */
    @BindView(R.id._myInfoChangeLayout)
    lateinit var _MyInfoChangeLayout : ScalableLayout

    @BindView(R.id._idTitleText)
    lateinit var _IdTitleText : TextView

    @BindView(R.id._idText)
    lateinit var _IdText : TextView

    @BindView(R.id._nameTitleText)
    lateinit var _NameTitleText : TextView

    @BindView(R.id._inputNameEditText)
    lateinit var _InputNameEditText : EditText

    @BindView(R.id._inputNameDeleteButton)
    lateinit var _InputNameDeleteButton : ImageView

    @BindView(R.id._inputNameBg)
    lateinit var _InputNameBg : ImageView

    @BindView(R.id._inputEmailBg)
    lateinit var _InputEmailBg : ImageView

    @BindView(R.id._emailTitleText)
    lateinit var _EmailTitleText : TextView

    @BindView(R.id._inputEmailEditText)
    lateinit var _InputEmailEditText : EditText

    @BindView(R.id._inputEmailDeleteButton)
    lateinit var _InputEmailDeleteButton : ImageView

    @BindView(R.id._emailAtText)
    lateinit var _EmailAtText : TextView

    @BindView(R.id._emailEndTitleText)
    lateinit var _EmailEndTitleText : TextView

    @BindView(R.id._inputEmailEndBg)
    lateinit var _InputEmailEndBg : ImageView

    @BindView(R.id._inputEmailEndEditText)
    lateinit var _InputEmailEndEditText : EditText

    @BindView(R.id._inputEmailEndSelectButton)
    lateinit var _InputEmailEndSelectButton : ImageView

    @BindView(R.id._inputEmailEndSelectButtonRect)
    lateinit var _InputEmailEndSelectButtonRect : ImageView

    @BindView(R.id._phoneTitleText)
    lateinit var _PhoneTitleText : TextView

    @BindView(R.id._phoneOptionTitleText)
    lateinit var _PhoneOptionTitleText : TextView

    @BindView(R.id._inputPhoneEditText)
    lateinit var _InputPhoneEditText : EditText

    @BindView(R.id._inputPhoneDeleteButton)
    lateinit var _InputPhoneDeleteButton : ImageView

    @BindView(R.id._inputPhoneBg)
    lateinit var _InputPhoneBg : ImageView

    @BindView(R.id._saveInfoButton)
    lateinit var _SaveInfoButton : TextView

    /** 비밀번호 변경 화면 */
    @BindView(R.id._passwordChangeLayout)
    lateinit var _PasswordChangeLayout : ScalableLayout

    @BindView(R.id._inputPasswordEditBackground)
    lateinit var _InputPasswordEditBackground : ImageView

    @BindView(R.id._inputPasswordEditText)
    lateinit var _InputPasswordEditText : EditText

    @BindView(R.id._inputNewPasswordEditBackground)
    lateinit var _InputNewPasswordEditBackground : ImageView

    @BindView(R.id._inputNewPasswordEditText)
    lateinit var _InputNewPasswordEditText : EditText

    @BindView(R.id._inputNewPasswordConfirmEditBackground)
    lateinit var _InputNewPasswordConfirmEditBackground : ImageView

    @BindView(R.id._inputNewPasswordConfirmEditText)
    lateinit var _InputNewPasswordConfirmEditText : EditText

    @BindView(R.id._savePasswordButton)
    lateinit var _SavePasswordButton : TextView

    companion object
    {
        private const val MESSAGE_DATA_CHECK_ERROR : Int = 100 // 입력값 유효성 오류
    }

    private lateinit var mContext : Context
    private lateinit var mUnbinder : Unbinder
    private lateinit var mMyInfoChangeFragmentDataObserver : MyInfoChangeFragmentDataObserver   // Fragment -> Presenter
    private lateinit var mMyInfoPresenterDataObserver : MyInfoPresenterDataObserver             // Presenter -> Fragment

    // 이메일 주소 선택 다이얼로그 데이터
    private var mEmailSpinnerList : Array<String>? = null
    private var mEmailSelectIndex : Int = -1

    var mErrorViewHandler : Handler = object : Handler()
    {
        override fun handleMessage(msg : Message)
        {
            when(msg.what)
            {
                MESSAGE_DATA_CHECK_ERROR ->
                {
                    CommonUtils.getInstance(mContext).hideKeyboard()
                }
            }
        }
    }

    /** ========== LifeCycle ========== */
    override fun onAttach(context : Context)
    {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater : LayoutInflater, container : ViewGroup?, savedInstanceState : Bundle?) : View?
    {
        Log.f("")
        var view : View? = null
        if(CommonUtils.getInstance(mContext).checkTablet)
        {
            view = inflater.inflate(R.layout.fragment_my_info_change_tablet, container, false)
        } else
        {
            view = inflater.inflate(R.layout.fragment_my_info_change, container, false)
        }
        mUnbinder = ButterKnife.bind(this, view)
        return view
    }

    override fun onViewCreated(view : View, savedInstanceState : Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        Log.f("")
        initView()
        initFont()
        setupObserverViewModel()
        resetEditTextBackground()
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

    override fun onDestroy()
    {
        super.onDestroy()
    }
    /** ========== LifeCycle ========== */

    /** ========== Init ========== */
    private fun initView()
    {
        // [나의 정보 수정]
        _InputNameEditText.onFocusChangeListener = mEditInfoFocusListener
        _InputEmailEditText.onFocusChangeListener = mEditInfoFocusListener
        _InputEmailEndEditText.onFocusChangeListener = mEditInfoFocusListener
        _InputPhoneEditText.onFocusChangeListener = mEditInfoFocusListener
        _InputEmailEditText.setOnEditorActionListener(mEditKeyActionListener)
        _InputEmailEndEditText.setOnEditorActionListener(mEditKeyActionListener)
        _InputPhoneEditText.setOnEditorActionListener(mEditKeyActionListener)
        mEmailSpinnerList = mContext.resources.getStringArray(R.array.text_list_email)

        // [비밀번호 변경]
        _InputPasswordEditText.onFocusChangeListener = mEditPasswordFocusListener
        _InputNewPasswordEditText.onFocusChangeListener = mEditPasswordFocusListener
        _InputNewPasswordConfirmEditText.onFocusChangeListener = mEditPasswordFocusListener
        _InputNewPasswordConfirmEditText.setOnEditorActionListener(mEditKeyActionListener)
    }

    private fun initFont()
    {
        // [나의 정보 수정]
        _IdTitleText.typeface = Font.getInstance(mContext).getRobotoRegular()
        _IdText.typeface = Font.getInstance(mContext).getRobotoMedium()
        _NameTitleText.typeface = Font.getInstance(mContext).getRobotoRegular()
        _EmailTitleText.typeface = Font.getInstance(mContext).getRobotoRegular()
        _EmailAtText.typeface = Font.getInstance(mContext).getRobotoRegular()
        _EmailEndTitleText.typeface = Font.getInstance(mContext).getRobotoRegular()
        _InputNameEditText.typeface = Font.getInstance(mContext).getRobotoMedium()
        _InputEmailEditText.typeface = Font.getInstance(mContext).getRobotoMedium()
        _InputEmailEndEditText.typeface = Font.getInstance(mContext).getRobotoRegular()
        _PhoneTitleText.typeface = Font.getInstance(mContext).getRobotoRegular()
        _PhoneOptionTitleText.typeface = Font.getInstance(mContext).getRobotoRegular()
        _InputPhoneEditText.typeface = Font.getInstance(mContext).getRobotoMedium()
        _SaveInfoButton.typeface = Font.getInstance(mContext).getRobotoMedium()

        // [비밀번호 변경]
        _InputPasswordEditText.typeface = Font.getInstance(mContext).getRobotoRegular()
        _InputNewPasswordEditText.typeface = Font.getInstance(mContext).getRobotoRegular()
        _InputNewPasswordConfirmEditText.typeface = Font.getInstance(mContext).getRobotoRegular()
        _SavePasswordButton.typeface = Font.getInstance(mContext).getRobotoMedium()
    }
    /** ========== Init ========== */

    private fun setupObserverViewModel()
    {
        mMyInfoChangeFragmentDataObserver = ViewModelProviders.of(mContext as AppCompatActivity).get(MyInfoChangeFragmentDataObserver::class.java)
        mMyInfoPresenterDataObserver = ViewModelProviders.of(mContext as AppCompatActivity).get(MyInfoPresenterDataObserver::class.java)

        // 페이지 이동 이벤트
        mMyInfoPresenterDataObserver.viewPagerChange.observe(mContext as AppCompatActivity, { position ->
            setViewData(position)
        })

        // 사용자 정보 화면에 세팅
        mMyInfoPresenterDataObserver.setMyInfoChangeFragment.observe(mContext as AppCompatActivity, { userInfo ->
            setUserInformation(userInfo)
        })

        // 입력필드 오류 표시
        mMyInfoPresenterDataObserver.setInputErrorView.observe(mContext as AppCompatActivity, { type ->
            showInputError(type)
        })

        // 저장 버튼 활성 여부 [나의 정보 수정]
        mMyInfoPresenterDataObserver.setSaveInfoButtonEnable.observe(mContext as AppCompatActivity, { isEnable ->
            setSaveInfoButtonEnable(isEnable)
        })

        // 저장 버튼 활성 여부 [비밀번호 변경]
        mMyInfoPresenterDataObserver.setSavePasswordButtonEnable.observe(mContext as AppCompatActivity, { isEnable ->
            setSavePasswordButtonEnable(isEnable)
        })

        // 화면 초기화
        mMyInfoPresenterDataObserver.clearMyInfoChangeFragment.observe(mContext as AppCompatActivity, { isEnable ->
            setClearData()
        })
    }

    /**
     * 화면 초기화
     */
    private fun setClearData()
    {
        CommonUtils.getInstance(mContext).hideKeyboard()
        _InputPasswordEditText.setText("")
        _InputNewPasswordEditText.setText("")
        _InputNewPasswordConfirmEditText.setText("")
        resetEditTextBackground()
        setSaveInfoButtonEnable(false)
    }

    /**
     * 화면 변경 ( 나의 정보 수정 / 비밀번호 변경 )
     */
    private fun setViewData(position : Int)
    {
        when (position)
        {
            Common.PAGE_MY_INFO_CHANGE ->
            {
                _MyInfoChangeLayout.visibility = View.VISIBLE
                _PasswordChangeLayout.visibility = View.GONE
            }
            Common.PAGE_PASSWORD_CHANGE ->
            {
                _MyInfoChangeLayout.visibility = View.GONE
                _PasswordChangeLayout.visibility = View.VISIBLE
            }
        }
    }

    /**
     * 화면에 사용자 정보 표시
     */
    private fun setUserInformation(userInformation : LoginInformationResult)
    {
        _IdText.text = userInformation.getUserInformation().getLoginID()
        _InputNameEditText.setText(userInformation.getUserInformation().getName())

        // 이메일 데이터 있는 경우
        val emailFull = userInformation.getUserInformation().getEmail()
        if (emailFull.indexOf("@") != -1)
        {
            // @를 기준으로 앞 뒤 쪼개서 EditText 세팅
            val emailFront = emailFull.substring(0, emailFull.indexOf("@"))
            val emailEnd = emailFull.substring(emailFull.indexOf("@") + 1, emailFull.length)
            if (emailFront != "")
            {
                _EmailTitleText.visibility = View.GONE
                _EmailEndTitleText.visibility = View.GONE
            }

            _InputEmailEditText.setText(emailFront)
            _InputEmailEndEditText.setText(emailEnd)

            for (i in mEmailSpinnerList!!.indices)
            {
                if (emailEnd == mEmailSpinnerList!![i])
                {
                    // 기존에 사용자가 입력했던 주소가 이메일 목록에 있는 경우라면 선택값 체크하고 EditText 활성화
                    mEmailSelectIndex = i
                    _InputEmailEndEditText.isEnabled = false
                    return
                }
                else
                {
                    // 이메일 목록에 없는 경우 라면 (직접 입력)으로 체크하고 EditText 비활성화
                    mEmailSelectIndex = mEmailSpinnerList!!.size - 1
                    _InputEmailEndEditText.isEnabled = true
                }
            }
        }
        else
        {
            // 이메일 영역 초기화
            _EmailEndTitleText.visibility = View.VISIBLE
            _EmailEndTitleText.setText(R.string.text_spinner_hint)
            _InputEmailEditText.setText("")
            _InputEmailEndEditText.setText("")
            _InputEmailEndEditText.isEnabled = false
            mEmailSelectIndex = -1
        }

        // 휴대폰 번호 있는 경우 하이픈 제거
        var phone = userInformation.getUserInformation().getPhone()
        if (phone.indexOf("-") != -1)
        {
            phone = phone.replace("-", "")
            _InputPhoneEditText.setText(phone)
        }
        else
        {
            // 초기화
            _InputPhoneEditText.setText("")
        }
    }

    /**
     * 입력필드 배경화면 초기화
     */
    private fun resetEditTextBackground()
    {
        // [나의 정보 수정]
        _InputNameBg.setBackgroundResource(R.drawable.text_box)
        _InputEmailBg.setBackgroundResource(R.drawable.text_box)
        _InputEmailEndBg.setBackgroundResource(R.drawable.text_box)
        _InputPhoneBg.setBackgroundResource(R.drawable.text_box)

        // [비밀번호 변경]
        _InputPasswordEditBackground.setBackgroundResource(R.drawable.text_box)
        _InputNewPasswordEditBackground.setBackgroundResource(R.drawable.text_box)
        _InputNewPasswordConfirmEditBackground.setBackgroundResource(R.drawable.text_box)
    }

    /**
     * 이메일 앞 뒤 조합한 결과 추출
     */
    private fun getEmailEditTextResult() : String
    {
        val emailFront = _InputEmailEditText.text.toString().trim()
        val emailEnd = _InputEmailEndEditText.text.toString().trim()

        // 이메일 앞, 뒤가 다 공백이면 빈값을 추출해준다.
        if (emailFront == "" && emailEnd == "") return ""
        else return "$emailFront@$emailEnd"
    }

    /**
     * 이메일 주소 선택 다이얼로그 표시
     */
    private fun showEmailSelectDialog()
    {
        Log.f("")
        val builder = AlertDialog.Builder(mContext)
        builder.setSingleChoiceItems(mEmailSpinnerList, mEmailSelectIndex, DialogInterface.OnClickListener { dialog, index ->
            dialog.dismiss()
            mEmailSelectIndex = index

            if (index != mEmailSpinnerList!!.size - 1)
            {
                // 사용자가 제공된 기본 주소 선택
                _EmailEndTitleText.visibility = View.GONE
                _InputEmailEndEditText.isEnabled = false
                _InputEmailEndEditText.setText(mEmailSpinnerList!![mEmailSelectIndex])
            }
            else
            {
                // [직접 입력] 선택
                _EmailEndTitleText.text = mEmailSpinnerList!![mEmailSelectIndex]
                _EmailEndTitleText.visibility = View.VISIBLE
                _InputEmailEndEditText.isEnabled = true
                _InputEmailEndEditText.setText("")
            }
        })

        val dialog : AlertDialog = builder.show()
        dialog.show()
    }

    /**
     * 입력값 유효성 체크 (전체)
     */
    private fun checkInputAvailable(position : Int)
    {
        if (position == Common.PAGE_MY_INFO_CHANGE)
        {
            mMyInfoChangeFragmentDataObserver.checkInfoInputDataAvailable(
                name = _InputNameEditText.text.toString().trim(),
                email = getEmailEditTextResult(),
                phone = _InputPhoneEditText.text.toString().trim())
        }
        else if (position == Common.PAGE_PASSWORD_CHANGE)
        {
            mMyInfoChangeFragmentDataObserver.checkPasswordInputDataAvailable(
                oldPassword = _InputPasswordEditText.text.toString().trim(),
                newPassword = _InputNewPasswordEditText.text.toString().trim(),
                confirmPassword = _InputNewPasswordConfirmEditText.text.toString().trim()
            )
        }
    }

    /**
     * 입력값 에러 표시
     */
    private fun showInputError(type : InputDataType)
    {
        when(type)
        {
            InputDataType.NAME -> _InputNameBg.setBackgroundResource(R.drawable.box_list_error)
            InputDataType.EMAIL ->
            {
                _InputEmailBg.setBackgroundResource(R.drawable.box_list_error)
                _InputEmailEndBg.setBackgroundResource(R.drawable.box_list_error)
            }
            InputDataType.PHONE -> _InputPhoneBg.setBackgroundResource(R.drawable.box_list_error)
            InputDataType.PASSWORD -> _InputPasswordEditBackground.setBackgroundResource(R.drawable.box_list_error)
            InputDataType.NEW_PASSWORD -> _InputNewPasswordEditBackground.setBackgroundResource(R.drawable.box_list_error)
            InputDataType.NEW_PASSWORD_CONFIRM -> _InputNewPasswordConfirmEditBackground.setBackgroundResource(R.drawable.box_list_error)
        }

        val msg = Message.obtain()
        msg.what = MESSAGE_DATA_CHECK_ERROR
        msg.obj = ""
        mErrorViewHandler.sendMessageDelayed(msg, Common.DURATION_NORMAL)
    }

    /**
     * 회원정보 저장버튼 활성/비활성
     */
    private fun setSaveInfoButtonEnable(isEnable : Boolean)
    {
        _SaveInfoButton.isEnabled = isEnable
        if (isEnable)
        {
            _SaveInfoButton.setBackgroundResource(R.drawable.round_box_light_blue_84)
        }
        else
        {
            _SaveInfoButton.setBackgroundResource(R.drawable.round_box_gray_84)
        }
    }

    /**
     * 비밀번호 변경 저장버튼 활성/비활성
     */
    private fun setSavePasswordButtonEnable(isEnable : Boolean)
    {
        _SavePasswordButton.isEnabled = isEnable
        if (isEnable)
        {
            _SavePasswordButton.setBackgroundResource(R.drawable.round_box_light_blue_84)
        }
        else
        {
            _SavePasswordButton.setBackgroundResource(R.drawable.round_box_gray_84)
        }
    }

    @Optional
    @OnClick(
        R.id._mainBaseLayout, R.id._mainBackgroundView, R.id._saveInfoButton, R.id._savePasswordButton,
        R.id._inputNameDeleteButton, R.id._inputEmailDeleteButton, R.id._inputPhoneDeleteButton,
        R.id._inputNameEditText, R.id._inputEmailEditText, R.id._inputEmailEndEditText, R.id._inputPhoneEditText,
        R.id._inputEmailEndSelectButton, R.id._inputEmailEndSelectButtonRect, R.id._emailTitleText, R.id._emailEndTitleText,
        R.id._inputNameBg, R.id._inputEmailBg, R.id._inputEmailEndBg, R.id._inputPhoneBg,
        R.id._inputPasswordEditText, R.id._inputNewPasswordEditText, R.id._inputNewPasswordConfirmEditText,
        R.id._inputPasswordEditBackground, R.id._inputNewPasswordEditBackground, R.id._inputNewPasswordConfirmEditBackground,
    )
    fun onClickView(view : View)
    {
        // 배경화면 탭하면 키보드 닫기
        if (view.id == R.id._mainBaseLayout || view.id == R.id._mainBackgroundView)
        {
            CommonUtils.getInstance(mContext).hideKeyboard()
            _InputNameEditText.clearFocus()
            _InputEmailEditText.clearFocus()
            _InputEmailEndEditText.clearFocus()
            _InputPhoneEditText.clearFocus()

            _InputPasswordEditText.clearFocus()
            _InputNewPasswordEditText.clearFocus()
            _InputNewPasswordConfirmEditText.clearFocus()
        }

        when(view.id)
        {
            // 타이틀 클릭으로 포커싱 & 키보드 표시
            R.id._inputNameBg -> CommonUtils.getInstance(mContext).showKeyboard(_InputNameEditText)
            R.id._inputEmailBg, R.id._emailTitleText ->
            {
                _EmailTitleText.visibility = View.GONE
                CommonUtils.getInstance(mContext).showKeyboard(_InputEmailEditText)
            }
            R.id._inputPhoneBg -> CommonUtils.getInstance(mContext).showKeyboard(_InputPhoneEditText)

            // Email 선택 다이얼로그 표시
            R.id._inputEmailEndSelectButton, R.id._inputEmailEndSelectButtonRect -> showEmailSelectDialog()

            // 입력필드 X버튼 (입력필드 초기화)
            R.id._inputNameDeleteButton ->
            {
                _InputNameEditText.text.clear()
                if (!_InputNameEditText.hasFocus()) _InputNameBg.setBackgroundResource(R.drawable.text_box)
                setSaveInfoButtonEnable(false)
            }
            R.id._inputEmailDeleteButton ->
            {
                _EmailTitleText.visibility = View.VISIBLE
                _InputEmailEditText.text.clear()
                if (!_InputEmailEditText.hasFocus())
                {
                    _InputEmailBg.setBackgroundResource(R.drawable.text_box)
                    _InputEmailEndBg.setBackgroundResource(R.drawable.text_box)
                }
                setSaveInfoButtonEnable(false)
            }
            R.id._inputPhoneDeleteButton ->
            {
                _InputPhoneEditText.text.clear()
                if (!_InputPhoneEditText.hasFocus()) _InputPhoneBg.setBackgroundResource(R.drawable.text_box)
                setSaveInfoButtonEnable(false)
            }

            // 저장버튼 클릭 이벤트 [나의 정보 수정]
            R.id._saveInfoButton ->
            {
                CommonUtils.getInstance(mContext).hideKeyboard()
                mMyInfoChangeFragmentDataObserver.onClickInfoChangeButton(
                    name = _InputNameEditText.text.toString().trim(),
                    email = getEmailEditTextResult(),
                    phone = _InputPhoneEditText.text.toString().trim()
                )
            }

            // 저장버튼 클릭 이벤트 [비밀번호 변경]
            R.id._savePasswordButton ->
            {
                CommonUtils.getInstance(mContext).hideKeyboard()
                mMyInfoChangeFragmentDataObserver.onClickPasswordChangeButton(
                    oldPassword = _InputPasswordEditText.text.toString().trim(),
                    newPassword = _InputNewPasswordEditText.text.toString().trim(),
                    confirmPassword = _InputNewPasswordConfirmEditText.text.toString().trim()
                )
            }
        }
    }

    /**
     * EditText TextChange Listener
     */
    private val mEditInfoFocusListener = object : View.OnFocusChangeListener
    {
        override fun onFocusChange(view : View?, hasFocus : Boolean)
        {
            when(view?.id)
            {
                // 이름
                R.id._inputNameEditText ->
                {
                    if (hasFocus)
                    {
                        _InputNameBg.setBackgroundResource(R.drawable.text_box_b)
                    }
                    else
                    {
                        _InputNameBg.setBackgroundResource(R.drawable.text_box)
                        mMyInfoChangeFragmentDataObserver.checkNameAvailable(_InputNameEditText.text.toString().trim())
                    }
                }

                // 이메일
                R.id._inputEmailEditText, R.id._inputEmailEndEditText ->
                {
                    if (hasFocus)
                    {
                        if (view.id == R.id._inputEmailEditText)
                        {
                            _EmailTitleText.visibility = View.GONE
                        }
                        else if (view.id == R.id._inputEmailEndEditText)
                        {
                            _EmailEndTitleText.visibility = View.GONE
                        }
                        _InputEmailBg.setBackgroundResource(R.drawable.text_box_b)
                        _InputEmailEndBg.setBackgroundResource(R.drawable.text_box_b)
                    }
                    else
                    {
                        _InputEmailBg.setBackgroundResource(R.drawable.text_box)
                        _InputEmailEndBg.setBackgroundResource(R.drawable.text_box)

                        if (_InputEmailEditText.text.isEmpty())
                        {
                            _EmailTitleText.visibility = View.VISIBLE
                        }

                        if (_InputEmailEndEditText.text.isEmpty())
                        {
                            _EmailEndTitleText.visibility = View.VISIBLE
                        }

                        if (_InputEmailEditText.text.isNotEmpty() && _InputEmailEndEditText.text.isNotEmpty()) // 이메일 앞, 뒤 다 입력된 상태에서만 유효성 체크
                        {
                            mMyInfoChangeFragmentDataObserver.checkEmailAvailable(getEmailEditTextResult())
                        }
                    }
                }

                // 휴대폰 번호
                R.id._inputPhoneEditText ->
                {
                    if (hasFocus)
                    {
                        _InputPhoneBg.setBackgroundResource(R.drawable.text_box_b)
                    }
                    else
                    {
                        _InputPhoneBg.setBackgroundResource(R.drawable.text_box)
                        mMyInfoChangeFragmentDataObserver.checkPhoneAvailable(_InputPhoneEditText.text.toString().trim())
                    }
                }
            }

            // 포커싱 해제시에만 전체 유효성 체크
            if (hasFocus == false)
            {
                checkInputAvailable(Common.PAGE_MY_INFO_CHANGE)
            }
        }
    }

    /**
     * EditText TextChange Listener
     */
    private val mEditPasswordFocusListener = object : View.OnFocusChangeListener
    {
        override fun onFocusChange(view : View?, hasFocus : Boolean)
        {
            when(view?.id)
            {
                // 기존 비밀번호
                R.id._inputPasswordEditText ->
                {
                    if (hasFocus)
                    {
                        _InputPasswordEditBackground.setBackgroundResource(R.drawable.text_box_b)
                    }
                    else
                    {
                        _InputPasswordEditBackground.setBackgroundResource(R.drawable.text_box)
                        mMyInfoChangeFragmentDataObserver.checkPassword(_InputPasswordEditText.text.toString().trim())
                    }
                }

                // 신규 비밀번호
                R.id._inputNewPasswordEditText ->
                {
                    if (hasFocus)
                    {
                        _InputNewPasswordEditBackground.setBackgroundResource(R.drawable.text_box_b)
                    }
                    else
                    {
                        _InputNewPasswordEditBackground.setBackgroundResource(R.drawable.text_box)
                        mMyInfoChangeFragmentDataObserver.checkNewPasswordAvailable(_InputNewPasswordEditText.text.toString().trim())

                    }
                }

                // 신규 비밀번호 확인
                R.id._inputNewPasswordConfirmEditText ->
                {
                    if (hasFocus)
                    {
                        _InputNewPasswordConfirmEditBackground.setBackgroundResource(R.drawable.text_box_b)
                    }
                    else
                    {
                        _InputNewPasswordConfirmEditBackground.setBackgroundResource(R.drawable.text_box)
                        mMyInfoChangeFragmentDataObserver.checkNewPasswordConfirm(
                            _InputNewPasswordEditText.text.toString().trim(),
                            _InputNewPasswordConfirmEditText.text.toString().trim()
                        )
                    }
                }
            }

            // 포커싱 해제시에만 유효성 체크
            if (hasFocus == false)
            {
                checkInputAvailable(Common.PAGE_PASSWORD_CHANGE)
            }
        }
    }

    /**
     * EditText Key Action Listener
     * (키보드 완료 버튼 눌렀을 때 처리)
     */
    private val mEditKeyActionListener = object : TextView.OnEditorActionListener
    {
        override fun onEditorAction(v : TextView?, actionId : Int, event : KeyEvent?) : Boolean
        {
            if(actionId == EditorInfo.IME_ACTION_DONE)
            {
                CommonUtils.getInstance(mContext).hideKeyboard()
                when(v?.id)
                {
                    R.id._inputEmailEditText -> _InputEmailEditText.clearFocus()
                    R.id._inputEmailEndEditText -> _InputEmailEndEditText.clearFocus()
                    R.id._inputPhoneEditText -> _InputPhoneEditText.clearFocus()
                    R.id._inputNewPasswordConfirmEditText -> _InputNewPasswordConfirmEditText.clearFocus()
                }
                return true
            }
            return false
        }
    }
}