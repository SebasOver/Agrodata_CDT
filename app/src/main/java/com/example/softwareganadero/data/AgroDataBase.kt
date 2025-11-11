package com.example.softwareganadero.data

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.Room
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.softwareganadero.dao.BirthRecordDao
import com.example.softwareganadero.dao.FemaleCowDao
import com.example.softwareganadero.dao.ProducerDao
import com.example.softwareganadero.dao.UserDao
@TypeConverters(UserRoleConverter::class) // habilita conversor enum<->TEXT [web:68]
@Database(entities = [Producer::class, User::class, FemaleCow::class, BirthRecord::class], version = 7, exportSchema = true)
abstract class AgroDatabase : RoomDatabase() {
    abstract fun producerDao(): ProducerDao
    abstract fun userDao(): UserDao
    abstract fun femaleCowDao(): FemaleCowDao
    abstract fun birthRecordDao(): BirthRecordDao

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
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    INSERT OR IGNORE INTO users (full_name, role, active, created_at) VALUES
                    ('Camilo Rodelo','OPERATOR',1, strftime('%s','now')*1000),
                    ('Jesus Gonzalez','OPERATOR',1, strftime('%s','now')*1000),
                    ('Yaith Salazar','OPERATOR',1, strftime('%s','now')*1000),
                    ('Pedro Maria','ADMIN',1, strftime('%s','now')*1000)
                """.trimIndent())
            }
        }
            val MIGRATION_3_4 = object : Migration(3, 4) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    db.execSQL("""
                        CREATE TABLE IF NOT EXISTS female_cows(
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        tag TEXT NOT NULL,
                        active INTEGER NOT NULL DEFAULT 1,
                        created_at INTEGER NOT NULL
                        )
                        """.trimIndent())
                    db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_female_cows_tag ON female_cows(tag)")
                }
                }
        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
            CREATE TABLE IF NOT EXISTS `birth_records`(
              `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
              `cow_tag` TEXT NOT NULL,
              `calf_tag` TEXT NOT NULL,
              `sex` TEXT NOT NULL,
              `color` TEXT,
              `weight` TEXT,
              `colostrum` INTEGER NOT NULL,
              `notes` TEXT,
              `operator_name` TEXT NOT NULL,
              `created_at` INTEGER NOT NULL
            )
        """.trimIndent())
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_birth_records_cow_tag` ON `birth_records`(`cow_tag`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_birth_records_operator_name` ON `birth_records`(`operator_name`)")
            }
        }
        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // 1) Añadir columna NOT NULL con default para no romper filas existentes
                db.execSQL("ALTER TABLE birth_records ADD COLUMN created_at_text TEXT NOT NULL DEFAULT ''")

                // 2) Poblar el texto legible desde el millis (zona local)
                db.execSQL(
                    """
            UPDATE birth_records
            SET created_at_text = strftime('%Y-%m-%d %H:%M', created_at/1000, 'unixepoch', 'localtime')
            WHERE created_at_text = ''
            """.trimIndent()
                )

                // 3) Asegurar índices con columnas explícitas
                db.execSQL("CREATE INDEX IF NOT EXISTS index_birth_records_cow_tag ON birth_records(cow_tag)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_birth_records_operator_name ON birth_records(operator_name)")
            }
        }
        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("UPDATE birth_records SET operator_name = 'Desconocido' WHERE operator_name IS NULL OR operator_name = ''")
                // Si necesitas asegurar NOT NULL estricta a nivel schema: recrea tabla
                db.execSQL("""
            CREATE TABLE IF NOT EXISTS birth_records_new(
              id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
              cow_tag TEXT NOT NULL,
              calf_tag TEXT NOT NULL,
              sex TEXT NOT NULL,
              color TEXT,
              weight TEXT,
              colostrum INTEGER NOT NULL,
              notes TEXT,
              operator_name TEXT NOT NULL,
              created_at INTEGER NOT NULL,
              created_at_text TEXT NOT NULL
            )
        """.trimIndent())
                db.execSQL("""
            INSERT INTO birth_records_new
            (id,cow_tag,calf_tag,sex,color,weight,colostrum,notes,operator_name,created_at,created_at_text)
            SELECT id,cow_tag,calf_tag,sex,color,weight,colostrum,notes,operator_name,created_at,created_at_text
            FROM birth_records
        """.trimIndent())
                db.execSQL("DROP TABLE birth_records")
                db.execSQL("ALTER TABLE birth_records_new RENAME TO birth_records")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_birth_records_cow_tag ON birth_records(cow_tag)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_birth_records_operator_name ON birth_records(operator_name)")
            }
        }
        @Volatile private var INSTANCE: AgroDatabase? = null
        /*fun get(context: Context): AgroDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(context, AgroDatabase::class.java, "agrodata.db")
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            db.execSQL("""INSERT OR IGNORE INTO users (full_name, role, active, created_at) VALUES
                    ('Camilo Rodelo','OPERATOR',1, strftime('%s','now')*1000),
                    ('Jesus Gonzalez','OPERATOR',1, strftime('%s','now')*1000),
                    ('Yaith Salazar','OPERATOR',1, strftime('%s','now')*1000),
                    ('Pedro Maria','ADMIN',1, strftime('%s','now')*1000) """)
                        }
                        override fun onOpen(db: SupportSQLiteDatabase) {
                            db.execSQL("""INSERT OR IGNORE INTO users (full_name, role, active, created_at) VALUES
                    ('Camilo Rodelo','OPERATOR',1, strftime('%s','now')*1000),
                    ('Jesus Gonzalez','OPERATOR',1, strftime('%s','now')*1000),
                    ('Yaith Salazar','OPERATOR',1, strftime('%s','now')*1000),
                    ('Pedro Maria','ADMIN',1, strftime('%s','now')*1000) """)
                            // Block 1
                            db.execSQL("""
                    INSERT OR IGNORE INTO female_cows(tag, active, created_at) VALUES
                    ('365',1,strftime('%s','now')*1000),('430',1,strftime('%s','now')*1000),
                    ('440',1,strftime('%s','now')*1000),('442',1,strftime('%s','now')*1000),
                    ('444',1,strftime('%s','now')*1000),('446',1,strftime('%s','now')*1000),
                    ('457',1,strftime('%s','now')*1000),('458',1,strftime('%s','now')*1000),
                    ('468',1,strftime('%s','now')*1000),('484',1,strftime('%s','now')*1000),
                    ('01-19',1,strftime('%s','now')*1000),('02-19',1,strftime('%s','now')*1000),
                    ('04-19',1,strftime('%s','now')*1000),('08-19',1,strftime('%s','now')*1000),
                    ('09-19',1,strftime('%s','now')*1000)
                    """.trimIndent())
                            // Block 2
                            db.execSQL("""
                    INSERT OR IGNORE INTO female_cows(tag, active, created_at) VALUES
                    ('10-19',1,strftime('%s','now')*1000),('12-19',1,strftime('%s','now')*1000),
                    ('13-19',1,strftime('%s','now')*1000),('14-19',1,strftime('%s','now')*1000),
                    ('15-19',1,strftime('%s','now')*1000),('17-19',1,strftime('%s','now')*1000),
                    ('18-19',1,strftime('%s','now')*1000),('19-19',1,strftime('%s','now')*1000),
                    ('23-19',1,strftime('%s','now')*1000),('25-19',1,strftime('%s','now')*1000),
                    ('30-20',1,strftime('%s','now')*1000),('32-20',1,strftime('%s','now')*1000),
                    ('34-20',1,strftime('%s','now')*1000),('37-20',1,strftime('%s','now')*1000),
                    ('45-21',1,strftime('%s','now')*1000)
                    """.trimIndent())
                            // Block 3
                            db.execSQL("""
                    INSERT OR IGNORE INTO female_cows(tag, active, created_at) VALUES
                    ('48-21',1,strftime('%s','now')*1000),('55-21',1,strftime('%s','now')*1000),
                    ('56-21',1,strftime('%s','now')*1000),('57-21',1,strftime('%s','now')*1000),
                    ('61-21',1,strftime('%s','now')*1000),('66-22',1,strftime('%s','now')*1000),
                    ('77-23',1,strftime('%s','now')*1000),('80-24',1,strftime('%s','now')*1000),
                    ('84-24',1,strftime('%s','now')*1000),('85-24',1,strftime('%s','now')*1000),
                    ('86-24',1,strftime('%s','now')*1000),('87-24',1,strftime('%s','now')*1000),
                    ('88-24',1,strftime('%s','now')*1000),('90-24',1,strftime('%s','now')*1000),
                    ('91-24',1,strftime('%s','now')*1000)
                    """.trimIndent())
                        }
                    })
                    .build()
            }*/
        fun get(context: Context): AgroDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: run { val isDebug = (context.applicationInfo.flags and
                        android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0
                    val dbName = if (isDebug) "agrodata_dev.db" else "agrodata.db"
                    // 2) Construcción del builder como ya lo tienes
                    val builder = Room.databaseBuilder(
                        context.applicationContext,
                        AgroDatabase::class.java,
                        dbName
                    )
                        .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7)
                        .addCallback(object : RoomDatabase.Callback() {
                            override fun onCreate(db: SupportSQLiteDatabase) {
                                db.execSQL("""
                        INSERT OR IGNORE INTO users (full_name, role, active, created_at) VALUES
                        ('Camilo Rodelo','OPERATOR',1, strftime('%s','now')*1000),
                        ('Jesus Gonzalez','OPERATOR',1, strftime('%s','now')*1000),
                        ('Yaith Salazar','OPERATOR',1, strftime('%s','now')*1000),
                        ('Pedro Maria','ADMIN',1, strftime('%s','now')*1000)
                    """.trimIndent())
                            }
                            override fun onOpen(db: SupportSQLiteDatabase) {
                                db.execSQL("""
                        INSERT OR IGNORE INTO users (full_name, role, active, created_at) VALUES
                        ('Camilo Rodelo','OPERATOR',1, strftime('%s','now')*1000),
                        ('Jesus Gonzalez','OPERATOR',1, strftime('%s','now')*1000),
                        ('Yaith Salazar','OPERATOR',1, strftime('%s','now')*1000),
                        ('Pedro Maria','ADMIN',1, strftime('%s','now')*1000)
                    """.trimIndent())
                                // tus 3 bloques de female_cows como ya están
                                db.execSQL("""
                    INSERT OR IGNORE INTO female_cows(tag, active, created_at) VALUES
                    ('365',1,strftime('%s','now')*1000),('430',1,strftime('%s','now')*1000),
                    ('440',1,strftime('%s','now')*1000),('442',1,strftime('%s','now')*1000),
                    ('444',1,strftime('%s','now')*1000),('446',1,strftime('%s','now')*1000),
                    ('457',1,strftime('%s','now')*1000),('458',1,strftime('%s','now')*1000),
                    ('468',1,strftime('%s','now')*1000),('484',1,strftime('%s','now')*1000),
                    ('01-19',1,strftime('%s','now')*1000),('02-19',1,strftime('%s','now')*1000),
                    ('04-19',1,strftime('%s','now')*1000),('08-19',1,strftime('%s','now')*1000),
                    ('09-19',1,strftime('%s','now')*1000)
                    """.trimIndent())
                                db.execSQL("""
                    INSERT OR IGNORE INTO female_cows(tag, active, created_at) VALUES
                    ('10-19',1,strftime('%s','now')*1000),('12-19',1,strftime('%s','now')*1000),
                    ('13-19',1,strftime('%s','now')*1000),('14-19',1,strftime('%s','now')*1000),
                    ('15-19',1,strftime('%s','now')*1000),('17-19',1,strftime('%s','now')*1000),
                    ('18-19',1,strftime('%s','now')*1000),('19-19',1,strftime('%s','now')*1000),
                    ('23-19',1,strftime('%s','now')*1000),('25-19',1,strftime('%s','now')*1000),
                    ('30-20',1,strftime('%s','now')*1000),('32-20',1,strftime('%s','now')*1000),
                    ('34-20',1,strftime('%s','now')*1000),('37-20',1,strftime('%s','now')*1000),
                    ('45-21',1,strftime('%s','now')*1000)
                    """.trimIndent())
                                db.execSQL("""
                    INSERT OR IGNORE INTO female_cows(tag, active, created_at) VALUES
                    ('48-21',1,strftime('%s','now')*1000),('55-21',1,strftime('%s','now')*1000),
                    ('56-21',1,strftime('%s','now')*1000),('57-21',1,strftime('%s','now')*1000),
                    ('61-21',1,strftime('%s','now')*1000),('66-22',1,strftime('%s','now')*1000),
                    ('77-23',1,strftime('%s','now')*1000),('80-24',1,strftime('%s','now')*1000),
                    ('84-24',1,strftime('%s','now')*1000),('85-24',1,strftime('%s','now')*1000),
                    ('86-24',1,strftime('%s','now')*1000),('87-24',1,strftime('%s','now')*1000),
                    ('88-24',1,strftime('%s','now')*1000),('90-24',1,strftime('%s','now')*1000),
                    ('91-24',1,strftime('%s','now')*1000)
                    """.trimIndent())
                            }
                        })
                    android.util.Log.d("AgroDB", "Abriendo BD: $dbName")
                    builder.build().also { INSTANCE = it }
                }
            }
    }
}
