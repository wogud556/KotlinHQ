## 팬인 패턴
- 팬인 디자인 패턴은 여러 일꾼의 작업 결과를 하나로 모으기 위한 패턴임
- 여러 일꾼이 결과를 생산한 뒤에 이를 하나로 모아야 한다면 팬인 패턴을 사용하면 됨
- 이 패턴은 이전 절에서 배운 팬아웃 패턴의 반대임
- 여러 코루틴이 같은 채널을 읽는 대신 여러 코루틴이 결과를 같은 채널로 보냄
- 팬아웃 패턴과 팬인 패턴을 잘 조합하면 맵 리듀스 알고리듬의 훌륭한 기반이 됨
- 이를 확인하기 위해 일꾼을 만드는 코드를 살짝 바꿔본다

```
private fun CoroutineScope.doWorkAsync(
    channel: ReceiveChannel<String>,
    resultChannel: Channel<String>
) = async(Didpatchers.Default) {
    for (p in channel) {
        resultChannel.send(p.repeat(2))
    }
}
```
- 이제 각 일꾼은 주어진 작업을 처리하고 그 결과를 resultChannel로 보냄
- 이 패턴은 이전에 봤던 행위자나 생산자와는 다르다는 점에 유의함
- 모든 행위자는 고유한 채널을 갖지만 여기서는 모든 일꾼들이 하나의 resultChannel을 공유함
- 일꾼의 작업 결과를 모으기 위해서는 다음과 같은 코드를 사용함

```
runBlocking {
    val workChannel = generateWork()
    val resultChannel = Channel<String>()
    val workers = List(10) {
        doWorkAsync(workChannel, resultChannel)
    }
    resultChannel.consumeEach {
        println(it)
    }
}
```
- 이 코드가 어떻게 동작하는지 살펴본다
    1. 우선 resultChannel을 생성 모든 일꾼이 이 채널을 공유할 것
    2. 만든 채널을 모든 일꾼들에게 알려줌. 총 10개의 일꾼이 있으며, 각 일꾼은 받은 메시지를 두번 반복해서 resultChannel에 보냄
    3. 마지막으로 메인 코루틴에서 결과를 소비함. 이렇게 하면 동시에 실행되는 여러 일꾼이 만든 결과를 한곳으로 모을 수 있음
- 출력은 다음과 같다.
```
>...
> 9995쪽9995쪽
> 9996쪽9996쪽
> 9997쪽9997쪽
> 9999쪽9999쪽
> 9998쪽9998쪽
> 10000쪽10000쪽
```
- 다음으로는 특정한 경우에 프로그램의 반응성을 높이는 데에 사용할 수 이쓴ㄴ 디자인 패턴을 알아본다.