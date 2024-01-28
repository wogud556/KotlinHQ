package com.kotlin.`11`

fun main(args : Array<String>){
    val t = Thread(object : Runnable{
        override fun run() {
            TODO("Not yet implemented")
        }

    })
    t.run()

    val x = Thread (object : Runnable {
        override fun run() {
            TODO("Not yet implemented")
        }
    })
}

class MyRunnable : Runnable {
    override fun run() {
       println("hello world")
    }

}