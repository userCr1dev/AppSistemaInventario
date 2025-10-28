package com.example.appsistemainventario.DAO

import androidx.room.*
import com.example.appsistemainventario.Entidades.Proveedor

/**
 * DAO de Proveedores
 * Gestiona las operaciones CRUD y consultas específicas sobre la tabla "proveedores".
 */
@Dao
interface ProveedorDAO {

    /** Inserta un nuevo proveedor */
    @Insert
    suspend fun insertar(proveedor: Proveedor)

    /** Actualiza los datos de un proveedor existente */
    @Update
    suspend fun actualizar(proveedor: Proveedor)

    /** Elimina un proveedor */
    @Delete
    suspend fun eliminar(proveedor: Proveedor)

    /** Obtiene todos los proveedores ordenados alfabéticamente */
    @Query("SELECT * FROM proveedores ORDER BY nombre ASC")
    suspend fun obtenerTodos(): List<Proveedor>

    /** Busca proveedores por nombre */
    @Query("SELECT * FROM proveedores WHERE nombre LIKE '%' || :nombre || '%' ORDER BY nombre ASC")
    suspend fun buscarPorNombre(nombre: String): List<Proveedor>

    /** Verifica si existe un proveedor con un nombre determinado */
    @Query("SELECT COUNT(*) FROM proveedores WHERE nombre = :nombre")
    suspend fun existePorNombre(nombre: String): Int

    /** Busca un proveedor por su número de RUC */
    @Query("SELECT * FROM proveedores WHERE ruc = :ruc LIMIT 1")
    suspend fun buscarPorRuc(ruc: String): Proveedor?

    /** Obtiene un proveedor por su ID */
    @Query("SELECT * FROM proveedores WHERE id = :id LIMIT 1")
    suspend fun obtenerPorId(id: Int): Proveedor

    /** Devuelve el número total de proveedores registrados */
    @Query("SELECT COUNT(*) FROM proveedores")
    suspend fun contarTodos(): Int
}
