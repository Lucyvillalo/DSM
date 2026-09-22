package com.example.sqliteapp

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View

import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.sqliteapp.db.HelperDB
import com.example.sqliteapp.model.Categoria
import com.example.sqliteapp.model.Productos
class MainActivity : AppCompatActivity(), View.OnClickListener {
private var managerCategoria: Categoria? = null
private var managerProductos: Productos? = null
private var dbHelper: HelperDB? = null
private var db: SQLiteDatabase? = null
private var cursor: Cursor? = null

private var txtIdDB: TextView? = null
private var txtId: EditText? = null
private var txtNombre: EditText? = null
private var txtPrecio: EditText? = null
private var txtCantidad: EditText? = null
private var cmbCategorias: Spinner? = null

private var btnAgregar: Button? = null
private var btnActualizar: Button? = null
private var btnEliminar: Button? = null
private var btnBuscar: Button? = null

override fun onCreate(savedInstanceState: Bundle?) {
super.onCreate(savedInstanceState)
setContentView(R.layout.activity_main)

txtIdDB = findViewById(R.id.txtIdDB)
txtId = findViewById(R.id.txtId)
txtNombre = findViewById(R.id.txtNombre)
txtPrecio = findViewById(R.id.txtPrecio)
txtCantidad = findViewById(R.id.txtCantidad)
cmbCategorias = findViewById<Spinner>(R.id.cmbCategorias)

btnAgregar = findViewById(R.id.btnAgregar)
btnActualizar = findViewById(R.id.btnActualizar)
btnEliminar = findViewById(R.id.btnEliminar)
btnBuscar = findViewById(R.id.btnBuscar)

dbHelper = HelperDB(this)
db = dbHelper!!.writableDatabase
managerProductos = Productos(this)

setSpinnerCategorias()

btnAgregar!!.setOnClickListener(this)
btnActualizar!!.setOnClickListener(this)
btnEliminar!!.setOnClickListener(this)
btnBuscar!!.setOnClickListener(this)
}

fun setSpinnerCategorias() {
// Cargando valores por defecto
managerCategoria = Categoria(this)
managerCategoria!!.insertValuesDefault()
cursor = managerCategoria!!.showAllCategoria()

var cat = ArrayList<String>()
if (cursor != null && cursor!!.count > 0) {
cursor!!.moveToFirst()
do {
cat.add(cursor!!.getString(1))
} while (cursor!!.moveToNext())
}

cursor?.close()

var adaptador = ArrayAdapter(this, android.R.layout.simple_spinner_item, cat)
adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
cmbCategorias!!.adapter = adaptador
}

override fun onClick(view: View) {
if (db == null || !db!!.isOpen) {
Toast.makeText(this, "No se puede conectar a la Base de Datos", Toast.LENGTH_LONG).show()
return
}

if (view === btnAgregar || view === btnActualizar) {
val opc = if (view === btnAgregar) "insertar" else "actualizar"
if (!vericarFormulario(opc)) return
val nombre = txtNombre!!.text.toString().trim()
val precio = txtPrecio!!.text.toString().trim().toDouble()
val cantidad = txtCantidad!!.text.toString().trim().toInt()
val idcategoria = managerCategoria!!.searchID(cmbCategorias!!.selectedItem.toString())
if (idcategoria == null) {
Toast.makeText(this, "Seleccione una categoria valida", Toast.LENGTH_LONG).show()
return
}

if (view === btnAgregar) {
val id = managerProductos!!.addNewProducto(idcategoria, nombre, precio, cantidad)
if (id != -1L) {
txtId!!.setText(id.toString())
txtIdDB!!.text = id.toString()
Toast.makeText(this, "Producto agregado. Codigo: $id", Toast.LENGTH_LONG).show()
} else {
Toast.makeText(this, "No se pudo agregar el producto", Toast.LENGTH_LONG).show()
}
} else {
val filas = managerProductos!!.updateProducto(
txtId!!.text.toString().trim().toInt(), idcategoria, nombre, precio, cantidad)
if (filas > 0) {
Toast.makeText(this, "Producto actualizado", Toast.LENGTH_LONG).show()
} else {
Toast.makeText(this, "No existe un producto con ese codigo", Toast.LENGTH_LONG).show()
}
}
} else if (view === btnEliminar) {
if (!vericarFormulario("eliminar")) return
val filas = managerProductos!!.deleteProducto(txtId!!.text.toString().trim().toInt())
if (filas > 0) {
txtId!!.text.clear()
limpiarDatosProducto()
Toast.makeText(this, "Producto eliminado", Toast.LENGTH_LONG).show()
} else {
Toast.makeText(this, "No existe un producto con ese codigo", Toast.LENGTH_LONG).show()
}
} else if (view === btnBuscar) {
if (!vericarFormulario("buscar")) return
val resultado = managerProductos!!.searchProducto(txtId!!.text.toString().trim().toInt())
try {
if (resultado != null && resultado.moveToFirst()) {
txtIdDB!!.text = resultado.getInt(0).toString()
txtNombre!!.setText(resultado.getString(2))
txtPrecio!!.setText(resultado.getDouble(3).toString())
txtCantidad!!.setText(resultado.getInt(4).toString())
val categoria = managerCategoria!!.searchNombre(resultado.getInt(1))
for (i in 0 until cmbCategorias!!.count) {
if (cmbCategorias!!.getItemAtPosition(i).toString() == categoria) {
cmbCategorias!!.setSelection(i)
break
}
}
Toast.makeText(this, "Producto encontrado", Toast.LENGTH_LONG).show()
} else {
limpiarDatosProducto()
Toast.makeText(this, "No existe un producto con ese codigo", Toast.LENGTH_LONG).show()
}
} finally {
resultado?.close()
}
}
}

private fun vericarFormulario(opc: String): Boolean {
var response = true
if (opc == "actualizar" || opc == "eliminar" || opc == "buscar") {
val idproducto = txtId!!.text.toString().trim().toIntOrNull()
if (idproducto == null || idproducto <= 0) {
txtId!!.error = "Ingrese un codigo entero mayor que cero"
response = false
}
}
if (opc == "insertar" || opc == "actualizar") {
if (txtNombre!!.text.toString().trim().isEmpty()) {
txtNombre!!.error = "Ingrese el nombre del producto"
response = false
}
val precio = txtPrecio!!.text.toString().trim().toDoubleOrNull()
if (precio == null || !precio.isFinite() || precio <= 0) {
txtPrecio!!.error = "Ingrese un precio mayor que cero"
response = false
}
val cantidad = txtCantidad!!.text.toString().trim().toIntOrNull()
if (cantidad == null || cantidad < 0) {
txtCantidad!!.error = "Ingrese una cantidad entera mayor o igual a cero"
response = false
}
if (cmbCategorias!!.selectedItem == null) {
Toast.makeText(this, "No hay categorias disponibles", Toast.LENGTH_LONG).show()
response = false
}
}
if (!response) {
Toast.makeText(this, "Verifique los campos indicados", Toast.LENGTH_LONG).show()
}
return response
}

private fun limpiarDatosProducto() {
txtIdDB!!.text = ""
txtNombre!!.text.clear()
txtPrecio!!.text.clear()
txtCantidad!!.text.clear()
if (cmbCategorias!!.count > 0) cmbCategorias!!.setSelection(0)
}

override fun onDestroy() {
managerProductos?.close()
managerCategoria?.close()
dbHelper?.close()
super.onDestroy()
}
}
