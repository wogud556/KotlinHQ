## 전략 패턴

### 목표
- 객체의 동작을 런타임에 변경시키는 것

### 예
- 3장에서 이야기했던 경량 디자인 패턴을 설명할때 사용했던 횡스크롤 게임
- 모든 무기는 캐릭터가 바라보는 방향으로 탄환을 발사(달팽이한테 너무 가까이 다가가면 위험)
```
enum class Direction{
    LEFT, RIGHT
}
```
- 모든 탄환은 좌표쌍과 방향을 가짐(이 게임은 2D 게임임)
```
data class Projectile(
    private var x: Int,
    private var y: Int,
    private var direction: Direction
)
```
- 탄환이 한종류 뿐이었다면 2장에서 배운 정적 팩토리 메서드를 통해 다음과 같이 간단하게 구현할 수 있음
```
class OurHero{
    private var direction = Direction.LEFT
    private var x: Int = 42
    private var y: 173

    fun shoot(): Projectile{
        return Proejectile(x,y,direction)
    }
}
```
- 그러나마이클은 최소 세종류의 탄환을 구현하기 원함
  - 콩알총 : 직선으로 날아가는 콩알탄을 발사함. 캐릭터가 기본적으로 장착하고 있는 무기
  - 석류탄 : 마치 수류탄처럼 적에게 맞으면 폭발
  - 바나나 : 화면 끝에 닿으면 부메랑처럼 돌아옴

### 과일 무기 구현
- 자바 스타일로 해결하는 법을 본다
- 자바에서는 변경되는 부분을 추상화하는 인터페이스를 만듬
- 과일무기 에제에서는 변경되는 부분이 무기이기 때문에 다음과 같이 Weapon인터페이스를 선언함
```
interface Weapon{
    fun shoot(
        x: Int,
        y: Int,
        direction : Direction
    ): Projectile
}
```
- 모든 무기는 잉 인터페이스를 구현해야 함
- 먼저는 무기를 화면에 그리거나 애니메이션을 구현해야 하는 것은 아니기 때문에 그런 동작은 코드에 포함하지 않았음
```
// 직선으로 날아가는 콩알총
class Peashooter : Weapon{
    override fun shoot(
        x: Int,
        y: Int,
        direction: Direction
    ) = Projectile(x, y, direction)
}

// 화면 끝에 닿으면 되돌아오는 바나나
class Banana : Weapon{
    override fun shoot(
        x: Int,
        y: Int,
        direction: Direction
    ) = Projectile(x, y, direction)
}

// 비슷한 방법으로 다른 구현체도 추가할 수 있음
```
- 이 게임의 모든 무기는 동일한 인터페이스를 구현
- 즉 항상 shoot 메서드를 덮어 씀
- 주인공 캐릭터는 무기 객체에 대한 참조를 들고 있을 것
- 처음에는 콩알총을 들고 있다
```
private var currentWeapon: Weapon = Peashooter()
```
- 실제 발사 동작은 들고 있는 무기 객체에 위임
```
fun shoot(): Projectile = currentWeapon.shoot(x, y, direction)
```
- 이제 다른 무기를 장착하는 기능만 구현하면 됨
```
fun equip(weapon: Weapon){
    currentWeapon = weapon
}ㄴ
```
- 이것이 전략 패턴의 전부
- 전략 패턴을 사용하면 알고리즘을 교체할 수 있음

### 일급 객체로서의 함수
- 코틀린에서는 클래스를 이렇게 많이 작성하지 않고 더 효율적으로 같은 기능을 구현할 수 있음
- 코틀린의 함수가 일급 객체 라는 사실 덕분임
- 그런데 일급 객체라니 무슨뜻인가?
- 그건 다른 일반적인 값처럼 변수에 함수를 대입할 수 있다는 것
- 예를들어 기본적인 타입의 값은 다음과 같이 변수에 할당할 수 있음
```
val x = 7
```
- 마찬가지로 객체도 변수에 할당할 수 있다
```
var myPet = Canary("마이클")
```
- 그렇다면 변수에 함수를 할당하지 못할 이유가 있나
- 코틀린에서는 다음과 같이 손쉽게 변수에 함수를 저장할 수 있다
```
val square = fun(x: Int): Long{
    return (x*x)
}
```
- 이를 통해 얼마다 설계를 단순화 시킬 수 있는지 살펴본다
- 먼저 모든 무기를 정의할 네임 스페이스를 만들 것
- 이를 위해서는 Object 키워드를 사용하면 됨
- 꼭 네임 스페이스를 만들어야 하는 것은 아니지만 좀 더 안전한 방법임
- 그러면 각 무기를 클래스 대신 하나의 함수로 정의할 수 있음
```
object Weapon{
    // 직선으로 날아가는 콩알총
    fun peashooter(x: Int, y: Int, direction: Direction):
        Projectile{
            return Projectile(x, y, direction)
    }

    fun banana(x: Int, y: Int, direction: Direction): Projectile{
        return Projectile(x, y, direction)
    }
    // 비슷한 방법으로 다른 구현체도 추가할 수 있음
}
```
- 보다시피 인터페이스를 구현하는 대신 같은 매개변수와 반환형을 갖는 함수를 여러개 구현함
- 가장 흥미로운 부분은 주인공 객체
- OurHero클래스는 이제 2개의 값을 갖는데 둘 다 함수임
```
class OurHero{
    var currentWeapon = Weapons::peashooter

    val shoot = fun(){
        currentWeapon(x, y, direction)
    }
}
```
- 교체되는 부분은 currentWeapon이며 이제 shoot은 currentWeapon을 호출하는 익명 함수가 됐음
- 이런 구현이 잘 동작하는지 확인하기 위해 기본 무기를 한 번 발사하고 다른 무기로 교체해서 다시 발사하는 코드를 작성해보자
```
val hero = OurHero()
hero.shoot()
hero.currentWeapon = Weapon::banana
hero.shoot()
```
- 기능은 똑같지만 작성해야 하는 클래스의 수가 크게 줄었다는 점에서 주목해라
- 교체해야 하는 알고리즘이 상태에 의존하지 않는다면 간단한 함수를 사용할 수 있음
- 그렇지 않다면 인터페이스를 하나 정의하고 각 전략 패턴이 인터페이스를 구현하도록 해야 함
- 메서드 참조 연산자(::)가 처음 등장함
- 이 연산자를 사용하면 메서드를 호출하는 대신 마치 변수처럼 참조할 수 있음
- 전략 패턴은 련타임에 애플리케이션의 동작을 변경하고 싶을 때 매우 윺용하게 사용할 수 있음
- 예를들어 초과 예약(좌석 수보다 더 많은 승객이 예약하는 것)을 허용하는 비행 예약 시스템을 만든드고 하자.
  - 비행 하루 전까지 초과 예약을 허용했다가 그 이후로는 초과 예약을 받지 않도록 하고 싶음
  - 이럴 때는 비행 하루 전에 전략을 교체하면 됨
  - 그러면 복잡한 검사 코드를 작성할 필요가 없다
- 이제 복잡한 자료구조를 다룰 때 도움이 되는 패턴을 알아본다.