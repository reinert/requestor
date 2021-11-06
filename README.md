# Requestor [![Build Status](https://travis-ci.org/reinert/requestor.svg?branch=master)](https://travis-ci.org/reinert/requestor) [![Gitter](https://img.shields.io/badge/Gitter-Join%20Chat-blue.svg?style=flat)](https://gitter.im/reinert/requestor)

**Request like a boss.** 😎

*Ask more. Do less. Keep track of everything.*

Requestor is a powerful HTTP Client API for cutting-edge Java/GWT client apps. It offers plenty of
carefully designed features that enable developers to rule the network communication process smoothly:
* [**Requesting Fluent API**](#requesting-fluent-api-briefing) - code as you think, read as you code.
* [**Promises**](#promises) - chain callbacks to different results and statuses.
* [**Serialization**](#serialization) - serialize and deserialize payloads integrating any library.
* [**Authentication**](#authentication) - make complex async authentication procedures in a breeze.
* [**Request/Response Hooking**](#processors-hooking) - asynchronously filter and intercept requests and responses.
* [**HTTP Polling**](#poll) - make long or short polling with a single command.
* [**Session**](#session) - set default options to all requests.
* [**Store**](#store) - save and retrieve data both in session and request scope.
* [**Service**](#service) - break down the API consumption into smaller independent contexts.
* [**Links API**](#links-api-hateoas) - navigate through an API interacting with its links (HATEOAS for real).
* [**Headers API**](#headers-api) - directly create and parse complex headers.
* [**URI API**](#uri-api) - build and parse complicated URIs easily.
* [**Binary Data**](#binary-data) - upload and download files tracking the progress.

It supports GWT 2.9 and Java 8+ while maintaining backward compatibility with GWT 2.7 and Java 1.5.
In addition, GWT3 and J2CL support are in the roadmap without breaking API compatibility.


## Preview

Make a GET request and deserialize the response body as String:

```java
Session session = new GwtSession();
session.get("http://httpbin.org/ip", String.class).success( Window::alert );
```

Make a POST request sending a serialized object in the payload:

```java
Book book = new Book("Clean Code", "Robert C. Martin", new Date(1217552400000L));
session.post("/api/books", book).success( () -> showSuccessMsg() ).fail( () -> showErrorMsg() );
```

GET a collection of objects:

```java
session.get("/api/books", List.class, Book.class).success( books -> renderTable(books) );
```

**Note**: Check the [Serialization](#serialization) section to enable ***auto-serialization***.

The above examples are shortcuts in Session class to make quick requests.
Additionally, you can access the fluent API to build and send more complex requests.

### Requesting Fluent API *(briefing)*

Requesting involves three steps:
1) Access the request builder by calling `requestor.req( <uri> )`, and **set the request options** 
   through the chaining interface.
2) Following, we must **call one of the invoke methods**, represented by the corresponding HTTP 
   methods (*get*, *post*, *put*, and so on). In this action, we specify the type we expect to 
   receive in the response payload.
3) Finally, we receive a Promise instance, which allows us to **chain callbacks** according to 
   different outcomes.


```java
session.req("/api/books/1")             // 0. Start building the request
       .timeout(10000)                  // 1. Set the request options
       .header("ETag", "33a64df5") 
       .get(Book.class)                 // 2. Invoke an HTTP method with the expected type
       .success(book -> render(book))   // 3. Add callbacks to the promise
       .fail(response -> log(response));
```

See the [Requesting Fluent API](#requesting-fluent-api) section to know more details of how it 
works.

Meet all the request options available in the [Request Options](#request-options) section.

### ⚙️ Set up your Session

Requestor features a configurable client `Session`. There we *set default request options* 
that apply to all requests. Also, we are able to *cache and share data* through the `Store`. 
Eventually, we can *reset the session state* at any time.

```java
Session session = new GwtSession();

// Set all requests to have 10s timeout and 'application/json' Content-Type
session.setTimeout(10000);
session.setContentType("aplication/json");


// Perform login, save user info, and authenticate all subsequent requests
session.post("/login", credentials, UserInfo.class)
        .success(userInfo -> {
            session.getStore().put("userInfo", userInfo);
            session.setAuth(new BearerAuth(userInfo.getToken()));
        });
//...
        
// Make authenticated requests
session.post("/api/books", book);
//...

// Clear the session state
session.reset();

// Clear all data from the store
session.getStore().clear();

// Now all requests will have the default parameters
session.post("/api/books", book);
```


### Looking for some REST? 😪

Requestor offers a pre-defined REST client so that we can perform basic CRUD operations against 
a resource. See the example below on how to create a new `RestService`.

```java
// Init by calling newRestService( <uri>, <entityClass>, <idClass>, <collectionClass> )
bookService = session.newRestService("/api/books", Book.class, Integer.class, List.class);

// Configure your service to always set Content-Type and Accept headers as 'application/json'
bookService.setMediaType("application/json");

// POST a book to '/api/books' and receive the created book from server
Book book = new Book("RESTful Web Services", "Leonard Richardson", new Date(1179795600000L));
bookService.post(book).success(createdBook -> render(createdBook));

// GET all books from '/api/books'
bookService.get().success(books -> render(books));

// GET books from '/api/books?author=Richardson&year=2006'
bookService.get("author", "Richardson", "year", 2006).success(books -> render(books));

// GET the book of ID 123 from '/api/books/123'
bookService.get(123).success(books -> render(books));

// PUT a book in the resource with ID 123 from '/api/books/123' and receive the updated book
bookService.put(123, book).success(updatedBook -> render(updatedBook));

// PATCH book's title and year in '/api/books/123' and receive the updated book
bookService.patch(123, book, "title", "year").success(updatedBook -> render(updatedBook));

// DELETE the book of ID 123 from '/api/books/123' (returns void)
bookService.delete(123).success(() -> showSuccess("Book was deleted."));
```

Although Requestor provides this generic REST client, extending the `AbstractService` class and 
implementing our service clients is more beneficial. `AbstractService` affords the advantage of
little coding while empowering complete control of the requesting logic. Consequently, it 
improves the testing capabilities and bug tracking. See more details in the [Service](#service) 
section.


## Installation

Add the following requestor impl dependency to your POM.

```xml
<dependency>
    <groupId>io.reinert.requestor.impl</groupId>
    <artifactId>requestor-gwt</artifactId>
    <version>0.2.0</version>
</dependency>
```

Then, make requestor available to your GWT project by importing the implementation's module.

```xml
<inherits name="io.reinert.requestor.RequestorGwt"/>
```

Requestor primarily focuses on the HTTP Client API. Hence, **requestor-core** declares a Promise 
interface, but does not implement it. The implementation is delegated to **requestor-impl**s. 
Thus, a **requestor-impl** integrates **requestor-core** to some promise library.

Currently, there is one impl available: **requestor-gwt**. It binds requestor with 
[gdeferred](https://github.com/reinert/gdeferred) promise API. Furthermore, an impl integrating 
**requestor-core** with [elemental2](https://github.com/google/elemental2) promise API is on the way.

### Latest Release

0.2.0 (18 Feb 2015)


## Yet another REST Client library?

*No. Not at all*. Requestor is an **HTTP Client API** intended to provide several features related to network communication.
Its scope is broader than popular (and often misunderstood) REST patterns. Requestor precisely models each entity in the
HTTP client-side context to enable its users to handle any requirement in this boundary. It values good **code readability
and maintainability** for the user by providing carefully designed interfaces and abstractions that others can extend and
add their logic with **low or zero integration effort**. Workarounds and hacks are not welcome here. Developers should be able
to implement their requirements keeping **high cohesion** through all their codebase.

Additionally, Requestor was crafted from the Client perspective instead of the Server's (like other rest libraries were thought).
In that fashion, developers have a more **consistent and intuitive experience** consuming HTTP services while coding. We do not
need to pre-declare Server API's facades. We can just consume them on demand. This approach empower us to build *micro clients*
that interact with many different *micro services*.

Besides, we value **code traceability**. So code generation is the last option in design decisions. Whenever a new requirement appears,
we strive to develop a good design solution that allows the user to write less code and achieve the desired results. If something proves
to be inevitably repetitive on the user side, after reaching the best possible design, then code generation is used to save the user
from repetitive work. Still, leveraging Requestor's components, people will probably automate most of their work using fundamental
object-oriented techniques like inheritance and composition. This way, they will better comprehend what is going on and have complete
control of the coding flow.

Requestor was inspired by successful HTTP Client APIs in other ecosystems like Python Requests, Angular HttpClient, Ruby Http.rb, and JAX-RS Client.

With Requestor, we can:
* Quickly make offhand requests writing as little code as possible.
* Communicate with different HTTP APIs keeping the same client communication pattern, thus improving the codebase maintainability.
* Handle multiple media types (JSON and XML, for instance) for the same java type without hacks.
* Deserialize different types according to the response status, properly modeling error messages in our app.
* Navigate through discoverable REST API links, fully leveraging HATEOAS.
* Build different and complex queries on demand, not having to map each possible iteration with Server APIs previously.
* Add new logic requirements not needing to change existing classes, instead of creating new small units, avoiding code conflict between co-workers.


## Request Options

Requestor's Fluent API exposes many chaining methods to properly configure your request.

Suppose you start building the request below:

```java
RequestInvoker req = session.req("/api/books/");
```

### *payload*

Set an object as the request payload. This object is then serialized into the HTTP message's body
as part of the [request processing](#processors-hooking) after its invocation.

```java
req.payload( "a simple string" );
req.payload( new Book("RESTful Web Services", "Leonard Richardson", new Date(1179795600000L) );
req.payload( Arrays.asList(book1, book2, book3) );
```

Optionally, we can specify the fields we want to be serialized. In this case, the Serializer
must implement the required logic to filter the informed fields during serialization.

```java
Book book = new Book("RESTful Web Services", "Leonard Richardson", new Date(1179795600000L);
req.payload( book, "title", "author" ); // Inform the serializer to filter title and author only
```

### *auth*

Set an `Auth` implementation to properly authenticate your request. This async functional interface
is capable of supplying the request with the required credentials and later sending it.

```java
req.auth( new BasicAuth("username", "password") );
req.auth( new BearerAuth("a-secret-token") );
req.auth( new MyOwnAuth("whatever") );
```

### *accept*

A shortcut to set the Accept header.

```java
req.accept( "application/json" );
```

### *contentType*

A shortcut to set the Content-Type header.

```java
req.contentType( "application/json" );
```

### *header*

Set an HTTP header informing a key-value pair or a [Header](#headers-api) type.

```java
req.header( "Accept-Language", "da, en-gb;q=0.8, en;q=0.7" );
req.header( new QualityFactorHeader("Accept-Language", "da", 1.0, "en-gb", 0.8, "en", 0.7) );
```

### *delay*

Set a time in milliseconds to postpone the request sending.

```java
req.delay( 5000 ); // Delay the request for 5s
```

### *timeout*

Set a period in milliseconds in which the request should be timeout.

```java
req.timeout( 10000 ); // Timeout after 10s
```

### *poll*

Needing to ping an endpoint in a specific interval, we can set the poll option with the 
`PollingStrategy` and the `interval` in milliseconds. Additionally, we can set the maximum 
number of requests to stop the polling by informing the `limit` argument.

```java
// Send the request each 3s
req.poll( PollingStrategy.SHORT, 3000 );

// Send the request each 3s up to the limit of 10 requests
req.poll( PollingStrategy.SHORT, 3000, 10 );
```

There are two PollingStrategy choices: **LONG** or **SHORT**.
* `PollingStrategy.LONG` - Long polling means the subsequent request will be dispatched only 
  **after receiving the previous response**.
  * If we set an interval, the following request will only be delayed after the previous 
    response has come.
* `PollingStrategy.SHORT` - On the other hand, with Short polling, the subsequent request will 
  be dispatched right **after the previous request has been sent**.
    * If we set an interval, then the following request will be delayed by this interval as 
      soon the previous request is dispatched.

```java
// The next requests are dispatched 3s after the previous ones
req.poll( PollingStrategy.SHORT, 3000 );

// The next requests are dispatched as soon the responses are received
req.poll( PollingStrategy.LONG); // Same as `req.poll( PollingStrategy.LONG, 0 )`

// The next requests are dispatched 10s after previous responses up to the limit of 5 requests
req.poll( PollingStrategy.LONG, 10000, 5 );
```

In both cases, if we also set the request's delay option, then the subsequent dispatches' 
*total delay* = *request delay* + *polling interval*.

```java
// The first request is delayed by 2s and the next ones are delayed by 5s (2 + 3)
req.delay(2000).poll( PollingStrategy.SHORT, 3000 );
```

Furthermore, not setting a *polling limit*, we can manually ***stop*** the polling by calling 
`request.stopPolling()`.

```java
session.req("/api/books/")
       .poll(PollingStrategy.LONG)
       .get()
       .load(new ResponseCallback() {
           @Override
           public void execute(Response response) {
               Request request = response.getRequest();

               if (request.getPollingCounter() == 3) {
                   request.stopPolling(); // Stop polling after receving the third response
               }
           }
       });
```

It is worth noting that each new dispatched request will pass through all the [request/response 
processing cycle](#processors-hooking). Thereby, we will have every polling request always up 
to date with our filters, 
serializers, and interceptors.

## Promises

Requestor declares its own Promise contract coherent with the requesting domain.

A `Promise<T>` gives access to the response body as `T` if it is successful (2xx). Check the 
available callbacks:
  * **success**( payload [, response ] -> {} )
    * executed when the response *is successful* (status = 2xx)
    * features the *deserialized payload* and the *response* (optional)
  * **fail**( response -> {} )
    * executed if the response *is unsuccessful* (status ≠ 2xx)
    * features the *response*
  * **load**( response -> {} )
    * executed if the response *is completed*, regardless of *success or failure*
    * features the *response*
  * **status**( statusCode|statusFamily, response -> {} )
    * executed when the response *returned the given status code/family*
    * features the *response*
  * **progress**( requestProgress -> {} )
    * executed many times while the request is being sent
    * features the *requestProgress* that enables tracking the download progress
  * **upProgress**( requestProgress -> {} )
    * executed many times while the response is being received
    * features the *requestProgress* that enables tracking the upload progress
  * **timeout**( timeoutException -> {} )
    * executed when a timeout occurs
    * features the *timeoutException* including the *request*
  * **abort**( requestException -> {} )
    * executed if the request *could not be performed* due to any exception (even timeout)
    * features the original *exception*

Check how you can use them below:

```java
// You can chain single method callbacks (functional interfaces) to handle success, failure or both: 
session.get('/httpbin.org/ip', String.class).success(new PayloadCallback<String>() {
    public void execute(String ip) {
        // This is executed if the request was successful (status = 2xx)
        view.showIp(ip);
    }
}).success(new PayloadResponseCallback<String>() {
    public void execute(String ip, Response r) {
        Window.alert("Response status was " + r.getStatus.toString());
    }
}).fail(new ResponseCallback() {
    public void execute(Response r) {
        // This is executed if the request was unsuccessful (status ≠ 2xx)
        view.showError("Request failed. Server message: " + r.getPayload().toString());
    }
}).load(new ResponseCallback() {
    public void execute(Response r) {
        // This is always executed, regardless of success or failure
        Window.alert("Response status was " + r.getStatus.toString());
    }
}).status(429, new ResponseCallback() {
    public void execute(Response r) {
        // This is executed if the response status code 429
        view.showError("Too many requests. Please try again in a few seconds.");
    }
}).status(StatusFamily.SERVER_ERROR, new ResponseCallback() {
    public void execute(Response r) {
        // This is executed if the response status code was 5xx (server error)
        view.showError("Request failed. Server message: " + r.getPayload().toString());
    }
}).progress(new ProgressCallback() {
    public void execute(RequestProgress progress) {
        // This is executed many times while the response is being received
        if (progress.isLengthComputable())
            view.setDownloadProgress( (progress.getLoaded() / progress.getTotal()) * 100 );
    }
}).upProgress(new ProgressCallback() {
    public void execute(RequestProgress progress) {
        // This is executed many times while the request is being sent
        if (progress.isLengthComputable())
          // getCompletedFraction(int factor) calculates (loaded/total)*factor
          view.setUploadProgress(progress.getCompletedFraction(100));
    }
}).timeout(new TimeoutCallback() {
    public void execute(TimeoutException e) {
        // This is executed if the request could not be performed due to timeout
        view.showError("Request timed out: " + e.getMessage());
    }
}).fail(new ExceptionCallback() {
    public void execute(RequestException e) {
        // This is executed if the request could not be performed due to any exception thrown before sending   
        if (t instanceof TimeoutException) {
            // It catches timeouts also
            view.showError("Request timed out: " + e.getMessage());
        } else {
            // Any other exception was thrown while processing the request
            view.showError("Request could not be sent: " + e.getMessage());
        }
    }
});
```

### Success callbacks and Collections

When requesting, we will always receive a `Promise<Collection<T>>` despite the particular 
collection type (*List*, *Set*, and so on) we asked due to a design limitation of the Java 
language, which does not allow "generics of generics." Nevertheless, we can declare the 
collection we demanded in the callback to typecast the result automatically.
See the example:

```java
// An ArrayList was requested, but the get method returned a Promise<Collection<Book>>
Promise<Collection<Book>> promise = session.req("/server/books").get(ArrayList.class, Book.class);

// Even though we can declare a List<Book> as the callback's parameterized type
promise.success(new PayloadCallback<List<Book>>() {
    public void execute(List<Book> payload) {
        ...
    }
});
```

ℹ️ When using **lambda expressions**, we must explicitly declare the demanded collection type in 
the signature to access it. Check below:

```java
// An ArrayList was requested, but the get method returned a Promise<Collection<Book>>
Promise<Collection<Book>> promise = session.req("/server/books").get(ArrayList.class, Book.class);

// The payload parameter in callback is a Collection<Book>
promise.success( books -> books.get(0) ); // COMPILATION ERROR: books is Collection<Book> and .get belongs to List

// You can explicitly declare the type in lambda signature to typecast
promise.success( (List<Book> books) -> books.get(0) ); // OK: Now it works
```

## Serialization

Serialization is part of the [Request Processing](#processors-hooking), and deserialization is 
part of the [Response Processing](#processors-hooking).

Requestor exposes the `Serializer` interface responsible for serializing and deserializing a 
specific type while holding the **Media Types** it handles. Therefore, it is possible 
to have multiple Serializers for the same Java Type handling different Media Types, e.g., JSON and 
XML. Requestor's serialization engine is smart enough to **match** the appropriate **Serializer** 
according to the asked class and the request/response's **Content-Type**.

To enable serialization and deserialization, we must register a `Serializer` instance in the
[session](#session). Necessitating only the deserialization part, we can register a 
`Deserializer` implementation.

```java
session.register(new MyTypeSerializer());
```

Additionally, we can register a `Provider` instead of an actual Serializer instance. It is a 
factory that will provide the Serializer instance on demand. When Requestor's serialization engine 
matches a serializer according to the class and media type, it asks the Provider for an 
instance. If this instance is disposable (i.e., it is not living in an outer context), the 
garbage collector will free up some memory after its usage. See how to register a Serializer 
Provider:

```java
register(new SerializerProvider() {
    @Override
    public Serializer<?> getInstance() {
        return new MyTypeSerializer(); // return a disposable instance instead of a reference
    }
});

// Lambda syntax
session.register(MyTypeSerializer::new); // Same as `session.register(() -> new MyTypeSerializer())`
```

**💡 PRO TIP**: If you start having too many serializers, consider registering them with `Providers` to save memory.

Although it is possible to implement our custom Serializers, we often resort to **AUTO-SERIALIZATION**
provided by requestor extensions. Currently, there are two available: `requestor-gwtjackson` and
`requestor-autobeans`.

### Gwt-Jackson auto-serialization

The `requestor-gwtjackson` extension integrates gwt-jackson into requestor to provide auto 
serialization. Intending to have the Serializers automatically generated, we just need to 
declare a `SerializationModule` annotating it with the required classes. Additionally, we can 
set the **Media Types** the serializers should handle. Since gwt-jackson serializes to JSON 
format, "application/json" is the default media type, but you can declare other media types, even 
wildcard types like "\*/json" or "\*/\*".

```java
@MediaType({"application/*json*", "*/javascript"})
@JsonSerializationModule({ Author.class, Book.class })
interface MySerializationModule extends SerializationModule {}
```

According to this example, when asking for `Author.class` or `Book.class`, all three scenarios 
below will match the auto-generated serializers:

```java
// Matches 'application/*json*' to serialize the payload
session.req("/books").contentType("application/json+gzip").payload(book).post();

// Matches 'application/*json*' to deserialize the response's payload
// assuming the response's content-type was really 'application/stream+json'
session.req("/books").accept("application/stream+json").get(List.class, Book.class);

// Matches '*/javascript' to deserialize the response's payload
// assuming the response's content-type was really 'text/javascript'
session.req("/authors/1").accept("text/javascript").get(Author.class);
```

We do not need to register the auto-generated serializers in our session instance. This 
is automatically done under the hood.

In order to install requestor-gwtjackson extension, add the following dependency to your project:

```xml
<dependencies>
  ...
  <dependency>
    <groupId>com.github.nmorel.gwtjackson</groupId>
    <artifactId>gwt-jackson</artifactId>
    <version>${gwtjackson.version}</version>
  </dependency>
  <dependency>
    <groupId>io.reinert.requestor.ext</groupId>
    <artifactId>requestor-gwtjackson</artifactId>
    <version>${requestor.version}</version>
  <dependency>
  ...
</dependencies>
```

Then inherit the `GwtJacksonExt` GWT module in your gwt.xml file:

```xml
<inherits name="io.reinert.requestor.gwtjackson.GwtJacksonExt"/>
```

### AutoBean auto-serialization

Similarly to the previous extension, `requestor-autobean` provides auto serialization for 
AutoBean interfaces. Likewise, we declare a SerializationModule with the classes and media types 
to generate the serializers.

```java
@MediaType({"application/json", "*/*"})
@JsonSerializationModule({ Author.class, Book.class })
interface MySerializationModule extends SerializationModule {}
```

Further, Requestor graciously enables us to create new AutoBean instances directly from the 
Session by calling `session.getInstance(<Class>)`.

```java
Book book = session.getInstance(Book.class);
```

The installation procedure is pretty much the same.

```xml
<dependencies>
  ...
  <dependency>
    <groupId>io.reinert.requestor.ext</groupId>
    <artifactId>requestor-autobean</artifactId>
    <version>${requestor.version}</version>
  <dependency>
  ...
</dependencies>
```

Then inherit the `AutoBeanExt` GWT module in your gwt.xml file:

```xml
<inherits name="io.reinert.requestor.autobean.AutoBeanExt"/>
```

### Custom
#### JSON
#### XML
#### SubTypes

## Authentication

Requestor features the `Auth` functional interface responsible for authenticating the requests as the last step in the [Request Processing](#request-processing). It delivers the credentials to the request and ultimately sends it. Like any other processor, the Auth interface is an **async callback**. Therefore, after performing the necessary changes in the request, it must call `request.send()` to really send it. Moreover, we may find it advantageous to use the Session's Store to retrieve credential info. Check the following example:

```java
session.req("/api/authorized-only")
        .auth(new Auth() {
            @Override
            public void auth(PreparedRequest request) {
                // Retrieve the token from the Store
                String userToken = request.getStore().get("userToken");
                
                // Provide the credentials in the Authorization header
                request.setHeader("Authorization", "Bearer " + userToken);
                
                // Your request will be hanging forever if you do not call `send`
                request.send();
            }
        });
```

This is an example of how to perform a usual authentication. Indeed, this logic is already provided
by the [`BearerAuth`](#bearer-token) implementation.

Notice that Auth's async nature enables us to do complex stuff before actually providing the credential data
to the request. We can perform other asynchronous tasks before properly configuring the request. If, for instance,
we need to ping another endpoint to grab some token data, we can easily do it. Check the example below:

```java
session.req("/api/authorized-only")
        .auth(request -> {
            // We are reaching another endpoint sending a password to get an updated token
            session.post("/api/token", "my-password", String.class)
                    .success(token -> {
                        // After receiving the updated token, we set it into the request and send it
                        request.setHeader("Authorization", "Bearer " + token);
                        request.send();
                    });
        });
```

We may do any other useful async task, like performing heavy hash processes using *web workers*, before sending the request.

Additionally, Requestor allows us to register an Auth `Provider` instead of the `Auth` instance. The Provider is a **factory**
that returns an `Auth` instance for each request. It is really valuable when implementing authentication mechanisms
that require state management, like the `DigestAuth`. Check an example below of how to register an `Auth.Provider` in
the Session:

```java
session.setAuth(new Auth.Provider() {
    @Override
    public Auth getInstance() {
        // Supposing you implemented MyAuth elsewhere
        return new MyAuth( session.getStore().get("userToken") );
    }
});
    
// Lambda syntax
session.setAuth( () -> new MyAuth(session.getStore().get("userToken")) );
```


### Basic

In order to facilitate our development, Requestor provides standard Auth implementations. For instance, the `BasicAuth` performs the **basic access authentication** by putting a header field in the form of `Authorization: Basic <credentials>`, where credentials is the Base64 encoding of `username` and `password` joined by a single colon `:`. It might be helpful to retrieve credentials data from the Session Store, like in the following example:

```java
User user = session.getStore().get("user");

session.req("/api/authorized-only")
        .auth(new BasicAuth( user.getUsername(), user.getPassword() ));
```

### Bearer Token

Correspondingly, the `BearerAuth` performs the **bearer token authentication** by adding a header to the request in the form of `Authorization: Bearer <token>`.
See how you can enable this Auth in your Session to all requests using a `Provider`:

```java
session.setAuth(() -> {
    UserInfo userInfo = session.getStore().get("userInfo");
    return new BearerAuth(user.getToken());
});
```

### Digest
// TBD

### CORS
// TBD

### OAuth2
// TBD

## Processors (hooking)

One of the library's main features is the ability to introduce ***asynchronous hooks*** to 
process requests and responses. These middlewares are called **Processors**. Furthermore, it is 
essential to note that each processor kind has a specific capacity that differentiates it from 
others. Thus, it helps us to organize our application's communication processing stack better.

The request and response processing are include in the **REQUEST LIFECYCLE** as follows:
1. User builds and invokes a new **Request** through the **Session**
2. Request is processed by `RequestFilters`
3. Request is processed by `RequestSerializer` and serialized
4. Request is processed by `RequestInterceptors`
5. Request is processed by `Auth` and *sent* to network
6. **Response** is *received* from network
7. Response is processed by `ResponseInterceptors`
8. Response is processed by `ResponseDeserializer` and deserialized
9. Response is processed by `ResponseFilters`
10. User receives the processed Response though the **Promise**

All processors are able to manipulate the request/response, but they have the following 
distinguishing characteristic according to its kind:
* **Filters** - they can modify the *deserialized* payload
* **Serializers / Deserializers** - they perform payload serialization and deserialization
* **Interceptors** - they can modify the *serialized* payload

We can register as many **Filters** and **Interceptors** as we want in a **Session**. They are 
executed in the same order they were registered.

Regarding the **Auth**, there is only one available *per* ***Request***. We can register a default 
Auth in the Session using `session.setAuth(<Auth>)`, but we can override it when building a request.
Refer to the [Auth](#auth) section for more details.

As for the **RequestSerializer** and **ResponseDeserializer**, there is only one available *per* 
***Session***. They resort to the **SerializationEngine** to perform serialization and 
deserialization. The engine holds the registered **Serializers** and is responsible for 
de/serializing objects according to the *class* and *media type*.

### Request Filter

A `RequestFilter` hooks an undergoing deserialized request (`RequestInProcess`) to modify it, even 
its payload, or perform any other action that fits the business requirements triggered by a new
request. As any other **Processor**, the **RequestFilter** is ***asynchronous***, so we must call
`request.proceed()` to move the request forward in the request processing.

We can add a `RequestFilter` to the **Session** by calling `session.register(<RequestFilter>)`. 

```java
session.register(new RequestFilter() {
    @Override
    public void filter(RequestInProcess request) {
        // Access the Store bounded to this request/response lifecycle
        String encoding = request.getStore().get("encoding");

        // Modify the request headers
        request.setHeader("Accept-Encoding", encoding);

        // Modify any other request option, including the payload
        request.setPayload(null);

        // Call proceed otherwise the request hangs forever
        request.proceed();
    }
});
```

In addition to registering a **RequestFilter** instance, we can instead register a `Provider` to 
create new filter instances for each new request. This is useful if a filter relies on some 
internal state that should not be shared among other request lifecycles. See how to register it:

```java
session.register(new RequestFilter.Provider() {
    @Override
    public RequestFilter getInstance() {
        // Supossing you implemented MyRequestFilter elsewhere
        return new MyRequestFilter();
    }
});

// Lambda syntax
session.register(MyRequestFilter::new); // Same as `session.register(() -> new MyRequestFilter())`
```

Besides proceeding with the request, we can alternatively ***abort*** it by calling
`request.abort(<MockResponse>|<RequestException>)`. Check below:

```java
session.register(new RequestFilter() {
    @Override
    public void filter(RequestInProcess request) {
        // Abort the request with a fake response
        request.abort(new MockResponse(Status.BAD_REQUEST));
    }
});
```

If we abort with `MockResponse` then **load** callbacks are triggered,
as well **success** and **status** depending on the response status code.
Otherwise, if the request is aborted with `RequestException`, then **abort** 
callbacks are triggered.

### Request Serializer

A `RequestSerializer` receives a `SerializableRequestInProcess` along with the
`SerializationEngine`. It is supposed to serialize the request and proceed with
the request processing. The engine uses the registered **Serializers** to serialize 
the request matching the payload object *class* and the request's *content-type*.

There is only one `RequestSerializer` per Session, and it can be set with
`session.setRequestSerializer(<RequestSerializer>)`. Check the default implementation below:

```java
session.setRequestSerializer(new RequestSerializer() {
    @Override
    public void serialize(SerializableRequestInProcess request,
                          SerializationEngine engine) {
        // The engine is capable of serializing the request
        engine.serializeRequest(request);
        // It's possible to perform any async task during serialization
        request.proceed();
    }
});
```

### Request Interceptor

A `RequestInterceptor` hooks an undergoing serialized request (`SerializedRequestInProcess`) to 
modify it, even its payload, or perform any other action that fits the business requirements 
triggered by a new request. As any other **Processor**, the **RequestInterceptor** is 
***asynchronous***, so we must call `request.proceed()` to move the request forward in the 
request processing.

We can add a `RequestInterceptor` to the **Session** by calling `session.register(<RequestInterceptor>)`. 

```java
session.register(new RequestInterceptor() {
    @Override
    public void intercept(SerializedRequestInProcess request) {
        // Access the Store bounded to this request lifecycle
        String encoding = request.getStore().get("encoding");

        // Modify the request headers
        request.setHeader("Accept-Encoding", encoding);

        // Modify any other request option, including the serialized payload
        String json = request.getSerializedPayload().getString();
        SerializedPayload jsonp = SerializedPayload.fromText(")]}',\\n" + json);
        request.setSerializedPayload(jsonp);

        // Call proceed otherwise the request hangs forever
        request.proceed();
    }
});
```

In addition to registering a **RequestInterceptor** instance, we can instead register a `Provider` to 
create new interceptor instances for each new request. This is useful if an interceptor relies 
on some internal state that should not be shared among other request lifecycles. See how to 
register it:

```java
session.register(new RequestInterceptor.Provider() {
    @Override
    public RequestInterceptor getInstance() {
        // Supossing you implemented MyRequestInterceptor elsewhere
        return new MyRequestInterceptor();
    }
});

// Lambda syntax
session.register(MyRequestInterceptor::new); // Same as `session.register(() -> new MyRequestInterceptor())`
```

Besides proceeding with the request, we can alternatively ***abort*** it by calling
`request.abort(<MockResponse>|<RequestException>)`. Check below:

```java
session.register(new RequestInterceptor() {
    @Override
    public void interceptor(SerializedRequestInProcess request) {
        // Abort the request with an exception
        request.abort(new RequestException(request, "Manually aborted"));
    }
});
```

If we abort with `MockResponse` then **load** callbacks are triggered,
as well **success** and **status** depending on the response status code.
Otherwise, if the request is aborted with `RequestException`, then **abort** 
callbacks are triggered.

### Response Interceptor

A `ResponseInterceptor` hooks an incoming serialized response (`SerializedResponseInProcess`) to
modify it, even its serialized payload, or perform any other action that fits the business 
requirements triggered by a new response. As any other **Processor**, the **ResponseInterceptor**
is ***asynchronous***, so we must call `response.proceed()` to move the response forward in the
response processing.

We can add a `ResponseInterceptor` to the **Session** by calling `session.register(<ResponseInterceptor>)`.

```java
session.register(new ResponseInterceptor() {
    @Override
    public void intercept(SerializedResponseInProcess response) {
        // Modify the response headers
        response.putHeader(new ContentTypeHeader("application/json"));

        // Modify any other response option, including the serialized payload
        String jsonp = response.getSerializedPayload().getString();
        SerializedPayload json = SerializedPayload.fromText(jsonp.substring(8));
        response.setSerializedPayload(json);

        // Call proceed otherwise the response hangs forever
        response.proceed();
    }
});
```

In addition to registering a **ResponseInterceptor** instance, we can instead register a `Provider` to
create new interceptor instances for each new response. This is useful if an interceptor relies 
on some internal state that should not be shared among other request lifecycles. See how to 
register it:

```java
session.register(new ResponseInterceptor.Provider() {
    @Override
    public ResponseInterceptor getInstance() {
        // Supossing you implemented MyResponseInterceptor elsewhere
        return new MyResponseInterceptor();
    }
});

// Lambda syntax
session.register(MyResponseInterceptor::new); // Same as `session.register(() -> new MyResponseInterceptor())`
```

### Response Deserializer

A `ResponseDeserializer` receives a `DeserializableResponseInProcess` along with the
`SerializationEngine`. It is supposed to deserialize the response and proceed with
the response processing. The engine uses the registered **Deserializers** to deserialize
the response matching the asked payload *class* and the response's *content-type*.

There is only one `ResponseDeserializer` per Session, and it can be set with
`session.setResponseDeserializer(<ResponseDeserializer>)`. Check the default implementation below:

```java
session.setResponseDeserializer(new ResponseDeserializer() {
    @Override
    public void deserialize(DeserializableResponseInProcess response,
                            SerializationEngine engine) {
        // The engine is capable of deserializing the response
        engine.serializeResponse(response);
        // It's possible to perform any async task during deserialization
        response.proceed();
    }
});
```

### Response Filter

A `ResponseFilter` hooks an incoming deserialized response (`ResponseInProcess`) to
modify it, even its serialized payload, or perform any other action that fits the business
requirements triggered by a new response. As any other **Processor**, the **ResponseFilter**
is ***asynchronous***, so we must call `response.proceed()` to move the response forward in the
response processing.

We can add a `ResponseFilter` to the **Session** by calling `session.register(<ResponseFilter>)`.

```java
session.register(new ResponseFilter() {
    @Override
    public void filter(ResponseInProcess response) {
        // Modify the response headers
        response.putHeader(new ContentTypeHeader("application/json"));

        // Modify any other response option, including the serialized payload
        String jsonp = response.getSerializedPayload().getString();
        SerializedPayload json = SerializedPayload.fromText(jsonp.substring(8));
        response.setSerializedPayload(json);

        // Call proceed otherwise the response hangs forever
        response.proceed();
    }
});
```

In addition to registering a **ResponseFilter** instance, we can instead register a `Provider` to
create new filter instances for each new response. This is useful if a filter relies
on some internal state that should not be shared among other request lifecycles. See how to
register it:

```java
session.register(new ResponseFilter.Provider() {
    @Override
    public ResponseFilter getInstance() {
        // Supossing you implemented MyResponseFilter elsewhere
        return new MyResponseFilter();
    }
});

// Lambda syntax
session.register(MyResponseFilter::new); // Same as `session.register(() -> new MyResponseFilter())`
```

## Session

Requestor is a session-based HTTP client. It means that a **Session** ties every configuration and action a user can take related to communication. Thus, the `Session` object is the baseline of every communication process in the application. What is better, Requestor does not restrict its users to having only one global Session. We can have ***as many sessions as it makes sense*** according to our business requirements. For example, if we are building a modern client app, we may communicate with different microservices. It may be reasonable to have one Session for each microservice with different configurations. This flexibility promotes a much more **reliable and maintainable code** since we can isolate different business logics in their own context, avoiding runtime conflicts and undesirable multi-path coding.

To instantiate a new `Session`, we must call one of its implementations. Requestor provides two: `CleanSession` and `GwtSession`, the latter having a predefined configuration for JSON-based communication. Additionally, we can implement our Session subclass, including the configurations that fit our requirements.

```java
Session session = new CleanSession();
```

Besides allowing registration of many [Processors](#processors-hooking), the Session admits setting many default request options. Along with that, it is possible to reset the Session state.

```java
session.reset()
```

This method will reset all the Session's request options to their default values.

### Default request options

Below are the available options.

#### Media-Type

This session configuration will be applied to every request's Content-Type and Accept headers.

```java
session.setMediaType("application/json");
```

#### Auth

This session configuration will be applied to every request's [`auth`](#auth) option. We can either register and [`Auth`](#auth-1) instance or a [`Provider`]

```java
// The same Auth instance will be used by all requests
session.setAuth( new BearerAuth(session.getStore().get("token")) );

// By setting a Provider, each request will have a new Auth instance
// Helpful to restrict the Auth's internal state to the request lifecycle
session.setAuth( () -> new BearerAuth(session.getStore().get("token")) );
```

#### Timeout

This session configuration will be applied to every request's [`timeout`](#timeout) option.

```java
// Every request will have a timeout of 20s
session.setTimeout(20000);
```

#### Delay

This session configuration will be applied to every request's [`delay`](#delay) option.

```java
// Every request will have a delay of 3s
session.setDelay(3000);
```

#### Polling

This session configuration will be applied to every request's [`poll`](#poll) option.

```java
// Every request will be long polling for 5 times
session.setPolling(PollingStrategy.LONG, 0, 5);
```

### Requesting

The Session is the starting point to build requests. We access the request builder by calling `session.req(<Uri>)`. Since the builder is also an invoker, we can call an invoke method any time to send the request. The invoke methods are named according to the respective HTTP methods they claim. All those methods are chainable, as demonstrated below:

```java
// Start requesting informing the URI
RequestInvoker req = session.req("/api/book/1")

// Set up the request
req = req.timeout(5000)

// Invoke the request
Promise<Book> promise = req.get(Book.class);

// All together
Promise<Book> promise = session.req("/api/book/1")
        .timeout(5000)
        .get(Book.class);
```

In order to better understand the requesting mechanics, refer to the [Requesting Fluent API](#requesting-fluent-api) section.

In addition to the fluent request builder and invoker exposed by the `req` method, the Session features **direct invoker methods** that quickly dispatch requests.

```java
Book book = new Book("Clean Code", "Robert C. Martin");

// Same as `session.req("/api/books").payload(book).post(Book.class)`
Promise<Book> promise = session.post("/api/books", book, Book.class);
```

### Deferred Factory

Another convenient feature is the possibility of instantiating a Session with a customized `Deferred.Factory`. This factory provides `Deferred` instances to the request dispatcher, returning a `Promise` to the Session's user. Thus, we can immediately add some global callbacks to keep our code DRY when generating a Deferred instance.

The example below demonstrates a customized Deferred Factory that fires a `ShowLoadingEvent` right before the request is sent and fires a `HideLoadingEvent` once the request gets [loaded](#promises) or [aborted](#promises).

```java
class AppDeferredFactory implements Deferred.Factory {

    @Override
    public <T> Deferred<T> newDeferred() {
        final DeferredRequest<T> deferred = new DeferredRequest<T>();

        // Show loading widget before sending the request
        APP_FACTORY.getEventBus().fireEvent(new ShowLoadingEvent());

        // Hide loading widget on load or abort
        deferred.load(() -> APP_FACTORY.getEventBus().fireEvent(new HideLoadingEvent()))
                .abort(() -> APP_FACTORY.getEventBus().fireEvent(new HideLoadingEvent()));

        return deferred;
    }
}
```

Therefore, we can instantiate our Session with this customized Deferred Factory, as demonstrated below:

```java
Session session = new CleanSession(new AppDeferredFactory());
```


## Store

Requestor provides a place where we can save and retrieve objects by key: the `Store`. There are two different kinds of Store: `SessionStore` and `TransientStore`. The [Session](#session) features a long-living `SessionStore` where we can save and retrieve objects by key during the Session's life. On top of that, whenever we create a new **Request** or **Service**, we have access to a new short-living `TransientStore` to manage data during the component's lifecycle.

The `TransientStore` envelopes a `Store` (be it Transient or Session) to expose access to its data. Thus, whenever the Transient Store is queried, it first tries to retrieve data from its local storage. Not succeeding, it queries the underlying Store. When saving data, we can ask the Transient Store to save it locally or delegate it to the wrapped Store. Finally, when deleting, we are able to remove only locally saved data. We cannot delete data persisted in the underlying Store from the Transient Store.

### Session Store

The Session Store is available throughout the Session's life. We access it by calling `session.getStore()`. With the Store, we can put, get and remove objects by key.

```java
Store store = session.getStore();

// Save an object in the store
store.save("key", anyObject);

// Check if there's an object with that key
boolean isSaved = store.has("key");

// Get an object from the store
// Automatically typecasts to the requested type
AnyType object = store.get("key");

// Remove the object from the store
boolean isDeleted = store.delete("key");
```

### Request Store

The Request Store is a `TransientStore` available during the [Request Lifecycle](#processors-hooking) and accessed within the [Processors](#processors-hooking) either by `request.getStore()` or by `response.getStore()`.

Having a transient **Request Store** is helpful to share information among **Processors** without cluttering the deriving **Session Store** or **Service Store**.

The Request Store provides access to the deriving Store's data. We can even persist data from the Request Store into the underlying Store (by setting the boolean `persist` param to true when saving), though we cannot delete data from it.

When we call `store.get(<key>)`, the Request Store first tries to retrieve the associated object from the request scope storage. Not finding, it queries the deriving Store. Also, the result is automatically typecasted to the requested type.

```java
Store store = request.getStore();

// Get an object from the store or the deriving store
// Automatically typecasts the result
AnyType object = store.get("key");
```

To save an object locally, we call `store.save(<key>, <object>)`. Differently, to save an object in the deriving Store, we need to call `store.save(<key>, <object>, <persit=true>)`.

```java
Store store = request.getStore();

// Save an object in request scope only
store.save("key", anyObject);

// Save an object in the deriving store
store.save("key", anyObject, true);
```

To delete a local record, we call `store.delete(<key>)`. We cannot delete records from the deriving Store.

```java
Store store = request.getStore();

// Save an object in the deriving store
store.delete("key");
```

### Service Store

The Service Store is a `TransientStore` derived from the Session Store.

Having a **Service Store** is helpful to share information among the requests that originated from that Service.

The Service Store provides access to the deriving Session Store's data. We can even persist data from the Service Store into the underlying Store (by setting the boolean `persist` param to true when saving), though we cannot delete Session's data from it.

When we call `store.get(<key>)`, the Service Store first tries to retrieve the associated object from the service scope storage. Not finding, it queries the underlying Session Store. Also, the result is automatically typecasted to the requested type.

```java
Store store = service.getStore();

// Get an object from the store or the deriving session store
// Automatically typecasts the result
AnyType object = store.get("key");
```

To save an object locally, we call `store.save(<key>, <object>)`. Differently, to save an object in the deriving Session Store, we need to call `store.save(<key>, <object>, <persit=true>)`.

```java
Store store = service.getStore();

// Save an object in request scope only
store.save("key", anyObject);

// Save an object in the deriving session store
store.save("key", anyObject, true);
```

To delete a local record, we call `store.delete(<key>)`. We cannot delete records from the deriving Session Store.

```java
Store store = service.getStore();

// Save an object in the deriving store
store.delete("key");
```


## Service

Requestor introduces the **Service** concept to represent a ***subject-oriented client***. It 
means that a Service should bind together closely related network operations according to some 
criteria. In this sense, if we are consuming a REST API, we can build *Entity- or 
Resource-oriented* Services. Analogously, when consuming RPC APIs, we can create *Feature- or 
Group-oriented* Services.

The `Service` is a **session branch** derived from the main [Session](#session) that holds local 
configurations but residually leverages the main's. Within this coordinated context, we can 
define configurations only related to the target subject without cluttering the main context. In 
other words, the Service's [Request Options](#request-options) and the [Store](#store) are 
independent of the Session's and have preferential usage over it.

In order to create a **Service**, we need to extend the `AbstractService` class and implement 
the network operations related to the server API's subject.

### Extending AbstractService

Requestor follows a design principle of favoring good code design to code generation.
In this sense, a great effort is made in crafting classes that can cohesively be extended or 
composed into new richer components, so the user can quickly build the functionalities he needs.
Thus, to create a client service related to a server subject, we can extend the 
`AbstractService` class and implement our calls, like below:

```java
public class BookService extends AbstractService {

    public BookService(Session session) {
        super(session, "/api/books"); // Provide the root path of the REST resource or RPC group
    }

    public Promise<Book> createBook(Book book) {
       Uri uri = getUriBuilder() // get UriBuilder provided by the parent
               .build(); // The UriBuilder starts in the root path, so here we built /api/books uri 
       return request(uri).payload(book).post(Book.class);
    }
   
    public Promise<Collection<Book>> getBooks(String... authors) {
        Uri uri = getUriBuilder()
                .queryParam("author", authors) // append ?author={author} to the root path
                .build();
       return request(uri).get(List.class, Book.class);
    }

    public Promise<Book> getBookById(Integer id) {
        Uri uri = getUriBuilder()
                .segment(id) // add a path segment with the book id like /api/books/123
                .build();
        return request(uri).get(Book.class);
    }

    public Promise<Void> updateBook(Integer id, Book book) {
        Uri uri = getUriBuilder().segment(id).build();
        return request(uri).payload(book).put();
    }

    public Promise<Void> deleteBook(Integer id) {
        Uri uri = getUriBuilder().segment(id).build();
        return request(uri).delete();
    }
}
```

Now, use the service:

```java
// It's a good practice to use Session object as a singleton
Session session = getMySession();

// Create your service passing the session instance.
// The service then takes advantage of all the configurations present in the session
BookService bookService = new BookService(session);

// POST a new book to /api/books
Book book = new Book("Clean Code", "Robert C. Martin", new Date(1217552400000L));
bookService.createBook(book).success( createdBook -> showBook(createdBook) ).fail(...);

// GET all books from /api/books?author=Martin
bookService.getBooks("Martin").success( books -> showBooks(books) ).fail(...);

// GET the book of id 123 from /api/books/123
bookService.getBookById(123).success( book -> showBook(book) ).fail(...);

// PUT a book to /api/books/123
bookService.updateBook(123, updatedBook).success( () -> showSucessMsg() ).fail(...);

// DELETE the resource /api/books/123
bookService.deleteBook(123).success( () -> showSucessMsg() ).fail(...);
```

### Creating the app's abstract Service

It is helpful to handle the errors inside the Service, so we do not always have to set fail callbacks.
Therefore, we recommend implementing an app's abstract Service and extending the client services from it.
This way, it is feasible handle all non-happy paths in one place only. For example, check the 
`applyErrorCallbacks` method below. It adds some predefined callbacks to promises:

```java
public abstract class MyAppService<E> extends AbstractService {

    final Class<E> entityClass;
    final EventBus eventBus;
    
    // Construct your Service with any other object that will allow you to properly handle errors
    public MyAppService(Session session, String uri, Class<E> entityClass, EventBus eventBus) {
        super(session, uri);
        this.entityClass = entityClass;
        this.eventBus = eventBus;
    }

    // Implement the basic operations common to every resource...
    public Promise<E> create(E e) {
        Uri uri = getUriBuilder().build();
        // add the error handling callbacks in the promise returned by the post method
        Promise<E> promise = request(uri).payload(e).post(entityClass);
        return applyErrorCallbacks(promise);
    }
    
    // Implement all your service calls following the same pattern...
    private <T> Promise<T> applyErrorCallbacks(Promise<T> promise) {
        return promise
                .status(404, response -> handleNotFound(response.getRequest().getUri()))
                .status(500, response -> handleServerError(response))
                .timeout(t -> handleTimeout(t))
                .abort(e -> log(e));
    }

    // Implement supporting methods...
    private void handleNotFound(Uri ui) {
        eventBus.fireEvent(new NotFoundEvent(uri));
    }
}
```

**💡 PRO TIP**: Create your own **"AppAbstractService"** and handle the errors in the superclass to save you coding and maintenance cost.
Use the above example as inspiration as also the [RestService](https://github.com/reinert/requestor/blob/master/requestor/core/requestor-api/src/main/java/io/reinert/requestor/RestService.java) class.


## Requesting Fluent API

The Fluent API was designed to provide an enjoyable coding experience while requesting through a chainable interface. Here is how it works:
1. The client `Session` is the starting point when requesting.
2. It exposes the `req(<uri>)` method that returns a `RequestInvoker`, which has request building and invoking capabilities.
3. `RequestInvoker` implements the chainable `RequestBuilder` interface, which allows us to set the request options.
4. Further, `RequestInvoker` implements the `Invoker` interface, which allows us to send the request by calling one of the HTTP Methods.
5. When invoking the request, we also need to specify the class type we expect as the response payload.
6. The request invoking methods return a `Promise<T>` according to the expected type we specified.
7. The `Promise<T>` interface enables callback chaining so that we can handle different results neatly.

In summary, these are the three requesting steps:
1. Build the request
2. Invoke an HTTP method
3. Chain callbacks

```java
//========================================
// 1. Build the request
//========================================

RequestInvoker req = session.req("/api/books")
        .timeout(10000) // Set the request timeout in milliseconds
        .delay(2000) // Set the request delay in milliseconds (wait 2s before sending the request)
        .contentType("aplication/json") // Set the Content-Type header
        .accept("text/plain") // Set the Accept header
        .header("Accept-Encoding", "gzip") // Set a custom header 
        .auth(new BasicAuth("username", "password")) // Set the authentication (more on this later)
        .payload(book); // Set the payload to be serialized in the request body
        .poll(PollingStrategy.SHORT, 5000, 10) // Poll the request each 5s up to 10 times


//========================================
// 2. Invoke an HTTP method
//========================================

// Send a POST request expecting an Integer as response
Promise<Integer> postReq = req.post(Integer.class);

// Send a GET request expecting a Set of Books as response
Promise<Collection<Book>> getReq = req.get(Set.class, Book.class);

// Send a DELETE request ignoring the response body
Promise<Void> delReq = req.delete();

// PUT, PATCH, HEAD and OPTIONS are also available but were omitted for didactic purposes


//========================================
// 3. Chain callbacks
//======================================== 

// You can chain single method callbacks (functional interfaces) to handle success, failure or both: 
postReq.success(payload -> showSuccess(payload)) // Response was 2xx and body was deserialized as Integer
       .fail(response -> showError(response.getStatus())) // Response was unsuccessful (status ≠ 2xx)
       .abort(exception -> log(exception)); // Request was not performed

// If you requested a collection of objects, you can retrieve the deserialized payload as well:
getReq.success(books -> renderTable(books)); // Response was deserialized into a Set of Book objects

// If you want to access the Response in success, declare an additional argument in the callback
delReq.success((payload, response) -> { // Response returned 2xx
    HttpStatus status = response.getStatus(); // Get the response status
    Headers headers = response.getHeaders(); // Get all headers so you can iterate on them
    String hostHeader = response.getHeader("Host"); // Get the Host header
    String contentType = response.getContentType(); // Get the Content-Type header
    Link undoLink = response.getLink("undo"); // Get the 'undo' relation of the Link header
    // Store and access any object by key in session or request scope
    Store store = response.getStore();
});

// You can even deal with specific responses status codes and timeout events
delReq.status(400, response -> alert("Response was 400: " + response.getStatus().getReasonPhrase()))
      .status(429, response -> alert("Response was 429: " + response.getStatus().getReasonPhrase()))
      .status(500, response -> alert("Response was 500: " + response.getStatus().getReasonPhrase()))
      .timeout(e -> alert("Request timed out in " + e.getTimeoutMillis()/1000 + "s."));
```

### **❤️ Write beautiful code**
Joining the three parts together you can write a clean code like below.

```java
// Post a book to the server and retrieve the created entity
session.req("/api/books")
    .payload(book)
    .post(Book.class)
    .success(book -> view.render(book))
    .fail(Notifications::showError);
```


## Links API (HATEOAS)
### Get a Link
### Navigate into a Link
### Use cases
#### Pagination
#### Transactions
#### Undo Operations


## Headers API
### The Headers type
### Existing Header types
### Extending Header types


## URI API
### The URI type
### Building URIs
#### UriProxy
### Parsing URIs


## Binary Data
### Upload
### Download


## Form Data
### Native FormData dispatching
### Url Encoded serialization


## Examples
* [Showcase (Latest Release)](http://reinert.github.io/requestor/latest/examples/showcase)
* [Showcase (0.3.0-SNAPSHOT)](http://reinert.github.io/requestor/0.3.0-SNAPSHOT/examples/showcase)

## Documentation
* [Wiki](https://github.com/reinert/requestor/wiki)
* [Javadoc](http://reinert.github.io/requestor/latest/javadoc/apidocs/index.html)
* [Project Site (Latest Release)](https://reinert.github.io/requestor/latest)
* [Project Site (0.3.0-SNAPSHOT)](https://reinert.github.io/requestor/0.3.0-SNAPSHOT)

## Support
* [Chat](https://gitter.im/reinert/requestor)

## Snapshot installation
If you want to use the latest snapshot, you need to add the sonatype snapshot repository to your POM and set the dependency version to `0.3.0-SNAPSHOT`.

```xml
<repositories>
  ...
  <repository>
    <id>oss-sonatype</id>
    <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
    <snapshots>
      <enabled>true</enabled>
    </snapshots>
  </repository>
  ...
</repositories>

<dependencies>
  ...
  <dependency>
    <groupId>io.reinert.requestor.impl</groupId>
    <artifactId>requestor-gwt</artifactId>
    <version>0.3.0-SNAPSHOT</version>
  </dependency>
  ...
</dependencies>
```

### Latest Snapshot
0.3.0-SNAPSHOT

## License
Requestor is freely distributable under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0.html)

[![Analytics](https://ga-beacon.appspot.com/UA-59721128-2/reinert/requestor?pixel)](https://ga-beacon.appspot.com/UA-59721128-2/reinert/requestor)
