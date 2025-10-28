package com.example.appsistemainventario.DAO

import androidx.room.*
import com.example.appsistemainventario.Entidades.Movimiento

/**
 * DAO de Movimientos
 * Gestiona las operaciones relacionadas con entradas y salidas de productos.
 */
@Dao
interface MovimientoDAO {

    /** Inserta un nuevo registro de movimiento (entrada o salida) */
    @Insert
    suspend fun insertar(movimiento: Movimiento)

    /** Obtiene todos los movimientos ordenados por fecha descendente */
    @Query("SELECT * FROM movimientos ORDER BY fecha DESC")
    suspend fun obtenerTodos(): List<Movimiento>

    /** Obtiene los movimientos asociados a un producto específico */
    @Query("SELECT * FROM movimientos WHERE productoId = :id ORDER BY fecha DESC")
    suspend fun obtenerPorProducto(id: Int): List<Movimiento>

    /** Calcula el stock de un producto a partir de sus movimientos */
    @Query("SELECT SUM(CASE WHEN tipo = 'ENTRADA' THEN cantidad ELSE -cantidad END) FROM movimientos WHERE productoId = :id")
    suspend fun calcularStockPorMovimientos(id: Int): Int
}
