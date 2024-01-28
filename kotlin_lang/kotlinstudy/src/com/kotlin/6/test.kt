
fun main(args : Array<String>){
   // other1()
   // other2()

    var a = listOf(1,2,3) // 코틀린에서 자동적으로 포함된 것
    println(a)

    foo0()
    foo1()
}

fun foo0(){
    var ints = listOf(0,1,2,3)
    ints.forEach(
        fun(value: Int){ //람다식
            if(value ==  1) return
            print(value)
        }
    )
    println("End")
}

fun foo1(){
    var ints = listOf(0,1,2,3)
    ints.forEach label@{// 예상치 않은 결과를 얻을 수 있다.
            if(it ==  1) return@label//람다 함수 한번만 호출하기 때문에 label를 사용
            print(it)
        }

    println("End")
}

fun foo2(){
    var ints = listOf(0,1,2,3)
    ints.forEach {
        if(it ==  1) return@forEach//암시적 label 사용한 모습
        print(it)
    }

    println("End")
}