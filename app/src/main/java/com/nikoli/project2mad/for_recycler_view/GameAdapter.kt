import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.nikoli.project2mad.R
import com.nikoli.project2mad.for_recycler_view.GameItem

/**
 * Game adapter
 *
 * @property gameList
 * @constructor Create empty Game adapter
 */
class GameAdapter(private var gameList: List<GameItem>) : RecyclerView.Adapter<GameAdapter.GameViewHolder>() {

    private var filteredList: List<GameItem> = gameList

    /**
     * Game view holder
     *
     * @constructor
     *
     * @param itemView
     */
    class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val gameName: TextView = itemView.findViewById(R.id.gameName)
        val gameTime: TextView = itemView.findViewById(R.id.gameTime)
        val gameAccuracy: TextView = itemView.findViewById(R.id.gameAccuracy)
        val gameDatePlayed: TextView = itemView.findViewById(R.id.gameDatePlayed)
        val itemContainer: View = itemView.findViewById(R.id.item_container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item, parent, false)
        return GameViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val game = filteredList[position]
        holder.gameName.text = game.name
        holder.gameTime.text = "Reaction time: ${game.reactionTime} ms"
        holder.gameAccuracy.text = "Accuracy: ${game.accuracy}%"
        holder.gameDatePlayed.text = game.datePlayed

        val colorRes = when (game.name) {
            "New Game" -> R.color.main_violet
            "Trail Making Test" -> R.color.main_green
            "Number Size Congruency" -> R.color.main_blue
            else -> android.R.color.white
        }
        holder.itemContainer.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, colorRes))
    }

    override fun getItemCount() = filteredList.size

    /**
     * Update list
     *
     * @param newList
     */
    fun updateList(newList: List<GameItem>) {
        gameList = newList.sortedByDescending { it.datePlayed }
        filterItems()
    }

    private fun filterItems() {
        filteredList = gameList
        notifyDataSetChanged()
    }

    /**
     * Filter by name
     *
     * @param gameName
     */
    fun filterByName(gameName: String) {
        if (gameName.isEmpty()) {
            filteredList = gameList
        } else {
            filteredList = gameList.filter { it.name == gameName }
        }
        notifyDataSetChanged()
    }
}
