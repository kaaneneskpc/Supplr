package com.kaaneneskpc.supplr.data

import app.cash.turbine.test
import com.kaaneneskpc.supplr.data.fake.FakeProductRepository
import com.kaaneneskpc.supplr.shared.domain.Product
import com.kaaneneskpc.supplr.shared.domain.ProductCategory
import com.kaaneneskpc.supplr.shared.util.RequestState
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ProductRepositoryTest {
    private lateinit var fakeRepository: FakeProductRepository
    @BeforeTest
    fun setup() {
        fakeRepository = FakeProductRepository()
        fakeRepository.shouldReturnError = false
    }
    @Test
    fun testReadDiscountedProductsReturnsOnlyDiscountedProducts() = runTest {
        val inputProducts = listOf(
            createProduct("prod-1", "Product 1", isDiscounted = true),
            createProduct("prod-2", "Product 2", isDiscounted = false),
            createProduct("prod-3", "Product 3", isDiscounted = true)
        )
        fakeRepository.setProducts(inputProducts)
        fakeRepository.readDiscountedProducts().test {
            awaitItem()
            val actualSuccess = awaitItem()
            assertTrue(actualSuccess is RequestState.Success)
            val actualProducts = (actualSuccess as RequestState.Success).data
            assertEquals(2, actualProducts.size)
            assertTrue(actualProducts.all { it.isDiscounted })
            awaitComplete()
        }
    }
    @Test
    fun testReadNewProductsReturnsOnlyNewProducts() = runTest {
        val inputProducts = listOf(
            createProduct("prod-1", "Product 1", isNew = true),
            createProduct("prod-2", "Product 2", isNew = false),
            createProduct("prod-3", "Product 3", isNew = true)
        )
        fakeRepository.setProducts(inputProducts)
        fakeRepository.readNewProducts().test {
            awaitItem()
            val actualSuccess = awaitItem()
            assertTrue(actualSuccess is RequestState.Success)
            val actualProducts = (actualSuccess as RequestState.Success).data
            assertEquals(2, actualProducts.size)
            assertTrue(actualProducts.all { it.isNew })
            awaitComplete()
        }
    }
    @Test
    fun testReadProductByIdReturnsCorrectProduct() = runTest {
        val expectedProduct = createProduct("target-id", "Target Product")
        val inputProducts = listOf(
            createProduct("prod-1", "Product 1"),
            expectedProduct,
            createProduct("prod-3", "Product 3")
        )
        fakeRepository.setProducts(inputProducts)
        fakeRepository.readProductByIdFlow("target-id").test {
            awaitItem()
            val actualSuccess = awaitItem()
            assertTrue(actualSuccess is RequestState.Success)
            val actualProduct = (actualSuccess as RequestState.Success).data
            assertEquals("target-id", actualProduct.id)
            assertEquals("Target Product", actualProduct.title)
            awaitComplete()
        }
    }
    @Test
    fun testReadProductByIdReturnsErrorForNonExistentProduct() = runTest {
        fakeRepository.setProducts(emptyList())
        fakeRepository.readProductByIdFlow("non-existent").test {
            awaitItem()
            val actualError = awaitItem()
            assertTrue(actualError is RequestState.Error)
            assertEquals("Product not found", (actualError as RequestState.Error).message)
            awaitComplete()
        }
    }
    @Test
    fun testReadProductsByIdsReturnsMatchingProducts() = runTest {
        val inputProducts = listOf(
            createProduct("prod-1", "Product 1"),
            createProduct("prod-2", "Product 2"),
            createProduct("prod-3", "Product 3")
        )
        fakeRepository.setProducts(inputProducts)
        fakeRepository.readProductsByIdsFlow(listOf("prod-1", "prod-3")).test {
            awaitItem()
            val actualSuccess = awaitItem()
            assertTrue(actualSuccess is RequestState.Success)
            val actualProducts = (actualSuccess as RequestState.Success).data
            assertEquals(2, actualProducts.size)
            assertTrue(actualProducts.any { it.id == "prod-1" })
            assertTrue(actualProducts.any { it.id == "prod-3" })
            awaitComplete()
        }
    }
    @Test
    fun testReadProductsByCategoryFlowReturnsMatchingCategory() = runTest {
        val inputProducts = listOf(
            createProduct("prod-1", "Product 1", category = "Meat"),
            createProduct("prod-2", "Product 2", category = "Chicken"),
            createProduct("prod-3", "Product 3", category = "Meat")
        )
        fakeRepository.setProducts(inputProducts)
        fakeRepository.readProductsByCategoryFlow(ProductCategory.Meat).test {
            awaitItem()
            val actualSuccess = awaitItem()
            assertTrue(actualSuccess is RequestState.Success)
            val actualProducts = (actualSuccess as RequestState.Success).data
            assertEquals(2, actualProducts.size)
            assertTrue(actualProducts.all { it.category == "Meat" })
            awaitComplete()
        }
    }
    @Test
    fun testReadProductsByCategoryPaginatedReturnsCorrectPage() = runTest {
        val inputProducts = (1..15).map { i ->
            createProduct("prod-$i", "Product $i", category = "Meat")
        }
        fakeRepository.setProducts(inputProducts)
        val actualResult = fakeRepository.readProductsByCategoryPaginated(
            category = ProductCategory.Meat,
            pageSize = 5
        )
        assertTrue(actualResult is RequestState.Success)
        val actualPaginatedResult = (actualResult as RequestState.Success).data
        assertEquals(5, actualPaginatedResult.items.size)
        assertTrue(actualPaginatedResult.hasNextPage)
        assertEquals("prod-5", actualPaginatedResult.lastDocumentId)
    }
    @Test
    fun testReadProductsByCategoryPaginatedReturnsSecondPage() = runTest {
        val inputProducts = (1..15).map { i ->
            createProduct("prod-$i", "Product $i", category = "Meat")
        }
        fakeRepository.setProducts(inputProducts)
        val actualResult = fakeRepository.readProductsByCategoryPaginated(
            category = ProductCategory.Meat,
            pageSize = 5,
            lastDocumentId = "prod-5"
        )
        assertTrue(actualResult is RequestState.Success)
        val actualPaginatedResult = (actualResult as RequestState.Success).data
        assertEquals(5, actualPaginatedResult.items.size)
        assertTrue(actualPaginatedResult.hasNextPage)
        assertEquals("prod-10", actualPaginatedResult.lastDocumentId)
        assertEquals("prod-6", actualPaginatedResult.items.first().id)
    }
    @Test
    fun testReadProductsByCategoryPaginatedReturnsFalseHasNextPageForLastPage() = runTest {
        val inputProducts = (1..7).map { i ->
            createProduct("prod-$i", "Product $i", category = "Meat")
        }
        fakeRepository.setProducts(inputProducts)
        val actualResult = fakeRepository.readProductsByCategoryPaginated(
            category = ProductCategory.Meat,
            pageSize = 5,
            lastDocumentId = "prod-5"
        )
        assertTrue(actualResult is RequestState.Success)
        val actualPaginatedResult = (actualResult as RequestState.Success).data
        assertEquals(2, actualPaginatedResult.items.size)
        assertFalse(actualPaginatedResult.hasNextPage)
    }
    @Test
    fun testRepositoryErrorHandling() = runTest {
        val expectedError = "Network error"
        fakeRepository.shouldReturnError = true
        fakeRepository.errorMessage = expectedError
        fakeRepository.readDiscountedProducts().test {
            awaitItem()
            val actualError = awaitItem()
            assertTrue(actualError is RequestState.Error)
            assertEquals(expectedError, (actualError as RequestState.Error).message)
            awaitComplete()
        }
    }
    private fun createProduct(
        id: String,
        title: String,
        category: String = "Meat",
        isDiscounted: Boolean = false,
        isNew: Boolean = false
    ): Product = Product(
        id = id,
        title = title,
        description = "Description for $title",
        thumbnail = "$id.jpg",
        category = category,
        price = 10.0,
        isDiscounted = isDiscounted,
        isNew = isNew
    )
}
