package com.littlefox.app.foxschool.api.base

import com.littlefox.app.foxschool.`object`.result.base.BaseResult
import com.littlefox.app.foxschool.api.data.ResultData
import com.littlefox.app.foxschool.base.MainApplication
import com.littlefox.app.foxschool.common.Common
import com.littlefox.logmonitor.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

suspend fun <T : Any> safeApiCall(call: suspend () -> Response<T>) : Flow<ResultData<T>?> = flow {
    var result: ResultData<T>? = null
    try
    {
        val response = call.invoke()
        if(response.isSuccessful)
        {
            when(val temp = response.body())
            {
                is BaseResponse<*> ->
                {
                    if(temp.isSuccess)
                    {
                        if (temp.data != null)
                        {
                            result = ResultData.Success(temp.data as T)
                        }
                        else
                        {
                            result = ResultData.Success(temp)
                        }
                    }
                    else
                    {
                        result = ResultData.Fail(temp.status, temp.message)
                    }

                    if(temp.access_token.equals("") == false)
                    {
                        Log.f("temp.access_token : ${temp.access_token}")
                        MainApplication.instance.setUserToken(temp.access_token!!)
                    }
                }
            }
        }
        else
        {
            result = ResultData.Fail(response.code(),response.message())
        }

    }
    catch (e: HttpException)
    {
        e.printStackTrace()
        result = ResultData.Fail(Common.FAIL_CODE_NETWORK_NOT_CONNECT,"Http Internet error")
    }
    catch (e: SocketTimeoutException)
    {
        result = ResultData.Fail(Common.FAIL_CODE_INTERNAL_SERVER_ERROR,"요청한 시간이 초과되었습니다.", )
    }
    catch (e: UnknownHostException)
    {
        result = ResultData.Fail(Common.FAIL_CODE_NETWORK_NOT_CONNECT,"네트워크에 연결할 수 없습니다.\n" +
                "네트워크 상태 확인 후 다시 시도해 주세요", )
    }
    catch (e: Exception)
    {
        e.printStackTrace()
        result = ResultData.Fail(Common.FAIL_CODE_INTERNAL_SERVER_ERROR,e.message ?: "Internet error runs")
    }
    emit(result)
}.flowOn(Dispatchers.IO)


fun getNetworkErrorJson() : String
{
    val data = JSONObject()
    try
    {
        data.put("status", BaseResult.FAIL_CODE_NETWORK_NOT_CONNECT)
        data.put("message", "네트워크에 접속할 수 없습니다.\n네트워크 연결 상태를 확인해 주세요.")
    }
    catch(e : JSONException)
    {
        e.printStackTrace()
    }
    return data.toString()
}