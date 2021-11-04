package live.nirmalyasaha.me.bookstore.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [BookEntity::class], version = 2)
abstract class BookDatabase: RoomDatabase() {

    abstract fun bookDao(): BookDao

}