## 생성자 오버로딩
- 자바에서 생성자를 오버로딩하는 것은 흔한 일임
- 예를 들어 다음과 같은 자바 클래스를 살펴본다
- 초기화를 위해 name 값은 필수로 입력해야 하며, resetPassword는 값을 입력하지 않으면 기본적으로 false가 됨
```
class User {
    private final String name;
    private final boolean resetPassword;
    public User(String name) {
        this(name, true)
    }
    public User(String name, boolean resetPassword) {
        this.name = name;
        this.resetPassword = resetPassword;
    }
}
```

- 코틀린에서는 constructor 키워드를 통해 여러개의 생성자를 정의함으로 이를 구현할 수 있음

```
class User(val name : String, val resetPassword: Boolean) {
    constructor(name :String) : this(name, true)
}
``` 
- 클래스 본문에 저으이된 부 생성자는 주 생성를 호출하면서 두번째 인자에 true라는 기본값을 넘겨줄 것
- 하지만 주 생성자만 사용하되 기본값과 명명 인수를 활용하는 것이 더 낫다.
```
class User(val name: String, val resetPassword: Boolean = true)
```
- 부 생성자는 반드시 This 키워드를 통해 주 생성자를 호출해야 한다는 사실을 유념
- 유일한 예외는 주 생성자가 기본 생성자인 경우

```
class User {
    val resetPassword: Boolean
    val name: String

    constructor(name: String, resetPassword: Boolean = true) {
        this.name = name
        this.resetPassword = resetPassword
    }
}
```
