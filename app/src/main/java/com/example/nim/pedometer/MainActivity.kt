package com.example.nim.pedometer

import android.Manifest
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject
import android.content.pm.PackageManager



class MainActivity : AppCompatActivity()
{
    private lateinit var sensorManager:SensorManager
    private lateinit var accelerometerSensor:Sensor
    private lateinit var accelerometerSensorListener: SensorEventListener

    private val TAG = this::class.java.name
    public var testRunning=false

    private lateinit var accelerometerValues:ArrayList<FloatArray>
    public lateinit var gpsDistanceValues:ArrayList<Double>
    private var initialSteps = 0


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        accelerometerValues = ArrayList<FloatArray>()
        gpsDistanceValues = ArrayList<Double>()

        //This function saves instance for each sensor
        initializeAllSensors()
        //This function saves instance for each sensor
        initializeAllListeners()
        //This function links the sensor with listeners
        linkListeners()
        //This function sets Button Click listeners
        setButtonListeners()


    }

    private fun setButtonListeners()
    {
        SamplingButton.setOnClickListener {
            if (testRunning) //stop pressed
            {
                testRunning=false
                SamplingButton.text="Start"
                val accelerometerJsonArray = JSONArray(accelerometerValues)
                val gpsJsonArray = JSONArray(gpsDistanceValues)
                val url = "http://192.168.0.107:8001"
                val data = JSONObject()
                data.put("accelerometer", accelerometerJsonArray)
                data.put("gps", gpsJsonArray)


                val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url,data,
                        Response.Listener { response ->
                            Log.i(TAG, response.toString())
                            accelerometerValues = ArrayList<FloatArray>()
                        },
                        Response.ErrorListener { error ->
                            Log.i(TAG, error.toString())
                            accelerometerValues = ArrayList<FloatArray>()
                        }
                )

                val queue = Volley.newRequestQueue(this)
                queue.add(jsonObjectRequest)

            }
            else
            {
                testRunning=true
                SamplingButton.text="Stop"
            }

        }
    }

    private fun linkListeners()
    {
        sensorManager.registerListener(accelerometerSensorListener, accelerometerSensor,10000)
    }



    private fun initializeAllSensors()
    {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometerSensor= sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
    }

    private lateinit var gpsSensorListener: MyLocationListener


    private fun initializeAllListeners()
    {
        accelerometerSensorListener = object : SensorEventListener
        {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int)
            {
            }

            override fun onSensorChanged(event: SensorEvent?)
            {
                if (testRunning)
                {
                    accelerometerValues.add(event!!.values.clone())
                    if (initialSteps==0)
                    {
                        initialSteps = event.values[0].toInt()
                    }
                    else
                    {
                        initialSteps+=1
                        stepCountView.text = initialSteps.toString()
                    }
                }
            }

        }


        val permissions: Array<String> = Array(size = 1) {Manifest.permission.ACCESS_FINE_LOCATION}
        ActivityCompat.requestPermissions(this, permissions, 1)
        val locationManager:LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val minimumTimeBetweenUpdates = 0L
        val minimumDistanceBetweenUpdates = 0.0f
        gpsSensorListener = MyLocationListener(this)

        val permission = "android.permission.ACCESS_FINE_LOCATION"
        val res = this.checkCallingOrSelfPermission(permission)
        if ( res == PackageManager.PERMISSION_GRANTED)
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minimumTimeBetweenUpdates, minimumDistanceBetweenUpdates, gpsSensorListener)
            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            gpsSensorListener.initialLocation = location
        }

    }


}
