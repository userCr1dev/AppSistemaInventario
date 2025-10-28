package com.example.appsistemainventario.Entidades

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad Categoria
 * Representa una categoría de productos en el sistema de inventario.
 * Se almacena en la tabla "categorias" dentro de la base de datos Room.
 */
@Entity(tableName = "categorias")
data class Categoria(

    /** Identificador único de la categoría (clave primaria autogenerada) */
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    /** Nombre de la categoría (por ejemplo: Bebidas, Limpieza, Alimentos, etc.) */
    val nombre: String,

    /** Descripción opcional de la categoría */
    val descripcion: String = ""
)
