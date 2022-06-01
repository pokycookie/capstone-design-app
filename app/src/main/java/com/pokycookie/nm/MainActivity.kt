package com.pokycookie.nm

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.pokycookie.nm.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private var bluetoothAdapter: BluetoothAdapter? = null
    lateinit var activityResult: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        activityResult =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    init()
                } else {
                    finish()
                }
            }
    }

    private fun init() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        if (bluetoothAdapter !== null) {
            binding.bluetoothBtn.isChecked = !bluetoothAdapter?.isEnabled!!
        }

        binding.bluetoothBtn.setOnCheckedChangeListener { buttonView, isChecked ->
            bluetoothSwitch()
        }
    }

    private fun bluetoothSwitch() {
        if (bluetoothAdapter == null) {
            Log.d("bluetooth", "Device doesn't support Bluetooth")
        } else {
            if (bluetoothAdapter?.isEnabled!! &&
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                bluetoothAdapter?.disable()
            } else {
                val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableIntent, 1)
            }
        }
    }
}