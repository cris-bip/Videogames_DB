package mx.org.bm.videogamesdb.data

import mx.org.bm.videogamesdb.data.db.GameDao
import mx.org.bm.videogamesdb.data.db.model.GameEntity

class GameRepository(private val gameDao:GameDao){

    suspend fun insertGame(game: GameEntity){
        gameDao.insertGame(game)
    }

    suspend fun getAllGames(): List<GameEntity> = gameDao.getAllGames()

    suspend fun updateGame(game: GameEntity){
        gameDao.updateGame(game)
    }

    suspend fun deleteGame(game: GameEntity){
        gameDao.deleteGame(game)
    }
}