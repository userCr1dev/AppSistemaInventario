package com.example.appsistemainventario.Adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appsistemainventario.Entidades.Proveedor
import com.example.appsistemainventario.R

/**
 * Adaptador para mostrar la lista de proveedores en un RecyclerView.
 * Permite gestionar las acciones de edición y eliminación de proveedores.
 */
class ProveedoresAdapter(
    private val lista: List<Proveedor>,
    private val onEditar: (Proveedor) -> Unit,
    private val onEliminar: (Proveedor) -> Unit
) : RecyclerView.Adapter<ProveedoresAdapter.ViewHolder>() {

    /**
     * ViewHolder que representa cada elemento individual de la lista de proveedores.
     * Contiene las referencias a los componentes de la vista y sus asignaciones de datos.
     */
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombreProveedor)
        val tvContacto: TextView = itemView.findViewById(R.id.tvContactoProveedor)
        val tvDireccion: TextView = itemView.findViewById(R.id.tvDireccionProveedor)
        val btnEditar: ImageButton = itemView.findViewById(R.id.btnEditarProveedor)
        val btnEliminar: ImageButton = itemView.findViewById(R.id.btnEliminarProveedor)
    }

    /**
     * Crea y retorna un nuevo ViewHolder inflando el layout correspondiente al item de proveedor.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_proveedor, parent, false)
        return ViewHolder(vista)
    }

    /**
     * Asigna los valores del proveedor actual a los componentes visuales del ViewHolder.
     * También configura las acciones de los botones de editar y eliminar.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val proveedor = lista[position]
        holder.tvNombre.text = proveedor.nombre
        holder.tvContacto.text = "${proveedor.telefono} / ${proveedor.correo}"
        holder.tvDireccion.text = proveedor.direccion

        holder.btnEditar.setOnClickListener { onEditar(proveedor) }
        holder.btnEliminar.setOnClickListener { onEliminar(proveedor) }
    }

    /**
     * Devuelve la cantidad total de proveedores en la lista.
     */
    override fun getItemCount(): Int = lista.size
}
