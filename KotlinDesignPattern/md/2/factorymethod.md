## 팩토리 메서드 패턴
- 객체를 생성하는 메서드에 관한 디자인 패턴임
  - 객체를 생성하는 메서드가 왜 필요할까? 객체를 생성하기 위해 생성자가 있는게 아닌가?
- 생성자 만으로는 한계가 있기 때문이다.
- 예를들어 체스 게임을 만든다고 가정하면, 게임의 상태를 텍스트 파일로 저장하고 다시 읽어서 상태를 복원하는 기능을 구현하기 원함
- 체스판의 크기는 정해져 있다. 따라서 각 기물의 위치와 종류만 저장하면 된다.
- 여기서는 대수 기보법을 사용할 것인데 C3에 있는 퀸을 qc3라고 저장하고, A8에 있는 폰을 pa이라고 저장하는 식임
- 파일을 읽어서 문자열 배열을 만드는 기능은 이미 구현했다고 치자
  - 한편으로는 이 기능에 앞서 살펴본 싱글톤이 더 나음
- 기보가 리스트로 주어질 때 체스판을 만들어 내는 코드는 다음과 같음
```
// 모든 기물의 목록
val notations = listOf("pa8", "qc3",...)
val pisces = mutableListOf<ChessPiece>
for (n in notations) {
    pieces.add(createPiece(n))
}
println(pieces)
```
- createPiece함수를 구현하기 전에 먼저 기물의 공통적인 속성이 무엇인지 결정해야 함
- 기물을 나타내는 인터페이스를 다음과 같이 만들 것
```
interface ChessPiece{
    val file: Char
    val rank: Char
}
```
- 코틀린에서 인터페이스는 속성을 가질 수 있다는 사실에 주목하라.
- 굉장히 강력한 기능임
- 각 기물은 이 인터페이스를 구현하는 데이터 클래스가 될 것
```
data class Pawn(
    override val file : Char,
    override val rank : Char
) : ChessPiece

data class Queen(
    override val file: Char,
    override val rankL Char
) : ChessPiece
```
- 이제 createPiece함수를 작성하는 일만 남았다.
```
fun createPiece(notation: String): ChessPiece{
    
    // 분해선언
    val(type, file, rank) = notation.toCharArray()

    return when (type){
        'q' -> Queen(file, rank)
        'p' -> Pawn(file, rank)
        //
        else -> throw RuntimeException("알 수 없는 기물 종류 : $type")
    }
}
```
- 이 함수의 역할이 무엇인지 살펴보기 전 먼저 등장한 문법 4개를 소개한다.

#### toCharArray()
- toCharArray는 문자열을 문자 배열로 쪼개는 함수임
- 대수기보법에서 각 기물 3개의 문자로 표현된다고 가정하므로 0번 위치는 문자는 기물의 종류를, 1번 위치의 문자는 기물이 위치한 행 번호(file(파일)잉)를, 마지막 문자는 기물이 위치한 열 번호(Rank라고 부른다)를 나타냄

#### 분해선언
- 다음으로 3개의 값(type, file, rank)이 괄호 안에 있는 것을 볼 수 있음
- 이것을 분해 선언이라고 함
- 자바스크립트와 같은 언어에도 이런 문법이 있는데
- 모든 데이터 클래스는 이처럼 분해할 수 있다.
```
val type = notation.toCharArray()[0]
val file = notation.toCharArray()[1]
val rank = notation.toCharArray()[2]
```

#### when 표현식
- 위의 코드에서 when 표현식은 기물 종류를 나타내는 문자에 따라 ChessPiece 인터페이스를 구현하는 여러 구현체 중 하나를 인스턴스화 함
- 바로 이게 팩토리 메서드 디자인 패턴의 역할이라는 것을 기억해야 함
- 이해도를 높이기 위해 나머지 기물을 생성하는 로직도 자유롭게 작성해볼것
- 마지막으로 함수의 제일 아랫부분에 throw 표현식이 처음 등장함
- 이 표현식은 이름에서 알 수 있듯 예외를 '던져서' 프로그램의 정상 동작을 중단시킴
- 5장에서 예외를 다루는 법을 살펴볼 것
- 현업에서 팩토리 메서드 패턴은 라이브러리 코드가 XML이나 Json, YMAL같은 설정 파일을 파싱해서 런타임 객체를 생성할 때 자주 사용됨
