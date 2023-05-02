package dk.itu.moapd.scootersharing.alia.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import dk.itu.moapd.scootersharing.alia.databinding.ListRidesBinding
import dk.itu.moapd.scootersharing.alia.interfaces.RideItemClickListener
import dk.itu.moapd.scootersharing.alia.models.Ride
import dk.itu.moapd.scootersharing.alia.utils.RidesHolder

class RidesListFirebaseAdapter(private val itemClickListener: RideItemClickListener,
                               options: FirebaseRecyclerOptions<Ride>) : FirebaseRecyclerAdapter<Ride, RidesHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RidesHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListRidesBinding.inflate(inflater, parent, false)
        return RidesHolder(binding)
    }

    override fun onBindViewHolder(holder: RidesHolder, position: Int, ride: Ride) {
        holder.apply {
            bind(ride)
            itemView.setOnClickListener {
                itemClickListener.onItemClickListener(ride, position)
            }
        }
    }
}