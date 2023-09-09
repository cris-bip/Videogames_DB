package mx.org.bm.videogamesdb.ui

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mx.org.bm.videogamesdb.R
import mx.org.bm.videogamesdb.application.VideogamesDBApp
import mx.org.bm.videogamesdb.data.GameRepository
import mx.org.bm.videogamesdb.data.db.model.GameEntity
import mx.org.bm.videogamesdb.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var games: List<GameEntity> = emptyList()
    private lateinit var repository: GameRepository


    private lateinit var gameAdapter: GameAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        repository = (application as VideogamesDBApp).repository


        gameAdapter = GameAdapter(){game ->
            gameClicked(game)
        }
        binding.rvGames.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = gameAdapter
        }
        updateUI()
    }

    private fun updateUI(){
        lifecycleScope.launch {
            games = repository.getAllGames()

            if(games.isNotEmpty()){
                binding.tvSinRegistros.visibility = View.INVISIBLE
            }else{
                binding.tvSinRegistros.visibility = View.VISIBLE
            }

            gameAdapter.updateList(games)
        }
    }

    fun click(view: View) {
        val dialog = GameDialog( updateUI = {
            updateUI()
        }, message = {id ->
            message(id)
        })

        dialog.show(supportFragmentManager, "dialog")
    }

    private fun gameClicked(game: GameEntity){
         //Toast.makeText(this, "Click en el juego ${game.title}", Toast.LENGTH_LONG).show()
        val dialog = GameDialog(isNewGame = false, game = game, updateUI = {
            updateUI()
        }, message = {id ->
           message(id)
        })

        dialog.show(supportFragmentManager, "dialog")
    }

    private fun message(id: Int){
        Snackbar.make(binding.cl, getString(id), Snackbar.LENGTH_SHORT)
            .setTextColor(Color.parseColor("#FFFFFF"))
            .setBackgroundTint(Color.parseColor("#9E1734"))
            .show()
    }
}