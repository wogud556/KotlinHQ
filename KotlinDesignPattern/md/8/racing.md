## 경주패턴
- 경주 디자인 패턴은 여러 작업을 동시에 실행한 뒤에 먼저 반환되는 '승자'의 결과만 사용하고 나머지 패자의 결과는 버리는 패턴임
- 코틀린에서 경주 패턴을 구현할 때 채널에 대해 select() 함수를 호출함
- 날씨 어플리케이션을 작성한다고 해본다.
- 안정성을 높이기 위해 두 곳의 날씨 정보 사이트에서 데이터를 받아올 것
- 하나는 Precise Weather 이고, 다른 하나는 Weather Today라는 곳
- 예제에서 사이트 이름 및 현재 기온을 반환하는 생산자를 구현해서 날씨 정보 사이트를 표현할 것
- 생산자가 여럿이면 모든 생산자의 채널을 구독한 후에 먼저 만들어지는 겨로가를 사용할 수 있음
- 먼저 두 곳의 날씨 정보 사이트를 선언한다.
```
fun CoroutineScope.preciseWeather() = produce {
    delay(Random.nextLong(100))
    send("Precise Weather " to "+25c")
}

fun CoroutineScope.weatherToday() = produce {
    delay(Random.nextLong(100))
    send("Weather Today" to "+24c")
}
```
- 두 생산자의 로직은 거의 동일함
- 임의의 시간 동안 기다렸다가 온도 값과 사이트 이름을 반환함
- 다음과 같이 select 식을 사용하면 두 채널을 동시에 구독할 수 있음
```
runBlocking {
    val winner = select<Pair<String, String>> {
        preciseWeather().onRecive {
            preciseWeatherResult -> preciseWeatherResult
        }
        weatherToday().onReceive { weatherTodayResult -> weatherTodayResult }
    }
    println(winner)
}
```
- onReceive() 함수를 이용해서 여러 채널을 동시에 구독할 수 있음
- 이 코드를 여러번 실행하면 (Precise Weather, +25c)와 (Weather Today, +24c) 중 하나가 무작위로 출력될 것.
- 두 사이트가 '승리'할 확률은 동일하기 때문
- 경주 패턴은 반응성을 위해 자원을 희생하고자 할 때 유용하게 사용할 수 있는 개념임
- 구현에는 코틀린의 select 식을 사용
- 이번에는 select 식을 사용한 다른 동시성 디자인 패턴을 배우기 위해 select 식을 조금 더 깊이 알아본다

### 비편향 select
- select 구문을 사용할 때는 순서가 중요함
- 기본적으로 select는 편향적이기 때문
- 만약 두 이벤트가 동시에 발생하면 select는 첫 번째 구문을 선택함
- 생산자를 하나만 만들 것. 이 생산자는 다음에 시청해야 할 영화 제목을 채널로 보냄
```
fun CoroutineScope.fastProducer(
    movieName: String
) = produce(capacity = 1) {
    send(movieName)
}
```
- 채널에 0이 아닌 capacity 를 설정했기 때문에 코루틴이 실해오디면 곧바로 값을 얻을 수 있을 것
- 이제 이 생산자를 2개 시작하고 select 식을 사용하여 어던 영화가 선택되는지 본다
```
runBlocking {
    val firstOption = fastProducer("분노의 질주 7")
    val secondOption = fastProducer("리벤저")

    delay(10)
    val movie To Watch = select <String> {
        firstOption.onReceive{ it }
        secondOption.onReceive { it }
    }
    println(movieToWatch)
}
```
- 이 코드는 아무리 실행해도 <분노의 질주 7이 항상 이김>
- select 구문은 두 값이 동시에 준비되는 경우 항상 둘 중 첫번째 채널을 선택하기 때문
- 이번에는 select대신 selectUnbiased구문을 사용해본다

```
val moveToWatch = selectUnbiased<String> {
    firstOption.onReceive{ it }
    secondOption.onReceive{ it }
}
```
- 이 코드를 실행하면 <분노의 질주 7>이 출력될 때도 있고 <리벤저>가 출력될 때도 있을 것
- 그냥 select 구문과는 달리 selectUnbiased는 순서를 무시함.
- 여러 결과가 동시에 준비되면 임의로 하나를 택함
