package com.kotlin.`7`

fun main(args : Array<String>){
   // var obj = Customer("코틀린")
    //println(obj)//해당 클래스의 주소 값을 볼 수 있다.
    //var obj2 = Customer2()
    //println(obj2.fullname)
}

class Customer constructor(name : String){ //기본 생성자에 String 타입으로 입력을 받아야 함

    init{ // 기본생성자로 선언하여 사용해야한다.
        println("$name") // 여전히 사용이 가능하지 않다.
    }


//    fun print(){
//        println("$name") // 여전히 사용이 가능하지 않다.
//    }
}

class Customer2(var full : String ){ // 를 선언한다면 밑에 get set을 선언을 할 필요가 없어진다.
    var fullname : String = ""
    get(){return field}
    set(value) {field = value}
}