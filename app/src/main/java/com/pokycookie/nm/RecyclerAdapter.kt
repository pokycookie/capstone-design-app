package com.pokycookie.nm

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.pokycookie.nm.databinding.BluetoothRecyclerBinding

class RecyclerAdapter: RecyclerView.Adapter<RecyclerHolder>() {
    var deviceData = mutableListOf<BluetoothData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerHolder {
        val binding = BluetoothRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecyclerHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerHolder, position: Int) {
        holder.setData(deviceData[position])
    }

    override fun getItemCount(): Int {
        return deviceData.size
    }
}

class RecyclerHolder(private val binding: BluetoothRecyclerBinding): RecyclerView.ViewHolder(binding.root) {
    init {
        binding.root.setOnClickListener {
            Toast.makeText(binding.root.context, "${binding.deviceName.text}", Toast.LENGTH_SHORT).show()
        }
    }

    fun setData(data: BluetoothData) {
        binding.deviceName.text = "${data.name}"
        binding.deviceMAC.text = "${data.mac}"
    }
}