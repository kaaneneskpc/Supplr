package com.kaaneneskpc.supplr.data

import app.cash.turbine.test
import com.kaaneneskpc.supplr.data.fake.FakeFavoritesRepository
import com.kaaneneskpc.supplr.shared.domain.Product
import com.kaaneneskpc.supplr.shared.util.RequestState
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FavoritesRepositoryTest {
    private lateinit var fakeRepository: FakeFavoritesRepository
    @BeforeTest
    fun setup() {
        fakeRepository = FakeFavoritesRepository()
        fakeRepository.shouldReturnError = false
    }
    @Test
    fun testAddProductToFavoritesEmitsLoadingThenSuccess() = runTest {
        val inputProductId = "product-123"
        fakeRepository.addProductToFavorites(inputProductId).test {
            val actualLoading = awaitItem()
            assertTrue(actualLoading is RequestState.Loading)
            val actualSuccess = awaitItem()
            assertTrue(actualSuccess is RequestState.Success)
            awaitComplete()
        }
    }
    @Test
    fun testAddProductToFavoritesEmitsErrorWhenConfigured() = runTest {
        val inputProductId = "product-123"
        val expectedError = "Custom error"
        fakeRepository.shouldReturnError = true
        fakeRepository.errorMessage = expectedError
        fakeRepository.addProductToFavorites(inputProductId).test {
            awaitItem()
            val actualError = awaitItem()
            assertTrue(actualError is RequestState.Error)
            assertEquals(expectedError, (actualError as RequestState.Error).message)
            awaitComplete()
        }
    }
    @Test
    fun testRemoveProductFromFavoritesEmitsSuccess() = runTest {
        val inputProductId = "product-123"
        fakeRepository.addProductToFavorites(inputProductId).test {
            awaitItem()
            awaitItem()
            awaitComplete()
        }
        fakeRepository.removeProductFromFavorites(inputProductId).test {
            val actualLoading = awaitItem()
            assertTrue(actualLoading is RequestState.Loading)
            val actualSuccess = awaitItem()
            assertTrue(actualSuccess is RequestState.Success)
            awaitComplete()
        }
    }
    @Test
    fun testReadFavoriteProductIdsReturnsAddedIds() = runTest {
        val inputProductId1 = "product-1"
        val inputProductId2 = "product-2"
        fakeRepository.addProductToFavorites(inputProductId1).test {
            awaitItem()
            awaitItem()
            awaitComplete()
        }
        fakeRepository.addProductToFavorites(inputProductId2).test {
            awaitItem()
            awaitItem()
            awaitComplete()
        }
        fakeRepository.readFavoriteProductIds().test {
            awaitItem()
            val actualSuccess = awaitItem()
            assertTrue(actualSuccess is RequestState.Success)
            val actualIds = (actualSuccess as RequestState.Success).data
            assertEquals(2, actualIds.size)
            assertTrue(actualIds.contains(inputProductId1))
            assertTrue(actualIds.contains(inputProductId2))
            awaitComplete()
        }
    }
    @Test
    fun testReadFavoriteProductsReturnsMatchingProducts() = runTest {
        val inputProduct = Product(
            id = "product-123",
            title = "Test Product",
            description = "Description",
            thumbnail = "thumbnail.jpg",
            category = "Meat",
            price = 19.99
        )
        fakeRepository.addProductToStore(inputProduct)
        fakeRepository.addProductToFavorites(inputProduct.id).test {
            awaitItem()
            awaitItem()
            awaitComplete()
        }
        fakeRepository.readFavoriteProducts().test {
            awaitItem()
            val actualSuccess = awaitItem()
            assertTrue(actualSuccess is RequestState.Success)
            val actualProducts = (actualSuccess as RequestState.Success).data
            assertEquals(1, actualProducts.size)
            assertEquals(inputProduct.id, actualProducts.first().id)
            awaitComplete()
        }
    }
    @Test
    fun testGetCurrentUserIdReturnsUserId() {
        val expectedUserId = "test-user-id"
        val actualUserId = fakeRepository.getCurrentUserId()
        assertEquals(expectedUserId, actualUserId)
    }
    @Test
    fun testAddToFavoritesFailsWhenUserNotAuthenticated() = runTest {
        val inputProductId = "product-123"
        fakeRepository.setCurrentUser(null)
        fakeRepository.addProductToFavorites(inputProductId).test {
            awaitItem()
            val actualError = awaitItem()
            assertTrue(actualError is RequestState.Error)
            assertEquals("User not authenticated", (actualError as RequestState.Error).message)
            awaitComplete()
        }
    }
}
