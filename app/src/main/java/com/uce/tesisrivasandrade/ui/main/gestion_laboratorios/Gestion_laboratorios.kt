package com.uce.tesisrivasandrade.ui.main.gestion_laboratorios

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.uce.tesisrivasandrade.R
import com.uce.tesisrivasandrade.data.model.registrouso.LaboratorioResponseDTO
import com.uce.tesisrivasandrade.utils.SessionManager
import kotlinx.coroutines.launch

class Gestion_laboratorios : Fragment(R.layout.fragment_gestion_laboratorios) {

    private val viewModel: GestionLaboratoriosViewModel by activityViewModels()
    private lateinit var adapter: LaboratoriosGestionAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sessionManager = SessionManager(requireContext())
        val esAdmin = sessionManager.esAdmin()

        val rv = view.findViewById<RecyclerView>(R.id.rvGestionLaboratorios)
        val fabAgregar = view.findViewById<ExtendedFloatingActionButton>(R.id.fabAgregarLab)
        val btnPapelera = view.findViewById<MaterialButton>(R.id.btnPapelera)

        fabAgregar?.visibility = if (esAdmin) View.VISIBLE else View.GONE
        btnPapelera?.visibility = if (esAdmin) View.VISIBLE else View.GONE

        fabAgregar?.setOnClickListener {
            val bundle = Bundle().apply { putLong("laboratorioId", -1L) }
            findNavController().navigate(R.id.action_gestionLaboratoriosFragment_to_editarLaboratorioFragment, bundle)
        }

        btnPapelera?.setOnClickListener {
            findNavController().navigate(R.id.action_gestionLaboratoriosFragment_to_papeleraFragment)
        }

        adapter = LaboratoriosGestionAdapter(
            laboratorios = emptyList(),
            isAdmin = esAdmin,
            onVerClick = { lab ->
                val bundle = Bundle().apply { putLong("laboratorioId", lab.id) }
                findNavController().navigate(R.id.action_gestionLaboratoriosFragment_to_detalleLaboratorioFragment, bundle)
            },
            onEditarClick = { lab ->
                val bundle = Bundle().apply { putLong("laboratorioId", lab.id) }
                findNavController().navigate(R.id.action_gestionLaboratoriosFragment_to_editarLaboratorioFragment, bundle)
            },
            onEliminarClick = { lab ->
                mostrarDialogoEliminar(lab)
            }
        )
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter

        observeViewModel()
        viewModel.cargarLaboratorios()
    }

    private fun mostrarDialogoEliminar(lab: LaboratorioResponseDTO) {
        MaterialAlertDialogBuilder(requireContext(), R.style.DialogBlackWhite)
            .setTitle("Eliminar Laboratorio")
            .setMessage("¿Estás seguro de que deseas mover a la papelera el laboratorio ${lab.nombre}?")
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Eliminar") { _, _ ->
                viewModel.eliminarLaboratorio(lab.id)
            }
            .show()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.laboratorios.collect { lista ->
                adapter.updateData(lista)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.mensaje.collect { mensaje ->
                mensaje?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    viewModel.clearMensaje()
                }
            }
        }
    }

}
