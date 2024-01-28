## 재귀호출
- 함수가 자기 자신을 새로운 인수로 다시 호출하는 것
- 깊이 우선 탐색을 비롯해 많은 유명 알고리듬이 재귀 호출을 사용한다
- 다음의 예제 코드는 재귀 호출을 사용하여 주어진 리스트의 모든 수를 더하는 매우 비효울 적인 함수
```
fun sumRec(i : Int, sum: Long, numbers: List<Int>): Long{
    return if(i == numbers.size) {
        return sum
    } else {
        sumRec(i + 1, numbers[i] + sum, numbers)
    }
}
```
- 재귀 호출을 잘못 사용하면 호출 스택이 너무 깊어져서 스택 오버플로 오류가 발생할 수 있기 떄문에 재귀 호출을 되도록 지양하기도 함
- 100만개의 수가 들어있는 리스트로 위의 예제 함수를 호출하면 스택 오버플로를 볼 수 있음
```
val numbers = List<1_000_000> {it}
println(sumRec(0, numbers))
// 생각보다 금방(7천 정도) 오류 발생
```
- 하지만 코틀린은 꼬리 재귀라는 최적화를 지원함
- 꼬리 재귀의 최대 장점은 공포의 스택 오버플로를 방지할 수 있다는 것
- 함수에 재귀 호출이 하나 뿐이라면 꼬리 재귀 최적화를 적용할 수 있음
- tailrec이라는 새 키워드를 사용해서 위의 재귀 함수를 다시 작성해본다
```
tailrec fun sumRec(i : Int, sum: Long, numbers: List<Ints>) : Long {
    return if(i == numbers.size){
        return sum
    } else {
        sumRec(i + 1, numbers[i] + sum, numbers)
    }
}
```
- 이제 컴파일러는 함수 호출을 최적화해 절대 스택 오버플로가 발생하지 않도록 할 것임
- 그러나 병합정렬 알고리듬처럼 함수에서 자기 자신을 여러 번 호출하는 경우에는 이 최적화를 적용할 수 있음
- 다음 함수를 살펴본다
```
tailrec fun mergeSort(numbers : List<Int>): List<Int> {
    return when {
        numbers.size <= 1 -> numbers
        numbers.size == 2 -> {
            return if (numbers[0] < numbers[1]) {
                numbers
            } else {
                listOf(numbers[1], numbers[0])
            }
        }
    }
    else -> {
        val left = mergeSort(numbers.slice(0..numbers.size / 2))
        val right = mergeSort(numbers.slice(numbers.size / 2 + 1 until numbers.size))
        return merge(left, right)
    }
}
```
- 보다시피 재귀 호출이 두번 일어남
- 이런 경우에 코틀린 컴파일러는 다음과 같은 경고 메시지를 보여줌
- tailrec 키워드가 붙어 있지만 꼬리 재귀 최적화가 적용 불가능하다는 뜻