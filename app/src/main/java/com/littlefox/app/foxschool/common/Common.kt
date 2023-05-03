package com.littlefox.app.foxschool.common

class Common
{
    companion object
    {
        const val PACKAGE_NAME : String = "com.littlefox.app.foxschool"
        const val PATH_APP_ROOT : String = "/data/data/" + PACKAGE_NAME + "/files/"

        const val TEST_URL : String                     = "https://apis-foxschool.littlefox.co.kr/"
        const val BASE_URL : String                     = "https://foxschool-api.littlefox.co.kr/"
        const val BASE_API : String                     = BASE_URL + "api/"

        const val API_INIT : String                         = BASE_API + "app/version"
        const val API_SCHOOL_LIST : String                  = BASE_API + "users/school"
        const val API_LOGIN : String                        = BASE_API + "auth/login"
        const val API_ME : String                           = BASE_API + "auth/me"
        const val API_MAIN : String                         = BASE_API + "app/main"
        const val API_MY_INFO_UPDATE : String               = BASE_API + "users/myinfo/update"
        const val API_PASSWORD_CHANGE : String              = BASE_API + "users/password/update"
        const val API_PASSWORD_CHANGE_NEXT : String         = BASE_API + "users/password/next"
        const val API_PASSWORD_CHANGE_KEEP : String         = BASE_API + "users/password/keep"
        const val API_INQUIRE : String                      = BASE_API + "forum/inquiry"
        const val API_FOXSCHOOL_NEWS : String               = BASE_API + "forum/board/news"
        const val API_FAQ : String                          = BASE_API + "forum/board/faq"
        const val API_STUDENT_HOMEWORK : String             = BASE_API + "homeworks/student"
        const val API_TEACHER_HOMEWORK : String             = BASE_API + "homeworks/teacher"

        const val API_STUDENT_HOMEWORK_DETAIL_LIST : String = API_STUDENT_HOMEWORK + "/list"
        const val API_TEACHER_CLASS_LIST : String           = API_TEACHER_HOMEWORK + "/class"
        const val API_HOMEWORK_STATUS_DATA : String         = API_TEACHER_HOMEWORK + "/state"
        const val API_TEACHER_HOMEWORK_DETAIL_LIST : String = API_TEACHER_HOMEWORK + "/list"
        const val API_TEACHER_HOMEWORK_CONTENTS : String    = API_TEACHER_HOMEWORK + "/show"

        const val API_CONTENTS : String             = BASE_API + "contents/"
        const val API_STORY_DETAIL_LIST : String    = API_CONTENTS + "story/series/"
        const val API_SONG_DETAIL_LIST : String     = API_CONTENTS + "song/series/"
        const val API_INTRODUCE_SERIES : String     = API_STORY_DETAIL_LIST + "info/"

        const val API_QUIZ : String                 = API_CONTENTS + "quiz/"
        const val API_AUTH_CONTENT_PLAY : String    = API_CONTENTS + "player/"
        const val API_STUDY_LOG_SAVE : String       = API_CONTENTS + "player/save"
        const val API_QUIZ_SAVE_RECORD : String     = API_CONTENTS + "quiz/"
        const val API_VOCABULARY_CONTENTS : String  = API_CONTENTS + "vocabularies"
        const val API_SEARCH_LIST : String          = API_CONTENTS + "search"
        const val API_BOOKSHELF : String            = API_CONTENTS + "bookshelves"
        const val API_VOCABULARY_SHELF : String     = API_CONTENTS + "vocabularies"
        const val API_RECORD_UPLOAD : String        = API_CONTENTS + "record"
        const val API_RECORD_HISTORY : String       = API_CONTENTS + "record/history"
        const val API_FLASHCARD_SAVE : String       = API_CONTENTS + "flashcard"

        const val BASE_WEBVIEW_URL  : String                = BASE_URL + "web/"
        const val URL_LEARNING_LOG : String                 = BASE_WEBVIEW_URL + "studylog/history"
        const val URL_FAQS : String                         = BASE_WEBVIEW_URL + "help/faq/list"
        const val URL_1_ON_1_ASK : String                   = BASE_WEBVIEW_URL + "help/qna/list"
        const val URL_GAME_STARWORDS : String               = BASE_WEBVIEW_URL + "game/starwords/"
        const val URL_GAME_CROSSWORD : String               = BASE_WEBVIEW_URL + "game/crossword/"
        const val URL_EBOOK : String                        = BASE_WEBVIEW_URL + "ebook/"

        const val URL_FOXSCHOOL_NEWS_DETAIL : String    = BASE_WEBVIEW_URL + "forum/board/news/"
        const val URL_FAQ_DETAIL : String               = BASE_WEBVIEW_URL + "forum/board/faq/"
        const val URL_TERMS : String                    = BASE_WEBVIEW_URL + "forum/usernote"
        const val URL_PRIVACY : String                  = BASE_WEBVIEW_URL + "forum/privacy"
        const val URL_ORIGIN_TRANSLATE : String         = BASE_WEBVIEW_URL + "contents/originaltranslate/"
        const val URL_FIND_ID : String                  = BASE_WEBVIEW_URL + "auth/find/id"
        const val URL_FIND_PW : String                  = BASE_WEBVIEW_URL + "auth/find/password"
        const val URL_FOXSCHOOL_INTRODUCE : String      = "https://foxschool.littlefox.co.kr/home/app"

        const val PARAMS_DISPLAY_METRICS : String               = "display_metrics"
        const val PARAMS_REGISTER_APP_VERSION : String          = "app_version"
        const val PARAMS_VERSION_INFORMATION : String           = "version_information"
        const val PARAMS_USER_LOGIN : String                    = "user_login"
        const val PARAMS_FILE_MAIN_INFO : String                = "file_main_info"
        const val PARAMS_ACCESS_TOKEN : String                  = "access_token"
        const val PARAMS_USER_API_INFORMATION : String          = "user_api_information"
        const val PARAMS_IS_AUTO_LOGIN_DATA : String            = "is_auto_login_data"
        const val PARAMS_IS_DISPOSABLE_LOGIN : String           = "is_disposable_login"
        const val PARAMS_IS_VIDEO_HIGH_RESOLUTION : String      = "is_video_high_resolution"
        const val PARAMS_PLAYER_SPEED_INDEX : String            = "player_speed_index"
        const val PARAMS_IS_ENABLE_CAPTION : String             = "is_enable_caption"
        const val PARAMS_IS_ENABLE_PAGE_BY_PAGE : String        = "is_enable_page_by_page"
        const val PARAMS_VOCABULARY_INTERVAL : String           = "vocabulary_interval"
        const val PARAMS_APP_EXECUTE_DATE : String              = "app_execute_date"
        const val PARAMS_IAC_CONTROLLER_INFORMATION : String    = "iac_controller_information"
        const val PARAMS_IS_FORCE_PROGRESSIVE_PLAY : String     = "is_force_progressive_play"
        const val PARAMS_FIREBASE_PUSH_TOKEN : String           = "firebase_access_token"
        const val PARAMS_IS_PUSH_SEND : String                  = "is_push_send"
        const val PARAMS_IS_TEACHER_MODE : String               = "is_teacher_mode"
        const val PARAMS_CHECK_TABLET : String                  = "check_tablet"
        const val PARAMS_CHECK_PHONE_DEVICE_RADIO : String      = "check_phone_device_radio"
        const val PARAMS_CHECK_TABLET_DEVICE_RADIO : String     = "check_tablet_device_radio"

        const val INTENT_IS_LOGIN_FROM_MAIN : String                = "init_intro_login"
        const val INTENT_PLAYER_DATA_PARAMS : String                = "player_data_params"
        const val INTENT_PLAYER_INTRODUCE_VIDEO_PARAMS : String     = "introduce_video"
        const val INTENT_VOCABULARY_DATA : String                   = "vocabulary_data"
        const val INTENT_MODIFY_BOOKSHELF_NAME : String             = "modify_bookshelf_name"
        const val INTENT_MODIFY_VOCABULARY_NAME : String            = "vocabulary_name"
        const val INTENT_RESULT_SERIES_ID : String                  = "series_id"
        const val INTENT_STORY_SERIES_DATA : String                 = "story_series_data"
        const val INTENT_STORY_CATEGORY_DATA : String               = "story_category_data"
        const val INTENT_SERIES_INFORMATION_ID : String             = "series_information_id"
        const val INTENT_QUIZ_PARAMS : String                       = "quiz_params"
        const val INTENT_GAME_STARWORDS_ID : String                 = "starwords_id"
        const val INTENT_GAME_CROSSWORD_ID : String                 = "crossword_id"
        const val INTENT_FLASHCARD_DATA : String                    = "flashcard_data"
        const val INTENT_RECORD_PLAYER_DATA : String                = "record_player_data"
        const val INTENT_ORIGIN_TRANSLATE_ID : String               = "origin_translate"
        const val INTENT_FIND_INFORMATION : String                  = "find_information"
        const val INTENT_HOMEWORK_CHECKING_DATA : String            = "homework_checking_data"
        const val INTENT_BOOKSHELF_DATA : String                    = "bookshelf_data"
        const val INTENT_MANAGEMENT_MYBOOKS_DATA : String           = "management_mybooks_type"
        const val INTENT_EBOOK_DATA : String                        = "ebook_data"

        const val COROUTINE_CODE_INIT : String                          = "init"
        const val COROUTINE_CODE_SCHOOL_LIST : String                   = "school_list"
        const val COROUTINE_CODE_MAIN : String                          = "main"
        const val COROUTINE_CODE_ME : String                            = "me"
        const val COROUTINE_CODE_LOGIN : String                         = "login"
        const val COROUTINE_CODE_FILE_DOWNLOAD : String                 = "file_download"
        const val COROUTINE_CODE_CLASS_RECORD_FILE : String             = "record_file"
        const val COROUTINE_CODE_CLASS_RECORD_HISTORY : String          = "record_history"
        const val COROUTINE_CODE_AUTH_CONTENT_PLAY : String             = "auth_content_play"
        const val COROUTINE_CODE_BOOKSHELF_DETAIL_LIST_INFO : String    = "bookshelf_detail_list_info"
        const val COROUTINE_CODE_BOOKSHELF_CREATE : String              = "bookshelf_create"
        const val COROUTINE_CODE_BOOKSHELF_UPDATE : String              = "bookshelf_update"
        const val COROUTINE_CODE_BOOKSHELF_DELETE : String              = "bookshelf_delete"
        const val COROUTINE_CODE_BOOKSHELF_CONTENTS_ADD : String        = "bookshelf_contents_add"
        const val COROUTINE_CODE_BOOKSHELF_CONTENTS_DELETE : String     = "bookshelf_contents_delete"
        const val COROUTINE_CODE_STUDY_LOG_SAVE : String                = "study_log_save"
        const val COROUTINE_CODE_VOCABULARY_SHELF : String              = "vocabulary_shelf_list"
        const val COROUTINE_CODE_VOCABULARY_CREATE : String             = "vocabulary_create"
        const val COROUTINE_CODE_VOCABULARY_DELETE : String             = "vocabulary_delete"
        const val COROUTINE_CODE_VOCABULARY_UPDATE : String             = "vocabulary_update"
        const val COROUTINE_CODE_VOCABULARY_CONTENTS_ADD : String       = "vocabulary_contents_add"
        const val COROUTINE_CODE_VOCABULARY_CONTENTS_DELETE : String    = "vocabulary_contents_delete"
        const val COROUTINE_CODE_VOCABULARY_CONTENTS_LIST : String      = "vocabulary_contents_list"
        const val COROUTINE_SERIES_CONTENTS_LIST_INFO : String          = "series_contents_list_info"
        const val COROUTINE_CODE_INTRODUCE_SERIES : String              = "introduce_series"
        const val COROUTINE_CODE_STORY_CATEGORY_LIST_INFO : String      = "story_categoty_list_info"
        const val COROUTINE_CODE_SEARCH_LIST : String                   = "search_list"
        const val COROUTINE_CODE_QUIZ_INFORMATION : String              = "quiz_information"
        const val COROUTINE_CODE_QUIZ_SAVE_RECORD : String              = "quiz_save_record"
        const val COROUTINE_CODE_MY_INFO_UPDATE : String                = "my_info_update"
        const val COROUTINE_CODE_PASSWORD_CHANGE : String               = "password_change"
        const val COROUTINE_CODE_PASSWORD_CHANGE_NEXT : String          = "password_change_next"
        const val COROUTINE_CODE_PASSWORD_CHANGE_KEEP : String          = "password_change_keep"
        const val COROUTINE_CODE_INQUIRE : String                       = "inquire"
        const val COROUTINE_CODE_FORUM : String                         = "forum"
        const val COROUTINE_CODE_FLASHCARD_SAVE : String                = "flashcard_save"

        const val COROUTINE_CODE_STUDENT_HOMEWORK_CALENDER              = "student_homework_calender"
        const val COROUTINE_CODE_STUDENT_HOMEWORK_DETAIL_LIST           = "student_homework_status_list"
        const val COROUTINE_CODE_STUDENT_COMMENT_REGISTER               = "student_comment_register"
        const val COROUTINE_CODE_STUDENT_COMMENT_UPDATE                 = "student_comment_update"
        const val COROUTINE_CODE_STUDENT_COMMENT_DELETE                 = "student_comment_delete"

        const val COROUTINE_CODE_TEACHER_CLASS_LIST                     = "teacher_class_list"
        const val COROUTINE_CODE_TEACHER_HOMEWORK_CALENDER              = "teacher_homework_calender"
        const val COROUTINE_CODE_TEACHER_HOMEWORK_STATUS                = "teacher_homework_status"
        const val COROUTINE_CODE_TEACHER_HOMEWORK_DETAIL_LIST           = "teacher_homework_status_list"
        const val COROUTINE_CODE_TEACHER_HOMEWORK_CONTENTS              = "teacher_homework_contents"
        const val COROUTINE_CODE_TEACHER_HOMEWORK_CHECKING              = "teacher_homework_checking"

        const val LOG_FILE : String = "littlefox_foxschool.txt"

        // View name of the header image. Used for actihvity scene transitions
        const val STORY_DETAIL_LIST_HEADER_IMAGE : String       = "story_content_list_header_image"
        const val CATEGORY_DETAIL_LIST_HEADER_IMAGE : String    = "category_content_list_header_image"

        const val CONTENT_TYPE_ALL : String     = ""
        const val CONTENT_TYPE_STORY : String   = "S"
        const val CONTENT_TYPE_SONG : String    = "M"

        const val HTTP_HEADER_ANDROID : String  = "Android"
        const val HTTP_HEADER_APP_NAME : String = "LF_APP_AOS"
        const val DEVICE_TYPE_PHONE : String    = "phone"
        const val DEVICE_TYPE_TABLET : String   = "tablet"

        const val SERVICE_NOT_SUPPORTED : String    = "N"
        const val SERVICE_SUPPORTED_PAID : String   = "Y"
        const val SERVICE_SUPPORTED_FREE : String   = "F"

        const val SERVICE_NOT_ENDED : String    = "N"
        const val SERVICE_ENDED : String        = "Y"

        const val USER_TYPE_STUDENT : String = "S"
        const val USER_TYPE_TEACHER : String = "T"

        const val MILLI_SECOND : Int = 100
        const val HALF_SECOND : Int = 500
        const val SECOND : Int = 1000
        const val MINIMUM_TABLET_DISPLAY_RADIO : Float  = 1.4f
        const val PHONE_DISPLAY_RADIO_20_9 : Float      = 1.9f
        const val PHONE_DISPLAY_RADIO_FLIP : Float      = 2.2f

        const val TARGET_PHONE_DISPLAY_WIDTH : Float = 1080.0f
        const val TARGET_TABLET_DISPLAY_WIDTH : Float = 1920.0f

        const val LOADING_DIALOG_SIZE : Int              = 150

        const val DURATION_SHORTEST : Long               = 100
        const val DURATION_SHORTER : Long                = 150
        const val DURATION_SHORT : Long                  = 300
        const val DURATION_MENU_ANIMATION_PHONE : Long   = 350
        const val DURATION_NORMAL : Long                 = 500
        const val DURATION_CHANGE_USER : Long            = 600
        const val DURATION_SHORT_LONG : Long             = 700
        const val DURATION_LONG : Long                   = 1000
        const val DURATION_LONGER : Long                 = 1500
        const val DURATION_LONGEST : Long                = 2000
        const val DURATION_EASTER_EGG : Long             = 5000
        /**
         * 한 화면에 페이지 표시 개수
         */
        const val MAX_PAGE_BY_PAGE_COUNT_IN_LINE : Int = 5

        const val MAX_RECENTLY_LEARN_CONTENTS : Int     = 10
        const val MAX_BOOKSHELF_SIZE : Int              = 10
        const val MAX_BOOKSHELF_CONTENTS_SIZE : Int     = 300
        const val MAX_VOCABULARY_SIZE : Int             = 10
        const val MAX_VOCABULARY_CONTENTS_SIZE : Int    = 300

        const val PAGE_STORY : Int      = 0
        const val PAGE_SONG : Int       = 1
        const val PAGE_MY_BOOKS : Int   = 2

        /**
         * 나의 정보 화면 페이지 ( 나의 정보 0 / 나의 정보 수정 1 / 비밀번호 변경 2 )
         */
        const val PAGE_MY_INFO : Int            = 0
        const val PAGE_MY_INFO_CHANGE : Int     = 1
        const val PAGE_PASSWORD_CHANGE : Int    = 2

        /**
         * 포럼 페이지 (리스트 0 / 상세화면 1)
         */
        const val PAGE_FORUM_LIST : Int     = 0
        const val PAGE_FORUM_WEBVIEW : Int  = 1

        /**
         * 숙제관리 화면 페이지
         *  - 공통 : 달력 0, 숙제 현황 1, 코멘트(학습자/선생님 한마디) 3
         *  - 선생님 : 숙제현황상세보기/숙제 내용 2
         */
        const val PAGE_HOMEWORK_CALENDAR : Int          = 0
        const val PAGE_HOMEWORK_STATUS : Int            = 1
        const val PAGE_HOMEWORK_DETAIL : Int            = 2
        const val PAGE_HOMEWORK_COMMENT : Int           = 3

        const val BRIDGE_NAME : String = "littlefoxJavaInterface"

        const val IAC_AWAKE_CODE_ALWAYS_VISIBLE : String            = "C"
        const val IAC_AWAKE_CODE_ONCE_VISIBLE : String              = "E"
        const val IAC_AWAKE_CODE_SPECIAL_DATE_VISIBLE : String      = "F"

        const val RESULT_CODE_SERIES_LIST : Int = 10002

        /** 개발자 이메일  */
        const val DEVELOPER_EMAIL : String = "foxschool@littlefox.co.kr"

        const val INAPP_CAMPAIGN_MODE_NEWS : String             = "N"
        const val INAPP_CAMPAIGN_MODE_TESTIMONIAL : String      = "T"

        const val PLAYER_TYPE_NORMAL : String       = "normal"
        const val PLAYER_TYPE_PROGRESSIVE : String  = "progressive"

        const val MAXIMUM_LOG_FILE_SIZE : Long = 1024 * 1024 * 10L

        /** 퀴즈 종류별 코드 */
        const val QUIZ_CODE_PICTURE : String            = "N"
        const val QUIZ_CODE_TEXT : String               = "T"
        const val QUIZ_CODE_PHONICS_SOUND_TEXT : String = "S"
        const val QUIZ_CODE_SOUND_TEXT : String         = "E"

        const val PUSH_TOPIC_NAME : String = "FOXSCHOOL"

        const val FILE_TEACHER_MANUAL : String = "teacher_manual.pdf"
        const val FILE_HOME_NEWSPAPER : String = "school_letter.hwp"

        //API
        const val FAIL_CODE_NETWORK_NOT_CONNECT                     = 105
        const val FAIL_CODE_USER_AUTHORIZATION_NO_TAKEN             = 401
        const val FAIL_CODE_USER_AUTHORIZATION_INVALID_TOKEN        = 4011
        const val FAIL_CODE_USER_PAID_CHANGE                        = 450
        const val FAIL_CODE_USER_EXPIRE_USER                        = 451
        const val FAIL_CODE_USER_DUPLICATE_LOGIN                    = 452
        const val FAIL_CODE_INTERNAL_SERVER_ERROR                   = 500
        const val SUCCESS_CODE_OK                                   = 200

        const val PAGE_LOAD_COUNT: Int = 20
    }

}