# Requestor [![Build Status](https://travis-ci.org/reinert/requestor.svg?branch=master)](https://travis-ci.org/reinert/requestor) [![Gitter](https://img.shields.io/badge/Gitter-Join%20Chat-blue.svg?style=flat)](https://gitter.im/reinert/requestor)

**Request like a boss.** 😎

*Ask more. Do less. Keep track of everything.*

Requestor is a powerful HTTP Client API for cutting-edge GWT apps. It offers plenty of carefully
designed features that enable developers to rule the network communication process smoothly:
* **Requesting Fluent API** - code as you think, read as you code.
* **Promises** - chain callbacks to different results and statuses.
* **Serialization** - serialize and deserialize payloads integrating any library.
* **Authentication** - make complex async authentication procedures in a breeze.
* **Request/Response Hooking** - asynchronously filter and intercept requests and responses.
* **HTTP Polling** - make long or short polling with a single command.
* **Client Session** - set default options to all requests.
* **Data Store** - save and retrieve data both in session and request scope.
* **Links API** - navigate through an API interacting with its links (HATEOAS for real).
* **Headers API** - directly create and parse complex headers.
* **URI API** - build and parse complicated URIs easily.
* **Binary Data** - upload and download files tracking the progress.

It supports GWT 2.9 and Java 8+ while maintaining backward compatibility with GWT 2.7 and Java 1.5.
In addition, GWT3 and J2CL support are in the roadmap without breaking API compatibility.


## Preview

Make a GET request and deserialize the response body as String:

```java
Session s = new JsonSession();
s.get("http://httpbin.org/ip", String.class).success( Window::alert );
```

Make a POST request sending a serialized object in the payload:

```java
Book book = new Book("Clean Code", "Robert C. Martin", new Date(1217552400000L));
s.post("/api/books", book).success( () -> showSuccessMsg() ).fail( () -> showErrorMsg() );
```

GET a collection of objects:

```java
s.get("/api/books", List.class, Book.class).success( books -> renderTable(books) );
```

The above examples are shortcuts in Session class to make quick requests.
Additionally, you can access the fluent API to build and send more complex requests.

**Note**: Check the [Serialization](#serialization) section to enable ***auto-serialization***.

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

Requestor provides a configurable client `Session`. There we *set default request options* 
that apply to all requests. Also, we are able to *cache and share data* through the `Store`. 
Eventually, we can *reset the session state* at any time.

```java
Session session = new JsonSession();

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

// DELETE the book of ID 123 from '/api/books/123' (returns void)
bookService.delete(123).success(() -> showSuccess("Book was deleted."));
```

Although Requestor provides this generic REST client, extending the `AbstractService` class and 
implementing our service clients is more beneficial. `AbstractService` affords the advantage of
little coding while empowering complete control of the requesting logic. Consequently, it 
improves the testing capabilities and bug tracking. See more details in the [REST](#rest) section.


## Installation

Add the following requestor impl dependency to your POM.

```xml
<dependency>
    <groupId>io.reinert.requestor.impl</groupId>
    <artifactId>requestor-gdeferred</artifactId>
    <version>0.2.0</version>
</dependency>
```

Then, make requestor available to your GWT project by importing the implementation's module.

```xml
<inherits name="io.reinert.requestor.RequestorByGDeferred"/>
```

Requestor primarily focuses on the HTTP Client API. Hence, **requestor-core** declares a Promise 
interface, but does not implement it. The implementation is delegated to **requestor-impl**s. 
Thus, a **requestor-impl** integrates **requestor-core** to some promise library.

Currently, there is one impl available: **requestor-gdeferred**. It binds requestor with 
[gdeferred](https://github.com/reinert/gdeferred) promise API. Furthermore, an impl integrating 
**requestor-core** with [elemental2](https://github.com/google/elemental2) promise API is on the way.

### Latest Release

0.2.0 (18 Feb 2015)


## Yet another REST Client library for GWT?

*No. Not at all*. Requestor is an **HTTP Client API** intended to provide several features related to HTTP communication.
Its scope is broader than popular (and often misunderstood) REST patterns. Requestor precisely models each entity in the
HTTP client-side context to enable its users to handle any requirement in this boundary. It values good **code readability
and maintainability** for the user by providing carefully designed interfaces and abstractions that others can extend and
add their logic with **low or zero integration effort**. Workarounds and hacks are not welcome here. Developers should be able
to implement their requirements keeping **high cohesion** through all their codebase.

Additionally, Requestor was crafted from the client perspective instead of the server's (like other rest libraries were thought).
In that fashion, developers have a more **consistent and intuitive experience** consuming HTTP services while coding.

Besides, we value **code traceability**. So code generation is the last option in design decisions. Whenever a new requirement appears,
we strive to develop a good design solution that allows the user to write less code and achieve the desired results. If something proves
to be inevitably repetitive on the user side, after achieving the best possible solution, then code generation is used to save the user
from repetitive work. Still, leveraging Requestor's components, people will probably automate most of their work using fundamental
object-oriented techniques like abstraction and composition. This way, they will better comprehend what is going on and have complete
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
as part of the [request processing](#request-processing) after its invocation.

```java
req.payload( "a simple string" );
req.payload( new Book("RESTful Web Services", "Leonard Richardson", new Date(1179795600000L) );
req.payload( Arrays.asList(book1, book2, book3) );
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
req.poll( PollingStrategy.LONG); // Equivalent to req.poll( PollingStrategy.LONG, 0 );

// The next requests are dispatched 10s after previous responses up to the limit of 5 requests
req.poll( PollingStrategy.LONG, 10000, 5 );
```

In both cases, if we also set the request's delay option, then the subsequent dispatches' 
*total delay* = *request delay* + *polling interval*.

```java
// The first request is delayed by 2s and the next ones are delayed by 5s (2 + 3)
req.delay(2000).poll( PollingStrategy.SHORT, 3000 );
```

Furthermore, not setting a *polling limit*, we can manually stop the polling by calling 
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

It is worth noting that each new dispatched request will pass through all the **request/response 
processing**. Thereby, we will have every polling request always up to date with our filters, 
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
collection type (*List*, *Set*, and so on) we demanded due to a design limitation of the Java 
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

Serialization is part of the [Request Processing](#request-processing), and deserialization is 
part of the [Response Processing](#response-processing).

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
session.register(MyTypeSerializer::new); // Equivalent to the logic above
```

**💡 PRO TIP**: Always register your `Serializers` using `Providers` to save memory.

Although we can implement our custom Serializers, we often resort to **AUTO-SERIALIZATION** provided
by requestor extensions. Currently, there are two available: `requestor-gwtjackson` and
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

Additionally, Requestor graciously enables us to create new AutoBean instances directly from the Session by 
calling `session.getInstance( <Class> )`.

```java
Book book = session.getInstance(Book.class);
```

The installation procedure is pretty much the same.

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

## Auth

Requestor provides the `Auth` functional interface to authenticate your requests, responsible for delivering the
necessary credential data to the request before sending it. Furthermore, the Auth interface, like any other processor,
is an **async callback**. Therefore, after performing the necessary changes in the request, it requires you to finally
call `request.send()` actually to dispatch it. Also, you may find it helpful to use the Session's Store to retrieve
credential info. Check the following simple example:

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
        }).get().success( /* ... */ );
```

This is just a simple example of how to perform a usual authentication. Indeed, this logic is already provided to
you through the [`BearerAuth`](#bearer-token) implementation.

Notice that Auth's async nature will enable you to do complex stuff before actually providing the credential data
to your request. You can perform other asynchronous tasks before properly configuring the request. If, for instance,
you need to ping another endpoint to grab some token data, you can easily do it. Check the example below:

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

You may do any other useful async task, like performing heavy hash processes using *web workers*, before sending the request.

Additionally, Requestor allows you to register an Auth `Provider` instead of the `Auth` instance. It's just a **factory**
that will create new `Auth` instances for each request. You'll find it really valuable when implementing authentication mechanisms
that require state management, like the `DigestAuth`. Check an example below of how to register an `Auth.Provider`:

```java
session.req("/api/authorized-only")
        .auth(new Auth.Provider() {
            @Override
            public Auth getInstance() {
                // Supposing you implemented MyAuth elsewhere
                return new MyAuth( session.getStore().get("userToken") );
            }
        });
    
// Lambda syntax
session.req("/api/authorized-only")
        .auth( () -> new MyAuth(session.getStore().get("userToken")) );
```


### Basic
### Bearer Token
### Digest
### CORS
### OAuth2
### Custom

## Request Processing
### Filter
### Serialize
### Intercept
### Authenticate

## Response Processing
### Intercept
### Deserialize
### Filter

## Session

## Data Store
### Session Store
### Request Store


## REST

As stated before, Requestor provides the [RestService](#looking-for-some-rest) to handle basic CRUD operations against a REST resource.
But you are likely to implement one service client for each resource you need to consume extending from `AbstractService` class.
**AbstractService** is a **resource oriented client** that enables you to gracefully customize the interaction with a resource.

🧐 It's worth noting that ***AbstractService is a branch session derived from the main session***.
Hence, the default parameters defined in the main session are also applied in your AbstractService.
Everything you set as a default in an AbstractService will only affect that service and will have preference over those defined in the main session.

### Extending AbstractService

Requestor has a design principle of favoring good code design to code generation.
All the pieces were well modeled, so you can easily extend the basic types and create you own client services easily.
Thus, to create a client service related to a server side REST resource, you can extend the `AbstractService` class and
implement your calls with little coding. See example below:

```java
public class BookService extends AbstractService {

    public BookService(Session session) {
        super(session, "/api/books"); // Provide the root path of the REST resource
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

Now use your service client:

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

### Create your app's AbstractService

👌 It's handful to *handle the errors inside your service*, so you don't have to always set fail callbacks.
Therefore, it's a good practice to implement your app's AbstractService and extend the client services from it. 
This way, you can handle all non-happy paths in one place only. As example, check the `applyErrorCallbacks` method below.
It adds some predefined callbacks to promises:

```java
public abstract class MyAppService<R> extends AbstractService {

    final Class<R> resourceClass;
    final EventBus eventBus;
    
    // Construct your Service with any other object that will allow you to properly handle errors
    public MyAppService(Session session, String uri, Class<R> resourceClass, EventBus eventBus) {
        super(session, uri);
        this.resourceClass = resourceClass;
        this.eventBus = eventBus;
    }

    // Implement the basic operations common to every resource...
    public Promise<R> create(R r) {
        Uri uri = getUriBuilder().build();
        // add the error handling callbacks in the promise returned by the post method
        Promise<R> promise = request(uri).payload(r).post(resourceClass);
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

The Fluent API was designed to provide an enjoyable coding experience while requesting through a chainable interface. Here how it works:
1. Your client `Session` is the starting point when requesting.
2. It exposes the `req( <uri> )` method that returns a `RequestInvoker`, which has request building and invoking capabilities.
3. `RequestInvoker` implements the chainable `RequestBuilder` interface, which allows you to set the request options.
4. Also, `RequestInvoker` implements the `Invoker` interface, which allows you to send the request by calling one of the HTTP Methods.
5. When invoking the request, you also need to specify the class type you expect as the response payload.
6. The request invoke methods return a `Promise<T>` according to the expected type you specified.
7. The `Promise<T>` interface enables callback chaining, so you can handle different results neatly.

In summary, these are the three requesting steps:
1. Build your request
2. Invoke an HTTP method
3. Chain callbacks

```java
//========================================
// 1. Build your request
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
// This is executed if the request was successful
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
    <artifactId>requestor-gdeferred</artifactId>
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
