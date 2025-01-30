package com.dicoding.membership.di

import com.dicoding.core.data.repository.PromoRepository
import com.dicoding.core.domain.auth.interactor.AuthInteractor
import com.dicoding.core.domain.auth.usecase.AuthUseCase
import com.dicoding.core.domain.membership.interactor.MembershipInteractor
import com.dicoding.core.domain.membership.usecase.MembershipUseCase
import com.dicoding.core.domain.points.interactor.PointsInteractor
import com.dicoding.core.domain.points.usecase.PointsUseCase
import com.dicoding.core.domain.merchants.interactor.MerchantInteractor
import com.dicoding.core.domain.merchants.usecase.MerchantUseCase
import com.dicoding.core.domain.promo.interactor.PromoInteractor
import com.dicoding.core.domain.promo.usecase.PromoUseCase
import com.dicoding.core.domain.test.auth.usecase.AuthUseCaseTester
import com.dicoding.core.domain.test.story.usecase.StoryUseCaseTester
import com.dicoding.membership.core.domain.auth.tester.interactor.AuthInteractorTester
import com.dicoding.core.domain.test.story.interactor.StoryInteractorTester
import com.dicoding.core.domain.user.interactor.UserInteractor
import com.dicoding.core.domain.user.usecase.UserUseCase
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


    @Binds
    @Singleton
    abstract fun provideUserUseCase(userInteractor: UserInteractor): UserUseCase

    @Binds
    @Singleton
    abstract fun provideMembershipUseCase(membershipInteractor: MembershipInteractor): MembershipUseCase

    @Binds
    @Singleton
    abstract fun providePromoUseCase(promoInteractor: PromoInteractor): PromoUseCase

    @Binds
    @Singleton
    abstract fun provideMerchantUseCase(merchantInteractor: MerchantInteractor): MerchantUseCase
    @Binds
    @Singleton
    abstract fun providePointsUseCase(pointsInteractor: PointsInteractor): PointsUseCase

}