package com.example.di

import com.example.repositories.ImageRepository
import com.example.repositories.ImageRepositoryImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val imageModule = module {
    singleOf(::ImageRepositoryImpl) {
        bind<ImageRepository>()
    }
}