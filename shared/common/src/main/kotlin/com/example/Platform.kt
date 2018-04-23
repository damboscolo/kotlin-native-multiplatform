package com.example

expect class Platform() {
    val name: String
}

class Main {
    fun sayHello(): String = "Hello, ${Platform().name}"
}
