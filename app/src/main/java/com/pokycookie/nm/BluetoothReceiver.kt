package com.pokycookie.nm

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat

class BluetoothReceiver(context: Context): BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when(intent.action) {
            BluetoothDevice.ACTION_FOUND -> {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                    return
                }
                Log.d("debug", "receiver")
                val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                val deviceName = device?.name
                val deviceMAC = device?.address
            }
            BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                Log.d("debug", "searching...")
            }
            BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                Log.d("debug", "search finished")
            }
        }
    }
}