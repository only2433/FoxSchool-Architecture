package com.littlefox.app.foxschool.`object`.result.forum.paging

import com.google.gson.annotations.SerializedName
import com.littlefox.app.foxschool.`object`.result.common.MetaDataPagingResult
import java.util.ArrayList

data class ForumBaseListPagingResult(
    @SerializedName("isnew")
    val isnew : String = "",

    @SerializedName("list")
    val list : ArrayList<ForumBasePagingResult> = ArrayList<ForumBasePagingResult>(),

    @SerializedName("meta")
    val meta : MetaDataPagingResult? = null
)
{
    fun getIsNew() : String = isnew

    val currentPageIndex : Int
        get()
        {
            if(meta != null)
            {
                return meta.current_page
            }
            else
                return 0
        }

    val lastPageIndex : Int
        get()
        {
            if(meta != null)
            {
                return meta.last_page
            }
            else
                return 0
        }

    val isLastPage : Boolean
        get()
        {
            if(meta != null)
            {
                if(meta.current_page === meta.last_page)
                {
                    return true
                }
            }
            return false
        }

    val totalItemCount : Int
        get()
        {
            if(meta != null)
            {
                return meta.total
            }
            else
                return 0
        }

    fun getNewsList() : List<ForumBasePagingResult> = list
}