package com.kotlin.`10`

fun main(args: Array<String>){
    val obj2 = User2("Kotlin",113)
    println(obj2)

    val obj1 = Nodata("Kotlin",113)
    println(obj1)
}

class Nodata(val name : String, var age : Int)

data class User2(val name : String, var age : Int)