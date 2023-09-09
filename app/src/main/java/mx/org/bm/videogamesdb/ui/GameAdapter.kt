package mx.org.bm.videogamesdb.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import mx.org.bm.videogamesdb.R
import mx.org.bm.videogamesdb.data.db.model.GameEntity
import mx.org.bm.videogamesdb.databinding.GameElementBinding

class GameAdapter(private  val onGameClick: (GameEntity) -> Unit): RecyclerView.Adapter<GameAdapter.ViewHolder>() {

    private var games: List<GameEntity> = emptyList()

    class ViewHolder(private val binding: GameElementBinding): RecyclerView.ViewHolder(binding.root){
        val ivIcon = binding.ivIcon

        fun bind(game: GameEntity){
            binding.apply {
                tvTitle.text = game.title
                tvGenre.text = game.genre
                tvDeveloper.text = game.developer
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = GameElementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = games.count()


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val game = games[position]
        holder.bind(game)

        holder.itemView.setOnClickListener {
            onGameClick(game)
        }

        holder.ivIcon.setOnClickListener {

        }

        holder.ivIcon.setImageResource(getIconWithGenreId(game.genreId))
    }

    private fun getIconWithGenreId(genreId: Int): Int{
        return when (genreId){
            0 -> R.drawable.shooter_icon
            1 -> R.drawable.fighting_icon
            2 -> R.drawable.strategy_icon
            4 -> R.drawable.music_icon
            5 -> R.drawable.sports_icon
            6 -> R.drawable.racing_icon
            else -> R.drawable.no_genre_icon
        }
    }


    fun updateList(list: List<GameEntity>){
        games = list
        notifyDataSetChanged()
    }
}