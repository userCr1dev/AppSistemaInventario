package com.example.appsistemainventario.UI

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appsistemainventario.Adaptadores.ProductosAdapter
import com.example.appsistemainventario.Entidades.AppDB
import com.example.appsistemainventario.Entidades.Producto
import com.example.appsistemainventario.R
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.*

class DashboardActivity : AppCompatActivity() {

    // Referencias a elementos de la interfaz
    private lateinit var tvTotalProductos: TextView
    private lateinit var tvStockBajo: TextView
    private lateinit var tvTotalCategorias: TextView
    private lateinit var tvTotalProveedores: TextView
    private lateinit var cardProductos: LinearLayout
    private lateinit var recyclerStockBajo: RecyclerView
    private lateinit var btnActualizarDashboard: MaterialButton

    // Instancia de la base de datos y alcance de corrutinas
    private val db by lazy { AppDB.getInstance(this) }
    private val uiScope = MainScope()

    // Variables temporales para datos
    private var productosStockBajo: List<Producto> = emptyList()
    private var listaVisible = false

    // Registro de proveedor con resultado
    private val registrarProveedor = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            cargarDatos()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_AppSistemaInventario)
        setContentView(R.layout.activity_dashboard)

        inicializarUI()
        cargarDatos()
        configurarBotones()
    }

    // Inicializa los elementos de la interfaz
    private fun inicializarUI() {
        tvTotalProductos = findViewById(R.id.tvTotalProductos)
        tvStockBajo = findViewById(R.id.tvStockBajo)
        tvTotalCategorias = findViewById(R.id.tvTotalCategorias)
        tvTotalProveedores = findViewById(R.id.tvTotalProveedores)
        cardProductos = findViewById(R.id.cardProductos)
        recyclerStockBajo = findViewById(R.id.recyclerStockBajo)
        btnActualizarDashboard = findViewById(R.id.btnActualizarDashboard)

        recyclerStockBajo.layoutManager = LinearLayoutManager(this)
    }

    // Carga los datos del dashboard desde la base de datos
    private fun cargarDatos() {
        uiScope.launch {
            val totalProductos = withContext(Dispatchers.IO) {
                db.productosDAO().contarTodos()
            }

            productosStockBajo = withContext(Dispatchers.IO) {
                db.productosDAO().obtenerConStockMenorA(5)
            }

            val totalCategorias = withContext(Dispatchers.IO) {
                db.categoriaDAO().contarTodas()
            }

            val totalProveedores = withContext(Dispatchers.IO) {
                db.proveedorDAO().contarTodos()
            }

            // Muestra los resultados en la interfaz
            tvTotalProductos.text = "Total de productos: $totalProductos"
            tvStockBajo.text = "Stock bajo: ${productosStockBajo.size}"
            tvTotalCategorias.text = "Total de categorías: $totalCategorias"
            tvTotalProveedores.text = "Total de proveedores: $totalProveedores"

            configurarTarjetaStockBajo()
        }
    }

    // Configura la tarjeta que muestra los productos con bajo stock
    private fun configurarTarjetaStockBajo() {
        if (productosStockBajo.isNotEmpty()) {
            cardProductos.setOnClickListener {
                listaVisible = !listaVisible
                recyclerStockBajo.visibility = if (listaVisible) View.VISIBLE else View.GONE
                if (listaVisible) {
                    recyclerStockBajo.adapter = ProductosAdapter(productosStockBajo)
                }
            }
        } else {
            cardProductos.setOnClickListener(null)
            recyclerStockBajo.visibility = View.GONE
        }
    }

    // Configura los botones del dashboard y sus acciones
    private fun configurarBotones() {
        findViewById<Button>(R.id.btnRegistrarProducto).setOnClickListener {
            startActivity(Intent(this, RegistrarProductoActivity::class.java))
        }

        findViewById<Button>(R.id.btnRegistrarCategoria).setOnClickListener {
            startActivity(Intent(this, RegistrarCategoriaActivity::class.java))
        }

        findViewById<Button>(R.id.btnRegistrarProveedor).setOnClickListener {
            registrarProveedor.launch(Intent(this, RegistrarProveedorActivity::class.java))
        }

        findViewById<Button>(R.id.btnRegistrarMovimiento).setOnClickListener {
            startActivity(Intent(this, RegistrarMovimientoActivity::class.java))
        }

        findViewById<Button>(R.id.btnActualizarDashboard).setOnClickListener {
            cargarDatos()
            Toast.makeText(this, "Panel actualizado", Toast.LENGTH_SHORT).show()
        }
    }

    // Cancela las corrutinas al destruir la actividad
    override fun onDestroy() {
        super.onDestroy()
        uiScope.cancel()
    }
}
