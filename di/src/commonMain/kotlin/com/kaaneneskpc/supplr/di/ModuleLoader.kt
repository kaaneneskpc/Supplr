package com.kaaneneskpc.supplr.di

import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module

object ModuleLoader {
    private val loadedModules: MutableSet<ModuleType> = mutableSetOf()

    enum class ModuleType {
        ADMIN,
        SECONDARY,
        GAMIFICATION
    }

    fun loadModuleIfNeeded(moduleType: ModuleType) {
        if (isModuleLoaded(moduleType)) return
        val module: Module = getModuleForType(moduleType)
        loadKoinModules(module)
        loadedModules.add(moduleType)
    }

    fun isModuleLoaded(moduleType: ModuleType): Boolean = moduleType in loadedModules

    private fun getModuleForType(moduleType: ModuleType): Module {
        return when (moduleType) {
            ModuleType.ADMIN -> adminModule
            ModuleType.SECONDARY -> secondaryModule
            ModuleType.GAMIFICATION -> gamificationModule
        }
    }

    fun resetLoadedModules() {
        loadedModules.clear()
    }
}
