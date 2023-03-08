package dk.itu.moapd.scootersharing.alia

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dk.itu.moapd.scootersharing.alia.databinding.FragmentMainBinding

class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
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
        binding.showRidesButton.setOnClickListener {
            findNavController().navigate(R.id.listRidesFragment)
        }
    }
}