package mx.org.bm.videogamesdb.data.db

import android.provider.SyncStateContract.Constants
import androidx.room.*
import mx.org.bm.videogamesdb.data.db.model.GameEntity
import mx.org.bm.videogamesdb.util.Constants.DATABASE_GAME_TABLE

@Dao
interface GameDao {

    @Insert
    suspend fun insertGame(game: GameEntity)

    @Query("SELECT * FROM ${DATABASE_GAME_TABLE}")
    suspend fun getAllGames(): List<GameEntity>

    @Update
    suspend fun updateGame(game: GameEntity)

    @Delete
    suspend fun deleteGame(game: GameEntity)
}