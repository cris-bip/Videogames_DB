package mx.org.bm.videogamesdb.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
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
        holder.bind(games[position])

        holder.itemView.setOnClickListener {
            onGameClick(games[position])
        }

        holder.ivIcon.setOnClickListener {

        }
    }

    fun updateList(list: List<GameEntity>){
        games = list
        notifyDataSetChanged()
    }
}