package com.littlefox.app.foxschool.main.viewmodel.base

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.littlefox.app.foxschool.enumerate.DialogButtonType
import com.littlefox.app.foxschool.enumerate.ResultLauncherCode

abstract class BaseFactoryViewModel : ViewModel()
{

    protected val _toast = MutableLiveData<String>()
    val toast: LiveData<String> get() = _toast

    protected val _successMessage = MutableLiveData<String>()
    val successMessage: LiveData<String> get() = _successMessage

    protected val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    protected val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading


    abstract fun setupViewModelObserver()
    abstract fun resume()
    abstract fun pause()
    abstract fun destroy()


    open fun init(context: Context){}
    open fun onAddResultLaunchers(vararg launchers : ActivityResultLauncher<Intent?>?){}
    open fun onRequestPermissionsResult(requestCode : Int, permissions : Array<out String>, grantResults : IntArray){}
    open fun onActivityResult(code : ResultLauncherCode = ResultLauncherCode.DEFAULT, intent : Intent? = null){}
    open fun onDialogClick(eventType : Int){}
    open fun onDialogChoiceClick(buttonType : DialogButtonType, eventType: Int){}
}