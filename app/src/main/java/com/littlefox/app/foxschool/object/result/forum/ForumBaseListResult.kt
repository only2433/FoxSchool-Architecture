package com.littlefox.app.foxschool.`object`.result.forum

import java.util.ArrayList

class ForumBaseListResult
{
    private val isnew : String = ""
    private val list : ArrayList<ForumBaseResult> = ArrayList<ForumBaseResult>()
    private val meta : MetaDataResult? = null

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

    fun getNewsList() : List<ForumBaseResult> = list
}