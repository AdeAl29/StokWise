package com.ade.fuzzyrisk.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PhoneCatalogTest {
    @Test
    fun requestedPhonesAreAvailableInInputCatalog() {
        val expectedModels = mapOf(
            "OPPO" to "A6X",
            "realme" to "60X",
            "Redmi" to "Redmi Note 15C",
            "Samsung" to "A26",
            "Infinix" to "Infinix Smart 20"
        )

        expectedModels.forEach { (brand, model) ->
            assertTrue(
                "$brand $model should be available in the input catalog",
                phoneModelsForBrand(brand).any { it.name == model }
            )
        }
    }

    @Test
    fun requestedPhonesBuildExpectedSavedNames() {
        val expectedPhoneTypes = mapOf(
            "OPPO" to ("A6X" to "OPPO A6X"),
            "realme" to ("60X" to "realme 60X"),
            "Redmi" to ("Redmi Note 15C" to "Redmi Note 15C"),
            "Samsung" to ("A26" to "Samsung A26"),
            "Infinix" to ("Infinix Smart 20" to "Infinix Smart 20")
        )

        expectedPhoneTypes.forEach { (brand, modelAndPhoneType) ->
            val (model, phoneType) = modelAndPhoneType
            assertEquals(phoneType, buildPhoneType(brand, model))
        }
    }

    @Test
    fun requestedPhoneImagesUsePreferredSearchNames() {
        val samsungUrl = phoneImageUrl("Samsung A26").orEmpty()
        val realmeUrl = phoneImageUrl("realme 60X").orEmpty()

        assertTrue(samsungUrl.contains("Samsung+Galaxy+A26+5G"))
        assertTrue(realmeUrl.contains("realme+Note+60X"))
    }
}
