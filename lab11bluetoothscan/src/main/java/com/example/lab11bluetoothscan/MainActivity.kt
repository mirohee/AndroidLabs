package com.example.lab11bluetoothscan

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData


class MainActivity : ComponentActivity() {
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private val _devices = MutableLiveData<List<BluetoothDeviceData>>()
    val devices: LiveData<List<BluetoothDeviceData>> = _devices

    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private var scanning: Boolean = false
    private val scanResults = mutableListOf<BluetoothDeviceData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allPermissionsGranted = permissions.values.all { it }
            startScan(allPermissionsGranted)
        }

        checkAndRequestPermissions()

        setContent {
            MainScreen(devices) { startScan(true) }
        }
    }

    private fun checkAndRequestPermissions() {
        val permissions = getRequiredPermissions()
        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            startScan(true)
        }
    }

    private fun getRequiredPermissions(): List<String> {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            listOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        } else {
            listOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }
    }

    private fun startScan(permissionGranted: Boolean) {
        Log.d("BluetoothScan", "Start Scan: permissionGranted = $permissionGranted")
        if (scanning || !permissionGranted) return

        scanning = true
        scanResults.clear()
        val scanner = bluetoothAdapter?.bluetoothLeScanner

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request the missing permissions
            checkAndRequestPermissions()
            return
        }

        val scanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                super.onScanResult(callbackType, result)

                try {
                    val device = result.device
                    val deviceInfo = BluetoothDeviceData(
                        device.name ?: "Unknown",
                        device.address,
                        result.rssi,
                        result.isConnectable
                    )
                    scanResults.add(deviceInfo)
                    _devices.postValue(scanResults)
                } catch (e: SecurityException) {
                    e.printStackTrace()
                    // Handle the exception as needed
                }
            }

            override fun onScanFailed(errorCode: Int) {
                super.onScanFailed(errorCode)
                scanning = false
            }
        }
        Log.d("BluetoothScan", "Starting BLE scan...")
        scanner?.startScan(scanCallback)

        // Stop scanning after 5 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            scanner?.stopScan(scanCallback)
            scanning = false
        }, 5000)
    }
}

@Composable
fun MainScreen(devices: LiveData<List<BluetoothDeviceData>>, onStartScan: () -> Unit) {
    val deviceList by devices.observeAsState(emptyList())

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Button(onClick = { onStartScan() }) {
            Text("Start Scanning")
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(deviceList) { device ->
                Text(
                    text = "${device.name} - ${device.macAddress} - RSSI: ${device.rssi}",
                    color = if (device.isConnectable) Color.Blue else Color.Gray
                )
            }
        }
    }
}