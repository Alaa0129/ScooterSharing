package dk.itu.moapd.scootersharing.alia

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dk.itu.moapd.scootersharing.alia.databinding.ListRidesBinding

class RideListAdapter(private val rides: List<Scooter>)
    : RecyclerView.Adapter<RidesHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RidesHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListRidesBinding.inflate(inflater, parent, false)
        return RidesHolder(binding)
    }

    override fun onBindViewHolder(holder: RidesHolder, position: Int) {
        val ride = rides[position]
        holder.bind(ride)
    }

    override fun getItemCount() = rides.size
}