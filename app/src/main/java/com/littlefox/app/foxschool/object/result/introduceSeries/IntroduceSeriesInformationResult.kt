package com.littlefox.app.foxschool.`object`.result.introduceSeries

import java.util.ArrayList

class IntroduceSeriesInformationResult
{
    private var id : String = ""
    private var name : String = ""
    private var level : Int = -1
    private var introduction : String = ""
    private var contents_count : Int = -1
    private var total_count : Int = -1
    private var categories : String = ""
    private var is_single : String = ""
    private var mp4_url : String = ""
    private var m3u8_url : String = ""
    private var introduce_thumbnail_url : String = ""
    private var characters : ArrayList<IntroduceSeriesCharacterResult> = ArrayList<IntroduceSeriesCharacterResult>()
    private var creators : IntroduceSeriesCreatorsResult? = null

    fun getSeriesID() : String = id

    fun getTitle() : String = name

    fun getLevel() : Int = level

    fun getIntroduction() : String = introduction

    fun getSchoolContentsCount() : Int = contents_count

    fun getTotalCount() : Int = total_count

    fun getCategories() : String = categories

    fun getCharacterInformationList() : ArrayList<IntroduceSeriesCharacterResult> = characters

    fun getCreatorInformation() : IntroduceSeriesCreatorsResult? = creators

    fun getIntroduceVideoMp4() : String = mp4_url

    fun getIntroduceVideoHls() : String = m3u8_url

    fun getIntroduceThumbnail() : String = introduce_thumbnail_url

    val isSingleSeries : Boolean
        get()
        {
            if(is_single == "")
            {
                return false
            }
            return if(is_single == "Y") true else false
        }
}