package com.example.interiordesign_ai.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.interiordesign_ai.model.SaveEstimateRequest
import com.example.interiordesign_ai.network.RetrofitClient
import com.example.interiordesign_ai.session.SessionManager
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.roundToLong

data class BudgetBreakdown(
    val total: Long,
    val flooring: Long,
    val walls: Long,
    val ceiling: Long,
    val furniture: Long,
    val lighting: Long,
    val accessories: Long,
    val formattedTotal: String
)

class BudgetViewModel(app: Application) : AndroidViewModel(app) {

    private val session = SessionManager(app)
    private val api     = RetrofitClient.api

    private val _breakdown = MutableStateFlow<BudgetBreakdown?>(null)
    val breakdown: StateFlow<BudgetBreakdown?> = _breakdown.asStateFlow()

    // ── Indian Room Base Rates (₹ per sq.ft, mid-point of realistic 2025-26 range) ──
    private val baseRatePerSqft = mapOf(
        "Living Room"   to 1650.0,
        "Bedroom"       to 1300.0,
        "Master Bedroom" to 1550.0,
        "Kitchen"       to 2400.0,
        "Bathroom"      to 1850.0,
        "Dining Room"   to 1400.0,
        "Home Office"   to 1750.0,
        "Kids Room"     to 1100.0,
        "Pooja Room"    to 950.0,
        "Balcony"       to 750.0,
        "Entire House"  to 1800.0
    )

    // Quality multipliers
    private val qualityMultiplier = mapOf(
        "Budget"   to 0.65,
        "Standard" to 1.00,
        "Premium"  to 1.65,
        "Luxury"   to 2.60
    )

    // City-tier cost adjustment
    private val metroMultiplier = mapOf(
        "Mumbai" to 1.38, "Delhi" to 1.35, "New Delhi" to 1.35, "Bengaluru" to 1.32,
        "Hyderabad" to 1.22, "Chennai" to 1.20, "Pune" to 1.18, "Kolkata" to 1.15,
        "Ahmedabad" to 1.12, "Noida" to 1.25, "Gurgaon" to 1.28, "Thane" to 1.20,
        "Secunderabad" to 1.18, "Kochi" to 1.12, "Jaipur" to 1.05, "Lucknow" to 1.04,
        "Chandigarh" to 1.10, "Indore" to 1.02
    )

    // Breakdown proportions
    private val proportions = mapOf(
        "Flooring"    to 0.20,
        "Walls"       to 0.15,
        "Ceiling"     to 0.10,
        "Furniture"   to 0.35,
        "Lighting"    to 0.08,
        "Accessories" to 0.12
    )

    fun calculateEstimate(
        state: String,
        city: String,
        district: String,
        roomType: String,
        areaSqft: Double,
        qualityTier: String
    ) {
        if (areaSqft <= 0) return

        val baseRate  = baseRatePerSqft[roomType] ?: 1500.0
        val qualMult  = qualityMultiplier[qualityTier] ?: 1.0
        val cityMult  = metroMultiplier[city] ?: 1.0

        val rawTotal  = (areaSqft * baseRate * qualMult * cityMult).roundToLong()

        val flooring    = (rawTotal * proportions["Flooring"]!!).roundToLong()
        val walls       = (rawTotal * proportions["Walls"]!!).roundToLong()
        val ceiling     = (rawTotal * proportions["Ceiling"]!!).roundToLong()
        val furniture   = (rawTotal * proportions["Furniture"]!!).roundToLong()
        val lighting    = (rawTotal * proportions["Lighting"]!!).roundToLong()
        val accessories = rawTotal - flooring - walls - ceiling - furniture - lighting

        _breakdown.value = BudgetBreakdown(
            total        = rawTotal,
            flooring     = flooring,
            walls        = walls,
            ceiling      = ceiling,
            furniture    = furniture,
            lighting     = lighting,
            accessories  = accessories,
            formattedTotal = formatIndianCurrency(rawTotal)
        )

        // Persist to backend
        viewModelScope.launch {
            val userId = session.getUserId()
            if (userId == 0) return@launch
            try {
                val breakdownMap = mapOf(
                    "flooring" to flooring, "walls" to walls, "ceiling" to ceiling,
                    "furniture" to furniture, "lighting" to lighting, "accessories" to accessories
                )
                api.saveEstimate(
                    SaveEstimateRequest(
                        userId       = userId,
                        state        = state,
                        city         = city,
                        district     = district,
                        roomType     = roomType,
                        areaSqft     = areaSqft,
                        qualityTier  = qualityTier,
                        totalCost    = rawTotal.toDouble(),
                        breakdownJson= Gson().toJson(breakdownMap)
                    )
                )
            } catch (_: Exception) {}
        }
    }

    fun clearResult() { _breakdown.value = null }

    private fun formatIndianCurrency(amount: Long): String {
        return when {
            amount >= 10_000_000 -> "₹%.2f Cr".format(amount / 10_000_000.0)
            amount >= 100_000    -> "₹%.2f L".format(amount / 100_000.0)
            else                 -> "₹%,d".format(amount)
        }
    }
}
