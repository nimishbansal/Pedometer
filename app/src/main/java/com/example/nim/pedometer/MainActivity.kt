package com.example.nim.pedometer

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity()
{
    private lateinit var sensorManager:SensorManager
    private lateinit var accelerometerSensor:Sensor
    private lateinit var accelerometerSensorListener: SensorEventListener

    private val TAG = this::class.java.name
    private var testRunning=false

    private lateinit var accelerometerValues:ArrayList<FloatArray>


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
            if (testRunning)
            {
                testRunning=false
                SamplingButton.text="Start"
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
        accelerometerSensor= sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
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
                    accelerometerValues.add(event!!.values)
                }
            }

        }


    }


}
