package com.example.appsistemainventario.UI

import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appsistemainventario.Adaptadores.ProveedoresAdapter
import com.example.appsistemainventario.Entidades.AppDB
import com.example.appsistemainventario.Entidades.Proveedor
import com.example.appsistemainventario.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.*

class RegistrarProveedorActivity : AppCompatActivity() {

    // Vistas
    private lateinit var etNombre: TextInputEditText
    private lateinit var etRuc: TextInputEditText
    private lateinit var etTelefono: TextInputEditText
    private lateinit var etCorreo: TextInputEditText
    private lateinit var etDireccion: TextInputEditText
    private lateinit var btnGuardar: MaterialButton
    private lateinit var btnVolverDashboard: MaterialButton
    private lateinit var rvProveedores: RecyclerView
    private lateinit var adapter: ProveedoresAdapter

    // Base de datos y corrutinas
    private val db by lazy { AppDB.getInstance(this) }
    private val uiScope = MainScope()

    // Lista de proveedores
    private var listaProveedores: List<Proveedor> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_AppSistemaInventario)
        setContentView(R.layout.activity_registrar_proveedor)

        // Inicialización de vistas
        etNombre = findViewById(R.id.etNombreProveedor)
        etRuc = findViewById(R.id.etRucProveedor)
        etTelefono = findViewById(R.id.etTelefonoProveedor)
        etCorreo = findViewById(R.id.etCorreoProveedor)
        etDireccion = findViewById(R.id.etDireccionProveedor)
        btnGuardar = findViewById(R.id.btnGuardarProveedor)
        btnVolverDashboard = findViewById(R.id.btnVolverDashboard)
        rvProveedores = findViewById(R.id.rvProveedores)

        // Configurar RecyclerView
        rvProveedores.layoutManager = LinearLayoutManager(this)

        // Botón: volver al Dashboard
        btnVolverDashboard.setOnClickListener { finish() }

        // Cargar lista de proveedores
        cargarProveedores()

        // Botón: guardar proveedor
        btnGuardar.setOnClickListener {
            if (!validarProveedor()) return@setOnClickListener

            val proveedor = Proveedor(
                nombre = etNombre.text.toString().trim(),
                ruc = etRuc.text.toString().trim(),
                telefono = etTelefono.text.toString().trim(),
                correo = etCorreo.text.toString().trim(),
                direccion = etDireccion.text.toString().trim()
            )

            uiScope.launch {
                val existe = withContext(Dispatchers.IO) {
                    db.proveedorDAO().buscarPorRuc(proveedor.ruc)
                }

                if (existe != null) {
                    Toast.makeText(this@RegistrarProveedorActivity, "⚠️ Ya existe un proveedor con ese RUC", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                withContext(Dispatchers.IO) {
                    db.proveedorDAO().insertar(proveedor)
                }

                limpiarCampos()
                etNombre.requestFocus()
                Toast.makeText(this@RegistrarProveedorActivity, "✅ Proveedor registrado", Toast.LENGTH_SHORT).show()
                cargarProveedores()
            }
        }
    }

    // Validación de datos del proveedor
    private fun validarProveedor(): Boolean {
        var esValido = true

        if (etNombre.text.isNullOrBlank()) {
            etNombre.error = "Ingrese el nombre"
            esValido = false
        }

        if (etRuc.text.isNullOrBlank() || etRuc.text!!.length != 11) {
            etRuc.error = "RUC inválido (11 dígitos)"
            esValido = false
        }

        if (etTelefono.text.isNullOrBlank() || etTelefono.text!!.length < 9) {
            etTelefono.error = "Teléfono inválido"
            esValido = false
        }

        if (etCorreo.text.isNullOrBlank() || !etCorreo.text!!.contains("@")) {
            etCorreo.error = "Correo inválido"
            esValido = false
        }

        if (etDireccion.text.isNullOrBlank()) {
            etDireccion.error = "Ingrese la dirección"
            esValido = false
        }

        return esValido
    }

    // Cargar lista de proveedores desde la base de datos
    private fun cargarProveedores() {
        uiScope.launch {
            listaProveedores = withContext(Dispatchers.IO) { db.proveedorDAO().obtenerTodos() }

            adapter = ProveedoresAdapter(
                listaProveedores,
                onEditar = { proveedor -> mostrarDialogEditar(proveedor) },
                onEliminar = { proveedor -> mostrarDialogEliminar(proveedor) }
            )

            rvProveedores.adapter = adapter
        }
    }

    // Limpiar los campos del formulario
    private fun limpiarCampos() {
        etNombre.text?.clear()
        etRuc.text?.clear()
        etTelefono.text?.clear()
        etCorreo.text?.clear()
        etDireccion.text?.clear()
    }

    // Mostrar diálogo para editar un proveedor
    private fun mostrarDialogEditar(proveedor: Proveedor) {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 32, 48, 0)
        }

        val titulo = TextView(this).apply {
            text = "Editar proveedor"
            textSize = 20f
            setTextColor(ContextCompat.getColor(this@RegistrarProveedorActivity, R.color.naranjaRegistroProveedor))
            setPadding(0, 0, 0, 16)
            gravity = Gravity.CENTER
        }
        layout.addView(titulo)

        fun crearCampo(label: String, valor: String): TextInputEditText {
            val tvLabel = TextView(this).apply {
                text = label
                setTextColor(ContextCompat.getColor(this@RegistrarProveedorActivity, R.color.naranjaRegistroProveedor))
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

        val inputNombre = crearCampo("Nombre", proveedor.nombre)
        val inputRuc = crearCampo("RUC", proveedor.ruc)
        val inputTelefono = crearCampo("Teléfono", proveedor.telefono)
        val inputCorreo = crearCampo("Correo", proveedor.correo)
        val inputDireccion = crearCampo("Dirección", proveedor.direccion)

        val dialog = AlertDialog.Builder(this, R.style.ThemeOverlay_App_Dialog)
            .setView(layout)
            .setPositiveButton("GUARDAR") { _, _ ->
                val actualizado = proveedor.copy(
                    nombre = inputNombre.text.toString().trim(),
                    ruc = inputRuc.text.toString().trim(),
                    telefono = inputTelefono.text.toString().trim(),
                    correo = inputCorreo.text.toString().trim(),
                    direccion = inputDireccion.text.toString().trim()
                )

                uiScope.launch {
                    withContext(Dispatchers.IO) { db.proveedorDAO().actualizar(actualizado) }
                    Toast.makeText(this@RegistrarProveedorActivity, "✅ Proveedor actualizado", Toast.LENGTH_SHORT).show()
                    cargarProveedores()
                }
            }
            .setNegativeButton("CANCELAR", null)
            .create()

        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            ?.setTextColor(ContextCompat.getColor(this, R.color.naranjaRegistroProveedor))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            ?.setTextColor(ContextCompat.getColor(this, R.color.blanco))
    }

    // Mostrar diálogo para eliminar un proveedor
    private fun mostrarDialogEliminar(proveedor: Proveedor) {
        uiScope.launch {
            val productosAsociados = withContext(Dispatchers.IO) {
                db.productosDAO().buscarPorProveedor(proveedor.id)
            }

            if (productosAsociados.isNotEmpty()) {
                Toast.makeText(
                    this@RegistrarProveedorActivity,
                    "⚠️ No se puede eliminar. Este proveedor está vinculado a productos.",
                    Toast.LENGTH_LONG
                ).show()
                return@launch
            }

            val layout = LinearLayout(this@RegistrarProveedorActivity).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(48, 32, 48, 0)
                gravity = Gravity.CENTER
            }

            val titulo = TextView(this@RegistrarProveedorActivity).apply {
                text = "Eliminar proveedor"
                textSize = 20f
                setTextColor(ContextCompat.getColor(this@RegistrarProveedorActivity, R.color.naranjaRegistroProveedor))
                setPadding(0, 0, 0, 16)
                gravity = Gravity.CENTER
            }

            val mensaje = TextView(this@RegistrarProveedorActivity).apply {
                text = "¿Deseas eliminar a ${proveedor.nombre}?"
                textSize = 16f
                setTextColor(ContextCompat.getColor(this@RegistrarProveedorActivity, R.color.naranjaRegistroProveedor))
                gravity = Gravity.CENTER
            }

            layout.addView(titulo)
            layout.addView(mensaje)

            val dialog = AlertDialog.Builder(this@RegistrarProveedorActivity, R.style.ThemeOverlay_App_Dialog)
                .setView(layout)
                .setPositiveButton("ELIMINAR") { _, _ ->
                    uiScope.launch {
                        withContext(Dispatchers.IO) { db.proveedorDAO().eliminar(proveedor) }
                        Toast.makeText(this@RegistrarProveedorActivity, "Proveedor eliminado", Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK)
                        finish()
                    }
                }
                .setNegativeButton("CANCELAR", null)
                .create()

            dialog.show()
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                ?.setTextColor(ContextCompat.getColor(this@RegistrarProveedorActivity, R.color.naranjaRegistroProveedor))
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                ?.setTextColor(ContextCompat.getColor(this@RegistrarProveedorActivity, R.color.blanco))
        }
    }

    // Cancelar corrutinas al destruir la actividad
    override fun onDestroy() {
        super.onDestroy()
        uiScope.cancel()
    }
}
