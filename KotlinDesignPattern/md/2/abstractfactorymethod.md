## 추상 팩토리 패턴
- 추상 팩토리 패턴은 많은 오해를 사는 디자인 패턴임
  - 매우 복잡하고 이상한 패턴이라는 것
  - 그러나 사실은 굉장히 간단하다.
  - 팩토리 메서드 패턴을 이해하면 추상팩토리도 금세 이해할 수 있을것이다.
  - 추상 팩토리만 팩토리를 만들어내는 팩토리이기 때문
- 팩토리는 다른 클래스를 만들어 낼 수 있는 함수나 클래스임
- 추상팩토리는 여러 팩토리 메서드를 감싸는 클래스

### 추상팩토리의 주 사용처
- 현업에서 추상팩토리 디자인패턴은 프레임워크나 라이브러리가 파일에서 구성설정을 읽어드릴때 자주 사용되는데 스프링 프레임워크가 좋은예다.
  
### 추상 팩토리 디자인의 예제
- 추상 팩토리 디자인패턴이 어떻게 동작하는지 이해하기 위해 이전 절의 서버 예제를 계속 사용할 것
- 이번에는 서버 클래스에서 사용하는 설정 파일이 다음과 같이 YAML로 작성되 있다고 보자
```
server:
  port: 8080
environment: production
```
- 지금부터 해야할 일은 이 설정 파일을 읽어서 객체를 셍서하는 것임
- 그러나 여기서는 두 종류의 객체를 생성할 것임
- 두 객체는 서로 관련돼 있으나 형제 관계 까지는 아님
- 두객체를 위한 인터페이스를 먼저 정의한다.

```
interface Property{
  val name: String
  val value : Any
}
```
- 이제 데이터 클래스 대신 인터페이스를 반환할 것
- 왜 이렇게 해야 하는지는 나중에 알게 될 것
```
interface ServerConfiguration{
  val properties: List<Property>
}
```
- 이제 나중에 사용할 기본 코드를 구현함
```
data class PropertyImpl(
  override val name: String
  override val value: Any
) : Property
data class Server ConfigurationImpl{
  override val properties: List<Property>
} : ServerConfiguration
```
- 서버 설정에는 속성의 리스트만 있고, 각 속성은 이름 객체와 값 객체로 이뤄짐
- Any 타입이 처음 등장했다. 코틀린의 Any는 자바의 object에 해당된다.
- 그러나 중요한 타이가 있는데 null을 가질 수 있다는 것임
- 첫번쨰 팩토리 메서드를 생서해보면, 이 메서드는 문자열로 된 Property객체를 생성함
```
fun property(prop: String): Property{
  val (name, value) = prop.split(",")
  return when(name) {
    "port" -> PropertyImpl(name, value.trim().toInt())
    "environment" -> PropertyImpl(name, value.trim())
    else -> throw RuntimeException("알 수 없는 속성: $name")
  }
}
```
- 다른 언어들과 마찬가지로 trim() 함수는 문자열에서 공백을 제거하는 함수임
- 이제 이 서비스의 포트와 환경을 나타내는 두 속성을 만들어보자
```
val portProperty = property("port : 8080")
val environment = property("environment: production")
```
- 이 코드에는 작은 문제가 있다. 어떤 문제인지 이해하기 위해 port 속성의 값을 다른 변수에 저장한다
```
val port: Int = portProperty.value
// Type mismatch: inferred type is Any but Int was expected
```
- 이미 팩토리 메서드에서 port가 int타입으로 파싱된 것을 확인함
- 그러나 이 정보는 사라져버렸는데, value 가 Any타입으로 선언되어있기 때문이다.
- 그래서 value는 String일수도 Int일수도 있고, 다른 어떤 타입이든 될 수 있다.
- 이 문제를 해결하려면 다른 도구가 필요함

### 캐스팅
- 타입 언어에서 캐스팅은 컴파일러가 추론한 타입 대신 프로그래머가 지정한 타입을 사요하도록 강제하는것.
- 값의 타입이 무엇인지 확실히 알고 있다면 다음과 같이 '안전하지 않은' 캐스팅을 할 수 있다.
```
val port: Int = protProperty.value as Int
```
- 만약 값이 기대한 값과 타입이 다른 경우 컴파일러 경고 메시지도 없이 프로그램이 비정상 종료됨.
- 그래서 안전하지 않은 이라는 수식어가 붙는다
- 한편 '안전한' 캐스팅도 사용할 수 있다.
```
val port: Int? = portProperty.value as? Int
```
- 안전한 캐스팅은 프로그램 종료시키지 않음
- 그러나 객체가 기대한 타입과 다른 경우 null을 반환한다.
- 이번에는 port 변수가 null 값을 가질 수 있는 Int? 타입으로 선언된 것에 주목하라.
- 따라서 컴파일 타임에 기대했던 것과 다른 타입을 갖느 경우까지도 명시적으로 고려해서 코드를 작성해야 함

### 하위 클래스 생성
- 이번에는 캐스팅 말고 다른 접근을 시도하자
- Any 타입의 값을 갖는 단일 구현체를 사용하는 대신 2개의 구분된 구현체를 사용할 것
```
data class IntProperty(
  override val name: String,
  override val value: Int
): Property

data class StringProperty(
  override val name: String,
  override val value: String
): Property
```
- 두 클래스 중 하나를 리턴하려면 앞서 구현한 팩토리 메서드를 약간 수정해야 함
```
fun property(prop: String): Property{
  val(name, value) = prop.split(":")

  return when(name) {
    "port" -> IntProperty(name, value.trim().toInt())
    "environment" -> StringProperty(name, value.trim())
    else -> throw RuntimeException("알 수 없는 속성: $name")
  }
}
```
- 이제 문제가 없을 것 같지만 컴파일을 시도하면 여전히 오류가 발생함
```
val portProperty = Parser.property("port: 8080")
val port: Int = portProperty.value
```
- 이제 구체 클래스는 2개가 생김.
- 그렇지만 컴파일러는 아직까지 파싱된 속성이 IntProperty인지 StringProperty인지 구분하지 못하며 그저 Property인터페이스를 구현하고 있다는 것만 안다.
- 그리고 그 값은 여전히 Any타입임
```
> Type mismatch: inferred type is Any but Int was expected
```
- 다른 기법이 필요하다. 바로 스마트 캐스팅이라 부르는 기법

### 스마트 캐스팅
- is 키워드를 사용하면 객체 타입을 검사할 수 있다.
```
println(portProperty is IntProperty) // true
```
- 그러나 코틀린 컴파일러는 굉장히 똑똑함
- if 표현식으로 타입을 검사한 뒤에는 portProperty가 확실히 IntProperty라는 것을 알 수 있다.
- 그래서 안전하게 캐스팅할 수 있다.
- 코틀린 컴파일러는 알아서 해줌
```
if(portProperty is IntProperty){
  val port: Int = portProperty.value
}
```
- 더 이상 컴파일 오류가 발생하지 않는다.
- 캐스팅 결과가 null인 경우를 처리할 필요도 없음
- 스마트 캐스팅은 null에 대해서도 동작함
- 코틀린의 타입 계층에서 null 불가 타입인 Int는 null기능인 Int?의 하위 타입임
- Int 뿐 아니라 모든 타입이 마찬가지이다.
- 앞서 안전한 캐스팅이 실패하면 Null을 반환한다고 했음
```
val port: Int? = portProperty.value as? Int
```
- 만약 port 가 null인지 검사해서 null 이 아니라는 것을 확인했다면 port는 자동으로 null불가 타입으로 캐스팅됨
```
if (port != null){
  val port: Int = port
}
```
- 1장에서 값은 재할당 될 수 없다고 했는데 여기선 port값을 두번 정의했다.
- 이게 어떻게 가능한건가 했는데 코틀린의 변수 가리기라는 기능이다.

### 변수 가리기
- 먼저 변수 가리기가 없었다면 어떤 코드가 됐을지 생각해보라
- 이름이 서로 다른 변수 2개를 선언해야 했을 것
```
val portOrNull : Int? = portProperty.value as? Int
if(portOrNull != null){
  val port: Int = portOrNull //Ok
}
```
- 하지만 이 코드는 낭비적인데, 두가지 이유 떄문임
  - 첫번째로 변수 이름이 상당히 길어짐
  - 둘쨰로 이 코드 이후에 portOrNull 변수가 쓰일 일이 없다는 것
- null값은 그다지 쓸모 있는 값이 아니기 때문.
- 변수 가리기를 활용하면 같은 이름의 값을 다른 시야에 정의할 수 있음
- 변수 가리기를 사용하면 자칫 혼란스럽고 오류가 있는 코드를 작성하기 쉽게 때문에 주의가 필요함
- 그러나 이런 기능의 존재를 알 고 있는 것은 주요함
- 그럼에도 변수 이름은 최대한 명시적으로 지을 것을 권고함

### 팩토리 메서드의 모음
- 잠깐 샛길로 빠져 캐스팅과 변수 가리개를 살펴봤으니 다시 이전 코드 예레로 돌아가 두번째 팩토리 메서드를 구현해보자.
- 이 메서드는 Server 설정 객체를 생성할 것임
```
fun server(propertyStrings: List<String>): ServerConfiguration {
  val parsedProperties = mutableListOf<Property>()
  for(p in propertyStrings){
    parsedProperties += property(p)
  }
}
```
- 이 메서드는 앞서 구현한 property() 팩토리 메서드를 활용해 입력으로 받은 설정 파일의 각 줄을 Property 객체로 변환함
- 두번째 팩토리 메서드도 잘 동작할하는 것을 다음과 같이 테스트 할 수 있음
```
println(server(listOf("port: 8080", "environment: production")))
> ServerConfigurationImpl(properties=[IntProperty(name=port, value=8080),
StringProperty(name=enviornment, value = production)])
```
- 이 두 메서드는 서로 연관돼 있기 떄문에 같은 클래스에 두는 것이 좋다.
- 이 클래스를 Parser라고 부르자.
- 여기서는 파일을 실제로 파싱하지는 않고 이미 내용이 줄 단위로 주어진다고 가정함
- 그러나 파일의 내용을 실제로 읽는 작업도 그리 어렵지는 앖다(책의 뒷부분을 읽다 보면 알 수 있을 것)
- 이전 절에서 배운 정적 팩토리 메서드와 동반 객체 문법도 사용할 수 있다.
```
class Parser{
  companion object{
    fun property(prop: String): Property{

    }
    fun server(propertyStrings: List<String>): ...{

    }
  }
}
```
- 이 패턴을 사용하면 연관된 객체를 가족처럼 하나로 묶을 수 있다.
- 여기서는 ServerConfig이 여러 속성의 부모 역할을 한다.
- 위의 코드는 추상 팩토리를 구현하는 한 가지 방법에 지나지 않음
- 대신 인터페이스를 구현하는 방법도 있다.
```
interface Parser{
  fun property(prop: String): Property
  fun server(propertyStrings: List<String>) : ServerConfiguration
}
class YamlParser: Parser{
  // Yaml 파일을 읽는 구현체
}
class JsonParser: Parser{
  // Json 파일을 읽는 구현체
}
```
- 팩토리 메서드가 커져서 코드 양이 많아지는 경우 이 방법이 더 나을 것
- 마지막으로, 실제 코드에서는 추상 팩토리가 어디에서 쓰이는지 궁금할 수 있다.
- 한가지 예는 java.util.Collections 클래스임
- 이 클래스에는 emptyMap, emptyList, emptySet과 같은 메서드가 있는데 모두 다른 클래스를 생성한다.
- 그러나 동시에 모두 집합 자료 구조라는 공통점을 갖고 있음
