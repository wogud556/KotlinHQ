## 시야 지정 함수
- 코틀린에는 시야 지정 함수라는 개념이 있다.
- 시야 지정 함수는 모든 객체에 대해 사용 가능하며, 이를 사용하면 반복적인 코드를 줄일 수 있음
- 다른 장점도 많이 있지만, 특히 단일심 함수를 간단하게 작성할 때 굉장히 도움이 됨
- 시야 지정 함수는 람다식을 인자로 받기 때문에 일곶의 고차 함수
- 이 절에서는 모든 시야 지정 함수의 의미와 함께 객체를 시야 삼아 코드 블록을 실행하는 예제를 살펴볼 것
- 절 전반에서 '시야'와 맥락 객체는 같은 의미로 사용되며 시야 지정 함수가 동작하는 객체를 가리킴

### let함수
- let은 null 가능 객체에 대해 호출할 수 있음
- 하지만 그 객체가 null이 아닐 때만 함수가 실행됨
- 예를 들어 다음과 같이 영화의 유명한 대사를 맵에 저장해 뒀다고 한다
```
val clientEastwoodQuotes = mapOf(
    "석양의 무법자" to "모든 총은 제각기 쓰는 법이 잇지.",
    "황야의 무법자" to "제가 실수 했네요. 관짝 4개요"
)
```
- 이제 영화 이름으로 이 맴에 있는 대사를 하나 골라서 출력할 것
- 다만 맵에 없는 영화일 수도 있기 때문에 대사가 null인지 검사해야 함
```
val quote = clientEastwoodQuotes["용서받지 못한 자"]
if (quote != null) {
    println(quote)
}
```
- 다음과 같이 let 시야 지정 함수를 사용해서 이 코드를 다시 작성할 수 있음.
```
clintEastwoodQuotes["용서받지 못한 자"]?.let {
    println(it)
}
```
- 흔히 저지르는 실수 중 하나는 let앞에 안전 탐색 연산자를 빠뜨리는 것
- let() 자체는 Null에 대해서도 동작하기 때문에 다음 코드에서도 오류가 발생하지는 않는다.
```
clintEastwoodQuotes["용서받지 못한 자"].let {
    println(it)
}
```
- 다만 콘솔에 Null이 출력될 것
- 따라서 null 검사를 위해 let()을 사용하는 경우에는 물음표를 잊으면 안됨

### apply함수
- apply()는 앞에서 이미 살펴본 바 있음
- 이 함수는 블록 내에서 실행 대상 객체를 this로 받으며, 실행 후에는 이 객체를 반환함
- 특히 가변 객체를 초기화 할 때 apply()를 유용하게 쓸 수 있음
- 인수 없는 생성자를 호출한 다음 수많은 설정자를 부르면서 필드르 초기화해야 했던 경험을 떠올려보라.
- 예를 들어 다음 코드를 본다. 어떤 라이브러리에서 제공하는 클래스라고 생각해 본다
```
class JamesBond {
    lateinit var name: String
    lateinit var movie: String
    lateinit var alsoStarring: String
        
}
```
- 이런 클래스의 인스턴스가 필요할 때 다음과 같은 절차적 코드를 작성할 수 있음
```
val agent = JamesBond()
agent.name = "션 코너리"
agent.movie = "007 살인번호"
```
- 이런 코드 대신 apply() 함수를 써서 name과 movie만 설정하고 alsoStarring은 비워둘 수도 있음
```
val `007` = JamesBond().apply {
    this.name = "션 코너리"
    this.movie = "007 살인번호"
}

println(`007`.name)
```
- 여기서는 This가 맥락 객체이기 때문에 this 를 생략해서 다음과 같이 간단하게 작성할 수 있다.
```
val `007` = JamesBond().apply {
    name = "션 코너리"
    movie = "007 살인번호"
}
```
- 자바 클래스는 빈 기본 생성자와 수많은 설정자 함수를 가질 때가 많음
- 이런 클래스를 다룰 때 특히 apply() 함수가 도움이 됨

### also 함수
- 이 절의 도입부에서 단일 식 함수의 아름다움과 간결함에 대해 이야기 했음
- 두 수를 곱하는 간단한 함수를 알아본다
```
fun multiply(a: Int, b: Int) : Int = a * b
```
- 하지만 종종 로그 출력과 같이 다른 부수 효과가 필요할 때가 있음
- 이를 위해서는 다음과 같이 구현해야 할 것
```
fun multiply(a: Int, b: Int): Int {
    val c = a * b
    println(c)
    return c
}
```
- 코드가 더 길어졌을 뿐만 아니라 새로운 변수까지 생김.
- 대신 also()함수를 사용하면 다음과 같이 구현할 수 있음
```
fun multiply(a: Int, b: Int): Int = 
    (a * b).also { println(it)}
```
- 이 함수는 식의 결괏값을 it에 할당해서 블록을 실행한 뒤에 그 값을 반환함
- .also() 함수는 연쇄 호출 도중에 부수 효과를 일으키고자 할 때 유용함
```
val l = (1..100).toList()
l.filter{ it % 2 == 0}
    .also{ println(it)}
    .map { it * it }
```
- also()를 호출해서 리스트의 각 원소를 출력한 뒤에도 계속 map()을 호출해서 연쇄 호출을 이어갈 수 있는 것을 볼 수 있음

### run 함수
- run()함수는 let()과 굉장히 비슷함
- 다만 맥락 객체를 it 대신 this에 할당한다는 차이가 있음
- 다음의 코드를 보면서 살펴본다.
```
val justAString = "String"
val n = justAString.run {
    this.length
}
```
- 이 예제에서는 this는 justAString 변수를 가리킴
- 일반적으로 this는 생략할 수 있음
- 따라서 이 코드는 다음과 같이 작성할 수도 있음
```
val n = justAString.run {
    length
}
```
- run() 함수는 apply() 함수와 함께 객체 추기화에 유용하게 쓸 수 있음
- 하지만 apply()함수가 맥락 객체를 반환하는 것과는 달리 run()은 블록의 계산 결과를 반환함
```
val lowerCaseName = JamesBond().run {
    name = "ROGER MOORE"
    movie = "007 황금총을 가진 사나이"
    name.toLowerCase() // <= JamesBond 타입이 아님
}
println(lowerCaseName)
```
- 이 코드의 출력은 다음과 같음
```
> roger moore
```
- 여기서 객체의 이름은 "ROGER MOORE"로 초기화됨
- run()함수는 JamesBond 객체를 맥락으로 해서 실행되지만 반환되는 값은 String 타입이라는 점을 유의할 것

### with 함수
- 다른 시야 지정 함수와 달리 with()는 확장 함수가 아님
- 즉 다음과 같은 코드는 불가능함
```
"scope".with {...}
```
- 대신 with는 맥락 객체를 인수로 받음

```
with("scope") {
    println(this.length) // "this"는 with()의 인수로 전달된 객체를 가리킴
}
```
- 다른 곳에서와 마찬가지로 this는 생략할 수 있음

```
with("scope") {
    length
}
```
- run()이나 let()처럼 with()도 어떤 값을 반환할 수 있음
- 이 절에는 여러 시야 지정 함수를 통해 객체에 대해 실행되는 코드 블록을 정의함으로써 불필요한 코드 반복을 줄이는 방법을 배움
- 다음 절에서는 코틀린에서는 왜 다른 언어만큼 인스턴스 검사가 팔요하지 않은지 알아 볼 것