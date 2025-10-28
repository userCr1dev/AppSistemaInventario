package com.example.appsistemainventario.Adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appsistemainventario.Entidades.Categoria
import com.example.appsistemainventario.Entidades.Producto
import com.example.appsistemainventario.Entidades.Proveedor
import com.example.appsistemainventario.R

/**
 * Adaptador para mostrar la lista de productos en un RecyclerView.
 * Incluye la información relacionada con categorías y proveedores.
 * Permite editar y eliminar registros mediante callbacks.
 */
class ProductosAdapter(
    private val productos: List<Producto>,
    private val categorias: List<Categoria> = emptyList(),
    private val proveedores: List<Proveedor> = emptyList(),
    private val onEditar: ((Producto) -> Unit)? = null,
    private val onEliminar: ((Producto) -> Unit)? = null
) : RecyclerView.Adapter<ProductosAdapter.ProductoViewHolder>() {

    /**
     * ViewHolder que representa cada elemento visual de la lista de productos.
     * Contiene referencias a los TextView y botones del layout item_producto.xml.
     */
    inner class ProductoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombreProducto)
        val tvCategoria: TextView = itemView.findViewById(R.id.tvCategoriaProducto)
        val tvProveedor: TextView = itemView.findViewById(R.id.tvProveedorProducto)
        val tvPrecio: TextView = itemView.findViewById(R.id.tvPrecioProducto)
        val tvStock: TextView = itemView.findViewById(R.id.tvStockProducto)
        val tvUnidad: TextView = itemView.findViewById(R.id.tvUnidadProducto)
        val btnEditar: ImageButton = itemView.findViewById(R.id.btnEditarProducto)
        val btnEliminar: ImageButton = itemView.findViewById(R.id.btnEliminarProducto)
    }

    /**
     * Infla el layout correspondiente a cada ítem de producto.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto, parent, false)
        return ProductoViewHolder(view)
    }

    /**
     * Asocia los datos de cada producto con los elementos visuales del layout.
     */
    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = productos[position]

        // Busca los nombres de categoría y proveedor correspondientes
        val categoriaNombre = categorias.find { it.id == producto.categoriaId }?.nombre ?: "Sin categoría"
        val proveedorNombre = proveedores.find { it.id == producto.proveedorId }?.nombre ?: "Sin proveedor"

        // Asigna los valores a los TextView
        holder.tvNombre.text = producto.nombre
        holder.tvPrecio.text = "S/ %.2f".format(producto.precio)
        holder.tvStock.text = "Stock: ${producto.stock}"
        holder.tvUnidad.text = "${producto.cantidadPorUnidad} ${producto.unidadMedida}"
        holder.tvCategoria.text = "Categoría: $categoriaNombre"
        holder.tvProveedor.text = "Proveedor: $proveedorNombre"

        // Configura los botones de edición y eliminación si se proporcionaron callbacks
        if (onEditar != null && onEliminar != null) {
            holder.btnEditar.visibility = View.VISIBLE
            holder.btnEliminar.visibility = View.VISIBLE
            holder.btnEditar.setOnClickListener { onEditar.invoke(producto) }
            holder.btnEliminar.setOnClickListener { onEliminar.invoke(producto) }
        } else {
            holder.btnEditar.visibility = View.GONE
            holder.btnEliminar.visibility = View.GONE
        }
    }

    /**
     * Devuelve la cantidad total de productos a mostrar en la lista.
     */
    override fun getItemCount(): Int = productos.size
}
