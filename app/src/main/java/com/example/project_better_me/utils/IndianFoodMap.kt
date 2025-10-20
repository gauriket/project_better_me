package com.example.project_better_me.utils

import android.content.Context
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader

data class IndianFood(
    val name: String,
    val calories: Int = 0,
    val carbs: Double = 0.0,
    val protein: Double = 0.0,
    val fat: Double = 0.0,
    val freeSugar: Double = 0.0,
    val fibre: Double = 0.0,
    val sodium: Double = 0.0,
    val calcium: Double = 0.0,
    val iron: Double = 0.0,
    val vitaminC: Double = 0.0,
    val folate: Double = 0.0
)

fun loadIndianFoodsFromCSV(context: Context, filename: String = "indian_foods.csv"): List<IndianFood> {
    val foods = mutableListOf<IndianFood>()
    try {
        val inputStream = context.assets.open(filename)
        csvReader().open(inputStream) {
            readAllWithHeaderAsSequence().forEach { row ->
                val food = IndianFood(
                    name = row["Dish Name"]?.trim() ?: "",
                    calories = row["Calories (kcal)"]?.toDoubleOrNull()?.toInt() ?: 0,
                    carbs = row["Carbohydrates (g)"]?.toDoubleOrNull() ?: 0.0,
                    protein = row["Protein (g)"]?.toDoubleOrNull() ?: 0.0,
                    fat = row["Fats (g)"]?.toDoubleOrNull() ?: 0.0,
                    freeSugar = row["Free Sugar (g)"]?.toDoubleOrNull() ?: 0.0,
                    fibre = row["Fibre (g)"]?.toDoubleOrNull() ?: 0.0,
                    sodium = row["Sodium (mg)"]?.toDoubleOrNull() ?: 0.0,
                    calcium = row["Calcium (mg)"]?.toDoubleOrNull() ?: 0.0,
                    iron = row["Iron (mg)"]?.toDoubleOrNull() ?: 0.0,
                    vitaminC = row["Vitamin C (mg)"]?.toDoubleOrNull() ?: 0.0,
                    folate = row["Folate (µg)"]?.toDoubleOrNull() ?: 0.0
                )
                foods.add(food)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return foods.sortedBy { it.name.lowercase() }
}

