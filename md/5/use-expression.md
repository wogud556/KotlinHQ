## 문 대신 식 사용하기
- 문은 아무것도 반환하지 않는 코드 블록을 의미함
- 반면 식은 새로운 값을 반환함
- 문은 아무 결과를 반환하지 않으므로 상태를 변경할 때만 쓸모가 있음
- 이를테면 변수의 값을 바꾸거나 자료 구조를 변경하거나 IO를 수행하는 등
- 함수형 프로그래밍에서 상태 변경을 최소화하기를 원함
- 이론적으로 식을 더 많이 사용할수록 함수는 순수해지고 순수 함수의 모든 장점도 따라옴
- 지금까지 if 조건식을 많이 사용해 봤으니 여러가지 장점 중 하나는 확실히 느꼈을 겻
- 코드가 간결해진다는 것인데 이때문에 다른 언어의 if문을 사용할 때 보다 오류 가능성도 적어짐

### 패턴 매칭
- 강화 버전의 switch/case라고 할 수 있다.
- when 표현식의 사용법은 1장에서 이미 살펴봄. 여기서는 이 개념이 왜 함수형 패러다임에서 중요한지 간단히 알아본다
- 자바에서 switch문에 몇가지 기본 타입과 문자열, 열거형 밖에 사용할 수 없음
- 다음 코드를 살펴본다.
- 다형성을 구현하는 방법을 설명하기 위해 흔히 사용하는 예시
```
class Cat: Animal {
    fun purr(): String {
        return "가르랑 가르랑"
    }
}
class Dog : Animal {
    fun bark(): String {
        return "멍멍";
    }
}

interface Animal
```
- 여러 함수 중에서 어떤 것을 호출할지 결정하는 코드는 다음과 같음
```
fun getSound(animal: Animal): String {
    val sound: String? = null;
    if (animal is Cat) {
        sound =  (animal as Cat).purr();
    }
    else if (animal is Dog) {
        sound = (animal as Dog).bark();
    }

    if(sound == null) {
        throw RuntimeException();
    }
    return sound
}
```
- 이 코드는 런타임에 getSound에서 어떤 함수를 호출할 지 결정함
- 여러개의 return 문을 사용하면 함수 길이를 더 줄일 수 있음
- 하지만 return을 여러번 하는 것은 실무에서 보통 나쁜 습관으로 간주함
- 또한 switch문에서는 클래스를 사용할 수 없기 때문에 if문을 사용한 것을 볼 수 있음
```
fun getSound(animal: Animal) = when(animal) {
    is Cat -> animal.purr()
    is Dog -> animal.bark()
    else -> throw RuntimeException("알 수 없는 동물")
}
```
- when은 식이기 때문에 이전 코드에서처럼 임시 변수를 선언하지 않아도 됨
- 또한 패턴 매칭을 사용하면 어떠한 타입 검사나 캐스팅도 필요 없게 됨
- 명령형 프로그래밍의 if문을 함수형 프로그래밍 패러다임에 가까운 when 표현식으로 대체하는 방법을 알아봄
- 이제 명령형 프로그래밍의 반복문을 재귀 호출로 대체하는 방법을 살펴본다