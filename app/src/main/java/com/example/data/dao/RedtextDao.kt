package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.RedtextItem
import kotlinx.coroutines.flow.Flow

@Dao
interface RedtextDao {
    @Query("SELECT * FROM redtext_items ORDER BY isFavorite DESC, updatedAt DESC")
    fun getAllItems(): Flow<List<RedtextItem>>

    @Query("SELECT * FROM redtext_items WHERE category = :category ORDER BY updatedAt DESC")
    fun getItemsByCategory(category: String): Flow<List<RedtextItem>>

    @Query("SELECT * FROM redtext_items WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%' ORDER BY updatedAt DESC")
    fun searchItems(query: String): Flow<List<RedtextItem>>

    @Query("SELECT * FROM redtext_items WHERE id = :id LIMIT 1")
    suspend fun getItemById(id: Long): RedtextItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: RedtextItem): Long

    @Update
    suspend fun updateItem(item: RedtextItem)

    @Delete
    suspend fun deleteItem(item: RedtextItem)

    @Query("DELETE FROM redtext_items WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM redtext_items")
    suspend fun deleteAll()
}
