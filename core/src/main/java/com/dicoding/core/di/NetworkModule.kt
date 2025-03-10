package com.dicoding.core.di

import com.dicoding.core.BuildConfig
import com.dicoding.core.data.source.remote.network.ApiService
import com.dicoding.core.di.interceptor.AuthAuthenticator
import com.dicoding.core.di.interceptor.AuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@Module(includes = [DatabaseModule::class])
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.HEADERS
        }
    }

    @Provides
    fun provideCertificatePinner(): CertificatePinner {
        val hostname = BuildConfig.BASE_URL.replace("https://", "").replace("/", "")

        return CertificatePinner.Builder()
            .add(hostname, "sha256/6dUoy56RTnvuYedwmdGBbKMPEK531PHEuoLTATx/J7c=")
            .build()
    }

    @Provides
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        authAuthenticator: AuthAuthenticator,
        certificatePinner: CertificatePinner
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(
                // Hanya tampilkan log detail di mode debug
                if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                else HttpLoggingInterceptor.Level.BASIC
            ))
            .addInterceptor(authInterceptor)
            .authenticator(authAuthenticator)
            // Tambahkan Certificate Pinning
            .certificatePinner(certificatePinner)
            // Atur timeout
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    fun provideApiService(client: OkHttpClient): ApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        return retrofit.create(ApiService::class.java)
    }
}

