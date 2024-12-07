package com.dicoding.membership.di

import com.dicoding.core.domain.test.story.usecase.StoryUseCaseTester
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ListFavoriteModuleDependencies {

    fun favoriteUseCase(): StoryUseCaseTester
}