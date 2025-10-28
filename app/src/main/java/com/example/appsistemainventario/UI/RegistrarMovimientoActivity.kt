package com.example.appsistemainventario.UI

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.appsistemainventario.Entidades.*
import com.example.appsistemainventario.R
import com.example.appsistemainventario.Utils.FormatoFecha
import kotlinx.coroutines.*

class RegistrarMovimientoActivity : AppCompatActivity() {

    // Declaración de vistas
    private lateinit var spProducto: Spinner
    private lateinit var spTipoMovimiento: Spinner
    private lateinit var etCantidadMovimiento: EditText
    private lateinit var btnRegistrarMovimiento: Button
    private lateinit var btnVolverDashboard: Button

    // Inicialización de la base de datos
    private val db by lazy { AppDB.getInstance(this) }

    // Alcance principal de corrutinas
    private val uiScope = MainScope()

    // Lista de productos disponibles en el inventario
    private lateinit var listaProductos: List<Producto>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_AppSistemaInventario)
        setContentView(R.layout.activity_registrar_movimiento)

        // Vinculación de vistas con sus elementos del layout
        spProducto = findViewById(R.id.spProducto)
        spTipoMovimiento = findViewById(R.id.spTipoMovimiento)
        etCantidadMovimiento = findViewById(R.id.etCantidadMovimiento)
        btnRegistrarMovimiento = findViewById(R.id.btnRegistrarMovimiento)
        btnVolverDashboard = findViewById(R.id.btnVolverDashboard)

        // Configuración del spinner de tipo de movimiento
        spTipoMovimiento.adapter = ArrayAdapter(
            this,
            R.layout.spinner_item_simple,
            listOf("ENTRADA", "SALIDA")
        ).apply {
            setDropDownViewResource(R.layout.spinner_item_simple)
        }

        // Carga de productos en el spinner desde la base de datos
        uiScope.launch {
            listaProductos = withContext(Dispatchers.IO) {
                db.productosDAO().obtenerTodos()
            }

            spProducto.adapter = ArrayAdapter(
                this@RegistrarMovimientoActivity,
                R.layout.spinner_item_simple,
                listaProductos.map { it.nombre }
            ).apply {
                setDropDownViewResource(R.layout.spinner_item_simple)
            }
        }

        // Acción del botón para registrar movimiento
        btnRegistrarMovimiento.setOnClickListener {
            registrarMovimiento()
        }

        // Acción del botón para volver al Dashboard
        btnVolverDashboard.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }
    }

    // Método principal para registrar un movimiento de entrada o salida
    private fun registrarMovimiento() {
        val productoIndex = spProducto.selectedItemPosition
        val tipo = spTipoMovimiento.selectedItem.toString()
        val cantidad = etCantidadMovimiento.text.toString().toIntOrNull()

        // Validación de los campos del formulario
        if (productoIndex !in listaProductos.indices || cantidad == null || cantidad <= 0) {
            Toast.makeText(this, "⚠️ Completa todos los campos correctamente", Toast.LENGTH_SHORT).show()
            return
        }

        val producto = listaProductos[productoIndex]
        val nuevoStock = when (tipo) {
            "ENTRADA" -> producto.stock + cantidad
            "SALIDA" -> {
                if (cantidad > producto.stock) {
                    Toast.makeText(this, "⚠️ Stock insuficiente", Toast.LENGTH_SHORT).show()
                    return
                }
                producto.stock - cantidad
            }
            else -> return
        }

        // Creación del objeto Movimiento con los datos ingresados
        val movimiento = Movimiento(
            productoId = producto.id,
            tipo = tipo,
            cantidad = cantidad,
            fecha = FormatoFecha.obtenerFechaActual()
        )

        // Inserción del movimiento en la base de datos y actualización del stock
        uiScope.launch {
            withContext(Dispatchers.IO) {
                db.movimientoDAO().insertar(movimiento)
                db.productosDAO().actualizar(producto.copy(stock = nuevoStock))
            }
            Toast.makeText(this@RegistrarMovimientoActivity, "✅ Movimiento registrado", Toast.LENGTH_SHORT).show()
            limpiarCampos()
        }
    }

    // Limpieza de los campos del formulario
    private fun limpiarCampos() {
        etCantidadMovimiento.text?.clear()
        spProducto.setSelection(0)
        spTipoMovimiento.setSelection(0)
    }

    // Cancelación del alcance de corrutinas al destruir la actividad
    override fun onDestroy() {
        super.onDestroy()
        uiScope.cancel()
    }
}
