package com.uce.tesisrivasandrade.ui.main.registrouso

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.uce.tesisrivasandrade.R
import com.uce.tesisrivasandrade.data.model.registrouso.LaboratorioResponseDTO
import com.uce.tesisrivasandrade.data.repository.RegistroUsoRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RegistroUsoFragment : Fragment(R.layout.fragment_registro_uso) {

    private lateinit var viewModel: RegistroUsoViewModel
    private var selectedLaboratorioId: Long? = null
    private var selectedLaboratorioSecundarioId: Long? = null
    private var isModoSalida: Boolean = false
    private var esExamen: Boolean = false
    private var laboratorioPreseleccionadoId: Long = -1L

    // View references
    private lateinit var tvTitle: TextView
    private lateinit var tvSubtitle: TextView
    private lateinit var layoutTipoUso: View
    private lateinit var toggleGroupUso: MaterialButtonToggleGroup
    private lateinit var tilLaboratorio: TextInputLayout
    private lateinit var tilLaboratorioSecundario: TextInputLayout
    private lateinit var cardNotaExamen: MaterialCardView
    private lateinit var tilProposito: TextInputLayout
    private lateinit var etLaboratorio: AutoCompleteTextView
    private lateinit var etLaboratorioSecundario: AutoCompleteTextView
    private lateinit var etProposito: TextInputEditText
    private lateinit var etObservaciones: TextInputEditText
    private lateinit var btnEntrada: MaterialButton
    private lateinit var btnSalida: MaterialButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recuperarArgumentos()
        inicializarViewModel()
        inicializarVistas(view)
        configurarModo()
        configurarDropdownLaboratorios()
        configurarListeners(view)
        observarMensajes()
    }

    private fun recuperarArgumentos() {
        // Obtener argumentos de navegación
        isModoSalida = arguments?.getBoolean("isModoSalida", false) ?: false
        laboratorioPreseleccionadoId = arguments?.getLong("laboratorioId", -1L) ?: -1L
    }

    private fun inicializarViewModel() {
        val repository = RegistroUsoRepository(requireContext())
        val factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return RegistroUsoViewModel(repository) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[RegistroUsoViewModel::class.java]
    }

    private fun inicializarVistas(view: View) {
        tvTitle = view.findViewById(R.id.tvTitle)
        tvSubtitle = view.findViewById(R.id.tvSubtitle)
        layoutTipoUso = view.findViewById(R.id.layoutTipoUso)
        toggleGroupUso = view.findViewById(R.id.toggleGroupUso)
        tilLaboratorio = view.findViewById(R.id.tilLaboratorio)
        tilLaboratorioSecundario = view.findViewById(R.id.tilLaboratorioSecundario)
        cardNotaExamen = view.findViewById(R.id.cardNotaExamen)
        tilProposito = view.findViewById(R.id.tilProposito)
        etLaboratorio = view.findViewById(R.id.etLaboratorioId)
        etLaboratorioSecundario = view.findViewById(R.id.etLaboratorioSecundarioId)
        etProposito = view.findViewById(R.id.etProposito)
        etObservaciones = view.findViewById(R.id.etObservaciones)
        btnEntrada = view.findViewById(R.id.btnEntrada)
        btnSalida = view.findViewById(R.id.btnSalida)

        // Forzar color negro en los dropdowns
        val blackColor = ContextCompat.getColor(requireContext(), android.R.color.black)
        etLaboratorio.setTextColor(blackColor)
        etLaboratorioSecundario.setTextColor(blackColor)
    }

    private fun configurarModo() {
        if (isModoSalida) {
            tvTitle.text = "Registrar Salida"
            tvSubtitle.text = "Finalice su sesión de laboratorio"
            layoutTipoUso.visibility = View.GONE
            tilLaboratorio.visibility = View.GONE
            tilLaboratorioSecundario.visibility = View.GONE
            cardNotaExamen.visibility = View.GONE
            tilProposito.visibility = View.GONE
            btnEntrada.visibility = View.GONE
            btnSalida.visibility = View.VISIBLE
        } else {
            tvTitle.text = "Registrar Entrada"
            tvSubtitle.text = "Inicie una nueva sesión"
            btnEntrada.visibility = View.VISIBLE
            btnSalida.visibility = View.GONE
            viewModel.cargarLaboratorios()
            configurarToggleExamen()
        }
    }

    private fun configurarToggleExamen() {
        toggleGroupUso.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                esExamen = checkedId == R.id.btnUsoExamen
                if (esExamen) {
                    tilLaboratorioSecundario.visibility = View.VISIBLE
                    cardNotaExamen.visibility = View.VISIBLE
                    tilLaboratorio.hint = "Laboratorio Principal"
                } else {
                    tilLaboratorioSecundario.visibility = View.GONE
                    cardNotaExamen.visibility = View.GONE
                    tilLaboratorio.hint = "Laboratorio"
                    selectedLaboratorioSecundarioId = null
                    etLaboratorioSecundario.setText("", false)
                }
            }
        }
    }

    private fun configurarDropdownLaboratorios() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.laboratorios.collectLatest { laboratorios ->
                val adapter = ArrayAdapter(requireContext(), R.layout.item_dropdown, laboratorios)
                etLaboratorio.setAdapter(adapter)
                etLaboratorioSecundario.setAdapter(adapter)

                if (laboratorioPreseleccionadoId != -1L) {
                    val labEncontrado = laboratorios.find { it.id == laboratorioPreseleccionadoId }
                    labEncontrado?.let {
                        etLaboratorio.setText(it.nombre, false)
                        selectedLaboratorioId = it.id
                    }
                }
            }
        }
    }

    private fun configurarListeners(view: View) {
        val regresarAction = { findNavController().navigateUp() }

        view.findViewById<MaterialButton>(R.id.btnCancelar)?.setOnClickListener { regresarAction() }

        etLaboratorio.setOnItemClickListener { parent, _, position, _ ->
            selectedLaboratorioId = (parent.getItemAtPosition(position) as LaboratorioResponseDTO).id
        }

        etLaboratorioSecundario.setOnItemClickListener { parent, _, position, _ ->
            selectedLaboratorioSecundarioId = (parent.getItemAtPosition(position) as LaboratorioResponseDTO).id
        }

        btnEntrada.setOnClickListener { onEntradaClick() }
        btnSalida.setOnClickListener { onSalidaClick() }
    }

    private fun onEntradaClick() {
        val proposito = etProposito.text.toString()
        val observaciones = etObservaciones.text.toString()

        if (selectedLaboratorioId == null || proposito.isBlank()) {
            Toast.makeText(requireContext(), "Complete los campos obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        if (esExamen) {
            if (selectedLaboratorioSecundarioId == null) {
                Toast.makeText(requireContext(), "Seleccione el segundo laboratorio para el examen", Toast.LENGTH_SHORT).show()
                return
            }
            if (selectedLaboratorioId == selectedLaboratorioSecundarioId) {
                Toast.makeText(requireContext(), "Los laboratorios para examen deben ser diferentes", Toast.LENGTH_SHORT).show()
                return
            }
        }

        viewModel.registrarEntrada(
            laboratorioId = selectedLaboratorioId!!,
            laboratorioSecundarioId = selectedLaboratorioSecundarioId,
            proposito = proposito,
            observaciones = observaciones,
            esExamen = esExamen
        )
    }

    private fun onSalidaClick() {
        viewModel.registrarSalida(etObservaciones.text.toString())
    }

    private fun observarMensajes() {
        val regresarAction = { findNavController().navigateUp() }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.mensaje.collectLatest { mensaje ->
                mensaje?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                    if (it.contains("correctamente")) {
                        regresarAction()
                        viewModel.clearMensaje()
                    }
                }
            }
        }
    }
}
