package com.flipperdevices.core.data

import org.junit.Assert
import org.junit.Test

class SemVerCompare {

    @Test
    fun `compareTo with different major versions`() {
        val semVer1 = SemVer(1, 0, 0)
        val semVer2 = SemVer(2, 0, 0)
        Assert.assertTrue(semVer1 < semVer2)
        Assert.assertFalse(semVer1 > semVer2)
    }

    @Test
    fun `compareTo with different minor versions`() {
        val semVer1 = SemVer(1, 0, 0)
        val semVer2 = SemVer(1, 1, 0)
        Assert.assertTrue(semVer1 < semVer2)
        Assert.assertFalse(semVer1 > semVer2)
    }

    @Test
    fun `compareTo with different patch versions`() {
        val semVer1 = SemVer(1, 0, 0)
        val semVer2 = SemVer(1, 0, 1)
        Assert.assertTrue(semVer1 < semVer2)
        Assert.assertFalse(semVer1 > semVer2)
    }

    @Test
    fun `compareTo with different additional versions`() {
        val semVer1 = SemVer(1, 0, 0, 0)
        val semVer2 = SemVer(1, 0, 0, 1)
        Assert.assertTrue(semVer1 < semVer2)
        Assert.assertFalse(semVer1 > semVer2)
    }

    @Test
    fun `compareTo with null patch versions`() {
        val semVer1 = SemVer(1, 0, null)
        val semVer2 = SemVer(1, 0, 1)
        Assert.assertFalse(semVer1 < semVer2)
        Assert.assertFalse(semVer1 > semVer2)
    }

    @Test
    fun `compareTo with null additional versions`() {
        val semVer1 = SemVer(1, 0, 0, null)
        val semVer2 = SemVer(1, 0, 0, 1)
        Assert.assertFalse(semVer1 < semVer2)
        Assert.assertFalse(semVer1 > semVer2)
    }

    @Test
    fun `compareTo with same versions`() {
        val semVer1 = SemVer(1, 0, 0)
        val semVer2 = SemVer(1, 0, 0)
        Assert.assertTrue(semVer1 == semVer2)
        Assert.assertFalse(semVer1 > semVer2)
        Assert.assertFalse(semVer1 < semVer2)
    }

    @Test
    fun `compare to all`() {
        val semVer1 = SemVer(1, 4, 1, 860)
        val semVer2 = SemVer(1, 4, 1, 861)
        Assert.assertTrue(semVer1 < semVer2)
    }

    @Test
    fun `compare to all without additional`() {
        val semVer1 = SemVer(1, 4, 1)
        val semVer2 = SemVer(1, 4, 2)
        Assert.assertTrue(semVer1 < semVer2)
    }

    @Test
    fun `compare with diff data`() {
        val semVer1 = SemVer(1, 4, 1)
        val semVer2 = SemVer(1, 4, 1, 861)
        Assert.assertFalse(semVer1 < semVer2)
    }
}
