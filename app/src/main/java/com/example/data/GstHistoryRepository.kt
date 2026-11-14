package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.domain.BusinessProfile
import com.example.domain.SavedGstCalculation
import com.example.domain.SupplyType
import com.example.domain.TaxMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

private val Context.gstDataStore: DataStore<Preferences> by preferencesDataStore(name = "elemensys_gst_prefs")

class GstHistoryRepository(private val context: Context) {

    private object Keys {
        val SAVED_CALCULATIONS = stringPreferencesKey("saved_calculations_json")
        val BUSINESS_NAME = stringPreferencesKey("business_name")
        val BUSINESS_GSTIN = stringPreferencesKey("business_gstin")
        val BUSINESS_ADDRESS = stringPreferencesKey("business_address")
        val CURRENCY_SYMBOL = stringPreferencesKey("currency_symbol")
        val CHECKLIST_STATES = stringPreferencesKey("play_checklist_json")
    }

    val savedCalculationsFlow: Flow<List<SavedGstCalculation>> =
        context.gstDataStore.data.map { prefs ->
            val jsonStr = prefs[Keys.SAVED_CALCULATIONS].orEmpty()
            parseCalculations(jsonStr)
        }

    val businessProfileFlow: Flow<BusinessProfile> =
        context.gstDataStore.data.map { prefs ->
            BusinessProfile(
                businessName = prefs[Keys.BUSINESS_NAME].orEmpty(),
                gstin = prefs[Keys.BUSINESS_GSTIN].orEmpty(),
                addressLine = prefs[Keys.BUSINESS_ADDRESS].orEmpty()
            )
        }

    val currencySymbolFlow: Flow<String> =
        context.gstDataStore.data.map { prefs ->
            prefs[Keys.CURRENCY_SYMBOL] ?: "₹"
        }

    val checklistStatesFlow: Flow<Map<String, Boolean>> =
        context.gstDataStore.data.map { prefs ->
            parseChecklist(prefs[Keys.CHECKLIST_STATES].orEmpty())
        }

    suspend fun saveCalculation(item: SavedGstCalculation) {
        context.gstDataStore.edit { prefs ->
            val current = parseCalculations(prefs[Keys.SAVED_CALCULATIONS].orEmpty()).toMutableList()
            current.removeAll { it.id == item.id }
            current.add(0, item)
            val trimmed = current.take(30)
            prefs[Keys.SAVED_CALCULATIONS] = serializeCalculations(trimmed)
        }
    }

    suspend fun deleteCalculation(id: String) {
        context.gstDataStore.edit { prefs ->
            val current = parseCalculations(prefs[Keys.SAVED_CALCULATIONS].orEmpty())
                .filterNot { it.id == id }
            prefs[Keys.SAVED_CALCULATIONS] = serializeCalculations(current)
        }
    }

    suspend fun clearAllCalculations() {
        context.gstDataStore.edit { prefs ->
            prefs.remove(Keys.SAVED_CALCULATIONS)
        }
    }

    suspend fun saveBusinessProfile(profile: BusinessProfile) {
        context.gstDataStore.edit { prefs ->
            prefs[Keys.BUSINESS_NAME] = profile.businessName.trim()
            prefs[Keys.BUSINESS_GSTIN] = profile.gstin.trim().uppercase()
            prefs[Keys.BUSINESS_ADDRESS] = profile.addressLine.trim()
        }
    }

    suspend fun saveCurrencySymbol(symbol: String) {
        context.gstDataStore.edit { prefs ->
            prefs[Keys.CURRENCY_SYMBOL] = symbol
        }
    }

    suspend fun toggleChecklistItem(itemId: String, checked: Boolean) {
        context.gstDataStore.edit { prefs ->
            val map = parseChecklist(prefs[Keys.CHECKLIST_STATES].orEmpty()).toMutableMap()
            map[itemId] = checked
            val obj = JSONObject()
            map.forEach { (k, v) -> obj.put(k, v) }
            prefs[Keys.CHECKLIST_STATES] = obj.toString()
        }
    }

    private fun parseCalculations(jsonStr: String): List<SavedGstCalculation> {
        if (jsonStr.isBlank()) return emptyList()
        return runCatching {
            val array = JSONArray(jsonStr)
            buildList {
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    add(
                        SavedGstCalculation(
                            id = obj.getString("id"),
                            title = obj.optString("title", "GST Calculation"),
                            partyName = obj.optString("partyName", ""),
                            hsnCode = obj.optString("hsnCode", ""),
                            timestampMillis = obj.optLong("timestampMillis", 0L),
                            amount = obj.getString("amount"),
                            gstRatePercent = obj.getString("gstRatePercent"),
                            cessRatePercent = obj.optString("cessRatePercent", "0"),
                            quantity = obj.optInt("quantity", 1),
                            discountPercent = obj.optString("discountPercent", "0"),
                            taxMode = TaxMode.valueOf(obj.optString("taxMode", TaxMode.EXCLUSIVE.name)),
                            supplyType = SupplyType.valueOf(obj.optString("supplyType", SupplyType.INTRA_STATE.name)),
                            currencySymbol = obj.optString("currencySymbol", "₹"),
                            netTaxableFormatted = obj.optString("netTaxableFormatted", ""),
                            totalTaxFormatted = obj.optString("totalTaxFormatted", ""),
                            grandTotalFormatted = obj.optString("grandTotalFormatted", "")
                        )
                    )
                }
            }
        }.getOrDefault(emptyList())
    }

    private fun serializeCalculations(items: List<SavedGstCalculation>): String {
        val array = JSONArray()
        items.forEach { item ->
            val obj = JSONObject().apply {
                put("id", item.id)
                put("title", item.title)
                put("partyName", item.partyName)
                put("hsnCode", item.hsnCode)
                put("timestampMillis", item.timestampMillis)
                put("amount", item.amount)
                put("gstRatePercent", item.gstRatePercent)
                put("cessRatePercent", item.cessRatePercent)
                put("quantity", item.quantity)
                put("discountPercent", item.discountPercent)
                put("taxMode", item.taxMode.name)
                put("supplyType", item.supplyType.name)
                put("currencySymbol", item.currencySymbol)
                put("netTaxableFormatted", item.netTaxableFormatted)
                put("totalTaxFormatted", item.totalTaxFormatted)
                put("grandTotalFormatted", item.grandTotalFormatted)
            }
            array.put(obj)
        }
        return array.toString()
    }

    private fun parseChecklist(jsonStr: String): Map<String, Boolean> {
        if (jsonStr.isBlank()) return emptyMap()
        return runCatching {
            val obj = JSONObject(jsonStr)
            buildMap {
                val keys = obj.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    put(key, obj.optBoolean(key, false))
                }
            }
        }.getOrDefault(emptyMap())
    }
}
