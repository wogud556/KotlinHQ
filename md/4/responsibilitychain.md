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
- 그리고 각 단계가 그 결과에 따라 계속해서 다음 단계로 진행할지, 또는 처리를 끝내고 결과를 반환할지 결정ㅂ