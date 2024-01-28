package com.kotlin.`9`

const val MY_CONST = "CONST" //어떤 클래스에도 종속되지 않고 클래스 톱레벨에 선언이 된 상태

@Deprecated(MY_CONST)// 최초 상수식으로 사용할 수 없다고 나옴
fun main(args: Array<String>){
    var obj = Address()
    println(obj.name);
}
class Address {
    var name: String = "Kotlin"
}

class Address2 {
    var name: String = "Kotlin"
        get() {
            return field + "!!!"
        }///get()함수 선언으로 재정의를 할 수 있다.
        set(value) {field = value}///get과 같음
}

class Address3 {

    val isEmpty: Boolean
        get() {return false}
}
/**
* 클래스의 바디에서 선언된 프로퍼티만 가능
* 기본생성ㄴ자에서 선안된 프로퍼티는 안됨
* var 프로퍼티만 가능
 * custom accessor이 없아야 함
 * non-null 타입이어야 함
 * 프리미티브 타입이면 안됨
* */


class Address4 {
    lateinit var data : String

    fun setUp(){
        data = "나중에"
    }
}