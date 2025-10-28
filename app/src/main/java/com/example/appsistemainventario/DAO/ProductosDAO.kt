package com.example.appsistemainventario.DAO

import androidx.room.*
import com.example.appsistemainventario.Entidades.Producto

/**
 * DAO de Productos
 * Define las operaciones CRUD y consultas específicas para la tabla "productos".
 */
@Dao
interface ProductosDAO {

    /** Inserta un nuevo producto en la base de datos */
    @Insert
    suspend fun insertar(producto: Producto)

    /** Actualiza los datos de un producto existente */
    @Update
    suspend fun actualizar(producto: Producto)

    /** Elimina un producto */
    @Delete
    suspend fun eliminar(producto: Producto)

    /** Elimina un producto por su ID */
    @Query("DELETE FROM productos WHERE id = :id")
    suspend fun eliminarPorId(id: Int)

    /** Obtiene todos los productos ordenados alfabéticamente */
    @Query("SELECT * FROM productos ORDER BY nombre ASC")
    suspend fun obtenerTodos(): List<Producto>

    /** Busca productos cuyo nombre contenga el texto indicado */
    @Query("SELECT * FROM productos WHERE nombre LIKE '%' || :nombre || '%' ORDER BY nombre ASC")
    suspend fun buscarPorNombre(nombre: String): List<Producto>

    /** Verifica si existe un producto con el nombre especificado */
    @Query("SELECT COUNT(*) FROM productos WHERE nombre = :nombre")
    suspend fun existe(nombre: String): Int

    /** Devuelve el número total de productos registrados */
    @Query("SELECT COUNT(*) FROM productos")
    suspend fun contarTodos(): Int

    /** Obtiene un producto específico por su ID */
    @Query("SELECT * FROM productos WHERE id = :id LIMIT 1")
    suspend fun obtenerPorId(id: Int): Producto

    /** Obtiene los productos con stock menor al límite indicado */
    @Query("SELECT * FROM productos WHERE stock < :limite ORDER BY stock ASC")
    suspend fun obtenerConStockMenorA(limite: Int): List<Producto>

    /** Obtiene todos los productos de una categoría específica */
    @Query("SELECT * FROM productos WHERE categoriaId = :id ORDER BY nombre ASC")
    suspend fun obtenerPorCategoria(id: Int): List<Producto>

    /** Obtiene todos los productos de un proveedor específico */
    @Query("SELECT * FROM productos WHERE proveedorId = :id ORDER BY nombre ASC")
    suspend fun buscarPorProveedor(id: Int): List<Producto>

    /** Cuenta cuántos productos pertenecen a una categoría */
    @Query("SELECT COUNT(*) FROM productos WHERE categoriaId = :id")
    suspend fun productosPorCategoria(id: Int): Int

    /** Cuenta cuántos productos están asociados a un proveedor */
    @Query("SELECT COUNT(*) FROM productos WHERE proveedorId = :id")
    suspend fun productosPorProveedor(id: Int): Int
}
