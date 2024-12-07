package com.dicoding.membership.di

import com.dicoding.core.domain.auth.interactor.AuthInteractor
import com.dicoding.core.domain.auth.usecase.AuthUseCase
import com.dicoding.core.domain.test.auth.usecase.AuthUseCaseTester
import com.dicoding.core.domain.test.story.usecase.StoryUseCaseTester
import com.dicoding.membership.core.domain.auth.tester.interactor.AuthInteractorTester
import com.dicoding.core.domain.test.story.interactor.StoryInteractorTester
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun provideAuthUseCaseTester(authInteractor: AuthInteractorTester): AuthUseCaseTester

    @Binds
    @Singleton
    abstract fun provideStoryUseCase(storyInteractor: StoryInteractorTester): StoryUseCaseTester

    ////////////////////////////////////////////////////////////////////////////////////////////

    @Binds
    @Singleton
    abstract fun provideAuthUseCase(authInteractor: AuthInteractor): AuthUseCase
}