package com.pcit.shop.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pcit.shop.model.Product

@Database(
    entities = [Product::class],
    version = 1
)
abstract class ProductDatabase : RoomDatabase() {
    abstract val productDao: ProductDao
}