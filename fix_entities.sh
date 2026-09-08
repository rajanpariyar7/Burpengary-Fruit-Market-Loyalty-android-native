echo '@Entity(tableName = "categories")\ndata class Category(\n    @PrimaryKey(autoGenerate = true) val id: Int = 0,\n    val name: String\n)' >> app/src/main/java/com/example/data/model/Entities.kt
sed -i 's/AuditLog::class\], version = 6/AuditLog::class, Category::class\], version = 7/g' app/src/main/java/com/example/data/local/LoyaltyDatabase.kt
