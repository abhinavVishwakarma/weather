package com.example.weatherapp

import WeatherApp
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SearchView
import com.example.weatherapp.R.drawable.colud_background
import com.example.weatherapp.R.drawable.sunny
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//96526b89a05c43afea4707e0a4e7945f
class MainActivity : AppCompatActivity() {
    private val binding :ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
//
//    private val binding = ActivityMainBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        fetchWeatherData("noida")
        searchCity()
    }

    private fun searchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {

                if (query != null) {
                    fetchWeatherData(query)

                }
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return true
            }


        })
    }

    // Api calling
    private fun fetchWeatherData(cityName:String) {
//        TODO("Not yet implemented")

        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)

        // for response
        val response= retrofit.getWeatherData(cityName,"96526b89a05c43afea4707e0a4e7945f","metric")
        response.enqueue(object : Callback<WeatherApp> {
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
              val responseBody = response.body()
                if (response.isSuccessful && responseBody!=null){
                    val temperature = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise
                    val sunSet = responseBody.sys.sunset
                    val seaLevel = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main?:"unknown"
                    val maxTemp = responseBody.main.temp_max
                    val minTemp = responseBody.main.temp_min
                    binding.tempearature.setText("$temperature ℃").toString()
                    binding.weather.setText("$condition").toString()
                    binding.maxTemp.setText("$maxTemp ℃")
                    binding.minTemp.setText("$minTemp ℃")
                    binding.humadity.setText("$humidity %")
                    binding.windSpeed.setText("$windSpeed m/s")
                    binding.sunrise.setText("${time(sunRise.toLong())} ")
                    binding.sunset.setText("${time(sunRise.toLong())} ")
                    binding.sea.setText("$seaLevel hPa")
                    binding.condition.setText(condition)
                    binding.cityName.setText("$cityName")
                    binding.date.setText(date())
                    binding.day.setText(dayName(System.currentTimeMillis()))

                    changeBackground(condition)

                    }
                }


            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {

            }

        })


    }

    private fun changeBackground(condition:String){
        when(condition){

           "Clear Sky","Sunny","Clear" ->{
               binding.root.setBackgroundResource(R.drawable.sunny_background)
               binding.lottieAnimationView.setAnimation(R.raw.sunny)
           }
            "Partly Clouds", "Clouds", "Overcast","Mist","Foggy"->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Light Rain","Drizzle","Moderate Rain" ,"Showers","Heavy Rain" ->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "Light Snow","Moderate Snow","Heavy Snow","blizzard" ->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }
            else->{

                    binding.root.setBackgroundResource(R.drawable.sunny_background)
                    binding.lottieAnimationView.setAnimation(R.raw.sunny)
                }
            }



        binding.lottieAnimationView.playAnimation()
    }

    private fun date():String{
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format(((Date())))

    }

    private fun time(timestamp:Long):String{
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(((Date(timestamp*1000))))
    }

    fun dayName(timestamp:Long):String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(((Date())))
    }
}