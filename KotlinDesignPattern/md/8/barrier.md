## 장벽 패턴
- 장벽 디자인 패턴을 사용하면 프로그램을 잠시 멈추고 여러개의 동시성 작업이 완료되기를 기다릴 수 있음
- 일반적으로는 여러 곳에서 자료를 가져올 때 장벽 패터늘 사용함
- 예를 들어 다음과 같은 클래스가 있다 하다
```
data class FavoriteCharacter(
    val name: String, 
    val catchpharase: String,
    val picture: ByteArray = Random.nextBytes(42)
)
```
- 여기서 catchpharase 데이터와 picture 데이터를 각각 서로 다른 서비스를 통해 받아와야 한다고 가정함
- 다음과 같이 두 데이터를 비동기적으로 받아오려고 함

```
fun CoroutineScope.getCatchpharaseAsync (
    characterName: String
) = async {...}

fun CoroutineScope.getPicture (
    characterName: String
) = async {...}
```
- 두 데이터를 받아오는 동시성 코드를 가장 기본적인 방법으로 구현하면 다음과 같음
```
suspend fun fetchFavoriteCharacter(name: String) = coroutineScope {
    val catchpharase = getCatchphraseAsync(name).await()
    val picture = getPicture(name).await()
    FavoriteCharacter(name, catchphrase, picture)
}
```
- 하지만 이 코드에는 문제가 있는데, catchphrase데이터 수신을 완료하기 전까지는 picture데이터를 받아오지 못한다는 것
- 즉 이 코드는 불필요한 순차성을 갖고 있음
- 어떻게 개선할 수 있는지 살펴본다

### 데이터 클래스를 장벽으로 사용하기
- 앞의 코드를 조금 바꿔서 다음과 같이 작성할 수 있음
```
suspend fun gfetch FavoriteCharacter(name: String) = coroutineScope {
    val catchphrase = getCatchphraseAsync(name)
    val picture = getPicture(name)
    FavoriteCharacter(name, catchpharase.await(), picture.await())
}
```
- 이렇게 await 함수 호출을 데이터 클래스 생성자로 옮기면 모든 코루틴을 동시에 시작하고 모든 코루틴이 완료되기를 기다리게 됨 원하던 대로
- 데이터 클래스를 장벽으로 사용하면 좋은점이 있는데, 쉽게 분해가 가능하다는 점

```
val (name, catchphrase) = fetchFavoriteCharacter("이니고 몬토야")
println("$name: $catchpharase")
```
-  여러 비동기 작업이 서로 다른 타입의 결과를 반환할 떄는 이 방법을 사용하면 좋음
-  하지만 같은 타입의 결과를 기다려야 하는 상황도 있음
-  예를 들어 마이클 (게임 개발 팀의 카나리아), 테일러(바리스타),그리고 내가 좋아하는 영화 주인공을 이야기하는 코드를 만들어본다
  
```
object Micheal {
    suspend fun getFavoriteCharacter() = coroutineScope {
        async {
            FavoriteCharacter("터미네이터", "Hasta la vista, baby")
        }
    }
}

object Taylor {
    suspend fun getFavoriteCharacter() = coroutineScope {
        async {
            FavoriteCharacter("돈 비토 코를레오네", 그 자에게 거절할 수 없는 제안을 하겠다.)
        }
    }
}

object Me {
    suspend fun getFavoriteCharacter() = coroutineScope {
        async {
            // 이미 대답을 준비했지?
            FavoriteCharacter ("이니고 몬토야", "안녕, 난 이니고 몬토야이다")
        }
    }
}
```
- 이번에는 세 객체가 거의 비슷함
- 반환값의 내용만 다를 뿐임
- 이 경우 결과를 수집하기 위해 리스트를 사용할 수 있음

```
val characters: List<Deferred<FavoriteCharacter>> = listOf(
    Me.getFavoriteCharacter(),
    Tayler.getFavoriteCharacter(),
    Michael.getFavoriteCharacter(),
)
```
- 리스트의 타입을 눈여겨보라
- 지연된 FavoriteCharacter를 모아 놓은 리스트임
- 이런 자료 구조에 대해서는 awaitAll()이라는 함수를 호출할 수 있는데, 이 함수는 장벽의 기능을 함

```
println(characters.awaitAll())
```
- 이처럼 같은 타입을 갖는 여러 비동기 작업을 사용하며 다음 단계로 넘어가기 전에 모든 작업이 완료되기를 원한다면 awaitAll()함수를 사용하라
- 장벽 디자인 패턴은 여러 비동기 작업이 한 곳으로 모이도록 함