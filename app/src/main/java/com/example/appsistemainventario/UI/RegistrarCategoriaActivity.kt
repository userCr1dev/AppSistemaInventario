package com.example.appsistemainventario.UI

import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appsistemainventario.Adaptadores.CategoriasAdapter
import com.example.appsistemainventario.Entidades.AppDB
import com.example.appsistemainventario.Entidades.Categoria
import com.example.appsistemainventario.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.*

class RegistrarCategoriaActivity : AppCompatActivity() {

    // Declaración de componentes de la interfaz
    private lateinit var etNombreCategoria: TextInputEditText
    private lateinit var etDescripcionCategoria: TextInputEditText
    private lateinit var btnGuardarCategoria: MaterialButton
    private lateinit var btnVolverDashboard: MaterialButton
    private lateinit var rvCategorias: RecyclerView
    private lateinit var adapter: CategoriasAdapter

    // Instancia de la base de datos Room
    private val db by lazy { AppDB.getInstance(this) }

    // Alcance principal de corrutinas
    private val uiScope = MainScope()

    // Lista de categorías para el RecyclerView
    private var listaCategorias: List<Categoria> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_AppSistemaInventario)
        setContentView(R.layout.activity_registrar_categoria)

        // Inicialización de vistas
        etNombreCategoria = findViewById(R.id.etNombreCategoria)
        etDescripcionCategoria = findViewById(R.id.etDescripcionCategoria)
        btnGuardarCategoria = findViewById(R.id.btnGuardarCategoria)
        btnVolverDashboard = findViewById(R.id.btnVolverDashboard)
        rvCategorias = findViewById(R.id.rvCategorias)

        // Configuración del RecyclerView
        rvCategorias.layoutManager = LinearLayoutManager(this)

        // Acción para volver al Dashboard
        btnVolverDashboard.setOnClickListener { finish() }

        // Carga inicial de categorías
        cargarCategorias()

        // Acción del botón "Guardar categoría"
        btnGuardarCategoria.setOnClickListener {
            if (!validarCategoria()) return@setOnClickListener

            val categoria = Categoria(
                nombre = etNombreCategoria.text.toString().trim(),
                descripcion = etDescripcionCategoria.text.toString().trim()
            )

            uiScope.launch {
                val existe = withContext(Dispatchers.IO) {
                    db.categoriaDAO().buscarPorNombre(categoria.nombre)
                }

                if (existe != null) {
                    Toast.makeText(this@RegistrarCategoriaActivity, "⚠️ Ya existe una categoría con ese nombre", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                withContext(Dispatchers.IO) {
                    db.categoriaDAO().insertar(categoria)
                }

                limpiarCampos()
                etNombreCategoria.requestFocus()
                Toast.makeText(this@RegistrarCategoriaActivity, "✅ Categoría registrada", Toast.LENGTH_SHORT).show()
                cargarCategorias()
            }
        }
    }

    // Validación de los campos del formulario
    private fun validarCategoria(): Boolean {
        var esValido = true

        if (etNombreCategoria.text.isNullOrBlank()) {
            etNombreCategoria.error = "Ingrese el nombre de la categoría"
            esValido = false
        }

        if (etDescripcionCategoria.text.isNullOrBlank()) {
            etDescripcionCategoria.error = "Ingrese la descripción"
            esValido = false
        }

        return esValido
    }

    // Carga todas las categorías en el RecyclerView
    private fun cargarCategorias() {
        uiScope.launch {
            listaCategorias = withContext(Dispatchers.IO) { db.categoriaDAO().obtenerTodas() }
            adapter = CategoriasAdapter(
                listaCategorias,
                onEditarClick = { categoria -> mostrarDialogEditar(categoria) },
                onEliminarClick = { categoria -> mostrarDialogEliminar(categoria) }
            )
            rvCategorias.adapter = adapter
        }
    }

    // Limpia los campos del formulario
    private fun limpiarCampos() {
        etNombreCategoria.text?.clear()
        etDescripcionCategoria.text?.clear()
    }

    // Muestra el cuadro de diálogo para editar una categoría
    private fun mostrarDialogEditar(categoria: Categoria) {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 32, 48, 0)
        }

        val titulo = TextView(this).apply {
            text = "Editar categoría"
            textSize = 20f
            setTextColor(ContextCompat.getColor(this@RegistrarCategoriaActivity, R.color.azulRegistroCategoria))
            setPadding(0, 0, 0, 16)
            gravity = Gravity.CENTER
        }
        layout.addView(titulo)

        fun crearCampo(label: String, valor: String): TextInputEditText {
            val tvLabel = TextView(this).apply {
                text = label
                setTextColor(ContextCompat.getColor(this@RegistrarCategoriaActivity, R.color.azulRegistroCategoria))
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

        val inputNombre = crearCampo("Nombre", categoria.nombre)
        val inputDescripcion = crearCampo("Descripción", categoria.descripcion)

        val dialog = AlertDialog.Builder(this, R.style.ThemeOverlay_App_Dialog)
            .setView(layout)
            .setPositiveButton("GUARDAR") { _, _ ->
                val actualizado = categoria.copy(
                    nombre = inputNombre.text.toString().trim(),
                    descripcion = inputDescripcion.text.toString().trim()
                )

                uiScope.launch {
                    withContext(Dispatchers.IO) { db.categoriaDAO().actualizar(actualizado) }
                    Toast.makeText(this@RegistrarCategoriaActivity, "✅ Categoría actualizada", Toast.LENGTH_SHORT).show()
                    cargarCategorias()
                }
            }
            .setNegativeButton("CANCELAR", null)
            .create()

        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            ?.setTextColor(ContextCompat.getColor(this, R.color.azulRegistroCategoria))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            ?.setTextColor(ContextCompat.getColor(this, R.color.blanco))
    }

    // Muestra el cuadro de diálogo para confirmar la eliminación de una categoría
    private fun mostrarDialogEliminar(categoria: Categoria) {
        uiScope.launch {
            val productosAsociados = withContext(Dispatchers.IO) {
                db.productosDAO().obtenerPorCategoria(categoria.id)
            }

            if (productosAsociados.isNotEmpty()) {
                Toast.makeText(
                    this@RegistrarCategoriaActivity,
                    "⚠️ No se puede eliminar. Esta categoría está vinculada a productos.",
                    Toast.LENGTH_LONG
                ).show()
                return@launch
            }

            val layout = LinearLayout(this@RegistrarCategoriaActivity).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(48, 32, 48, 0)
                gravity = Gravity.CENTER
            }

            val titulo = TextView(this@RegistrarCategoriaActivity).apply {
                text = "Eliminar categoría"
                textSize = 20f
                setTextColor(ContextCompat.getColor(this@RegistrarCategoriaActivity, R.color.azulRegistroCategoria))
                setPadding(0, 0, 0, 16)
                gravity = Gravity.CENTER
            }

            val mensaje = TextView(this@RegistrarCategoriaActivity).apply {
                text = "¿Deseas eliminar \"${categoria.nombre}\"?"
                textSize = 16f
                setTextColor(ContextCompat.getColor(this@RegistrarCategoriaActivity, R.color.azulRegistroCategoria))
                gravity = Gravity.CENTER
            }

            layout.addView(titulo)
            layout.addView(mensaje)

            val dialog = AlertDialog.Builder(this@RegistrarCategoriaActivity, R.style.ThemeOverlay_App_Dialog)
                .setView(layout)
                .setPositiveButton("ELIMINAR") { _, _ ->
                    uiScope.launch {
                        withContext(Dispatchers.IO) { db.categoriaDAO().eliminar(categoria) }
                        Toast.makeText(this@RegistrarCategoriaActivity, "✅ Categoría eliminada", Toast.LENGTH_SHORT).show()
                        cargarCategorias()
                    }
                }
                .setNegativeButton("CANCELAR", null)
                .create()

            dialog.show()
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                ?.setTextColor(ContextCompat.getColor(this@RegistrarCategoriaActivity, R.color.azulRegistroCategoria))
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                ?.setTextColor(ContextCompat.getColor(this@RegistrarCategoriaActivity, R.color.blanco))
        }
    }

    // Limpieza del alcance de corrutinas al destruir la actividad
    override fun onDestroy() {
        super.onDestroy()
        uiScope.cancel()
    }
}
