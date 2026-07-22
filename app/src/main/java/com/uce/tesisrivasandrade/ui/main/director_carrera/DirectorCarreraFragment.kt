package com.uce.tesisrivasandrade.ui.main.director_carrera

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.uce.tesisrivasandrade.R
import com.uce.tesisrivasandrade.utils.SessionManager
import kotlinx.coroutines.launch

class DirectorCarreraFragment : Fragment(R.layout.fragment_director_carrera) {

    private val viewModel: DirectorCarreraViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sessionManager = SessionManager(requireContext())
        val username = sessionManager.getUsername()

        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        val tvMensaje = view.findViewById<TextView>(R.id.tvMensaje)
        
        val cardLaboratorios = view.findViewById<View>(R.id.cardLaboratorios)
        val cardEquipos = view.findViewById<View>(R.id.cardEquipos)
        val cardNovedades = view.findViewById<View>(R.id.cardNovedades)
        val cardRegistros = view.findViewById<View>(R.id.cardRegistros)

        // Observar estado del panel
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                progressBar?.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                state.mensaje?.let { tvMensaje?.text = it }
                state.error?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.cargarDatos(username)

        // Navegación con Navigation Component
        cardLaboratorios?.setOnClickListener {
            findNavController().navigate(R.id.action_directorCarreraFragment_to_gestionLaboratoriosFragment)
        }

        cardEquipos?.setOnClickListener {
            findNavController().navigate(R.id.action_directorCarreraFragment_to_gestionEquiposFragment)
        }

        cardNovedades?.setOnClickListener {
            findNavController().navigate(R.id.action_directorCarreraFragment_to_novedadesFragment)
        }

        cardRegistros?.setOnClickListener {
            findNavController().navigate(R.id.action_directorCarreraFragment_to_registroUsoMenuFragment)
        }
    }
}
