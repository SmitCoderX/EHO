package com.driver.eho.ui.Home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.driver.eho.R
import com.driver.eho.SharedPreferenceManager
import com.driver.eho.databinding.FragmentHomeBinding
import com.driver.eho.model.Login.DriverSignInResponse
import com.driver.eho.ui.fragment.AmbulancRequestBottomFragment
import com.driver.eho.ui.fragment.AmbulanceAcceptBottomFragment
import com.driver.eho.ui.viewModel.viewModelFactory.HomeFragmentViewModelProviderFactory
import com.driver.eho.ui.viewModels.HomeViewModel
import com.driver.eho.utils.Constants.REQUEST
import com.driver.eho.utils.Constants.TAG
import com.driver.eho.utils.Constants.snackbarError
import com.driver.eho.utils.EHOApplication
import com.driver.eho.utils.Resources
import com.driver.eho.utils.SocketHandler
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

class HomeFragment : Fragment(R.layout.fragment_home), OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var client: FusedLocationProviderClient
    private var lasttLocation: Location? = null
    private var mGoogleMao: GoogleMap? = null
    internal var mCurrLocationMarker: Marker? = null
    private lateinit var mLocationRequest: LocationRequest
    private lateinit var prefs: SharedPreferenceManager
    private var driverDetails: DriverSignInResponse? = DriverSignInResponse()
    private val homeViewModel: HomeViewModel by viewModels {
        HomeFragmentViewModelProviderFactory(
            requireActivity().application,
            (requireActivity().application as EHOApplication).repository
        )
    }
    private val handler = Handler(Looper.myLooper()!!)

    companion object {
        private const val REQUEST_CODE = 101
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        prefs = SharedPreferenceManager(requireContext())

        homeViewModel.getDriverDetails(
            prefs.getToken().toString()
        )
        profileData()

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        mGoogleMao?.let { onMapReady(it) }
        client = LocationServices.getFusedLocationProviderClient(requireContext())

        binding.toggle.setOnToggledListener { _, isOn ->
            if (isOn) {
                prefs.setToggleState(true)
            } else {
                prefs.setToggleState(false)
                SocketHandler.closeConnection()
            }
        }


        SocketHandler.setSocket()
        if (prefs.getToggleState()) {
            binding.toggle.isOn = true
            SocketHandler.establishConnection()

            handler.postDelayed({
                SocketHandler.emitSubscribe(prefs.getData()?.data?.id.toString())
            }, 500)

            handler.postDelayed({
                startListeners()
            }, 800)


            // Send Location using Timer every 1 Sec
            /*  handler.postDelayed({
                  sendLocation()
              }, 1000)*/

        } else {
            binding.toggle.isOn = false
            SocketHandler.closeConnection()
        }

    }

    private var mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val locationList = locationResult.locations
            if (locationList.isNotEmpty()) {
                //The last location in the list is the newest
                val location = locationList.last()
                prefs.setLat(location.latitude.toString())
                prefs.setLong(location.longitude.toString())
                Log.i("MapsActivity", "Location: " + location.latitude + " " + location.longitude)
                lasttLocation = location
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker?.remove()
                }

                //Place current location marker
                val latLng = LatLng(location.latitude, location.longitude)
                val markerOptions = MarkerOptions()
                markerOptions.position(latLng)
                markerOptions.title("Current Position")
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                mCurrLocationMarker = mGoogleMao?.addMarker(markerOptions)

                //move map camera
                mGoogleMao?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f))
            }
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMao = googleMap
        mGoogleMao?.uiSettings?.isZoomControlsEnabled = true
        mGoogleMao?.setOnMarkerClickListener(this)
        mLocationRequest = LocationRequest.create().apply {
            interval = 100
            fastestInterval = 50
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            maxWaitTime = 100
        }
        setUpMap()

        /* val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
        val markerOptions = MarkerOptions().position(latLng).title("${latLng}").icon(BitmapFromVector(requireContext(),R.drawable.destination))
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12f))
        mMap.addMarker(markerOptions)*/
    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE
            )
            setUpMap()
            return
        }
        mGoogleMao?.isMyLocationEnabled = true
        client.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper()!!)
        client.lastLocation.addOnSuccessListener(requireActivity()) { location ->
            if (location != null) {
                lasttLocation = location
                val currentLatLog = LatLng(location.latitude, location.longitude)
                placeMakerOnMap(currentLatLog)
                mGoogleMao?.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLog, 12f))
                Log.d(TAG, "setUpMap: $currentLatLog")
            }
        }
    }


    private fun placeMakerOnMap(currentLatLog: LatLng) {
        val markerOptions = MarkerOptions().position(currentLatLog)
        markerOptions.title("$currentLatLog")
        mGoogleMao?.addMarker(markerOptions)
    }

    private fun BitmapFromVector(context: Context, vectorResId: Int): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
        vectorDrawable!!.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )
        val bitmap =
            Bitmap.createBitmap(
                vectorDrawable.intrinsicWidth,
                vectorDrawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    override fun onMarkerClick(p0: Marker) = false

    override fun onPause() {
        super.onPause()

        client.removeLocationUpdates(mLocationCallback)
    }

    private fun profileData() {
        homeViewModel.driverMutableLiveData.observe(viewLifecycleOwner) { resources ->
            when (resources) {
                is Resources.Success -> {
                    hideLoadingView()
                    driverDetails = resources.data!!
                }

                is Resources.Error -> {
                    hideLoadingView()
                    snackbarError(binding.root, resources.message.toString())
                }

                is Resources.Loading -> {
                    showLoadingView()
                }
            }
        }
    }

    private fun showLoadingView() {
        binding.viewLoader.visibility = View.VISIBLE
    }

    private fun hideLoadingView() {
        binding.viewLoader.visibility = View.GONE
    }

    private fun sendLocation() {
        SocketHandler.emitSentLocation(
            latitude = lasttLocation?.latitude.toString(),
            longitude = lasttLocation?.longitude.toString(),
            driverId = driverDetails?.data?.id.toString()
        )
    }

    private fun startListeners() {
        stopListeners()
        SocketHandler.sendRequestDriverListener {
            val bundle = Bundle()
            bundle.putParcelable(REQUEST, it)
            Log.d(TAG, "sendRequestDriverListener: $it")
            val ambulancRequestBottomFragment = AmbulancRequestBottomFragment()
            ambulancRequestBottomFragment.setTargetFragment(this, 1)
            ambulancRequestBottomFragment.arguments = bundle
            ambulancRequestBottomFragment.show(
                parentFragmentManager,
                ambulancRequestBottomFragment.tag
            )
        }

        // this listener will show when driver accepts the request
        SocketHandler.acceptRequestDriverListener {
            Log.d(TAG, "acceptRequestDriverListener: $it")
            val bundle = Bundle()
            bundle.putParcelable(REQUEST, it)
            Log.d(TAG, "sendRequestDriverListener: $it")
            val ambulancRequestBottomFragment = AmbulanceAcceptBottomFragment()
            ambulancRequestBottomFragment.setTargetFragment(this, 1)
            ambulancRequestBottomFragment.arguments = bundle
            ambulancRequestBottomFragment.show(
                parentFragmentManager,
                ambulancRequestBottomFragment.tag
            )
        }

        SocketHandler.emitIsAccepted(
            driverDetails?.data?.id.toString()
        )

        SocketHandler.dropOffRequestDriverListener {
            val bundle = Bundle()
            bundle.putParcelable(REQUEST, it)
            Log.d(TAG, "sendRequestDriverListener: $it")
            val ambulancRequestBottomFragment = AmbulancRequestBottomFragment()
            ambulancRequestBottomFragment.setTargetFragment(this, 1)
            ambulancRequestBottomFragment.arguments = bundle
            ambulancRequestBottomFragment.show(
                parentFragmentManager,
                ambulancRequestBottomFragment.tag
            )
        }

        SocketHandler.rejectRequestDriverListener {
            Log.d(TAG, "startListeners Reject: $it")
        }

        SocketHandler.cancelRequestDriverListener {
            Log.d(TAG, "startListeners Cancel: $it")
        }
    }

    private fun stopListeners() {
        SocketHandler.closeAcceptRequestDriverListener {
            Log.d(TAG, "stopListeners ACCEPT: $it")
        }

        SocketHandler.closeSendRequestDriverListner {
            Log.d(TAG, "stopListeners SEND: $it")
        }

        SocketHandler.closeDropOffRequestDriverListener {
            Log.d(TAG, "stopListeners DROPOFF: $it")
        }

        SocketHandler.closeRejectRequestDriverListener {
            Log.d(TAG, "stopListeners REJECT: $it")
        }

        SocketHandler.closeCancelRequestDriverListener {
            Log.d(TAG, "stopListeners: $it")
        }
    }
}


/*private fun fetchLocation() {
    if (ActivityCompat.checkSelfPermission(
            requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE)
        return
    }
    val task: Task<Location> = client.lastLocation
    task.addOnSuccessListener(OnSuccessListener<Location> { location ->
        if (location != null) {
            currentLocation = location
            Toast.makeText(getApplicationContext(), currentLocation.latitude.toString() + "" + currentLocation.longitude, Toast.LENGTH_SHORT).show()
        }
    })
}*/
/*
override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
    when (requestCode) {
        REQUEST_CODE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            fetchLocation()
        }
    }
}
}*/
