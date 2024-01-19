package com.littlefox.app.foxschool.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import com.littlefox.app.foxschool.R
import com.littlefox.app.foxschool.`object`.result.main.MainInformationResult
import com.littlefox.app.foxschool.common.Common
import com.littlefox.app.foxschool.common.CommonUtils
import com.littlefox.app.foxschool.common.Font
import com.littlefox.app.foxschool.enumerate.BookColor
import com.littlefox.app.foxschool.enumerate.BookType
import com.littlefox.app.foxschool.main.viewmodel.factory.MainFactoryViewModel
import com.littlefox.library.view.animator.ViewAnimator
import com.littlefox.logmonitor.Log
import com.ssomai.android.scalablelayout.ScalableLayout

class MainMyBooksFragment() : Fragment()
{
    @BindView(R.id._switchButtonLayout)
    lateinit var _SwitchButtonLayout : ScalableLayout

    @BindView(R.id._booksItemLayout)
    lateinit var _BooksItemBaseLayout : LinearLayout

    @BindView(R.id._switchAnimationButton)
    lateinit var _SwitchAnimationButton : ImageView

    @BindView(R.id._bookshelfTextButton)
    lateinit var _BookshelfTextButton : TextView

    @BindView(R.id._vocabularyTextButton)
    lateinit var _VocabularyTextButton : TextView

    @Nullable
    @BindView(R.id._backgroundLayout)
    lateinit var _BackgroundLayout : ScalableLayout

    @Nullable
    @BindView(R.id._backgroundImage)
    lateinit var _BackgroundImage : ImageView

    companion object
    {
        private var SWITCH_TAB_WIDTH : Float = 0f
        private val BOOKS_MAX_SIZE = 10
        val instance : MainMyBooksFragment
            get() = MainMyBooksFragment()
    }

    private lateinit var mContext : Context
    private lateinit var mUnbinder : Unbinder
    private var mCurrentBookItemSize = 0
    private lateinit var mMainInformationResult : MainInformationResult
    private var mCurrentBookType : BookType = BookType.BOOKSHELF
    private var mSelectColor : Int = -1

    private val factoryViewModel : MainFactoryViewModel by activityViewModels()

    override fun onAttach(context : Context)
    {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater : LayoutInflater, container : ViewGroup?, savedInstanceState : Bundle?) : View?
    {
        val view : View
        if(CommonUtils.getInstance(mContext).checkTablet)
        {
            view = inflater.inflate(R.layout.fragment_main_my_books_tablet, container, false)
        } else
        {
            view = inflater.inflate(R.layout.fragment_main_my_books, container, false)
        }
        mUnbinder = ButterKnife.bind(this, view)
        mMainInformationResult = CommonUtils.getInstance(mContext).loadMainData()
        return view
    }

    override fun onViewCreated(view : View, savedInstanceState : Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initFont()
        if(CommonUtils.getInstance(mContext).checkTablet)
        {
            settingBooksInformationTablet()
        } else
        {
            settingBooksInformation()
        }
    }

    override fun onActivityCreated(savedInstanceState : Bundle?)
    {
        super.onActivityCreated(savedInstanceState)
        setupObserverViewModel()
    }

    override fun onPause()
    {
        super.onPause()
    }

    override fun onResume()
    {
        super.onResume()
    }

    override fun onStop()
    {
        super.onStop()
    }

    override fun onDestroy()
    {
        super.onDestroy()
    }

    override fun onDestroyView()
    {
        super.onDestroyView()
        mUnbinder.unbind()
        Log.f("")
    }

    private fun initView()
    {
        /**
         * 서버에서 데이터가 없을때는 단어장 항목을 보여주지 않는다.
         */
        if(mMainInformationResult.getVocabulariesList().size <= 0)
        {
            _SwitchButtonLayout.setVisibility(View.GONE)
        }

        if(CommonUtils.getInstance(mContext).checkTablet)
        {
            SWITCH_TAB_WIDTH = 231.0f
        } else
        {
            SWITCH_TAB_WIDTH = 330.0f
        }

        // 선생님/학생에 따른 셀렉터 on 이미지, 컬러 설정
        if (CommonUtils.getInstance(mContext).isTeacherMode)
        {
            mSelectColor = mContext.resources.getColor(R.color.color_29c8e6)
            _SwitchAnimationButton.setImageResource(R.drawable.tab_main_on_teacher)
        }
        else
        {
            mSelectColor = mContext.resources.getColor(R.color.color_23cc8a)
            _SwitchAnimationButton.setImageResource(R.drawable.tab_main_on_student)
        }
        switchTabsTextColor(mCurrentBookType)
    }

    private fun initFont()
    {
        _BookshelfTextButton.setTypeface(Font.getInstance(mContext).getTypefaceBold())
        _VocabularyTextButton.setTypeface(Font.getInstance(mContext).getTypefaceBold())
    }

    private fun updateData(mainInformationResult : MainInformationResult)
    {
        Log.f("")
        mMainInformationResult = mainInformationResult
        if(CommonUtils.getInstance(mContext).checkTablet)
        {
            settingBooksInformationTablet()
        } else
        {
            settingBooksInformation()
        }
    }

    private fun setupObserverViewModel()
    {
        factoryViewModel.updateMyBooksData.observe(viewLifecycleOwner, Observer<Any> { mainInformationResult ->
            updateData(mainInformationResult as MainInformationResult)
        })
    }

    private fun switchTabsAnimation(tab : BookType)
    {
        if(tab === BookType.BOOKSHELF)
        {
            ViewAnimator.animate(_SwitchAnimationButton)
                .translationX(CommonUtils.getInstance(mContext).getPixel(SWITCH_TAB_WIDTH), 0f)
                .duration(Common.DURATION_SHORT).start()
        } else
        {
            ViewAnimator.animate(_SwitchAnimationButton)
                .translationX(0f, CommonUtils.getInstance(mContext).getPixel(SWITCH_TAB_WIDTH))
                .duration(Common.DURATION_SHORT).start()
        }
    }

    private fun switchTabsTextColor(type : BookType)
    {
        if(type === BookType.BOOKSHELF)
        {
            _BookshelfTextButton.setTextColor(mSelectColor)
            _VocabularyTextButton.setTextColor(mContext!!.resources.getColor(R.color.color_a0a0a0))
        }
        else
        {
            _BookshelfTextButton.setTextColor(mContext!!.resources.getColor(R.color.color_a0a0a0))
            _VocabularyTextButton.setTextColor(mSelectColor)
        }
    }

    /**
     * 폰 일시에 북 화면 구성
     */
    private fun settingBooksInformation()
    {
        _BooksItemBaseLayout.removeAllViews()
        if(mCurrentBookType === BookType.BOOKSHELF)
            mCurrentBookItemSize = mMainInformationResult.getBookShelvesList().size
        else
            mCurrentBookItemSize = mMainInformationResult.getVocabulariesList().size
        Log.f("MAX_ITEM_COUNT : $mCurrentBookItemSize, currentSwitchTab : $mCurrentBookType")
        for(i in 0 until mCurrentBookItemSize)
        {
            val index = i
            val itemView : View = LayoutInflater.from(mContext).inflate(R.layout.mybooks_add_item, null)
            val baseLayout : ScalableLayout = itemView.findViewById<View>(R.id._baseLayout) as ScalableLayout
            val settingButton = itemView.findViewById<View>(R.id._booksEnterButtonRect) as ImageView
            settingBooksItemView(i, itemView)
            settingButton.setOnClickListener(View.OnClickListener {
                if(mCurrentBookType === BookType.BOOKSHELF)
                {
                    factoryViewModel.onSettingBookshelf(index)
                }
                else
                {
                    factoryViewModel.onSettingVocabulary(index)
                }
            })
            baseLayout.setOnClickListener(object : View.OnClickListener
            {
                override fun onClick(view : View)
                {
                    if(mCurrentBookType === BookType.BOOKSHELF)
                    {
                        factoryViewModel.onEnterBookshelfList(index)
                    } else
                    {
                        factoryViewModel.onEnterVocabularyList(index)
                    }
                }
            })
        }
        val addModeView : View = LayoutInflater.from(mContext).inflate(R.layout.mybooks_add_mode, null)
        settingAddModeView(addModeView)
    }

    /**
     * 테블릿 일시에 북 화면 구성
     */
    private fun settingBooksInformationTablet()
    {
        val TABLET_ITEM_HEIGHT : Float  = 120.0f
        val TABLET_ADD_MODE_HEIGHT : Float = 170.0f
        val TABLET_BACKGROUND_LEFT : Float = 480.0f
        val TABLET_BACKGROUND_WIDTH : Float = 960.0f
        _BooksItemBaseLayout.removeAllViews()

        if(mCurrentBookType === BookType.BOOKSHELF)
            mCurrentBookItemSize =mMainInformationResult.getBookShelvesList().size
        else
            mCurrentBookItemSize =mMainInformationResult.getVocabulariesList().size

        Log.f("MAX_ITEM_COUNT : $mCurrentBookItemSize, currentSwitchTab : $mCurrentBookType")
        var backgroundHeight : Float = 0.0f
        Log.f("mCurrentBookItemSize : $mCurrentBookItemSize")
        if(mCurrentBookItemSize == BOOKS_MAX_SIZE)
        {
            backgroundHeight = BOOKS_MAX_SIZE * TABLET_ITEM_HEIGHT + 30
        } else
        {
            backgroundHeight = TABLET_ITEM_HEIGHT * mCurrentBookItemSize + TABLET_ADD_MODE_HEIGHT
        }
        _BackgroundLayout?.setScaleSize(Common.TARGET_TABLET_DISPLAY_WIDTH, backgroundHeight.toFloat())
        _BackgroundLayout?.moveChildView(_BackgroundImage,
            TABLET_BACKGROUND_LEFT.toFloat(),
            0f,
            TABLET_BACKGROUND_WIDTH.toFloat(),
            backgroundHeight.toFloat()
        )
        for(i in 0 until mCurrentBookItemSize)
        {
            val index = i
            val itemView : View = LayoutInflater.from(mContext).inflate(R.layout.mybooks_add_item_tablet, null)
            settingBooksItemView(i, itemView)
            val clickItemImage = itemView.findViewById<View>(R.id._clickBooksImage) as ImageView
            val settingButton = itemView.findViewById<View>(R.id._booksEnterButtonRect) as ImageView
            clickItemImage.setOnClickListener(object : View.OnClickListener
            {
                override fun onClick(view : View)
                {
                    if(mCurrentBookType === BookType.BOOKSHELF)
                    {
                        factoryViewModel.onEnterBookshelfList(index)
                    } else
                    {
                        factoryViewModel.onEnterVocabularyList(index)
                    }
                }
            })
            settingButton.setOnClickListener(object : View.OnClickListener
            {
                override fun onClick(v : View)
                {
                    if(mCurrentBookType === BookType.BOOKSHELF)
                    {
                        factoryViewModel.onSettingBookshelf(index)
                    } else
                    {
                        factoryViewModel.onSettingVocabulary(index)
                    }
                }
            })
        }
        val addModeView : View = LayoutInflater.from(mContext).inflate(R.layout.mybooks_add_mode_tablet, null)
        settingAddModeView(addModeView)
    }

    /**
     * 각각의 Book Item 정보를 세팅하는 메소드
     * @param position 포지션
     * @param itemView 각각 아이템 뷰의 Parent
     */
    private fun settingBooksItemView(position : Int, itemView : View)
    {
        val color : BookColor
        if(mCurrentBookType === BookType.BOOKSHELF)
            color = CommonUtils.getInstance(mContext).getBookColorType(mMainInformationResult.getBookShelvesList().get(position).getColor())
        else
            color = CommonUtils.getInstance(mContext).getBookColorType(mMainInformationResult.getVocabulariesList().get(position).getColor())

        val iconImage = itemView.findViewById<View>(R.id._iconBooksImage) as ImageView
        val coverImage = itemView.findViewById<View>(R.id._coverBooksImage) as ImageView
        val bookTitle : TextView = itemView.findViewById<View>(R.id._booksTitle) as TextView
        val bottomLine = itemView.findViewById<View>(R.id._booksBottomLine) as ImageView
        if(position == (BOOKS_MAX_SIZE - 1))
        {
            bottomLine.visibility = View.GONE
        }
        bookTitle.setTypeface(Font.getInstance(mContext).getTypefaceRegular())
        if(mCurrentBookType === BookType.BOOKSHELF)
        {
            iconImage.setImageResource(R.drawable.icon_bookshelf)
            coverImage.setImageResource(CommonUtils.getInstance(mContext).getBookResource(color))
            bookTitle.setText(
                (mMainInformationResult.getBookShelvesList().get(position).getName()
                        + " (" + mMainInformationResult.getBookShelvesList().get(position).getContentsCount() + ")")
            )
        }
        else
        {
            iconImage.setImageResource(R.drawable.icon_voca)
            coverImage.setImageResource(CommonUtils.getInstance(mContext).getBookResource(color))
            bookTitle.setText(
                (mMainInformationResult.getVocabulariesList().get(position).getName()
                     + " (" + mMainInformationResult.getVocabulariesList().get(position).getWordCount() + ")")
            )
        }
        _BooksItemBaseLayout.addView(itemView)
    }

    /**
     * 추가 버튼 관련 뷰 정보 추가 메소드
     */
    private fun settingAddModeView(addModeView : View)
    {
        if(mCurrentBookItemSize >= BOOKS_MAX_SIZE)
        {
            return
        }
        val addMyBooksButton = addModeView.findViewById<View>(R.id._mybooksAddView) as ImageView
        addMyBooksButton.setOnClickListener(mSwitchTabClickListener)

        val addMyBooksText : TextView = addModeView.findViewById<View>(R.id._mybooksAddText) as TextView
        addMyBooksText.setOnClickListener(mSwitchTabClickListener)

        val switchButtonText : TextView = addModeView.findViewById<View>(R.id._mybooksAddText) as TextView
        switchButtonText.setTypeface(Font.getInstance(mContext).getTypefaceRegular())

        if(mCurrentBookType === BookType.BOOKSHELF)
        {
            switchButtonText.setText(mContext.resources.getString(R.string.text_add_bookshelf))
        } else
        {
            switchButtonText.setText(mContext.resources.getString(R.string.text_add_vocabulary))
        }
        _BooksItemBaseLayout.addView(addModeView)
    }

    @OnClick(R.id._bookshelfTextButton, R.id._vocabularyTextButton)
    fun onClickView(view : View)
    {
        when(view.id)
        {
            R.id._bookshelfTextButton ->
            if(mCurrentBookType === BookType.VOCABULARY)
            {
                mCurrentBookType = BookType.BOOKSHELF
                switchTabsAnimation(mCurrentBookType)
                switchTabsTextColor(mCurrentBookType)
                if(CommonUtils.getInstance(mContext).checkTablet)
                {
                    settingBooksInformationTablet()
                } else
                {
                    settingBooksInformation()
                }
            }
            R.id._vocabularyTextButton ->
            if(mCurrentBookType === BookType.BOOKSHELF)
            {
                mCurrentBookType = BookType.VOCABULARY
                switchTabsAnimation(mCurrentBookType)
                switchTabsTextColor(mCurrentBookType)
                if(CommonUtils.getInstance(mContext).checkTablet)
                {
                    settingBooksInformationTablet()
                } else
                {
                    settingBooksInformation()
                }
            }
        }
    }

    private val mSwitchTabClickListener : View.OnClickListener = object : View.OnClickListener
    {
        override fun onClick(v : View)
        {
            Log.i("")
            if(mCurrentBookType === BookType.BOOKSHELF)
            {
                factoryViewModel.onAddBookshelf()
            }
            else
            {
                factoryViewModel.onAddVocabulary()
            }
        }
    }
}