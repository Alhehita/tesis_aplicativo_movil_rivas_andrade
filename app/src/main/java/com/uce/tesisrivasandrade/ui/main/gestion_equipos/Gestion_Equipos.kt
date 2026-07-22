package com.uce.tesisrivasandrade.ui.main.gestion_equipos

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
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
import com.uce.tesisrivasandrade.data.repository.GestionLaboratorios
import com.uce.tesisrivasandrade.utils.SessionManager
import kotlinx.coroutines.launch

class Gestion_Equipos : Fragment(R.layout.fragment_gestion__equipos) {

    private val viewModel: GestionEquiposViewModel by activityViewModels {
        GestionEquiposViewModel.factory(requireContext())
    }

    private lateinit var adapter: GestionEquiposAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sessionManager = SessionManager(requireContext())
        val esAdmin = sessionManager.esAdmin()

        val spinnerLaboratorio = view.findViewById<AutoCompleteTextView>(R.id.spinnerLaboratorioFiltro)
        val fabAgregar = view.findViewById<ExtendedFloatingActionButton>(R.id.fabAgregarEquipo)
        val btnPapelera = view.findViewById<MaterialButton>(R.id.btnPapelera)

        fabAgregar.visibility = if (esAdmin) View.VISIBLE else View.GONE
        btnPapelera.visibility = if (esAdmin) View.VISIBLE else View.GONE

        fabAgregar.setOnClickListener {
            val bundle = Bundle().apply { putLong("equipoId", -1L) }
            findNavController().navigate(R.id.action_gestionEquiposFragment_to_editarEquipoFragment, bundle)
        }

        btnPapelera.setOnClickListener {
            findNavController().navigate(R.id.action_gestionEquiposFragment_to_papeleraFragment)
        }

        val rv = view.findViewById<RecyclerView>(R.id.rvGestionLaboratorios)
        adapter = GestionEquiposAdapter(
            isAdmin = esAdmin,
            onVerClick = { equipo ->
                val bundle = Bundle().apply { putLong("equipoId", equipo.id) }
                findNavController().navigate(R.id.action_gestionEquiposFragment_to_detalleEquipoFragment, bundle)
            },
            onEditarClick = { equipo ->
                val bundle = Bundle().apply { putLong("equipoId", equipo.id) }
                findNavController().navigate(R.id.action_gestionEquiposFragment_to_editarEquipoFragment, bundle)
            },
            onEliminarClick = { equipo ->
                mostrarDialogoEliminar(equipo.id, equipo.codigo)
            }
        )
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter

        cargarFiltroLaboratorios(spinnerLaboratorio)
        spinnerLaboratorio.setOnItemClickListener { parent, _, position, _ ->
            val seleccion = parent.getItemAtPosition(position).toString()
            viewModel.filtrarPorLaboratorio(seleccion)
        }

        observeViewModel()
        viewModel.cargarEquipos()
    }

    private fun cargarFiltroLaboratorios(spinner: AutoCompleteTextView) {
        val repoLabs = GestionLaboratorios(requireContext())
        lifecycleScope.launch {
            repoLabs.listarActivos().onSuccess { laboratorios ->
                val nombres = mutableListOf("Todos")
                nombres.addAll(laboratorios.map { it.nombre })
                val adapter = ArrayAdapter(
                    requireContext(),
                    R.layout.item_dropdown,
                    nombres
                )
                spinner.setAdapter(adapter)
            }
        }
    }

    private fun mostrarDialogoEliminar(id: Long, codigo: String) {
        MaterialAlertDialogBuilder(requireContext(), R.style.DialogBlackWhite)
            .setTitle("Eliminar Equipo")
            .setMessage("¿Estás seguro de que deseas mover a la papelera el equipo $codigo?")
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Eliminar") { _, _ ->
                viewModel.eliminarEquipo(id)
            }
            .show()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.equipos.collect { lista ->
                adapter.submitList(lista)
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
