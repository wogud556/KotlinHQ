## 타입 이해하기

### 기본 타입
- 어떤 언어에서는 원시타입 (primitive type) 객체로 구분
- 코틀린에서는 그런 구분 자체가 없다 모든 타입에서 동등함
- 대부분의 타입은 자바와 비슷
- 예외로 친다면 자바의 Integer는 코틀린에서 Int임
- 자바의 Void 는 코틀린에서 Unit임

#### 예시
- 수
  - Int, Long, Double
- 문자열
  - String
- 부울
  - Boolean
- 문자
  - Char

### 타입추론
```
var greeting = “hello Kotlin”
Println
```
- 이 다음 코드에서 오류를 발생시킬 수 있다
```
greeting = 1
// 코틀린에서는 자동으로 타입 추론을 하므로 자료형 선언이 되어있지 않다 하더라도 숫자형으로 맘대로 바꿀 수 없음
```
- 오류가 발생하는 이유는 변수의 타입을 딱 한번만 정의하기 때문
- 만약 변수를 명시적으로 선언하려고 한다면 아래와 같이 선언한다.
```
var greeting : String = “hello Kotlin”
```

### 값
- 자바에서는 변수를 final로 선언할 수 있음
- sfinal 변수는 딱 한번만 할당되고 이 변수에 대한 참조는 사실상 불변(immutable)
```
final String s = “hi”;
s = “Bye”;
```
- 코틀린에서 이 방법을 한번에 바꿔버리는 방법이 있다
- val을 사용하는 방법
- var와 val의 차이라고 할 수 있으며 val은 불변된 값 지정이 가능하다
```
val greeting = “Hi”//final 과 같은 역할을 한다.
greeting = “Bye”//불가능
```
- 변수보다 값을 더 사용하는 것이 바람직한데, 불변 데이터가 값을 추적하기 알맞기 때문


### 비교와 동등성
- 자바에서는 객체끼리 비교시 == 연산자를 사용하면 원하는 결과를 얻지 못한다는 것을 익히 알고 있을것
- == 연산자는 두 객체가 동등한지 비교하는 대신 두 포인터가 같은 객체를 바라보고 있는지 비교하기때문
- 대신 자바 객체비교에 equals()를 사용하고, ==는 원시타입 변수끼리 비교할때만 사용함
- 코틀린 또한 ==연산자를 equals()로 해석한다
- 참조 동등성을 확인하려면 === 을 사용
- 그러나 기본타입 중에는 참조 동등성 비교가 불가능한 타입도 있음.

### 함수선언
- 자바에서 모든 메서드는 클래스나 인터페이스 안에 있어야 함
- 클래스나 인터페이스의 정보를 전혀 사용하지 않더라도 그렇다
- 코틀린에서는 클래스 바깥에도 함수를 선언할 수 있다. main()함수가 그런 케이스임
- 함수를 선언할 때 fun 키워드를 사용한다.
- 인수의 경우 인수 앞이 아니라 뒤에 온다.
```
fun greet(greeting: String){
	println(greeting)
}
```
- 결과를 반환해야 한다면 반환형은 함수 선언 뒷부분에 기술
```
fun getGreeting():String{
	return “Hello, Kotlin!”
}
```
```
// 아래 코드도 직접 실행해보자
fun main(){
	greet(getGreeting())
}
```
- 함수가 아무것도 반환하지 않는다면 반환형은 아예 생략할 수 있다
- 즉 void 타입(코틀린상에서 Unit)으로 선언할 필요가 없음
- 함수가 식 하나로 표현될 만큼 짧다면 (위의 getGreeting()함수같이) 반환형과 중괄호를 지우고 다음과 같이 더 짧게 쓸 수 있다
```
fun getGreeting() = “hello, Kotlin”
```
- 이 때 코틀린 컴파일러는 함수가 String 타입을 반환하는 것을 추론할 것
- 어떤 스크립트 언어에서는 함수 선언 순서가 중요하나, 코틀린에서는 아님
- 가령 예제 코드의 main 함수는 시야에 있는 모든 함수를 호출할 수 있음
- Main 함수는 시야의 모든 함수를 호출 할 수 있음
- main 함수보다 뒤에 선언됐더라도 호출할 수 있음

### null 안전성
- nullpointException 이 발생되는 주 원인은 어떤 객체든 null일 수 있기 대문에 발생된다
- 이러한 문제를 해결하기 위해 자바 8에서는 새로운 클래스인Optional 을 사용하여 값이 없을 수 있다는 것을 표현할 수 있다.
```
var optional = Optional.of(“null 이 아닌 문자열”)
if(optional.isPresent()){
	System.out.println(optional.get().length());
}
```
- 그러나 인수로 받는 함수도 null를 전달할 수 있고 그렇게되면 런타임 오류가 발생된다.
```
void printLength(Optional<String> optional) {
	if(optional.isPresent()){
		System.out.println(optional.get().length());
	}
}
PrintLength(null);
```
- 코틀린의 경우 컴파일 시 null 검사를 수행함
```
val s: String = null // 컴파일 불가
```
- 아래 코틀린으로 작성된 PrintLength() 함수가 있다
```
fun printLength(s: String){
	println(s.length)
}
```
- 위 함수에 Null 을 전달하면 컴파일이 되지 않음
```
printLength(null)
// “Null cannot be a value of a non-null type String” 오류 발생
```
- 만약에 null을 받고 싶다면
```
fun printLength(stringOrNull : String?){…}
```
- 위 코드와 같이 자료형 뒤에 ?를 입력하여 null 이 들어갈 수 있음을 명시함
