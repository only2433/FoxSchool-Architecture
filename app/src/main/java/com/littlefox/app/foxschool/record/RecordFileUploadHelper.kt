package com.littlefox.app.foxschool.record

import android.content.Context
import android.os.Build
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.littlefox.app.foxschool.`object`.data.record.RecordInfoData
import com.littlefox.app.foxschool.`object`.result.login.UserInformationResult
import com.littlefox.app.foxschool.`object`.result.record.RecordFileUploadBaseObject
import com.littlefox.app.foxschool.common.Common
import com.littlefox.app.foxschool.common.CommonUtils
import com.littlefox.app.foxschool.common.Feature
import com.littlefox.app.foxschool.enumerate.DataType
import com.littlefox.library.system.async.listener.AsyncListener
import com.littlefox.logmonitor.Log
import io.github.lizhangqu.coreprogress.ProgressHelper
import io.github.lizhangqu.coreprogress.ProgressListener

import okhttp3.*
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

class RecordFileUploadHelper(private val mContext : Context)
{
    private val mSendFile : File? = null
    private lateinit var mRecordInfoData : RecordInfoData
    private lateinit var mAsyncListener : AsyncListener
    private var mOkHttpClient : OkHttpClient? = null
    fun setAsyncListener(listener : AsyncListener)
    {
        mAsyncListener = listener
    }

    fun setData(vararg objects : Any?) : RecordFileUploadHelper
    {
        mRecordInfoData = objects[0] as RecordInfoData
        return this
    }

    fun build()
    {
        Log.f("")
        val request = uploadFile()
        if(request != null)
        {
            mAsyncListener.onRunningStart(Common.COROUTINE_CODE_CLASS_RECORD_FILE)
            //mOkHttpClient = new OkHttpClient();
            mOkHttpClient = OkHttpClient.Builder().connectTimeout(1, TimeUnit.MINUTES).readTimeout(20, TimeUnit.SECONDS).writeTimeout(20, TimeUnit.SECONDS).build()
            mOkHttpClient?.newCall(request)?.enqueue(object : Callback
            {
                override fun onFailure(call : Call, e : IOException)
                {
                    Log.f("업로드 실패 : " + e.message)
                    mAsyncListener.onErrorListener(Common.COROUTINE_CODE_CLASS_RECORD_FILE, e.message)
                }

                @Throws(IOException::class)
                override fun onResponse(call : Call, response : Response)
                {
                    val gson : Gson = GsonBuilder().create()
                    var result : RecordFileUploadBaseObject? = null
                    result = try
                    {
                        gson.fromJson(response.body()!!.string(), RecordFileUploadBaseObject::class.java)
                    }
                    catch(e : Exception)
                    {
                        Log.f("데이터 파싱 실패 : " + e.message)
                        mAsyncListener.onErrorListener(Common.COROUTINE_CODE_CLASS_RECORD_FILE, e.message)
                        return
                    }
                    try
                    {
                        if(result?.getAccessToken().equals("") === false)
                        {
                            CommonUtils.getInstance(mContext).setSharedPreference(Common.PARAMS_ACCESS_TOKEN, result?.getAccessToken().toString())
                        }
                    }
                    catch(e : NullPointerException)
                    {
                        Log.f("result data 실패 : " + e.message)
                        mAsyncListener.onErrorListener(Common.COROUTINE_CODE_CLASS_RECORD_FILE, e.message)
                        return
                    }
                    mAsyncListener.onRunningEnd(Common.COROUTINE_CODE_CLASS_RECORD_FILE, result)
                }
            })
        }
    }

    private fun uploadFile() : Request?
    {
        Log.f("")
        val audioFile : File = File(mRecordInfoData.getFilePath() + mRecordInfoData.getFileName())
        val request : Request
        val requestBody : RequestBody
        val deviceType : String = if(Feature.IS_TABLET) Common.DEVICE_TYPE_TABLET else Common.DEVICE_TYPE_PHONE
        val token = "Bearer " + CommonUtils.getInstance(mContext).getSharedPreference(Common.PARAMS_ACCESS_TOKEN, DataType.TYPE_STRING) as String
        val userAgent : String = Common.HTTP_HEADER_APP_NAME.toString() + ":" + deviceType + File.separator + CommonUtils.getInstance(mContext).getPackageVersionName(Common.PACKAGE_NAME) + File.separator + Build.MODEL + File.separator + Common.HTTP_HEADER_ANDROID + ":" + Build.VERSION.RELEASE
        val userInformationResult : UserInformationResult = CommonUtils.getInstance(mContext).getPreferenceObject(Common.PARAMS_USER_API_INFORMATION, UserInformationResult::class.java) as UserInformationResult
        if(audioFile.exists())
        {
            Log.f("파일 있음")
            requestBody = MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("file", mRecordInfoData.getFileName(), RequestBody.create(MediaType.parse("application/zip"), audioFile)).addFormDataPart("file_count", java.lang.String.valueOf(mRecordInfoData.getItemCount())).addFormDataPart("class_id", java.lang.String.valueOf(mRecordInfoData.getClassID())).addFormDataPart("fc_id", mRecordInfoData.getContentsID()).addFormDataPart("fu_id", userInformationResult.getCurrentUserID()).addFormDataPart("index_of_day", java.lang.String.valueOf(mRecordInfoData.getIndexOfDay())).build()
            val requestBodyData : RequestBody = ProgressHelper.withProgress(requestBody, object : ProgressListener()
            {
                override fun onProgressChanged(numBytes : Long, totalBytes : Long, percent : Float, speed : Float)
                {
                    Log.f("numBytes : $numBytes, totalBytes : $totalBytes, percent : $percent")
                    val percentData = numBytes.toFloat() / totalBytes.toFloat() * 100.0f
                    mAsyncListener.onRunningProgress(Common.COROUTINE_CODE_CLASS_RECORD_FILE, percentData.toInt())
                }
            })

            if(Feature.IS_FREE_USER === false)
            {
                request = Request.Builder().addHeader("api-user-agent", userAgent).addHeader("Authorization", token).url(Common.API_CLASS_RECORD_UPLOAD).post(requestBodyData).build()
            }
            else
            {
                request = Request.Builder().addHeader("api-user-agent", userAgent).url(Common.API_CLASS_RECORD_UPLOAD).post(requestBody).build()
            }
            return request
        }
        else
        {
            mAsyncListener.onErrorListener(Common.COROUTINE_CODE_CLASS_RECORD_FILE, "보내는 파일 못찾음.")
        }
        return null
    }
}