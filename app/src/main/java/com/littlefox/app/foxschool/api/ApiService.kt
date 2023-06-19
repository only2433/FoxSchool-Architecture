package com.littlefox.app.foxschool.api

import com.google.gson.JsonObject
import com.littlefox.app.foxschool.`object`.result.forum.paging.ForumBaseListPagingResult
import com.littlefox.app.foxschool.`object`.result.homework.HomeworkCalendarBaseResult
import com.littlefox.app.foxschool.`object`.result.homework.HomeworkDetailBaseResult
import com.littlefox.app.foxschool.`object`.result.homework.HomeworkStatusBaseResult
import com.littlefox.app.foxschool.`object`.result.homework.TeacherClassItemData
import com.littlefox.app.foxschool.`object`.result.login.LoginInformationResult
import com.littlefox.app.foxschool.`object`.result.login.SchoolItemDataResult
import com.littlefox.app.foxschool.`object`.result.main.MainInformationResult
import com.littlefox.app.foxschool.`object`.result.version.VersionDataResult
import com.littlefox.app.foxschool.api.base.BaseResponse
import com.littlefox.app.foxschool.common.Common
import com.littlefox.app.foxschool.`object`.result.content.ContentsBaseResult
import com.littlefox.app.foxschool.`object`.result.forum.ForumBaseListResult
import com.littlefox.app.foxschool.`object`.result.main.MyBookshelfResult
import com.littlefox.app.foxschool.`object`.result.main.MyVocabularyResult
import com.littlefox.app.foxschool.`object`.result.player.PlayItemResult
import com.littlefox.app.foxschool.`object`.result.quiz.QuizInformationResult
import com.littlefox.app.foxschool.`object`.result.search.SearchListResult
import com.littlefox.app.foxschool.`object`.result.search.paging.ContentBasePagingResult
import com.littlefox.app.foxschool.`object`.result.search.paging.SearchListPagingResult
import com.littlefox.app.foxschool.`object`.result.vocabulary.VocabularyDataResult
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface ApiService
{
    @FormUrlEncoded
    @POST("app/version")
    suspend fun initAsync(
        @Field("device_id") device_id: String,
        @Field("push_address") push_address: String,
        @Field("push_on") push_on: String
    ) : Response<BaseResponse<VersionDataResult>>

    @GET("auth/me")
    suspend fun authMeAsync() : Response<BaseResponse<LoginInformationResult>>

    @GET("app/main")
    suspend fun mainAsync() : Response<BaseResponse<MainInformationResult>>

    @FormUrlEncoded
    @POST("users/password/update")
    suspend fun passwordChangeAsync(
        @Field("password_check") currentPassword: String,
        @Field("password") changePassword: String,
        @Field("password_confirm") changePasswordConfirm: String
    ) : Response<BaseResponse<Nothing>>

    @GET("users/password/next")
    suspend fun passwordChangeNextAsync() : Response<BaseResponse<Nothing>>

    @GET("users/password/keep")
    suspend fun passwordChangeKeepAsync() : Response<BaseResponse<Nothing>>

    @GET("users/school")
    suspend fun schoolListAsync() : Response<BaseResponse<ArrayList<SchoolItemDataResult>>>

    @FormUrlEncoded
    @POST("auth/login")
    suspend fun loginAsync(
        @Field("login_id") id : String,
        @Field("password") password : String,
        @Field("school_id") schoolCode : String
    ) : Response<BaseResponse<LoginInformationResult>>

    @GET("contents/player/{data}")
    suspend fun authContentsPlayAsync(
        @Path("data") requestData: String
    ) : Response<BaseResponse<PlayItemResult>>


    @FormUrlEncoded
    @POST("contents/player/save")
    suspend fun savePlayerStudyAsync(
        @Field("content_id") contentsID: String,
        @Field("play_type") playType: String,
        @Field("play_time") playTime: String,
        @Field("hw_no") homeworkNumber : Int? = null
    ) : Response<BaseResponse<Nothing>>


    @GET ("contents/quiz/{content_id}")
    suspend fun quizInformationAsync(
        @Path("content_id") contentsID : String
    ) : Response<BaseResponse<QuizInformationResult>>

    @Headers("Content-Type: application/json")
    @POST("contents/quiz/{content_id}/result")
    suspend fun saveQuizRecordAsync(
        @Path("content_id") contentsID : String,
        @Body data: JsonObject
    ) : Response<BaseResponse<Nothing>>

    @GET("contents/vocabularies/{content_id}")
    suspend fun getVocabularyContentsList(
        @Path("content_id") contentsID : String
    ) : Response<BaseResponse<ArrayList<VocabularyDataResult>>>

    @FormUrlEncoded
    @PUT("contents/vocabularies/{vocabulary_id}/words")
    suspend fun addVocabularyContents(
        @Path("vocabulary_id") vocabularyID: String,
        @FieldMap queryMap : Map<String, String>
    ) : Response<BaseResponse<MyVocabularyResult>>

    @GET("contents/bookshelves/{bookshelf_id}")
    suspend fun getBookshelfContentsList(
        @Path("bookshelf_id") bookshelfID : String
    ) : Response<BaseResponse<ArrayList<ContentsBaseResult>>>

    @FormUrlEncoded
    @PUT("contents/bookshelves/{data}/contents")
    suspend fun addBookshelfContentsAsync(
        @Path("data") bookshelfID: String,
        @FieldMap queryMap : Map<String, String>
    ) : Response<BaseResponse<MyBookshelfResult>>

    @DELETE("contents/bookshelves/{bookshelf_id}/contents")
    suspend fun deleteBookshelfContents(
        @Path("bookshelf_id") bookshelfID : String,
        @QueryMap queryMap : Map<String, String>
    ) : Response<BaseResponse<MyBookshelfResult>>

    @GET("contents/flashcard/{content_id}")
    suspend fun saveFlashcardRecordAsync(
        @Path("content_id") contentsID : String
    ) : Response<BaseResponse<Nothing>>

    @GET("forum/board/faq")
    suspend fun forumFAQListAsync(
        @Query("per_page") pageCount: Int,
        @Query("page") currentPage: Int
    ) : Response<BaseResponse<ForumBaseListResult>>

    @GET("forum/board/news")
    suspend fun forumNewsListAsync(
        @Query("per_page") pageCount: Int,
        @Query("page") currentPage: Int
    ) : Response<BaseResponse<ForumBaseListResult>>

    @GET("forum/board/news")
    suspend fun forumPagingListAsync(
        @Query("per_page") pageCount: Int,
        @Query("page") currentPage: Int
    ) : Response<BaseResponse<ForumBaseListPagingResult>>

    @GET("contents/search")
    suspend fun getSearchList(
        @Query("type") searchType: String? = null,
        @Query("keyword") keyword: String,
        @Query("per_page") pageCount: Int,
        @Query("page") currentPage: Int
    ) : Response<BaseResponse<SearchListPagingResult>>

    @GET("homeworks/student")
    suspend fun studentHomeworkCalendarAsync(
        @Query("year") id : String,
        @Query("month") password : String
    ) : Response<BaseResponse<HomeworkCalendarBaseResult>>

    @GET("homeworks/student/list/{hw_no}")
    suspend fun studentHomeworkListAsync(
        @Path("hw_no") homeworkNumber : Int
    ) : Response<BaseResponse<HomeworkDetailBaseResult>>

    @PUT("homeworks/student")
    suspend fun studentCommentRegisterAsync(
        @Query("comment") comment : String,
        @Query("hw_no") homeworkNumber : Int
    ) : Response<BaseResponse<Nothing>>


    @FormUrlEncoded
    @POST("homeworks/student")
    suspend fun studentCommentUpdateAsync(
        @Field("comment") comment : String,
        @Field("hw_no") homeworkNumber : Int
    ) : Response<BaseResponse<Nothing>>

    @DELETE("homeworks/student")
    suspend fun studentCommentDeleteAsync(
        @Query("hw_no") homeworkNumber : Int
    ) : Response<BaseResponse<Nothing>>

    @GET("homeworks/teacher/class")
    suspend fun teacherHomeworkClassListAsync() : Response<BaseResponse<ArrayList<TeacherClassItemData>>>

    @GET("homeworks/teacher/{school_class_id}")
    suspend fun teacherHomeworkCalendarAsync(
        @Path("school_class_id") classId : String,
        @Query("year") id : String,
        @Query("month") password : String
    ) : Response<BaseResponse<HomeworkCalendarBaseResult>>

    @GET("homeworks/teacher/state/{school_class_id}/{hw_no}")
    suspend fun teacherHomeworkStatusAsync(
        @Path("school_class_id") classId : Int,
        @Path("hw_no") homeworkNumber : Int
    ) : Response<BaseResponse<HomeworkStatusBaseResult>>

    @GET("homeworks/teacher/list/{school_class_id}/{hw_no}/{fu_id}")
    suspend fun teacherHomeworkDetailAsync(
        @Path("school_class_id") classId : Int,
        @Path("hw_no") homeworkNumber : Int,
        @Path("fu_id") userID : String
    ) : Response<BaseResponse<HomeworkDetailBaseResult>>

    @GET("homeworks/teacher/show/{school_class_id}/{hw_no}")
    suspend fun teacherHomeworkContentsAsync(
        @Path("school_class_id") classId : Int,
        @Path("hw_no") homeworkNumber : Int
    ) : Response<BaseResponse<HomeworkDetailBaseResult>>


    @FormUrlEncoded
    @POST("homeworks/teacher")
    suspend fun teacherHomeworkCheckingAsync(
        @Field("hw_no") homeworkNumber : Int,
        @Field("school_class_id") classId : Int,
        @Field("fu_id") userID : String,
        @Field("eval") evaluationState : String
    ) : Response<BaseResponse<Nothing>>


    @FormUrlEncoded
    @POST("homeworks/teacher")
    suspend fun teacherHomeworkCheckingAsync(
        @Field("hw_no") homeworkNumber : Int,
        @Field("school_class_id") classId : Int,
        @Field("fu_id") userID : String,
        @Field("eval") evaluationState : String,
        @Field("eval_comment") evaluationComment : String,
    ) : Response<BaseResponse<Nothing>>

    companion object
    {
        const val CONNECTION_TIMEOUT: Long = 20000L
        fun create() : ApiService
        {
            val okHttpClient = OkHttpClient.Builder().apply {
                addInterceptor(
                    HeaderInterceptor()
                )
                addInterceptor(
                    SaveLogInterceptor()
                )
            }

            val client = okHttpClient
                .readTimeout(CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
                .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(Common.BASE_API)
                .client(client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }
}