package com.dicoding.core.di

import com.dicoding.core.data.repository.AuthRepositoryTester
import com.dicoding.core.data.repository.StoryRepositoryTester
import com.dicoding.membership.core.domain.auth.tester.repository.IAuthRepositoryTester
import com.dicoding.membership.core.domain.story.tester.repository.IStoryRepositoryTester
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module(includes = [NetworkModule::class, DatabaseModule::class])
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    // Tester
    @Binds
    abstract fun provideAuthRepository(authRepositoryTester: AuthRepositoryTester): IAuthRepositoryTester

    @Binds
    abstract fun provideStoryRepository(storyRepository: StoryRepositoryTester): IStoryRepositoryTester

    // Dynamic Feature Dagger Multi Module
}