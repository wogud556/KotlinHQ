## Null 다루기
- 코드에 Null이 등장하는 것은 불가피함
- 특히 자바 라이브러리를 다루거나 데이터베이스에서 자료를 가져오는 코드를 작성한다면 더욱 그렇다
- 코틀린에서 변수가 null인지 검사하는 여러가지 방법은 이미 살펴봄 예를 들어 다음 코드를 본다
```
// 절반의 확률로 "문자열"을, 나머지 절반의 확률로 null을 반환
val stringOrNull: String? = if ( Random.nextBoolean()) "문자열" else null

// 자바 스타일의 검사 방법
if (stirngOrNull != null) {
    println(stringOrNull.length)
}
```
- 엘비스 연산자를 사용하면 이 코드를 다음과 같이 쓸 수 있음
```
val alwaysLength = stringOrNull?.length ?: 0
```
- 이 연산자는 lengthrk null 이 아니라면 그 값을 반환하고, Null 이라면 기본값을 반환함
- 객체가 중첩돼 있는 경우에는 연쇄적으로 Null검사를 수행할 수 있음
- 예를 들어 Response 객체 안에 Profile이 들어 있고, 그 안에는 성과 이름 필드가 있다고 하자.
- 두 필드는 모두 Null 값을 가질 수 있음
```
data class Response (
    val profile: UserProfile?
)

data class UserProfile (
    val firstName: String?,
    val lastName : String?
)
```
- 연쇄적으로 Null 검사를 하는 코드는 다음과 같음
```
val response : Response? = Response(UserProfile(null, null))
println(response?.profile?.firstName?.length)
```
- 연쇄 사슬에 있는 필드 중 하나라도 null 값을 가지면 이 코드는 null을 출력(비정상 종료 대신)
- 마지막으로 9장 '시야 지정 함수 사용하기' 절에서 간단히 언급했던 let()블록을 사용해서 null 검사를 하는 방법도 있음
- 위의 코드를 let()을 사용해서 다시 작성하면 다음과 같음
```
println(response? let {
    if.protile?.let {
        it.firstName?.length
    }
})
```
- 군데군데 있는 it이 보기 싫다면 다른 시야 지정 함수인 run()을 사용하는 방법도 있음
```
println(response?.run {
    profile?.run {
        firstName?.length
    }
})
```
- !! 연산자는 안전하지 않기 때문에 실무에서는 사용을 피해야 함
```
println(json!!.User!!.firstName!!.length)
```
- 이 코드는 KotlinNullPointerException 을 던질 것
- 하지만 테스트를 할 때는 null 안전성 이슈를 빠르게 잡아내기 위해 !! 연산자를 사용하는 것도 도움이 될 수 있음