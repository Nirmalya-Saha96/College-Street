package live.nirmalyasaha.me.bookstore.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class BookEntity (
    @PrimaryKey val book_id: String,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "book_title") val bookTitle: String,
    @ColumnInfo(name = "book_author") val bookAuthor: String,
    @ColumnInfo(name = "book_price") val bookPrice: String,
    @ColumnInfo(name = "book_image") val bookImage: String
)