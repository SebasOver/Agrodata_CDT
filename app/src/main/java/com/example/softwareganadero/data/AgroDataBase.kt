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
import com.example.softwareganadero.dao.HeatDetectionDao
import com.example.softwareganadero.dao.PastureEvaluationDao
import com.example.softwareganadero.dao.PastureFenceLogDao
import com.example.softwareganadero.dao.PastureInventoryDAO
import com.example.softwareganadero.dao.PrecipitationDAO
import com.example.softwareganadero.dao.ProducerDao
import com.example.softwareganadero.dao.SupplementDao
import com.example.softwareganadero.dao.UserDao
import com.example.softwareganadero.dao.WaterEvaluationDao
import com.example.softwareganadero.dao.corralesDao.HealthControlDao
import com.example.softwareganadero.data.corralesData.HealthControl

@TypeConverters(UserRoleConverter::class) // habilita conversor enum<->TEXT [web:68]
@Database(
    entities = [
        Producer::class, User::class, FemaleCow::class, BirthRecord::class,
        Precipitation::class, PastureInventory::class,
        HeatDetection::class, PastureEvaluation::class, WaterEvaluation::class, PastureFenceLog::class,Supplement::class,
        HealthControl::class,
    ],
    version = 21, // subir desde 9
    exportSchema = true
)    abstract class AgroDatabase : RoomDatabase() {
    abstract fun producerDao(): ProducerDao
    abstract fun userDao(): UserDao
    abstract fun femaleCowDao(): FemaleCowDao
    abstract fun birthRecordDao(): BirthRecordDao
    abstract fun precipitationDao(): PrecipitationDAO
    abstract fun pastureInventoryDao(): PastureInventoryDAO
    abstract fun heatDetectionDao(): HeatDetectionDao
    abstract fun pastureEvaluationDao(): PastureEvaluationDao
    abstract fun waterEvaluationDao(): WaterEvaluationDao
    abstract fun pastureFenceLogDao(): PastureFenceLogDao
    abstract fun supplementDao(): SupplementDao
    abstract fun healthControlDao(): HealthControlDao



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
        val MIGRATION_2_3 = object : Migration( 2, 3) {
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
        val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
            CREATE TABLE IF NOT EXISTS precipitations(
              id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
              amount_mm REAL NOT NULL,
              operator_name TEXT NOT NULL,
              created_at INTEGER NOT NULL,
              created_at_text TEXT NOT NULL
            )
        """.trimIndent())
                db.execSQL("CREATE INDEX IF NOT EXISTS index_precipitations_operator_name ON precipitations(operator_name)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_precipitations_created_at ON precipitations(created_at)")

                db.execSQL("""
            CREATE TABLE IF NOT EXISTS pasture_inventories(
              id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
              healthy INTEGER NOT NULL,
              sick INTEGER NOT NULL,
              total INTEGER NOT NULL,
              operator_name TEXT NOT NULL,
              created_at INTEGER NOT NULL,
              created_at_text TEXT NOT NULL
            )
        """.trimIndent())
                db.execSQL("CREATE INDEX IF NOT EXISTS index_pasture_inventories_operator_name ON pasture_inventories(operator_name)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_pasture_inventories_created_at ON pasture_inventories(created_at)")
            }
        }
        val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
            CREATE TABLE IF NOT EXISTS `producers`(
              `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
              `name` TEXT NOT NULL,
              `created_at` INTEGER NOT NULL
            )
        """.trimIndent())
            }
        }
        val MIGRATION_9_10 = object : Migration(9, 10) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS grazings(
                      id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                      rotacion TEXT NOT NULL,
                      potrero TEXT NOT NULL,
                      created_at INTEGER NOT NULL,
                      created_at_text TEXT NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_grazings_rotacion ON grazings(rotacion)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_grazings_potrero ON grazings(potrero)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_grazings_created_at ON grazings(created_at)")

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS fences_states(
                      id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                      volteos TEXT NOT NULL,
                      created_at INTEGER NOT NULL,
                      created_at_text TEXT NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_fences_states_volteos ON fences_states(volteos)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_fences_states_created_at ON fences_states(created_at)")
            }
        }
        val MIGRATION_10_11 = object : Migration(10, 11) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
            CREATE TABLE IF NOT EXISTS heat_detections(
              id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
              in_heat INTEGER NOT NULL,
              cow_tag TEXT,
              notes TEXT,
              created_at INTEGER NOT NULL,
              created_at_text TEXT NOT NULL
            )
        """.trimIndent())
                db.execSQL("CREATE INDEX IF NOT EXISTS index_heat_detections_cow_tag ON heat_detections(cow_tag)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_heat_detections_created_at ON heat_detections(created_at)")
            }
        }
        val MIGRATION_11_12 = object : Migration(11, 12) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Añadir columna NOT NULL con default 0 para no romper filas existentes
                db.execSQL("ALTER TABLE grazings ADD COLUMN animals_count INTEGER NOT NULL DEFAULT 0")
                // Si prefieres recrear con esquema exacto, crea grazings_new, copia datos y renombra.
            }
        }
        val MIGRATION_12_13 = object : Migration(12, 13) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
            CREATE TABLE IF NOT EXISTS pasture_evaluations(
              id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
              height_entry TEXT,
              height_exit TEXT,
              color TEXT,
              created_at INTEGER NOT NULL,
              created_at_text TEXT NOT NULL
            )
        """.trimIndent())
                db.execSQL("CREATE INDEX IF NOT EXISTS index_pasture_evaluations_created_at ON pasture_evaluations(created_at)")
                db.execSQL("""
            CREATE TABLE IF NOT EXISTS water_evaluations(
              id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
              availability TEXT NOT NULL,
              temperature REAL NOT NULL,
              created_at INTEGER NOT NULL,
              created_at_text TEXT NOT NULL
            )
        """.trimIndent())
                db.execSQL("CREATE INDEX IF NOT EXISTS index_water_evaluations_created_at ON water_evaluations(created_at)")
            }
        }
        val MIGRATION_13_14 = object : Migration(13, 14) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE pasture_evaluations ADD COLUMN color_entry TEXT")
                db.execSQL("ALTER TABLE pasture_evaluations ADD COLUMN color_exit TEXT")
                // Si venías de una única columna 'color', migrar a color_entry:
                //db.execSQL("UPDATE pasture_evaluations SET color_entry = color WHERE color_entry IS NULL AND color IS NOT NULL")
                // (Opcional) luego puedes mantener 'color' o eliminarla recreando la tabla; más simple: déjala sin uso.
            }
        }
        val MIGRATION_14_15 = object : Migration(14, 15) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // 1) Crear tabla nueva con el esquema FINAL (sin 'color', con 'color_entry' y 'color_exit')
                db.execSQL("""
            CREATE TABLE IF NOT EXISTS pasture_evaluations_new(
              id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
              height_entry TEXT,
              height_exit TEXT,
              color_entry TEXT,
              color_exit TEXT,
              created_at INTEGER NOT NULL,
              created_at_text TEXT NOT NULL
            )
        """.trimIndent())

                // 2) Copiar datos desde la tabla vieja, mapeando 'color' -> 'color_entry' si existía
                // Si tu tabla vieja tenía 'color', lo llevamos a color_entry; color_exit quedará NULL
                db.execSQL("""
            INSERT INTO pasture_evaluations_new
            (id, height_entry, height_exit, color_entry, color_exit, created_at, created_at_text)
            SELECT
              id,
              height_entry,
              height_exit,
              CASE
                WHEN instr((SELECT group_concat(name) FROM pragma_table_info('pasture_evaluations')), 'color') > 0
                THEN color
                ELSE NULL
              END AS color_entry,
              NULL AS color_exit,
              created_at,
              created_at_text
            FROM pasture_evaluations
        """.trimIndent())

                // 3) Borrar la tabla vieja
                db.execSQL("DROP TABLE pasture_evaluations")

                // 4) Renombrar la nueva a la original
                db.execSQL("ALTER TABLE pasture_evaluations_new RENAME TO pasture_evaluations")

                // 5) Recrear índices EXACTAMENTE como los espera Room
                db.execSQL("CREATE INDEX IF NOT EXISTS index_pasture_evaluations_created_at ON pasture_evaluations(created_at)")
            }
        }
        val MIGRATION_15_16 = object : Migration(15, 16) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
            CREATE TABLE IF NOT EXISTS pasture_fence_logs(
              id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
              rotacion TEXT NOT NULL,
              potrero TEXT NOT NULL,
              volteos TEXT NOT NULL,
              notes TEXT,
              created_at INTEGER NOT NULL,
              created_at_text TEXT NOT NULL
            )
        """.trimIndent())
                db.execSQL("CREATE INDEX IF NOT EXISTS index_pasture_fence_logs_rotacion ON pasture_fence_logs(rotacion)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_pasture_fence_logs_potrero ON pasture_fence_logs(potrero)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_pasture_fence_logs_volteos ON pasture_fence_logs(volteos)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_pasture_fence_logs_created_at ON pasture_fence_logs(created_at)")

                // Cuando estés listo para retirar las viejas:
                db.execSQL("DROP TABLE IF EXISTS grazings")
                db.execSQL("DROP TABLE IF EXISTS fences_states")
            }
        }
        val MIGRATION_16_17 = object : Migration(16, 17) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE pasture_inventories ADD COLUMN lot INTEGER NOT NULL DEFAULT 0")
            }
        }
        val MIGRATION_17_18 = object : Migration(17, 18) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE pasture_evaluations ADD COLUMN rotation TEXT") // permite NULL
                db.execSQL("ALTER TABLE pasture_evaluations ADD COLUMN paddock TEXT")  // permite NULL
                // recrea el índice si hace falta (ya existe index_pasture_evaluations_created_at)
                //db.execSQL("CREATE INDEX IF NOT EXISTS index_pasture_evaluations_created_at ON pasture_evaluations(created_at)")
            }
        }
        val MIGRATION_18_19 = object : Migration(18, 19) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
            CREATE TABLE IF NOT EXISTS supplements(
              id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
              rotation TEXT NOT NULL,
              lot INTEGER NOT NULL,
              animals_count INTEGER NOT NULL,
              name TEXT NOT NULL,
              quantity REAL NOT NULL,
              created_at INTEGER NOT NULL,
              created_at_text TEXT NOT NULL
            )
        """.trimIndent())
                db.execSQL("CREATE INDEX IF NOT EXISTS index_supplements_rotation ON supplements(rotation)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_supplements_lot ON supplements(lot)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_supplements_created_at ON supplements(created_at)")
            }
        }
        val MIGRATION_19_20 = object : Migration(19, 20) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
            CREATE TABLE IF NOT EXISTS supplements_new(
              id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
              rotation TEXT NOT NULL,
              lot TEXT NOT NULL,                 -- ahora TEXT
              animals_count INTEGER NOT NULL,
              name TEXT NOT NULL,
              quantity REAL NOT NULL,
              created_at INTEGER NOT NULL,
              created_at_text TEXT NOT NULL
            )
        """.trimIndent())
                db.execSQL("""
            INSERT INTO supplements_new (id, rotation, lot, animals_count, name, quantity, created_at, created_at_text)
            SELECT id, rotation, CAST(lot AS TEXT), animals_count, name, quantity, created_at, created_at_text
            FROM supplements
        """.trimIndent())
                db.execSQL("DROP TABLE supplements")
                db.execSQL("ALTER TABLE supplements_new RENAME TO supplements")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_supplements_rotation ON supplements(rotation)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_supplements_lot ON supplements(lot)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_supplements_created_at ON supplements(created_at)")
            }
        }
        val MIGRATION_20_21 = object : Migration(20, 21) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
            CREATE TABLE IF NOT EXISTS health_control(
              id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
              treatment TEXT NOT NULL,
              animal TEXT NOT NULL,
              medicines TEXT NOT NULL,
              dose TEXT NOT NULL,
              quantity TEXT NOT NULL,
              observations TEXT,
              created_at INTEGER NOT NULL,
              created_at_text TEXT NOT NULL
            )
        """.trimIndent())
                db.execSQL("CREATE INDEX IF NOT EXISTS index_health_control_created_at ON health_control(created_at)")
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
                        .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9, MIGRATION_9_10,
                            MIGRATION_10_11, MIGRATION_11_12, MIGRATION_12_13, MIGRATION_13_14, MIGRATION_14_15, MIGRATION_15_16,
                            MIGRATION_16_17, MIGRATION_17_18, MIGRATION_18_19, MIGRATION_19_20,MIGRATION_20_21,)
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
