package com.udacity.project4.locationreminders.savereminder.selectreminderlocation

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject
import java.util.*

const val REQUEST_LOCATION_PERMISSION = 33
const val TAG_SELECTED_LOCATION = "Selected Location"

open class SelectLocationFragment : BaseFragment(), OnMapReadyCallback {

    private lateinit var map: GoogleMap

    private lateinit var POIofUser: PointOfInterest


    //Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSelectLocationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this


        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)


        val mapFragment =
            (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).also {
                it.getMapAsync(this)
            }




        return binding.root
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        map = googleMap!!

        val latitude = -34.0
        val longitude = 151.0
        val zoom = 15f

        val latLng = LatLng(latitude, longitude)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom))
        map.addMarker(MarkerOptions().position(latLng))

        enableMyLocation()
        setStyleMap(map)
        setPoiListener(map)
        setMapLongClick(map)


        onLocationSelected()
    }

    private fun setMapLongClick(map: GoogleMap) {
        map.setOnMapLongClickListener { latLng ->
            POIofUser = PointOfInterest(latLng, "", getString(R.string.dropped_pin))
            val snippet = String.format(
                Locale.getDefault(), "Lat : %1$.5f ,Long: %2$.5f",
                latLng.latitude, latLng.longitude
            )


            map.addMarker(
                MarkerOptions().position(latLng).title(getString(R.string.dropped_pin))
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            )


        }
    }

    private fun onLocationSelected() {
        binding.saveButton.setOnClickListener {
            if (this::POIofUser.isInitialized) {
                _viewModel.latitude.value = POIofUser.latLng.latitude
                _viewModel.longitude.value = POIofUser.latLng.longitude
                _viewModel.reminderSelectedLocationStr.value = POIofUser.name
                _viewModel.selectedPOI.value = POIofUser
                _viewModel.navigationCommand.value = NavigationCommand.Back

            } else {
                Toast.makeText(context, "Please select a location", Toast.LENGTH_LONG).show()
            }

        }
    }


    private fun setPoiListener(map: GoogleMap) {
        map.setOnPoiClickListener { poi ->
            map.clear()
            POIofUser = poi
            val poiMarker = map.addMarker(MarkerOptions().position(poi.latLng).title(poi.name))

            map.addCircle(
                CircleOptions().center(poi.latLng)
                    .radius(200.0)
                    .strokeColor(Color.argb(255, 255, 0, 0))
                    .fillColor(Color.argb(64, 255, 0, 0)).strokeWidth(4F)
            )

            poiMarker?.showInfoWindow()

        }
    }

    private fun setStyleMap(map: GoogleMap) {
        try {
            val success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(),
                    R.raw.style_map
                )
            )
            if (!success) {
                Log.e(TAG_SELECTED_LOCATION, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG_SELECTED_LOCATION, "Can't find style. Error: ", e)

        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.normal_map -> {
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            map.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            map.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }


    //methods for permissions and request permissions

    private fun enableMyLocation() {
        when {
            (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) -> {

                map.isMyLocationEnabled = true

                Toast.makeText(context, "Location permission is granted.", Toast.LENGTH_LONG).show()
            }
            (ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )) -> {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_LOCATION_PERMISSION
                )
                Toast.makeText(
                    context,
                    "you need this permission to use all features of this application!",
                    Toast.LENGTH_LONG
                ).show()

            }

            else -> {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_LOCATION_PERMISSION
                )
                Toast.makeText(
                    context,
                    "Location permission was denied,Please allow this permission to use all Application Features",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation()

            } else {
                Toast.makeText(
                    context,
                    "Location permission was not granted,Please allow this permission!",
                    Toast.LENGTH_SHORT
                ).show()


        }
    }
}
}
