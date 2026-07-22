package com.uce.tesisrivasandrade.ui.main.registro_novedades

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.uce.tesisrivasandrade.R
import com.uce.tesisrivasandrade.data.remote.ApiClient
import com.uce.tesisrivasandrade.data.repository.NovedadRepository
import com.uce.tesisrivasandrade.databinding.FragmentNovedadesBinding
import com.uce.tesisrivasandrade.utils.SessionManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NovedadesFragment : Fragment(R.layout.fragment_novedades) {

    private var _binding: FragmentNovedadesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NovedadesViewModel by viewModels {
        NovedadViewModelFactory(NovedadRepository(ApiClient.getNovedadService(requireContext())))
    }

    private lateinit var adapter: NovedadesAdapter
    private var soloMias: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentNovedadesBinding.bind(view)

        val sessionManager = SessionManager(requireContext())

        // 1. Recuperar argumentos usando Navigation Component
        soloMias = arguments?.getBoolean("soloMias", false) ?: false

        // 2. Mostrar Switch solo para ADMIN y TECNICO
        val puedeVerOpcionTodos = sessionManager.esAdmin() || sessionManager.esTecnico()
        if (puedeVerOpcionTodos) {
            binding.switchVerMias.visibility = View.VISIBLE
            binding.switchVerMias.isChecked = soloMias

            binding.switchVerMias.setOnCheckedChangeListener { _, isChecked ->
                viewModel.obtenerNovedades(soloMias = isChecked)
            }
        } else {
            binding.switchVerMias.visibility = View.GONE
        }

        setupRecyclerView()
        setupFilters()
        setupObservers()

        binding.fabAddNovedad.setOnClickListener {
            findNavController().navigate(R.id.action_novedadesFragment_to_reportarNovedadFragment)
        }

        // 3. Carga inicial respetando el switch o el argumento
        viewModel.obtenerNovedades(soloMias = if (puedeVerOpcionTodos) binding.switchVerMias.isChecked else soloMias)
    }

    private fun setupRecyclerView() {
        adapter = NovedadesAdapter { novedad ->
            val bundle = Bundle().apply { putLong("novedadId", novedad.id) }
            findNavController().navigate(R.id.action_novedadesFragment_to_detalleNovedadFragment, bundle)
        }
        binding.rvNovedades.adapter = adapter
    }

    private fun setupFilters() {
        binding.chipGroupFilters.setOnCheckedStateChangeListener { _, checkedIds ->
            val checkedId = checkedIds.firstOrNull()
            val estado = when (checkedId) {
                R.id.chipTodas -> "Todas"
                R.id.chipPendientes -> "PENDIENTE"
                R.id.chipEnProceso -> "EN_PROCESO"
                R.id.chipSolucionadas -> "SOLUCIONADA"
                else -> "Todas"
            }
            viewModel.filtrarNovedades(estado)
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                    adapter.submitList(state.novedades)

                    state.error?.let {
                        Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                        viewModel.clearMessages()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
