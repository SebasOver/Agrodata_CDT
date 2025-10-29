package com.example.softwareganadero.data

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.Room
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.softwareganadero.dao.ProducerDao
import com.example.softwareganadero.dao.UserDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@TypeConverters(UserRoleConverter::class) // habilita conversor enum<->TEXT [web:68]
@Database(
    entities =
        [Producer::class,
            User::class],
            version = 2,
            exportSchema = true)
abstract class AgroDatabase : RoomDatabase() {
    abstract fun producerDao(): ProducerDao
    abstract fun userDao(): UserDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
            CREATE TABLE IF NOT EXISTS `users`(
              `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
              `full_name` TEXT NOT NULL,
              `role` TEXT NOT NULL DEFAULT 'OPERATOR',
              `active` INTEGER NOT NULL DEFAULT 1,
              `created_at` INTEGER NOT NULL
            )
        """.trimIndent())
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_users_full_name` ON `users`(`full_name`)")
            }
        }
        @Volatile private var INSTANCE: AgroDatabase? = null
        fun get(context: Context): AgroDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AgroDatabase::class.java,
                    "agrodata.db"
                )
                    .addMigrations(MIGRATION_1_2) // evita el crash conservando datos
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Insertar estático sin llamar a get(context) ni usar DAOs aquí
                            db.execSQL("""
                    INSERT INTO users (full_name, role, active, created_at) VALUES
                    ('Camilo Rodelo','OPERATOR',1, strftime('%s','now')*1000),
                    ('Jesus Gonzalez','OPERATOR',1, strftime('%s','now')*1000),
                    ('Yaith Salazar','OPERATOR',1, strftime('%s','now')*1000)
                """.trimIndent())
                        }
                    })
                    // En dev, si lo prefieres, puedes añadir además:
                    // .fallbackToDestructiveMigrationOnDowngrade()
                    .build().also { INSTANCE = it }
            }
    }
}
