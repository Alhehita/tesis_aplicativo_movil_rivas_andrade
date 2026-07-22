package com.uce.tesisrivasandrade.ui.main.profesor

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.uce.tesisrivasandrade.R
import com.uce.tesisrivasandrade.utils.SessionManager
import kotlinx.coroutines.launch

class ProfesorFragment : Fragment(R.layout.fragment_profesor) {

    private val viewModel: ProfesorViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sessionManager = SessionManager(requireContext())
        val username = sessionManager.getUsername()

        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        val tvMensaje = view.findViewById<TextView>(R.id.tvMensaje)
        val cardRegistros = view.findViewById<View>(R.id.cardRegistros)
        val cardLaboratorios = view.findViewById<View>(R.id.cardLaboratorios)
        val cardEquipos = view.findViewById<View>(R.id.cardEquipos)
        val cardNovedades = view.findViewById<View>(R.id.cardNovedades)

        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                progressBar?.visibility =
                    if (state.isLoading) View.VISIBLE else View.GONE
                state.mensaje?.let {
                    tvMensaje?.text = it
                }
                state.error?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.cargarDatos(username)

        cardRegistros?.setOnClickListener {
            findNavController().navigate(R.id.action_profesorFragment_to_registroUsoMenuFragment)
        }

        cardLaboratorios?.setOnClickListener {
            findNavController().navigate(R.id.action_profesorFragment_to_gestionLaboratoriosFragment)
        }

        cardEquipos?.setOnClickListener {
            findNavController().navigate(R.id.action_profesorFragment_to_gestionEquiposFragment)
        }

        cardNovedades?.setOnClickListener {
            findNavController().navigate(R.id.action_profesorFragment_to_novedadesFragment)
        }
    }
}
