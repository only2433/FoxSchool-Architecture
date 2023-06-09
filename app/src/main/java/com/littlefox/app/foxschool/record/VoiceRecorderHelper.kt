package com.littlefox.app.foxschool.record

import android.content.Context
import android.media.MediaRecorder
import com.littlefox.app.foxschool.common.CommonUtils
import com.littlefox.app.foxschool.record.listener.VoiceRecordEventListener
import com.littlefox.logmonitor.Log
import java.io.*
import java.util.*
import kotlin.collections.ArrayList

class VoiceRecorderHelper(private val mContext : Context)
{
    internal inner class RecordProgressTask : TimerTask()
    {
        override fun run()
        {
            mCurrentDuration += DURATION_PROGRESS_TASK
            val currentPercent : Int = CommonUtils.getInstance(mContext).getCurrentPercent(mCurrentDuration, mMaxRecordDuration)
            mVoiceRecordEventListener.onRecordProgress(currentPercent)
            if(mCurrentDuration >= mMaxRecordDuration)
            {
                enableTask(false)
            }
        }
    }

    private var mMediaRecorder : MediaRecorder? = null
    private var mProgressTimer : Timer? = null
    private lateinit var mVoiceRecordEventListener : VoiceRecordEventListener
    private var mMaxRecordDuration = 0
    private var mCurrentDuration = 0
    private val isRecordingAvailableStorage : Boolean
        get()
        {
            var availableStorageSize = 0L
            availableStorageSize = CommonUtils.getInstance(mContext).availableStorageSize
            if(availableStorageSize >= MIN_ACQUIRE_STORAGE_SIZE)
            {
                return true
            }
            else
            {
                return false
            }
        }

    fun startRecording(maxDurationSecond : Int, fileName : String)
    {
        Log.f("maxDurationSecond : $maxDurationSecond, fileName : $fileName")
        if(isRecordingAvailableStorage == false)
        {
            mVoiceRecordEventListener.inFailure(ERROR_EXTERNAL_STORAGE_USE, "The device's internal capacity is less than 500mb.")
            return
        }
        mMaxRecordDuration = maxDurationSecond
        if(mMediaRecorder == null)
        {
            mMediaRecorder = MediaRecorder()
        }
        else
        {
            mMediaRecorder?.reset()
        }
        mMediaRecorder!!.run {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioEncodingBitRate(DEFAULT_BIT_RATE)
            setAudioSamplingRate(DEFAULT_SAMPLING_RATE)
            setOutputFile(fileName)
            setMaxDuration(mMaxRecordDuration)
            setOnInfoListener(onInfoListener)
            setOnErrorListener(onErrorListener)
            try
            {
                prepare()
            }
            catch(e : IOException)
            {
                Log.f("MediaRecorder Prepare Failed")
                mVoiceRecordEventListener.inFailure(ERROR_RECORDER_PREPARE, "MediaRecorder Prepare Failed")
            }
            start()
        }

    }

    fun stopRecording()
    {
        Log.f("")
        try
        {
            if(mMediaRecorder != null)
            {
                mMediaRecorder?.stop()
                enableTask(false)
                if(mVoiceRecordEventListener != null)
                    mVoiceRecordEventListener.onCompleteRecord()
            }
        }
        catch(e : RuntimeException)
        {
            Log.f("Error : " + e.message)
        }
    }

    fun releaseRecording()
    {
        if(mMediaRecorder != null)
        {
            mMediaRecorder?.release()
            mMediaRecorder = null
        }
    }

    private fun enableTask(isEnable : Boolean)
    {
        if(isEnable)
        {
            if(mProgressTimer == null)
            {
                mProgressTimer = Timer()
                mProgressTimer!!.schedule(RecordProgressTask(), 0, DURATION_PROGRESS_TASK.toLong())
            }
        }
        else
        {
            if(mProgressTimer != null)
            {
                mProgressTimer!!.cancel()
                mProgressTimer = null
            }
        }
    }

    /**
     * 녹음 파일 결합 메소드
     */
    fun mergeMediaFiles(sourceFiles : ArrayList<String>, targetFile : String?)
    {
        try
        {
            var sourceFileArray : ArrayList<File> = ArrayList()

            for(index in sourceFiles.indices)
            {
                var tempFile = File(sourceFiles.get(index))
                sourceFileArray.add(tempFile)
            }
            val outputStream = FileOutputStream(targetFile, true)
            val buffer = ByteArray(1048576)
            var totalSize = 0L
            for(file in sourceFileArray) {
                totalSize += file.length()
            }
            var currentSize = 0
            for(file in sourceFileArray) {
                val inputStream = FileInputStream(file)
                while(true){
                    val count = inputStream.read(buffer)
                    if(count == -1)
                        break
                    else {
                        currentSize += count

                    }
                    outputStream.write(buffer, 0, count)
                    outputStream.flush()
                }
                inputStream.close()
            }
            outputStream.flush()
            outputStream.close()

            if (mVoiceRecordEventListener != null)
            {
                mVoiceRecordEventListener.onCompleteFileMerged()
            }

        } catch(e : IOException)
        {
            Log.f("Error merging media files. exception: ${e.message} || $e")
        }
    }

    fun setVoiceRecordEventListener(voiceRecordEventListener : VoiceRecordEventListener)
    {
        mVoiceRecordEventListener = voiceRecordEventListener
    }

    private val onInfoListener : MediaRecorder.OnInfoListener = object : MediaRecorder.OnInfoListener
    {
        override fun onInfo(mr : MediaRecorder, what : Int, extra : Int)
        {
            if(what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED)
            {
                Log.f("MEDIA_RECORDER_INFO_MAX_DURATION_REACHED")
                stopRecording()
            }
        }
    }

    private val onErrorListener : MediaRecorder.OnErrorListener = object : MediaRecorder.OnErrorListener
    {
        override fun onError(mr : MediaRecorder, what : Int, extra : Int)
        {
            mVoiceRecordEventListener.inFailure(what, "Media Recorder OnError")
        }
    }

    companion object
    {
        const val ERROR_RECORDER_PREPARE = 1001
        const val ERROR_EXTERNAL_STORAGE_USE = 1002
        private const val DURATION_PROGRESS_TASK = 100
        private const val MIN_ACQUIRE_STORAGE_SIZE = 500
        private const val DEFAULT_SAMPLING_RATE = 48000
        private const val DEFAULT_BIT_RATE = 64000
    }
}