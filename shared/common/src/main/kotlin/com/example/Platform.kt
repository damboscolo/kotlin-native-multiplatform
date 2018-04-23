package com.example

expect class Platform() {
    val name: String
}

class Greeting {
    fun greeting(): String = "Hello, ${Platform().name}"
}
