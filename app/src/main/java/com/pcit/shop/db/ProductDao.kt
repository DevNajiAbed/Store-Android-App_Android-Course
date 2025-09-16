package com.pcit.shop.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.pcit.shop.model.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Upsert
    suspend fun upsertProduct(product: Product)

    @Query("SELECT * FROM product")
    fun getAllProducts(): Flow<List<Product>>

    @Query("SELECT * FROM product WHERE id = :id")
    suspend fun getProductById(id: Int): Product
}