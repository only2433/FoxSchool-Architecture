package com.littlefox.app.foxschool.main

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.coordinatorlayout.widget.CoordinatorLayout
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.littlefox.app.foxschool.R
import com.littlefox.app.foxschool.base.BaseActivity
import com.littlefox.app.foxschool.common.Common
import com.littlefox.app.foxschool.common.CommonUtils
import com.littlefox.app.foxschool.common.Font
import com.littlefox.app.foxschool.view.ProgressBarAnimation
import com.littlefox.logmonitor.Log
import com.ssomai.android.scalablelayout.ScalableLayout
import dagger.hilt.android.AndroidEntryPoint
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.littlefox.app.foxschool.enumerate.IntroViewMode
import com.littlefox.app.foxschool.main.viewmodel.factory.IntroFactoryViewModel
import com.littlefox.app.foxschool.dialog.PasswordChangeDialog
import com.littlefox.app.foxschool.dialog.TemplateAlertDialog
import com.littlefox.app.foxschool.dialog.listener.DialogListener
import com.littlefox.app.foxschool.dialog.listener.PasswordChangeListener
import com.littlefox.app.foxschool.enumerate.DialogButtonType
import com.littlefox.app.foxschool.enumerate.PasswordGuideType

@AndroidEntryPoint
class IntroActivity : BaseActivity()
{
    @BindView(R.id._mainBaseLayout)
    lateinit var _MainBaseLayout : CoordinatorLayout

    @BindView(R.id._introBaseLayout)
    lateinit var _IntroBaseLayout : RelativeLayout

    @BindView(R.id._logoTextImage)
    lateinit var  _IntroLogoTextImage : ImageView

    @BindView(R.id._introProgressPercent)
    lateinit var _IntroProgressPercent : ProgressBar

    @BindView(R.id._introProgressText)
    lateinit var _IntroProgressText : TextView

    @BindView(R.id._progressLayout)
    lateinit var _ProgressLayout : ScalableLayout

    @BindView(R.id._frameAnimationLayout)
    lateinit var _FrameAnimationLayout : ScalableLayout

    @BindView(R.id._frameAnimationView)
    lateinit var _FrameAnimationView : ImageView

    @BindView(R.id._introItemSelectLayout)
    lateinit var _IntroItemSelectLayout : ScalableLayout

    @BindView(R.id._introMessageText)
    lateinit var _IntroTitleText : TextView

    @BindView(R.id._introduceText)
    lateinit var _IntroduceTextButton : TextView

    @BindView(R.id._loginText)
    lateinit var _LoginTextButton : TextView

    companion object
    {
        private const val SYSTEM_DIALOG_REASON_KEY : String         = "reason"
        private const val SYSTEM_DIALOG_REASON_HOME_KEY : String    = "homekey"
    }

    private lateinit var mProgressBarAnimation : ProgressBarAnimation
    private var mFrameAnimationDrawable : AnimationDrawable? = null
    private var mHomeKeyIntentFilter : IntentFilter? = null

    // 비밀번호 변경 안내 관련 변수
    private var mPasswordChangeDialog : PasswordChangeDialog? = null
    private lateinit var mTemplateAlertDialog : TemplateAlertDialog


    private val factoryViewModel: IntroFactoryViewModel by viewModels()

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)
        if(CommonUtils.getInstance(this).isTabletModel)
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE)
            setContentView(R.layout.activity_intro_tablet)
        } else
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            setContentView(R.layout.activity_intro)
        }
        ButterKnife.bind(this)

        initView()
        initFont()
        setupObserverViewModel()

        factoryViewModel.init(this)
        factoryViewModel.onAddResultLaunchers(mLoginActivityResult)
        mHomeKeyIntentFilter = IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
    }

    override fun onResume()
    {
        super.onResume()
        factoryViewModel.resume()
        registerReceiver(mBroadcastReceiver, mHomeKeyIntentFilter)
    }

    override fun onPause()
    {
        super.onPause()
        factoryViewModel.pause()
        unregisterReceiver(mBroadcastReceiver)
    }

    override fun onStop()
    {
        Log.f("")
        super.onStop()
    }

    override fun onDestroy()
    {
        super.onDestroy()
        factoryViewModel.destroy()
        stopFrameAnimation()
    }

    override fun initView()
    {
        _IntroLogoTextImage.setOnTouchListener(mLogoTouchListener)
    }

    override fun initFont()
    {
        _IntroTitleText.setTypeface(Font.getInstance(this).getTypefaceBold())
        _IntroProgressText.setTypeface(Font.getInstance(this).getTypefaceBold())
        _IntroduceTextButton.setTypeface(Font.getInstance(this).getTypefaceBold())
        _LoginTextButton.setTypeface(Font.getInstance(this).getTypefaceBold())
    }

    override fun setupObserverViewModel()
    {
        factoryViewModel.isLoading.observe(this, Observer<Boolean> { loading ->
            if(loading)
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

        factoryViewModel.bottomViewType.observe(this, Observer<IntroViewMode> {mode ->
                when(mode)
                {
                    IntroViewMode.PROGRESS ->
                    {
                        Log.f("------> IntroViewMode.PROGRESS")
                        _IntroItemSelectLayout.setVisibility(View.GONE)
                        _ProgressLayout.setVisibility(View.VISIBLE)
                        _FrameAnimationLayout.setVisibility(View.VISIBLE)
                        startFrameAnimation()
                    }
                    IntroViewMode.SELECT ->
                    {
                        Log.f("------> IntroViewMode.SELECT")
                        _IntroItemSelectLayout.setVisibility(View.VISIBLE)
                        _ProgressLayout.setVisibility(View.GONE)
                        _FrameAnimationLayout.setVisibility(View.GONE)
                    }
                }
            })

        factoryViewModel.progressPercent.observe(this, Observer<Pair<Float, Float>> { progress ->
            mProgressBarAnimation = ProgressBarAnimation(_IntroProgressPercent, _IntroProgressText, progress.first, progress.second)
            mProgressBarAnimation.duration = Common.DURATION_SHORT_LONG
            _IntroProgressPercent.startAnimation(mProgressBarAnimation)
        })

        factoryViewModel.dialogFilePermission.observe(this, Observer {
            showChangeFilePermissionDialog()
        })

        factoryViewModel.dialogSelectUpdate.observe(this, Observer {
            showSelectUpdateDialog()
        })

        factoryViewModel.dialogForceUpdate.observe(this, Observer {
            showForceUpdateDialog()
        })

        factoryViewModel.showDialogPasswordChange.observe(this, Observer<PasswordGuideType> { type ->
            showPasswordChangeDialog(type)
        })

        factoryViewModel.hideDialogPasswordChange.observe(this, Observer{
            hidePasswordChangeDialog()
        })
    }


    override fun onUserLeaveHint()
    {
        super.onUserLeaveHint()
        Log.f("")
    }

    override fun onRequestPermissionsResult(requestCode : Int, permissions : Array<out String>, grantResults : IntArray)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        factoryViewModel.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onNewIntent(intent : Intent?)
    {
        Log.i("")
        super.onNewIntent(intent)
    }

    private fun startFrameAnimation()
    {
        if(mFrameAnimationDrawable == null)
        {
            _FrameAnimationView.setBackgroundResource(R.drawable.frame_animation_intro)
            mFrameAnimationDrawable = _FrameAnimationView.background as AnimationDrawable
        }
        mFrameAnimationDrawable?.start()
    }

    private fun stopFrameAnimation()
    {
        mFrameAnimationDrawable?.stop()
        mFrameAnimationDrawable = null
    }

    override fun onBackPressed()
    {
        finish()
    }

    /**
     * 파일 권한 허용 요청 다이얼로그
     * - 로그 파일 저장을 위해
     */
    private fun showChangeFilePermissionDialog()
    {
        Log.f("")
        mTemplateAlertDialog = TemplateAlertDialog(this).apply {
            setMessage(resources.getString(R.string.message_warning_storage_permission))
            setDialogEventType(IntroFactoryViewModel.DIALOG_TYPE_WARNING_FILE_PERMISSION)
            setButtonType(DialogButtonType.BUTTON_2)
            setButtonText(resources.getString(R.string.text_cancel), resources.getString(R.string.text_change_permission))
            setDialogListener(mDialogListener)
            show()
        }
    }

    private fun showForceUpdateDialog()
    {
        Log.f("")
        mTemplateAlertDialog = TemplateAlertDialog(this).apply {
            setMessage(resources.getString(R.string.message_force_update))
            setDialogEventType(IntroFactoryViewModel.DIALOG_TYPE_FORCE_UPDATE)
            setButtonType(DialogButtonType.BUTTON_1)
            setDialogListener(mDialogListener)
            show()
        }
    }

    private fun showSelectUpdateDialog()
    {
        Log.f("")
        mTemplateAlertDialog = TemplateAlertDialog(this).apply {
            setMessage(resources.getString(R.string.message_need_update))
            setDialogEventType(IntroFactoryViewModel.DIALOG_TYPE_SELECT_UPDATE_CONFIRM)
            setButtonType(DialogButtonType.BUTTON_2)
            setDialogListener(mDialogListener)
            show()
        }
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


    @OnClick( R.id._introduceText, R.id._loginText)
    fun onClickView(view : View)
    {
        when(view.id)
        {
            R.id._introduceText -> factoryViewModel.onClickIntroduce()
            R.id._loginText -> factoryViewModel.onClickLogin()
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

    private val mLogoTouchListener : View.OnTouchListener = object : View.OnTouchListener
    {
        override fun onTouch(view : View?, event : MotionEvent?) : Boolean
        {
            if(event?.action == MotionEvent.ACTION_DOWN)
            {
                factoryViewModel.onActivateEasterEgg()
            }
            else if(event?.action == MotionEvent.ACTION_UP || event?.action == MotionEvent.ACTION_OUTSIDE)
            {
                factoryViewModel.onDeactiveEasterEgg()
            }
            return true
        }
    }

    /**
     * 비밀번호 변경 다이얼로그 Listener
     */
    val mPasswordChangeDialogListener : PasswordChangeListener = object : PasswordChangeListener
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

    private val mBroadcastReceiver : BroadcastReceiver = object : BroadcastReceiver()
    {
        override fun onReceive(context : Context, intent : Intent)
        {
            Log.i("", ">>> Home Event")
            val action : String? = intent.getAction()
            if(action == Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
            {
                val reason : String = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY)!!
                if(reason != null)
                {
                    if(reason == SYSTEM_DIALOG_REASON_HOME_KEY)
                    {
                        factoryViewModel.onClickHomeButton()
                    }
                }
            }
        }
    }

    private val mLoginActivityResult = registerForActivityResult(StartActivityForResult())
    { result ->
        if(result.resultCode == RESULT_OK)
        {
            factoryViewModel.onActivityResult()
        }
    }
}