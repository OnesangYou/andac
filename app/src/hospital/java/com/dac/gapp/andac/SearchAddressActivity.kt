package com.dac.gapp.andac

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.dac.gapp.andac.base.BaseActivity
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment
import com.google.android.gms.location.places.ui.PlaceSelectionListener
import android.content.Intent



class SearchAddressActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_address)

        val autocompleteFragment = fragmentManager.findFragmentById(R.id.place_autocomplete_fragment) as PlaceAutocompleteFragment

        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                Log.i(localClassName, "Place : $place")

                Intent().let{
                    it.apply{
                        putExtra("name", place.name)
                        putExtra("latLng", place.latLng)
                        putExtra("address", place.address)
                        putExtra("phoneNumber", place.phoneNumber)
                    }

                    setResult(Activity.RESULT_OK, it)
                }
                finish()
            }
            override fun onError(status: Status) {
                Log.i(localClassName, "An error occurred: $status")
            }
        })
    }
}
