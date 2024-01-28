package com.kotlin.`11`

object CountManager {
    var count = 0
}

fun main(args : Array<String>){
    println(CountManager)//알아서 참조가 되고 컴파일이 된다.(객체명)

    CountManager.count ++
    println(CountManager.count)
    CountManager.count ++
    println(CountManager.count)
    CountManager.count ++
    println(CountManager.count)
}
