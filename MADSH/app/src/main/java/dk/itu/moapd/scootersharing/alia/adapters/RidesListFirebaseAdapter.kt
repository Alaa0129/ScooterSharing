package dk.itu.moapd.scootersharing.alia.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import dk.itu.moapd.scootersharing.alia.databinding.ListRidesBinding
import dk.itu.moapd.scootersharing.alia.models.Scooter
import dk.itu.moapd.scootersharing.alia.utils.RidesHolder

class RidesListFirebaseAdapter(private val itemClickListener: ItemClickListener,
                               options: FirebaseRecyclerOptions<Scooter>) : FirebaseRecyclerAdapter<Scooter, RidesHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RidesHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListRidesBinding.inflate(inflater, parent, false)
        return RidesHolder(binding)
    }

    override fun onBindViewHolder(holder: RidesHolder, position: Int, scooter: Scooter) {
        holder.apply {
            bind(scooter)
            itemView.setOnLongClickListener {
                itemClickListener.onItemClickListener(scooter, position)
                true
            }
        }
        holder.bind(scooter)
        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(scooter)
        }
    }
}