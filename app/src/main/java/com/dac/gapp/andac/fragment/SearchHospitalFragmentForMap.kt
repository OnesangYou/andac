package com.dac.gapp.andac.fragment

import android.annotation.SuppressLint
import android.content.IntentSender
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.algolia.instantsearch.helpers.Searcher
import com.algolia.search.saas.AbstractQuery
import com.algolia.search.saas.Query
import com.dac.gapp.andac.base.BaseActivity
import com.dac.gapp.andac.R
import com.dac.gapp.andac.model.Algolia
import com.dac.gapp.andac.model.firebase.HospitalInfo
import com.dac.gapp.andac.util.Common
import com.dac.gapp.andac.util.MyToast
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_search_hospital_for_map.*
import timber.log.Timber
import java.lang.Exception


class SearchHospitalFragmentForMap : Fragment() {

    // static method
    companion object {
        const val CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000

        fun create(title: String): SearchHospitalFragmentForMap {
            val f = SearchHospitalFragmentForMap()
            f.title = title
            val args = Bundle()
            f.arguments = args
            return f
        }
    }

    private var mapView: MapView? = null
    private var googleMap: GoogleMap? = null

    private var currentLatitude: Double = 0.toDouble()
    private var currentLongitude: Double = 0.toDouble()
    private var mGoogleApiClient: GoogleApiClient? = null

    private var mLocationRequest: LocationRequest? = null

    private var hospitals: HashMap<String, HospitalInfo> = HashMap()

    var title: String = ""

    val mLocationListener = LocationListener {
        currentLatitude = it.latitude
        currentLongitude = it.longitude

        googleMap!!.moveCamera(CameraUpdateFactory.newLatLng(LatLng(currentLatitude, currentLongitude)))
        googleMap!!.animateCamera(CameraUpdateFactory.zoomTo(10f))
        Toast.makeText(requireContext(), "current LatLng($currentLatitude, $currentLongitude)", Toast.LENGTH_LONG).show()
    }

    @SuppressLint("MissingPermission")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_search_hospital_for_map, container, false)
        mapView = view.findViewById<View>(R.id.map) as MapView
        mapView!!.getMapAsync({
            it.isMyLocationEnabled = true
            googleMap = it
            showAllHospitals()
        })
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCurrentLocation()
        setupEventsOnCreate()
    }

    @SuppressLint("MissingPermission")
    private fun setupCurrentLocation() {
        mGoogleApiClient = GoogleApiClient.Builder(requireContext())
                // The next two lines tell the new client that “this” current class will handle connection stuff
                .addConnectionCallbacks(object : GoogleApiClient.ConnectionCallbacks {

                    override fun onConnected(p0: Bundle?) {
                        val location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)

                        if (location == null) {
                            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, mLocationListener)
                        } else {
                            //If everything went fine lets get latitude and longitude
                            currentLatitude = location.latitude
                            currentLongitude = location.longitude
                            moveCamera(LatLng(currentLatitude, currentLongitude))
                        }
                    }

                    override fun onConnectionSuspended(p0: Int) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                })
                .addOnConnectionFailedListener { connectionResult ->
                    /*
                            * Google Play services can resolve some errors it detects.
                            * If the error has a resolution, try sending an Intent to
                            * start a Google Play services activity that can resolve
                            * error.
                            */
                    if (connectionResult.hasResolution()) {
                        try {
                            // Start an Activity that tries to resolve the error
                            connectionResult.startResolutionForResult(requireActivity(), CONNECTION_FAILURE_RESOLUTION_REQUEST)
                            /*
                                                     * Thrown if Google Play services canceled the original
                                                     * PendingIntent
                                                     */
                        } catch (e: IntentSender.SendIntentException) {
                            // Log the error
                            e.printStackTrace()
                        }

                    } else {
                        /*
                                                 * If no resolution is available, display a dialog to the
                                                 * user with the error.
                                                 */
                        Log.e("Error", "Location services connection failed with code " + connectionResult.getErrorCode())
                    }
                }
                //fourth line adds the LocationServices API endpoint from GooglePlayServices
                .addApi(LocationServices.API)
                .build()

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval((10 * 1000).toLong())        // 10 seconds, in milliseconds
                .setFastestInterval((1 * 1000).toLong()) // 1 second, in milliseconds
    }

    private fun setupEventsOnCreate() {
        btnClearMarkers.setOnClickListener({
            googleMap!!.clear()
        })
        btnShowAllHospitals.setOnClickListener({
            showAllHospitals()
        })
        btnSetRadius.setOnClickListener({
            try {
                val aroundRadius = Integer.parseInt(etAddress.text.toString())
                searchHospital(aroundRadius)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "error: ${e.message}", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        })
        btnGetLocation.setOnClickListener({
            val address = Common.getFromLocationName(context, etAddress.text.toString())
            if (address != null) {
                val latLng = LatLng(address.latitude, address.longitude)
                val markerOptions = MarkerOptions()
                markerOptions.position(latLng)
                markerOptions.title(etAddress.text.toString())
                googleMap!!.addMarker(markerOptions)
                googleMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                googleMap!!.animateCamera(CameraUpdateFactory.zoomTo(10f))
            } else {
                Toast.makeText(context, "주소가 올바르지 않습니다!!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun searchHospital(aroundRadius: Int) {
        MyToast.show(requireContext(), "($currentLatitude, $currentLongitude), ${aroundRadius / 1000}km")
        val query = Query()
                .setAroundLatLng(AbstractQuery.LatLng(currentLatitude, currentLongitude))
                .setAroundRadius(aroundRadius)
                .setHitsPerPage(1000) // default 20, maximum 1000
        val searcher = Searcher.create(Algolia.APP_ID.value, Algolia.SEARCH_API_KEY.value, Algolia.INDEX_NAME_HOSPITAL.value)
        searcher.searchable.searchAsync(query, { jsonObject, algoliaException ->
            val latLng = jsonObject.getString("params").split("&")[0].split("=")[1].split("%2C")
            Timber.d("jsonObject: ${jsonObject.toString(4)}")

            if (jsonObject.has(Algolia.HITS.value) && jsonObject.getJSONArray(Algolia.HITS.value).length() > 0) {
                Timber.d("jsonObject: ${jsonObject.getJSONArray(Algolia.HITS.value).getJSONObject(0).getString(Algolia.NAME.value)}")
                val hits = jsonObject.getJSONArray(Algolia.HITS.value)
                var i = 0
                while (i < hits.length()) {
                    val jo = hits.getJSONObject(i)
                    Timber.d("jsonObject[$i]: ${jo.toString(4)}")
                    Timber.d("jsonObject[$i]: ${jo.getString(Algolia.NAME.value)}")
                    val geoloc = jo.getJSONObject(Algolia.GEOLOC.value)
                    addMarker(jo.getString(Algolia.NAME.value), LatLng(geoloc.getDouble(Algolia.LAT.value), geoloc.getDouble(Algolia.LNG.value)))
                    i++
                }

                Timber.d("currentLatitude, currentLatitude $currentLatitude, $currentLongitude")
                Timber.d("lat, lng: $latLng")
                Timber.d("algoliaException: $algoliaException")
                MyToast.show(requireContext(), "근처 병원 ${hits.length()}개를 찾았습니다!!")
            } else {
                MyToast.show(requireContext(), "근처 병원이 없습니다!!")
            }
            moveCamera(LatLng(currentLatitude, currentLongitude))
        })
    }

    private fun addMarker(title: String, latLng: LatLng) {
        addMarker(title, "", BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE), latLng)
    }

    private fun addMarker(title: String, snippet: String, icon: BitmapDescriptor, latLng: LatLng) {
        val markerOptions = MarkerOptions()
        markerOptions.title(title)
        markerOptions.snippet(snippet)
        markerOptions.position(latLng)
        markerOptions.icon(icon)
        googleMap!!.addMarker(markerOptions)
    }

    private fun moveCamera(latLng: LatLng) {
        googleMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        googleMap!!.animateCamera(CameraUpdateFactory.zoomTo(14.5f))
    }

    private fun showAllHospitals() {
        (activity as BaseActivity).getHospitals()
                .get()
                .addOnCompleteListener({
                    if (it.isSuccessful) {
                        for (document in it.result) {
//                            Timber.d(document.id + " => " + document.data)
                            hospitals[document.id] = document.toObject(HospitalInfo::class.java)
                            val hospitalInfo = hospitals[document.id]
                            val address = if (hospitalInfo!!.address1 != "") hospitalInfo.address1 else hospitalInfo.address2
                            addMarker(hospitalInfo.name, address, BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE), hospitalInfo.getLatLng())
                            Timber.d("${hospitalInfo.name} ${hospitalInfo._geoloc}")
                        }
                        MyToast.show(requireContext(), "근처 병원 ${it.result.size()}개를 찾았습니다!!")
                    } else {
                        Timber.w("Error getting documents. ${it.exception}")
                    }
                })
    }

    // activity 가 아닌 fragment 에서 google map 을 사용할 때 lifecycle 마다 정의 해줘야 하는 것 같음...
    // 안해주면 fragment 에서 google map 안뜸!!

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (mapView != null) {
            mapView!!.onCreate(savedInstanceState)
        }
    }

    override fun onStart() {
        super.onStart()
        mapView!!.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView!!.onResume()
        mGoogleApiClient!!.connect()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView!!.onSaveInstanceState(outState)
    }

    override fun onPause() {
        super.onPause()
        mapView!!.onPause()
        //Disconnect from API onPause()
        if (mGoogleApiClient!!.isConnected) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, mLocationListener)
            mGoogleApiClient!!.disconnect()
        }
    }

    override fun onStop() {
        super.onStop()
        mapView!!.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView!!.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView!!.onLowMemory()
    }
}