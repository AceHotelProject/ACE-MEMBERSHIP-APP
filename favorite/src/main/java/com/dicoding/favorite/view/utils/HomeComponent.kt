package com.dicoding.favorite.view.utils

import android.content.Context
import com.dicoding.favorite.view.home.HomeFragment
import com.dicoding.membership.di.ListFavoriteModuleDependencies
import dagger.BindsInstance
import dagger.Component

@Component(dependencies = [ListFavoriteModuleDependencies::class])
interface HomeComponent {
    fun inject(fragment: HomeFragment)

    @Component.Builder
    interface Builder {
        fun context(@BindsInstance context: Context): Builder
        fun appDependencies(listFavoriteModuleDependencies: ListFavoriteModuleDependencies): Builder
        fun build(): HomeComponent
    }
}
