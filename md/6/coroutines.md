## 코루틴
- 코틀린은 자바의 스레드 모델에 대해 코루틴 모델을 제공함
- 코루틴은 가벼운 스레드라고 생각하면 됨
- 코루틴이 스레드에 비해 어떤 장점을 갖는지는 곧 살펴볼 것
- 먼저 알아야 할 점은 코루틴이 언어의 내장 기능이 아니라는 것, 단지 젯브레인스에서 제공하는 하나의 라이브러리일 뿐
- 따라서 코루틴을 사용하려면 그래들 설정 파일(gradle.kts)에 이를 명시해줘야함
```
dependencies {
    ...
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1")
}
```
- 코루틴 최신 라이브러리를 사용할 것

### 코루틴 시작하기
- 코틀린에서 새로운 스레드를 시작하는 방법은 6장의 '스레드 심화' 절에서 살펴봄
- 이제 새로운 코루틴을 시작한다
- 스레드에서 사용했던 것과 거의 같은 예제를 작성할 것
- 각 코루틴은 어떤 변수의 값을 증가 시킨 뒤 IO작업을 모사하기 위해 잠깐 잠들었다가 깨어나서 다시 변수의 값을 증가시킴
```
val latch = CountDownLatch(10_000)
val c = AtomicInteger()
val start = System.currentTimeMillis()
for (i in 1..10_000) {
    GlobalScope.launch {
        c.incrementAndGet()
        delay(100)
        c.incrementAndGet()
        latch.countDown()
    }
}
latch.await(10, TimeUnit.SECONDS)

println("${c.get()/2}개의 코루틴을 ${System.currentTimeMillis() - start} 밀리초 동안 수행")
```
#### 코루틴 사용법
- 코루틴을 시작하는 첫 번째 방법은 launch()함수를 사용하는 것
- 다시한번 강조하지만 이건 코틀린의 문법이 아니라 하나의 함수일 뿐이다
- 이 코드에서 눈여겨 봐야 하는 또 하나는 delay() 함수 호출임
- 데이터베이스나 네트워크 위치에서 자료를 가져오는 것과 같은 IO 작업을 모사하기 위해 delay() 함수를 사용함
- Thread.sleep() 함수와 마찬가지로 delay()도 현재 코루틴을 잠들게 함
- 그러나 Thread.sleep()과 다른 점이 있는데, 코루틴이 잠들어 있는 동안 다른 코루틴은 정상적으로 실행 될 수 있다는 것임
- 이건 delay() 함수에 suspend 키워드가 붙어 있기 때문임
- 이에 관해서는 다음 절에 자세히 설명함
- 이 코드를 실행하면 200밀리초 남짓 밖에 걸리지 않는 것을 볼 수 있음
- 스레드를 사용하면 20초가 걸리거나 메모리 초과 오류가 발생했을 것
- 코드도 별로 많이 고치지 않암ㅆ음
- 코루틴이 높은 동시성을 제공하는 덕
- 코루틴을 일시 중단하더라도 그 스레드는 멈추지 않는다.
- 스레드가 멈추지 않는다는 것은 엄청난 장점임
- 값비싼 OS스레드를 덜 쓰고도 더 많은 일을 할 수 있기 때문임

#### 코루틴 사용시 주의사항
- 해당 코드를 Intellij에서 사용시 GlobalScope가 주의해야 하는 API로 표시되는것을 볼 수 있음
- GlobalScope의 내부 동작을 잘 이해하지 못한다면 실제 프로젝트에서는 사용하면 안된다는 뜻
- 함부로 사용하면 원하지 않는 자원 누출이 발생할 수 있음
- 6장 뒷부분에서 코루틴을 시작하는 더 좋은 방법을 배운다
- 코루틴이 스레드보다 더 높은 동시성을 제공하기는 하지만 그렇다고 마법이 숨어있는 것은 아님
- 이제 코루틴을 시작하는 다른 방법을 알아보고 코루틴을 사용하더라도 여전히 발생하는 문제에 어떤 것이 있는지 본다

#### 코루틴 사용시 발생하는 문제
- launch()함수는 아무것도 반환하지 않는 코루틴을 시작함. 반면에 async() 함수는 어떤 값을 반환하는 코루틴을 시작함
- launch() 함수 호출은 반환형이 Unit인 함수를 호출하는 것과 같음
- 그러나 대부분의 함수는 어떤 결과를 반환함
- 그래서 async() 함수가 존재함
- 코루틴을 띄우는 것은 똑같지만, 작업 대신 Deffered<T>를 반환함
- 이때 T는 나중에 얻게 될 반환값의 타입을 나타냄
- 예를들어 다음 함수는 UUID를 비동기적으로 생성해 반환하는 코루틴을 시작
```
fun fastUuidAsync() = GlobalScope.async {
    UUID.randomUUID()
}
println(fastUuidAsync())
```
- main 메서드에서 다음 함수를 실행하면 예상한 결과가 출력되지 않음
- 즉 UUID가 아니라 다음과 같은 문구가 출력됨
```
> DeferredCoroutine{Active}
```
- 코루틴이 반환한 객체를 작업이라고 부름. 이것이 무엇이며 어떻게 사용해야 하는지 자세히 알아본다

### 작업
- 비동기적으로 어떤 일을 시작헀을 때 그 결과를 작업이라고 부름
- 마치 Thread 객체가 실제 OS스레드를 표현하는 것과 같이 Job객체는 실제 코루틴을 나타냄
- 이 예제 코드의 결과는 Job 이었던 것임
```
val job: Job = fastUuidAsync()
println(job)
```
- 작업의 생애 주기는 단순함
- 다음의 네가지 상태를 가질 수 있음
  - 신규 : 생성됐으나 시작되지 않음
  - 활성 : launch() 함수 등에 의해 시작됨, 기본 상태
  - 완료 : 모든 것이 순조롭게 진행됨
  - 취소 : 무언가 잘못됨
- 자식이 있는 작업은 다음과 같이 두가지 상태를 추가로 갖음
  - 완료 중 : 완료하기 전에 자식이 실행되기를 기다리는 중
  - 취소 중 : 취소하기 전 자식이 실행되기를 기다리는 중
- 작업의 부모 자식 관계에 대해 자세히 알고 싶다면 6장의 구조화된 동시성 절을 읽음 됨
- 앞의 예제에서 출력된 작업은 활성 상태에 있었음
- 즉 아직 UUID 생성을 마치지 못한 상태임
- 값을 갖는 작업을 Deferred라고 함
```
val job : Deferred<UUID> = fastUuidAsync()
```
- Deferred에 대해서는 8장에서 더 자세히 다룰 것
- 작업이 완료되기를 기다려서 실제 반환값을 가져오려면 await() 함수를 사용함
```
val job: Deferred<UUID> = fastUuidAsync()
println(job.await())
```
- 그러나 이 코드는 다음과 같은 오류 메시지를 내며 컴파일 되지 않는다.
```
> Suspend function 'await' should be called only from a coroutine or another suspend function
```
- 메시지에서 알 수 있듯 main() 함수가 suspend 함수도 코루틴도 아니기 때문에 오류가 발생함
- 다음과 같이 runBlocking 함수로 코드를 감싸면 문제가 해결됨
```
runBlocking{
    val job: Deferred<UUID> = fastUuidAsync()
    println(job.await())
}
```
- runBlocking함수는 모든 코루틴이 끝날 때 까지 메인 스레드를 중지시킴
- 이 함수는 4장에서 배운 브리지 패턴을 구현하고 있는데, 일반 코드와 코루틴을 사용하는 코드 사이에 다리를 좋아줌
- 이 코드를 실행하면 드디어 기대했던 대로 임의의 UUID가 출력됨

### 코루틴의 내부동작 이해하기
- 지금까지 다음의 사실은 수 차례 언급함
  - 코루틴은 가벼운 스레드와도 같음
    - 스레드에 비해 적은 자원을 사용하기 때문에 더 많이 생성할 수 있음
  - 코루틴은 스레드를 통째로 멈추지 않고 자기 자신만 실행을 중단할 수 있음
    - 그동안 스레드는 다른 코드를 실행할 수 있음
  - 그런데 코루틴은 어떻게 동작하는건가
  - 예를들어 설명하기 위해 사용자의 프로필을 만들어 내는 다음의 함수를 살펴본다
```
fun profileBlocking(id : String) : Profile {
    //1초 소요
    val bio = fetchBioOverHttpBlocking(id)
    //100밀리초 소요
    val picture = fetchPicture FromDBBlocking(id)
    //500밀리초 소요
    val friends = fetchFriendsFromDBBlocking(id)
    return Profile(bio, picture, friends)
}
```
- 이 함수가 완료되는 데에는 약 1.6초 정도 걸림
- 모든 작업이 순차적으로 이뤄지기 떄문에 이 함수를 실행하는 스레드는 계속 멈춰 있을 것
- 이 함수를 코루틴을 이용하여 다시 작성하면 다음과 같다
```
suspend fun profile(id: String): Profile {
    //1초 소요
    val bio = fetchBioOverHttpAsync(id)
    //100밀리초 소요
    val picture = fetchPictureFromDBAsync(id)
    // 500밀리초 소요
    val friends = fetchFriendsFromDBAsync(id)
    return Profile(bio.await(), picture.await(), friends.await())
}
```
- suspend키워드를 붙이지 않으면 이 비동기 함수는 컴파일이 되지 않음
- suspend키워드가 무슨 의미인지는 다음 절에서 설명할 것
- 각각의 비동기 함수가 어떻게 생겼는지 알아보기 위해 하나만 예를 든다
```
fun fetchFriendsFromDBAsync(id: String) = GlobalScope.async {
    delay(500)
    emptyList<String>
}
```
- 이제 스레드 전체가 멈추도록 구현한 순차 실행 버전과 코루틴을 사용한 버전의 성능을 비교한다
- 앞서 했던 것처럼 각 함수를 runBlocking 함수로 감싼 뒤 measureTimeMillis를 사용해 소요시간을 잴 것
```
runBlocking {
    val t1 = measureTimeMillis {
        blockingProfile("123")
    }
    val t2 = measureTImeMillis {
        profile("123")
    }
    println("순차. 실행 버전: $t1")
    println("코루틴 버전: $t2")
}
```
- 출력은 다음과 같은 것
```
> 순차 실행 버전: 1623
> 코루틴 버전: 1021
```
- 동시에 실행되는 여러 코루틴의 총 실행 시간은 가장 긴 코루틴의 실행 시간과 같은 것을 확인할 수 있음
- 반면 순차적으로 실행하는 경우엔 모든 함수의 실행 시간을 더한 것이 전체 실행 시간이 됨
- 각 함수에 suspend 키워드를 붙여서 일시 중단 함수를 만들 것
```
suspend fun fetchFriendsFromDB(id: String): List<String> {
    delay(500)
    return emptyList()
}
```
- 이 예제를 실행해보면 순차 실행버전과 비슷한 시간이 걸릴 것
- 그렇다면 왜 일시 중단 함수를 사용하나
- 일시 중단 함수는 스레드를 멈추지 않음
- 함수 하나의 성능은 달라지지 않지만 코틀린이 일시 중단 함수로 표시된 코드를 똑똑하게 재작성해주는 덕
- 코틀린 컴파일러는 suspend 키워드를 발견하면 함수를 쪼개서 재작성함
- 재작서된 코드는 다음과 같이 생김
```
fun profile(state: Int, id: String, context: ArrayList<Any>): Profile [
    when(state) {
        0 -> {
            context += fetchBioOverHttp(id)
            profile(1,id,context)
        }
        1 -> {
            context += fetchPictureFromDB(id)
            profile(2, id, context)
        }
        2 -> {
            context += fetchPictureFromDB(id)
            profile(3, id, context)            
        }
        3 -> {
            val (bio, picture, friends) = context
            return Profile(bio, picture, friends)   
        }
    }
]
```
- 코틀린이 재작성한 코드는 주어진 함수를 여러 단계로 쪼개기 위해 4장에서 배웠던 상태 패턴을 사용함
- 이를 통해 상태 계기의 각 단계를 실행할 때 코루틴을 실행하는 스레드 자원을 놓아줄 수 있다

> 위의 예시가 만들어진 코드를 완벽하게 설명하고 있는 것은 아님
> 단지 코틀린 컴파일러가 뒤에서 어떤 일을 하고 있는지 보여 주는 것이 목적이며, 간결한 설명을 위해 복잡한 세부사항은 생략함

- 앞서 작성한 비동기 코드와는 달리 상태 기계 자체는 순차적이며 모든 단계를 수행하는 데에 순차 코드와 동일한 시간이 걸린다는 점을 기억할것
- 다만 이 예제에서 중요한 부분은 어떤 단계도 스레드를 멈추지 않는다는 것

### 코루틴 취소하기
- 자바 개발자라면 스레드를 주잊하는 일이 생각처럼 쉽지 않는다는걸 알것
- 예를 들어 Thread.stop()는 사용이 권고되지 않는 메서드임
- Thread.interrupt()함수도 있으나, 모든 스레드가 이 플래그를 검사하는 것은 아님
- volatile플래그를 사용하는 것도 권고되는 방법 중 하나지만 여간 성가신 일이 아님
- 스레드 풀을 사용한다면 Future 객체를 얻을 것이고, 여기에는 cancel(boolean mayInterruptIfRunning) 메서드가 있음
- 코틀린에서는 launch 함수가 작업을 반환함
- 이 작업은 취소할 수 있음. 하지만 이전 예제에서와 동일한 규칙이 적용됨
- 코루틴이 다른 일시 중단 함수를 호출하거나 yield 함수를 호출하지 않는 이상 cancel()은 무시될 것
- 이를 확인하기 위해 다음과 같이 반복문 내에서 yield()를 호출하는 코루틴을 만들어본다.

```
val cancellable = GlobalScope.launch {
    try {
        for(i in 1..10_000) {
            println("취소 가능")
            if(i % 100 == 0) {
                yield()
            }
        }
    }
    catch ( e: CancellationException) {
        e.printStackTrace()
    }
}
```
- 보다시피 이 코루틴은 print문을 100번 호출할 때마다 곧이어 yield 함수를 호출함
- 그리고 이 코루틴은취소하면 호출 스택이 출력될 것
- 이번에는 yield를 호출하지 않는 코루틴을 작성해본다
```
val notCancellable = GlobalScope.launch {
    for (i in 1..10_000) {
        if (i % 100 == 0) {
            println("취소 불가능")
        }
    }
}
```
- 이 코루틴은 yield를 호출하지 않는다. 또한 콘솔이 너무 많은 메시지가 출력되는 것을 막기 위해 100번에 한 번씩만 메시지를 출력함
- 두 코루틴을 취소해보자.
```
println("취소 가능 코루틴을 취소 중")
cancellable.cancel()
println("취소 불가능 코루틴을 취소 중")
notCancellable.cancel()
```
- 그리고 다음과 같이 결과를 기다림
```
cancellable.join()
notCancellable.join()
```
- join()을 호출하면 코루틴 실행이 완료되길 기다릴 수 있음
- 이제 위 코드의 출력을 살펴본다
```
> 취소 가능 코루틴을 취소 중
> 취소 불가능 100
> 취소 가능: 1
...
> 취소 불가능 코루틴을 취소 중
...
> 취소 가능: 100
...
> 취소 불가능: 9900
> 취소 불가능: 10000
```
- 코루틴의 동작을 살펴보기 위한 이 실험에서 흥미로운 두가지 사실을 발견할 수 있음
  - 취소 가능 코루틴을 취소해도 즉시 취소되지 않음
    - 100까지는 출력되고 나서 취소됨
  - CancellationException을 잡아서 처리하더라도 코루틴은 어쨌든 취소된 것으로 간주됨
    - 따라서 예외를 처리해도 코루틴을 계속 실행하는 것은 불가능함
- 이제 무슨일이 일어난 것인지 자세히 알아본다.
- 코루틴은 상태 전환이 일어날 때만 취소 여부를 검사함
- 취소 불가능 코루틴은 일시 중단 함수를 하나도 호출하지 않기 때문에 취소 요청이 있는지를 검사조차 하지 않음
- 취소 가능 코루틴에서는 새로 등장한 yield()라는 함수를 사용함
- 반복할 때 마다 yield()를 호출할 수도 있었지만 예제에서는 100번에 한 번만 호출함
- yield()함수가 호출되면 다른 코루틴이 작업을 수행하려고 기다리고 있는지 확인함
- 아무도 없으면 현재 코루틴의 작업을 계속 이어서 수행함
- 기다리고 있는 코루틴이 있다면 그 코루틴이 마지막에 실행했던 지점부터 이어서 작업을 수행하게 됨
- suspend 키워드가 붙은 함수나 코루틴 생성기의 블록 내부가 아니라면 yield() 함수는 사용할 수 없다는 사실을 유념한다
- 사실 suspend가 붙은 모든 함수가 그렇다
- 이런함수는 또 다른 일시 중단 함수 또는 코루틴 안에서 호출해야함

### 타임아웃 설정하기
- 다음과 같은 상황을 상정해본다.
- 만약 어떤 경우에 사용자의 프로필을 받아오는 데에 너무 오랜 시간이 걸린다면? 만약 프로필을 가져오는 데 0.5초 이상 걸리면 그냥 프로필을 표시하지 않기로 결정했다면?
- 그럴 때는 withTimeout() 함수를 사용할 수 있음
```
val coroutine = async {
    withTimeout(500) {
        try {
            val time = Random.nextLong(1000)
            println("수행하는 데에 $time 밀리초가 걸림")
            delay(time)
            "프로필"
        }
        catch (e : TimeoutCancelationException) {
            e.printStackTrace()
        }
    }
}
```
- 이 코루틴은 완료되는 데에 0~1000밀리초가 걸리는데 500밀리초의 타임아웃을 검
- 즉 50% 확률로 작업이 완료되지 못할 것
- 코루틴에 await을 호출하고 무슨 일이 일어나느지 살펴본다
```
val result = try {
    coroutine.await()
}
catch(e: TimeoutCancellationException) {
    "프로필 없음"
}
println(result)
```
- 여기선 코틀린의 try가 식이라는 사실 덕분에 즉시 값을 반환할 수 있음
- 이 코루틴 타임아웃 이전에 값을 반환하면 result의 값은 프로필이 됨
- 그렇지 못하면 TimeoutCancellationException을 받게 되고 Result의 값은 '프로필 없음'이 됨
- 타임아웃을 try-catch식과 함께 사용하면 외부 시스템과 안전하게 상호작용하기 위한 강력한 도구가 됨

### 분배기
- 지금까지 runBlocking 함수를 사용하여 실행한 코루틴은 메인 스레드에서 실행됨
- 다음 코드를 통해 확인할 수 있음
```
runBlocking {
    launch {
        println(Thread.currentThread().name) // "main"이 출력됨
    }
}
```
- 반면 GlobalScope로 실행한 코루틴은 DefaultDispatcher라는 것을 위해서 실행됨
```
GlobalScope.launch {
    println("GlobalScope.launch: ${Thread.currentThread().name}")
}
```
- 이 코드의 출력은 다음과 같음'
```
> DefaultDispatcher-worker-1
```
- DefaultDispatcher는 수명이 짧은 코루틴을 실행하기 위한 스레드 풀임
- launch()나 async()등의 코루틴 생성기에는 기본 인수가 여럿 있는데 그 중 하나가 코루틴을 실행할 분배기를 지정함
- 다른 분배기를 사용하고 싶다면 코루틴을 만들 떄 인수로 분배기를 전달하면됨
```
runBlocking {
    launch(Dispatchers.Default) {
        println(Thread.currentThread().name)
    }
}
```
- 위의 코드는 다음과 같이 출력할 것
```
> DefualtDispatcher-worker-1
```
- 지금까지 살펴봤던 Main이나 Default 분배기 말고 IO라는 분배기도 있음
- 이 분배기는 오래 걸리는 작업을 처리하기 위해 사용됨
- 다른 분배기와 마찬가지로 코루틴을 생성할 때 인수로 IO분배기를 특정해 사용할 수 있음
```
async(Dispatchers.IO) {
    //오래걸리는 작업
}
```
### 구조화된 동시성
- 어떤 코루틴 안에서 다른 코루틴을 띄우는 것은 굉장히 흔한일임
- 구조화된 동시성의 첫번째 규칙은 다음과 같음
- 부모 코루틴은 반드시 모든 자식 코루틴이 완료될 때 까지 기다려야 함
- 이 규칙을 지키지 않으면 자원 누출이 발생할 수 있음
- 구조화된 동시성 개념이 없는 언어에서 자원 누출이 잘 일어나는 것도 이 때문
- 다음 예제 코드에서 부모 코루틴은 10개의 자식 코루틴을 시작
- 구조화된 동시성이 있다는 것은 부모 코루틴이 모든 자식 코루틴이 종료되기를 명시적으로 기다리지 않아도 괜찮다는 뜻
```
val parent = launch(Dispatchers.Default) {
    val children = List(10) {
        childId -> 
        launch {
            for (i in 1..1_000_000) {
                UUID.randomUUID()
                if (i % 100_000 == 0) {
                    println("childId - $i")
                    yield()
                }
            }
        }
    }
}
```
- 이제 자식 코루틴 중 하나가 시작하고 잠시 뒤에 예외를 던지도록 해본다
```
...
if(i % 100_000 == 0) {
    println("$childId - $i")
    yield()
}
if(childId == 8 && i == 300_000) {
    throw RuntimeException("예외 발생")
}
...
```
- 이 코드르 실행하면 재미있는 일이 일어남
- 예외가 발생한 코루틴만 종료되는 것이 아니라 실행된 다른 자식 코루틴이 함께 종료됨
- 여기서는 예외가 처리되지 않아 부모 코루틴까지 올라가서 부모 코루틴의 실행의 취소함
- 이때 부모 코루틴은 다른 모든 자식 코루틴을 종료함
- 자원 누툴을 미연에 방지하기 위함
- 보통은 이런식의 동작을 기대할 것
- 하지만 만약 자식 코루틴에서 발생한 예외 때문에 부모 코루틴이 종료되지 않도록 하려면 supervisorScope를 사용하면 됨
- 하지만 여전히 Cancel() 함수를 통해 부모 코루틴을 취소하면 모든 자식 코루틴도 함께 취소됨
- 이제 구조화된 동시성의 이점까지 살펴봤으니 6장 초반에서 잠깐 언급했던 내용을 다시 살펴본다
- GlobalScope가 주의해야하는 API라는 사실
- GlobalScope는 launch()나 async()와 동일한 방법으로 사용할 수 있지만 구조화된 동시성의 원칙을 준수하지 않음
- 그래서 GlobalScope를 잘못된 방법으로 사용하면 자원 누출이 발생할 수 있음 이런 까닭에 실무에서 GlobalScope를 사용하는 것은 피해야 함