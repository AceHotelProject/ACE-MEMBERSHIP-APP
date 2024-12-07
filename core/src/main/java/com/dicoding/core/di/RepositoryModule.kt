package com.dicoding.core.di

import com.dicoding.core.data.repository.AuthRepository
import com.dicoding.core.data.repository.test.AuthRepositoryTester
import com.dicoding.core.data.repository.test.StoryRepositoryTester
import com.dicoding.core.domain.auth.repository.IAuthRepository
import com.dicoding.membership.core.domain.auth.tester.repository.IAuthRepositoryTester
import com.dicoding.membership.core.domain.story.tester.repository.IStoryRepositoryTester
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module(includes = [NetworkModule::class, DatabaseModule::class])
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    // Tester
    @Binds
    @Singleton
    abstract fun provideAuthRepositoryTester(authRepositoryTester: AuthRepositoryTester): IAuthRepositoryTester

    @Binds
    @Singleton
    abstract fun provideStoryRepository(storyRepository: StoryRepositoryTester): IStoryRepositoryTester

    // Dynamic Feature Dagger Multi Module
    ///////////////////////////////////////////////////////////////////////////////////////////////////

    @Binds
    @Singleton
    abstract fun provideAuthRepository(authRepository: AuthRepository): IAuthRepository
}