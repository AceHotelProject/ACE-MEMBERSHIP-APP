package com.dicoding.core.di

import com.dicoding.core.data.repository.AuthRepository
import com.dicoding.core.data.repository.PromoRepository
import com.dicoding.core.data.repository.MembershipRepository
import com.dicoding.core.data.repository.PointsRepository
import com.dicoding.core.data.repository.MerchantRepository
import com.dicoding.core.data.repository.test.AuthRepositoryTester
import com.dicoding.core.data.repository.test.StoryRepositoryTester
import com.dicoding.core.domain.auth.repository.IAuthRepository
import com.dicoding.core.domain.promo.repository.IPromoRepository
import com.dicoding.core.domain.membership.repository.IMembershipRepository
import com.dicoding.core.domain.points.repository.IPointsRepository
import com.dicoding.core.domain.merchants.repository.IMerchantRepository
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

    @Binds
    @Singleton
    abstract fun provideMembershipRepository(membershipRepository: MembershipRepository): IMembershipRepository

    @Binds
    @Singleton
    abstract fun providePromoRepository(promoRepository: PromoRepository): IPromoRepository

    @Binds
    @Singleton
    abstract fun provideMerchantRepository(merchantRepository: MerchantRepository):IMerchantRepository

    @Binds
    @Singleton
    abstract fun providePointsRepository(pointsRepository: PointsRepository): IPointsRepository

}