package com.example.appsistemainventario.Adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.appsistemainventario.Entidades.Movimiento
import com.example.appsistemainventario.Entidades.Producto
import com.example.appsistemainventario.R
import com.example.appsistemainventario.Utils.FormatoFecha
import java.text.SimpleDateFormat

/**
 * Adaptador para la lista de movimientos del inventario.
 * Permite mostrar los datos de entradas y salidas de productos en un RecyclerView.
 */
class MovimientosAdapter(
    private val lista: List<Movimiento>,
    private val productos: List<Producto>
) : RecyclerView.Adapter<MovimientosAdapter.MovimientoViewHolder>() {

    /**
     * ViewHolder que representa cada elemento del listado de movimientos.
     * Se encarga de asignar los valores del modelo Movimiento a los componentes del layout.
     */
    inner class MovimientoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        // Referencias a los elementos del layout item_movimiento.xml
        private val tvProducto: TextView = itemView.findViewById(R.id.tvProductoMovimiento)
        private val tvTipo: TextView = itemView.findViewById(R.id.tvTipoMovimiento)
        private val tvCantidad: TextView = itemView.findViewById(R.id.tvCantidadMovimiento)
        private val tvFecha: TextView = itemView.findViewById(R.id.tvFechaMovimiento)

        /**
         * Asigna los datos de un movimiento a la vista correspondiente.
         */
        fun bind(mov: Movimiento) {
            // Obtiene el nombre del producto asociado al movimiento
            val nombreProducto = productos.find { it.id == mov.productoId }?.nombre ?: "Producto desconocido"
            tvProducto.text = "Producto: $nombreProducto"
            tvCantidad.text = "Cantidad: ${mov.cantidad}"

            // Configura el texto y color del tipo de movimiento (Entrada o Salida)
            tvTipo.text = "Tipo: ${mov.tipo}"
            val color = if (mov.tipo == "ENTRADA") R.color.verdeRegistroProducto else R.color.rojoError
            tvTipo.setTextColor(ContextCompat.getColor(itemView.context, color))

            // Convierte la fecha al formato legible utilizando la utilidad FormatoFecha
            val fechaParseada = SimpleDateFormat("yyyy-MM-dd").parse(mov.fecha)
            val fechaFormateada = FormatoFecha.formatearFecha(fechaParseada)
            tvFecha.text = "Fecha: $fechaFormateada"
        }
    }

    /**
     * Crea y retorna un nuevo ViewHolder inflando el layout correspondiente.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovimientoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movimiento, parent, false)
        return MovimientoViewHolder(view)
    }

    /**
     * Asigna los datos de un movimiento específico a su ViewHolder correspondiente.
     */
    override fun onBindViewHolder(holder: MovimientoViewHolder, position: Int) {
        holder.bind(lista[position])
    }

    /**
     * Devuelve la cantidad total de movimientos en la lista.
     */
    override fun getItemCount(): Int = lista.size
}
