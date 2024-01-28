## 템플릿 메서드 패턴
- 어떤 일정은 절대 바뀌지 않지만 어떤 일정은 유동적
- 실제로 회의 참석이라고 돼있는 2개의 시간대에서 몇개의 회의가 잡힐지는 알 수 없음
- 이 두 시간대를 준비 시간과 정리 시간으로 사용해서 유동적인 일과를 표현해 보기로 했음
- 마침 그 사이에는 점심 식사가 있다
- 아키텍트에게 점심 식사는 신성한 일과이기 때문
- 자바 식으로 이를 구현하는 법은 뻔함.
- 먼저 추상 클래스를 하나 만듬, 그리고 직접 구현하고자 하는 메서드에는 모두 private을 붙임


```
abstract class DayRoutine {
    private fun arriveToWork() {
        println("부장님 안녕하세요! 저는 가끔 사무실에 등장합니다")
    }

    private fun drinkCoffee() {
        println("오늘은 커피 맛이 좋다")
    }

    private fun goToLunch() {
        println("햄버거랑 감자튀김 세트")
    }

    private fun goHome() {
        //아무도 알아채지 못하게 하는게 핵심
        //절대로 조용히 해야함
        println()
    }
}
```
- 매일 바뀌는 일과를 나타내는 메서드에는 모두 abstract메서드가 붙여야 함
```
abstract class DayRoutine {
    ...
    abstract fun doBeforeLunch()
    ...
    abstract fun doAfterLunch()
    ...
}
```
- 유동적이긴 하지만 기본 구현을 작성하고 싶은 함수는 public 함수로 남겨둠
```
abstract class DayRoutine {
    ...
    open fun bossHook() {
        // 부장님이 불러 세우지 않기를        
    }
}
```
- 코틀린에서 public이 기본 접근 제한자라는 것을 기억한다
- 마지막으로 전체 알고리즘을 실행하는 메서드가 필요
- 이 메서드는 기본적으로 Final임

```
abstract class DayRoutine {
    ...
    fun runSchedule() {
        arriveToWork() 
        drinkCoffee()
        doAfterLunch()
        goToLunch()
        doAfterLunch()
        goHome()
    }
}
```
- 이제 월요일 일과가 필요하다면 빠진 부분만 구현하면 됨
```
class MondaySchedule: DayRoutine() {
    override fun doBeforeLunch() {
        println("쓸 데 없는 회의")
        println("코드 리뷰, 이게 무슨 코드냐")
    }


    override fun doBeforeLunch() {
        println("랄프와 회의")
        println("다른 아키텍트와 농담 따먹기")
    }

    override fun doBeforeLunch() {
        println("잠깐 사무실로 오실?")
    }
}
```
- 코틀린에는 조금 더 나은 방법이 있나?
- 지금까지처럼 코틀린은 간결성을 선사함
- 앞서 다른 패턴에서와 마찬가지로 함수를 사용하면 됨
- 이 일과에는 유동적인 부분이 세 곳이 있는데, 필수 일과 2개(소프트웨어 아키텍트는 점심 식사 전과 후에 무언가를 반드시 해야 함), 필수적이지 않은 일과가 1개(슬그머니 퇴근하려는데 부장님이 부러세울 수 있음)있다.
```
fun runSchdule(beforeLunch: () -> Unit,
                afterLunch: () -> Unit,
                bossHook:(() -> Unit)? = fun(){println() })
```
- 3개의 다른 함수를 인수로 받는 함수를 작성할 것
- 처음 둘은 필수 인수이며, 세 번째 인수는 아예 전달하지 않을 수 있고 null을 전달해서 명시적으로 실행되지 않기를 원한다는 것을 나타낼 수도 있음
```
fun runSchedule(...) {
    ...
    arriveToWork()
    drinkCoffee()
    beforeLunch()
    goToLunch()
    afterLunch()
    bossHook??.let{ it() }
    goHome()
}
```
- 이 함수 내부에 알고리듬을 작성할 것임
- beforeLunch()와 afterLunch()를 호출하는 부분은 어렵지 않음
- 인수로 전달된 함수를 그냥 실행하는 것뿐이니까
- 세 번째 인수인 bossHook은 Null일 수 있기 때문에 null이 아닐 경우에만 실행해야 함
- 이를 위해 다음과 같은 문법을 사용하는데
```
?.let{ it() }
```
- 그런데 항상 직접 구현해줘야하는 다른 함수들은 어떻게 할까?
- 코틀린에는 지역함수라는 개념이 있다. 다른 함수안에 있는 함수를 말함
```
fun runSchedule(...) {
    fun arriveToWork(){
        println("좋은 아침입니다!")
    }

    val drinkCoffee = {println("우유 남는 게 좀 있나?")}

    fun goToLunch() = println("이탈리아 음식을 먹고 싶다")

    val goHome = fun() {
        println("드디어 휴식!")
    }

    arriveToWork()
    drinkCoffee()
    ...
    goToLunch()
    ...
    goHome()
}
```
- 모두 지역 함수를 선언하는 유효한 방법
- 어떻게 정의하든 똑같이 호출할 수 있음
- 지역 함수는 선언된 부모 함수 내에서만 접근 가능하며, 공통된 로직을 외부에 노출하지 않고서도 함수로 추출할 수 있는 방법
- 알고리즘의 뼈대를 정의하되 세부 동작은 나중에 다른 누군가가 결정하도록 하는것 이것이 템플릿 메소드 패턴임