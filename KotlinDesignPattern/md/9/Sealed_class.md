## 열거형 대신 봉인 클래스 사용하기
- 자바 개발자라면 열거형에 기능을 추가하고 싶은 유혹이 있다
- 예를들어 피자를 주문하고 상태를 추적하는 애플리케이션을 만든다고 한다.
- 다음과 같은 코드를 작성할 수 있음
```
// enum이 상태를 나타내는 자바 스타일 코드
enum class PizzaOrderState {
    ORDER_RECEIVED, PIZZE_BEING_MADE, OUT_FOR_DELIVERY, COMPLETED;

    fun ntextStatus(): PizzaOrderStatus {
        return when (this) {
            ORDER_RECEIVED -> PIZZE_BEING_MADE
            PIZZA_BEING_MADE -> OUT_FOR_DELIVERY
            OUT_FOR_DELIVERY -> COMPLETED
            COMPLETED -> COMPLETED
        }
    }
}
```
- 하지만 봉인 클래스를 사용하는 방법도 있다.
```
sealed class PizzaOrderStatus(protected val orderId : Int) {
    abstract fun nextStatus() : PizzaOrderStatus
}

class OrderReceived(orderId : Int) :
    PizzaOrderStatus(orderId) {
        override fun nextStatus() = PizzaBeingMade(orderId)
   
}

class PizzaBeingMade(orderId : Int) : 
    PizzaOrderStatus(orderId) {
        override fun nextStatus() = OutForDelivery(orderId)
}

class OutForDelivery(orderId : Int) :
    PizzaOrderStatus(orderId) {
        override fun nextStatus() = Completed(orderId)
}
class Completed(orderId : Int) : PizzaOrderStatus(orderId) {
    override fun nextStatus() = this
}
```
- 여기서는 PizzaOrderStatus라는 봉인 클래스를 상속받아 주문 상태를 나타내는 클래스를 상태마다 하나씩 만듬
- 이렇게 하면 주문 상태와 함께 추가적인 정보를 더 쉽게 저장할 수 있다는 장점이 있음
- 이 예제에서는 주문의 ID를 지정할 수 있다.
```
var status : PizzaOrderStatus = OrderReceived(123)

while(status !is Completed) {
    status = when ( status ) {
        is OrderReceieved -> status.nextStatus()
        is PizzaBeingMade -> status.nextStatus()
        is OutForDelivery -> status.nextStatus()
        is Completed -> status
    }
}
```
- 일반적으로 상태와 함께 어떤 데이터를 관리해야 하는 경우에 봉인 클래스를 쓰면 좋음
- 또한 열거형보다는 봉인 클래스가 낫다.