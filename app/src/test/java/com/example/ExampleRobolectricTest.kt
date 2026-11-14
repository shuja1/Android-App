package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.PlayStoreDocsContent
import com.example.domain.GstCalculationInput
import com.example.domain.GstCalculatorEngine
import com.example.domain.SupplyType
import com.example.domain.TaxMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.math.BigDecimal

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read app_name string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Elemensys GST Calculator", appName)
    assertTrue("Play Store title must be <= 30 chars", appName.length <= 30)
  }

  @Test
  fun `play store docs and all gst slabs are available`() {
    assertTrue(PlayStoreDocsContent.sections.size >= 4)
    val result = GstCalculatorEngine.calculate(
      GstCalculationInput(
        amount = BigDecimal("5000"),
        gstRatePercent = BigDecimal("40"),
        taxMode = TaxMode.EXCLUSIVE,
        supplyType = SupplyType.INTRA_STATE
      )
    )
    assertEquals(BigDecimal("2000.00"), result.totalGstAmount)
    assertEquals(BigDecimal("7000.00"), result.grandTotalAmount)
    assertEquals(9, result.slabComparison.size)
  }
}
