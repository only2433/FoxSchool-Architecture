package com.littlefox.app.foxschool.api.data

import com.littlefox.app.foxschool.api.enumerate.RequestCode

class QueueData(
    val requestCode : RequestCode,
    val duration : Long = 0L,
    vararg val objects : Any?)
{

}
