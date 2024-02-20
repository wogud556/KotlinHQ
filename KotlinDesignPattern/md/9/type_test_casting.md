## 타입 검사와 캐스팅
- 코트를 작성하다 보면 Is를 이용해서 어떤 객체가 어떤 타입인지 검사하고 as를 이용해서 타입 변환을 하고 싶을 때가 있음
- 예를 들어 슈퍼 히어로를 위한 시스템을 만들고 있다고 한다.
- 슈퍼히어로들은 제각기 다른 메서드를 갖고 있음

```
interface Superhero
class Batman : Superhero {
    fun callRobin() {
        println("출격, 로빈!")
    }
}

class Superman : Superhero {
    fun fly() {
        println("높이 높이, 저 멀리!")
    }
}
```
- 초능력을 쓰려 할때 다음 함수를 호출

```
fun doCoolStuff(s: Superhero) {
    if (s is Superman) {
        (s as Superman).fly()
    }
    else if (s is Batman) {
        (a as Batman).callaRobin()
    }
}
```
- 하지만 알다시피 코틀린에는 스마트 캐스팅이 있음
- 그래서 이 경우에는 굳이 명시적 캐스팅을 해주지 않아도 됨
- 스마트 캐스팅을 사용하면 얼마나 코드가 깔끔해지는지 본다
- 명시적 캐스팅만 지워주면 됨

```
fun doCoolStuff(s: Superhero) {
    if (s is Superman) {
        s.fly()
    }
    else if (s is Batman) {
        s.callRobin()
    }
}
```
- 뿐만 아니라 스마트 캐스팅을 사용할 때 when()을 사용하면 코드가 한층 더 깔끔해진다.

```
fun doCoolStuff(s : Superhero) {
    when(s) {
        is Superman -> s.fly()
        is Batman -> s.callRobin()
        else -> println("알 수 없는 슈퍼히어로")
    }
}
```
- 경우에 따라 예외는 있을 수 있지만 일반적으로 명시적 캐스팅은 지양하고 되도록 스마트 캐스팅을 사용해야 한다.
```
//superhero
val superheroAsString = (s as String)
```
- 이 안전한 캐스팅 연산자는 캐스팅이 불가능한 경우에 예외를 던지는 대신 null을 반환함