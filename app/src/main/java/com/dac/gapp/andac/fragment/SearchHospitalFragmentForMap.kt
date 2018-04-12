package com.dac.gapp.andac.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.dac.gapp.andac.R
import com.dac.gapp.andac.util.Common
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_search_hospital_for_map.*

class SearchHospitalFragmentForMap : Fragment(), OnMapReadyCallback {

    private var mapView: MapView? = null

    private var googleMap: GoogleMap? = null

    var title: String = ""

    // static method
    companion object {
        fun create(title: String): SearchHospitalFragmentForMap {
            val f = SearchHospitalFragmentForMap()
            f.title = title
            val args = Bundle()
            f.arguments = args
            return f
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_search_hospital_for_map, container, false)
        mapView = view.findViewById<View>(R.id.map) as MapView
        mapView!!.getMapAsync(this)
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupEventsOnCreate()
    }

    override fun onStart() {
        super.onStart()
        mapView!!.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView!!.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        mapView!!.onSaveInstanceState(outState)
    }

    override fun onResume() {
        super.onResume()
        mapView!!.onPause()
    }

    override fun onPause() {
        super.onPause()
        mapView!!.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView!!.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView!!.onDestroy()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (mapView != null) {
            mapView!!.onCreate(savedInstanceState)
        }
    }

    private fun setupEventsOnCreate() {
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

    override fun onMapReady(map: GoogleMap?) {
        val seoul = LatLng(37.56, 126.97)
        val markerOptions = MarkerOptions()
        markerOptions.position(seoul)
        markerOptions.title("서울")
        markerOptions.snippet("한국의 수도")
        map!!.addMarker(markerOptions)

        map.moveCamera(CameraUpdateFactory.newLatLng(seoul))
        map.animateCamera(CameraUpdateFactory.zoomTo(10f))

        googleMap = map
    }
}