## 해석기 패턴
- 해석기 디자인 패턴은 컴퓨터 과학에 대한 배경 지식의 수준에 따라 매우 간단해 보일 수 있고, 매우 어려워 보일 수 있음
- 고전 소프트웨어 디자인 패턴을 다루는 책 중에서는 이 패턴을 아예 생략하거나 책 뒷부분에 배치해서 궁금한 독자들만 읽을 수 있도록 하는 경우도 있음
- 해석기 패턴은 특정한 언어를 번역할 때 사용하는 디자인 패턴이기 때문
- 그런데 언어를 번역할 일이 있기는 하는가
- 컴파일러가 해주는거 아님?

### 도메인 특화 언어
- 모든 개발자는 여러 개의 언어나 하위 언얼르 다룸
- 평벙한 개발자라고 해도 2개 이상의 언어를 사용함
- 메이븐이나 그래들 같은 프로젝트 빌드 도구를 생각해본다
  - 빌드 도구의 설정 파일이나 빌드 스크립트는 특덩한 문법을 갖는 하나의 언어라고 할 수 있음
  - 정해진 문법에 맞게 작성하지 않으면 프로젝트가 제대로 빌드되지 않을 것
  - 프로젝트에 있는 해석기가 설정 파일을 해석한 뒤 그대로 동작하도록 하기 때문
- SQL이나 NoSQL 데이터베이스용 언어와 같은 쿼리 언어도 있음
  - 안드로이드 개발자는 XML레이아웃을 일종의 프로그래밍 언어라고 생각할 수 있음
- 심지어는 HTML도 사용자 인터페이스를 정의하는 언어로 볼 수 있음
- 이 외에도 수많은 언어가 있음
- 직접 정의한 언어를 사용해서 테스트를 수행하는 테스트 프레임 워크도 있음
- 큐컵버가 그 중 하나임
- 이런 언어를 도메인언어(Domain-Specific Language)라고 하며 DSL은 언어 안의 언어로 특정 도메인을 사용하기 위해 만든 언어임 

### 나만의 언어 만들기
- 이 절에서는 'SQL을 위한 도메인 특화 언어'를 간단하게 정의할 것
- 형식이나 문법을 따로 정의하지 않고 다음의 예시로 대신할 것
```
val sql = select("name", "age"){
    from("users"){
        where("age > 25")
    } //from
} //select
```
- 이 언어의 목적은 SQL의 가독성을 높이고 흔히 저지르는 실수(FROM을 FORM이라고 잘못 쓰는 등)을 방지하는 것
- 따라서 컴파일 시에 유효성 검사도 해주고 자동 오나성도 지원할 것
- 위 코드는 다음과 같은 출력이 됨
> SELECT name, age FROM users WHERE age > 25
- 가장 쉬운 부분인 select 함수부터 구현해본다
```
fun select(colums: String, from: SelectClause.() -> Unit): SelectClause{
    return SelectClause(cloumns).apply(from)
}
```
- 단일식 함수 표기법으로도 작성할 수 있지만 여기선 명확성을 위해 일부러 길게 작성함
- 이 함수는 매개변수를 2개 받음.
  - 첫번째는 단순한 String
  - 두번째는 아무것도 받지 않고 아무것도 반환해주지 않음
- 가장 흥미로운 부분은 다음과 같이 람다 함수의 수신자를 지정했다는 점
```
SelectClause.() -> Unit
```
- 꽤나 그럴듯한 속임수이므로 집중해서 읽어봐야함
- 1장과 2장에서 다뤘던 확장 함수를 기억 속에서 꺼내본다
- 위의 코드는 사실 다음 코드와 같다
```
(SelectClause) -> Unit
```
- 인수를 받지 않는 줄 알았던 람다 함수가 사실은 인수 하나를 받고 있었던 것
- 그 인수는 SelectClause 타입의 객체임
- 두번째로 살펴볼 속임수는 2장에서 봤던 appply() 함수에 있음
- 다음 코드를 본다
```
SelectClause(columns).apply(from)
```
- 이는 다음 코드와 동일함.
```
val selectClause = SelectClause(column)
from(selectClause)
return selectClause
```
- 이 코드가 수행하는 일을 자세히 나타내면 다음과 같음
  1. SelectClause 객체를 초기화함. 이 객체는 생성자에 인수를 1개 받는 간단한 객체
  2. from() 함수에 SelectClause인스턴스를 전달해 호출
  3. SelectClause인스턴스를 반환함
- from()이 SelectClause를 받아서 의미 있는 동작을 하는 함수일 때만 이 코드가 의미가 있음
- DSL예제를 다시 한번 살펴본다
```
select("name, ages", {
    this@select.from("users",{
        where("age > 25")
    })
})
```
- 이번에는 함수의 수신잘르 명시적으로 나타냈음
- 즉 from()함수는 SelectClause객체의 from()함수를 호출할 것
- 이 from()함수가 어떻게 생겼을지 추측해볼것
- 첫 번째 인수로 String을 받고 두번째 인수로는 다른 람다 함수를 받음
```
class SelectClause(private val columns: String) {
    private lateinit val from : FromClause
    fun from (
        table: String,
        where: FromClause.() -> Unit
    ): From Clause {
        this.from = FromClause(table)
        return this.from.apply(where)
    }

    override fun to String() = "SELECT $columns $from"
}
```
- 이 예제코드도 짧게 쓸 수 있음
- 하지만 그러면 apply함수 안에서 또 apply함수를 호출해야 할 것임
- 아직 코틀린 코드기 익숙하지 않다면 다소 햇갈릴 수 있음
- lateinit 키워드가 등장
  - 코틀린 컴파일러는 null안전성을 매우 엄격하게 지킨다는 점을 기억
  - 이 코드에서 lateinit을 생략하면 컴파일러는 변수를 기본값으로 초기화하라고 요구할 것
  - 그러나 from 변수의 값은 나중에나 알 수 있기 때문에 먼저 컴파일러한테 조금 봐달라고 요청하는 것임
> 나중에 변수를 초기화하겠다는 약속을 잊어버리고 변수에 값을 할당하지 않으면 그 변수에 처음 접근하는 순간 UninitializedPropertyAccessException에외가 발생하게 됨
- lateinit키워드는 굉장히 위험할 수 있기 때문에 사용시 주의를 기울여야함
- 이코드가 하는 일은 아래와 같다.
  1. FromClause 인스턴스를 생성
  2. FromClause를 SelectClause 멤버로 저장
  3. FromClause 인스턴스를 where 람다 함수로 전달
  4. FromClause 인스턴스를 반환
```
select("name, age", {
    this@select.from("users", {
        this@from.where("age> 25")
    })
})
```
- 이 코드는 무슨 뜻인가 from() 메서드를 이해 헀다면 이 메서드는 식은 죽 먹기임
- FromClause에는 String 타입의 인수 1개를 받는 where()메서드가 있으리라 추측할 수 있음
```
class FromClause(private val table: String){
    private lateinit var where: WhereClause

    fun where(conditions: String) = this.apply{
        where = WhereClause(conditions)
    }

    override fun toString() = "From $table $where"
}
```
- 이번에는 apply 함수를 사용해서 짧은 버전으로 작성해봄
- 코드의 내용은 간단하다. 입력으로 받은 문자열로 WhereClause객체를 초기화하고 그것을 반환함
```
class WhereClause(private val conditions : String){
    override fun toString() = "WHERE $conditions"
}
```
- WhereClause클래스는 전달받은 조건문을 WHERE키워드와 함꼐 출력하는 역할을 함
```
class FromClause(private val table: String){
    // 다른 코드는 생략
    override fun toString() = "FROM $table $where"
}
```
- FromClause 클래스는 FROM 키워드와 함께 테이블명 및 WhereClause의 출력 결과를 출력함
```
class SelectClause(private val columns: String){
    // 다른 코드는 생략
    override fun toString() = "SELECT $columns $from"
}
```
- SelectClause클래스는 SELECT 키워드, 칼럼명, FromClause가 출력한 내용 전체를 출력함

#### 쉬어가기
- 코틀린은 가독성이 높고 타입 안전한 DSL을 만들기 위해 여러가지 아름다운 기능을 제공함
- 그러나 해석기 디자인 패턴은 가장 다루기 어려운 패턴 중 하나이기도함
- 한번 읽어서 내용이 이해가 가지 않는다면 예제 코드를 디버깅하는데에 시간을 투자해봐라
- 각 단계에서 this가 가리키는 것이 무엇인지, 언제 객체의 함수가 호출되고 언제 객체의 메서드가 호출되는지 고민해본다

### 접미 호출
- 코틀린에서 DSL에 관련된 기능 하나를 설명하지 않고 미뤄왔음
- 헷갈릴 수 있기 때문
- DSL을 사용한 예제 코드를 본다
```
val sql = select("name,  age") {
    from("users") {
        where("age > 25")
    } // from
}//select
```
- select 함수는 인수를 2개 받아야 되는데(문자열과 람다 함수) 람다 함수가 괄호 안쪽에 있지 않고 바깥으로 나온 것을 알 수 있음
- 이러한 용법을 접미 호출이라고 하며 굉장히 흔하게 사용하는 문법임
- 어떤 함수의 마지막 인수가 함수라면 이 함수는 괄호 바깥에 둬도 됨
- 이렇게 하면 문법이 훨씬 깔끔해짐
- 특히 예제 코드처럼 DSL을 사용할 떄 도움이 됨
- 해석기 디자인 패턴과 타입 안전한 DSL을 만들 수 있도록 지원하는 코틀린의 기능은 매우 강력해 보임
- 그러나 큰 힘에는 큰 책임이 따르는 법
- 언어 안에서 새로운 언어를 만들어야 할 만큼 복잡한 일에만 해석기 패턴을 사용한다.