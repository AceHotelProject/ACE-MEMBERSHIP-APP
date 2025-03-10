package com.dicoding.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import androidx.security.crypto.MasterKey
import com.dicoding.core.data.source.local.datastore.DatastoreManager
import com.dicoding.core.data.source.local.room.membership.MembershipDao
import com.dicoding.core.data.source.local.room.membership.MembershipDatabase
import com.dicoding.core.data.source.local.room.test.StoryDao
import com.dicoding.core.data.source.local.room.test.StoryDatabase
import com.dicoding.core.data.source.local.room.user.UserDao
import com.dicoding.core.data.source.local.room.user.UserDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): DataStore<Preferences> {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() }
            ),
            migrations = listOf(SharedPreferencesMigration(context, TOKEN_MANAGER)),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            produceFile = { context.preferencesDataStoreFile(TOKEN_MANAGER) }
        )
    }

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): StoryDatabase {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val passphrase = "secure_story_database_passphrase".toByteArray()

        val factory = net.sqlcipher.database.SupportFactory(passphrase)

        return Room.databaseBuilder(
            context,
            StoryDatabase::class.java,
            "StoryDatabase.db"
        )
            .openHelperFactory(factory) // Tambahkan factory untuk enkripsi
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideStoryDao(database: StoryDatabase): StoryDao {
        return database.storyDao()
    }

    @Singleton
    @Provides
    fun provideUserDatabase(@ApplicationContext context: Context): UserDatabase {
        // Buat MasterKey untuk enkripsi database
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        // Buat passphrase yang aman (berbeda dari StoryDatabase)
        val passphrase = "secure_user_database_passphrase".toByteArray()

        // Buat SupportFactory dengan passphrase
        val factory = net.sqlcipher.database.SupportFactory(passphrase)

        return Room.databaseBuilder(
            context,
            UserDatabase::class.java,
            "UserDatabase.db"
        )
            .openHelperFactory(factory) // Tambahkan factory untuk enkripsi
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideUserDao(database: UserDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    fun provideTokenManager(dataStore: DataStore<Preferences>): DatastoreManager =
        DatastoreManager(dataStore)

    companion object {
        const val TOKEN_MANAGER = "token_manager"
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): MembershipDatabase {
        return Room.databaseBuilder(
            context,
            MembershipDatabase::class.java,
            "app_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideMembershipDao(database: MembershipDatabase): MembershipDao {
        return database.membershipDao()
    }
}