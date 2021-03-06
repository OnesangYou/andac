package com.dac.gapp.andac.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.IntentSender
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.algolia.instantsearch.helpers.Searcher
import com.algolia.search.saas.AbstractQuery
import com.algolia.search.saas.Query
import com.dac.gapp.andac.HospitalActivity
import com.dac.gapp.andac.R
import com.dac.gapp.andac.base.BaseFragment
import com.dac.gapp.andac.databinding.FragmentSearchHospitalForMapBinding
import com.dac.gapp.andac.enums.Algolia
import com.dac.gapp.andac.extension.loadImageAny
import com.dac.gapp.andac.model.firebase.HospitalInfo
import com.dac.gapp.andac.util.Common
import com.dac.gapp.andac.util.JsonUtil
import com.dac.gapp.andac.util.MyToast
import com.dac.gapp.andac.util.UiUtil
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import io.reactivex.disposables.Disposable
import timber.log.Timber
import java.lang.Exception


class SearchHospitalFragmentForMap : BaseFragment() {

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
        Timber.d("$currentLatitude, $currentLongitude")

        googleMap?.moveCamera(CameraUpdateFactory.newLatLng(LatLng(currentLatitude, currentLongitude)))
        googleMap?.animateCamera(CameraUpdateFactory.zoomTo(10f))
    }

    private var prevMarker: Marker? = null

    private lateinit var binding: FragmentSearchHospitalForMapBinding
    @SuppressLint("MissingPermission")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflate(inflater, R.layout.fragment_search_hospital_for_map, container, false)
        binding = getBinding()
        mapView = view?.findViewById<View>(R.id.map) as MapView
        mapView?.getMapAsync { map ->
            map.isMyLocationEnabled = true
            googleMap = map
            moveCamera(LatLng(37.56, 126.97))
            googleMap?.setOnMarkerClickListener { marker ->
                prevMarker?.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.hospital_on))
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.hospital_clickpin))
                val hospitalInfo = hospitals[marker.tag.toString()]
                hospitalInfo?.let { info ->
                    binding.layoutHospitalInfo?.let {
                        UiUtil.visibleOrGone(true, it.root)
                        it.imgviewThumbnail.loadImageAny(info.run { if (profilePicUrl.isNotEmpty()) profilePicUrl else if (approval) R.drawable.hospital_profile_default_approval else R.drawable.hospital_profile_default_not_approval })
                        it.txtviewTitle.text = info.name
                        it.txtviewAddress.text = info.address2
                        it.ratingBar.rating = info.rateAvg
                        it.heartCount.text = info.likeCount.toString()
                        it.root.setOnClickListener {
                            startActivity(HospitalActivity.createIntent(thisActivity(), hospitalInfo))
                        }
                    }
                }
                prevMarker = marker
                true
            }
//            showAllHospitals()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        UiUtil.visibleOrGone(false, binding.layoutHospitalInfo?.root)
        setupCurrentLocation()
        setupEventsOnCreate()
    }

    private var mDisposable: Disposable? = null

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
                            Timber.d("$currentLatitude, $currentLongitude")
//                            moveCamera(LatLng(currentLatitude, currentLongitude))
                            mDisposable = SearchHospitalFragment.observeCurrentPosition()
                                    .subscribe { currentPosition ->
                                        if (currentPosition == 0) {
                                            searchHospital(3000)
                                        }
                                    }
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
                        Timber.e("Location services connection failed with code %s", connectionResult.errorCode)
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
        binding.btnClearMarkers.setOnClickListener {
            googleMap?.clear()
        }
        binding.btnShowAllHospitals.setOnClickListener {
            showAllHospitals()
        }
        binding.btnSetRadius.setOnClickListener {
            try {
                val aroundRadius = Integer.parseInt(binding.etAddress.text.toString())
                searchHospital(aroundRadius)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "error: ${e.message}", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
        binding.btnGetLocation.setOnClickListener {
            val address = Common.getFromLocationName(context, binding.etAddress.text.toString())
            if (address != null) {
                val latLng = LatLng(address.latitude, address.longitude)
                val markerOptions = MarkerOptions()
                markerOptions.position(latLng)
                markerOptions.title(binding.etAddress.text.toString())
                googleMap?.addMarker(markerOptions)
                googleMap?.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                googleMap?.animateCamera(CameraUpdateFactory.zoomTo(10f))
            } else {
                Toast.makeText(context, "주소가 올바르지 않습니다?", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun searchHospital(aroundRadius: Int) {
//        MyToast.showShort(requireContext(), "($currentLatitude, $currentLongitude), ${aroundRadius / 1000}km")
        val query = Query()
                .setAroundLatLng(AbstractQuery.LatLng(currentLatitude, currentLongitude))
                .setAroundRadius(aroundRadius)
//                .setHitsPerPage(1000) // default 20, maximum 1000
        val searcher = Searcher.create(Algolia.APP_ID.value, Algolia.SEARCH_API_KEY.value, Algolia.INDEX_NAME_HOSPITAL.value)
        context?.showProgressDialog()
        searcher.searchable.searchAsync(query) { jsonObject, algoliaException ->
            jsonObject?.let { jo ->
                val latLng = JsonUtil.getString(jo, "params").split("&")[0].split("=")[1].split("%2C")
//            Timber.d("jsonObject: ${jsonObject.toString(4)}")

                val hits = JsonUtil.getArray(jo, Algolia.HITS.value)
                if (hits.length() > 0) {
//                Timber.d("jsonObject: ${jsonObject.getJSONArray(Algolia.HITS.value).getJSONObject(0).getString(Algolia.NAME.value)}")
                    var i = 0
                    while (i < hits.length()) {
                        val jo = hits.getJSONObject(i)
//                    Timber.d("jsonObject[$i]: ${jo.toString(4)}")
                        val hospitalInfo = HospitalInfo.create(jo)
                        hospitals[hospitalInfo.objectID] = hospitalInfo
                        addMarker(hospitalInfo.objectID, hospitalInfo.getLatLng())
                        i++
                    }

                    Timber.d("currentLatitude, currentLatitude $currentLatitude, $currentLongitude")
                    Timber.d("lat, lng: $latLng")
                    Timber.d("algoliaException: $algoliaException")
//                MyToast.showShort(requireContext(), "근처 병원 ${hits.length()}개를 찾았습니다")
                } else {
                    val newAroundRadius = aroundRadius + 1000
                    searchHospital(newAroundRadius)
//                MyToast.showShort(requireContext(), "근처에 병원이 없습니다. 다음 반경으로 다시 검색합니다. ${newAroundRadius / 1000f} km")
                }
                moveCamera(LatLng(currentLatitude, currentLongitude))
                context?.hideProgressDialog()
            }
        }
    }

    private fun thisActivity(): Activity {
        return requireActivity()
    }

    private fun addMarker(id: String, latLng: LatLng) {
        context?.let {
            it.runOnUiThread {
                googleMap?.let {
                    it.addMarker(MarkerOptions().apply {
                        icon(BitmapDescriptorFactory.fromResource(R.drawable.hospital_on))
                        position(latLng)
                    }).apply {
                        tag = id
                    }
                }
            }
        }
    }

    private fun moveCamera(latLng: LatLng) {
        googleMap?.apply {
            animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.0f), 1, object : GoogleMap.CancelableCallback {
                override fun onFinish() {
                    Timber.d("animateCamera onFinish()")
                }

                override fun onCancel() {
                    try {
                        Timber.d("animateCamera onCancel()")
                        animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.0f), 1, this)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
        }
    }

    private fun showAllHospitals() {
        context?.getHospitals()
                ?.get()
                ?.addOnCompleteListener {
                    Thread {
                        if (it.isSuccessful) {
                            for (document in it.result) {
                                //                            Timber.d(document.id + " => " + document.data)
                                hospitals[document.id] = document.toObject(HospitalInfo::class.java)
                                val hospitalInfo = hospitals[document.id]
                                hospitalInfo?.let {
                                    addMarker(document.id, hospitalInfo.getLatLng())
                                }
                                //                                Timber.d("${hospitalInfo.name} ${hospitalInfo._geoloc}")
                            }
                            context?.runOnUiThread {
                                MyToast.showShort(requireContext(), "근처 병원 ${it.result.size()}개를 찾았습니다?")
                            }
                        } else {
                            Timber.w("Error getting documents. ${it.exception}")
                        }
                    }.start()
                }
    }

    // activity 가 아닌 fragment 에서 google map 을 사용할 때 lifecycle 마다 정의 해줘야 하는 것 같음...
    // 안해주면 fragment 에서 google map 안뜸?

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mapView?.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
        mGoogleApiClient?.connect()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
        //Disconnect from API onPause()
        mGoogleApiClient?.let {
            if (it.isConnected) {
                LocationServices.FusedLocationApi.removeLocationUpdates(it, mLocationListener)
                it.disconnect()
            }
        }
        mDisposable?.dispose()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }
}