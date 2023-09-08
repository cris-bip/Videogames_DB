package mx.org.bm.videogamesdb.application

import android.app.Application
import mx.org.bm.videogamesdb.data.GameRepository
import mx.org.bm.videogamesdb.data.db.GameDatabase

class VideogamesDBApp(): Application() {

    private val database by lazy {
        GameDatabase.getDatabase((this@VideogamesDBApp))
    }

    val repository by lazy {
        GameRepository(database.gameDao())
    }

}