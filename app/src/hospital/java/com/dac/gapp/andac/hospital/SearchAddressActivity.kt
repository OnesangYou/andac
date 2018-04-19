package com.dac.gapp.andac.hospital

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.dac.gapp.andac.R
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment
import com.google.android.gms.location.places.ui.PlaceSelectionListener

class SearchAddressActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_address)

        val autocompleteFragment = fragmentManager.findFragmentById(R.id.place_autocomplete_fragment) as PlaceAutocompleteFragment

        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                Log.i(localClassName, "Place: " + place.name)

                ("latLng: " + place.latLng).let{
                    Toast.makeText(this@SearchAddressActivity, it, Toast.LENGTH_SHORT).show()
                    Log.i(localClassName, it)
                }
            }

            override fun onError(status: Status) {
                Log.i(localClassName, "An error occurred: $status")
            }
        })
    }
}
