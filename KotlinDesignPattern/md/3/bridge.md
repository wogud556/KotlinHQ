## 브릿지 패턴
- 어댑터 디자인 패턴을 사용하면 레거시 코드를 쉽게 사용할 수 있음
- 반면 브리지 디자인 패턴은 상속을 남용하는 것을 막아줌
- 브릿지 패턴의 동작 원리는 굉장히 간단함
- 은하제국 지상군 소속의 각종 트루퍼(보병)를 관리하는 시스템을 만든다고 하자
- 먼저 트루퍼를 나타내는 인터페이스를 하나 정의할 것
```
interface Trooper{
    fun move(x: Long, y: Long)
    fun attackRebel(x: Long, y: Long)
}
```
- 그리고 트루퍼의 각 종류에 해당하는 클래스를 다음과 같이 구현함
```
class StormTrooper: Trooper{
    override fun move(x: Long, y: Long){
        //보통 속도로 이동
    }
    override fun attackRebel(x: Long, y: Long){
        //대부분 빚나감
    }
}

class ShockTrooper: Trooper{
    override fun move(x: Long, y: Long){
        // 일반적인 stromTrooper보다는 느리게 이동
    }
    override fun attackRebel(x: Long, y: Long){
        // 명중할 때도 있음
    }
}
```
- 더 강한 버전도 있다.
```
class RiotControllerTrooper: StormTrooper(){
    override fun attackRebel(x: Long, y : Long){
        //전기 충격 곤봉을 가짐
    }
}

class FlameTrooper : ShockTrooper(){
    override fun attackRebel(x: Long, y: Long){
        //화염방사기를 사용함
    }
}
```
- 다른 트루퍼보다 빠르게 이동할 수 있는 스카우트 트루퍼도 있음
```
class ScoutTrooper : ShockTropper(){
    override fun move(x: Long, y: Long){
        //빠른 속도로 이동
    }
}
```
- 정의할 클래스가 정말 많음
- 어느 날 친애하는 디자이너가 다가와서 새로운 요청사항을 전달함
- 모든 스톰트루퍼가 소리를 지를 수 있어야 하며 내용은 각각 달라야 한다는 것임
- 별 생각 없이 인터페이스에 새 함수를 추가한다.

```
interface Infantry{
    fun move(x: Long, y: Long)
    fun attackRebel(x: Long, y : Long)
    fun shout(): String
}
```
- shout() 함수를 추가하는 순간 이 인터페이스를 구현하던 모든 클래스가 컴파일되지 않음
- 심지어 개수도 적지 않음
- 수정해야 하는 코드가 매우 많다는 뜻
- 어쩔 수 없다.
- 진짜 그런가?
- 클래스 5개의 구현을 변경한 뒤에 50개가 아니라서 다행이라고 안도하는 수밖에 없는건가

### 변경 사항에 다리 놓기
- 브리지 디자인 패턴의 핵심 아이디어는 클래스 계층 구조를 얕게 만듦으로써 시스템에서 구체 클래스의 수를 줄이는 것
- 뿐만 아니라 부모 클래스를 수정했을 때 자식 클래스에서 발견하기 어려운 버그가 발생하는 현상을 뜻하는 '깨지기 쉬운 기반 클래스 문제'를 예방하는 데에도 도움이 됨
- 먼 왜 이렇게 복잡한 클래스 계층이 생겼는지 알아보
- 그 이는 서로 무관한 두가지의 독립적인 속성이  있기 때문임
- 바로 무기 종류와 이동 속도
- 상속을 사용하지 않고 이 속성들을 생성자로 전달하기를 원한다고 하자
- 이 클래스는 지금까지 사용한 인터페이스를 똑같이 구현함
```
data class StormTrooper(
    private val weapon: Weapon,
    private val legs: Legs
) : Trooper{
    override fun move(x: Long, y: Long){
        legs.move(x, y)
    }

    override fun attackRebel(x: Long, y: Long){
        weapon.attack(x, y)
    }
}
```
- StormTrooper가 받는 속성도 인터페이스여야 함
- 그래야 나중에 어떤 속성을 사용할 지 결정할 수 있기 때문임
```
typealias PointsOfDamage = Long
typealias Meters = Int
interface Weapon {
    fun attack(): PointOfDamage
}
interface Legs{
    fun move(): Meters
}
```
- 메서드에서 Long이나 Int를 반환하지 않고 Meter와 PointsOfDamage를 반환하고 있는 것에 주목
- 이 기능을 타입별칭이라고 함
- 잠시 샛길로 새서 타입 별칭이 어떻게 동작하는지 알아보자

### 타입 별칭
- 코틀린에서는 존재하는 타입에 별명을 붙일 수 있음. 이를 타입 별칭
- 별칭을 설명할 때는 typealias키워드를 사용
- 지금부터 move()메서드의 반환 타입으로 그냥 int대신 meters를 사용할 수 있음
- 새로운 타입을 만든 것이 아님
- 코틀린 컴파일러는 컴파일 과정에서 항상 PointOfDamage를 Long으로 해석할 것임
- 타입 별칭을 사용하면 다음과 같은 이점이 있음
  - 코드에 의미가 더 잘 드러남
  - 반환값의 의미가 무엇인지 정확하게 표현이 됨
  - 코드가 더 간결해짐
  - 타입 별칭을 사용하면 복잡한 제네릭 표현식을 숨길 수 있음
    - 이어지는 절에서 더 자세히 설명
### 상수
- stormTrooper 클래스로 다시 돌아가보자.
- 이제 weapon과 legs 인터페이스를 실제로 구현해야한다
- 먼저 stormTrooper의 보통 데미지와 보통 속도를 미터법 단위를 사용해 정의한다
```
const val RIFLE_DAMAGE = 3L
const val REGULAR_SPEED: Meters = 1
```
- 상수는 컴파일 시에 결정되는 값이기 때문에 매우 효율적
- 자바의 static final변수와는 달리 코틀린의 상수는 클래스 내에서 선언 불가
- 패키지 최상위에 선언하거나 객체 안에 두거나 둘중 하나
> 코틀린은 타입 추론이 가능하긴 하지만 상수 선언 시 타입을 명시적으로 지정할 수도 있고 타입별칭을 사용할 수도 있음
> DEFAULT_TIMEOUT_SECONDS = 60 대신 DEFAULT_TIMEOUT: Seconds 60이라고 쓰는 것이 나을수도?

- 예제 인터페이스
```
class Rifle : Weapon{
    override fun attack(x: Long, y: Long) = RIFLE_DAMAGE
}
class Flamethrower : Weapon{
    override fun attack(x: Long, y: Long) = RIFLE_DAMAGE * 2
}
class Batton : Weapon{
    override fun attack(x: Long, y: Long) = RIFLE_DAMAGE * 3
}
```
- 그 다음에는 이동에 관한 인터페이스를 구현
```
class RegularLegs: Legs{
    override fun move() = REGULAR_SPEED
}
class AthleticLegs: Legs{
    override fun move() = REGULAR_SPEED * 2
}
```
- 복잡한 클래스 계층 구조 없이도 동일한 기능을 구현할 수 있게 됐음
- 정말 그런지 확인해본다
```
val stormTrooper = StormTrooper(Rifle(), RegularLegs())
val flameTrooper = StormTrooper(Flamethrower(), RegularLegs())
val scoutTrooper = StormTrooper(Rifle(), AthleticLegs())
```
- 이제 클래스 계층이 납작해진 덕분에 확장도 용이하며 코드를 이해하기도 더 쉬움
- 다른 기능이 더 필요하다면 (앞서 언급한 소리치기같이) 새로운 인터페이스를 정의하고 stromTrooper 생성자에 인수를 추가하면 됨
- 현업에서 브리지 패턴은 의존성 주입과 함께 사용될 때가 많음
- 예를들어 브리지 패턴을 사용하면 실제 데이터베이스를 사용하는 구현체를 손쉽게 목 객체로 대체할 수 있음
- 이렇게 하면 코드를 작성하기도 쉬워지고 테스트 속도도 향상됨
