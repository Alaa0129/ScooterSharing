package dk.itu.moapd.scootersharing.alia.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dk.itu.moapd.scootersharing.alia.R
import dk.itu.moapd.scootersharing.alia.adapters.RidesListFirebaseAdapter
import dk.itu.moapd.scootersharing.alia.databinding.FragmentListRidesBinding
import dk.itu.moapd.scootersharing.alia.interfaces.ItemClickListener
import dk.itu.moapd.scootersharing.alia.models.Ride
import dk.itu.moapd.scootersharing.alia.models.Scooter

class ListRidesFragment : Fragment(), ItemClickListener {
    private var _binding: FragmentListRidesBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    companion object {
        private lateinit var adapter: RidesListFirebaseAdapter
        private lateinit var database: DatabaseReference
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = Firebase.database("https://scootersharing-jokf-alia-default-rtdb.europe-west1.firebasedatabase.app").reference
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentListRidesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Firebase.auth.currentUser?.let {
            val query = database.child("rides").child(it.uid)

            val options = FirebaseRecyclerOptions.Builder<Ride>()
                .setQuery(query, Ride::class.java)
                .setLifecycleOwner(this)
                .build()

            adapter = RidesListFirebaseAdapter(this, options)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClickListener(ride: Ride, position: Int) {
        showDeleteConfirmationDialog(position)
    }

    private fun showDeleteConfirmationDialog(position: Int) {
        MaterialAlertDialogBuilder(binding.root.context)
            .setTitle(R.string.deleteRideDialogTitle)
            .setMessage(R.string.deleteRideDialogMessage)
            .setCancelable(true)
            .setPositiveButton(R.string.yes) { _, _ ->
                database.child("rides").child(Firebase.auth.currentUser!!.uid).child(position.toString()).removeValue()
            }
            .setNegativeButton(R.string.no) { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }
}