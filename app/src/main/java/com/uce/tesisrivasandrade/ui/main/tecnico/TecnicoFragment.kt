package com.uce.tesisrivasandrade.ui.main.tecnico

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.uce.tesisrivasandrade.R
import com.uce.tesisrivasandrade.utils.SessionManager
import kotlinx.coroutines.launch

class TecnicoFragment : Fragment(R.layout.fragment_tecnico) {

    private val viewModel: TecnicoViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sessionManager = SessionManager(requireContext())
        val username = sessionManager.getUsername()

        val loadingContainer = view.findViewById<View>(R.id.loadingContainer)
        val tvMensaje = view.findViewById<TextView>(R.id.tvMensaje)
        val cardLaboratorios = view.findViewById<View>(R.id.cardLaboratorios)
        val cardEquipos = view.findViewById<View>(R.id.cardEquipos)
        val cardNovedades = view.findViewById<View>(R.id.cardNovedades)

        // Observar estado del ViewModel
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                loadingContainer?.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                state.mensaje?.let { tvMensaje?.text = it }
                state.error?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.cargarDatos(username)

        cardLaboratorios?.setOnClickListener {
            findNavController().navigate(R.id.action_tecnicoFragment_to_gestionLaboratoriosFragment)
        }

        cardEquipos?.setOnClickListener {
            findNavController().navigate(R.id.action_tecnicoFragment_to_gestionEquiposFragment)
        }

        // Al navegar desde técnico, el action en nav_graph.xml ya define soloMias=true
        cardNovedades?.setOnClickListener {
            findNavController().navigate(R.id.action_tecnicoFragment_to_novedadesFragment)
        }
    }
}
