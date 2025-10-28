package com.example.appsistemainventario.Adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appsistemainventario.Entidades.Categoria
import com.example.appsistemainventario.R

/**
 * Adaptador para la lista de categorías utilizado en un RecyclerView.
 * Permite mostrar los datos de cada categoría y manejar las acciones de edición y eliminación.
 */
class CategoriasAdapter(
    private val lista: List<Categoria>,
    private val onEditarClick: (Categoria) -> Unit,
    private val onEliminarClick: (Categoria) -> Unit
) : RecyclerView.Adapter<CategoriasAdapter.CategoriaViewHolder>() {

    /**
     * ViewHolder que representa cada ítem de la lista de categorías.
     * Se encarga de vincular los datos de la entidad Categoria con los elementos de la vista.
     */
    inner class CategoriaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        // Referencias a los componentes visuales del layout item_categoria
        private val tvNombre: TextView = itemView.findViewById(R.id.tvNombreCategoria)
        private val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcionCategoria)
        private val btnEditar: ImageButton = itemView.findViewById(R.id.btnEditarCategoria)
        private val btnEliminar: ImageButton = itemView.findViewById(R.id.btnEliminarCategoria)

        /**
         * Asigna los valores de una categoría a los elementos del layout y configura los eventos.
         */
        fun bind(categoria: Categoria) {
            tvNombre.text = categoria.nombre
            tvDescripcion.text = categoria.descripcion

            // Configuración de los listeners para las acciones de edición y eliminación
            btnEditar.setOnClickListener { onEditarClick(categoria) }
            btnEliminar.setOnClickListener { onEliminarClick(categoria) }
        }
    }

    /**
     * Crea una nueva vista para un elemento del RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_categoria, parent, false)
        return CategoriaViewHolder(view)
    }

    /**
     * Vincula los datos de la categoría correspondiente a la posición con la vista.
     */
    override fun onBindViewHolder(holder: CategoriaViewHolder, position: Int) {
        holder.bind(lista[position])
    }

    /**
     * Retorna la cantidad total de elementos en la lista.
     */
    override fun getItemCount(): Int = lista.size
}
