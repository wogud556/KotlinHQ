## 확장함수
- 1장에서 다룰 마지막 기능은 확장함수임
- 간혹 final로 선언된 클래스의 기능을 갖고 싶을 때가 있다.
- 예를 들어 앞 절에서 구현한 hidePassword() 함수가 문자열에도 있다면 좋을 것
- 한가지 방법은 문자열을 감싸는 클래스를 직접 선언하는 것
```
data class Password(val password: String){
    fun hidePassword() = "*",repeat(password.length)
}
```
- 그러나 이 방법은 꽤 낭비적임
- 귀찮은 껍데기가 하나 더해졌다.
- 코틀린에는 더 좋은 방법이 있는데 클래스를 상속하지 않고 확장하려면 함수 
```
fun String.hidePassword() = "*".repeat(this.length)
```
- 일반적인 최상위 함수 선언과 거의 똑같아 보임
- 하지만 결정적인 차이가 있는데, 함수 이름 앞에 클래스 이름이 들어감.
- 이렇게 붙는 클래스 이름을 메서드 수신자 라고 부름
- 함수 본문에서는 this는 함수를 호출한 특정 문자열 객체를 가리킬 것임
- 이제 문자열을 선언하고 새로운 함수를 호출해보자
```
val password: String  =  "secretpassword"
println("비밀번호: ${password.hidePassword()}")
```
- 다음과 같이 출력됨
```
> Password: *******************
```
- 도대체 무슨 흑마술을 부린걸까?
- 방금 final 클래스에 함수를 추가했음
- 이론적으로는 불가능하다.
- 이건 코틀린 컴파일러 기능 중 하나임 확장 함수를 컴파일하면 대강 다음과 같은 코드가 나옴
```
fun hidePassword(this:String){
    "*".repeat(this.length)
}
```
- 보다시피 확장함수는 사실 그냥 보통의 전역함수임
- 다만 첫번째 인수가 확장되는 클래스의 인스턴스임
- 고 언어에서 구조체에 대해 메서드를 실행하는 것과 비슷하다고 볼 수있다.
- 따라서 비밀번호를 별표로 출력하는 코드를 다음과 같이 작성할 수 있음
```
val password: String = "secretpassword"
println("password: ${hidePassword(password)}")
```
- 이런 이유로 확장 함수는 클래스의 멤버 함수를 덮어 쓸 수는 없으며, private이나 protected로 선언된 속성에 접근할 수 없다.
