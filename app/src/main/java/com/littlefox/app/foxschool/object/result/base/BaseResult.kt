package com.littlefox.app.foxschool.`object`.result.base

import com.google.gson.annotations.SerializedName
import com.littlefox.logmonitor.Log

open class BaseResult
{
    companion object
    {
        const val FAIL_CODE_NETWORK_NOT_CONNECT                     = 105
        const val FAIL_CODE_USER_AUTHORIZATION_NO_TAKEN             = 401
        const val FAIL_CODE_USER_AUTHORIZATION_INVALID_TOKEN        = 4011
        const val FAIL_CODE_USER_PAID_CHANGE                        = 450
        const val FAIL_CODE_USER_EXPIRE_USER                        = 451
        const val FAIL_CODE_USER_DUPLICATE_LOGIN                    = 452
        const val FAIL_CODE_INTERNAL_SERVER_ERROR                   = 500
        const val SUCCESS_CODE_OK                                   = 200
    }

    @SerializedName("status")
    private var status : Int = -1

    @SerializedName("message")
    private var message = ""

    @SerializedName("access_token")
    private var access_token : String = ""

    val isSuccess: Boolean
        get()
        {
            if(getStatus() == SUCCESS_CODE_OK)
            {
                return true
            }
            else
            {
                return false
            }
        }

    val isNetworkError : Boolean
        get()
        {
            if(getStatus() == FAIL_CODE_NETWORK_NOT_CONNECT)
            {
                return true
            }
            else
                return false
        }


    /**
     * Access Token이 없거나 다른 사용자가 사용시 TRUE . 아닐경우 FALSE
     * @return Access Token이 없거나 다른 사용자가 사용시 TRUE . 아닐경우 FALSE
     */
    val isAuthenticationBroken : Boolean
        get()
        {
            if(getStatus() == FAIL_CODE_USER_AUTHORIZATION_INVALID_TOKEN
                    || getStatus() == FAIL_CODE_USER_AUTHORIZATION_NO_TAKEN
                    || getStatus() == FAIL_CODE_USER_EXPIRE_USER
                    || getStatus() == FAIL_CODE_USER_PAID_CHANGE
                    || getStatus() == FAIL_CODE_USER_DUPLICATE_LOGIN)
            {
                Log.f("Access Token 을 사용할 수 없다.")
                return true
            }
            else
            {
                return false
            }
        }

    /**
     * 중복 로그인 상태인지 여부
     * @return TRUE : 중복, FALSE : 중복 아님.
     */
    val isDuplicateLogin : Boolean
        get()
        {
            if(getStatus() == FAIL_CODE_USER_DUPLICATE_LOGIN)
            {
                return true
            }
            else
                return false
        }

    fun getStatus() : Int = status

    fun getMessage() : String = message

    fun getAccessToken() : String = access_token

}