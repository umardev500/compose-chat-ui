package com.umar.chat.dependency

import com.umar.chat.data.network.ChatApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ChatNetworkModule {
    @Provides
    fun provideChatApiService(): ChatApiService {
        return ChatApiService()
    }
}