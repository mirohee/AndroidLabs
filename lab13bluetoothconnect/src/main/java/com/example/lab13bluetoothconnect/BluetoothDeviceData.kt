package com.example.lab13bluetoothconnect

import android.bluetooth.BluetoothDevice

data class BluetoothDeviceData(
    var name: String,
    var macAddress: String,
    var rssi: Int,
    var isConnectable: Boolean,
    val device: BluetoothDevice
)

