package com.littlefox.app.foxschool.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "coachMark")
data class CoachmarkEntity(
        @ColumnInfo(name = "user_id")
        var userID : String,

        @ColumnInfo(name = "is_story_coachmark_viewed")
        var isStoryCoachmarkViewed : Boolean,

        @ColumnInfo(name = "is_song_coachmark_viewed")
        var isSongCoachmarkViewed : Boolean,

        @ColumnInfo(name = "is_record_coachmark_viewed")
        var isRecordCoachmarkViewed : Boolean,

        @ColumnInfo(name = "flashcard_coachmark_viewed")
        var isFlashcardCoachmarkViewed : Boolean = false
)
{
        @PrimaryKey(autoGenerate = true)
        var id : Int = 0
}

