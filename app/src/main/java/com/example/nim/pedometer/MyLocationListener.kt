package com.example.nim.pedometer

import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.util.Log
import kotlin.math.roundToInt

class MyLocationListener(mainActivity: MainActivity) : LocationListener
{
    var initialLocation:Location?=null
    var distanceTravelled=0.toDouble()
    var context=mainActivity


    fun getDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double
    {
        val latA = Math.toRadians(lat1)
        val lonA = Math.toRadians(lon1)
        val latB = Math.toRadians(lat2)
        val lonB = Math.toRadians(lon2)
        val cosAng = Math.cos(latA) * Math.cos(latB) * Math.cos(lonB - lonA) + Math.sin(latA) * Math.sin(latB)
        val ang = Math.acos(cosAng)
        return ang * 6371
    }

    override fun onLocationChanged(location: Location?)
    {
        Log.i("MYTAG", "x:"+ location?.latitude?.toString()+","+"y:"+location?.longitude?.toString())
        if (initialLocation!=null)
        {
            val distance = 1000*getDistance(initialLocation!!.latitude, initialLocation!!.longitude, location!!.latitude, location.longitude)
            initialLocation = Location(location)
            if (context.testRunning)
            {
                distanceTravelled+=distance
                context.gpsDistanceValues.add(distance)
            }

        }

    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?)
    {
        Log.i("MYTAG","status changed to " + status.toString())
    }

    override fun onProviderEnabled(provider: String?)
    {
        Log.i("MYTAG","provider enabled to " + provider.toString())
    }

    override fun onProviderDisabled(provider: String?)
    {
        Log.i("MYTAG","provider disabled to " + provider.toString())
    }

}
