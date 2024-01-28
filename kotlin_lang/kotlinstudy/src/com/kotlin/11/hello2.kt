package com.kotlin.`11`

class MyClass {
    public fun publicFun() = object {
        var x = 111
    }

    private fun privateFun() = object {
        var x = 222
    }
    fun print(){
       // println(publicFun().x)
        println(privateFun().x)
    }
}

fun main(args : Array<String>){
    MyClass().print()
}
