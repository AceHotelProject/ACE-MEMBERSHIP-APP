package com.dicoding.membership.di

import com.dicoding.core.domain.story.tester.usecase.StoryUseCaseTester
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface FavoriteModuleDependencies {

    fun storyUseCase(): StoryUseCaseTester
}