package com.littlefox.app.foxschool.`object`.result

import com.littlefox.app.foxschool.`object`.result.base.BaseResult
import com.littlefox.app.foxschool.`object`.result.homework.HomeworkCalendarBaseResult

class HomeworkCalenderBaseObject : BaseResult()
{
    private val data : HomeworkCalendarBaseResult? = null

    fun getData() : HomeworkCalendarBaseResult = data!!
}
