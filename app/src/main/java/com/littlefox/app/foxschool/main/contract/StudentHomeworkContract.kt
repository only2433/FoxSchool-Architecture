package com.littlefox.app.foxschool.main.contract

import com.littlefox.app.foxschool.adapter.HomeworkPagerAdapter
import com.littlefox.app.foxschool.enumerate.HomeworkCommentType
import com.littlefox.app.foxschool.main.contract.base.BaseContract

class StudentHomeworkContract
{
    interface View : BaseContract.View
    {
        fun initViewPager(mHomeworkPagerAdapter : HomeworkPagerAdapter)
        fun setCurrentViewPage(position : Int, commentType : HomeworkCommentType? = null)
    }

    interface Presenter : BaseContract.Presenter
    {
        fun onClickBackButton()
        fun onPageChanged(position : Int)
    }
}