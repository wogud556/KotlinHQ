## 상수 효율적으로 사용하기
- 자바의 모든 것은(원시타입 빼고) 객체이기 때문에 모든 상수를 객체의 정적 멤버로 만들어 버리는 것이 익숙함
- 코틀린에는 동반 객체가 있기 때문에 여기에 상수를 선언하곤 함
```
class Spock {
    companion object {
        val SENSE_OF_HUMOR = "None"
    }
}
```
- 이 코드도 잘 동작하지만, 어쨌단 동반 객체도 객체라는 사실을 잊으면 안됨
- 따라서 이 코틀린 코드를 자바 코드로 번역하면 다음과 같음
```
public class Spock {
    private static final String SENSE_OF_HUMOR = "None";
    
    public String getSENSE_OF_HUMOR() {
        return Spock.SENSE_OF_HUMOR;
    }
}
```
- 이 예제에서 코틀린 컴파일러는 상수를 가져오는 접근자 함수를 생성함
- 상수를 사용하는 데에 단계가 하나 더 추가된 셈
- 실제로 상수를 사용하는 코드를 살펴보면 다음과 같을 것
```
String var0 = Spock.Companion.getSENSE_OF_HUMOR()
System.out.println(var0);
```
- 상수를 가져오기 위해서 메서드를 호출함
  - 별로 효율적인 방법은 아님
- 이번에는 이 필드에 const 키워드를 붙여본다.

```
class Spock {
    companion object {
        const val SENSE_OF_HUMOR = "None"
    }
}
```
- 컴파일러가 만들어 내는 바이트코드는 다음과 같음
```
public class Spock {
    public static final String SENSE_OF_HUMOR = "None";
}
```
- 호출부는 다음과 같이 생김
```
String var1 = "None";
System.out.println(var1);
```
- 코드 어디에서도 더 이상 Spock 클래스를 참조하지 않는다는 사실에 주목해라
- 컴파일러가 상수 값을 인라인화 한 것
- 상수이므로 값이 절대 바뀔 일이 없고 안전하게 인라인화할 수 있음.
```
const val SPOCK_SENSE_OF_HUMOR = "NONE"
```
- 네임 스페이스가 필요하다면 객체 안에 둘 수도 있음
```
object SensesOfHumor {
    const val SPOCK = "None"
}
```
- 이제 상수를 효율적으로 사용하는 법을 알았으니 생성자를 잘 쓰는 법을 알아볼 차례임