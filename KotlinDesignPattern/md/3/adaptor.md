## 어댑터 파턴
- 어댑터 디자인 패턴은 어떤 인터페이스를 다른 인터페이스로 변환하고자 할 때 사용
- 이른바 돼지코라 불리는 플러그 어댑터 같은거 생각하면 됨
- 물리장치에 맞는 플러그를 사용하듯 어댑터 패턴도 똑같음
- 원리를 알아보기 위해 인터페이스를 정의한다.
```
interface PlugTypeF {
    val hasPower: Int
}
```
- PlugTypeA는 전원을 String으로 다룸
- 값이 True면 전원이 공급되는 상태를
- False면 공급되지 않는 상태를 의미함
```
interface PlugTypeA {
    val hasPower: String // "TRUE"또는 "FALSE"
}
```
- UsbMini클래스는 전원을 enum 으로 관리함
```
interface UsbMini {
    val hasPower: Power
}
enum class Power {
    TRUE, FALSE
}
```
- 마지막으로 UsbTypeC에서는 전원이 Boolean값
```
interface UsbTypeC {
    val hasPower: Boolean
}
```
- 최종적인 목표는 전원 값이 F타입 플러그에서 휴대전화까지 흐르게 하는 것
- 이 시나리오는 다음의 함수로 표현함
```
fun cellPhone (chargeCable: UsbTypeC){
    if(chargeCable.hasPower){
        println("충전중")
    } else {
        println("전원이 연결되지 않았음")
    }
}
```
- 먼저 F타입 전원 콘센트를 나타내는 코드를 구현하자
- PlugTypeF 객체를 반환하는 함수가 될 것임
```
//전원 콘센트는 PlugTypeF 인터페이스 노출
fun krPowerOutlet(): PlugTypeF{
    return object : PlugTypeF {
        override val hasPower = 1
    }
}
```
- 2장에서 object 키워드를 사용하는 두가지 경우를 봄
- 전역 시야에서는 object 키워드로 싱글톤 객체를 만듬
- 클래스 내부에서 companion키워드와 함께 사용하면 static함수를 정의할 공간을 만들어줌
- 이번에는 익명 클래스를 만드는 데에 object 키워드를 사용함
- 익명 클래스란 즉석에서 만든 클래스로, 보통 인터페이스를 즉석에서 구현할 때 활용함
- 휴대전화 충전기는 PlugTypeA를 입력으로 받아 UsbMini를 반환함
```
// 충전기는 PlugTypeA 인터페이스를 입력받고 UsbMini인터페이스를 노출
fun charger (plug: PlugTypeA) : UsbMini {
    return object : UsbMini {
        override val hasPower = Power.valueOf(plug.hasPower)
    }
}
```
- 다음으로 cellPhone, Charger, krPowerOutlet함수를 모두 연결해보자
```
    cellPhone (
        //Type mismatch: inferred Type is UsbMini but UsbTypeC was expected
        charger (
            //Type mismatch: inferred Type is PlugTypeF but PlugTypeF was expected
            krPowerOutlet()
        )
    )
```
- 타입 오류가 2개 발생하는 것을 볼 수 있음.
- 어댑터 디자인을 사용하면 이런 문제를 해결할 수 있다.

### 기존 코드에 어댑터 패턴 적용
- 필요한 어댑터는 두가지인데, 하나는 전원 플러그 어댑터이고 다른 하나는 USB포트 어댑터임
- 자바에서 보통 이런 상황에서 클래스를 2개 만듬
- 하지만 코틀린에서는 클래스를 만들지 않고 확장 함수를 사용함
- 확장 함수가 무엇인지는 1장에서 간략하게 설명했음
- 아래 예제는 F타입 콘센트에 A타입 플러그를 사용할 수 있도록 해주는 어댑터를 다음과 같이 확장 함수로 구현할 수 있음
```
fun PlugTypeF.toPlugTypeA(): PlugTypeA {
    val hasPower = if(this.hasPower == 1) "TRUE" else "FALSE"
    return object : PlugTypeA {
        //전원 연결
        override val hasPower = hasPower
    }
}
```
- 확장함수 내부의 this키워드는 확장 대상 객체를 가리킴
- 클래스 정의 내부에서 메서드를 구현할 때와 마찬가지
- 여기서도 인터페이스를 즉석으로 구현하기 위해 익명 클래스를 사용함
- 미니 USB와 USB-C사이의 어댑터도 비슷한 방법으로 구현할 수 있음
```
fun UsbMini.toUsbType : UsbTypeC{
    val hasPower = this.hasPower == Power.TRUE
    return object : UsbTypeC {
        override val hasPower = hasPower
    }
}
```
- 마지막으로 어댑터를 사용해서 다음과 같이 모든 함수를 하나로 연결할 수 있음

```
cellPhone(
    charger(
        krPowerOutlet().toPlugTypeA()
    ).toUsbType()
)
```
- 예제에서 볼 수 있듯이 인터페이스를 구현하는 클래스를 새로 구현하지 않아도 됨
- 코틀린 확장함수를 사용하면 코드를 짧고 간결하게 유지할 수 있음
- 어댑터 디자인 패턴은 다른 디자인 패턴에 비해 직관적으로 이해하기 쉬우며 여러 곳에서 사용되는 것을 발견할 수 있을 것
- 이제 실제 코드에서 어댑터 패턴이 어떻게 사용되는지 자세히 알아보자

### 실제 코드에서 사용되는 어댑터 패턴
- 이미 어댑터 디자인 패턴을 본 적이 많이 있을 것
- 일반적으로는 개념과 구현 사이의 변환을 수행하기 위해 어댑터 패턴을 사용함
- JVM의 집합 자료구조 개념과 스트림 개념을 예로 들자
- 집합 자료구조는 1장에서 이미 설명함
- 리스트는 listOf() 함수로 생성할 수 있는 원소 집합
```
val list = listOf("a", "b", "c")
```
- 스트림이란 게으른 원소 집합임
- 그런데 스트림을 입력으로 받는 함수에 리스트를 전달할 수 없음
- 의미상으로는 아무 문제가 없음에도 그렇다.
```
fun printStream(stream: Stream<String>){
    stream.forEach(e -> println(e))
}
printStream(list)
```
- 다행히 집합 자료구조에는 .stream() 이라는 어댑터 메서드가 존재함
```
printStream(list.stream())
```
- 다른 많은 코틀린 객체도 어댑터 메서드를 가지고 있음
- 어댑터 메서드의 이름은 일반적으로 to로 시작함
- 가령 toTypedArray() 함수는 리스트를 배열로 변환함

### 어댑터 패턴 사용시 주의사항
- 110 볼트 전자 제품에 어댑터를 연결해 220볼트 콘센트에 꽂았다가 제품을 고장 낸 경험이 있는가?
- 주의를 기울이지 않으면 코드에서 똑같은 일이 발생함
- 다음 예제는 어댑터를 사용함
- 컴파일도 문제없이 된다
```
val stream = Stream.geterate{ 42 }
stream.toList()
```
- 그러나 프로그램은 무한 루프에 빠짐
- Stream.generate() 함수가 무한히 긴 정수 리스트를 만들어 내기 떄문
- 따라서 어댑터 디자인 패턴을 사용해도 괜찮을지 주의 깊게 살펴야 함
