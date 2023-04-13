package com.example.challenge48h.manager

class CoffeeApiManager {
    fun GetCoffeeMachineStatus() : Boolean{
        var random = Math.random()
        return random > 0.5;
    }
}