package com.kaaneneskpc.supplr.shared.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CartItemTest {
    @Test
    fun testCartItemCreationWithRequiredFields() {
        val inputProductId = "product-123"
        val inputQuantity = 2
        val actualCartItem = CartItem(
            productId = inputProductId,
            quantity = inputQuantity
        )
        assertEquals(inputProductId, actualCartItem.productId)
        assertEquals(inputQuantity, actualCartItem.quantity)
    }
    @Test
    fun testCartItemDefaultValuesAreApplied() {
        val actualCartItem = CartItem(
            productId = "product-456",
            quantity = 1
        )
        assertNull(actualCartItem.flavor)
        assertEquals("", actualCartItem.title)
        assertEquals("", actualCartItem.thumbnail)
        assertEquals(0.0, actualCartItem.price)
        assertTrue(actualCartItem.id.isNotEmpty())
    }
    @Test
    fun testCartItemWithAllFields() {
        val inputId = "custom-id"
        val inputProductId = "product-789"
        val inputFlavor = "Spicy"
        val inputQuantity = 3
        val inputTitle = "Test Product"
        val inputThumbnail = "thumbnail.jpg"
        val inputPrice = 29.99
        val actualCartItem = CartItem(
            id = inputId,
            productId = inputProductId,
            flavor = inputFlavor,
            quantity = inputQuantity,
            title = inputTitle,
            thumbnail = inputThumbnail,
            price = inputPrice
        )
        assertEquals(inputId, actualCartItem.id)
        assertEquals(inputProductId, actualCartItem.productId)
        assertEquals(inputFlavor, actualCartItem.flavor)
        assertEquals(inputQuantity, actualCartItem.quantity)
        assertEquals(inputTitle, actualCartItem.title)
        assertEquals(inputThumbnail, actualCartItem.thumbnail)
        assertEquals(inputPrice, actualCartItem.price)
    }
    @Test
    fun testCartItemUniqueIdGeneration() {
        val inputCartItem1 = CartItem(productId = "product-1", quantity = 1)
        val inputCartItem2 = CartItem(productId = "product-1", quantity = 1)
        assertNotEquals(inputCartItem1.id, inputCartItem2.id)
    }
    @Test
    fun testCartItemCopyWithUpdatedQuantity() {
        val originalCartItem = CartItem(
            id = "cart-item-1",
            productId = "product-123",
            quantity = 2,
            price = 10.0
        )
        val expectedNewQuantity = 5
        val actualUpdatedItem = originalCartItem.copy(quantity = expectedNewQuantity)
        assertEquals(expectedNewQuantity, actualUpdatedItem.quantity)
        assertEquals(originalCartItem.id, actualUpdatedItem.id)
        assertEquals(originalCartItem.productId, actualUpdatedItem.productId)
        assertEquals(originalCartItem.price, actualUpdatedItem.price)
    }
    @Test
    fun testCartItemTotalCalculation() {
        val inputPrice = 15.0
        val inputQuantity = 4
        val actualCartItem = CartItem(
            productId = "product-123",
            quantity = inputQuantity,
            price = inputPrice
        )
        val expectedTotal = inputPrice * inputQuantity
        val actualTotal = actualCartItem.price * actualCartItem.quantity
        assertEquals(expectedTotal, actualTotal)
    }
}
