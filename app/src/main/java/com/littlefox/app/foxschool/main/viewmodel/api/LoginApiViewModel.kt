package com.littlefox.app.foxschool.main.viewmodel.api

import androidx.lifecycle.viewModelScope
import com.littlefox.app.foxschool.`object`.result.login.LoginInformationResult
import com.littlefox.app.foxschool.`object`.result.login.SchoolItemDataResult
import com.littlefox.app.foxschool.main.viewmodel.base.BaseApiViewModel
import com.littlefox.app.foxschool.api.base.BaseResponse
import com.littlefox.app.foxschool.api.data.QueueData
import com.littlefox.app.foxschool.api.data.ResultData
import com.littlefox.app.foxschool.api.di.FoxSchoolRepository
import com.littlefox.app.foxschool.api.enumerate.RequestCode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class LoginApiViewModel @Inject constructor(private val repository : FoxSchoolRepository) : BaseApiViewModel()
{
    private val _schoolListData = MutableStateFlow<ArrayList<SchoolItemDataResult>?>(null)
    val schoolListData : MutableStateFlow<ArrayList<SchoolItemDataResult>?> = _schoolListData

    private val _loginData = MutableStateFlow<LoginInformationResult?>(null)
    val loginData : MutableStateFlow<LoginInformationResult?> = _loginData

    private val _changePasswordData = MutableStateFlow<BaseResponse<Nothing>?>(null)
    val changePasswordData : MutableStateFlow<BaseResponse<Nothing>?> = _changePasswordData

    private val _changePasswordNextData = MutableStateFlow<BaseResponse<Nothing>?>(null)
    val changePasswordNextData : MutableStateFlow<BaseResponse<Nothing>?> = _changePasswordNextData

    private val _changePasswordKeepData = MutableStateFlow<BaseResponse<Nothing>?>(null)
    val changePasswordKeepData : MutableStateFlow<BaseResponse<Nothing>?> = _changePasswordKeepData

    private suspend fun getSchoolList()
    {
        val result = repository.getSchoolList()
        withContext(Dispatchers.Main)
        {
            when(result)
            {
                is ResultData.Success ->
                {
                    val data = result.data as ArrayList<SchoolItemDataResult>
                    _schoolListData.value = data
                }
                is ResultData.Fail ->
                {
                    _errorReport.value = Pair(result, RequestCode.CODE_SCHOOL_LIST)
                }
            }
        }
        enqueueCommandEnd()
    }

    private suspend fun login(id : String, password : String, schoolCode : String)
    {
        val result = repository.login(id, password, schoolCode)
        withContext(Dispatchers.Main)
        {
            when(result)
            {
                is ResultData.Success ->
                {
                    val data = result.data as LoginInformationResult
                    _loginData.value = data
                }
                is ResultData.Fail ->
                {
                    _errorReport.value = Pair(result, RequestCode.CODE_LOGIN)
                }
            }
        }
        enqueueCommandEnd()
    }

    private suspend fun changePassword(currentPassword: String, changePassword: String, changePasswordConfirm: String)
    {
        val result = repository.setChangePassword(currentPassword, changePassword, changePasswordConfirm)
        withContext(Dispatchers.Main)
        {
            when(result)
            {
                is ResultData.Success ->
                {
                    val data = result.data as BaseResponse
                    _changePasswordData.value = data
                }
                is ResultData.Fail ->
                {
                    _errorReport.value = Pair(result, RequestCode.CODE_PASSWORD_CHANGE)
                }
            }
        }
        enqueueCommandEnd()
    }

    private suspend fun changePasswordToDoNext()
    {
        val result = repository.setChangePasswordToDoNext()
        withContext(Dispatchers.Main)
        {
            when(result)
            {
                is ResultData.Success ->
                {
                    val data = result.data as BaseResponse
                    _changePasswordNextData.value = data
                }
                is ResultData.Fail ->
                {
                    _errorReport.value = Pair(result, RequestCode.CODE_PASSWORD_CHANGE_NEXT)
                }
            }
        }
        enqueueCommandEnd()

    }

    private suspend fun changePasswordToKeep()
    {
        val result = repository.setChangePasswordToKeep()
        withContext(Dispatchers.Main)
        {
            when(result)
            {
                is ResultData.Success ->
                {
                    val data = result.data as BaseResponse
                    _changePasswordKeepData.value = data
                }
                is ResultData.Fail ->
                {
                    _errorReport.value = Pair(result, RequestCode.CODE_PASSWORD_CHANGE_KEEP)
                }
            }
        }
        enqueueCommandEnd()
    }

    override fun pullNext(data : QueueData)
    {
        super.pullNext(data)

        when(data.requestCode)
        {
            RequestCode.CODE_SCHOOL_LIST ->
            {
                mJob = viewModelScope.launch (Dispatchers.IO) {
                    delay(data.duration)
                    getSchoolList()
                }
            }
            RequestCode.CODE_LOGIN ->
            {
                mJob = viewModelScope.launch (Dispatchers.IO) {
                    delay(data.duration)
                    login(
                        data.objects[0] as String,
                        data.objects[1] as String,
                        data.objects[2] as String
                    )
                }
            }
            RequestCode.CODE_PASSWORD_CHANGE ->
            {
                mJob = viewModelScope.launch (Dispatchers.IO) {
                    delay(data.duration)
                    changePassword(
                        data.objects[0] as String,
                        data.objects[1] as String,
                        data.objects[2] as String
                    )
                }
            }
            RequestCode.CODE_PASSWORD_CHANGE_NEXT ->
            {
                mJob = viewModelScope.launch (Dispatchers.IO) {
                    delay(data.duration)
                    changePasswordToDoNext()
                }
            }
            RequestCode.CODE_PASSWORD_CHANGE_KEEP ->
            {
                mJob = viewModelScope.launch (Dispatchers.IO) {
                    delay(data.duration)
                    changePasswordToKeep()
                }
            }
        }
    }
}