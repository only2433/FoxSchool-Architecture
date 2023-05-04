package com.littlefox.app.foxschool.main

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.activity.viewModels
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Observer
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.littlefox.app.foxschool.R
import com.littlefox.app.foxschool.`object`.data.login.UserLoginData
import com.littlefox.app.foxschool.`object`.result.login.SchoolItemDataResult
import com.littlefox.app.foxschool.main.viewmodel.factory.LoginFactoryViewModel
import com.littlefox.app.foxschool.base.BaseActivity
import com.littlefox.app.foxschool.common.CommonUtils
import com.littlefox.app.foxschool.common.Font
import com.littlefox.app.foxschool.dialog.PasswordChangeDialog
import com.littlefox.app.foxschool.dialog.listener.PasswordChangeListener
import com.littlefox.app.foxschool.enumerate.PasswordGuideType
import com.littlefox.logmonitor.Log
import com.ssomai.android.scalablelayout.ScalableLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : BaseActivity()
{
    @BindView(R.id._mainBaseLayout)
    lateinit var _MainBaseLayout : CoordinatorLayout

    @BindView(R.id._titleBaselayout)
    lateinit var _TitleBaselayout : ScalableLayout

    @BindView(R.id._closeButtonRect)
    lateinit var _CloseButtonRect : ImageView

    @BindView(R.id._closeButton)
    lateinit var _CloseButton : ImageView

    @BindView(R.id._titleText)
    lateinit var _TitleText : TextView

    @BindView(R.id._contentsLayout)
    lateinit var _ContentsLayout : ScalableLayout

    @BindView(R.id._inputIdEditBackground)
    lateinit var _inputIdEditBackground : ImageView

    @BindView(R.id._inputIdEditText)
    lateinit var _InputIdEditText : EditText

    @BindView(R.id._inputPasswordEditBackground)
    lateinit var _inputPasswordEditBackground : ImageView

    @BindView(R.id._inputPasswordEditText)
    lateinit var _InputPasswordEditText : EditText

    @BindView(R.id._inputSchoolEditBackground)
    lateinit var _InputSchoolEditBackground : ImageView

    @BindView(R.id._inputSchoolLine)
    lateinit var _InputSchoolLine : ImageView

    @BindView(R.id._inputSchoolDeleteButton)
    lateinit var _InputSchoolDeleteButton : ImageView

    @BindView(R.id._inputSchoolEditText)
    lateinit var _InputSchoolEditText : EditText

    @BindView(R.id._autoLoginIcon)
    lateinit var _AutoLoginCheckIcon : ImageView

    @BindView(R.id._autoLoginText)
    lateinit var _AutoLoginText : TextView

    @BindView(R.id._loginButtonText)
    lateinit var _LoginButtonText : TextView

    @BindView(R.id._forgetIDText)
    lateinit var _ForgetIDText : TextView

    @BindView(R.id._forgetDividerLine)
    lateinit var _ForgetDividerLine : ImageView

    @BindView(R.id._forgetPasswordText)
    lateinit var _ForgetPasswordText : TextView

    @BindView(R.id._onlyWebSignPossibleTitleText)
    lateinit var _OnlyWebSignPossibleTitleText : TextView

    @BindView(R.id._customerCenterInfoText)
    lateinit var _CustomerCenterInfoText : TextView

    @BindView(R.id._searchSchoolView)
    lateinit var _SearchSchoolView : ScrollView

    // 비밀번호 변경 안내 관련 변수
    private var mPasswordChangeDialog : PasswordChangeDialog? = null

    private val mBaseSchoolList : ArrayList<SchoolItemDataResult> = ArrayList<SchoolItemDataResult>() // 학교 베이스 리스트
    private val mSearchSchoolList : ArrayList<SchoolItemDataResult> = ArrayList<SchoolItemDataResult>() // 학교 검색 결과 리스트
    private var mSelectedSchoolData : SchoolItemDataResult? = null
    private var isAutoLoginCheck : Boolean = false

    // 학교 검색 팝업 레이아웃 사이즈
    private var mSearchLayoutHeight : Float = 0f
    private var mSearchLayoutWidth : Float = 0f
    private var mSearchLayoutLeft : Float = 0f
    private var mSearchLayoutTop : Float = 0f

    // 학교 검색 팝업 텍스트뷰 사이즈
    private var mSearchTextHeight : Float = 0f
    private var mSearchTextLeft : Float = 0f
    private var mSearchTextTop : Float = 0f
    private var mSearchTextSize : Float = 0f

    private val factoryViewModel : LoginFactoryViewModel by viewModels()

    /** ========== LifeCycle ========== */
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)
        if(CommonUtils.getInstance(this).checkTablet)
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE)
            setContentView(R.layout.activity_login_tablet)
        } else
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            setContentView(R.layout.activity_login)
        }

        ButterKnife.bind(this)

        initView()
        initFont()
        setupObserverViewModel()

        // 학교 검색 팝업 레이아웃 사이즈
        mSearchLayoutHeight = if(CommonUtils.getInstance(this).checkTablet) 84f else 120f
        mSearchLayoutWidth = if(CommonUtils.getInstance(this).checkTablet) 796f else 884f
        mSearchLayoutLeft = if(CommonUtils.getInstance(this).checkTablet) 562f else 98f
        mSearchLayoutTop = if(CommonUtils.getInstance(this).checkTablet) 50f else 75f

        // 학교 검색 팝업 텍스트뷰 사이즈
        mSearchTextHeight = if(CommonUtils.getInstance(this).checkTablet) 75f else 100f
        mSearchTextLeft = if(CommonUtils.getInstance(this).checkTablet) 105f else 140f
        mSearchTextTop= if(CommonUtils.getInstance(this).checkTablet) 135f else 195f
        mSearchTextSize = if(CommonUtils.getInstance(this).checkTablet) 32f else 42f

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

    override fun onStop()
    {
        Log.f("")
        super.onStop()
    }

    override fun onDestroy()
    {
        super.onDestroy()
        mSelectedSchoolData = null
        factoryViewModel.destroy()
    }

    override fun finish()
    {
        super.finish()
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out)
    }

    /** ========== LifeCycle end ========== */

    /** ========== Init ========== */
    override fun initView()
    {
        _TitleText.text = resources.getString(R.string.text_login)
        _CloseButton.visibility = View.VISIBLE
        _CloseButtonRect.visibility = View.VISIBLE

        _InputSchoolEditText.addTextChangedListener(mEditTextChangeListener)
        _InputIdEditText.addTextChangedListener(mLoginTextChangeListener)
        _InputPasswordEditText.addTextChangedListener(mLoginTextChangeListener)
        _InputSchoolEditText.onFocusChangeListener = mEditFocusListener
        _InputIdEditText.onFocusChangeListener = mEditFocusListener
        _InputPasswordEditText.onFocusChangeListener = mEditFocusListener
        _InputPasswordEditText.setOnEditorActionListener(mEditKeyActionListener)
    }

    override fun initFont()
    {
        _TitleText.typeface = Font.getInstance(this).getTypefaceBold()
        _InputIdEditText.typeface = Font.getInstance(this).getTypefaceRegular()
        _InputPasswordEditText.typeface = Font.getInstance(this).getTypefaceRegular()
        _InputSchoolEditText.typeface = Font.getInstance(this).getTypefaceRegular()
        _AutoLoginText.typeface = Font.getInstance(this).getTypefaceRegular()
        _LoginButtonText.typeface = Font.getInstance(this).getTypefaceRegular()
        _ForgetIDText.typeface = Font.getInstance(this).getTypefaceRegular()
        _ForgetPasswordText.typeface = Font.getInstance(this).getTypefaceRegular()
        _OnlyWebSignPossibleTitleText.typeface = Font.getInstance(this).getTypefaceMedium()
        _CustomerCenterInfoText.typeface = Font.getInstance(this).getTypefaceMedium()
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
            CommonUtils.getInstance(this).showSuccessSnackMessage(_MainBaseLayout, message, Gravity.CENTER)
        })

        factoryViewModel.errorMessage.observe(this, Observer<String> { message ->
            CommonUtils.getInstance(this).showErrorSnackMessage(_MainBaseLayout, message, Gravity.CENTER)
        })

        factoryViewModel.schoolList.observe(this, Observer<ArrayList<SchoolItemDataResult>> { data ->
            mBaseSchoolList.addAll(data) // 학교 리스트 세팅
        })

        factoryViewModel.inputEmptyMessage.observe(this, Observer<String> { message ->
            CommonUtils.getInstance(this).showErrorSnackMessage(_MainBaseLayout, message, Gravity.CENTER)
        })

        factoryViewModel.showDialogPasswordChange.observe(this, Observer<PasswordGuideType> { type ->
            showPasswordChangeDialog(type)
        })

        factoryViewModel.hideDialogPasswordChange.observe(this, Observer{
            hidePasswordChangeDialog()
        })

        factoryViewModel.finishActivity.observe(this, Observer{
            setResult(Activity.RESULT_OK)
            finish()
        })
    }

    override fun onNewIntent(intent : Intent?)
    {
        Log.i("")
        super.onNewIntent(intent)
    }

    private fun showPasswordChangeDialog(type: PasswordGuideType)
    {
        Log.f("")
        mPasswordChangeDialog = PasswordChangeDialog(this, type).apply {
            setPasswordChangeListener(mPasswordChangeDialogListener)
            setCancelable(false)
            show()
        }
    }

    private fun hidePasswordChangeDialog()
    {
        mPasswordChangeDialog!!.dismiss()
    }

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
                if(viewNew == view)
                {
                    val rect = Rect()
                    val coordinates = IntArray(2)
                    view.getLocationOnScreen(coordinates)
                    rect[coordinates[0], coordinates[1], coordinates[0] + view.width] =
                        coordinates[1] + view.height
                    val x = ev.x.toInt()
                    val y = ev.y.toInt()
                    if(rect.contains(x, y))
                    {
                        return consumed
                    }
                } else if(viewNew is EditText)
                {
                    Log.f("consumed : $consumed")
                    return consumed
                }
                CommonUtils.getInstance(this).hideKeyboard()
                viewNew.clearFocus()
                return consumed
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    @OnClick(
        R.id._closeButtonRect, R.id._autoLoginIcon, R.id._autoLoginText, R.id._loginButtonText, R.id._forgetIDText,
        R.id._forgetPasswordText, R.id._inputSchoolDeleteButton
    )
    fun onClickView(view : View)
    {
        when(view.id)
        {
            R.id._closeButtonRect -> super.onBackPressed()
            R.id._autoLoginIcon, R.id._autoLoginText ->
            {
                isAutoLoginCheck = !isAutoLoginCheck
                if(isAutoLoginCheck)
                {
                    _AutoLoginCheckIcon.setImageResource(R.drawable.radio_on)
                } else
                {
                    _AutoLoginCheckIcon.setImageResource(R.drawable.radio_off)
                }
                factoryViewModel.onCheckAutoLogin(isAutoLoginCheck)
            }
            R.id._loginButtonText ->
            {
                val schoolCode = if (_InputSchoolEditText.text.isNotEmpty()) mSelectedSchoolData!!.getSchoolID() else ""
                factoryViewModel.onClickLogin(
                    UserLoginData(
                        _InputIdEditText.text.toString().trim(),
                        _InputPasswordEditText.text.toString().trim(),
                        schoolCode
                    )
                )
            }
            R.id._forgetIDText -> factoryViewModel.onClickFindID()
            R.id._forgetPasswordText -> factoryViewModel.onClickFindPassword()
            R.id._inputSchoolDeleteButton ->
            {
                _InputSchoolEditText.text.clear()
                mSelectedSchoolData = null // 선택 학교 데이터 초기화
                setLoginButtonBackground()
            }
        }
    }

    /**
     * 학교 검색 리스트 표시
     * (구조 : ScrollView -> ScalableLayout -> TextView)
     */
    private fun setSchoolSearchListView()
    {
        val listCount = if (CommonUtils.getInstance(this).checkTablet) 3 else 4 // 학교 리스트 표시 개수 : 태블릿 3건, 핸드폰 4건
        clearSearchView() // 기존의 검색화면(팝업)초기화
        setSearchState(isSearch = true) // 검색상태 변경 : 검색중
        val _SearchView = ScalableLayout(this)
        _SearchSchoolView.addView(_SearchView) // 스크롤뷰에 추가

        if(mSearchSchoolList.isNotEmpty())
        {
            // 검색 결과가 있는 경우
            for(i in mSearchSchoolList.indices)
            {
                val schoolText = TextView(this).let {
                    it.typeface = Font.getInstance(this).getTypefaceRegular()
                    it.gravity = Gravity.CENTER_VERTICAL
                    it.text = mSearchSchoolList[i].getSchoolName()
                    it.setTextColor(resources.getColor(R.color.color_444444))
                    it.setOnClickListener {
                        // 선택한 항목으로 학교 검색 필드에 값 입력
                        // 검색한 리스트와 뷰는 초기화
                        CommonUtils.getInstance(this).hideKeyboard()
                        mSelectedSchoolData = mSearchSchoolList[i]
                        _InputSchoolEditText.setText(mSelectedSchoolData!!.getSchoolName())
                        _InputSchoolEditText.clearFocus()
                        mSearchSchoolList.clear() // 검색결과 리스트 초기화
                        clearSearchView() // 검색화면(팝업)초기화
                        setLoginButtonBackground()
                    }
                    it
                }
                _SearchView.addView(schoolText, mSearchTextLeft, mSearchTextHeight * i, 650f, mSearchTextHeight)
                _SearchView.setScale_TextSize(schoolText, mSearchTextSize)
            }

            // 리스트 사이즈 만큼 _SearchView 크기 지정
            _SearchView.setScaleSize(mSearchLayoutWidth, mSearchTextHeight * mSearchSchoolList.size)

            // 학교 검색리스트 배경 사이즈 설정
            // _InputSchoolEditBackground 높이 : 기존 입력필드 높이 + 리스트사이즈
            // _SearchSchoolView 높이 : 리스트사이즈
            _ContentsLayout.moveChildView(_InputSchoolEditBackground, mSearchLayoutLeft, mSearchLayoutTop, mSearchLayoutWidth, mSearchLayoutHeight + mSearchTextHeight * listCount)
            _ContentsLayout.moveChildView(_SearchSchoolView, mSearchLayoutLeft, mSearchTextTop, mSearchLayoutWidth, mSearchTextHeight * listCount)
        }
        else if(mSearchSchoolList.isEmpty() && _InputSchoolEditText.text.isNotEmpty())
        {
            // 검색 결과가 없는 경우
            val resultText = TextView(this).apply {
                typeface = Font.getInstance(context).getTypefaceRegular()
                gravity = Gravity.CENTER_VERTICAL
                text = resources.getString(R.string.text_result_empty)
                setTextColor(resources.getColor(R.color.color_cacaca))
            }

            _SearchView.run {
                setScaleSize(mSearchLayoutWidth, mSearchTextHeight)
                addView(resultText, mSearchTextLeft, 0f, 650f, mSearchTextHeight)
                setScale_TextSize(resultText, mSearchTextSize)
            }

            // 학교 검색리스트 배경 사이즈 설정
            // _InputSchoolEditBackground 높이 : 기존 입력필드 높이 + 리스트사이즈
            // _SearchSchoolView 높이 : 리스트사이즈
            _ContentsLayout.moveChildView(_InputSchoolEditBackground, mSearchLayoutLeft, mSearchLayoutTop, mSearchLayoutWidth, mSearchLayoutHeight + mSearchTextHeight * listCount)
            _ContentsLayout.moveChildView(_SearchSchoolView, mSearchLayoutLeft, mSearchTextTop, mSearchLayoutWidth, mSearchTextHeight * listCount)
        }
        else
        {
            // 검색 안하는 경우
            // 팝업 뷰 및 데이터 초기화
            clearSearchView()
            mSearchSchoolList.clear()
        }
    }

    /**
     * 학교 검색에 따른 View 활성/비활성 처리
     */
    private fun setSearchState(isSearch : Boolean)
    {
        _SearchSchoolView.visibility = if (_InputSchoolEditText.text.isNotEmpty()) View.VISIBLE else View.GONE // 검색 스크롤 영역 표시/비표시
        _InputSchoolDeleteButton.visibility = if (_InputSchoolEditText.text.isNotEmpty()) View.VISIBLE else View.GONE // 뷰 구분선 표시/비표시
        _InputSchoolLine.visibility = if (isSearch) View.VISIBLE else View.GONE // 검색 초기화 버튼 활성/비활성
    }

    private fun setLoginButtonBackground()
    {
        if (mSelectedSchoolData != null &&
            _InputIdEditText.text.isNotEmpty() &&
            _InputPasswordEditText.text.isNotEmpty())
        {
            _LoginButtonText.background = resources.getDrawable(R.drawable.round_box_light_blue_84)
        }
        else
        {
            _LoginButtonText.background = resources.getDrawable(R.drawable.round_box_gray_84)
        }
    }

    /**
     * 검색뷰(팝업) 초기화
     */
    private fun clearSearchView()
    {
        _ContentsLayout.moveChildView(_InputSchoolEditBackground, mSearchLayoutLeft, mSearchLayoutTop, mSearchLayoutWidth, mSearchLayoutHeight) // 배경이미지 사이즈 원상복귀
        _ContentsLayout.moveChildView(_SearchSchoolView, mSearchLayoutLeft, mSearchTextTop, mSearchLayoutWidth, mSearchLayoutHeight) // 스크롤 사이즈 원상복귀
        setSearchState(isSearch = false) // 검색상태 변경 : 검색종료
        _SearchSchoolView.removeAllViews() // 스크롤뷰 안에 생성했던 뷰 삭제
    }

    /**
     * 학교검색 EditText TextChange Listener
     */
    private val mEditTextChangeListener = object : TextWatcher
    {
        override fun beforeTextChanged(s : CharSequence?, start : Int, count : Int, after : Int) { }

        override fun onTextChanged(s : CharSequence?, start : Int, before : Int, count : Int)
        {
            mSearchSchoolList.clear() // 이전에 검색된 리스트 초기화
            if(s!!.isNotEmpty())
            {
                mSearchSchoolList.addAll(mBaseSchoolList.filter {
                    (it.getSchoolName()).contains(_InputSchoolEditText.text)
                }) // 현재 입력된 값으로 새로 검색
            }
            setSchoolSearchListView() // 새로 검색된 리스트로 화면 표시
        }

        override fun afterTextChanged(s : Editable?) { }
    }

    /**
     * 아이디/비밀번호 EditText TextChange Listener
     */
    private val mLoginTextChangeListener = object : TextWatcher
    {
        override fun beforeTextChanged(s : CharSequence?, start : Int, count : Int, after : Int) { }

        override fun onTextChanged(s : CharSequence?, start : Int, before : Int, count : Int)
        {
            setLoginButtonBackground()
        }

        override fun afterTextChanged(s : Editable?) { }
    }

    private val mEditFocusListener = object : View.OnFocusChangeListener
    {
        override fun onFocusChange(view : View?, hasFocus : Boolean)
        {
            when(view?.id)
            {
                R.id._inputSchoolEditText ->
                {
                    _InputSchoolEditText.isCursorVisible = hasFocus
                }
                R.id._inputIdEditText ->
                {
                    if(hasFocus)
                    {
                        clearSearchView()
                        _inputIdEditBackground.setBackgroundResource(R.drawable.text_box_b)
                        _InputIdEditText.isCursorVisible = true
                    } else
                    {
                        _inputIdEditBackground.setBackgroundResource(R.drawable.text_box)
                        _InputIdEditText.isCursorVisible = false
                    }
                }
                R.id._inputPasswordEditText ->
                {
                    if(hasFocus)
                    {
                        clearSearchView()
                        _inputPasswordEditBackground.setBackgroundResource(R.drawable.text_box_b)
                        _InputPasswordEditText.isCursorVisible = true
                    } else
                    {
                        _inputPasswordEditBackground.setBackgroundResource(R.drawable.text_box)
                        _InputPasswordEditText.isCursorVisible = false
                    }
                }
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
                CommonUtils.getInstance(this@LoginActivity).hideKeyboard()
                when(v?.id)
                {
                    R.id._inputPasswordEditText -> _InputPasswordEditText.clearFocus()
                }
                return true
            }
            return false
        }
    }

    private val mPasswordChangeDialogListener : PasswordChangeListener = object : PasswordChangeListener
    {
        /**
         * [비밀번호 변경] 버튼 클릭 이벤트
         */
        override fun onClickChangeButton(oldPassword : String, newPassword : String, confirmPassword : String)
        {
            factoryViewModel.onClickChangeButton(oldPassword, newPassword, confirmPassword)
        }

        /**
         * [다음에 변경] 버튼 클릭 이벤트
         */
        override fun onClickLaterButton()
        {
            factoryViewModel.onClickLaterButton()
        }

        /**
         * [현재 비밀번호로 유지하기] 버튼 클릭 이벤트
         */
        override fun onClickKeepButton()
        {
            factoryViewModel.onClickKeepButton()
        }
    }
}
