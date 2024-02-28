package com.flipperdevices.core.data

import org.junit.Assert
import org.junit.Test

class SemVerParser {

    @Test
    fun `fromString with valid version string`() {
        val versionString = "1.2"
        val semVer = SemVer.fromString(versionString)
        Assert.assertNotNull(semVer)
        Assert.assertEquals(1, semVer?.majorVersion)
        Assert.assertEquals(2, semVer?.minorVersion)
        Assert.assertNull(semVer?.patchVersion)
        Assert.assertNull(semVer?.additionalVersion)
    }

    @Test
    fun `fromString with valid version string + patch version`() {
        val versionString = "1.2.3"
        val semVer = SemVer.fromString(versionString)
        Assert.assertNotNull(semVer)
        Assert.assertEquals(1, semVer?.majorVersion)
        Assert.assertEquals(2, semVer?.minorVersion)
        Assert.assertEquals(3, semVer?.patchVersion)
        Assert.assertNull(semVer?.additionalVersion)
    }

    @Test
    fun `fromString with valid version string with patch version and additional version`() {
        val versionString = "1.2.3.4"
        val semVer = SemVer.fromString(versionString)
        Assert.assertNotNull(semVer)
        Assert.assertEquals(1, semVer?.majorVersion)
        Assert.assertEquals(2, semVer?.minorVersion)
        Assert.assertEquals(3, semVer?.patchVersion)
        Assert.assertEquals(4, semVer?.additionalVersion)
    }

    @Test
    fun `fromString with invalid version string by count`() {
        val versionString = "1.2.3.4.5"
        val semVer = SemVer.fromString(versionString)
        Assert.assertNull(semVer)

        val versionString2 = "1."
        val semVer2 = SemVer.fromString(versionString2)
        Assert.assertNull(semVer2)
    }

    @Test
    fun `fromString with version string containing non-numeric characters`() {
        val versionString = "1.2.3.a"
        val semVer = SemVer.fromString(versionString)
        Assert.assertNull(semVer)

        val versionString2 = "1.2.a"
        val semVer2 = SemVer.fromString(versionString2)
        Assert.assertNull(semVer2)
    }
}
