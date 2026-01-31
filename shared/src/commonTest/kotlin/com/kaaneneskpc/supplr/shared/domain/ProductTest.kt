package com.kaaneneskpc.supplr.shared.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ProductTest {
    @Test
    fun testProductCreationWithRequiredFields() {
        val inputId = "product-123"
        val inputTitle = "Test Product"
        val inputDescription = "Test description"
        val inputThumbnail = "https://example.com/image.jpg"
        val inputCategory = "Meat"
        val inputPrice = 29.99
        val actualProduct = Product(
            id = inputId,
            title = inputTitle,
            description = inputDescription,
            thumbnail = inputThumbnail,
            category = inputCategory,
            price = inputPrice
        )
        assertEquals(inputId, actualProduct.id)
        assertEquals(inputTitle, actualProduct.title)
        assertEquals(inputDescription, actualProduct.description)
        assertEquals(inputThumbnail, actualProduct.thumbnail)
        assertEquals(inputCategory, actualProduct.category)
        assertEquals(inputPrice, actualProduct.price)
    }
    @Test
    fun testProductDefaultValuesAreApplied() {
        val actualProduct = Product(
            id = "product-456",
            title = "Default Test",
            description = "Description",
            thumbnail = "thumbnail.jpg",
            category = "Chicken",
            price = 19.99
        )
        assertFalse(actualProduct.isPopular)
        assertFalse(actualProduct.isDiscounted)
        assertFalse(actualProduct.isNew)
        assertNull(actualProduct.flavors)
        assertNull(actualProduct.weight)
    }
    @Test
    fun testProductWithOptionalFields() {
        val inputFlavors = listOf("Spicy", "Mild", "Hot")
        val inputWeight = 500
        val actualProduct = Product(
            id = "product-789",
            title = "Full Product",
            description = "Full description",
            thumbnail = "full.jpg",
            category = "FastFood",
            price = 15.99,
            flavors = inputFlavors,
            weight = inputWeight,
            isPopular = true,
            isDiscounted = true,
            isNew = true
        )
        assertEquals(inputFlavors, actualProduct.flavors)
        assertEquals(inputWeight, actualProduct.weight)
        assertTrue(actualProduct.isPopular)
        assertTrue(actualProduct.isDiscounted)
        assertTrue(actualProduct.isNew)
    }
    @Test
    fun testProductCategoryEnumValues() {
        val expectedCategories = listOf("Meat", "Chicken", "FastFood", "Vegetarian", "SaladMixed", "Others")
        val actualCategories = ProductCategory.entries.map { it.title }
        assertEquals(expectedCategories, actualCategories)
        assertEquals(6, ProductCategory.entries.size)
    }
    @Test
    fun testProductEquality() {
        val inputProduct1 = Product(
            id = "same-id",
            title = "Same Title",
            description = "Same Description",
            thumbnail = "same.jpg",
            category = "Meat",
            price = 10.0
        )
        val inputProduct2 = Product(
            id = "same-id",
            title = "Same Title",
            description = "Same Description",
            thumbnail = "same.jpg",
            category = "Meat",
            price = 10.0
        )
        assertEquals(inputProduct1, inputProduct2)
    }
    @Test
    fun testProductCopy() {
        val originalProduct = Product(
            id = "original-id",
            title = "Original Title",
            description = "Original Description",
            thumbnail = "original.jpg",
            category = "Meat",
            price = 25.0
        )
        val expectedNewPrice = 30.0
        val actualCopiedProduct = originalProduct.copy(price = expectedNewPrice)
        assertEquals(expectedNewPrice, actualCopiedProduct.price)
        assertEquals(originalProduct.id, actualCopiedProduct.id)
        assertEquals(originalProduct.title, actualCopiedProduct.title)
    }
}
