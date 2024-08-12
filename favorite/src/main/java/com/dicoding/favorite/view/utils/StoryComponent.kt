package com.dicoding.favorite.view.utils

import android.content.Context
import com.dicoding.favorite.view.history.test.DetailStoryActivity
import com.dicoding.membership.di.FavoriteModuleDependencies
import dagger.BindsInstance
import dagger.Component

@Component(dependencies = [FavoriteModuleDependencies::class])
interface StoryComponent {

    fun inject(activity: DetailStoryActivity)

    @Component.Builder
    interface Builder {
        fun context(@BindsInstance context: Context): Builder
        fun appDependencies(favoriteModuleDependencies: FavoriteModuleDependencies): Builder
        fun build(): StoryComponent
    }

}