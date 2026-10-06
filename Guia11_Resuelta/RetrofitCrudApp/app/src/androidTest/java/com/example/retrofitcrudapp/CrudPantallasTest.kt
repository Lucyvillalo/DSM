package com.example.retrofitcrudapp

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.recyclerview.widget.RecyclerView
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CrudPantallasTest {
    @Test fun profesoresCarganYPermitenAbrirEdicion() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            onView(withId(R.id.btnCambiar)).perform(click())
            var cantidad = 0
            val instrumentation = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation()
            val deadline = System.currentTimeMillis() + 30000
            while (cantidad == 0 && System.currentTimeMillis() < deadline) {
                instrumentation.runOnMainSync {
                    val activities = androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry.getInstance()
                        .getActivitiesInStage(androidx.test.runner.lifecycle.Stage.RESUMED)
                    val activity = activities.firstOrNull { it is ProfesorActivity }
                    cantidad = activity?.findViewById<RecyclerView>(R.id.recyclerView)?.adapter?.itemCount ?: 0
                }
                if (cantidad == 0) Thread.sleep(250)
            }
            assertTrue("El listado de profesores debe cargar con las edades de texto del servidor", cantidad > 0)
            instrumentation.runOnMainSync {
                val activity = androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry.getInstance()
                    .getActivitiesInStage(androidx.test.runner.lifecycle.Stage.RESUMED).first { it is ProfesorActivity }
                activity.findViewById<RecyclerView>(R.id.recyclerView).getChildAt(0).performClick()
            }
            onView(withText("Modificar Profesor")).inRoot(androidx.test.espresso.matcher.RootMatchers.isDialog()).perform(click())
            onView(withId(R.id.actualizarButton)).check(matches(isDisplayed()))
            onView(withId(R.id.nombreEditText)).check(matches(isDisplayed()))
        }
    }
    @Test fun crearProfesorValidaLosCamposVacios() {
        ActivityScenario.launch(CrearProfesorActivity::class.java).use {
            onView(withId(R.id.btnGuardar)).perform(click())
            onView(withId(R.id.editTextNombre)).check(matches(hasErrorText("Ingrese el nombre")))
            onView(withId(R.id.editTextApellido)).check(matches(hasErrorText("Ingrese el apellido")))
            onView(withId(R.id.editTextEdad)).check(matches(hasErrorText("Ingrese una edad válida")))
        }
    }
    @Test fun crearAlumnoValidaLosCamposVacios() {
        ActivityScenario.launch(CrearAlumnoActivity::class.java).use {
            onView(withId(R.id.btnGuardar)).perform(click())
            onView(withId(R.id.editTextNombre)).check(matches(hasErrorText("Ingrese el nombre")))
            onView(withId(R.id.editTextEdad)).check(matches(hasErrorText("Ingrese una edad válida")))
        }
    }
}
