package com.littlefox.app.foxschool.api.viewmodel.factory

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import com.littlefox.app.foxschool.R
import com.littlefox.app.foxschool.adapter.SearchListItemPagingAdapter
import com.littlefox.app.foxschool.adapter.listener.SearchItemListener
import com.littlefox.app.foxschool.api.base.BaseFactoryViewModel
import com.littlefox.app.foxschool.api.enumerate.RequestCode
import com.littlefox.app.foxschool.api.viewmodel.api.SearchApiViewModel
import com.littlefox.app.foxschool.common.Common
import com.littlefox.app.foxschool.common.CommonUtils


import com.littlefox.app.foxschool.management.IntentManagementFactory

import com.littlefox.app.foxschool.`object`.result.content.ContentsBaseResult
import com.littlefox.app.foxschool.`object`.result.main.MainInformationResult
import com.littlefox.app.foxschool.`object`.result.main.MyBookshelfResult
import com.littlefox.app.foxschool.`object`.result.search.SearchListResult
import com.littlefox.app.foxschool.observer.MainObserver
import com.littlefox.app.foxschool.viewmodel.SingleLiveEvent
import com.littlefox.library.system.handler.WeakReferenceHandler
import com.littlefox.logmonitor.Log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject
import java.util.ArrayList

@HiltViewModel
class SearchFactoryViewModel @Inject constructor(private val apiViewModel : SearchApiViewModel) : BaseFactoryViewModel()
{
    private val _showSearchListView = SingleLiveEvent<SearchListItemPagingAdapter>()
    val showSearchListView : LiveData<SearchListItemPagingAdapter> = _showSearchListView

    private val _showContentsLoading = SingleLiveEvent<Void>()
    val showContentsLoading : LiveData<Void> = _showContentsLoading

    private val _hideContentsLoading = SingleLiveEvent<Void>()
    val hideContentsLoading : LiveData<Void> = _hideContentsLoading

    private val _enableRefreshLoading = SingleLiveEvent<Boolean>()
    val enableRefreshLoading : LiveData<Boolean> = _enableRefreshLoading

    private val _dialogBottomOption = SingleLiveEvent <ContentsBaseResult>()
    val dialogBottomOption: LiveData<ContentsBaseResult> get() = _dialogBottomOption

    private val _dialogBottomBookshelfContentAdd = SingleLiveEvent<ArrayList<MyBookshelfResult>>()
    val dialogBottomBookshelfContentAdd: LiveData<ArrayList<MyBookshelfResult>> get() = _dialogBottomBookshelfContentAdd

    private val _dialogRecordPermission = SingleLiveEvent<Void>()
    val dialogRecordPermission: LiveData<Void> get() = _dialogRecordPermission

    companion object
    {
        const val DIALOG_TYPE_WARNING_RECORD_PERMISSION : Int   = 10001
    }


    private lateinit var mContext : Context

    private var mCurrentSearchListBaseResult : SearchListResult? = null
    private val mSearchItemList : ArrayList<ContentsBaseResult> = ArrayList<ContentsBaseResult>()
    private var mCurrentBookshelfAddResult : MyBookshelfResult? = null

    /**
     * 검색의 타입. ALL = "" , Stories = S , Songs = M
     */
    private var mCurrentSearchType : String = Common.CONTENT_TYPE_ALL
    private var mCurrentKeyword = ""
    private var mRequestPagePosition = 1
    private var mSearchListItemAdapter : SearchListItemPagingAdapter? = null

    private lateinit var mMainInformationResult : MainInformationResult
    private lateinit var mMainHandler : WeakReferenceHandler
    private val mSendBookshelfAddList : ArrayList<ContentsBaseResult> = ArrayList<ContentsBaseResult>()
    private var mCurrentSelectItem: ContentsBaseResult? = null
    private var mJob: Job? = null
    override fun init(context : Context)
    {
        mContext = context
        Log.f("onCreate")
        init()
        setupViewModelObserver()
    }

    private fun init()
    {
        Log.f("")
        mMainInformationResult = CommonUtils.getInstance(mContext).loadMainData()
        initRecyclerView()
    }

    override fun setupViewModelObserver()
    {
        (mContext as AppCompatActivity).lifecycleScope.launchWhenResumed {
            apiViewModel.isLoading.collect {data ->
                data?.let {
                    if(data.first == RequestCode.CODE_BOOKSHELF_CONTENTS_ADD)
                    {
                        if(data.second)
                        {
                            _isLoading.postValue(true)
                        }
                        else
                        {
                            _isLoading.postValue(false)
                        }
                    }
                }
            }
        }

        (mContext as AppCompatActivity).lifecycleScope.launchWhenResumed {
            apiViewModel.addBookshelfContentsData.collect{ data ->
                data?.let {
                    updateBookshelfData(data)
                    viewModelScope.launch(Dispatchers.Main){
                        withContext(Dispatchers.IO){
                            delay(Common.DURATION_NORMAL)
                        }
                        _successMessage.value = mContext.resources.getString(R.string.message_success_save_contents_in_bookshelf)
                    }
                }
            }
        }

        (mContext as AppCompatActivity).lifecycleScope.launchWhenResumed {
            apiViewModel.errorReport.collect{ data ->
                data?.let {
                    val result = data.first
                    val code = data.second

                    Log.f("status : ${result.status}, message : ${result.message} , code : $code")
                    if(result.isDuplicateLogin)
                    {
                        //중복 로그인 시 재시작
                        (mContext as AppCompatActivity).finish()
                        Toast.makeText(mContext, result.message, Toast.LENGTH_LONG).show()
                        IntentManagementFactory.getInstance().initAutoIntroSequence()
                    }
                    else if(result.isAuthenticationBroken)
                    {
                        Log.f("== isAuthenticationBroken ==")
                        (mContext as AppCompatActivity).finish()
                        Toast.makeText(mContext, result.message, Toast.LENGTH_LONG).show()
                        IntentManagementFactory.getInstance().initScene()
                    }
                    else
                    {
                        _toast.value = result.message
                    }
                }
            }
        }
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
    }

    private fun initRecyclerView()
    {
        mSearchListItemAdapter = SearchListItemPagingAdapter(mContext)
            .setDetailItemListener(mSearchItemListener)
        mSearchListItemAdapter?.addLoadStateListener { state ->

            if(state.refresh is LoadState.Loading ||
                state.append is LoadState.Loading ||
                state.prepend is LoadState.Loading)
            {
                _enableRefreshLoading.value = true
            }
            else
            {
                _enableRefreshLoading.value = false
            }

            val error = when {
                state.prepend is LoadState.Error -> state.prepend as LoadState.Error
                state.append is LoadState.Error -> state.append as LoadState.Error
                state.refresh is LoadState.Error -> state.refresh as LoadState.Error
                else -> null
            }
            error?.let {
                _toast.value = it.error.message
            }
        }
        _showSearchListView.value = mSearchListItemAdapter!!
    }

    private fun clearData()
    {
        mRequestPagePosition = 1
        mCurrentSearchListBaseResult = null
        mCurrentKeyword = ""
        mSearchItemList.clear()
        mSearchListItemAdapter?.notifyDataSetChanged()
    }

    private fun updateBookshelfData(result : MyBookshelfResult)
    {
        for(i in mMainInformationResult.getBookShelvesList().indices)
        {
            if(mMainInformationResult.getBookShelvesList()[i].getID() == result.getID())
            {
                Log.f("update Index :$i")
                mMainInformationResult.getBookShelvesList()[i] = result
            }
        }
        CommonUtils.getInstance(mContext).saveMainData(mMainInformationResult)
        MainObserver.updatePage(Common.PAGE_MY_BOOKS)
    }

    private fun requestBookshelfContentsAddAsync(data : ArrayList<ContentsBaseResult>)
    {
        apiViewModel.enqueueCommandStart(
            RequestCode.CODE_BOOKSHELF_CONTENTS_ADD,
            mCurrentBookshelfAddResult!!.getID(),
            data
        )
    }

    private fun getSearchDataList()
    {
        Log.f("mCurrentKeyword : $mCurrentKeyword")
        Log.f("position : $mRequestPagePosition, searchType : $mCurrentSearchType")
        mJob?.cancel()
        mJob = viewModelScope.launch {

            apiViewModel.getPagingData(mCurrentSearchType, mCurrentKeyword).collectLatest { data ->
                mSearchListItemAdapter?.submitData(data)
            }
        }
    }

    fun onClickSearchType(type: String)
    {
        if(mCurrentSearchType == type)
        {
            return
        }
        Log.f("type : $type")
        mCurrentSearchType = type

        if(mCurrentKeyword != "")
        {
            mRequestPagePosition = 1
            mCurrentSearchListBaseResult = null
            mSearchItemList.clear()
            if(mSearchListItemAdapter != null)
            {
                mSearchListItemAdapter!!.notifyDataSetChanged()
            }
            getSearchDataList()
        }
    }

    fun onClickSearchExecute(keyword: String)
    {
        Log.f("keyword : $keyword")
        if(keyword.trim().length < 2)
        {
            _errorMessage.value = mContext.resources.getString(R.string.message_warning_search_input_2_or_more)
            return
        }
        clearData()
        mCurrentKeyword = keyword
        getSearchDataList()
    }

    fun onClickQuizButton()
    {
        Log.f("")
    }

    fun onClickTranslateButton()
    {
        Log.f("")
    }

    fun onClickVocabularyButton()
    {
        Log.f("")
    }

    fun onClickAddBookshelfButton()
    {
        Log.f("")
    }

     fun onClickEbookButton()
    {
        Log.f("")
    }

    fun onClickStarwordsButton()
    {
        Log.f("")
    }

    fun onClickCrosswordButton()
    {
        Log.f("")
    }

    fun onClickFlashcardButton()
    {
        Log.f("")
    }

    fun onClickRecordPlayerButton()
    {
        Log.f("")
        if (CommonUtils.getInstance(mContext).checkRecordPermission() == false)
        {
            _dialogRecordPermission.call()
        }
        else
        {
        }
    }

    fun onDialogAddBookshelfClick(index : Int)
    {
    }


    private val mSearchItemListener = object : SearchItemListener
    {
        override fun onItemClickThumbnail(item : ContentsBaseResult)
        {
            Log.f("index : ${item.getID()}")
        }

        override fun onItemClickOption(item : ContentsBaseResult)
        {
            Log.f("index : ${item.getID()}")
        }
    }

}