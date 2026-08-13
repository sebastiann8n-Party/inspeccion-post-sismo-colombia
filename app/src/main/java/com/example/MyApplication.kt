package com.example

import android.app.Application
import com.example.data.AppDatabase
import com.example.data.BarrioRepository
import com.example.data.InspeccionRepository

class MyApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { InspeccionRepository(database.inspeccionDao()) }
    val barrioRepository by lazy { BarrioRepository(database.barrioDao()) }
}
