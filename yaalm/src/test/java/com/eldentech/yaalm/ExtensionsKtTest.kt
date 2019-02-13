package com.eldentech.yaalm

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class ExtensionsKtTest {

    @Test
    fun `test String isInValidList`() {
        var result = "test".isInValidList("test", "test1", "test2")
        assertTrue(result)
        result = "not in".isInValidList("test", "test1", "test2")
        assertFalse(result)
    }

    @Test
    fun `test Int isInValidList`() {
        var result = 3.isInValidList(1,2,3)
        assertTrue(result)
        result = 1.isInValidList(4,5,6)
        assertFalse(result)
    }

    @Test
    fun `Int isInValidRange`() {
        var result = 100.isInValidRange(0,101)
        assertTrue(result)
        result = 0.isInValidRange(1,101)
        assertFalse(result)
        result = (-1).isInValidRange(0,101)
        assertFalse(result)
        result = 101.isInValidRange(0,101)
        assertFalse(result)
    }

    @Test
    fun `Long isInValidRange`() {
        var result = 100L.isInValidRange(0,101)
        assertTrue(result)
        result = 0L.isInValidRange(1,101)
        assertFalse(result)
        result = (-1L).isInValidRange(0,101)
        assertFalse(result)
        result = 101L.isInValidRange(0,101)
        assertFalse(result)
    }
}