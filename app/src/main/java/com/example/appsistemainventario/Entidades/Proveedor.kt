package com.example.appsistemainventario.Entidades

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad Proveedor
 * Representa un proveedor de productos en el sistema de inventario.
 * Se almacena en la tabla "proveedores" dentro de la base de datos Room.
 */
@Entity(tableName = "proveedores")
data class Proveedor(

    /** Identificador único del proveedor (clave primaria autogenerada) */
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    /** Nombre o razón social del proveedor */
    val nombre: String,

    /** Número de RUC del proveedor */
    val ruc: String,

    /** Teléfono de contacto del proveedor */
    val telefono: String,

    /** Correo electrónico del proveedor */
    val correo: String,

    /** Dirección física del proveedor */
    val direccion: String
)
