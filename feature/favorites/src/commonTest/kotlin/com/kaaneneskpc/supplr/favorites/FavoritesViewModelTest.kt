package com.kaaneneskpc.supplr.favorites

import app.cash.turbine.test
import com.kaaneneskpc.supplr.data.domain.FavoritesRepository
import com.kaaneneskpc.supplr.shared.domain.Product
import com.kaaneneskpc.supplr.shared.util.RequestState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FakeFavoritesRepositoryForViewModel : FavoritesRepository {
    private val favoriteProductIds = mutableListOf<String>()
    private val products = mutableMapOf<String, Product>()
    private val favoriteProductsFlow = MutableStateFlow<RequestState<List<Product>>>(RequestState.Loading)
    var shouldReturnError = false
    var errorMessage = "Test error"
    fun setFavoriteProducts(productList: List<Product>) {
        productList.forEach { products[it.id] = it }
        favoriteProductIds.clear()
        favoriteProductIds.addAll(productList.map { it.id })
        favoriteProductsFlow.value = RequestState.Success(productList)
    }
    fun emitLoading() {
        favoriteProductsFlow.value = RequestState.Loading
    }
    fun emitError(message: String) {
        favoriteProductsFlow.value = RequestState.Error(message)
    }
    override fun getCurrentUserId(): String? = "test-user-id"
    override fun addProductToFavorites(productId: String): Flow<RequestState<Unit>> = flow {
        emit(RequestState.Loading)
        if (shouldReturnError) {
            emit(RequestState.Error(errorMessage))
            return@flow
        }
        if (!favoriteProductIds.contains(productId)) {
            favoriteProductIds.add(productId)
        }
        emit(RequestState.Success(Unit))
    }
    override fun removeProductFromFavorites(productId: String): Flow<RequestState<Unit>> = flow {
        emit(RequestState.Loading)
        if (shouldReturnError) {
            emit(RequestState.Error(errorMessage))
            return@flow
        }
        favoriteProductIds.remove(productId)
        emit(RequestState.Success(Unit))
    }
    override fun readFavoriteProductIds(): Flow<RequestState<List<String>>> = flow {
        emit(RequestState.Success(favoriteProductIds.toList()))
    }
    override fun readFavoriteProducts(): Flow<RequestState<List<Product>>> = favoriteProductsFlow
}

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesViewModelTest {
    private lateinit var fakeRepository: FakeFavoritesRepositoryForViewModel
    private lateinit var viewModel: FavoritesViewModel
    private val testDispatcher = StandardTestDispatcher()
    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeFavoritesRepositoryForViewModel()
    }
    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }
    @Test
    fun testInitialStateIsLoading() = runTest(testDispatcher) {
        fakeRepository.emitLoading()
        viewModel = FavoritesViewModel(fakeRepository)
        val actualState = viewModel.favoriteProducts.value
        assertTrue(actualState is RequestState.Loading)
    }
    @Test
    fun testFavoriteProductsEmitsSuccessWithProducts() = runTest(testDispatcher) {
        val expectedProducts = listOf(
            createTestProduct("prod-1", "Product 1"),
            createTestProduct("prod-2", "Product 2")
        )
        fakeRepository.setFavoriteProducts(expectedProducts)
        viewModel = FavoritesViewModel(fakeRepository)
        viewModel.favoriteProducts.test {
            var actualState = awaitItem()
            if (actualState is RequestState.Loading) {
                actualState = awaitItem()
            }
            assertTrue(actualState is RequestState.Success, "Expected Success but got $actualState")
            val actualProducts = (actualState as RequestState.Success).data
            assertEquals(2, actualProducts.size)
            assertEquals("prod-1", actualProducts[0].id)
            assertEquals("prod-2", actualProducts[1].id)
            cancelAndIgnoreRemainingEvents()
        }
    }
    @Test
    fun testFavoriteProductsEmitsErrorState() = runTest(testDispatcher) {
        val expectedErrorMessage = "Failed to load favorites"
        fakeRepository.emitError(expectedErrorMessage)
        viewModel = FavoritesViewModel(fakeRepository)
        viewModel.favoriteProducts.test {
            var actualState = awaitItem()
            if (actualState is RequestState.Loading) {
                actualState = awaitItem()
            }
            assertTrue(actualState is RequestState.Error, "Expected Error but got $actualState")
            assertEquals(expectedErrorMessage, (actualState as RequestState.Error).message)
            cancelAndIgnoreRemainingEvents()
        }
    }
    @Test
    fun testAddToFavoritesCallsOnSuccessCallback() = runTest(testDispatcher) {
        fakeRepository.shouldReturnError = false
        viewModel = FavoritesViewModel(fakeRepository)
        var wasSuccessCalled = false
        var wasErrorCalled = false
        viewModel.addToFavorites(
            productId = "new-product",
            onSuccess = { wasSuccessCalled = true },
            onError = { wasErrorCalled = true }
        )
        advanceUntilIdle()
        assertTrue(wasSuccessCalled)
        assertEquals(false, wasErrorCalled)
    }
    @Test
    fun testAddToFavoritesCallsOnErrorCallback() = runTest(testDispatcher) {
        fakeRepository.shouldReturnError = true
        fakeRepository.errorMessage = "Custom error"
        viewModel = FavoritesViewModel(fakeRepository)
        var wasSuccessCalled = false
        var capturedErrorMessage = ""
        viewModel.addToFavorites(
            productId = "new-product",
            onSuccess = { wasSuccessCalled = true },
            onError = { capturedErrorMessage = it }
        )
        advanceUntilIdle()
        assertEquals(false, wasSuccessCalled)
        assertEquals("Custom error", capturedErrorMessage)
    }
    @Test
    fun testRemoveFromFavoritesCallsOnSuccessCallback() = runTest(testDispatcher) {
        fakeRepository.shouldReturnError = false
        viewModel = FavoritesViewModel(fakeRepository)
        var wasSuccessCalled = false
        var wasErrorCalled = false
        viewModel.removeFromFavorites(
            productId = "existing-product",
            onSuccess = { wasSuccessCalled = true },
            onError = { wasErrorCalled = true }
        )
        advanceUntilIdle()
        assertTrue(wasSuccessCalled)
        assertEquals(false, wasErrorCalled)
    }
    @Test
    fun testRemoveFromFavoritesCallsOnErrorCallback() = runTest(testDispatcher) {
        fakeRepository.shouldReturnError = true
        fakeRepository.errorMessage = "Remove error"
        viewModel = FavoritesViewModel(fakeRepository)
        var wasSuccessCalled = false
        var capturedErrorMessage = ""
        viewModel.removeFromFavorites(
            productId = "existing-product",
            onSuccess = { wasSuccessCalled = true },
            onError = { capturedErrorMessage = it }
        )
        advanceUntilIdle()
        assertEquals(false, wasSuccessCalled)
        assertEquals("Remove error", capturedErrorMessage)
    }
    @Test
    fun testEmptyFavoriteProductsList() = runTest(testDispatcher) {
        fakeRepository.setFavoriteProducts(emptyList())
        viewModel = FavoritesViewModel(fakeRepository)
        viewModel.favoriteProducts.test {
            var actualState = awaitItem()
            if (actualState is RequestState.Loading) {
                actualState = awaitItem()
            }
            assertTrue(actualState is RequestState.Success, "Expected Success but got $actualState")
            val actualProducts = (actualState as RequestState.Success).data
            assertTrue(actualProducts.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }
    private fun createTestProduct(id: String, title: String): Product = Product(
        id = id,
        title = title,
        description = "Description for $title",
        thumbnail = "$id.jpg",
        category = "Meat",
        price = 10.0
    )
}
