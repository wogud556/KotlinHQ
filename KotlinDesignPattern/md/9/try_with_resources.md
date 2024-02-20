## try-with-resources 문의 대안
- 자바 7에는 AutoCloseable이라는 개념과 try-with-resouces문이 추가됨
- 이것을 사용하면 코드에서 어떤 자원을 다 사용하고 나서 자원 사용을 자동으로 종료할 수 있음
- 즉 더 이상 파일을 닫는 것을 잊어 버릴 염려가 없음(완전히 없지는 앖더라도 훨씬 덜함)
- 자바 7 이전에 자원이 안전하게 닫히도록 하려면 다음과 같이 지저분한 코드를 작성해야 했음

```
BufferedReader br = null;
try {
    br = new BufferedReader(new FileReader("./src/main/kotlin/7_TryWithResource.kt"));
    System.out.println(br.readLine());
}
finally {
    if(br != null) {
        br.close()
    }
}
```
- 자바 7이 출시된 후로는 위의 코드를 다음과 같이 작성할 수 있음
```
try ( BufferedReader br = new BufferedReader(new FileReader("/some/path"))) {
    System.out.println(br.readLine())
}
```
- 코틀린은 이 문법을 지원하지 않음
- 하지만 use()함수를 사용하면 try-with-resource 문을 대체할 수 있음
```
val br = BufferedReader(FileReader("./src/main/kotlin/7_TryWithResource.kt"))

br.use {
    println(it.readLines())
}
```