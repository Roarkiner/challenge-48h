package com.example.challenge48h

import android.content.Context
import com.google.gson.Gson
import java.io.File
import java.io.FileOutputStream

class MachineStatusCheckManager(private val context: Context) {

    fun checkIfStatusChanged(): Int?{
        val coffeeStatusInData = readMachineStatusData()
        val currentCoffeeStatus = checkMachineStatus()

        if(coffeeStatusInData != currentCoffeeStatus){
            updateCoffeeStatus(currentCoffeeStatus)
            return currentCoffeeStatus
        } else {
            return null
        }
    }

    fun checkMachineStatus(): Int{
        var rand = Math.random() > 0.5
        return if(rand)
            1
        else
            0
    }

    fun createDataFileIfDoesntExist(coffeeMachineStatus: Int){
        val file = File(context.filesDir, "machineStatusData.json")

        if (!file.exists()) {
            file.createNewFile()
            val coffeeStatus = MachineStatus(1)
            val json = Gson().toJson(coffeeStatus)
            file.writeText(json)
        }
    }

    private fun readMachineStatusData(): Int {
        val file = File(context.filesDir, "machineStatusData.json")
        val json = file.readText()
        return Gson().fromJson(json, MachineStatus::class.java).coffee
    }

    private fun updateCoffeeStatus(newStatus: Int){
        val file = File(context.filesDir, "machineStatusData.json")
        val json = file.readText()
        val machineStatus = Gson().fromJson(json, MachineStatus::class.java)
        machineStatus.coffee = newStatus

        val updatedJsonString = Gson().toJson(machineStatus)
        file.writeText(updatedJsonString)
    }
}