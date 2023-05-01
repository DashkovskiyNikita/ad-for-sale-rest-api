package com.example.di

import com.example.repositories.AdRepository
import com.example.repositories.AdRepositoryImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val adModule = module {
    singleOf(::AdRepositoryImpl) {
        bind<AdRepository>()
    }
}