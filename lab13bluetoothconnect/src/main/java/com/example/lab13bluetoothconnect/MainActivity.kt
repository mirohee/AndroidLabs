package com.example.lab13bluetoothconnect

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
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
import androidx.compose.foundation.clickable
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
import java.util.UUID


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

    // UUIDs for the Heart Rate Service and Characteristic
    val HEART_RATE_SERVICE_UUID = convertFromInteger(0x180D)
    val HEART_RATE_MEASUREMENT_CHAR_UUID = convertFromInteger(0x2A37)
    val CLIENT_CHARACTERISTIC_CONFIG_UUID = convertFromInteger(0x2902)
    /* Generates 128-bit UUID from the Protocol Indentifier (16-bit number)
     * and the BASE_UUID (00000000-0000-1000-8000-00805F9B34FB)
     */
    private fun convertFromInteger(i: Int): UUID {
        val MSB = 0x0000000000001000L
        val LSB = -0x7fffff7fa064cb05L
        val value = (i and -0x1).toLong()
        return UUID(MSB or (value shl 32), LSB)
    }

    private var isConnected by mutableStateOf(false)
    private var heartRate by mutableStateOf<String?>(null)

    private var bluetoothGatt: BluetoothGatt? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allPermissionsGranted = permissions.values.all { it }
            startScan(allPermissionsGranted)
        }

        checkAndRequestPermissions()

        setContent {
            MainScreen(
                devices,
                onStartScan = { startScan(true) },
                onConnectDevice = { device -> connectToDevice(device) },
                isConnected = isConnected,
                heartRate = heartRate,
            )
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
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }
    }

    private fun startScan(permissionGranted: Boolean) {
        if (scanning || !permissionGranted) return
        if (bluetoothAdapter?.isEnabled != true) {
            Log.d("MainActivity", "Bluetooth is not enabled")
            return
        }

        // Check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            checkAndRequestPermissions()
            return
        }

        // Stop any ongoing scan before starting a new one
        bluetoothAdapter?.bluetoothLeScanner?.stopScan(object : ScanCallback() {})

        scanning = true
        val scanner = bluetoothAdapter?.bluetoothLeScanner

        val scanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                super.onScanResult(callbackType, result)

                try {
                    val device = result.device
                    val deviceInfo = BluetoothDeviceData(
                        device.name ?: "Unknown",
                        device.address,
                        result.rssi,
                        result.isConnectable,
                        device
                    )

                    // Update scan results
                    updateScanResults(deviceInfo)
                } catch (e: SecurityException) {
                    e.printStackTrace()
                }
            }

            override fun onScanFailed(errorCode: Int) {
                super.onScanFailed(errorCode)
                scanning = false  // Reset scanning state on failure
            }
        }

        scanner?.startScan(scanCallback)

        // Stop scanning after 3 seconds and reset scanning state
        Handler(Looper.getMainLooper()).postDelayed({
            scanner?.stopScan(scanCallback)
            scanning = false
        }, 3000)
    }

    private fun updateScanResults(newDevice: BluetoothDeviceData) {
        val existingDeviceIndex = scanResults.indexOfFirst { it.macAddress == newDevice.macAddress }
        if (existingDeviceIndex >= 0) {
            scanResults[existingDeviceIndex] = newDevice
        } else {
            scanResults.add(newDevice)
        }
        _devices.postValue(scanResults.toList())
    }

    private fun connectToDevice(device: BluetoothDevice) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        bluetoothGatt = device.connectGatt(this, false, object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                super.onConnectionStateChange(gatt, status, newState)
                Log.d("Bluetooth", "Connection state changed: status=$status, newState=$newState")
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    isConnected = newState == BluetoothProfile.STATE_CONNECTED
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        Log.d("Bluetooth", "Connected to GATT server.")
                        checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT)
                        gatt.discoverServices()
                    }
                } else {
                    isConnected = false
                    Log.e("Bluetooth", "Connection failed: $status")
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                super.onServicesDiscovered(gatt, status)
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Log.d("Bluetooth", "Services discovered")
                    val heartRateService = gatt.getService(HEART_RATE_SERVICE_UUID)
                    val heartRateCharacteristic = heartRateService?.getCharacteristic(HEART_RATE_MEASUREMENT_CHAR_UUID)

                    heartRateCharacteristic?.let {
                        Log.d("Bluetooth", "Heart Rate characteristic found")
                        checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT)
                        gatt.setCharacteristicNotification(it, true)

                        val descriptor = it.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_UUID)
                        descriptor?.let { descriptor ->
                            descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                            val descriptorWriteSuccess = gatt.writeDescriptor(descriptor)
                            Log.d("Bluetooth", "Descriptor write success: $descriptorWriteSuccess")
                        }
                    } ?: Log.e("Bluetooth", "Heart Rate characteristic not found")
                } else {
                    Log.e("Bluetooth", "Service discovery failed: $status")
                }
            }

            override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
                super.onCharacteristicChanged(gatt, characteristic)
                if (characteristic.uuid == HEART_RATE_MEASUREMENT_CHAR_UUID) {
                    val heartRateValue = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 1)
                    heartRate = heartRateValue.toString()
                    Log.d("Bluetooth", "Heart Rate: $heartRate")
                }
            }
        })
    }

}



@Composable
fun MainScreen(
    devices: LiveData<List<BluetoothDeviceData>>,
    onStartScan: () -> Unit,
    onConnectDevice: (BluetoothDevice) -> Unit,
    isConnected: Boolean,
    heartRate: String?,
) {
    val deviceList by devices.observeAsState(emptyList())

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Button(onClick = { onStartScan() }) {
            Text("Start Scanning")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = if (isConnected) "Connected" else "Not Connected", color = if (isConnected) Color.Green else Color.Red)

        heartRate?.let {
            Text(text = "Heart Rate: $it bpm", color = Color.Black)
        }

        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(deviceList) { deviceData ->
                Text(
                    text = "${deviceData.name} - ${deviceData.macAddress} - RSSI: ${deviceData.rssi}",
                    color = if (deviceData.isConnectable) Color.Blue else Color.Gray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onConnectDevice(deviceData.device)
                        }
                        .padding(8.dp)
                )
            }
        }
    }
}