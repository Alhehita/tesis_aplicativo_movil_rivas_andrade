package com.uce.tesisrivasandrade.ui.main.gestion_laboratorios

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.uce.tesisrivasandrade.R
import com.uce.tesisrivasandrade.data.model.gestion_equipos.GestionEquiposResponse
import com.uce.tesisrivasandrade.data.model.registrouso.LaboratorioResponseDTO
import com.uce.tesisrivasandrade.ui.main.gestion_equipos.GestionEquiposAdapter
import com.uce.tesisrivasandrade.ui.main.gestion_equipos.GestionEquiposViewModel
import kotlinx.coroutines.launch

class PapeleraFragment : Fragment(R.layout.fragment_papelera) {

    private val labViewModel: GestionLaboratoriosViewModel by viewModels()
    private val equipoViewModel: GestionEquiposViewModel by viewModels {
        GestionEquiposViewModel.factory(requireContext())
    }

    private lateinit var rvPapelera: RecyclerView
    private lateinit var tvVacio: TextView
    private var showingLaboratorios = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvPapelera = view.findViewById(R.id.rvPapelera)
        tvVacio = view.findViewById(R.id.tvVacio)
        val toggleGroup = view.findViewById<MaterialButtonToggleGroup>(R.id.toggleGroupPapelera)

        rvPapelera.layoutManager = LinearLayoutManager(requireContext())

        // Listener mejorado para el cambio de pestañas
        toggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                showingLaboratorios = (checkedId == R.id.btnVerLaboratorios)
                actualizarVista()
                cargarDatosDePestana()
            }
        }

        observeViewModels()

        // Carga inicial
        actualizarVista()
        cargarDatosDePestana()
    }

    private fun cargarDatosDePestana() {
        if (showingLaboratorios) {
            labViewModel.cargarLaboratoriosEliminados()
        } else {
            equipoViewModel.cargarEquiposEliminados()
        }
    }

    private fun observeViewModels() {
        viewLifecycleOwner.lifecycleScope.launch {
            labViewModel.laboratoriosEliminados.collect { if (showingLaboratorios) actualizarVista() }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            equipoViewModel.equiposEliminados.collect { if (!showingLaboratorios) actualizarVista() }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            labViewModel.mensaje.collect { mostrarMensaje(it) }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            equipoViewModel.mensaje.collect { mostrarMensaje(it) }
        }
    }

    private fun actualizarVista() {
        if (showingLaboratorios) {
            val lista = labViewModel.laboratoriosEliminados.value
            tvVacio.visibility = if (lista.isEmpty()) View.VISIBLE else View.GONE
            rvPapelera.adapter = LaboratoriosGestionAdapter(
                laboratorios = lista,
                isAdmin = true,
                onVerClick = { }, // No ver en papelera
                onEditarClick = { restaurarLaboratorio(it) },
                onEliminarClick = { eliminarLaboratorioPermanente(it) }
            )
        } else {
            val lista = equipoViewModel.equiposEliminados.value
            tvVacio.visibility = if (lista.isEmpty()) View.VISIBLE else View.GONE
            val adapter = GestionEquiposAdapter(
                isAdmin = true,
                onVerClick = { },
                onEditarClick = { restaurarEquipo(it) },
                onEliminarClick = { eliminarEquipoPermanente(it) }
            )
            adapter.submitList(lista)
            rvPapelera.adapter = adapter
        }
    }

    private fun mostrarMensaje(msj: String?) {
        msj?.let {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            labViewModel.clearMensaje()
            equipoViewModel.clearMensaje()
        }
    }

    // --- Métodos de Restauración y Eliminación (Llaman a los ViewModels) ---
    private fun restaurarLaboratorio(lab: LaboratorioResponseDTO) {
        MaterialAlertDialogBuilder(requireContext(), R.style.DialogBlackWhite)
            .setTitle("Restaurar Laboratorio")
            .setMessage("¿Deseas restaurar el laboratorio ${lab.nombre}?")
            .setPositiveButton("Restaurar") { _, _ -> labViewModel.restaurarLaboratorio(lab.id) }
            .setNegativeButton("Cancelar", null).show()
    }

    private fun eliminarLaboratorioPermanente(lab: LaboratorioResponseDTO) {
        MaterialAlertDialogBuilder(requireContext(), R.style.DialogBlackWhite)
            .setTitle("Eliminar Permanentemente")
            .setMessage("Esta acción es irreversible. ¿Eliminar ${lab.nombre}?")
            .setPositiveButton("Eliminar") { _, _ -> labViewModel.eliminarPermanente(lab.id) }
            .setNegativeButton("Cancelar", null).show()
    }

    private fun restaurarEquipo(equipo: GestionEquiposResponse) {
        MaterialAlertDialogBuilder(requireContext(), R.style.DialogBlackWhite)
            .setTitle("Restaurar Equipo")
            .setMessage("¿Deseas restaurar el equipo ${equipo.codigo}?")
            .setPositiveButton("Restaurar") { _, _ -> equipoViewModel.restaurarEquipo(equipo.id) }
            .setNegativeButton("Cancelar", null).show()
    }

    private fun eliminarEquipoPermanente(equipo: GestionEquiposResponse) {
        MaterialAlertDialogBuilder(requireContext(), R.style.DialogBlackWhite)
            .setTitle("Eliminar Permanentemente")
            .setMessage("Esta acción es irreversible. ¿Eliminar equipo ${equipo.codigo}?")
            .setPositiveButton("Eliminar") { _, _ -> equipoViewModel.eliminarPermanente(equipo.id) }
            .setNegativeButton("Cancelar", null).show()
    }

}