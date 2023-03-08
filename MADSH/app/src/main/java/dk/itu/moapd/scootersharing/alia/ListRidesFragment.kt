package dk.itu.moapd.scootersharing.alia

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dk.itu.moapd.scootersharing.alia.databinding.FragmentListRidesBinding

class ListRidesFragment : Fragment() {
    private var _binding: FragmentListRidesBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    companion object {
        private lateinit var adapter: RideListAdapter
        private lateinit var ridesDB: RidesDB
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ridesDB = RidesDB.get(requireContext())
        adapter = RideListAdapter(requireContext(), R.layout.list_rides, ridesDB.getRidesList())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentListRidesBinding.inflate(inflater, container, false)
        _binding?.listView?.adapter = adapter
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}