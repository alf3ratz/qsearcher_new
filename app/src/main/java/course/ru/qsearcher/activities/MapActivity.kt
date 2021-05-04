package course.ru.qsearcher.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.media.audiofx.Equalizer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.snackbar.Snackbar
import course.ru.qsearcher.BuildConfig
import course.ru.qsearcher.R
import course.ru.qsearcher.databinding.ActivityMapBinding
import kotlinx.android.synthetic.main.activity_map.*


class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private val REQUEST_LOCATION_CODE: Int = 234
    private val SETTINGS_CODE: Int = 123

    private lateinit var activityMapBinding: ActivityMapBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var settingsClient: SettingsClient // Доступ к настройкам
    private lateinit var locationRequest: LocationRequest // Сохранение данных запроса
    private lateinit var locationSettingsRequest: LocationSettingsRequest // Определние настроек девайса пользователя
    private lateinit var locationCallback: LocationCallback // События определения местоположения
    private var location: Location? = null// Широта и долгота пользователя

    private lateinit var map: GoogleMap // Карта

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMapBinding = DataBindingUtil.setContentView(this, R.layout.activity_map)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        settingsClient = LocationServices.getSettingsClient(this)
        makeLocationRequest()
        makeLocationCallback()
        makeLocationSettings()
        startLocationUpdates()
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.eventsMap) as SupportMapFragment
        mapFragment.getMapAsync(this)
        setNavigation()
    }

    override fun onResume() {
        super.onResume()
        if (checkLocationPermission()) startLocationUpdates()
        else locationPermission()
    }


    private fun locationPermission() {
        val provide = ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (provide) {
            Snackbar.make(
                findViewById(android.R.id.content),
                "Подтверждение геолокации нужно для корректной работы приложения",
                Snackbar.LENGTH_INDEFINITE
            ).setAction("OK", View.OnClickListener {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_CODE
                )
            }).show()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_CODE
            )
        }
    }

    private fun checkLocationPermission(): Boolean {
        val state =
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        return state == PackageManager.PERMISSION_GRANTED
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            SETTINGS_CODE -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        Log.i("MapActivity", "Подтвердил использование местоположения")
                        startLocationUpdates()
                    }
                    Activity.RESULT_CANCELED -> {
                        Log.i("MapActivity", "Отклонил использование местоположения")
                    }
                }

            }
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        ).addOnFailureListener {
            val statusCode: Int = (it as ApiException).statusCode
            when (statusCode) {
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                    try {
                        val ex: ResolvableApiException = it as ResolvableApiException
                        ex.startResolutionForResult(this, SETTINGS_CODE)
                    } catch (e: IntentSender.SendIntentException) {
                        Toast.makeText(
                            applicationContext,
                            "Проблемы с подтверждением доступа к геолокации",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    Toast.makeText(
                        applicationContext,
                        "Подтвердите настройки геолокации",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun makeLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    private fun makeLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                Log.i("map", "сделал локейшн")
                location = p0.lastLocation
                Log.i("map", location?.latitude!!.toString())

            }
        }
    }

    private fun makeLocationSettings() {
        val settingsBuilder: LocationSettingsRequest.Builder = LocationSettingsRequest.Builder()
        settingsBuilder.addLocationRequest(locationRequest)
        locationSettingsRequest = settingsBuilder.build()
    }

    /**
     * Метод, который иннициализирует нижнюю навигацию и выделяет нужную иконку на ней.
     */
    private fun setNavigation() {
        val menuView = activityMapBinding.bottomNavigation
            .getChildAt(0) as BottomNavigationMenuView
        for (i in 0 until menuView.childCount) {
            val iconView =
                menuView.getChildAt(i).findViewById<View>(R.id.icon)
            val layoutParams = iconView.layoutParams
            val displayMetrics = resources.displayMetrics
            layoutParams.height =
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 26f, displayMetrics).toInt()
            layoutParams.width =
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 26f, displayMetrics).toInt()
            iconView.layoutParams = layoutParams
        }
        activityMapBinding.bottomNavigation.selectedItemId = R.id.map
        activityMapBinding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.favorites -> {
                    startActivity(
                        Intent(
                            applicationContext,
                            FavoritesActivity::class.java
                        )
                    )
                    overridePendingTransition(0, 0)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.chat -> {
                    startActivity(Intent(applicationContext, UsersActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.settings -> {
                    startActivity(Intent(applicationContext, SettingsActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@setOnNavigationItemSelectedListener true
                }
            }
            false
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        map = googleMap!!
        try {
            if (location == null) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        location = task.result
                        if (location != null) {
                            val position = LatLng(
                                location!!.latitude,
                                location!!.longitude
                            )
                            map.addMarker(
                                MarkerOptions()
                                    .position(position)
                                    .title("Выше местоположение")
                            )
                            map.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    position, 13.0F
                                )
                            )
                        }
                    } else {
                        Log.d("map", "Current location is null. Using defaults.")
                        Log.e("map", "Exception: %s", task.exception)
//                        map?.moveCamera(CameraUpdateFactory
//                            .newLatLngZoom(defaultLocation, DEFAULT_ZOOM.toFloat()))
                        map?.uiSettings?.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
//        map.apply {
//            if(location!=null){
//                val position = LatLng(location?.latitude!!, location?.longitude!!)
//                addMarker(
//                    MarkerOptions()
//                        .position(position)
//                        .title("Выше местоположение")
//                )
//                Log.i("map","поставил")
//                map.moveCamera(CameraUpdateFactory.newLatLng(position))
//            }else{
//                Toast.makeText(applicationContext,"Проблемы с получаением геопозиции",Toast.LENGTH_LONG).show()
//                Log.i("map","оно какого-то хрена налл")
//            }
//        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_CODE) {
            if (grantResults.isEmpty()) {
                Log.i("orRequestPermissions", "request was cancelled")
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates()
            } else {
                Snackbar.make(
                    findViewById(android.R.id.content),
                    "Включите геолокацию в настройках",
                    Snackbar.LENGTH_INDEFINITE
                ).setAction("Найстроки", View.OnClickListener {
                    val intent = Intent().setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri: Uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                    intent.data = uri
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }).show()
            }
        }
    }
}