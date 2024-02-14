## 뮤텍스 패턴
- 상호 배제라는 뜻의 뮤텍스는 여러 코루틴이 동시에 접근할 수 있는 공유 상태를 보호하기 위해 사용됨
- 늘 사용하는 공포의 '숫자 세기' 예제로 시작해본다
- 다음 코드에서는 여러 동시성 작업이 하나의 counter변수의 값을 바꾸려고 함
```
val counter = 0
val jobs = List(10) {
    async(Dispatchers.Default) {
        repeat(1000) {
            counter ++
        }
    }
}
jobs.awaitAll()
println(counter)
```
- 