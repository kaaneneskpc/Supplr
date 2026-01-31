package com.kaaneneskpc.supplr.shared.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RequestStateTest {
    @Test
    fun testIdleStateIsCorrectlyIdentified() {
        val inputState: RequestState<String> = RequestState.Idle
        val actualIsIdle = inputState.isIdle()
        val actualIsLoading = inputState.isLoading()
        val actualIsSuccess = inputState.isSuccess()
        val actualIsError = inputState.isError()
        assertTrue(actualIsIdle)
        assertFalse(actualIsLoading)
        assertFalse(actualIsSuccess)
        assertFalse(actualIsError)
    }
    @Test
    fun testLoadingStateIsCorrectlyIdentified() {
        val inputState: RequestState<String> = RequestState.Loading
        val actualIsIdle = inputState.isIdle()
        val actualIsLoading = inputState.isLoading()
        val actualIsSuccess = inputState.isSuccess()
        val actualIsError = inputState.isError()
        assertFalse(actualIsIdle)
        assertTrue(actualIsLoading)
        assertFalse(actualIsSuccess)
        assertFalse(actualIsError)
    }
    @Test
    fun testSuccessStateIsCorrectlyIdentified() {
        val inputData = "test data"
        val inputState: RequestState<String> = RequestState.Success(inputData)
        val actualIsIdle = inputState.isIdle()
        val actualIsLoading = inputState.isLoading()
        val actualIsSuccess = inputState.isSuccess()
        val actualIsError = inputState.isError()
        assertFalse(actualIsIdle)
        assertFalse(actualIsLoading)
        assertTrue(actualIsSuccess)
        assertFalse(actualIsError)
    }
    @Test
    fun testErrorStateIsCorrectlyIdentified() {
        val inputMessage = "Error occurred"
        val inputState: RequestState<String> = RequestState.Error(inputMessage)
        val actualIsIdle = inputState.isIdle()
        val actualIsLoading = inputState.isLoading()
        val actualIsSuccess = inputState.isSuccess()
        val actualIsError = inputState.isError()
        assertFalse(actualIsIdle)
        assertFalse(actualIsLoading)
        assertFalse(actualIsSuccess)
        assertTrue(actualIsError)
    }
    @Test
    fun testGetSuccessDataReturnsCorrectData() {
        val expectedData = "expected data"
        val inputState: RequestState<String> = RequestState.Success(expectedData)
        val actualData = inputState.getSuccessData()
        assertEquals(expectedData, actualData)
    }
    @Test
    fun testGetSuccessDataOrNullReturnsDataWhenSuccess() {
        val expectedData = "expected data"
        val inputState: RequestState<String> = RequestState.Success(expectedData)
        val actualData = inputState.getSuccessDataOrNull()
        assertEquals(expectedData, actualData)
    }
    @Test
    fun testGetSuccessDataOrNullReturnsNullWhenNotSuccess() {
        val inputStateLoading: RequestState<String> = RequestState.Loading
        val inputStateIdle: RequestState<String> = RequestState.Idle
        val inputStateError: RequestState<String> = RequestState.Error("error")
        assertNull(inputStateLoading.getSuccessDataOrNull())
        assertNull(inputStateIdle.getSuccessDataOrNull())
        assertNull(inputStateError.getSuccessDataOrNull())
    }
    @Test
    fun testGetErrorMessageReturnsCorrectMessage() {
        val expectedMessage = "Expected error message"
        val inputState: RequestState<String> = RequestState.Error(expectedMessage)
        val actualMessage = inputState.getErrorMessage()
        assertEquals(expectedMessage, actualMessage)
    }
    @Test
    fun testSuccessWithComplexType() {
        val expectedList = listOf(1, 2, 3, 4, 5)
        val inputState: RequestState<List<Int>> = RequestState.Success(expectedList)
        val actualData = inputState.getSuccessData()
        assertEquals(expectedList, actualData)
        assertEquals(5, actualData.size)
    }
}
