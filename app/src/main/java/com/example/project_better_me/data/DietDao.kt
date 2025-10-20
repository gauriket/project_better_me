package com.example.project_better_me.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DietDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiet(diet: Diet)

    @Query("SELECT * FROM diet WHERE date BETWEEN :start AND :end")
    fun getDietByDate(start: Long, end: Long): Flow<List<Diet>>

    @Delete
    suspend fun deleteDiet(diet: Diet)
}
