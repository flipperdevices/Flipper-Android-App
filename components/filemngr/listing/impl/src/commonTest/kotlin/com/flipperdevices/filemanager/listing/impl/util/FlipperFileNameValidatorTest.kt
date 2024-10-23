package com.flipperdevices.filemanager.listing.impl.util

import org.junit.Assert
import org.junit.Test

class FlipperFileNameValidatorTest {

    @Test
    fun testRegex() {
        val validator = FlipperFileNameValidator()
        Assert.assertTrue(validator.isValid("file.txt"))
        Assert.assertTrue(validator.isValid("file0.txt"))
        Assert.assertTrue(validator.isValid("fIletxt"))
        Assert.assertTrue(validator.isValid("File\\"))
        Assert.assertTrue(validator.isValid("File/"))
        Assert.assertTrue(validator.isValid("File%\$"))
        Assert.assertTrue(validator.isValid("0aA!#\\\$%&'()-@^_`{}~"))

        Assert.assertFalse(validator.isValid("FЯile/"))
        Assert.assertFalse(validator.isValid("Fдile/"))
    }
}
