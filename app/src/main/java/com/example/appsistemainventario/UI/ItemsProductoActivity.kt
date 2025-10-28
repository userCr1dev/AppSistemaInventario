package com.example.appsistemainventario.UI

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appsistemainventario.Adaptadores.ProductosAdapter
import com.example.appsistemainventario.Entidades.*
import com.example.appsistemainventario.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.*

class ItemsProductoActivity : AppCompatActivity() {

    // Elementos de la interfaz
    private lateinit var rvProductos: RecyclerView
    private lateinit var btnRegistrarProducto: Button
    private lateinit var btnVolverDashboard: Button

    // Base de datos y corrutinas
    private val db by lazy { AppDB.getInstance(this) }
    private val uiScope = MainScope()

    // Listas de referencia
    private lateinit var listaCategorias: List<Categoria>
    private lateinit var listaProveedores: List<Proveedor>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_AppSistemaInventario)
        setContentView(R.layout.activity_items_producto)

        // Inicialización de componentes
        rvProductos = findViewById(R.id.rvProductos)
        btnRegistrarProducto = findViewById(R.id.btnRegistrarProducto)
        btnVolverDashboard = findViewById(R.id.btnVolverDashboard)

        rvProductos.layoutManager = LinearLayoutManager(this)

        // Configuración de botones
        btnRegistrarProducto.setOnClickListener {
            startActivity(Intent(this, RegistrarProductoActivity::class.java))
        }

        btnVolverDashboard.setOnClickListener {
            finish()
        }

        // Carga inicial de productos
        cargarProductos()
    }

    // Obtiene los productos y sus relaciones desde la base de datos
    private fun cargarProductos() {
        uiScope.launch {
            val productos = withContext(Dispatchers.IO) { db.productosDAO().obtenerTodos() }
            listaCategorias = withContext(Dispatchers.IO) { db.categoriaDAO().obtenerTodas() }
            listaProveedores = withContext(Dispatchers.IO) { db.proveedorDAO().obtenerTodos() }

            rvProductos.adapter = ProductosAdapter(
                productos = productos,
                categorias = listaCategorias,
                proveedores = listaProveedores,
                onEditar = { producto -> mostrarDialogEditar(producto) },
                onEliminar = { producto -> mostrarDialogEliminar(producto) }
            )
        }
    }

    // Muestra un cuadro de diálogo para editar un producto
    private fun mostrarDialogEditar(producto: Producto) {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 32, 48, 0)
        }

        val titulo = TextView(this).apply {
            text = "Editar producto"
            textSize = 20f
            setTextColor(ContextCompat.getColor(this@ItemsProductoActivity, R.color.verdeRegistroProducto))
            setPadding(0, 0, 0, 16)
            gravity = Gravity.CENTER
        }
        layout.addView(titulo)

        // Función auxiliar para crear campos de texto
        fun crearCampo(label: String, valor: String): TextInputEditText {
            val tvLabel = TextView(this).apply {
                text = label
                setTextColor(ContextCompat.getColor(this@ItemsProductoActivity, R.color.verdeRegistroProducto))
                textSize = 16f
                setPadding(0, 16, 0, 8)
            }

            val inputLayout = TextInputLayout(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                boxBackgroundMode = TextInputLayout.BOX_BACKGROUND_OUTLINE
            }

            val input = TextInputEditText(this).apply {
                setText(valor)
            }

            inputLayout.addView(input)
            layout.addView(tvLabel)
            layout.addView(inputLayout)
            return input
        }

        // Campos editables del producto
        val inputNombre = crearCampo("Nombre", producto.nombre)
        val inputPrecio = crearCampo("Precio", producto.precio.toString())
        val inputStock = crearCampo("Stock", producto.stock.toString())

        val dialog = AlertDialog.Builder(this, R.style.ThemeOverlay_App_Dialog)
            .setView(layout)
            .setPositiveButton("GUARDAR") { _, _ ->
                val actualizado = producto.copy(
                    nombre = inputNombre.text.toString().trim(),
                    precio = inputPrecio.text.toString().toDoubleOrNull() ?: 0.0,
                    stock = inputStock.text.toString().toIntOrNull() ?: 0
                )

                uiScope.launch {
                    withContext(Dispatchers.IO) { db.productosDAO().actualizar(actualizado) }
                    Toast.makeText(this@ItemsProductoActivity, "Producto actualizado", Toast.LENGTH_SHORT).show()
                    cargarProductos()
                }
            }
            .setNegativeButton("CANCELAR", null)
            .create()

        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(ContextCompat.getColor(this, R.color.verdeRegistroProducto))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(ContextCompat.getColor(this, R.color.blanco))
    }

    // Muestra un cuadro de confirmación antes de eliminar un producto
    private fun mostrarDialogEliminar(producto: Producto) {
        uiScope.launch {
            val layout = LinearLayout(this@ItemsProductoActivity).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(48, 32, 48, 0)
                gravity = Gravity.CENTER
            }

            val titulo = TextView(this@ItemsProductoActivity).apply {
                text = "Eliminar producto"
                textSize = 20f
                setTextColor(ContextCompat.getColor(this@ItemsProductoActivity, R.color.verdeRegistroProducto))
                setPadding(0, 0, 0, 16)
                gravity = Gravity.CENTER
            }

            val mensaje = TextView(this@ItemsProductoActivity).apply {
                text = "¿Deseas eliminar \"${producto.nombre}\"?"
                textSize = 16f
                setTextColor(ContextCompat.getColor(this@ItemsProductoActivity, R.color.blanco))
                gravity = Gravity.CENTER
            }

            layout.addView(titulo)
            layout.addView(mensaje)

            val dialog = AlertDialog.Builder(this@ItemsProductoActivity, R.style.ThemeOverlay_App_Dialog)
                .setView(layout)
                .setPositiveButton("ELIMINAR") { _, _ ->
                    uiScope.launch {
                        withContext(Dispatchers.IO) { db.productosDAO().eliminar(producto) }
                        Toast.makeText(this@ItemsProductoActivity, "Producto eliminado", Toast.LENGTH_SHORT).show()
                        cargarProductos()
                    }
                }
                .setNegativeButton("CANCELAR", null)
                .create()

            dialog.show()
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(ContextCompat.getColor(this@ItemsProductoActivity, R.color.verdeRegistroProducto))
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(ContextCompat.getColor(this@ItemsProductoActivity, R.color.blanco))
        }
    }

    // Cancela las corrutinas al destruir la actividad
    override fun onDestroy() {
        super.onDestroy()
        uiScope.cancel()
    }
}
