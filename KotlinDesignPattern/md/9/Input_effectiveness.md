## 입력 유효성 검사
- 입력 유효성 검사는 꼭 필요하지만 귀찮음
- 도대체 다음과 같은 코드를 이제껏 몇번이나 쓴건가
```
fun setCapacity(cap: Int) {
    if(cap < 0) {
        throw IllegalArgumentException()
    }
}
```
- 코틀린에서는 require()함수를 사용해서 인수를 검사할 수 있음

```
fun setCapacity(cap: Int) {
    require(cap > 0)
}
```
- 훨씬 읽기 좋은 코드가 탄생함
- require()로 null 검사도 할 수 있다

```
fun printNameLength(p: Profile) {
    require(p.firstName != null)
}
```
- 하지만 더 간편한 requireNotNull()함수가 있음
```
fun printNameLength(p:Profile) {
    requireNotNull(p.firstName)
}
```
- check() 함수를 사용하면 객체의 상태를 검사할 수 있음
- 사용자가 제대로 설정하지 않은 객체를 반환할 때 이 함수를 사용하면 좋음
```
class HttpClient {
    var body: String? = null
    var url : String = ""

    fun postRequest() {
        check(body != null) {
            "POST 요청에는 반드시 본문이 필요함"
        }
    }
}
```
- 역시 마찬가지로 null 검사를 위한 checkNotNull() 함수도 있음
- Require() 와 check()의 차이점은 require()는 입력이 잘못됐을 때 IllegalArgument Exception을 던지는 데애 반해 check()은 IllegalStateExcpeion을 던진다는 점
- IllegalArgumentException은 함수의 입력이 잘못됐다는 뜻이지만 IllegalStateException은 객체의 상태가 잘못됐다는 뜻
- 코드의 가독성을 높이기 위해 require()나 check()같은 함수를 사용하라
- 마지막으로 코틀린에서 여러 상태를 표현하기 위한 효율적인 방법을 본다.