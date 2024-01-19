package com.littlefox.app.foxschool.adapter

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.littlefox.app.foxschool.R
import com.littlefox.app.foxschool.`object`.result.story.SeriesBaseResult
import com.littlefox.app.foxschool.`object`.result.story.SeriesInformationResult
import com.littlefox.app.foxschool.adapter.listener.SeriesCardItemListener
import com.littlefox.app.foxschool.common.CommonUtils
import com.littlefox.app.foxschool.common.Font
import com.littlefox.app.foxschool.enumerate.SeriesType
import com.littlefox.logmonitor.Log

import java.util.*

class SeriesCardViewAdapter : RecyclerView.Adapter<SeriesCardViewAdapter.ViewHolder?>
{
    private lateinit var mCurrentSeriesBaseResultList : ArrayList<SeriesInformationResult>
    private var mSeriesCardItemListener : SeriesCardItemListener? = null
    private var mCurrentSeriesType : SeriesType = SeriesType.LEVEL
    private var mCurrentClickPosition = -1
    private val mContext : Context

    companion object
    {
        private val RESOURCE_INDEX_IMAGE = intArrayOf(
            R.drawable.level01,
            R.drawable.level02,
            R.drawable.level03,
            R.drawable.level04,
            R.drawable.level05,
            R.drawable.level06,
            R.drawable.level07,
            R.drawable.level08,
            R.drawable.level09
        )
    }

    constructor(context: Context, list : ArrayList<SeriesInformationResult>)
    {
        mContext = context
        mCurrentSeriesBaseResultList = list
    }

    fun setData(list : ArrayList<SeriesInformationResult>)
    {
        mCurrentSeriesBaseResultList = list
    }

    fun setSeriesType(type : SeriesType)
    {
        mCurrentSeriesType = type
    }

    fun setIndexImageVisible()
    {
        if(mCurrentClickPosition != -1 && mCurrentSeriesType === SeriesType.LEVEL)
        {
            notifyItemChanged(mCurrentClickPosition, "unCheck")
        }
    }

    fun setSeriesCardItemListener(seriesCardItemListener : SeriesCardItemListener?)
    {
        mSeriesCardItemListener = seriesCardItemListener
    }

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : ViewHolder
    {
        val view : View
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.story_main_view_item, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder : ViewHolder, position : Int)
    {
        Glide.with(mContext)
            .load(mCurrentSeriesBaseResultList[holder.absoluteAdapterPosition].getThumbnailUrl())
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(holder._ThumbnailImage)
        if(mCurrentSeriesType === SeriesType.LEVEL && mCurrentSeriesBaseResultList[holder.absoluteAdapterPosition].getLevel() > 0)
        {
            holder._IndexImage.setImageResource(RESOURCE_INDEX_IMAGE[mCurrentSeriesBaseResultList[holder.absoluteAdapterPosition].getLevel() - 1])
            holder._IndexImage.visibility = View.VISIBLE
        }
        else
        {
            holder._IndexImage.visibility = View.GONE
        }

        if(mCurrentSeriesType === SeriesType.SONG)
        {
            holder._StoryCountText.setText(
                java.lang.String.format(
                    mContext.getString(R.string.text_count_songs),
                    CommonUtils.getInstance(mContext).getDecimalNumber(
                        mCurrentSeriesBaseResultList[holder.absoluteAdapterPosition].getContentsCount()
                    )
                )
            )
        }
        else
        {
            holder._StoryCountText.setText(
                java.lang.String.format(
                    mContext.getString(R.string.text_count_stories),
                    CommonUtils.getInstance(mContext).getDecimalNumber(
                        mCurrentSeriesBaseResultList[holder.absoluteAdapterPosition].getContentsCount()
                    )
                )
            )
        }
        holder._ThumbnailImage!!.setOnClickListener(View.OnClickListener {
            Log.f("position : " + holder.absoluteAdapterPosition + ", item : " + mCurrentSeriesBaseResultList[holder.absoluteAdapterPosition].getDisplayID())
            if(mCurrentSeriesType === SeriesType.LEVEL)
            {
                mCurrentClickPosition = holder.absoluteAdapterPosition
                notifyItemChanged(mCurrentClickPosition, "check")
            }
            mSeriesCardItemListener?.onClickItem(mCurrentSeriesBaseResultList[holder.absoluteAdapterPosition], holder._ThumbnailImage)
        })
    }


    override fun onBindViewHolder(holder : ViewHolder, position : Int, payloads : List<Any>)
    {
        if(payloads.isEmpty())
        {
            super.onBindViewHolder(holder, position, payloads)
        } else
        {
            for(payload : Any? in payloads)
            {
                if(payload is String)
                {
                    val check = payload
                    if(TextUtils.equals(check, "check") && holder is ViewHolder)
                    {
                        Log.f("check : position : $mCurrentClickPosition")
                        holder._IndexImage.visibility = View.GONE
                    }
                    else if(TextUtils.equals(check, "unCheck") && holder is ViewHolder)
                    {
                        Log.f("uncheck : position : $mCurrentClickPosition")
                        if(mCurrentSeriesType === SeriesType.LEVEL && mCurrentSeriesBaseResultList[position].getLevel() > 0)
                        {
                            holder._IndexImage.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    override fun getItemCount() : Int
    {
        return mCurrentSeriesBaseResultList.size
    }


    inner class ViewHolder : RecyclerView.ViewHolder
    {
        @BindView(R.id._thumbnailImage)
        lateinit var _ThumbnailImage : ImageView

        @BindView(R.id._indexImage)
        lateinit var _IndexImage : ImageView

        @BindView(R.id._storyCountText)
        lateinit var _StoryCountText : TextView

        constructor(view : View) : super(view)
        {
            ButterKnife.bind(this, view)
            initFont()
        }

        private fun initFont()
        {
            _StoryCountText.setTypeface(Font.getInstance(mContext).getTypefaceMedium())
        }
    }
}