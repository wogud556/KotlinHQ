## 책임 사슬 패턴
- 필터 사슬 개념은 웹 서버에서 보편적으로 사용되는 개념임
- 어떤 요청이 들어오면 일반적으로 세가지를 만족하기를 기대함
  - 매개변수가 이미 검증됨
  - 사용자가 이미 인증됨
- 사용자의 역할과 권한 정보가 주어졌고 동작을 수행하기 위한 인가가 이뤄짐
- 이에 따라 작성한 코드는 아래와 같다
```
data class Request(val email: String, val question: String)

fun handleRequest(r: Request){
    // 유효성 검사
    if(r.email.isEmpth() || r.question.isEmpty()){
        return
    }
    // 인증
    // 이 사용자가 누구인지 알아낸다
    if(r.isKnownEmail()) {
        return
    }

    // 인가
    // 주니어 개발자의 요청은 자동으로 무시됨
    if(r.isFromJuniorDeveloper()){
        return
    }

    println("모르곘네요. StackOverflow는 검색해 보셨나")
}
```
- 이 코드에서 어떤 개발자가 질문을 동시에 보내는 것을 발견함
- 이를 막으려면 함수에 로직을 추가해야 함
- 그런데 명색이 소프트웨어 아키텍쳐인데 둥작을 다른 곳으로 위임하는데 더 좋은 방법이 있을까
- 책임사슬 디자인 패턴은 복잡한 로직을 여러개의 작은 단계로 쪼갬
- 그리고 각 단계가 그 결과에 따라 계속해서 다음 단계로 진행할지, 또는 처리를 끝내고 결과를 반환할지 결정
- 이번에는 코틀린의 기능을 새로 배우기 보다 이미 알고 있는 지식을 활용할 것임
- 다음과 같은 인터페이스를 구현해본다
```
Interface Handler{
    fun handle(request: Request): Response
}
```
- 개발자의 질문에 대한 괴짜 아키텍트의 답변이 어떤 모습일지 이야기한 적은 없다
- 이 책임 사슬은 너무 길고 복잡해서 답변까지 갈 일이 잘없기 때문임
- 솔직히 말하면 한번도 제대로 답변하는 걸 본 적이 없다.
- 하지만 일단 응답이 다음과 같이 생겼다고 치자
```
data class Response(val answer: String)
```
- 자바 스타일로 구현하면 로직의 각 부분을 다음과 같이 Handler 안에 넣을 것임
```
class BasicValidationHandler(private val next:Handler) : Handler{
    override fun handle(request: Request): Response{
        if(request.email.isEmpty() || request.question.isEmpth()){
            throw IllegalArgumentException()
        }
    }
}
```
- 메서드가 하나뿐인 인터페이스를 구현하는 것을 볼 수 있음
- 이 메서드가 바로 객체가 수행해야 하는 동작임
- 다른 필터도 크게 다르지 않다. 필터를 만들고 나면 원하는 순서대로 조합하면 됨
```
val req = Request("developer@company.com", "왜 갑자기 빌드가 안 되죠?")

val chain = BasicValidationHandler(
    KnownEmailHandler(
        JuniorDeveloperFilterHandler(
            AnswerHandler()
        )
    )
)

val res = chain.handle(req)
```
- 이제 "더 나은 방법이 있을까?" 같은 뻔한 질문은 하지 않곘음
- 당연히 더 좋은 방법이 있는데, 여기는 코틀린 세계이다. 이미 함수의 다양한 사용법을 앞 절에서 많이 살펴봤음
- 여기서는 책임 사슬을 나타내기 위한 함수를 하나 정의한다
```
typealias Handler = (request : Request) -> Response
```
- 어떤 요청을 받아서 응답을 반환하는 간단한 기능을 구현하기 위해 별도의 클래스나 인터페이스를 만들지 않아도 됨
- 값으로서의 함수를 사용해서 인증을 구현하면 다음과 같다.
```
val authentication = fun(next: Handler) = 
    fun (request: Request): Response {
        if(!request.isKnownEmail()) {
            throw IllegalArgumentException()
        }
        return next(request)
    }
```
- 여기서 authentication은 함수를 받아서 새로운 함수를 반환함
- 이 패턴을 사용하면 쉽게 함수들을 조합할 수 있음
```
val req = Request("develop@company.com","소프트웨어 아키텍트가 왜 필요한가요")
val chain = basicValidation(authentication(finalResponse()))
val res = chain(req)
println(res)
```
- 어떤 방법을 사용할지 선택하는 것은 개발자의 몫임
- 가령 라이브러리나 프레임워크 처럼 다른 개발자가 기능을 확장할 가능성이 높다면 함수보다는 인터페이스를 사용해서 명시적으로 코드를 작성하는 쪽이 더 적합함
- 함수를 사용하려면 코드를 더 간결하게 할 수 있기 때문에 단지 코드를 작은 단위로 쪼개는 것이 목적이라면 함수를 사용하는 것이 더 좋을 수 있음
- 이 패턴은 실무에서도 많이 등장했는데 예를들어 많은 웹 서버 프레임워크는 인증이나 인가 로깅 심지어 요청 라우팅과 같은 횡단 관심사를 다루기 위해 책임 사슬 패턴을 사용함
- 간혹 필터나 미들웨어 라는 용어를 쓰기도 하는데 결국 책임 사슬 패턴을 의미하는 것은 똑같음
- 10장과 11장에서는 코틀린의 가장 유명한 프레임워크가 책임 사슬 패턴을 어떤 식으로 활용하는지 살펴볼 것임
- 이 때 조금 더 심도있게 의논해볼것
- 다음 디자인 패턴은 다른 패턴과 사뭇 결이 다름 그리고 복잡함