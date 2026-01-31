package com.kaaneneskpc.supplr.shared.domain

data class PaginatedResult<T>(
    val items: List<T>,
    val lastDocumentId: String?,
    val hasNextPage: Boolean,
    val totalCount: Int = 0
)

sealed class PaginationState {
    data object Idle : PaginationState()
    data object Loading : PaginationState()
    data object LoadingMore : PaginationState()
    data object EndReached : PaginationState()
    data class Error(val message: String) : PaginationState()
}
