package com.kaaneneskpc.supplr.shared.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PaginatedResultTest {
    @Test
    fun testPaginatedResultWithEmptyItems() {
        val inputItems = emptyList<String>()
        val actualResult = PaginatedResult(
            items = inputItems,
            lastDocumentId = null,
            hasNextPage = false
        )
        assertTrue(actualResult.items.isEmpty())
        assertNull(actualResult.lastDocumentId)
        assertFalse(actualResult.hasNextPage)
        assertEquals(0, actualResult.totalCount)
    }
    @Test
    fun testPaginatedResultWithItems() {
        val inputItems = listOf("item1", "item2", "item3")
        val inputLastDocumentId = "doc-123"
        val actualResult = PaginatedResult(
            items = inputItems,
            lastDocumentId = inputLastDocumentId,
            hasNextPage = true,
            totalCount = 10
        )
        assertEquals(3, actualResult.items.size)
        assertEquals(inputLastDocumentId, actualResult.lastDocumentId)
        assertTrue(actualResult.hasNextPage)
        assertEquals(10, actualResult.totalCount)
    }
    @Test
    fun testPaginatedResultLastPageHasNoNextPage() {
        val inputItems = listOf("last1", "last2")
        val actualResult = PaginatedResult(
            items = inputItems,
            lastDocumentId = "last-doc",
            hasNextPage = false,
            totalCount = 12
        )
        assertEquals(2, actualResult.items.size)
        assertFalse(actualResult.hasNextPage)
        assertEquals(12, actualResult.totalCount)
    }
    @Test
    fun testPaginatedResultGenericTypeWithProduct() {
        val inputProduct = Product(
            id = "prod-1",
            title = "Test Product",
            description = "Test Description",
            thumbnail = "test.jpg",
            category = "Meat",
            price = 19.99
        )
        val inputItems = listOf(inputProduct)
        val actualResult = PaginatedResult(
            items = inputItems,
            lastDocumentId = "prod-1",
            hasNextPage = false
        )
        assertEquals(1, actualResult.items.size)
        assertEquals("prod-1", actualResult.items.first().id)
        assertEquals("Test Product", actualResult.items.first().title)
    }
    @Test
    fun testPaginationStateIdle() {
        val actualState: PaginationState = PaginationState.Idle
        assertTrue(actualState is PaginationState.Idle)
    }
    @Test
    fun testPaginationStateLoading() {
        val actualState: PaginationState = PaginationState.Loading
        assertTrue(actualState is PaginationState.Loading)
    }
    @Test
    fun testPaginationStateLoadingMore() {
        val actualState: PaginationState = PaginationState.LoadingMore
        assertTrue(actualState is PaginationState.LoadingMore)
    }
    @Test
    fun testPaginationStateEndReached() {
        val actualState: PaginationState = PaginationState.EndReached
        assertTrue(actualState is PaginationState.EndReached)
    }
    @Test
    fun testPaginationStateError() {
        val expectedMessage = "Network error"
        val actualState: PaginationState = PaginationState.Error(expectedMessage)
        assertTrue(actualState is PaginationState.Error)
        assertEquals(expectedMessage, (actualState as PaginationState.Error).message)
    }
}
