## 합성 패턴
- 3장은 장 전체갚 객체 합성에 관한 내용임
- 그러니 합성 패턴이라는 절이 따로 있는것은 아님
- 자연스럽게 다음과 같은 질문이 생김
- 합성 패턴이 다른 모든 패턴을 포괄하는 것이 아닌가?
- 브릿지 패턴도 그랬지만 이름이 꼭 쓰임새와 유용성을 잘 설명해 주는 것은 아님
- 이전 절에서 사용했던 트루퍼 예제를 이어가보면, 은하 제국의 장교들은 아무리 좋은 장비를 갖추더라도 제대로 된 지휘 체계가 없기 때문에 반란군에 맞서 싸우기 어렵다는 사실을 발견하게 됨
- 지휘 체계를 개선하기 위해 제국은 분대라는 개념을 도입함
- 분대는 종류에 관계 없이 1명 이상의 트루퍼로 구성되며, 명령이 주어지면 마치 한몸처럼 똑같이 움직여야 함
- 분대를 나타내는 Squad클래스는 당연히 Trooper객체의 집합으로 이뤄짐
```
class Squad(val units: List<Trooper>)
```
- 먼저 2개 분대를 만들면
```
val bobaFett = StormTrooper(Rifle(), RegularLegs())
val squad= Squad(listOf(bobaFett.copy(), bobaFett.copy(), bobaFett.copy()))
```
- 분대가 한몸처럼 움직이도록 하기 위해 move와 attack이라는 메서드를 추가할 것
```
class Squad(private val unit: List<Trooper>){
    fun move(x: Long, y: Long){
        for(u in units){
            u.move(x,y)
        }
    }
    fun attack(x: Long, y: Long){
       for (u in units){
            u.attackRebel(x,y)
       } 
    }
}

```
- 두 함수는 받은 명령을 분소속 분대원 모두가 수행하도록 함
- 일견 괜찮은 방법같음
- 하지만 trooper인터페이스에 새로운 함수를 추가하면 어떻게되나?
- 다음 코드를 살펴본다
```
interface Trooper{
    fun move(x: Long, y: Long)
    fun attackRebel(x: Long, y: Long)
    fun retreat()
}
```
- 컴파일이 깨지지는 않는 것 같음
- 그러나 squad클래스는 더이상 한몸처럼 움직일 수 없음
- 개별 트루퍼에게 있는 메서드가 분대에는 존재하지 않기 떄문
- 이런 문제를 방지하기 위해 squad클래스가 소속 분대원과 마찬가지로 Trooper인터페이스를 구현하도록 하면 어떻게 되는지 살펴본다.
```
class Squad(private val units: List<StormTrooper>) : Trooper{...}
```
- Trooper 인터페이스를 구현하도록 변경함에 따라 이제 retreat함수도 구현해야 하며
- 나머지 2개 함수에는 override 키워드를 추가해야 함
```
class Squad(private val units: List<StormTrooper>) : Trooper{
    override fun move(x: Long, y: Long){
        ...
    }
    override fun attackRebels(x: Long, y: Long){
        ...
    }
    override fun retreat(){
        ...
    }
}
```
- 이제 잠시 본래의 흐름을 벗어나, 이 예제의 문제를 해결하는 데 더 편리한 방법을 살펴본다
- 이 방법을 사용하면 동일한 객체를 생성할 수 있지만 결과적으로 더 사용하기 편리한 합성 객체가 만들어짐

### 부 생성자
- 앞 절의 예제 코드는 목표를 달성함.
- 그러나 (지금 코드와 같이) 트루퍼 리스트를 생성자에 전달하는 대신 리스트로 감싸지 않고 트루퍼 객체들을 직접 전달하고 싶을 수도 있음
```
val squad = Squad(bobaFett.copy(), bobaFett.copy(), bobaFett.copy())
```
- 한가지 방법은 Squad 클래스에 부 생성자를 추가하는 것
- 지금까지는 항상 클래스의 주 생성자만 사용함
- 주 생성자는 클래스 이름 뒤에 선언하는 생성자임
- 하지만 더 많은 생성자를 정의하는 것도 가능함
- 부 생성자는 클래스 본문 안에 constructor키워드를 사용해서 정의함
```
class Squad(private val units: List<Trooper>) : Trooper{
    constructor(): this(listOf())
    constructor(t1: Trooper): this(listOf(t1))
    constructor(t1: Trooper, t2 : Trooper): this(listOf(t1, t2))
}
```
- 자바처럼 생성자마다 클래스 이름을 적지 않아도 됨
- 클래스 이름을 바꿀 때 수정해야 할 부분이 적다는 뜻
- 부 생성자는 방드시 주 생성자를 호출해야 한다는 점을 유의한다
- 자바에서 super키워들르 사용하는것과 비슷함

### varargs키워드
- 앞의 코드처럼 인수 개수마다 생성자를 만드는 것은 확실히 좋은 방법이 아님
- 누가 몇개의 인수를 전달할 지 미리 알 길이 없기 때문임
- 자바 개발 경험이 있다면 가변 인수(같은 타임의 인수를 임의의 개수만큼 받는 기능)를 떠올렸을 것
- 자바에서는 줄임표(온점 3개)를 사용해 가변 인수로 선언함.
- 예를들면 Trooper...units와 같음
- 코틀린에서 같은 목적으로 varargs키워드를 사용함
- 부 생성자와 varargs키워드를 함께 사용하면 다음과 같은 깔끔한코드를 작성할 수 있음
```
class squad(private val units: List<Trooper>): Trooper{
    constructor(vararg units: Tooper){
        this(units.toList())
    ...
    }
}
```
- 이제 굳이 리스트로 감싸지 않아도 여러 트루퍼 객체를 사용해 분대를 만들 수 있음
```
val squad = Squad(bobaFett.copy(), bobaFett.copy(), bobaFett.copy())
```
- 막후에서 어떤 일이 일어나는지 알아본다.
- 코틀린 컴파일러는 varargs 인수를 다음과 같이 해당 타입의 Array객체로 변환해준다.
```
constructor(units: Array<Trooper>, this(units.toList()))
```
- 코틀린의 Array에는 동일한 타입의 리스트로 변환하는 어댑터 메서드가 있음
- 합성 디자인 패턴을 구할 때 어댑터 패턴이 도움이 된다는 것도 흥미로운 점

### 합성 객체로 이뤄진 합성 객체 중첩하기
- 합성 디자인 패턴애는 재미있는 부분이 또 있음
- 앞서 여러 트루퍼 객체를 담고 있는 분대를 만들 수 있다는 것을 확인함
- 그런데 여러 분대로 이뤄진 분대도 만들 수 있음
```
val platoon = Squad(Squad(), Squad())
```
- 이제 소대에 명령을 내리면 분대에 명령을 내릴 때와 같은 방식으로 동작할 것임
- 사실 이 패턴을 사용하면 아무리 복잡한 트리 구조라도 만들어 낼 수 있으며 모든 노드에 어떤 동작을 지시할 수 있음
- 합성 디자인 패턴은 4장에서 다룰 반복자 디자인 패턴과 짝을 이룸
- 따라서 반복자 디자인 패턴을 배우기 전까지는 다소 불완전해 보일 수 있음
- 두 디자인 패턴은 함께 사용될 때 비로소 빛을 발함
- 이 절을 읽고 합성 디자인 패턴의 쓸모에 의문이 생긴다면 반복자 디자인 패턴을 배운 뒤 돌아ㅗ아서 다시 한번 읽어 보기 바람
- 현업에서 합성 디자인 패턴은 사용자 인터페이스 프레임워크에서 널리 사용됨
- 예를 들어 안드로이드의 Group 위젯은 합성 디자인 패턴을 구현하고 있음
- Group위젯은 다른 여러 요소를 하나로 묶어주며 View인터페이스를 구현함으로써 포함하고 있는 요소들을 대표해 동작함
- 계층 구조에 속한 모든 객체가 같은 인터페이스를 구현하는 한, 계층 구조가 아무리 깊더라도 최상위 객체에만 접근할 수 있다면 모든 하위 객체도 제어할 수 있음
