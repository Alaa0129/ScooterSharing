package dk.itu.moapd.scootersharing.alia

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import dk.itu.moapd.scootersharing.alia.databinding.FragmentStartRideBinding
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class StartRideFragment : Fragment() {
    private var _binding: FragmentStartRideBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }
    companion object {
        private val TAG = StartRideFragment::class.qualifiedName
        private lateinit var ridesDB: RidesDB
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ridesDB = RidesDB.get(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStartRideBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.startRideButton.setOnClickListener {
            if (binding.editTextName.text!!.isNotEmpty() && binding.editTextLocation.text!!.isNotEmpty()) {
                // Update the object attributes.
                val nameScooter = binding.editTextName.text.toString().trim()
                val nameLocation = binding.editTextLocation.text.toString().trim()

                ridesDB.addScooter(nameScooter, nameLocation, "")

                // Reset the text fields and update the UI.
                binding.editTextName.text?.clear()
                binding.editTextLocation.text?.clear()

                // Display ride info above editTextName, using a snackbar.
                val snackbar =
                    Snackbar
                        .make(
                            it,
                            "Ride started using ${ridesDB.getCurrentScooter()}",
                            Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                snackbar.setActionTextColor(requireContext().getColor(R.color.lightGrey))
                snackbar.anchorView = binding.editTextName
                val snackbarView = snackbar.view
                snackbarView.setBackgroundColor(requireContext().getColor(R.color.snackBarColor))
                snackbar.duration = 5000

                if (allPermissionsGranted())
                    getResult.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
                else
                    ActivityCompat.requestPermissions(
                        requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)

                snackbar.show()
                showMessage()
            }
        }
    }

    /**
     * A method to show a dialog to the users as ask permission to access their Android mobile
     * device resources.
     *
     * @return `PackageManager#PERMISSION_GRANTED` if the given pid/uid is allowed that permission,
     *      or `PackageManager#PERMISSION_DENIED` if it is not.
     */
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    private fun getFilePathFromUri(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = requireActivity().contentResolver.query(uri, projection, null, null, null)
        return cursor?.use {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            it.moveToFirst()
            it.getString(columnIndex)
        }
    }

    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
            if(it.resultCode == Activity.RESULT_OK){
                // Get the photo and do sth with it here
                val uri = it.data?.data
                if (uri != null) {
                    val filePath = getFilePathFromUri(uri)
                    val url = "file://$filePath"
                    val currentScooter = ridesDB.getCurrentScooter()
                    currentScooter.last_photo = url
                }
            }
        }

    private fun showMessage () {
        // Print a message in the ‘Logcat‘ system.
        Log.d(TAG, ridesDB.getCurrentScooterInfo())
    }
}