package com.example.appsistemainventario.Entidades

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad Producto
 * Representa un producto en el sistema de inventario.
 * Se almacena en la tabla "productos" dentro de la base de datos Room.
 */
@Entity(tableName = "productos")
data class Producto(

    /** Identificador único del producto (clave primaria autogenerada) */
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    /** Nombre del producto */
    var nombre: String,

    /** Precio unitario del producto */
    var precio: Double,

    /** Stock actual del producto */
    var stock: Int,

    /** ID de la categoría a la que pertenece el producto (relación con tabla Categoría) */
    val categoriaId: Int,

    /** ID del proveedor del producto (relación con tabla Proveedor) */
    val proveedorId: Int,

    /** Cantidad de unidades contenidas por paquete, caja o presentación */
    val cantidadPorUnidad: Int,

    /** Unidad de medida del producto (por ejemplo: kg, litros, unidades, etc.) */
    val unidadMedida: String
)
