package com.example.lab11bluetoothscan

data class BluetoothDeviceData(
    var name: String,
    var macAddress: String,
    var rssi: Int,
    var isConnectable: Boolean
)
