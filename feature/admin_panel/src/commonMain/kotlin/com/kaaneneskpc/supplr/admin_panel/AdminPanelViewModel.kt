package com.kaaneneskpc.supplr.admin_panel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaaneneskpc.supplr.data.domain.AdminRepository
import com.kaaneneskpc.supplr.shared.util.RequestState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

class AdminPanelViewModel(
    private val adminRepository: AdminRepository
): ViewModel() {
    val products = adminRepository.readLastTenProducts().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = RequestState.Idle
    )

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    fun updateSearchQuery(value: String) {
        _searchQuery.value = value
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val filteredProducts =
        searchQuery
            .debounce(500)
            .flatMapLatest { query ->
                if (query.isBlank()) products
                else adminRepository.searchProductsByTitle(query)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = RequestState.Loading
            )
}