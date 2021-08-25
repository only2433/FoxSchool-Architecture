package com.littlefox.app.foxschool.`object`.result.content

import com.littlefox.app.foxschool.common.Common
import java.io.Serializable

class ServiceSupportedTypeResult : Serializable
{
    private var story : String              = Common.SERVICE_NOT_SUPPORTED
    private var service : String            = Common.SERVICE_NOT_SUPPORTED
    private var original_text : String      = Common.SERVICE_NOT_SUPPORTED
    private var vocabulary : String         = Common.SERVICE_NOT_SUPPORTED
    private var quiz : String               = Common.SERVICE_NOT_SUPPORTED
    private var ebook : String              = Common.SERVICE_NOT_SUPPORTED
    private var crossword : String          = Common.SERVICE_NOT_SUPPORTED
    private var starwords : String          = Common.SERVICE_NOT_SUPPORTED
    private var flash_card : String         = Common.SERVICE_NOT_SUPPORTED
    private var recorder : String           = Common.SERVICE_SUPPORTED_FREE // TODO 김태은 테스트용

    fun getStorySupportType() : String
    {
        return story;
    }

    fun getServiceSupportType() : String
    {
        return service;
    }

    fun getOriginalTextSupportType() : String
    {
        return original_text;
    }

    fun getVocabularySupportType() : String
    {
        return vocabulary;
    }

    fun getQuizSupportType() : String
    {
        return quiz;
    }

    fun getEbookSupportType() : String
    {
        return ebook;
    }

    fun getCrosswordSupportType() : String
    {
        return crossword;
    }

    fun getStarwordsSupportType() : String
    {
        return starwords;
    }

    fun getFlashcardSupportType() : String
    {
        return flash_card
    }

    fun getRecorderSupportType() : String
    {
        return recorder
    }
}