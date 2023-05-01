package com.example.di

import com.example.repositories.FavoriteRepository
import com.example.repositories.FavoriteRepositoryImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val favoriteModule = module {
    singleOf(::FavoriteRepositoryImpl) {
        bind<FavoriteRepository>()
    }
}