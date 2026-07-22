package com.uce.tesisrivasandrade.ui.main.registrouso

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.uce.tesisrivasandrade.R
import com.uce.tesisrivasandrade.data.repository.RegistroUsoRepository
import com.uce.tesisrivasandrade.utils.SessionManager
import kotlinx.coroutines.launch

class RegistroUsoMenuFragment : Fragment(R.layout.fragment_registrar_uso_menu) {

    private lateinit var viewModel: RegistrarUsoMenuViewModel
    private lateinit var adapter: RegistrosUsoAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Inicializar Repositorio y ViewModel
        val repository = RegistroUsoRepository(requireContext())
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return RegistrarUsoMenuViewModel(repository) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[RegistrarUsoMenuViewModel::class.java]

        // 2. Referencias a las vistas
        val rvRegistros = view.findViewById<RecyclerView>(R.id.rvRegistros)
        val btnNuevaEntrada = view.findViewById<MaterialButton>(R.id.btnNuevaEntrada)
        val btnRegistrarSalida = view.findViewById<MaterialButton>(R.id.btnRegistrarSalida)
        val switchVerTodos = view.findViewById<SwitchMaterial>(R.id.switchVerTodos)

        // 3. Lógica de Roles centralizada: Mostrar el switch solo con permisos
        val sessionManager = SessionManager(requireContext())
        val puedeVerTodos = sessionManager.puedeVerTodosLosRegistros()
        
        switchVerTodos.visibility = if (puedeVerTodos) View.VISIBLE else View.GONE

        // 4. Configurar RecyclerView
        adapter = RegistrosUsoAdapter()
        rvRegistros.layoutManager = LinearLayoutManager(requireContext())
        rvRegistros.adapter = adapter

        // 5. Configurar observadores
        lifecycleScope.launch {
            viewModel.registros.collect {
                adapter.submitList(it)
            }
        }

        lifecycleScope.launch {
            viewModel.error.collect { error ->
                error?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                }
            }
        }

        // 6. Lógica del Switch y Carga Inicial
        switchVerTodos.setOnCheckedChangeListener { _, isChecked ->
            viewModel.cargarRegistros(verTodos = isChecked)
        }

        // Carga inicial usando el permiso centralizado
        viewModel.cargarRegistros(verTodos = if (puedeVerTodos) switchVerTodos.isChecked else false)

        // 7. Navegación con Navigation Component
        btnNuevaEntrada.setOnClickListener {
            val bundle = Bundle().apply { putBoolean("isModoSalida", false) }
            findNavController().navigate(R.id.action_registroUsoMenuFragment_to_registroUsoFragment, bundle)
        }

        btnRegistrarSalida.setOnClickListener {
            val bundle = Bundle().apply { putBoolean("isModoSalida", true) }
            findNavController().navigate(R.id.action_registroUsoMenuFragment_to_registroUsoFragment, bundle)
        }
    }
}
