package com.kotlin.`10`

fun main(args: Array<String>){
   val obj = User3("ttt", 111)

    println(obj)
    val obj2 = obj.copy(age=777)

    println(obj.copy("Kotlin"))

    val obj3 = obj2.copy("Kotlin")

}


data class User3(var name : String, var age : Int)