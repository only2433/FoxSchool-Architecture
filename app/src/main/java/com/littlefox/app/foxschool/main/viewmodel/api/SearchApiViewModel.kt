package com.littlefox.app.foxschool.main.viewmodel.api

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.littlefox.app.foxschool.main.viewmodel.base.BaseApiViewModel
import com.littlefox.app.foxschool.api.data.QueueData
import com.littlefox.app.foxschool.api.data.ResultData
import com.littlefox.app.foxschool.api.di.FoxSchoolRepository
import com.littlefox.app.foxschool.api.enumerate.RequestCode
import com.littlefox.app.foxschool.`object`.result.content.ContentsBaseResult
import com.littlefox.app.foxschool.`object`.result.main.MyBookshelfResult
import com.littlefox.app.foxschool.`object`.result.search.paging.ContentBasePagingResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.ArrayList
import javax.inject.Inject

@HiltViewModel
class SearchApiViewModel @Inject constructor(private val repository : FoxSchoolRepository) : BaseApiViewModel()
{
    private val _addBookshelfContentsData = MutableStateFlow<MyBookshelfResult?>(null)
    val addBookshelfContentsData: MutableStateFlow<MyBookshelfResult?> = _addBookshelfContentsData

    private suspend fun addBookshelfContents(bookshelfID: String, contentsList: ArrayList<ContentsBaseResult>)
    {
        val result = repository.addBookshelfContents(bookshelfID, contentsList)
        withContext(Dispatchers.Main)
        {
            when(result)
            {
                is ResultData.Success ->
                {
                    val data = result.data as MyBookshelfResult
                    _addBookshelfContentsData.value = data
                }
                is ResultData.Fail ->
                {
                    _errorReport.value = Pair(result, RequestCode.CODE_BOOKSHELF_CONTENTS_ADD)
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
            RequestCode.CODE_BOOKSHELF_CONTENTS_ADD ->
            {
                mJob = viewModelScope.launch(Dispatchers.IO) {
                    delay(data.duration)
                    addBookshelfContents(
                        data.objects[0] as String,
                        data.objects[1] as ArrayList<ContentsBaseResult>
                    )
                }
            }
        }
    }

    fun getPagingData(searchType : String = "", keyword : String) : Flow<PagingData<ContentBasePagingResult>>
    {
        val result : Flow<PagingData<ContentBasePagingResult>> = repository.getSearchListStream(searchType, keyword).cachedIn(viewModelScope)
        return result
    }

}