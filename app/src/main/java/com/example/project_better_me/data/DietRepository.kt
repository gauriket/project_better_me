package com.example.project_better_me.data

class DietRepository(private val dietDao: DietDao) {
    suspend fun addDiet(diet: Diet) = dietDao.insertDiet(diet)
    fun getDietForDate(start: Long, end: Long) = dietDao.getDietByDate(start, end)
    suspend fun removeDiet(diet: Diet) = dietDao.deleteDiet(diet)
}
