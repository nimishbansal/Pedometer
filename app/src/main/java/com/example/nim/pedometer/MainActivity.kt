package com.example.nim.pedometer

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity()
{
    private lateinit var sensorManager:SensorManager
    private lateinit var accelerometerSensor:Sensor
    private lateinit var accelerometerSensorListener: SensorEventListener

    private val TAG = this::class.java.name
    private var testRunning=false

    private lateinit var accelerometerValues:ArrayList<FloatArray>
    private var initialSteps = 0


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        accelerometerValues = ArrayList<FloatArray>()

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
                val jsonArray = JSONArray(accelerometerValues)
                val url = "http://192.168.0.107:8001"
                val data = JSONObject()
                data.put("hello", jsonArray)

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


    }


}
