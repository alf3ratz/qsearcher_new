package course.ru.qsearcher.dao

import androidx.room.*
import course.ru.qsearcher.model.Event
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface EventDao {
    @Query("SELECT*FROM events")
    fun getFavorites(): Flowable<List<Event>>;

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addToFavorites(event: Event):Completable;

    @Delete
    fun removeFromFavourites(event: Event):Completable
}