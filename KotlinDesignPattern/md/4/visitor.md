## 방문자 패턴
- 방문자 디자인 패턴은 3장에서 다룬 합성 패턴의 절친임
- 이 패턴을 사용하면 트리 구조의 복잡한 구조에서 데이터를 추출하거나 각 노드에 동작을 추가할 수 있다.
- 데코레이터 패턴이 객체 하나의 동작을 바꾼다면 방문자 패턴은 자료 구조에 포함된 여러 객체의 동작을 바꿈
- 게으른 아키텍트가 되고자 헀던 괴짜 소프트웨어 아키텍트의 계획은 어느정도 성공한 것 같음
- 책임 사슬을 사용한 몇몇 요청 응답 시스템은 잘 운영되고 있고 커피 마실 시간을 꽤 벌어줬다.
- 그런데 몇몇 개발자가 수군대는 소리가 들림 "저 아키텍트 순 사기꾼 아냐?"
- 그런 개발자들에게 혼란을 주기 위해 최신 기술을 다루는 글의 링크를 모아 매주 이메일을 보내려고 함
- 물론 직접 기술 블로그를 읽지는 않을 것임
- 그는 그냥 유명한 기술 사이트 몇 개에서 링크를 수집하고자 함

### 크롤러 작성하기
- 다음과 같은 자료 구조를 보자. 반복자 디자인 패턴을 배울 떄 다뤘던 것과 비슷함
```
Page(
    Container(Image(),
                Link(),
                Image()),
    Table(),
    Link(),
    Container(
                Table(),
                Link()),
    Container(
                Image(),
                Container(Image(),
                            Link()))
)
```
- Page는 다른 HTML원소를 담고 잇는 역할을 함(하지만 HtmlElement 자체를 들고 있지는 못함)
- Container는 다른 Container나 Table, Link, Image와 같은 원소를 들고 있을 수 있음
- Image는 src 속성에 이미지 링크 주소를 들고 있고, Link는 href 속성에 링크 주소를 들고 있다
- 괴짜 아키텍트는 이 객체에서 모든 URL을 추출하기 원함
- 먼저 이 객체 트리의 최상위 객체를 입력으로 받고 (이 예제에서는 Page 객체가 됨) 모든 링크 주소의 리스트를 반환하는 함수를 작성할 것
```
fun collectLink(page: Page) : List<String>{
    // 임시 변수 필요 없음
    return LinksCrawler().run{
        page.accept(this)
        this.links
    }
}
```
- run을 사용하면 블록의 반환값을 정할 수 있음
- 이 코드에서는 수집한 Links 객체를 반환할 것임
- run 블록 내에서 This는 수신 객체(지금은 LinksCrawler 객체)를 가리킴
- 자바에서는 일반적으로 새 기능을 받기 위한 메서드를 클래스마다 구현하는 방식으로 방문자 패턴을 구현함
- 여기서도 비슷한 방법을 사용할 것임
- 모든 클래스에 메서드를 구현하지는 않고, 내부에 다른 원소를 담고 있는 Container와 Page클래스에만 구현할 것
```
private fun Container.accept(feature: LinksCrawler) {
    feature.visit(this)
}

// 단일식 함수로 다음과 같이 줄여 쓸 수 있음
private fun Page.accept(feature: LinksCrawler) = feature.visit(this)
```
- 이제 구현해야 하는 기능 클래스(LinksCrawler)는 내부적으로 집합 자료 구조를 들고 있어야 하며, 외부에서 읽을 수 있도록 이를 노출해야 함
- 자바라면 해당 멤버에 대해 설정자 함수는 정의하지 않고, 접근자 함수만 정의할 것
- 코틀린에서는 다음과 같이 뒷받침 필드 없이 값을 선언할 수 있음
```
class LinkCrawler{
    private var _links = mutableListOf<String>()

    val links 
        get()= _links.toList()
}
```
- 외부에 노출되는 자료 구조는 불변이길 원하기 때문에 toList()함수를 호출했음
> 트리 형태의 자료 구조에서 반복을 수행할 때는 반복자 패턴을 사용하면 더욱 간단하게 구현할 수 있음

- Container 클래스가 입력으로 들어오면 그냥 하위 원소를 계속해서 방문함
```
class LinksCrawler{
    ...
    fun visit(page: Page){
        visit(page.elements)
    }

    fun visit(container: Container) = 
        visit(container.elements)
    ...
}
```
- 부모클래스를 봉인 클래스로 선언하면 컴파일러에게 더욱 도움이 됨
- 봉인 클래스에 대해서는 4장 앞부분에서 상태 패턴을 배우면서 설명한 바 있음
- 코드는 다음과 같다

```
sealed class HtmlElement

class Container(...) : HtmlElement() {
    ...
}

class Image(...) : HtmlElement() {
    ...
}

class Link(...) : HtmlElement() {
    ...
}

class Table : HtmlElement()
```
- 제일 흥미로운 부분은 트리 구조의 잎(말단)에 해당하는 부분을 처리하는 로직이다

```
class LinksCrawler{
    ...
    private fun visit(elements: List<HtmlElement>) {
        for(e in elements) {
            when(e) {
                is Container -> e.accept(this)
                is Link -> _links.add(e.href)
                is Image -> _links.add(e.src)
                else -> {}
            }
        }
    }
}
```
- 특정한 경우에는 아무런 처리를 하고 싶지 않을 수도 있음
- 그래서 else 코드 블록은 else -> {}와 같이 비어 있음
- 여기도 코틀린의 스마트 캐스팅이 사용됨
- 원소가 Link 타입이라는 것을 확인한 다음에는 타입 안전하게 href 속성에 접근할 수 있다는 점을 확인하라
- 컴파일러가 캐스팅을 알아서 해주기 때문
- Image 원소도 마찬가지
- 어쨌든 원하는 기능은 잘 구현했지만, 이 패턴이 얼마나 쓸모 잇는지 논란의 대상임
- 예제에서 볼 수 있다시피 코드가 장황해질 뿐 아니라 기능을 받아들이는 클래스와 방문자 클래스 사이에 강한 결합이 생기기 때문