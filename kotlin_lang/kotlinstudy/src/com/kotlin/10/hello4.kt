package com.kotlin.`10`

fun main(args: Array<String>){
    val(name, age) = User("heeee", 1)
    print("$name, $age")
}

data class User(var name: String, var age: Int)