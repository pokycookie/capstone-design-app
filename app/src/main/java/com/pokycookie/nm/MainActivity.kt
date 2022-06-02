package com.pokycookie.nm

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.pokycookie.nm.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val receiver = BluetoothReceiver(this)

    private var bluetoothAdapter: BluetoothAdapter? = null

    lateinit var permissionLauncher: ActivityResultLauncher<String>
    lateinit var bluetoothConnectLauncher: ActivityResultLauncher<String>
    lateinit var bluetoothScanLauncher: ActivityResultLauncher<String>
    lateinit var intentLauncher: ActivityResultLauncher<Intent>

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        val pairedDevice: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
        Log.d("debug", "PAIRED: $pairedDevice")
        pairedDevice?.forEach { device ->
            val deviceName = device.name
            val deviceMAC = device.address
        }

        val filter = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        }
        registerReceiver(receiver, filter)

        if (bluetoothAdapter !== null) {
            binding.bluetoothBtn.text = if(!bluetoothAdapter?.isEnabled!!) "ON" else "OFF"
            if(bluetoothAdapter?.isEnabled == false) {
                binding.textView.text = "Bluetooth OFF"
                binding.bluetoothScanBtn.isVisible = false;
            } else {
                binding.textView.text = "Bluetooth ON"
                binding.bluetoothScanBtn.isVisible = true;
            }
        }

        // Binding
        binding.bluetoothBtn.setOnClickListener {
            bluetoothConnectLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT)
        }
        binding.bluetoothScanBtn.setOnClickListener {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            permissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
            bluetoothScanLauncher.launch(Manifest.permission.BLUETOOTH_SCAN)
        }

        // Launcher
        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { }
        bluetoothScanLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                Log.d("debug", "SCAN: ${ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED}")
                Log.d("debug", "FINE_LOCATION: ${ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED}")
                Log.d("debug", "COARSE_LOCATION: ${ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED}")
                if (isGranted && ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
                    Log.d("debug","discovery: ${bluetoothAdapter?.startDiscovery()}")
                } else {
                    Log.d("permission", "BLUETOOTH_SCAN: false")
                }
            }
        bluetoothConnectLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    Log.d("permission", "BLUETOOTH_CONNECT: ${ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED}")
                    bluetoothSwitch()
                } else {
                    Log.d("permission", "BLUETOOTH_CONNECT: ${ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED}")
                    finish()
                }
            }
        intentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    binding.textView.text = "Bluetooth ON"
                    binding.bluetoothBtn.text = "OFF"
                    binding.bluetoothScanBtn.isVisible = true;
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
            bluetoothAdapter?.cancelDiscovery()
        }
        unregisterReceiver(receiver)
    }

//    class Receiver: BroadcastReceiver() {
//
//        override fun onReceive(context: Context, intent: Intent) {
//            when(intent.action) {
//                BluetoothDevice.ACTION_FOUND -> {
//                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
//                        return
//                    }
//                    val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
//                    val deviceName = device?.name
//                    val deviceMAC = device?.address
//                }
//            }
//        }
//    }

    private fun bluetoothSwitch() {
        if (bluetoothAdapter == null) {
            Log.d("bluetooth", "Device doesn't support Bluetooth")
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                if (bluetoothAdapter?.isEnabled!!) {
                    bluetoothAdapter?.disable()
                    binding.textView.text = "Bluetooth OFF"
                    binding.bluetoothBtn.text = "ON"
                    binding.bluetoothScanBtn.isVisible = false;
                } else {
                    val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    intentLauncher.launch(enableIntent)
                }
            } else {
                Log.d("permission","There's no Permission")
            }
        }
    }
}