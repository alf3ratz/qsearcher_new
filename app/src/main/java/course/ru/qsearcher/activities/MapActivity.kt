package course.ru.qsearcher.activities

import android.content.Intent
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import course.ru.qsearcher.R
import course.ru.qsearcher.databinding.ActivityMapBinding
import kotlinx.android.synthetic.main.activity_map.*


class MapActivity : AppCompatActivity(), OnMapReadyCallback {


    private lateinit var activityMapBinding: ActivityMapBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var settingsClient: SettingsClient // Доступ к настройкам
    private lateinit var locationRequest: LocationRequest // Сохранение данных запроса
    private lateinit var locationSettingsRequest: LocationSettingsRequest // Определние настроек девайса пользователя
    private lateinit var locationCallback: LocationCallback // События определения местоположения
    private lateinit var location: Location // Широта и долгота пользователя

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMapBinding = DataBindingUtil.setContentView(this, R.layout.activity_map)
        setNavigation()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        settingsClient = LocationServices.getSettingsClient(this)
        makeLocationRequest()
        makeLocationCallback()
        makeLocationSettings()
    }

    private fun makeLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 3000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private fun makeLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                location = p0.lastLocation
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
                // R.id.map -> startActivity(Intent(applicationContext, MapActivity::class.java))
            }
            false
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap?.apply {
            val position = LatLng(location.latitude, location.longitude)
            addMarker(
                MarkerOptions()
                    .position(position)
                    .title("Выше местоположение")
            )
        }
    }
}