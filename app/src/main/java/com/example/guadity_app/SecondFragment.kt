package com.example.guadity_app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class SecondFragment : Fragment() {

    private lateinit var map: GoogleMap
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    companion object {
        const val REQUEST_CODE_LOCATION = 100
    }

    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap
        val coordinates = LatLng(-8.094959521120115, -79.0495648431584)
        val marker = MarkerOptions().position(coordinates).title("Universidad Privada del Norte")
        googleMap.addMarker(marker)
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(coordinates))
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15f), 2000, null)
        enableLocation() // Asegúrate de llamar a enableLocation cuando el mapa esté listo
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_second, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                enableLocation()
            } else {
                Toast.makeText(requireContext(), "Para activar la localización ve a Ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isLocationPermissionGranted() = ContextCompat.checkSelfPermission(
        requireContext(),
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    private fun enableLocation() {
        if (!::map.isInitialized) return
        if (isLocationPermissionGranted()) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            map.isMyLocationEnabled = true
        } else {
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(requireContext(), "Por favor ingresa a Ajustes y activa los permisos", Toast.LENGTH_SHORT).show()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    override fun onResume() {
        super.onResume()
        if (!::map.isInitialized) return
        try {
            if (isLocationPermissionGranted()) {
                enableLocation()
            } else {
                map.isMyLocationEnabled = false
                Toast.makeText(requireContext(), "Para activar la localización ve a Ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
            }
        } catch (e: SecurityException) {
            map.isMyLocationEnabled = false
            Toast.makeText(requireContext(), "Error de permisos de localización", Toast.LENGTH_SHORT).show()
        }
    }
}
