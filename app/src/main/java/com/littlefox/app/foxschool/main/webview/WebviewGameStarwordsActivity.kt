package com.littlefox.app.foxschool.main.webview

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Message
import android.view.View
import android.view.Window
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.littlefox.app.foxschool.R
import com.littlefox.app.foxschool.base.BaseActivity
import com.littlefox.app.foxschool.common.Common
import com.littlefox.app.foxschool.common.CommonUtils
import com.littlefox.app.foxschool.enumerate.ActivityMode
import com.littlefox.app.foxschool.enumerate.AnimationMode
import com.littlefox.app.foxschool.main.webview.bridge.BaseWebviewBridge
import com.littlefox.app.foxschool.management.IntentManagementFactory
import com.littlefox.library.system.handler.WeakReferenceHandler
import com.littlefox.library.system.handler.callback.MessageHandlerCallback
import com.littlefox.library.view.dialog.MaterialLoadingDialog
import com.littlefox.logmonitor.Log

class WebviewGameStarwordsActivity : BaseActivity(), MessageHandlerCallback
{
    @BindView(R.id._mainBaseLayout)
    lateinit var _MainBaseLayout : CoordinatorLayout

    @BindView(R.id._closeButton)
    lateinit var _CloseButton : ImageView

    @BindView(R.id._webview)
    lateinit var _WebView : WebView

    private var mCurrentContentID = ""
    private var mLoadingDialog : MaterialLoadingDialog? = null
    private var mMainHandler : WeakReferenceHandler? = null

    /** ========== LifeCycle ========== */
    override fun onCreate(savedInstanceState : Bundle?)
    {
        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        super.onCreate(savedInstanceState)
        Log.f("")

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        setContentView(R.layout.activity_webview_starwords)
        ButterKnife.bind(this)
        mMainHandler = WeakReferenceHandler(this)

        initView()
        initWebView()
    }

    override fun onResume()
    {
        Log.f("")
        super.onResume()
        _WebView.onResume()
    }

    override fun onPause()
    {
        Log.f("")
        super.onPause()
        _WebView.onPause()
    }

    override fun onDestroy()
    {
        Log.f("")
        _WebView.loadUrl("about:blink")
        _WebView.removeAllViews()
        _WebView.destroy()
        super.onDestroy()
    }

    override fun finish()
    {
        super.finish()
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out)
    }
    /** ========== LifeCycle end ========== */

    /** ========== Init ========== */
    private fun initView()
    {
        val statusBarColor : Int = CommonUtils.getInstance(this).getTopBarStatusBarColor()
        CommonUtils.getInstance(this).setStatusBar(resources.getColor(statusBarColor))
    }

    private fun initWebView()
    {
        showLoading()
        mCurrentContentID = intent.getStringExtra(Common.INTENT_GAME_STARWORDS_ID)
        val extraHeaders = CommonUtils.getInstance(this).getHeaderInformation(true)
        _WebView.webViewClient = DataWebViewClient()
        _WebView.settings.javaScriptEnabled = true
        _WebView.loadUrl("${Common.URL_GAME_STARWORDS}${mCurrentContentID}", extraHeaders)
        _WebView.addJavascriptInterface(
            DataInterfaceBridge(this, _MainBaseLayout, _WebView),
            Common.BRIDGE_NAME
        )
    }
    /** ========== Init end ========== */

    private fun startLearningLogActivity()
    {
        Log.f("")
        IntentManagementFactory.getInstance()
            .readyActivityMode(ActivityMode.WEBVIEW_LEARNING_LOG)
            .setAnimationMode(AnimationMode.NORMAL_ANIMATION)
            .startActivity()
    }

    override fun onBackPressed() { super.onBackPressed() }

    @OnClick(R.id._closeButton)
    fun onClickView(view : View)
    {
        when(view.id)
        {
            R.id._closeButton -> super.onBackPressed()
        }
    }

    override fun handlerMessage(message : Message?) { }

    private fun showLoading()
    {
        if(mLoadingDialog == null)
        {
            mLoadingDialog = MaterialLoadingDialog(
                this,
                CommonUtils.getInstance(this).getPixel(Common.LOADING_DIALOG_SIZE)
            )
        }
        mLoadingDialog!!.show()
    }

    private fun hideLoading()
    {
        if(mLoadingDialog != null)
        {
            mLoadingDialog!!.dismiss()
            mLoadingDialog = null
        }
    }

    override fun onWindowFocusChanged(hasFocus : Boolean)
    {
        super.onWindowFocusChanged(hasFocus)
        if(hasFocus)
        {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION and View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }
    }

    internal inner class DataWebViewClient : WebViewClient()
    {
        override fun shouldOverrideUrlLoading(view : WebView, request : WebResourceRequest) : Boolean
        {
            return super.shouldOverrideUrlLoading(view, request)
        }

        override fun onPageFinished(view : WebView, url : String)
        {
            hideLoading()
            super.onPageFinished(view, url)
            Log.f("url : $url")
        }
    }

    internal inner class DataInterfaceBridge : BaseWebviewBridge
    {
        constructor(context: Context, coordinatorLayout : CoordinatorLayout, webView : WebView)
                : super(context, coordinatorLayout, webView)

        @JavascriptInterface
        fun onInterfaceSendStudyLog()
        {
            _WebView.postDelayed({startLearningLogActivity()}, Common.DURATION_SHORTER)
        }
    }
}