## 제네릭 타입 실체화
- 9장 앞부분에서 인라인 함수를 다룸
- 인라인 함수는 복사되기 때문에 jvm의 중요한 제약 중 하나임
- 즉 타입 소거라는 졔약을 피해 갈 수 있음
- 어쨌든 인라인 함수 안에서는 어떤 타입을 사용하는지 정확히 알 수 있기 때문
- 다음과 같은 예제를 살펴본다
- Number를 입력으로 받는 제네릭 함수를 만들 것
- 여기서 Number는 Int또는 Long인데 이 함수는 자신의 타입과 매갭녀수의 타입이 동일한 경우에만 매개 변수를 출력할 것
- 다음과 같이 단순하게 구현하면
- 인스턴스의 타입을 검사하는 소스임
```
fun <T> printIfSameType(a: Number) {
    if(a is T) { // <== 오류 발>
        println(a)
    }
}
```
- 하지만 이 코드는 다음과 같은 오류를 내며 컴파일 되지 않음
```
> Cannot check for instance of erased Type: T
```
- 자바에서는 이럴 때 보통 클래스를 인수로 넘김
- 코틀린에서도 비슷한 방법을 사용할 수 있음
- 안드로이드 개발 경험이 있다면 이 패턴이 표준 라이브러리에서 자주 사용되는 것을 알 수 있음
```
fun <T: Number> printIfSameType(a) {
    if(class.isInstance(a)) {
        println("예")
    } else {
        println("아니오")
    }
}
```
- 다음과 같이 테스트해보면 제대로 동작하는 것을 알 수 있음
```
printIfSameType(Int::class, 1) // "예" 출력(1은 int)
printIfSameType(Int::class, 2L) // "아니오" 출력(2L은 Long)
printIfSameType(Int::class, 3L) // "예" 출력(3L은 Long)
```
- 이제는 함수의 타입(Int또는 Long)을 인수로 전달하지 않고 꺾쇠괄호 사이에 전달하는 것을 볼 수 있음
- 실체화 된 타입을 사용하면 다음과 같은 장점이 있음
  - 클래스를 인수로 전달할 필요가 없어서 메서드 시그니처가 더 깔끔해짐
  - 함수 내에서 is 문법을 사용할 수 있음
  - 타입 추론이 잘 동작함
  - 즉 컴파일러가 추론할 수 있다면 타입 매개변수를 완전히 생략할 수 있음
- 당연히 Inline 함수에 적용되는 규칙도 동일하게 적용됨
- 함수 전체가 복제되기 때문에 함수가 너무 길면 안됨
- 실체화된 타입을 사용하는 다른 경우를 알아본다
- 바로 함수 오버로딩
- 이름은 같고 동작하는 타입만 다른 2개의 함수를 정의해본다.
```
fun printList(list: List<Int>) {
    println("Int 리스트입니다.")
    println(list)
}
fun printList(list: List<Long>) {
    println("Long 리스트이다.")
    println(list)
}
```
- 이 코드는 플랫폼 정의 충돌을 일으키기 대문에 컴파일되지 않음
- JVM이 볼때 두 함수는 똑같이 printList(list: List)와 같은 시그니처를 가짐
- 컴파일 도중에 타입이 소거되기 때문임
- 하지만 reified 키워드를 이용하면 이 문제를 쉽게 해결할 수 있음
```
inline fun <reified T : Any> printList(list: List<T>) {
    when {
        1 is T -> println("Int 리스트.")
        1L is T -> println("Long 리스트")
        else -> println("다른 타입의 리스트")
    }
    println(list)
}
```
- 전체 함수가 Inline 함수가 되기 때문에 이제 리스트의 실제 타입을 확인해서 올바른 결과를 출력할 수 있음
