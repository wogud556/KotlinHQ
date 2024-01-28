package com.kotlin.`8`

fun main(args: Array<String>){
    var obj = CC()
    obj.f()
}

open class AA(){//상속이 가능한 클래스로 바뀜
   open fun f(){
       println("AAAAAAAAAA")
   }

}

interface BB{
    open fun f(){
        println("BBBBBBBBB")
    }
}

class CC : AA(), BB //인터페이스 가져오기
{
    override fun f(){
        println("1")
        super<AA>.f()
        println("1")
        super<BB>.f()
        println("1")
    }
}
