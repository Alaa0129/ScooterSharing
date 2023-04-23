package dk.itu.moapd.scootersharing.alia.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import dk.itu.moapd.scootersharing.alia.databinding.ListScootersBinding
import dk.itu.moapd.scootersharing.alia.interfaces.RideItemClickListener
import dk.itu.moapd.scootersharing.alia.interfaces.ScooterItemClickListener
import dk.itu.moapd.scootersharing.alia.models.Scooter
import dk.itu.moapd.scootersharing.alia.utils.ScootersHolder

class ScooterListFirebaseAdapter(private val itemClickListener: ScooterItemClickListener,
                                 options: FirebaseRecyclerOptions<Scooter>) : FirebaseRecyclerAdapter<Scooter, ScootersHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScootersHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListScootersBinding.inflate(inflater, parent, false)
        return ScootersHolder(binding)
    }

    override fun onBindViewHolder(holder: ScootersHolder, position: Int, scooter: Scooter) {
        holder.apply {
            bind(scooter)
            itemView.setOnLongClickListener {
                itemClickListener.onItemClickListener(scooter, position)
                true
            }
        }
    }
}