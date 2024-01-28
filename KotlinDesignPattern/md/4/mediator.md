## 중개인 패턴
- 우리의 게임 개발 팀이 심각한 문제에 맞딱뜨림
- 코드와 직접적으로 관련된 문제는 아님
- 최근에 마이클이 케니라는 앵무새를 데리고옴
- 그리고 케니는 품질 보증 업무를 담당하게 됨
```
interface QA{
    fun doesMyCodeWork(): Boolean
}

interface Parrot{
    fun isEating(): Boolean
    fun isSleeping(): Boolean
}

object Kenny : QA, Parrot{
    // 앵무새 스케줄에 따라 인터페이스 메서드 구현
}
```
- 케니는 2개의 인터페이스(품질 보증 업무를  하는 QA 인터페이스, 앵무새임을 나타내는 Parrot 인터페이스)를 구현하는 간단한 객체
- 이 품질 보증 담당 앵무새는 열정이 넘침
- 언제든 게임의 최신 코드를 테스트할 준비가 돼 있음
- 하지만 잠을 자거나 모이를 먹을 떄 방해받는 것을 좋아하지 않는다.
```
object Me
object MyCompany{
    val cto = Me
    val qa = Kenny

    fun taskCompleted(){
        if(!qa.isEating() && !qa.isSleeping()) {
            println(qa.doesMyCodeWork())
        }
    }
}
```
- 케니가 궁금한 것이 생기면 물어볼 수 있도록 내 전화번호를 알려줌
```
object Kenny: ...{
    val developer = Me
}
```
- 케니는 성실한 앵무새
- 하지만 게임에 버그가 너무 많은 나머지 품질 보증 담당 앵무새를 한마리 더 고용해야 할 것 같음
- 새 앵무새 이름은 브래드
- 케니가 한가할 때는 프로젝트를 더 잘 아는 케니에게 일을 맡김
- 그러나 케니가 바쁘면 브래드가 한가한지 살핀 후 그에게 일을 줌
```
class My Company{
    ...
    val qa2 = Brad

    fun taskCompleted() {
        ...
        else if(!qa2.isEating() && !qa2.isSleeping()){
            println(qa2.doesMyCodeWork())
        }
    }
}
```
- 경험이 부족한 브레드는 보통 케니에게 먼저 물어봄
- 또한 케니는 브래드에게 내 번호를 알려줌

```
object Geroge: QA, Owl {
    val developer = Me
    val mate = Kenny
    ...
}
```
- 문제는 조지가 열혈 축구 팬이라는 점임.
- 조지를 부를 때는 혹시 경기를 보고 있는 것은 아닌지 먼저 확인해야 함
```
class MyCompany{
    ...
    val qa3 = Geroge

    fun taskCompleted(){
        ...
        else if(!qa3.isWatchingFootball()) {
            println(qa3.doesMyCodeWork())
        }
    }
}
```
- 어느 날 케니는 평소와 달리 조지에게 질문을 하려고 함
- 조지는 굉장히 박식한 부엉이이기 때문
```
object Kenny : QA, Parrot {
    val peer = George
}
```
- 이번에는 샌드라가 등장함
- 그런데 그는 QA 업무가 아니라 카피라이터 일을 맡음
```
interface Copywriter{
    fun areAllTextsCorrect(): Boolean
}

interface Kiwi

object Sandra : Copywriter, Kiwi {
    override fun areAllTextsCorrect(): Boolean{
        return ...
    }
}
```
- 주요 릴리스가 있지 않는 한 그를 귀찮게 하지 않으려고 함
```
class MyMind{
    ...
    val translator = Sandra

    fun taskCompleted(isMajorRelease: Boolean) {
        ...
        if (isMajorRelease){
            println(translator.areAllTranslationsCorrect())
        }
    }
}
```
- 여기에는 몇가지 문제가 있음
  - 첫째 모든 이름을 기억하느라 머리가 터질 지경 독자들도 그럴 것
  - 둘쨰 직원들과 의사소통을 어떻게 해야 하는지 일일이 기억해야 함
    - 직원을 호출하기 전에 일을 시킬 수 있는 상태인지 항상 확인해야 함
  - 셋째 조지는 모든 것을 케니에게 물어보고, 케니는 조지에게 물어봄
    - 다행히 지금까지 케니에게 질문이 생겼을 때 조지는 축구 경기를 보고 있었다
    - 그리고 조지에게 질문이 생겼을 때 케니가 잠들어 있었음
    - 만약 그렇지 않았다면 둘은 전화기를 잡고 영원히 서로에게 질문을 하고 있었을 것
  - 넷째 가장 골치아픈 것은 케니가 곧 새로운 ParrotPi라는 회사를 창업해서 팀을 떠날 예정이라는 것
    - 그렇게 되면 얼마나 많은 코드를 변경해야 할지 생각해보셈
- 게임의 품질을 검사할 수 있으면 됨. 누가 이 복잡한 의사소토을 도와주면 좋겠음

### 중개인
- 중개인 디자인 패턴 통제욕 강한 괴짜와도 같음
- 그는 다른 객체끼리 직접 대화하는 것을 두고 보지 못함
- 그를 통하지 않고 직접 대화했다가는 크게 화를 낼 것
- 모두가 중개인과만 대화해야 함
- 왜 그래야 하는가? 객체 간 결합도를 낮추기 위해서임
- 모든 객체는 다른 객체를 직접 아는 대신 중개인 하나만 알면 됨
- 게임 개발 팀에서 마이클이 모든 프로세스를 간리하는 중개인 역할을 하기로 함
```
interface Manager{
    fun isAllGood(majorRelease: Boolean): Boolean
}
```
- 마이클만 모든 새 직원에 대한 정보를 갖음
```
object Micheal: Canary, ProductManager{
    private val kenny = Kenny(this)
    private val brad = Brad(this)

    override fun isAllGood(majorRelease: Boolean): Boolean {
        if(!kenny.isEating() && !kenny.isSleeping()){
            println(kenny.doesMyCodeWork())
        } else if(!brad.isEating() && !brad.isSleeping()) {
            println(brad.doseMyCodeWork())
        }
        return true
    }
}
```
- 중개인은 굉장히 간단한 인터페이스를 제공함으로써 여러 객체 간의 복잡한 상호 작용을 캡슐화함
- 마이클 객체만 알면 다른 직원 관리는 마이클이 할 것임
```
class MyCompany(private val manager: Manager) {
    fun taskCompleted(isMajorRelease: Boolean) {
        println(manager.isAllGood(isMajorRelease))
    }
}
```
- 또한 모든 직원이 마이클의 전화번호만 알고 있도록 하고, 내 전화번호는 바꿔버렸음
```
class Brad(private val manager: Manager) : ...{
    //Me에 대한 참조 제거
    ...
}
```
- 이제 누군가 질문거리가 생기면 먼저 마이클에게 연락할 것
```
class Kenny(private val manager: Manager) : ...{
    //조지를 비롯한 다른 직원에 대한 참조 제거
}
```
- 보다시피 이 패턴을 구현하면서 코틀린에 관해 새로 배우 것은 없음


### 중개인 패턴의 두 가지 유형
- 종개인 패턴에는 두가지 유형이 있음
- 바로 엄격한 중개인과 느슨한 중개인임
- 방금 본 유형이 엄격한 중개인에 해당됨
- 원하는 바를 중개인에 이야기하면 중개인이 바로응답해줌
- 반면 느슨한 중개인을 활용할 떄는 중개인에게 무슨 일이 일어났는지만 전달하고 즉시 답을 얻지는 않음
- 대신 중개인이 응답할 내용이 생기면 그때 객체를 따로 호출함

### 중개인 패턴 사용 시 주의사항
- 갑자기 마이클이 매우 중요한 직원이 됨
- 모든 직원이 마이클을 알고 있고
- 그가 아니면 직원 간 의사소통을 관리할 수 있는 직원은 없음
- 자칫 전지전능한 신 객체가 될 수도 있음
- 9장에서 설명하겠지만 신 객체를 만드는 것은 안티 패턴 중 하나임
- 아무리 마이클이 중요한 객체이더라도 그가 무엇을 해야 하며 무엇을 하면 안되는지 명확하게 정의함
  - 그 중에서도 중개인이 해서는 안되는 일을 정의하는 것이 중요함
- 다음 절에서도 이 예제를 통해 새로운 동작 패턴을 살펴볼 것

