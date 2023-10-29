## 클래스와 상속
- 코틀린은 다중 패러다임 언어이긴 하나 자바의 친척뻘임은 부인하 수 없음
- 자바는 클래스에 기반한 언어기에 코틀린에도 당연한 기능이다.
- 클래스, 인터페이스, 추상클래스, 데이터 클래스를 선언하는 문법을 다룰 것

### 클래스
- 클래스란 데이터와 메서드를 한데 모아놓은 것
- 클래스를 선언할 때 자바와 똑같이 class 키워드를 사용함
- 컴퓨터 게임을 만든다고 생각할 때 플레이어를 나타내는 클래스는 다음과 같이 정의할 수 있음
```
class Player{

}
```
- 클래스를 인스턴트화 하는 방법도 간단함
```
val player = Player()
```
- 코틀린에서는 new 키워드를 사용하지 않는다.
- 코틀린 컴파일러는 클래스 이름 뒤에 괄호() 가 있으면 새로운 인스턴스를 생성한느 것으로 인식함
- 위의 예제처럼 클래스 본문이 없는 경우에는 중괄호를 생략할 수 있음
```
class Player // 유효한 클래스 선언
```
- 아무런 함수나 속성이 없는 클래스는 특별히 쓸모 있지는 않지만, 4장에서 왜 이런 문법이 존재하며 어떻게 코틀린의 다른 기능과 어우러지는제 살펴볼 것

### 주 생성자
- 플레이어를 생성할 때 이름을 지정할 수 있음 좋을 것임
- 이를 위해 Player 클래스에 주 생성자를 추가해보자
```
class Player(name : String)
```
- 이제 다음과 같은 선언은 불가능함
```
val player = Player()
```
- 다음과 같이 새로운 플레이어 객체를 만들 때마다 이름을 넣어 줘야 함
```
val player = Player("Roland")
```
- 생성자를 잠시 뒤에 살펴보고, 지금은 속성에 대해 알아보자

### 속성
- 자바를 사용하다 보면 접근자(getter)와 설정자(setter)의 개념이 익숙해진다.
- 플레이어를 나타내는 클래스를 자바 스타일로 작성한 코틀린 코드는 아래와 같다.
```
class Player(name :String){
    private var name = String = name

    fun getName(): String {
        return name
    }

    fun setName(name : String) {
        this.name = name;
    }
}
```
- 플레이어의 이름을 알아내기 위해서는 getName() 메서드를 호출함.
- 플레이어의 이름을 바꾸려면 setName() 메서드를 호출함
- 매우 단순하긴 하지만 너무 많은 코드를 작성해야 함
- 코틀린의 this 키워드가 처음으로 등장했으니 짧게 뜻을 설명하고 넘어가면
- 다른 언어처럼 this는 해당 클래스의 현재 객체를 가리키는 참조를 담고 있다.
- 이 예제에서는 player 클래스의 인스턴스를 가리킴
- 그냥 클래스를 다음과 같이 작성하면 안되나
```
class Player {
    var name : String = ""
}
```
- 언뜻 괜찮은 방법같아 보이며 당연히 코드 양은 훨씬 줄었다.
- 플레이어 이름도 player.name 으로 간단히 알아낼 수 있다.
- 플레이어의 이름을 바꾸는 것도 훨씬 직관적임. player.name = "Alex"와 같이 하면됨
- 하지만 이런식으로 하면 객체를 통제할 수 없게됨
- 가령 Player를 불변으로 만들 수 없다.
- 모두에게 플레이어릐 이름을 읽을 권한을 주면, 동시에 이름을 수정할 권한까지갖게 됨
- 나중에 코드를 수정할 계획이라면 이건 심각한 문제가 될 수 있다.
- 이처럼 설정자를 사용할 때 통제할 수 있던 부분이 공개 필드를 사용할 떄 통제를 벗어나게 됨
- 코틀린의 속성은 이 문제를 해결해줌
```
class Player(val name: String)
```
- 주 생성자 절의 예제와 거의 똑같은 코드이지만 이제 Name 앞에 val 제한자가 생김
- 이렇게 하면 코틀린은 멤버와 동일한 이름의 접근자를 자동으로 만들어 줌
- 생성자를 사용해 속성의 값을 설정할 수 있고 이름을 사용해 접근할 수 있음
```
val player = Player("Alex")
println(player.name)
```
- 그러나 player 객테의 이름을 바꾸려 하면 오류가 발생함
```
Player.name = "Alexey" // "val cannot be reassigned" 오류 발생
```
- 속성을 값으로 선언했기 떄문에 읽기 전용이 됨
- 속성을 바꿀수 있게 하려면 가변 속성으로 선언해야 함
- 생서아에서 인자 var를 붙이면 자동으로 접근자와 설정자가 만들어짐
```
class Player(val name : String, var score: Int)
```
- 객체 생성 시에는 값을 전달할 수 없게 하려면 속성을 클래스 본문 안으로 이동시키면 됨
```
class Player(val name : String){
    var score : Int = 0
}
```
- score는 null 불가 타입이기 때문에 기본값을 설정해줘야함

### 설정자와 접근자 직접 정의하기
- 이제 플레이어의 점수를 쉽게 변경할 수 있음
- 하지만 점수가 유효하지 않은 값일 수 있음
- 예
```
player.score = -10
```
- 이처럼 가변 속성에 유효성 검사가 필요하다면 set 키워드를 사용해서 설정자를 명시적으로 정의해야함
```
class Player(val name: String){
    var score : Int = 0
        set (value) {
            field = if(value >= 0){
                value
            }else {
                0
            }
        }
}
```
- 여기서 value는 속성의 새 값이며 field는 현재 값임.
- 위의 코드에서는 새 값이 음수라면 기본 값을 사용함
- 자바좀 쳐봤으면 설정자 코드를 다음과 같이 작성하고 싶은 마음이 들 수 있음
```
set(value) {
    this.score = if(value >= 0) value else 0
}
```
- 코틀린에서 이 코드는 무한 재귀호출을 일으킴
- 가변 속성마다 설정자를 자동으로 생성하기 떄문에
- 그래서 자동으로 제공되는 field 라는 이름을 쓰는 것임
- 비슷한 방법으로 접근자도 직접 선언할 수 있음
```
class Player(name : String){
    val name= name
        get() = field.toUpperCase()
}
```
- 먼저 생성자의 인수로 받은 값을 같은 이름의 필드에 저장함.
- 그리고 이 속성의 모든 문자를 대문자로 바꿔서 반환하는 접근자를 정의함
```
println(player.name)
```

### 인터페이스
- 이미 다른 언어에서 인터페이스 개념을 많이 접했을 것이다.
- 정적 타입 언어에서 인터페이스는 어떤 클래스가 구현해야 하는 동작이 무엇인지 정의함
- 인터페이스를 정의할 때 그냥 interface 키워드를 사용
- 주사위 굴리는 인터페이스 예제
```
interface DiceRoller{
    fun rollDice(): Int
}
```
- 이 인터페이스를 구현하려면 클래스이름 디에 콜론을 붙이고 인터페이스 이름을 써줌
- 코틀린에서는 implements 키워드를 사용하지 않는다.
```
import kotlin.random.*
class Player (...) : DiceRoller
{
    ...
    fun rollDice() = Random.nextInt(0, 6)
}
```
- import 키워드도 여기서 처음 등장했음
- 이름에서 알 수 있듯 이 키워드는 코틀린 표준 라이브러리에서 kotlin.random과 같은 패키지를 불러들이는 역할을 함
- 코틀린 인터페이스는 기본 함수도 지원함.
- 만약 함수가 객체의 상태에 의존하지 않는다면 함수의 구현을 인터페이스에 작성하는 것
- 위의 예제에서 rollDice()는 단순히 0부터 5사이의 난수를 발생시키는 함수이기 떄문에 이에 해당됨
```
interface DiceRoller {
    fun rollDice () = Random.nextInt(0,6)
}
```

### 추상 클래스
- 역시 많은 사람에게 익숙한 개념안 추상 클래스는 직접 인스턴스화할 수 없다
- 반드시 다른 클래스가 먼저 추상 클래스를 상속해야 한다.
- 인터페이스와의 차이는 상태를 가진다는 것
- 게임판 위에서 플레이어를 움직일 수 있도록 하는 추상 클래스를 작성하면 아래와 같다.
```
abstract class Moveable(){
    private var x: Int = 0
    private var y: Int = 0
    fun move(x: Int, y: Int){
        this.x=x
        this.y=y
    }
}
```
- Moveable을 구현하는 클래스는 모두 move() 함수도 상속받게 될 것임
- 방금 에제에서 처음 등장한 private 키워드에 대해 더 자세히 알아보자

### 접근제한자
- 1장의 앞부분에서 private를 언급했지만 자세히 설명할 기회가 없었음
- private속성이나 함수는 선언된 클래스 안에서만 접근할 수 있음
- 위의 예제에서는 Moveable 클래스 내부에서만 x와 y에 접근할 수 있음
- 클래스와 속성은 기본적으로 public 임 따라서 public 접근 제한자는 매번 붙이지 않아도 됨
- 추상 클래스를 확장할 때는 추상 클래스 이름을 콜론 뒤에 붙이면 됨, extends는 사용하지 않음
```
class ActivePlayer(name: String) : Moveable(), DiceRoller{
    // 추상클래스와 인터페이스를 둘다 가져온것
}
```
- implements나 extends 키워드가 없다면 어떻게 추상 클래스와 인터페이스를 구분할 수 있을것인가
- 추상 클래스는 이름 뒤에 둥근 괄호를 붙여 생성자가 있음을 표시함
- 이후의 장들에서 이 문법이 어떻게 쓰이는지 살펴볼 것

### 상속
- 추상 클래스 말고 일반적인 클래스도 확장할 수 있음
- 추상 클래스를 확장했던 것과 똑같은 문법을 사용해서 player 클래스를 확장해보자
- ConfusedPlayer 클래스를 만들 텐데, 이 클래스는 (x, y) 좌표를 전달하면 (y, x)로 이동함
- 먼저 그냥 Player를 상속받는 클래스를 생성해보자
```
class ConfusedPlayer(name: String) : ActivePlayer(name)
```
- 부모 클래스 이름 뒤에 둥근 괄호를 왜 붙이는지 이제 이해할 수 있을 것이다.
- 이 괄호에 인자를 넣어서 부모 클래스의 생성자를 호출할 수 있다.
- 자바의 super 키워드와 비슷
- 놀랍게도 이 코드는 컴파일 되지 않음
- 코틀린에서 모든 클래스는 기본적으로 상속이 불가능한 최종 클래스이기 때문
- 다른 클래스가 상속받는 것을 허락하려면 클래스를 open으로 선언해야 함
```
open class ActivePlayer(...) : Moveable(), DiceRoller{
    ...
}
```
- 이제 move 메서드를 덮어써보자.
```
class ConfusePlayer(name : String) : Player(name){
    // move()는 반드시 open으로 선언돼 있어야 함
    override fun move(x: Int, y: Int){
        this.x = y // protected로 선언되어야 함
        this.y = x // protected로 선언되어야 함
    }
}
```
- 덮어 쓰기를 사용하면 특정 함수가 부모 클래스와 다르게 동작하도록 할 수 있음
- 자바에서는 @override가 선택 사항이지만 코틀린에서는 override 키워드를 반드시 붙여야 함
- 상위 타입의 메서드를 그냥 덮어 쓰는 것은 허용되지 않ㄷ기 떄문에 명시적으로 override를 사용하지 않으면 컴파일 되지 않는다.
- 이 코드에 두가지 문제가 있는데
  - 첫번째로 open으로 선언되지 않은 메서드는 덮어 쓸 수 없다.
  - 두번째로 자식 클래스에서는 플레이어의 좌표를 수정할 수 없다.
  - 좌표 속성은 둘 다 private으로 선언돼 있기 때문이다.
- protected 접근 제한자를 사용해 자식 클래스에서도 속성에 접근할 수 있도록 하고, 함수를 open으로 선언해 덮어쓸 수 있도록 하자
```
abstract class Moveable(){
    protected var x: Int = 0
    protected var y: Int = 0
    open fun move(x : Int , y: Int){
        this.x = y
        this.y = x
    }
}
```
- 이제 문제가 해결되었다. protected 키워드도 여기서 처음 등장했는데, 자바와 마찬가지로 이 접근 제한자는 속성이나 메서드가 자기 자신과 하위 클래스에서만 보이도록 함

### 데이터 클래스
- 코틀린에서는 모든 것이 생산성을 높이는 데 초점을 맞추고 있다는 것을 기억해야 함
- 자바 개발자가 가장 많이 하는 일 중 하나는 POJO(Plain Old java Object)를 만드는 것이다.(사실 잘 모르겠음)
- POJO가 생소하다면 접근자와 설정자, 그리고 equals 와 hashCode 메서드 구현만 갖고 있는 객체라고 생각하면 된다.
- POJO를 만들일이 너무 많다보니 아예걍 내장기능으로 만듬
- 이걸 데이터클래스라고 함
- 예제
```
data class User(val username: String , private val password: String)
```
- 이 코드는 2개의 접근자가 있고 설정자는 없는(모든 인수가 val로 선언돼 있는 것을 확신하라) 클래스를 생성함.
- 뿐만 아니라 equals, hashCode, clone 함수도 올바르게 정의됨
- 데이터 클래스는 코틀린에서 반복 코드 작업을 가장 획기적으로 줄여주는 기능 중 하나
- 일반적인 클래스와 똑같이 데이터 클래스도 자체 함수를 가질 수 있음
- 예제
```
data class User(val username: String, private val password: String) {
    fun hidePassword() = "*".repeat(password.length)
}
val user = User("Alexey", "abcd1234")
println(user.hidePassword())
```
- 일반적인 클래스와 비교했을 때 데이터 클래스에는 한가지 중요한 제약이 있다.
- 데이터 클래스가 항상 최종 클래스라는 것
- 다른 클래스가 데이터 클래스를 상속받는 것은 불가능함
- 그러나 equals와 hashCode를 자동으로 구현해주는 것을 생각하면 감수할 만 하다.

### 코틀린 데이터 클래스와 자바 레코드 비교
- 코틀린에서 영감을 얻어 자바 15에는 레코드라는 개념이 추가됨
- 위 예제를 자바 레코드로 표현하면 아래와 같다.
```
public record User(String username, String password) {}
```
- 두 문법이 모두 매우 간결하다. 어떤 차이가 있을까
  - 코틀린의 데이터 클래스에는 copy() 함수가 있지만 레코드에는 없음
  - 2장에서 프로토타임 디자인패턴을 다루면서 이 내용을 살펴볼 것
  - 레코드에서 모든 속성은 항상 final 임
  - 코틀린 식으로 표현하자면 레코드는 값만 지원하며 변수는 사용할 수 없다.
  - 데이터 클래스는 다른 클래스를 상속받을 수 있으나, 레코드는 불가능함
- 간단히 말해 데이터 클래스는 여러 면에서 레코드보다 낫다.
- 그러나 각각의 언어에선느 정말 좋은 기능임에는 틀림없음
- 또한 코틀린은 상호 운용성을 중시하는 언어이기 때문에 데이터 클래스에 간단한 표시만 해주면 자바에서 레코드로 사용할 수 있음
```
@JvmRecord
data class User(val username: String, val password: String)
```
