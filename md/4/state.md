## 상태 패턴
- 상태 디자인 패턴은 4장에서 다뤘던 전략 패턴의 일종이라고 생각할 수 있음
- 다만 전략 패턴에서는 외부의 클라이언트가 전략을 교체하는 반면, 상태 패턴에서의 상태는 오로지 입력에 의해 내부적으로 변경됨
- 클라이언트가 전략 패턴을 사용하면서 나눈 대화는 아래와 같다
  - 클라이언트 : 여기 새로운 할일이 있다. 이제부터 이 일을 해라
  - 전략 : 알겠다
  - 클라이언트 : 늘 군말없이 따라 줘서 마음에 든다.
- 하지만 아래의 대화는 좀 다르다
  - 클라이언트 : 새로운 입력을 가져왔다
  - 상태 : 글쎄 동작을 바꿀수도 있고 아닐수도 있고
- 사용자는 심지어 어떤 입력이 거부될 수 있다는 것까지 염두해 둬야 함
  - 클라이언트 : 이 입력을 잘 처리해보라
  - 상태 : 무슨 말인지 모르겠다 바쁘니까 건들지 말라
- 대체 클라이언트가 이런 상태 패턴을 사용해야 할 이유는 무엇일까 

### 상태 패턴의 50가지 그림자
- 2차원 게임 예시로 돌아간다
- 육식성 달팽이는 이제 너무 많이 당한 듯 함
- 계속 콩과 바나나를 쏴 대자 새로운 던전이 펼쳐짐, 여기서는 달팽이가 움직임
- 객체가 동작을 바꿔야 할 때 상태 패턴이 어떤 도움이 되는지 알아본다
- 기본적으로 달팽이는 가만히 서 잇으면 달팽이 에너지를 모음 그러다가 주인공이 가까이 오면 주인공에게 공격적으로 달려듬
- 주인공이 달팽이에게 데미지를 입히면 달팽이는 뒤로 물러나서 상처를 핥음(?)
- 그리고 다시 공격하기를 어느 한쪽이 죽을 때까지 반복함
- 먼저 달팽이의 생애 동안 일어나는 일을 다음과 같이 선언한다.
```
interface WhatCanHappen{
    fun seeHero()
    fun getHit(pointsOfDamage:Int)
    fun calmAgain()
}
```
- 달팽이 객체는 이 인터페이스를 구현함으로 자신에게 발생하는 이벤트를 인식하고 이에 반응한다.
```
class Snail: WhatCanHappen{
    private var healthPoint = 10

    override fun seeHero(){

    }
    override fun getHit(pointsOfDamage: Int){

    }
    override fun calmAgain(){

    }
}
```
- 이제 달팽이의 상태를 나타내는 Mood 클래스를 선언해본다.
- 이 클래스는 sealed키워드를 붙여서 봉인 클래스로 만들 것
```
sealed class Mood{
    // 여기에 다양한 추상 메서드를 선언함
}
```
- 봉인 클래스는 추상 클래스이면서 인스턴스화가 불가능함
- 왜 이런 클래스를 사용하는지는 곧 알게됨
- 먼저 여러 상태를 선언해본다
```
object Still : Mood()
object Aggressive : Mood()
object Retreating : Mood()
object Dead : Mood()
```
- 각각 달팽이의 상태(또는 기분)를 나타냄
- 상태 패턴에서 snail 클래스는 맥락의 역할을 나타냄
- 즉 Snail클래스가 현재 상태를 저장한다는 뜻
- 따라서 Snail 클래스에 다음과 같이 상태를 저장하기 위한 멤버를 선언함
```
class Snail: WhatCanHappen{
    private var mood : Mood = Still
    // 이전 코드와 동일
}
```
- 이제 달팽이가 주인공 캐릭터를 봤을 때 어떤 행동을 하는지 정의한다
```
override fun seeHero(){
    mood = when(mood){
        is Still -> Aggressive
    }
}
```
- 이 코드는 컴파일 되지 않는다.
- Mood가 봉인 클래스로 선언됐기 때문
- 코틀린은 마치 enum의 원소가 몇개인지 알고 있는 것처럼 봉인 클래스를 상속하는 클래스가 몇개 인지 알고 있음
- 따라서 when에는 반드시 모든 경우가 나열돼야 함

> IDE로 Intellij를 사용한다면 Add remaning branches라는 메시지가 자동으로 뜨며 모든 경우의 수를 추가하도록 유도할 것
- 나머지 경우에 대해서는 else를 사용해서 상태 변화가 없음을 나타낼 수 있음\
```
override fun seeHero(){
    mood = when(mood) {
        is Still -> Aggressive
        else -> mood
    }
}
```
- 달팽이가 데미지를 입었을 때 죽었는지 검사할 필요가 있음
- 이때도 when을 사용하되 인자를 전달하지 않는다.
```
override fun getHit(pointsOfDamage: Int) {
    healthPoints -= pointsOfDamage

    mood = when {
        (healthPoint <= 0) -> Dead
        mood is Aggressive -> Retreating
        else -> mood
    }
}
```
- 여기서 사용한 is 키워드는 자바의 instanceof와 같은 역할을 하지만 훨씬 더 간결함

### 큰 규모의 상태 패턴
- 앞서 설명한 방법으로 필요한 로직은 거의 다 구현할 수 있음
- 그러나 필요한 로직의 규모가 커지면 다른 접근 방법을 사용하는 경우도 있음
- 그런 경우에는 snail 객체가 더 간결해짐
```
class Snail {
    internal val mood : Mood = Still(this)

    private var healthPoints = 10
    // 이게 전부임
}
```
- mood를 internal 로 선언했다는 점에서 주목하라.
- 이렇게 하면 같은 패키지에 있는 다른 클래스가 이 변수를 변경할 수 있음
- 이번에는 snail 클래스를 대신해서 Mood클래스가 WhatCanHappen을 구현할 것
```
sealed class Mood : WhatCanHappen
```
- 이제 상태 전환 로직은 상태 객체 안에 있음
```
class Still(private val snail: Snail) : Mood(){
    override fun seeHero(){
        snail.mood = Aggressive
    }
    override fun getHit(pointsOfDamage: Int) {
        // 이전과 동일한 로직
    }
    override fun calmAgain(){
        // Still 상태로 되돌아감
    }
}
```
- 이제 상태 객체가 생성자에게 맥락 객체(Snail)를 받는다는 점에 주목
- 상대적으로 작성해야 하는 코드 양이 적다면 첫번째 방법을 사용해라
- 상태 변화가 많다면 두번째 방법을 사용하라
- 실제 코드에서 상태 패턴이 널리 사용되는 한가지 예는 코틀린의 코루틴임
- 코루틴에 대해서는 5장에서 다룰 것