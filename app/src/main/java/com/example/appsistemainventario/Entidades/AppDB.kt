package com.example.appsistemainventario.Entidades

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.appsistemainventario.DAO.*

/**
 * Clase AppDB
 * Define la base de datos principal del sistema de inventario utilizando Room.
 * Incluye todas las entidades y sus respectivos DAO.
 */
@Database(
    entities = [Producto::class, Categoria::class, Proveedor::class, Movimiento::class],
    version = 3,
    exportSchema = false
)
abstract class AppDB : RoomDatabase() {

    /** DAO para operaciones CRUD sobre la tabla de productos */
    abstract fun productosDAO(): ProductosDAO

    /** DAO para operaciones CRUD sobre la tabla de categorías */
    abstract fun categoriaDAO(): CategoriaDAO

    /** DAO para operaciones CRUD sobre la tabla de proveedores */
    abstract fun proveedorDAO(): ProveedorDAO

    /** DAO para operaciones CRUD sobre la tabla de movimientos */
    abstract fun movimientoDAO(): MovimientoDAO

    companion object {
        /** Instancia única de la base de datos (Singleton) */
        @Volatile
        private var INSTANCE: AppDB? = null

        /**
         * Obtiene una instancia única de la base de datos.
         * Si no existe, la crea utilizando Room.
         */
        fun getInstance(context: Context): AppDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDB::class.java,
                    "inventario.db"
                )
                    // Permite destruir y recrear la base de datos si hay un cambio en la versión
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
