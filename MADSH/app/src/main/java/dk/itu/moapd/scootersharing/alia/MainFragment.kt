package dk.itu.moapd.scootersharing.alia

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import dk.itu.moapd.scootersharing.alia.databinding.ActivityMainBinding
import dk.itu.moapd.scootersharing.alia.databinding.FragmentMainBinding

class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        _binding?.listView?.adapter = adapter
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.startRidePageButton.setOnClickListener {
            findNavController().navigate(R.id.startRideFragment)
        }
        binding.updateRidePageButton.setOnClickListener {
            findNavController().navigate(R.id.updateRideFragment)
        }
    }
}