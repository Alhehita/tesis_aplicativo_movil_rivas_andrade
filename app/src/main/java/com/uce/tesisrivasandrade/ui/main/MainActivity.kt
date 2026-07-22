package com.uce.tesisrivasandrade.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.uce.tesisrivasandrade.R
import com.uce.tesisrivasandrade.ui.login.LoginActivity
import com.uce.tesisrivasandrade.utils.JwtUtils
import com.uce.tesisrivasandrade.utils.SessionManager
import android.util.Log

class MainActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sessionManager = SessionManager(this)
        val token = sessionManager.fetchToken()

        if (token == null) {
            irALogin()
            return
        }

        // Configurar el Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.app_name)

        // Configurar NavHostFragment y NavController con startDestination dinámico
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragment_container) as NavHostFragment
        navController = navHostFragment.navController

        // Inflar el navGraph y establecer el startDestination según el rol SOLO en creación inicial
        // Esto evita que adminFragment quede en el back stack
        if (savedInstanceState == null) {
            val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
            val roles = JwtUtils.getRolesFromToken(token)
            val startId = when {
                roles.contains("ROLE_ADMIN") -> R.id.adminFragment
                roles.contains("ROLE_FACULTAD_DIRECTOR_CARRERA") -> R.id.directorCarreraFragment
                roles.contains("ROLE_PROFESOR") -> R.id.profesorFragment
                roles.contains("ROLE_USUARIO") -> R.id.tecnicoFragment
                else -> R.id.profesorFragment
            }
            navGraph.setStartDestination(startId)
            navController.graph = navGraph
        }

        // Configurar ActionBar con NavController (para flecha de retroceso)
        setupActionBarWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        // Forzar el color rojo en el icono de cerrar sesión programáticamente
        menu?.findItem(R.id.action_logout)?.let { menuItem ->
            menuItem.icon?.let { icon ->
                val wrappedDrawable = DrawableCompat.wrap(icon)
                DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(this, R.color.red))
                menuItem.icon = wrappedDrawable
            }
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                try {
                    sessionManager.clear()
                } catch (e: Exception) {
                    Log.e("MainActivity", "Error al limpiar sesión", e)
                }
                irALogin()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun irALogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
