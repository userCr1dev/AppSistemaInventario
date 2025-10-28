package com.example.appsistemainventario.Entidades

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad Movimiento
 * Representa un registro de entrada o salida de productos en el inventario.
 * Se almacena en la tabla "movimientos" dentro de la base de datos Room.
 */
@Entity(tableName = "movimientos")
data class Movimiento(

    /** Identificador único del movimiento (clave primaria autogenerada) */
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    /** ID del producto asociado a este movimiento (relación con la tabla Productos) */
    val productoId: Int,

    /** Tipo de movimiento: puede ser "Entrada" o "Salida" */
    val tipo: String,

    /** Cantidad de unidades involucradas en el movimiento */
    val cantidad: Int,

    /** Fecha en que se realizó el movimiento */
    val fecha: String
)
