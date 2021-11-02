package com.littlefox.app.foxschool.coroutine

import android.content.ContentValues
import android.content.Context
import com.google.gson.Gson
import com.littlefox.app.foxschool.`object`.result.base.BaseResult
import com.littlefox.app.foxschool.common.Common
import com.littlefox.app.foxschool.common.CommonUtils
import com.littlefox.app.foxschool.common.NetworkUtil
import com.littlefox.library.system.coroutine.BaseCoroutine

class StudentCommentRegisterCoroutine : BaseCoroutine
{
    private var mComment : String = ""
    private var mHomeworkNumber : Int = 0
    constructor(context : Context) : super(context, Common.COROUTINE_CODE_STUDENT_COMMENT_REGISTER)

    override fun doInBackground() : Any?
    {
        if(isRunning == true)
        {
            return null
        }

        lateinit var result : BaseResult
        synchronized(mSync)
        {
            isRunning = true
            val list = ContentValues()
            list.put("comment", mComment)
            list.put("hw_no", mHomeworkNumber)

            val response = NetworkUtil.requestServerPair(
                mContext,
                Common.API_STUDENT_HOMEWORK,
                list,
                NetworkUtil.PUT_METHOD
            )

            result = Gson().fromJson(response, BaseResult::class.java)
            if(result.getAccessToken() != "")
            {
                CommonUtils.getInstance(mContext).setSharedPreference(Common.PARAMS_ACCESS_TOKEN, result.getAccessToken())
            }
        }

        return result
    }

    override fun setData(vararg objects : Any?)
    {
        mComment = objects[0] as String
        mHomeworkNumber = objects[1] as Int
    }

}