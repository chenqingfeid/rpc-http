## Getting Started

### Define RPC Service

```java
public interface GitHubService extends RpcService {

    @Get
    @Path("/users/{username}/repos")
    @Deserialization(GsonDeserializer.class)
    List<?> getRepositories(@PathParameter("username") String username) throws RpcException;

}
```

### General RPC

```java
RpcServiceFactory factory = new RpcServiceFactory(null);
GitHubService github = factory.newRpcService(GitHubService.class, "https://api.github.com");
List<?> repos = github.getRepositories("johnsonlee");
System.out.println(repos);
```

### Post Form

```java
@Post(contentType="application/x-www-form-urlencoded")
@Path("/signin")
@Serialization(FormSerializer.class)
@Deserialization(GsonDeserializer.class)
User signIn(@BodyParameter("username") String username, @BodyParameter("password") String password) throws RpcException;
```

### Upload File

```java
@Post(contentType="multipart/form-data")
@Path("/upload")
@Serialization(MultipartSerializer.class)
@Deserialization(NullDeserializer.class)
void upload(@BodyParameter("file") File file) throws RpcException;
```

## Download

### Gradle

```gradle
compile 'com.sdklite.rpc:http:0.0.1'
```

### Maven

```xml
<dependency>
  <groupId>com.sdklite.rpc</groupId>
  <artifactId>http</artifactId>
  <version>0.0.1</version>
</dependency>
```

## API Doc

Please see [HTTP RPC Java Doc](http://http.rpc.sdklite.com).