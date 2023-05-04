package com.littlefox.app.foxschool.api.di

import com.littlefox.app.foxschool.api.ApiService
import com.littlefox.app.foxschool.main.viewmodel.api.IntroApiViewModel
import com.littlefox.app.foxschool.main.viewmodel.api.LoginApiViewModel
import com.littlefox.app.foxschool.main.viewmodel.api.MainApiViewModel
import com.littlefox.app.foxschool.main.viewmodel.api.SearchApiViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object Injection
{
    @Singleton
    @Provides
    fun provideFoxSchoolRepository() : FoxSchoolRepository
    {
        return FoxSchoolRepository(ApiService.create())
    }

    @Provides
    fun provideIntroApiViewModel() : IntroApiViewModel
    {
        return IntroApiViewModel(provideFoxSchoolRepository())
    }

    @Provides
    fun provideLoginApiViewModel() : LoginApiViewModel
    {
        return LoginApiViewModel(provideFoxSchoolRepository())
    }

    @Provides
    fun provideMainApiViewModel() : MainApiViewModel
    {
        return MainApiViewModel(provideFoxSchoolRepository())
    }

    @Provides
    fun provideSearchApiViewModel() : SearchApiViewModel
    {
        return SearchApiViewModel(provideFoxSchoolRepository())
    }

}