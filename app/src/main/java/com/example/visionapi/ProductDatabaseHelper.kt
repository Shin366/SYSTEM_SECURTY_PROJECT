package com.example.visionapi

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class Product(var name: String, var kcal: String, var target: String, var type: String, var total: String)

class ProductDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "mydatabase.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_PRODUCT = "product"
        const val COLUMN_NAME = "name"
        const val COLUMN_KCAL = "kcal"
        const val COLUMN_TARGET = "target"
        const val COLUMN_TYPE  = "type"
        const val COLUMN_TOTAL = "total"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createProductTableQuery = ("CREATE TABLE $TABLE_PRODUCT (" +
                "$COLUMN_NAME TEXT, " +
                "$COLUMN_KCAL TEXT, " +
                "$COLUMN_TARGET TEXT, " +
                "$COLUMN_TYPE TEXT, " +
                "$COLUMN_TOTAL TEXT)"
                )
        db.execSQL(createProductTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PRODUCT")
        onCreate(db)
    }

    // 데이터 삽입 함수
    fun insertProduct(name: String, kcal: String, target: String, type:String, total: String) {
        val db = writableDatabase

        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_KCAL, kcal)
            put(COLUMN_TARGET, target)
            put(COLUMN_TYPE, type)
            put(COLUMN_TOTAL, total)
        }

        db.insert(TABLE_PRODUCT, null, values)
        db.close()
    }

    // 데이터 조회 함수
    fun getAllProducts(): List<Product> {
        val db = readableDatabase
        val cursor = db.query(TABLE_PRODUCT, null, null, null, null, null, null)
        val products = mutableListOf<Product>()
        val productNames = mutableSetOf<String>() // 중복된 이름을 확인할 Set

        with(cursor) {
            while (moveToNext()) {
                val name = getString(getColumnIndexOrThrow(COLUMN_NAME))
                val kcal = getString(getColumnIndexOrThrow(COLUMN_KCAL))
                val target = getString(getColumnIndexOrThrow(COLUMN_TARGET))
                val type = getString(getColumnIndexOrThrow(COLUMN_TYPE))
                val total = getString(getColumnIndexOrThrow(COLUMN_TOTAL))
                // 중복된 제품 이름이 없으면 추가
                if (!productNames.contains(name)) {
                    products.add(Product(name, kcal, target, type, total))
                    productNames.add(name)  // 추가한 제품 이름을 Set에 저장
                }
            }
        }
        cursor.close()
        db.close()

        return products
    }

    fun getProduct(name : String) : Product{
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Product WHERE name = '"+name+"'", null)
        val p: Product = Product("1", "1", "1", "1", "1")

        if(cursor != null && cursor.count != 0){
            while(cursor.moveToNext()){
                p.name = cursor.getString(0)
                p.kcal = cursor.getString(1)
                p.target = cursor.getString(2)
                p.type = cursor.getString(3)
                p.total = cursor.getString(4)

            }
        }
        cursor.close()
        db.close()
        return p
    }
    // 데이터 삭제 함수
    fun deleteProduct(name: String) {
        val db = writableDatabase
        db.delete(TABLE_PRODUCT, "$COLUMN_NAME = ?", arrayOf(name))
        db.close()
    }
}
