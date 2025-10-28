package com.example.appsistemainventario.UI

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appsistemainventario.Entidades.*
import com.example.appsistemainventario.R
import com.example.appsistemainventario.Utils.FormatoFecha
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.*

class RegistrarProductoActivity : AppCompatActivity() {

    // Vistas
    private lateinit var etNombre: TextInputEditText
    private lateinit var etPrecio: TextInputEditText
    private lateinit var etStock: TextInputEditText
    private lateinit var etCantidadPorUnidad: TextInputEditText
    private lateinit var spCategoria: Spinner
    private lateinit var spProveedor: Spinner
    private lateinit var spUnidadMedida: Spinner
    private lateinit var btnGuardar: MaterialButton
    private lateinit var btnVerProductos: MaterialButton
    private lateinit var btnVolverDashboard: MaterialButton

    // Listas de datos
    private lateinit var listaCategorias: List<Categoria>
    private lateinit var listaProveedores: List<Proveedor>

    // Lista fija para unidades de medida
    private val unidades = listOf("Seleccionar...", "unidad", "paquete", "g", "kg", "ml", "L")

    // Base de datos y ámbito de corrutinas
    private val db by lazy { AppDB.getInstance(this) }
    private val uiScope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_AppSistemaInventario)
        setContentView(R.layout.activity_registrar_producto)

        // Inicialización de vistas
        etNombre = findViewById(R.id.etNombreProducto)
        etPrecio = findViewById(R.id.etPrecioProducto)
        etStock = findViewById(R.id.etStockProducto)
        etCantidadPorUnidad = findViewById(R.id.etCantidadPorUnidad)
        spCategoria = findViewById(R.id.spCategoria)
        spProveedor = findViewById(R.id.spProveedor)
        spUnidadMedida = findViewById(R.id.spUnidadMedida)
        btnGuardar = findViewById(R.id.btnGuardarProducto)
        btnVerProductos = findViewById(R.id.btnVerProductos)
        btnVolverDashboard = findViewById(R.id.btnVolverDashboard)

        // Spinner de unidades (lista fija)
        spUnidadMedida.adapter = ArrayAdapter(
            this,
            R.layout.spinner_item_simple,
            unidades
        ).apply {
            setDropDownViewResource(R.layout.spinner_item_simple)
        }

        // Cargar categorías y proveedores desde Room
        uiScope.launch {
            listaCategorias = withContext(Dispatchers.IO) { db.categoriaDAO().obtenerTodas() }
            listaProveedores = withContext(Dispatchers.IO) { db.proveedorDAO().obtenerTodos() }

            spCategoria.adapter = ArrayAdapter(
                this@RegistrarProductoActivity,
                R.layout.spinner_item_simple,
                listaCategorias.map { it.nombre }
            )

            spProveedor.adapter = ArrayAdapter(
                this@RegistrarProductoActivity,
                R.layout.spinner_item_simple,
                listaProveedores.map { it.nombre }
            )
        }

        // Botón: guardar producto
        btnGuardar.setOnClickListener {
            guardarProducto()
        }

        // Botón: ver productos (abre ItemsProductoActivity)
        btnVerProductos.setOnClickListener {
            val intent = Intent(this, ItemsProductoActivity::class.java)
            startActivity(intent)
        }

        // Botón: volver al dashboard principal
        btnVolverDashboard.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }
    }

    // Guardar producto en la base de datos
    private fun guardarProducto() {
        val nombre = etNombre.text.toString().trim()
        val precio = etPrecio.text.toString().toDoubleOrNull()
        val stock = etStock.text.toString().toIntOrNull()
        val cantidadPorUnidad = etCantidadPorUnidad.text.toString().toIntOrNull() ?: 1
        val unidadMedida = spUnidadMedida.selectedItem.toString()
        val categoriaIndex = spCategoria.selectedItemPosition
        val proveedorIndex = spProveedor.selectedItemPosition

        // Validación de campos
        if (
            nombre.isEmpty() || precio == null || stock == null ||
            categoriaIndex !in listaCategorias.indices ||
            proveedorIndex !in listaProveedores.indices ||
            unidadMedida == "Seleccionar..."
        ) {
            Toast.makeText(this, "Completa todos los campos correctamente", Toast.LENGTH_SHORT).show()
            return
        }

        // Crear objeto Producto
        val producto = Producto(
            nombre = nombre,
            precio = precio,
            stock = stock,
            unidadMedida = unidadMedida,
            cantidadPorUnidad = cantidadPorUnidad,
            categoriaId = listaCategorias[categoriaIndex].id,
            proveedorId = listaProveedores[proveedorIndex].id
        )

        // Insertar producto en la base de datos
        uiScope.launch {
            withContext(Dispatchers.IO) {
                db.productosDAO().insertar(producto)

                // Registrar movimiento si hay stock inicial
                if (stock > 0) {
                    val fecha = FormatoFecha.obtenerFechaActual()
                    val movimiento = Movimiento(
                        productoId = producto.id,
                        tipo = "ENTRADA",
                        cantidad = stock,
                        fecha = fecha
                    )
                    db.movimientoDAO().insertar(movimiento)
                }
            }

            // Limpiar campos y mostrar mensaje
            limpiarCampos()
            Toast.makeText(this@RegistrarProductoActivity, "Producto registrado", Toast.LENGTH_SHORT).show()
        }
    }

    // Limpiar los campos del formulario
    private fun limpiarCampos() {
        etNombre.text?.clear()
        etPrecio.text?.clear()
        etStock.text?.clear()
        etCantidadPorUnidad.text?.clear()
        spCategoria.setSelection(0)
        spProveedor.setSelection(0)
        spUnidadMedida.setSelection(0)
    }

    // Cancelar corrutinas al destruir la actividad
    override fun onDestroy() {
        super.onDestroy()
        uiScope.cancel()
    }
}
