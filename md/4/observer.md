## 관찰자 패턴
- 관찰자 패턴은 4장의 하이라이트 가운데 하나임
- 또한 함수형 프로그래밍을 다루는 이후 장들로 넘어가기 위한 가교 역할을 해줄 것
- 관찰자 패턴이란 무엇인가?
- 먼저 발행자가 있고 이를 구독하는 여러 구독자가 있다
- 각각 주제와 관찰자 라고 부르기도 함
- 발행자에게 어던일이 발생하면 모든 구독자가 그 사실을 알게됨
- 중개인 패턴처럼 보일 수 있지만 조금 다름
- 구독자는 런타임에 구독 및 구독 취소가 가능하다
- 전통적으로는 모든 구독자가 특정 이벤트를 나타내는 인터페이스를 구현하면 발행자가 이벤트를 발생했음을 알리기 위해 이 인터페이스를 사용하는 방식으로 구현함
- 하지만 코틀린에는 고차 함수가 있기 때문에 더 간단히 구현할 수 있음
- 발행자는 여전히 구독 및 구독 취소를 할 수 있는 창구를 마련해야 함


### 예제 동물 합창단
- 동물들이 모여 합창단을 만들기로 함
- 고양이가 지휘자로 뽑힘
- 문제는 동물들이 자바 세상을 탈출했기 때문에 공통의 인터페이스를 갖고 있지 않다는 점
- 대신에 모두가 울음소리를 내는 제각각의 방법을 갖고 있음
```
class Bat{
    fun screech() {
        println("아----")
    }
}
class Turkey {
    fun gobble() {
        println("꾸륵꾸륵")
    }
}
class Dog {
    fun bark(){
        println("멍멍")
    }
    fun howl() {
        println("아우")
    }
}
```
- 다행히 고양이가 그저 노래를 못해서 지휘자로 뽑힌것은 아님
- 고양이는 이 책 내용을 잘 따라올 만큼 똑똑하기도 함
- 그래서 코틀린 세계에서는 함수를 받은 것도 가능하다는 사실을 이해하고 있음
```
class Cat{
    fun joinChior(whatToCall: () -> Unit) {
        ...
    }
    fun leaveChori(whatNotToCall: () -> Unit){
        ...
    }
}
```
- 지금까지는 인수로 새로운 함수나 함수 리터럴을 전달하는 법을 배움
- 그렇다면 멤버 함수에 대한 참조를 전달하려면 어떻게 해야 하나
- 이를 위해서 전략 패턴에서 사용한 방법, 즉 메서드 참조 연산자를 사용함
```
val catTheConductor = Cat()

val bat = Bat()
val dog = Dog()
val turkey = Turkey()

catTheConductor.joinChoir(bat::screech)
catTheConductor.joinChoir(dog::howl)
catTheConductor.joinChoir(dog::bark)
catTheConductor.joinChoir(turkey::gobble)
```
- 고양이는 이제 모든 구독자를 어떻게든 저장해야 함
- 맵을 사용하면 될 것 같음
- 그럼 무엇이 키가 돼야 할까

```
class Cat{
    private val participants = mutableMapOf<()->Unit, ()->Unit>

    fun joinChoir(whatToCall: ()->Unit){
        participants[whatToCall] = whatToCall
    }
}
```
- ()->Unit이 너무 많이 등장해서 어지럽다면 typealias를 통해 더 의미 있는 이름을 지어 줄 수 있다는 점을 기억하라(가령 subscriber라는 별칭을 붙일 수 있음)
- 방금 박쥐가 합창단을 탈퇴하기로 했음
- 아쉽게도 그의 아름다운 노랫소리를 이제 들을 수 없게 됐음

```
class Cat{
    fun leaveChoir(whatNotToCall: ()->Unit){
        participants.remove(whatNotToCall)
    }
}
```
- 박쥐가 해야할일은 다음과 같이 구독할 때 전달했던 함수를 다시 전달하는 것 뿐
```
catTheConductor.leaveChoir(bat::screech)
```
- 이게 바로 맵을 사용한 이유.
- 이제 고양이는 모든 합창단원을 모아 노래를 시킬 수 있음
```
typealias Times = Tin
class Cat {
    ...
    fun conduct(n: Times){
        for(p in participants.values){
            for(i in 1..n){
                p()
            }
        }
    }
}
```
- 리허설이 잘 끝났음
- 그런데 고양이가 모든 반복문을 수행하기는 너무 벅차보임
- 그래서 합창단원들에게 역할을 위임하려고 함
- 그건 식은죽 먹기이다.
```
class Cat{
    private val participants = mutableMapOf<(Int) -> Unit

    fun joinChoir(whatToCall: (Int)->Unit) {
        ...
    }

    fun leaveChoir(whatNotToCall: (Int)->Unit) {
        ...
    }

    fun conduct(n: Times){
        for(p in participants.values) {
            p(n)
        }
    }
}
```
- 합창단원들은 새로운 인수를 받기 위해 함수를 조금 수정해야함
- 예를 들어 칠면조는 다음과 같이 함수를 쉊ㅇ한다
```
class Turkey {
    fun gobble(repeat: Times){
        for(i in 1..repeat) {
            println("꾸륵꾸륵")
        }
    }
}
```
- 그런데 약간 문제가 있음
- 만약 고양이가 단원들에게 어떤 소리(높은 소리 또는 낮은 소리)를 낼지 지시해야 한다면? 다시 모든 구독자 함수와 고양이의 구현을 변경해야 할 것임
- 발행자를 설계할 때는 여러 속성을 갖는 하나의 데이터 클래스를 전달하도록 해라
- 그렇게 하지 않고 여러개의 자료를 전달하면 새 속성이 추가될 때 마다 구독자의 코드를 모두 고쳐야 할 것임
```
enum class SoundPitch{HIGH, LOW}
data class Message(val repeat: Times, val pitch:SoundPitch)

class Bat {
    fun screech(message: Message) {
        for(i in 1..message.repeat) {
            println("${message.pitch} 이----")
        }
    }
}
```
- 여기서는 울음소리의 높낮이를 나타내기 위해 enum을 사용하고, 높낮이와 반복 횟수를 캡슐화하기 위해 데이터 클래스를 사용함
- 전달하는 메시지 객체는 반드시 불변 객체로 만들라
- 그렇게 하지 않으면 이상한 동작을 보게 될 것
- 같은 발행자가 여러 종류의 메시지를 발행하려면 어떻게 하나?
- 이때는 스마트 캐스팅을 사용할 수 있음
```
interface Message {
    val repeat: Times
    val pitch: SoundPitch
}

data class LowMessage(override val repeat: Times): Message {
    override val pitch = SoundPitch.LOW
}
```