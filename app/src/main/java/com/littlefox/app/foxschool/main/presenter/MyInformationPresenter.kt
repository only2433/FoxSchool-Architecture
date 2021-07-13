package com.littlefox.app.foxschool.main.presenter

import android.content.Context
import android.content.Intent
import android.os.Message
import com.littlefox.app.foxschool.common.CommonUtils
import com.littlefox.app.foxschool.main.contract.MyInformationContract
import com.littlefox.library.system.handler.WeakReferenceHandler
import com.littlefox.library.system.handler.callback.MessageHandlerCallback
import com.littlefox.logmonitor.Log

class MyInformationPresenter : MyInformationContract.Presenter
{
    companion object
    {
        private const val SWITCH_AUTO_LOGIN : Int   = 0
        private const val SWITCH_BIO_LOGIN : Int    = 1
        private const val SWITCH_PUSH : Int         = 2
    }

    private lateinit var mContext : Context
    private lateinit var mMyInformationContractView : MyInformationContract.View
    private lateinit var mMainHandler : WeakReferenceHandler

    private var mCheckAutoLogin = false
    private var mCheckBioLogin = false
    private var mCheckPush = false

    constructor(context : Context)
    {
        mContext = context
        mMainHandler = WeakReferenceHandler(context as MessageHandlerCallback)
        mMyInformationContractView = mContext as MyInformationContract.View
        mMyInformationContractView.initView()
        mMyInformationContractView.initFont()

        Log.f("onCreate")
        init()
    }

    private fun init()
    {
        Log.f("")

        // SharedPreference에 저장된 bool값 가져와서 플래그 세팅

        // 플래그값에 따라 스위치 상태 세팅
        mMyInformationContractView.setSwitchView(SWITCH_AUTO_LOGIN, mCheckAutoLogin)
        mMyInformationContractView.setSwitchView(SWITCH_BIO_LOGIN, mCheckBioLogin)
        mMyInformationContractView.setSwitchView(SWITCH_PUSH, mCheckPush)
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
        mMainHandler.removeCallbacksAndMessages(null)
    }

    override fun acvitityResult(requestCode : Int, resultCode : Int, data : Intent?) { }

    override fun sendMessageEvent(msg : Message)
    {
        when(msg.what)
        {
        }
    }

    /**
     * 스위치 플래그 변경
     */
    override fun setSwitchState(switch : Int)
    {
        when(switch)
        {
            SWITCH_AUTO_LOGIN ->
            {
                mCheckAutoLogin = !mCheckAutoLogin
                mMyInformationContractView.setSwitchView(SWITCH_AUTO_LOGIN, mCheckAutoLogin)
            }
            SWITCH_BIO_LOGIN ->
            {
                mCheckBioLogin = !mCheckBioLogin
                mMyInformationContractView.setSwitchView(SWITCH_BIO_LOGIN, mCheckBioLogin)
            }
            SWITCH_PUSH ->
            {
                mCheckPush = !mCheckPush
                mMyInformationContractView.setSwitchView(SWITCH_PUSH, mCheckPush)
            }
        }
    }
}