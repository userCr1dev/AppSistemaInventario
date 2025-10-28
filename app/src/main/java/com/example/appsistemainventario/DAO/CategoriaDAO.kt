package com.example.appsistemainventario.DAO

import androidx.room.*
import com.example.appsistemainventario.Entidades.Categoria

/**
 * DAO de Categorías
 * Gestiona las operaciones CRUD y consultas específicas para la tabla "categorias".
 */
@Dao
interface CategoriaDAO {

    /** Inserta una nueva categoría */
    @Insert
    suspend fun insertar(categoria: Categoria)

    /** Actualiza los datos de una categoría existente */
    @Update
    suspend fun actualizar(categoria: Categoria)

    /** Elimina una categoría */
    @Delete
    suspend fun eliminar(categoria: Categoria)

    /** Elimina una categoría por su ID */
    @Query("DELETE FROM categorias WHERE id = :id")
    suspend fun eliminarPorId(id: Int)

    /** Obtiene todas las categorías ordenadas por nombre */
    @Query("SELECT * FROM categorias ORDER BY nombre ASC")
    suspend fun obtenerTodas(): List<Categoria>

    /** Busca una categoría por nombre exacto */
    @Query("SELECT * FROM categorias WHERE nombre = :nombre LIMIT 1")
    suspend fun buscarPorNombre(nombre: String): Categoria?

    /** Busca una categoría por su ID */
    @Query("SELECT * FROM categorias WHERE id = :id LIMIT 1")
    suspend fun buscarPorId(id: Int): Categoria?

    /** Verifica si existe una categoría con el nombre indicado */
    @Query("SELECT COUNT(*) FROM categorias WHERE nombre = :nombre")
    suspend fun existe(nombre: String): Int

    /** Devuelve el número total de categorías registradas */
    @Query("SELECT COUNT(*) FROM categorias")
    suspend fun contarTodas(): Int
}
