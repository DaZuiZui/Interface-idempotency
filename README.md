# Interface idempotency solution

## 1. What is idempotency

​ First of all, let’s think about what idempotency is. If an interface is called multiple times without side effects, it is idempotent. If an interface is called multiple times, the side effects are not idempotent, and there is a problem of idempotency.

## 2. Solution

### 2.1 Distributed lock+token

![](https://blogpublicfile.oss-cn-hangzhou.aliyuncs.com/article/20220421183837.png)

​ We ensure that idempotency is achieved by using redis+token. First, we obtain the token from the server, and then request it with the token. If the token exists in redis, delete the token. If it does not exist, do not operate. .

##3.code

### 3.1 Distributed lock+token

**pom.xml**

~~~xml
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-aop</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
~~~

**Controller layer**

````java
@RestController
public class TestController {
    @Autowired
    private RedisTemplate redisTemplate;


    @GetMapping("/getToken")
    public String getToken(){
        String token = UUID.randomUUID().toString().substring(0,8);
        redisTemplate.opsForValue().set(token,"1",60*10, TimeUnit.SECONDS);
        return token;
    }

    @GetMapping("/sub")
    public void sub(String token) throws InterruptedException {
        //todo business operation
    }
}
````

**Aop layer**

````java
@Aspect
@Component
public class TestAop {
    @Autowired
    private RedisTemplate redisTemplate;

    @Before("execution(* com.dazuizui.idempotentinterface.controller.TestController.sub(..))")
    public void before(JoinPoint proceedingJoinPoint) throws Exception {
        Object[] args = proceedingJoinPoint.getArgs();
        boolean b = redisTemplate.delete(args[0]);
        System.out.println(b);
        if (!b){
            //todo log operation
            throw new Exception("idempotent operation");
        }
    }
}
````
