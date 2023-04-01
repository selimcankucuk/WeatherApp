package com.example.weatherapp.model.view
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.viewmodel.MainViewModel
import com.google.android.material.internal.ViewUtils.hideKeyboard

class MainActivity : AppCompatActivity() {

    private var binding : ActivityMainBinding? = null
    private lateinit var viewModel : MainViewModel
    private lateinit var GET : SharedPreferences
    private lateinit var SET : SharedPreferences.Editor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        GET = getSharedPreferences(packageName, MODE_PRIVATE)
        SET = GET.edit()

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        var cName = GET.getString("cityName","ankara")
        binding?.edtCityName?.setText(cName)

        viewModel.refreshData(cName!!)
        getLiveData()

        binding?.swipeRefreshLayout!!.setOnRefreshListener {
            binding?.llData?.visibility = View.GONE
            binding?.tvError?.visibility = View.GONE
            binding?.pbLoading?.visibility = View.GONE

            var cityName = GET.getString("cityName",cName)
            binding?.edtCityName?.setText(cName)
            viewModel.refreshData(cityName!!)
            binding?.swipeRefreshLayout?.isRefreshing = false
        }
        binding?.imgSearchCity?.setOnClickListener {
            val cityName = binding?.edtCityName?.text.toString()
            SET.putString("cityName",cityName)
            SET.apply()
            viewModel.refreshData(cityName)
            getLiveData()
            it.hideKeyboard()
        }
    }


    private fun getLiveData() {
        viewModel.weather_data.observe(this, Observer { data ->
            data?.let {
                binding?.llData?.visibility = View.VISIBLE
                binding?.pbLoading?.visibility = View.GONE
                binding?.tvDegree?.text = data.main.temp.toInt().toString() + "°C"
                binding?.tvDescription?.text = data.weather.get(0).description
                binding?.tvCityCode?.text = data.sys.country
                binding?.tvCityName?.text = data.name
                binding?.tvFeelsLike?.text = data.main.feelsLike.toInt().toString()
                binding?.tvHumidity?.text = data.main.humidity.toString() + "%"
                binding?.tvWindSpeed?.text = data.wind.speed.toString()
                binding?.tvVisibility?.text = data.visibility.toString()
                binding?.tvLat?.text = data.coord.lat.toString()
                binding?.tvLon?.text = data.coord.lon.toString()



                Glide.with(this)
                    .load("https://openweathermap.org/img/wn/" + data.weather.get(0).icon + "@2x.png")
                    .into(binding?.imgWeatherPictures!!)
            }
        })
        viewModel.weather_load.observe(this, Observer {load ->
            load?.let {
                if (it){
                    binding?.pbLoading?.visibility = View.VISIBLE
                    binding?.tvError?.visibility = View.GONE
                    binding?.llData?.visibility = View.GONE
                }else{
                    binding?.pbLoading?.visibility = View.GONE
                }
            }
        })
        viewModel.weather_error.observe(this, Observer {error->
            error?.let {
                if (it){
                    binding?.tvError?.visibility = View.VISIBLE
                    binding?.pbLoading?.visibility = View.GONE
                    binding?.llData?.visibility = View.GONE
                }else{
                    binding?.tvError?.visibility = View.GONE

                }
            }
        })



    }


    private fun View.hideKeyboard() {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }


}