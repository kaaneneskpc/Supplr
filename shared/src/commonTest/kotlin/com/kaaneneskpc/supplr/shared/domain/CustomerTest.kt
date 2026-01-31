package com.kaaneneskpc.supplr.shared.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CustomerTest {
    @Test
    fun testCustomerCreationWithRequiredFields() {
        val inputId = "customer-123"
        val inputFirstName = "John"
        val inputLastName = "Doe"
        val inputEmail = "john.doe@example.com"
        val actualCustomer = Customer(
            id = inputId,
            firstName = inputFirstName,
            lastName = inputLastName,
            email = inputEmail
        )
        assertEquals(inputId, actualCustomer.id)
        assertEquals(inputFirstName, actualCustomer.firstName)
        assertEquals(inputLastName, actualCustomer.lastName)
        assertEquals(inputEmail, actualCustomer.email)
    }
    @Test
    fun testCustomerDefaultValuesAreApplied() {
        val actualCustomer = Customer(
            id = "customer-456",
            firstName = "Jane",
            lastName = "Smith",
            email = "jane.smith@example.com"
        )
        assertNull(actualCustomer.city)
        assertNull(actualCustomer.postalCode)
        assertNull(actualCustomer.address)
        assertNull(actualCustomer.phoneNumber)
        assertEquals(emptyList(), actualCustomer.cart)
        assertFalse(actualCustomer.isAdmin)
        assertNull(actualCustomer.profilePhotoUrl)
        assertNull(actualCustomer.birthDate)
        assertNull(actualCustomer.communicationPreferences)
        assertFalse(actualCustomer.isTwoFactorEnabled)
        assertEquals(0, actualCustomer.rewardPoints)
    }
    @Test
    fun testCustomerWithOptionalFields() {
        val inputPhoneNumber = PhoneNumber(dialCode = 90, number = "5551234567")
        val inputCart = listOf(
            CartItem(id = "cart-1", productId = "prod-1", quantity = 2, price = 10.0)
        )
        val actualCustomer = Customer(
            id = "customer-789",
            firstName = "Admin",
            lastName = "User",
            email = "admin@example.com",
            city = "Istanbul",
            postalCode = 34000,
            address = "Test Address 123",
            phoneNumber = inputPhoneNumber,
            cart = inputCart,
            isAdmin = true,
            profilePhotoUrl = "https://example.com/photo.jpg",
            birthDate = 631152000000L,
            isTwoFactorEnabled = true,
            rewardPoints = 100
        )
        assertEquals("Istanbul", actualCustomer.city)
        assertEquals(34000, actualCustomer.postalCode)
        assertEquals("Test Address 123", actualCustomer.address)
        assertEquals(inputPhoneNumber, actualCustomer.phoneNumber)
        assertEquals(1, actualCustomer.cart.size)
        assertTrue(actualCustomer.isAdmin)
        assertEquals("https://example.com/photo.jpg", actualCustomer.profilePhotoUrl)
        assertEquals(631152000000L, actualCustomer.birthDate)
        assertTrue(actualCustomer.isTwoFactorEnabled)
        assertEquals(100, actualCustomer.rewardPoints)
    }
    @Test
    fun testPhoneNumberCreation() {
        val inputDialCode = 1
        val inputNumber = "5551234567"
        val actualPhoneNumber = PhoneNumber(dialCode = inputDialCode, number = inputNumber)
        assertEquals(inputDialCode, actualPhoneNumber.dialCode)
        assertEquals(inputNumber, actualPhoneNumber.number)
    }
    @Test
    fun testCustomerEquality() {
        val inputCustomer1 = Customer(
            id = "same-id",
            firstName = "Same",
            lastName = "Customer",
            email = "same@example.com"
        )
        val inputCustomer2 = Customer(
            id = "same-id",
            firstName = "Same",
            lastName = "Customer",
            email = "same@example.com"
        )
        assertEquals(inputCustomer1.id, inputCustomer2.id)
        assertEquals(inputCustomer1.firstName, inputCustomer2.firstName)
        assertEquals(inputCustomer1.lastName, inputCustomer2.lastName)
        assertEquals(inputCustomer1.email, inputCustomer2.email)
    }
    @Test
    fun testCustomerCopyWithUpdatedRewardPoints() {
        val originalCustomer = Customer(
            id = "customer-original",
            firstName = "Original",
            lastName = "Customer",
            email = "original@example.com",
            rewardPoints = 50
        )
        val expectedNewPoints = 150
        val actualUpdatedCustomer = originalCustomer.copy(rewardPoints = expectedNewPoints)
        assertEquals(expectedNewPoints, actualUpdatedCustomer.rewardPoints)
        assertEquals(originalCustomer.id, actualUpdatedCustomer.id)
        assertEquals(originalCustomer.email, actualUpdatedCustomer.email)
    }
}
