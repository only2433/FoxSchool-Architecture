package com.littlefox.app.foxschool.`object`.data.quiz

import android.os.Parcel
import android.os.Parcelable

class QuizIntentParamsObject : Parcelable
{
    private var mContentID : String = ""
    private var mHomeworkNumber : Int = 0

    protected constructor(`in` : Parcel)
    {
        mContentID = `in`.readString()!!
        mHomeworkNumber = `in`.readInt()!!
    }

    constructor(contentID : String)
    {
        mContentID = contentID
        mHomeworkNumber = 0
    }

    constructor(contentID : String, homeworkNumber : Int) : this(contentID)
    {
        mHomeworkNumber = homeworkNumber
    }

    fun getContentID() : String = mContentID

    fun getHomeworkNumber() : Int = mHomeworkNumber


    override fun describeContents() : Int
    {
        return 0
    }

    override fun writeToParcel(dest : Parcel, flags : Int)
    {
        dest.writeString(mContentID)
        dest.writeInt(mHomeworkNumber)
    }

    companion object CREATOR : Parcelable.Creator<QuizIntentParamsObject>
    {
        override fun createFromParcel(parcel : Parcel) : QuizIntentParamsObject
        {
            return QuizIntentParamsObject(parcel)
        }

        override fun newArray(size : Int) : Array<QuizIntentParamsObject?>
        {
            return arrayOfNulls(size)
        }
    }
}