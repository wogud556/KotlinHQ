## 값 지연 패턴
- 계산 로직이 결과를 직접 반환하는 대신 결괏 값을 가리키는 참조를 반환하도록 함
- 자바와 스칼라, Future, 자바스크립트의 Promise가 모두 값 지연 패턴을 구현한다.
- 코틀린의 경우 값 지연 사례를 봤는데 async() 함수가 Deferred 라는 타이블 반환하는 것을 기억할 것임
- 이 타입의 바로 값 지연 패턴을 구현한다.
- 흥미로운 점은 Deferred가 3장에서 배운 프록시 패턴과 4장에서 배운 상태 패턴도 구현하고 있다는 점
- 비동기 계산의 결과를 담는 통을 만들기 위해서는 CompletableDeferred의 생성자를 사용함

```
val deffered = CompletableDeferred <String>()
```
- Deferred에 실제 결과가 담기도록 하려면 complete()함수를 사용함
- 만약 계산 과정에서 오류가 발생하면 completeExceptionally()함수를 통해 호출자에게 예외를 전달할 수 있음
- 이해를 돕기 위해 다음과 같은 비동기 계산결과를 반환하는 함수를 작성해 본다
- 이 함수는 절반의 확률로 ok라는 문자열을 반환하고 다른 절반의 확률로 예외를 던짐
```
suspend fun valueAsync(): Deferred<String> = coroutinScope {
    val deferred = CompletableDeferred<String>()
    launch {
        delay(100)
        if (Random.nextBoolean()) {
            deferred.complete("OK")
        }
        else {
            deferred.completeExceptionally(
                RuntimeException()
            )
        }
    }
    deferred
}
```
- Deferred는 거의 즉시 반환되는 것을 볼 수 있음
- 그 이후에 Launch를 이용해 비동기 계산을 사용하는데 delay() 함수를 통해 시간이 걸리는 계산을 모사함
- 계산이 비동기적으로 이뤄졌으므로 결과값을 바로 얻을 수는 없음
- 결과가 준비될 때 까지 기다렸다면 6장에서 다뤘전 await()함수를 사용함
```
runBlocking {
    val value = valueAsync()
    println(value.await())
}
```
- 반드시 complete()나 completeExceptionally()함수로 Deferred에 값을 채워 줘야 한다는 점애 유의한다
- 그렇지 않으면 프로그램은 결과가 준비될 때 까지 기약 없이 기다릴 수 있음
- 결과 값이 필요 없어지면 지연된 계산을 취소할 수 있음
- 그냥 cancel() 함수를 호출하면 됨

```
deferred.cancel()
```
- 사실 지연된 값을 직접 만들 일은 별로 없다.
- async() 함수가 반환한 값을 사용하는 것으로 충분함