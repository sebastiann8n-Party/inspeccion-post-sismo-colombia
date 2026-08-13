package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [InspeccionEntity::class, FotoEntity::class, BarrioEntity::class], version = 10, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun inspeccionDao(): InspeccionDao
    abstract fun barrioDao(): BarrioDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "inspecciones_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        populateInitialData(context)
                    }

                    override fun onOpen(db: SupportSQLiteDatabase) {
                        super.onOpen(db)
                        populateInitialData(context)
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }

        private fun populateInitialData(context: Context) {
            CoroutineScope(Dispatchers.IO).launch {
                INSTANCE?.let { database ->
                    if (database.barrioDao().getCount() == 0) {
                        database.barrioDao().insertBarrios(BarrioPreloadData.getInitialBarrios())
                    }
                }
            }
        }
    }
}
