package dk.itu.moapd.scootersharing.alia

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import dk.itu.moapd.scootersharing.alia.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding

    companion object {
        private lateinit var adapter: RideListAdapter
        private lateinit var ridesDB: RidesDB
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ridesDB = RidesDB.get(this)

        adapter = RideListAdapter(this, R.layout.list_rides, ridesDB.getRidesList())
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        mainBinding.content.ridesListView.adapter = adapter

        setContentView(R.layout.activity_main)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        mainBinding.content.apply {

            // The button "Start Ride" page listener.
            startRidePageButton.setOnClickListener {
                val intent = Intent(baseContext, StartRideActivity::class.java)
                startActivity(intent)
            }

            updateRidePageButton.setOnClickListener {
                val intent = Intent(baseContext, UpdateRideActivity::class.java)
                startActivity(intent)
            }
        }

        val view = mainBinding.root
        setContentView(view)
    }
}