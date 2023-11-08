package com.dicoding.storyapphanif.ui.map

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapphanif.R
import com.dicoding.storyapphanif.data.Result
import com.dicoding.storyapphanif.databinding.ActivityMapsBinding
import com.dicoding.storyapphanif.ui.ViewModelFactory
import com.dicoding.storyapphanif.ui.welcome.WelcomeActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {


    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var mapViewModel: MapViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val factory : ViewModelFactory = ViewModelFactory.getInstance(this)
        mapViewModel = ViewModelProvider(this , factory) [MapViewModel::class.java]

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        
        mMap = googleMap
        mapStyle()
        mapViewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                mapViewModel.storyWithLocation(user.token)
            }
        }

        mapViewModel.storyWithLocation.observe(this) {
            when (it) {
                is Result.Loading -> loadingVisible(true)
                is Result.Error -> {
                    loadingVisible(false)
                }
                is Result.Success -> {
                    loadingVisible(false)
                    val boundsBuilder = LatLngBounds.Builder()
                    it.data.forEach { data ->
                        if (data.lat != null && data.lon != null ) {
                            val latLng = LatLng(data.lat, data.lon)
                            mMap.addMarker(
                                MarkerOptions()
                                    .position(latLng)
                                    .title(data.name)
                                    .snippet(data.description)
                            )
                            boundsBuilder.include(latLng)
                        }
                    }
                    val bounds: LatLngBounds = boundsBuilder.build()
                    mMap.animateCamera(
                        CameraUpdateFactory.newLatLngBounds(
                            bounds,
                            resources.displayMetrics.widthPixels,
                            resources.displayMetrics.heightPixels,
                            300
                        )
                    )
                }
            }
        }

    }

    private fun loadingVisible(isLoading : Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun mapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    companion object {
        private const val TAG = "MapsActivity"
    }
}


