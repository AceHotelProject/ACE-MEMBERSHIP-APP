package com.dicoding.membership.di

import com.dicoding.core.domain.auth.tester.usecase.AuthUseCaseTester
import com.dicoding.core.domain.story.tester.usecase.StoryUseCaseTester
import com.dicoding.membership.core.domain.auth.tester.interactor.AuthInteractorTester
import com.dicoding.membership.core.domain.story.tester.interactor.StoryInteractorTester
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
    abstract fun provideAuthUseCase(authInteractor: AuthInteractorTester): AuthUseCaseTester

    @Binds
    @Singleton
    abstract fun provideStoryUseCase(storyInteractor: StoryInteractorTester): StoryUseCaseTester
}