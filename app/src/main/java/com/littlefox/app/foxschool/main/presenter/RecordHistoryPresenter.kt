package com.littlefox.app.foxschool.main.presenter

import android.content.Context
import android.content.Intent
import android.os.Message
import androidx.appcompat.app.AppCompatActivity
import com.littlefox.app.foxschool.`object`.result.RecordHistoryBaseObject
import com.littlefox.app.foxschool.`object`.result.base.BaseResult
import com.littlefox.app.foxschool.`object`.result.record.RecordHistoryResult
import com.littlefox.app.foxschool.adapter.RecordHistoryListAdapter
import com.littlefox.app.foxschool.adapter.listener.RecordItemListener
import com.littlefox.app.foxschool.common.Common
import com.littlefox.app.foxschool.coroutine.RecordHistoryCoroutine
import com.littlefox.app.foxschool.dialog.AudioPlayDialog
import com.littlefox.app.foxschool.dialog.TemplateAlertDialog
import com.littlefox.app.foxschool.main.contract.RecordHistoryContract
import com.littlefox.app.foxschool.management.IntentManagementFactory
import com.littlefox.library.system.async.listener.AsyncListener
import com.littlefox.library.system.handler.WeakReferenceHandler
import com.littlefox.library.system.handler.callback.MessageHandlerCallback
import com.littlefox.logmonitor.Log

/**
 * 녹음기록 Presenter
 * @author 김태은
 */
class RecordHistoryPresenter : RecordHistoryContract.Presenter
{
    private lateinit var mContext : Context
    private lateinit var mRecordHistoryContractView : RecordHistoryContract.View
    private lateinit var mMainHandler : WeakReferenceHandler
    private lateinit var mTemplateAlertDialog : TemplateAlertDialog

    private var mRecordHistoryCoroutine : RecordHistoryCoroutine? = null
    private lateinit var mRecordHistoryResult : ArrayList<RecordHistoryResult>

    private var mRecordHistoryListAdapter : RecordHistoryListAdapter? = null

    private var mAudioPlayDialog : AudioPlayDialog? = null

    constructor(context : Context)
    {
        mContext = context
        mMainHandler = WeakReferenceHandler(mContext as MessageHandlerCallback)
        mRecordHistoryContractView = mContext as RecordHistoryContract.View
        mRecordHistoryContractView.initView()
        mRecordHistoryContractView.initFont()

        Log.f("")
        init()
    }

    private fun init()
    {
        Log.f("")
        requestRecordHistory()
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
        mRecordHistoryCoroutine?.cancel()
        mRecordHistoryCoroutine = null
    }

    override fun activityResult(requestCode : Int, resultCode : Int, data : Intent?) { }

    override fun sendMessageEvent(msg : Message)
    {
        when(msg.what)
        {

        }
    }

    private fun setRecordHistoryList()
    {
        Log.f("")
        if (mRecordHistoryListAdapter == null)
        {
            // 초기 생성
            Log.f("mRecordHistoryListAdapter == null")
            mRecordHistoryListAdapter = RecordHistoryListAdapter(mContext)
                .setItemList(mRecordHistoryResult)
                .setHomeworkItemListener(mRecordHistoryItemListener)
            mRecordHistoryContractView.showRecordHistoryListView(mRecordHistoryListAdapter!!)
        }
        else
        {
            // 데이터 변경
            Log.f("mRecordHistoryListAdapter notifyDataSetChanged")
            mRecordHistoryListAdapter!!.setItemList(mRecordHistoryResult)
            mRecordHistoryContractView.showRecordHistoryListView(mRecordHistoryListAdapter!!)
            mRecordHistoryListAdapter!!.notifyDataSetChanged()
        }
    }

    private fun onClickRecordItem(item : RecordHistoryResult)
    {
        if (item.getExpire() > 0) // 기간만료 되지 않은 상태일 때
        {
            showAudioPlayDialog(item)
        }
    }

    /**
     * 녹음파일 재생 다이얼로그
     */
    private fun showAudioPlayDialog(item : RecordHistoryResult)
    {
        // TODO 김태은 녹음파일 재생 다이얼로그 연결하기
//        mAudioPlayDialog = AudioPlayDialog(mContext, item.getTitle(), item.getThumbnailUrl(), item.getMp3Path())
//        mAudioPlayDialog!!.show()
    }

    /**
     * 녹음기록 통신 요청
     */
    private fun requestRecordHistory()
    {
        Log.f("")
        mRecordHistoryContractView.showLoading()
        mRecordHistoryCoroutine = RecordHistoryCoroutine(mContext)
        mRecordHistoryCoroutine!!.asyncListener = mAsyncListener
        mRecordHistoryCoroutine!!.execute()
    }

    /**
     * 통신 이벤트 리스너
     */
    private val mAsyncListener : AsyncListener = object : AsyncListener
    {
        override fun onRunningStart(code : String?) { }

        override fun onRunningEnd(code : String?, mObject : Any?)
        {
            val result : BaseResult = mObject as BaseResult

            Log.f("code : " + code + ", status : " + result.getStatus())

            if(result.getStatus() == BaseResult.SUCCESS_CODE_OK)
            {
                if (code == Common.COROUTINE_CODE_CLASS_RECORD_HISTORY)
                {
                    mRecordHistoryContractView.hideLoading()
                    mRecordHistoryResult = (mObject as RecordHistoryBaseObject).getDate()
                    if (mRecordHistoryResult.size > 0)
                    {
                        setRecordHistoryList()
                    }
                    else
                    {
                        mRecordHistoryContractView.showRecordHistoryEmptyMessage()
                    }
                }
            }
            else
            {
                if(result.isAuthenticationBroken)
                {
                    Log.f("== isAuthenticationBroken ==")
                    (mContext as AppCompatActivity).finish()
                    IntentManagementFactory.getInstance().initScene()
                }
                else
                {
                    if (code == Common.COROUTINE_CODE_CLASS_RECORD_HISTORY)
                    {
                        mRecordHistoryContractView.hideLoading()
                        mRecordHistoryContractView.showErrorMessage(result.getMessage())
                    }
                }
            }
        }

        override fun onRunningCanceled(code : String?) { }

        override fun onRunningProgress(code : String?, progress : Int?) { }

        override fun onRunningAdvanceInformation(code : String?, `object` : Any?) { }

        override fun onErrorListener(code : String?, message : String?) { }
    }

    /**
     * 녹음 기록 리스트 클릭 이벤트 Listener
     */
    private val mRecordHistoryItemListener : RecordItemListener = object : RecordItemListener
    {
        override fun onClickItem(position : Int)
        {
            onClickRecordItem(mRecordHistoryResult[position])
        }
    }

}