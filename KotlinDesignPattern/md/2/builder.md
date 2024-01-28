## 빌더패턴
- 매우 단순한 객체는 생성자 하나로 충분할 때가 있다.
- 그러나 객체 생성이 굉장히 복잡하고 많은 매개변수를 사용할 때도 있음
- 이전 절에 더 나은 생성자를 사용할 수도 있도록 하는 패턴을 이미 살펴봄
- 바로 정적 팩토리 메서드 패턴이다
- 이번 절에는 빌더 디자인 패턴을 살펴볼 것
- 이 패턴을 사용하면 복잡한 객체를 보다 쉽게 만들 수 있음
- 복잡한 객체의 예를 들기 위해 이메일 발송 시스템을 상상하자
- 실제 발송 기능은 구현하지 않고 이메일을 나타내는 클래스만 설계할 것임
- 이메일에는 다음과 같은 속성이 있을 수 있음
  - 수신자 주소(최소 1개)
  - 참조(선택사항)
  - 제목(선택사항)
  - 본문(선택사항)
  - 중요표시(선택사항)
- 이 시스템의 이메일을 다음과 같은 데이터 클래스로 표현할 수 있음
```
data class Mail_V1(
  val to: List<String>,
  val cc: List<String>?,
  val title: String?,
  val message: String?,
  val important: Boolean
)
```
> 위 코드의 마지막 매개변수 정의를 보라. 쉼표는 오타가 아님. 이를 줄 끝 쉼표(trailing comma)라고 부르며 코틀린 1.4보부터 도입된 문법임
> 덕분에 매개변수의 순서를 쉽게 바꿀 수 있다.
- 이제 관리자에게 보내는 이메일을 하나 작성해보자
```
 val mail = Mail_V1(
  listOf("manage@company",
  null,
  "Ping",
  null,
  true)
 )
```
- 참조(cc)를 null기능 타입으로 정의해서 이메일 리스트를 받거나 null을 받을 수 있도록 했다.
- 아니면 List<String> 타입으로 정의해 항상 ListOf로 인수를 전달하도록 하는 방법도 있음
- 생성자에 전달할 인수가 굉장히 많기 때문에 혼동을 방지하기 위해 주석을 달아놨다.
- 하지만 클래스를 수정해야 한다면 어떻게 될까?
- 먼저 컴파일이 되지 않을 것임
- 또한 주석도 함꼐 수정해야 함
- 즉 생성자에 인수가 너무많으면 코드가 금세 지저분해짐
- 빌더 디자인 패턴은 이런 문제를 해결하기 위해 탄생했다.
- 빌더 패턴을 사용하면 객체 생성과 인수 할당을 분리함으로써 복잡한 객체를 차근차근 만들 수 있으
- 이 절에서는 이 문제를 해결하는 여러 방법을 살펴볼 것
- 먼저 Mail 클래스를 감싸고 있는 MailBuilder 클래스를 만들어보자
```
class MailBuilder{
  private var to : List<String> = listOf()
  private var cc : List<String> = listOf()
  private var title: String = ""
  private var message: String = ""
  private var important: Boolean = false

class Mail internal constructor(
  val to: List<String>,
  val cc: List<String>?,
  val title: String?,
  val message: String?
  val important: Boolean
)
}
```
- 빌더는 생성하고자 하는 클래스와 정확히 동일한 속성을 가짐
- 하지만 모두 가변 속성이라는 점에서 다름
- 생성자에 internal 접근 제한자가 붙어 있는 것에 주목
- 이는 모듈 내의 모든 코드에서 Mail클래스에 접근할 수 있다는 뜻
- 클래스 생성을 완료하기 위해 build() 함수를 구현해보자
```
fun build() : Mail{
  if(to.isEmpty()){
    throw RuntimeException("To 속성이 비어있습니다.")
  }
  return Mail(to, cc, title, message, important)
}
```
- 각 속성을 설정할 수 있는 함수도 구현할 것
```
fun message(message: String): MailBuilder{
  this.message = message
  return this
}
// 각 속성에 대해 모두 구현해야 함
```
- 이제 다음과 같이 빌더를 이용하여 이메일을 생성할 수 있음
  - MailBuilder의 생성자를 구현한 적이 없는데 예제 코드에서는 생성자를 사용하고 있다.
  - 따로 생성잘르 구현하지 않는 이상 오류가 발생하는 코드이고, 깃허브의 코드에서도 생성자를 사용하지 않고 있기 때문에 임의로 생성자 대신 to()를 사용하는 함수로 변경했음
```
val email = MailBuilder().to(listof("hello@hello.com")).title("안녕?").build()
```
- 새로운 값을 설정한 후 현재 MailBuilder 객체를 가리키는 참조를 반환함으로써 연속해서 다른 속성을 설정하는 함수를 호출하는 메서드 사슬을 만들 수 있다
- 이 빌더패턴은 두가지 문제가 있음
  - 만들고자 하는 클래스의 속성을 빌더에도 똑같이 나열해야함
  - 속성마다 값을 설정하기 위한 함수를 선언해야함
- 코틀린에는 더 실용적인 두가지 방법이 존재하는데

### 유창한 설정자
- 이걸 사용하면 코드가 좀 더 간결해짐
- 여기선 추가적인 클래스를 만들지 않고 데이터 클래스의 생성자에서 필수적인 필드만 입력으로 받을 것임
- 필수적이지 않은 나머지 필드는 비공개로 선언하고 각각에 대해 설정자를 구현할 것
```
data class Mail_V2 (
  val to: List<String>,
  private var _message: String? = null,
  private var _cc : List<String>? = null,
  private var _title: String? = null,
  private val _important: Boolean? = null
){
  fun message(message: String) = apply {
    _message = message
  }
  //다른 모든 필드에 대해서 같은 방법으로 구현함
}
```
> 비공개 변수의 이름에 밑줄 문자를 사용하는 것은 코틀린에서 일반적인 관례임.
> 이렇게 하면 this.message = message 와 같이 작성할 필요도 없고, message = message라고 작성하는 실수도 막을 수 있음

- 이 예제 코드에서는 apply 함수를 사용함
- 이 함수는 모든 코틀린 객체를 대상으로 호출할 수 있는 시야 지정 함수 중 하나
- 시야 지정 함수에 대한 자세한 내용은 9장에서 다룰 것
- apply함수는 주어진 블록을 실행한 뒤 객체의 참졸르 반환함
- 즉 이전 예제에서 다음과 같이 구현했던 설정자 코드의 짧은 버전임
```
fun message(message: String):MailBuilder{
  this.message = message
  return this
}
```
- apply 함수를 사용해도 API는 동일하므로 다음과 같이 사용할 수 있음
```
val mailV2 = Mail_V2(listOf("manager@company.com")).message("안녕")
```
- 그러나 아예 설정자를 구현하지 않는 방법도 있음.
- 위에서 언급한 apply() 함수를 객체 자체에 사용하는 것
- apply()함수는 코틀린의 모든 객체에 존재하는 확장함수임
- 다만 이 방법은 선택적 필드가 값이 아닌 변수로 선언돼 있을때만 가능
- 이 방법 사용 시 이메일 객체를 다음과 같이 생성할 수 있음
```
val mail = Mail_V2("hello@gamil.com").apply{
  message = "어떤 메시지"
  title = "Apply"
}
```
- 괜찮은 방법이다. 작성해야 하는 코드도 많이 줄었다.
- 하지만 이 방법에도 몇가지 단점이 있다.
  - 선택적 인수를 모두 가변 필드로 선언해야 함. 그러나 가능하다면 가변 필드보다는 스레드 안전하며 값을 추적하기 용이한 불변 필드를 사용하는 것이 나음
  - 모든 선택적 인수가 null값을 가질 수 있음. null안전성 탓에 변수에 접근할 때마다 값이 Null인지를 확인해야함
  - 문법이 너무 장황함. 각 필드에 대해 똑같은 패턴을 계속 반복해야함
- 이 문제를 해결하는 방법은 기본 인수를 사용하는 것이다.

### 기본인수
- 코틀린에서는 생성자의 함수의 매개변수에 기본값을 설정할 수 있음
```
data class Mail_V3(
  val to : List<String>
  val cc : List<String> = listOf()
  val title : String = ""
  val message: String = ""
  val important: Boolean = false
)
```
- 타입 뒤에 = 연산자를 사용해 기본 인수로 설정함
- 이렇게 하면 생성자가 모든 안수를 받을 수 있지만 모든 인수를 항상 필수적으로 전달할 필요는 없게 됨
- 따라서 본문이 없는 이메일을 생성하고 싶다면 다음과 같이 할 수 있음
```
val mail = Mail_V3(listOf("manage@company.com"), listOf(), "안녕")
```
- 하지만 참조 목록에 아무도 없다는 것을 나타내기 위해 빈 리스트를 전달해야 하는 것에 주목하라. 다소 불편함
- 만약 중요 플래그만 설정해서 이메일을 보내고 싶다면 어떻게 되나?
- 유창항 설정질르 사용하면 순서는 따질 필요가 없어 매우 편리했음
- 비슷한 목적으로 코틀린은 명명 인수라는 기능을 제공함
```
val mail = Mail_V3(title = "안녕", message = "잘 지내니?", to = listOf("my@dear.cat"))
```
- 기본 매개변수와 명명 인수를 함꼐 사용하면 코틀린에서 복잡한 객체를 사용하는 것이 한결 쉬워짐
- 따라서 코틀린에서 빌더 디자인 패턴을 사용할 일이 없어진다.
