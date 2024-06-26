<p align="center">
  <img src="https://user-images.githubusercontent.com/1285494/158213729-52458234-6c6b-41d5-bd6d-746c12a7c2c4.png" alt="Request like a boss."/>
</p>

## Why Requestor?
It uniquely combines simplicity, completeness and performance in a singular HTTP Client API for any Java derived language.

Requestor is:
* **Async-First** → smoothly handle several concurrent client-server interactions with the max throughput.
* **Event-Driven** → narrow down your code to specific results and implement complex features like HTTP Polling and Streaming seamlessly.
* **Session-Based** → set up multiple client configurations for different backends with instance level customization.
* **Scope-Bounded** → sessions, services and requests have isolated and interconnected contexts with fine-grained control over them.
* **Cache-Assisted** → promptly save and share data among different parts of your application while handling communication.
* **Functional-Friendly** → the api is designed to allow you to leverage the most of java lambdas and functional programming.


## How is it different from *Retrofit* and *Feign-Client*?
These are two of the most common choices regarding HTTP API consumption in the Java ecosystem.
They happen to have some similarities, though, that contrast with *Requestor* fundamentals.
Both were primarily designed to provide auto-generated clients for HTTP APIs declared as interfaces,
strongly biased by popular REST Server APIs, like JAX-RS, that work in the same fashion.
This elementary design decision can cause some issues:
- **High initial friction**: we can be burdened with a bunch of declarations and configurations before starting to use the library. This is special harmful when all we need is just to make a couple of simples requests.
- **Bad code traceability**: when all we have is auto-generated code, it may become difficult to find bugs and understand the code flow when needed. And this time will come.
- **Limited customizability**: when things start getting complex, we may find ourselves adding an excessive amount of code to handle interdependent scenarios.

Differently, Requestor's core goal is to provide high-fidelity modeling of the HTTP concepts oriented by the client's perspective. In addition, a request-response processing cycle was designed to allow us to customize the requests and responses at specific milestones and notify other parts of the system.
All this asynchronously. Around it, third-part concepts like Sessions, Services, and Stores, were attached to provide enhanced functionality and empower a communication-centric approach to build apps.

Requestor's design is extensible enough to allow us to exponentially grow the complexity of our requirements linearly affecting the size of our code, keeping it clean and dry.
With Requestor we can:
- Start simple and **make one line requests**.
- **Request in a sync or async flow**, although all requests are executed asynchronously (in background threads or coroutines), following the *thread-per-request* style, not blocking the main thread. Requestor is superpowered by the [Java Virtual Threads](#-how-to-use-virtual-threads-with-requestor-jdk19)!
- Set actions for specific request results due to a tailor-made event system to the request-response lifecycle, helping us to **write clear and concise code**.
- Straightforwardly enable complex features - such as polling, streaming, and retrying - and **build sophisticated communication flows** painlessly.
- **Add async middlewares to requests and responses** at different milestones in a well-suited lifecycle.
- Conveniently manipulate and interact with Headers, URIs, and Links to **navigate through discoverable HTTP APIs**, employing HATEOAS the right way.


## Features
* [**Requesting Fluent API**](#%EF%B8%8F-requesting-fluent-api-briefing) - code as you think, read as you code.
* [**Event-Driven Callbacks**](#event-driven-callbacks) - set callbacks for different results in a precise event system.
* [**Futures**](#futures) - access the response header, the body and the deserialized payload as soon they are available.
* [**Await**](#await) - alternatively work in a synchronous fashion by waiting for the request to finish.
* [**Serialization**](#serialization) - serialize and deserialize payloads integrating any library.
* [**Authentication**](#authentication) - make complex async authentication procedures in a breeze.
* [**Middlewares**](#processors-middlewares) - asynchronously filter and intercept requests and responses.
* [**HTTP Polling**](#poll) - make long or short polling with a single command.
* [**HTTP Streaming**](#http-streaming) - efficiently stream the byte chunks as soon they are received.
* [**Retry Policy**](#retry) - easily define a retry policy with two alternative approaches.
* [**Session**](#session) - manage all requesting configurations in one place.
* [**Service**](#services) - break down the API consumption into smaller independent contexts.
* [**Store**](#store) - save and retrieve data in different scope levels (session, service and request).
* [**Links**](#links-hateoas) - navigate through an API interacting with its links (HATEOAS for real).
* [**Headers**](#headers) - directly create and parse complex headers.
* [**URIs**](#uri) - build and parse complicated URIs easily.
* [**Binary Data**](#binary-data) - upload and download files tracking the progress.
* [**Form Data**](#form-data) - send both 'multipart/form-data' and 'application/x-www-form-urlencoded' requests.
* [**Gzip Compression**](#gzip_encoding_enabled--boolean) - automatically encode and decode payloads with gzip.
* [**Certificate Auth**](#certificate-authentication) - certificate based authentication made simple.

Requestor is developed on top of three main pillars: (1) **Interoperability**, (2) **Simplicity**, and (3)
**Extensibility**. In that fashion, **requestor-core** is developed in vanilla Java what makes it compatible
with any Java based platform and transpilable to other languages. To provide a fully working implementation, Requestor
impls are required to implement the dispatching mechanism through the wire. Currently, there are two requestor impls
available: **requestor-javanet** for JVM/Android and **requestor-gwt** for GWT2.


## Preview

👨‍💻 Create a Java app that queries the public IP, prints it and exits:

```java
final Session session = Requestor.newSession();

session.get("https://httpbin.org/ip", String.class)    // make a GET request and read the body as String
        .onSuccess( ip -> System.out.println(ip) )     // print the body if response was 2xx
        .onFail( res -> System.out.println("Response status was " + res.getStatus()) ) // print failure message if response was not 2xx
        .onError( e -> System.out.println("Request error: " + e.getMessage()) );       // print error message if no response was received
```

🔥 In **Kotlin**:

```kotlin
runBlocking {
    val runner = CoroutineAsyncRunner(this)
    val session = Requestor.newSession(runner)

    session.get("https://httpbin.org/ip", String::class.java)
            .onSuccess { ip -> println(ip) }
            .onFail { _ -> println("Unsuccessful response received") }
            .onError { _ -> println("An error occurred during the request") }
}
```

Check [here](#kotlin-coroutines) how to install **requestor-kotlin** extension and enable `CoroutineAsyncRunner` usage.

🤔 Prefer sync programming?

```java
final Session session = Requestor.newSession();

try {
    Response response = session.get("https://httpbin.org/ip", String.class)
            .await();

    if (response.getStatus() == Status.OK) {
        String ip = response.getPayload();
        System.out.println(ip);
    } else {
        System.out.println("Unsuccessful response received");
    }
} catch (RequestException e) {
    System.out.println("An error occurred during the request");
    e.printStackTrace();
}
```

👨‍💻 Make a POST request auto serializing an object into the request payload:

```java
Book book = new Book("Clean Code", "Robert C. Martin", new Date(1217552400000L));

session.post("/api/books", book)
        .onSuccess( view::showSuccessMsg )
        .onFail( view::showErrorMsg );
```

👨‍💻 Make a GET request auto deserializing the response payload to a collection of objects:

```java
session.get("/api/books", List.class, Book.class)
        .onSuccess( books -> render(books) );
```

**NOTE:** Check the [Serialization](#serialization) section to enable ***auto-serialization***.

The above examples are shortcuts in Session class to make quick requests.
Further, you can access the fluent API to build and send more complex requests as follows.

### ✍️ Requesting Fluent API *(briefing)*

Requesting involves three steps:
1) Access the request builder by calling `session.req( <uri> )`, and **set the request options** 
   through the chaining interface.
2) Following, we must **call one of the invoke methods**, represented by the corresponding HTTP 
   methods (*get*, *post*, *put*, and so on). In this action, we specify the type we expect to 
   receive in the response payload.
3) Finally, we receive a Request instance, which allows us to **chain callbacks** according to 
   different outcomes.


```java
session.req("/api/books/1")              // 0. Start building the request
       .timeout(10_000)                  // 1. Set the request options
       .header("ETag", "33a64df5") 
       .get(Book.class)                  // 2. Invoke an HTTP method with the expected type
       .onSuccess(book -> render(book))  // 3. Add callbacks to the request
       .onFail(response -> log(response));
```

See the [Requesting Fluent API](#requesting-fluent-api) section to know more details of how it 
works.

Meet all the request options available in the [Request Options](#request-options) section.

### 🛠️ Set up your Session

Requestor features a configurable client [Session](#session). There we *set default request options* 
that apply to all requests. Additionally, since the `Session` is also a [Store](#store), we can use it to *save and
retrieve data*, sharing state among components that rely on that session if necessary. 
Eventually, we can *reset the session state* at any time.

```java
Session session = Requestor.newSession();

// Set all requests to have 10s timeout and 'application/json' Content-Type
session.setTimeout(10_000);
session.setContentType("application/json");

// Perform login, save user info, and authenticate all subsequent requests
session.post("/login", credentials, UserInfo.class)
        .onSuccess(userInfo -> {
            // Save the user info in the session store
            session.save("userInfo", userInfo);
            // Set the default auth for every session request
            session.setAuth(new BearerAuth(userInfo.getToken()));
        })
        .await();

// Make authenticated requests
session.post("/api/books", book).await();

// Retrieve data from the session store
UserInfo userInfo = session.getValue("userInfo");

// Reset the session configuration to the defaults
session.reset();

// Clear all data from the session store
session.clear();

// Now all requests will have the default parameters
session.post("/api/books", book);

// Shutdown the session closing all underlying resources
session.shutdown();
```


### Looking for some REST? 😌

Requestor offers a pre-defined REST client so that we can perform basic CRUD operations against 
a resource. See the example below on how to create a new `RestService`.

```java
bookService = RestService
        .of(Book.class, Integer.class) // <entityClass>, <idClass>
        .at("/api/books")              // <rootPath>
        .on(session);                  // <session>

// Configure your service to always set Content-Type and Accept headers as 'application/json'
bookService.setMediaType("application/json");

// POST a book to '/api/books' and receive the created book from server
Book book = new Book("RESTful Web Services", "Leonard Richardson", new Date(1179795600000L));
bookService.post(book).onSuccess(createdBook -> render(createdBook));

// GET all books from '/api/books'
bookService.get().onSuccess(books -> render(books));

// GET books from '/api/books?author=Richardson&year=2006'
bookService.get("author", "Richardson", "year", 2006).onSuccess(books -> render(books));

// GET the book of ID 123 from '/api/books/123'
bookService.get(123).onSuccess(books -> render(books));

// PUT a book in the resource with ID 123 from '/api/books/123' and receive the updated book
bookService.put(123, book).onSuccess(updatedBook -> render(updatedBook));

// PATCH book's title and year in '/api/books/123' and receive the updated book
bookService.patch(123, book, "title", "year").onSuccess(updatedBook -> render(updatedBook));

// DELETE the book of ID 123 from '/api/books/123' (returns void)
bookService.delete(123).onSuccess(() -> showSuccess("Book was deleted."));
```

ℹ️ Although Requestor provides this generic REST client, extending the `BaseService` class and 
implementing our service clients is more beneficial. BaseService affords the advantage of
little coding while empowering complete control of the requesting logic. Consequently, it 
improves the testing capabilities and bug tracking. See more details in the [Service](#services) 
section.


## Installation

Requestor primarily focuses on the HTTP Client API. Hence, **requestor-core** provides most of the
features but delegates some internals, like the network operation, to the implementations.
The ***requestor impls*** make the bridge between requestor-core and the target platform
(JVM, Android, Browser, etc).

Currently, there are two requestor impls available:
- **requestor-javanet** - it implements requestor for the **JVM** and **Android** platforms providing the network operation powered by the `java.net` package. 
- **requestor-gwt**. It implements requestor for **GWT2** apps that runs on the **Browser** providing the network operation powered by the `XMLHttpRequest`.

### JVM / Android (Java / Kotlin)

The **requestor-javanet** impl is built with jdk8 and compatible with **Java 8+**.

```xml
<dependency>
    <groupId>io.reinert.requestor.impl</groupId>
    <artifactId>requestor-javanet</artifactId>
    <version>1.4.0</version>
</dependency>
```

If you're using jdk12+ and want to make PATCH requests then add the following command line arg to execute your java app:
`--add-opens java.base/java.net=ALL-UNNAMED`.

### Kotlin Coroutines

If we need to integrate Requestor with Kotlin structured concurrency then we can install `requestor-kotlin` ext and use
`CoroutineAsyncRunner` to start a `Session`.

Add `requestor-kotlin` dependency to the project:

```xml
<dependency>
    <groupId>io.reinert.requestor.ext</groupId>
    <artifactId>requestor-kotlin</artifactId>
    <version>1.4.0</version>
</dependency>
```

Use `CoroutineAsyncRunner` to create requestor sessions:

```kotlin
// inside a coroutine scope create the CoroutineAsyncRunner
runBlocking {
    // we can optionally set the Dispatcher as the second arg
    val runner = CoroutineAsyncRunner(this, Dispatchers.IO)
    // create a requestor session using this AsyncRunner
    val session = Requestor.newSession(runner)

    // now every request is bound to this coroutine scope
    session.req("https://httpbin.org/ip")
            .get(String::class.java)
            .onSuccess { ip -> println(ip) }

    // await also works as expected
    val req = session.get("https://httpbin.org/get", String::class.java).await()
    // this line is only executed after the request is finished because we called await
    val ip: String = req.getPayload()
    println(ip)
}
```

### GWT2

The **requestor-gwt** impl is compatible with **GWT 2.7+** (Java 7+).

```xml
<dependency>
    <groupId>io.reinert.requestor.impl</groupId>
    <artifactId>requestor-gwt</artifactId>
    <version>1.4.0</version>
</dependency>
```

Then, make requestor available to your GWT project by importing the implementation's module.

```xml
<inherits name="io.reinert.requestor.gwt.RequestorGwt"/>
```

### J2CL (GWT3)

This requestor impl is specified and we would love to have your contribution to help implementing it.
If you would like to get involded and make Requestor better, get in touch in our [community chat](#resources).

### Latest Release

1.4.0 (2 May 2024)


## Request Options

Requestor's Fluent API exposes many chaining methods to properly configure your request.

Suppose we start building the request below:

```java
RequestInvoker req = session.req("/api/books/");
```

We then have the following options to set up our request.

### *payload*

Set an object as the request payload. This object is then serialized into the HTTP message's body
as part of the [request processing](#processors-middlewares) after its invocation.

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

### *timeout*

Set a period in milliseconds in which the request should be timeout.

```java
req.timeout( 10_000 ); // Timeout after 10s
```

### *delay*

Set a time in milliseconds to postpone the request sending.
The [request processing](#processors-middlewares) will happen only after the delay period. 

```java
req.delay( 5_000 ); // Delay the request for 5s
```

### *skip*

Skip the request/response [processors](#processors-middlewares).

Example skipping the [authentication](#authentication):
```java
req.skip( Process.AUTH_REQUEST );
```

Example skipping filters:
```java
req.skip( Process.FILTER_REQUEST, Process.FILTER_RESPONSE );
```

Example skipping interceptors:
```java
req.skip( Process.INTERCEPT_REQUEST, Process.INTERCEPT_RESPONSE );
```

Example skipping serializers:
```java
req.skip( Process.SERIALIZE_REQUEST, Process.DESERIALIZE_RESPONSE );
```

### *retry*

There are two ways of defining a retry policy: one by informing the events that trigger a retry and a sequence of delays;
and another by implementing a `RetryPolicy`.

#### *retry( delays, events )*

Set a retry policy for the request with two arguments: (1) an array of `delays` in milliseconds and (2) an array of `events`.
The **delays** array is a sequence of times that Requestor will wait before retrying the request respectively.
It also indicates the number of retries that will be performed at most. Furthermore, the **events** array is
a set of events that will trigger a retry. Occurring a request event that is defined in the retry police,
the [callbacks](#event-driven-callbacks) bound to those events are not executed and a retry is triggered. These callbacks will be executed only
after all retries defined in the retry police were made and the retry event persisted to occur.

Regarding the `delays` argument, although we can provide an int array manually, we will often resort to the `DelaySequence`.
It is a factory that provides helpful methods to create sequences of delays according to different criteria.
* **DelaySequence.arithmetic**( *\<initialSeconds\>, \<commonDiff\>, \<limit\>* ) - creates a millis int array with an arithmetic sequence.
  * Ex: `DelaySequence.arithmetic(5, 20, 3)` generates `[5s, 25s, 45s]` 
* **DelaySequence.geometric**( *\<initialSeconds\>, \<ratio\>, \<limit\>* ) - creates a millis int array with a geometric sequence.
  * Ex: `DelaySequence.geometric(3, 2, 4)` generates `[3s, 6s, 12s, 36s]`
* **DelaySequence.fixed**( *\<seconds\>...* ) - creates a sequence with the given seconds array multiplied by 1000.
  * Ex: `DelaySequence.fixed(5, 15, 45)` generates `[5s, 15s, 45s]`

As for the `events` argument, Requestor has a set of pre-defined events in the `RequestEvent` enum, matching the events
that we can bind [callbacks](#event-driven-callbacks) to. Additionally, any `StatusFamily` or `Status` is also an event.
Check some examples of events:
* `RequestEvent.FAIL` - request receives a response with status ≠ 2xx
* `RequestEvent.TIMEOUT` - request has timed out with no response
* `RequestEvent.CANCEL` - request has been cancelled before receiving a response
* `RequestEvent.ABORT` - request has been aborted before sending either manually or due to a runtime exception in the processing cycle
* `StatusFamily.CLIENT_ERROR` - request receives a response with status = 4xx
* `StatusFamily.SERVER_ERROR` - request receives a response with status = 5xx
* `Status.TOO_MANY_REQUESTS` - request receives a response with status = 429
* `Status.of(429)` - request receives a response with status = 429

```java
// Set the request to retry on 'timeout' or '429' responses
req.retry( DelaySequence.geometric(3, 2, 4), RequestEvent.TIMEOUT, Status.TOO_MANY_REQUESTS )

// Set the request to retry on 'cancel' or '429' and '503' responses
req.retry( DelaySequence.arithmetic(5, 20, 3), RequestEvent.CANCEL, Status.of(429), Status.of(503) )

// Set the request to retry on 'timeout', '4xx' and '529'
req.retry( DelaySequence.fixed(5, 15, 45), RequestEvent.TIMEOUT, StatusFamily.CLIENT_ERROR, Status.SERVICE_UNAVAILABLE )
```

#### *retry( RetryPolicy )*

Furthermore, we are able to provide a more complex retry logic by implementing the `RetryPolicy` functional interface.

See this example implementing an exponential backoff retry with random jitter:

```java
public class ExponentialWithJitterRetryPolicy implements RetryPolicy {
    
    public int retryIn(RequestAttempt attempt) {
        // first check the conditions that would make us stop retrying be returning -1

        // event is the result of the request which can be a response or an exception
        if (!StatusFamily.SERVER_ERROR.includes(attempt.getEvent())) {
            // we should retry only 5xx responses
            return -1;
        }

        // retryCount holds the number of times this request was already retried
        if (attempt.getRetryCount() > 2) {
            // we should retry at most three times
            return -1;
        }
        
        // exponentially increase the next timeouts by a ratio of 2
        attempt.setTimeout(attempt.getTimeout() * 2);

        // next retry will happen in two to the power of the number of past retries
        int expDelay = (int) (Math.pow(2, attempt.getRetryCount()) * 1000);

        // let's add a random jitter to increase the chance of avoiding collisions
        int jitter = new Random().nextInt(1000);

        // return the time in milliseconds that the next retry should happen
        return expDelay + jitter;
    }
}
```

Check out an example implementing a Retry-After header retry police:

```java
public class RetryAfterHeaderRetryPolicy implements RetryPolicy {
    
    public int retryIn(RequestAttempt attempt) {
        // the request can fail due to a non success Response or a RequestException

        if (attempt.isResponseAvailable()) {
            // if the request failed due to a non success Response
            // then check if the Retry-After exists
            Response response = attempt.getResponse();
            
            if (response.hasHeader("Retry-After")) {
                // get the value of the Retry-After header
                String seconds = response.getHeader("Retry-After");
                
                // return the Retry-After value converted to milliseconds
                return Integer.parseInt(seconds) * 1000;
            }
        }

        // if the Retry-After header is not present then don't retry
        return -1;
    }
}
```

Once we have implemented our custom `RetryPolicy`, we can set it in the request either with an instance or a Provider.

```java
// register a new instance of our custom retry policy
req.retry( new ExponentialWithJitterRetryPolicy() );
```

When our customized retry policy contains state that should not be shared among different requests,
we must register it with a Provider instead.

```java
// register a Provider of our custom retry policy
req.retry( ExponentialWithJitterRetryPolicy::new );
```

By setting a Provider instead of an instance, every new request will have a different instance of the RetryPolicy.

### *poll*

Needing to ping an endpoint in a specific interval, we can set the poll option with the 
`PollingStrategy` and the `interval` in milliseconds. Additionally, we can set the maximum 
number of requests to stop the polling by informing the `limit` argument.

```java
// Send the request each 3s
req.poll( PollingStrategy.SHORT, 3_000 );

// Send the request each 3s up to the limit of 10 requests
req.poll( PollingStrategy.SHORT, 3_000, 10 );
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
req.poll( PollingStrategy.SHORT, 3_000 );

// The next requests are dispatched as soon the responses are received
req.poll( PollingStrategy.LONG ); // Same as `req.poll( PollingStrategy.LONG, 0 )`

// The next requests are dispatched 10s after previous responses up to the limit of 5 requests
req.poll( PollingStrategy.LONG, 10_000, 5 );
```

In both cases, if we also set the request's delay option, then the subsequent dispatches' 
*total delay* = *request delay* + *polling interval*.

```java
// The first request is delayed by 2s and the next ones are delayed by 5s (2 + 3)
req.delay(2_000).poll( PollingStrategy.SHORT, 3_000 );
```

Furthermore, not setting a *polling limit*, we can manually ***stop*** the polling by calling 
`request.stopPolling()`.

```java
session.req("/api/books/")
       .poll(PollingStrategy.LONG)
       .get()
       .onLoad((response, request) -> {
           if (request.getPollingCount() == 3) {
               request.stopPolling(); // Stop polling after receiving the third response
           }
       });
```

It is worth noting that each new dispatched request will pass through all the [request/response 
processing cycle](#processors-middlewares). Thereby, we will have every polling request always up 
to date with our filters, serializers, and interceptors.

Finally, if we set a retry police, each dispatched request will execute the retries individually as well,
before triggering the retry events.

#### 🔥 Polling in sync flow using *await*

We can poll a request in a sync flow by leveraging the [*await*](#await) feature.
Check how it looks like:

```java
// Create a polling request
PollingRequest<String> req = session.req("https://httpbin.org/get")
        .poll(PollingStrategy.LONG)
        .get(String.class);

// While the request is polling, await for each response and act
while (req.isPolling()) {
    try {
        Response res = req.await();

        doSomething(res);

        if (shouldStop(req, res)) {
            req.stopPolling();
        }
    } catch (RequestException e) {
        handleError(e);
    }
}
```

## Event-Driven Callbacks

Requestor defines an event system according to the [Request Lifecycle](#processors-middlewares).
We can add as many callbacks as we need for each event that may occur.
The events are divided into three main categories: **Load** events, **Error** events, and **Progress** events.
The `error` event is triggered whenever the request is interrupted before receiving a response.
It is subdivided into three events: `abort` (request interrupted before being sent), `cancel` (request interrupted after being sent), and `timeout` (request expired before receiving a response).
The `load` event is triggered whenever a request receives a response. It is subdivided into two events: `success` (2xx response received), `fail` (~2xx response received).
Additionally, any Status Code is also an event. So, if a 201 response is received, the `201` event is triggered.
Finally, the Status Families are also events. Thus, a 201 response triggers the `2` event as well.

Besides, Requestor also fires two **progress** events: the `read` and `write` events. They enable us to track download and upload progress respectively.
While the `error` and `load` (and their children) events are triggered once per request call, the `read` and `write` events are triggered many times per call.
These events also allow us to get each chunk of bytes that is sent or received during a request. They open the [HTTP Streaming](#http-streaming) door for us.

![request-events](https://user-images.githubusercontent.com/1285494/146399333-8294288f-b5b8-4cf6-bcee-e8e2fe939695.png)

After invoking a request, we receive a `Request<T>` instance. It is a deferred object that 
allows us to chain callbacks to handle any event that the request may produce. Besides, 
we can also access all request options that formed this request.

One `Request<T>` will fire either a `load` event or an `error` event only once.
But when setting the [poll](#poll) option while building a request, we receive a `PollingRequest<T>`.
It may trigger `error` or `load` events many times, once per each call that is made.
Additionally, the `PollingRequest<T>` interface extends from `Request<T>` and allows us to access the polling options that formed it as also manually stop the poll.
Notice, nevertheless, that Requestor treats every request as a polling request under the hood.
So even a simple request is a polling request of one call only.
That is the reason why every request needs a [Deferred Pool Factory](#deferredpool-factory). 

### ☑️ Load events

#### **.onSuccess**( [payload [, response [, request]]] -> {} )
* This callback is executed when the response *is successful* (status = 2xx)
* It features the *deserialized payload*, the *response* and the *request* arguments
* All arguments are optional

Example setting a no-arg callback to the success event:
```java
session.get("/health-check")
        .onSuccess(() -> System.out.println("alive!"));
```

Example setting a callback with the payload arg to the success event:
```java
session.get("/profile", UserProfile.class)
        .onSuccess(profile -> render(profile));
```

Example setting a callback with the payload and response args to the success event:
```java
session.req("/profile").get(UserProfile.class)
        .onSuccess((profile, res) -> render(profile, res));
```

Example setting a callback with the payload, response and request args to the success event:
```java
session.get("/endpoint")
        .onSuccess((none, res, req) -> render(res, req));
```

**NOTE:** All callbacks throw exceptions, so we are exempted from handling checked exceptions inside them.
If an exception occurs in a callback logic, the stack trace is printed out, and it does not affect other
callbacks that were added to the request.

#### **.onFail**( [response [, request]] -> {} )
* This callback is executed when the response *is unsuccessful* (status ≠ 2xx)
* It features the *response* and the *request* arguments
* All arguments are optional

Example setting a no-arg callback to the fail event:
```java
session.get("/health-check")
        .onFail(() -> System.out.println("dead!"));
```

Example setting a callback with the response arg to the fail event:
```java
// Print the response body as string
session.get("/endpoint")
        .onFail(res -> print(res.getSerializedPayload().asString()));
```

Example setting a callback with the response and request args to the fail event:
```java
session.get("/endpoint")
        .onFail((res, req) -> {
            System.out.println(res.getStatusCode());
            System.out.println(req.getUri());
        });
```

#### **.onStatus**( statusCode | statusFamily, ( [response [, request]] ) -> {} )
* This callback is executed when the response returns the given *Status Code* or *Status Family*
* It features the *response* and the *request* arguments
* All arguments are optional

Example setting a no-arg callback to the 204 status event:
```java
session.get("/health-check")
        .onStatus(204, () -> System.out.println("alive!"));
```

Example setting callbacks with the response arg to the 404 and 500 status events:
```java
session.get("/endpoint")
        .onStatus(Status.NOT_FOUND, res -> handleNotFound(res))
        .onStatus(Status.INTERNAL_SERVER_ERROR, res -> handleServerError(res));
```

Example setting callbacks with the response and request args to the 2xx, 4xx and 5xx status family events:
```java
session.req("/endpoint").get()
        .onStatus(StatusFamily.SUCCESSFUL, (res, req) -> handleSuccess(res, req))
        .onStatus(StatusFamily.CLIENT_ERROR, (res, req) -> handleClientError(res, req))
        .onStatus(StatusFamily.SERVER_ERROR, (res, req) -> handleServerError(res, req));
```

#### **.onLoad**( [response [, request]] -> {} )
* This callback is executed when any response is returned
* It features the *response* and the *request* arguments
* All arguments are optional

Example setting a callback with the response and request args to the load event:
```java
session.get("/endpoint")
        .onLoad((res, req) -> {
            print(res.getStatusCode());
            print(req.getUri());
        });
```

### ⛔ Error events

#### **.onAbort**( exception [, request] -> {} )
* This callback is executed if the request was *aborted before being sent* (either manually by the user or due to any runtime error)
* It features the *exception* and the *request* arguments
* The *request* argument is optional

Example setting a callback with the exception and request args to the abort event:
```java
session.get("/endpoint")
        .onAbort((exc, req) -> {
            print(exc.getMessage());
            print(req.getUri());
        });
```

#### **.onCancel**( exception [, request] -> {} )
* This callback is executed if the request was *cancelled after being sent* (either manually by the user or due to network error)
* It features the *exception* and the *request* arguments
* The *request* argument is optional

Example setting a callback with the exception arg to the cancel event:
```java
session.get("/endpoint")
        .onCancel(exc -> print(exc.getMessage()));
```

#### **.onTimeout**( exception [, request] -> {} )
* This callback is executed a timeout occurs
* It features the *exception* and the *request* arguments
* The *request* argument is optional

Example setting a callback with the exception arg to the timeout event:
```java
session.get("/endpoint")
        .onTimeout(exc -> {
            print(exc.getTimeoutMillis());
            print(exc.getUri());
        });
```

#### **.onError**( [exception [, request]] -> {} )
* This callback is executed if the request was *aborted before being sent* (either manually by the user or due to any runtime error)
* It features the *exception* and the *request* arguments
* All arguments are optional

Example setting a callback with the exception and request args to the abort event:
```java
session.get("/endpoint")
        .onAbort((exc, req) -> {
            print(exc.getCause());
            print(req.getMethod());
        });
```

**NOTE:** If an error occurs, and no callback for that error kind was set, than the exception stack trace is printed.
Thus, the errors are not hidden if we forgot to add callbacks for them.

### 🚧 Progress events

#### **.onRead**( progress -> {} )
* This callback is executed each time a chuck of bytes is *received*
* It features the *read progress* argument with:
  * the request
  * the incoming response
  * the loaded and total byte length
  * the read byte chunk

Example setting a callback with the read progress arg to the read event:
```java
// Enable read chunking (a.k.a. streaming) on the session
session.save(Requestor.READ_CHUNKING_ENABLED, true);

session.get("/endpoint").onRead(progress -> {
    if (progress.isLengthComputable()){
        print(progress.getTotal());
        print(progress.getLoaded());
        // print loaded / total * 100
        print(progress.getCompletedFraction(100));
    }

    // check if read chunking is enabled
    if (progress.isChunkAvailable()) {
        print(progress.getChunk().asBytes());
        print(progress.getChunk().asString());
    }

    print(progress.getRequest().getUri());
    print(progress.getResponse().getHeaders());
});
```

#### **.onWrite**( progress -> {} )
* This callback is executed each time a chuck of bytes is *sent*
* It features the *write progress* argument with:
    * the request
    * the loaded and total byte length
    * the written byte chunk

Example setting a callback with the write progress arg to the write event:
```java
File file = getFile();

session.req("/endpoint")
        // Enable write chunking (a.k.a. streaming) on the request
        .save(Requestor.WRITE_CHUNKING_ENABLED, true)
        .payload(file)
        .post()
        .onWrite(progress -> {
            if (progress.isLengthComputable()){
                print(progress.getTotal());
                print(progress.getLoaded());
                // print loaded / total * 100
                print(progress.getCompletedFraction(100));
            }
        
            // check if write chunking is enabled
            if (progress.isChunkAvailable()) {
                print(progress.getChunk().asBytes());
                print(progress.getChunk().asString());
            }
        
            print(progress.getRequest().getUri());
        });
```

### Success callbacks and Collections

When requesting, we will always receive a `Request<Collection<T>>` despite the particular 
collection type (*List*, *Set*, and so on) we asked due to a design limitation of the Java 
language, which does not allow "generics of generics." So, if we need the particular type we
asked, we need to explicitly typecast the payload.
See the example:

```java
// An ArrayList was requested, but the get method returned a Request<Collection<Book>>
Request<Collection<Book>> request = session.req("/server/books").get(ArrayList.class, Book.class);

// If wee need to access the List interface, we must typecast the paylaod
request.onSuccess(books -> {
    List<Book> bookList = (List<Book>) books;
    Book firstBook = bookList.get(0);
});
```


## Futures

<sup><b>@GwtIncompatible</b></sup>

Besides providing the async interface to handle requests results, Requestor exposes some Futures
in the three main milestones of the Request-Response lifecycle.

### 1. Request.getResponse() : Future\<IncomingResponse\>

The first milestone occurs when the response header is received but the body is still going to be read.

As soon the response header is available we receive it through the `getResponse()` method of the `Request`.

```java
Future<IncomingResponse> future = session.req("https://httpbin.org/ip")
        .get(String.class) // the type to later deserialize the response body
        .getResponse(); // get the response future

// Calling future.get() holds until the response header is received
IncomingResponse response = future.get();
```

The `IncomingResponse` provides us access to the response **status**, **headers**, **links** and the request
**store** among other useful things.

### 2. IncomingResponse.getSerializedPayload() : Future\<SerializedPayload\>

The second milestone occurs when the response body is read but is still going to be processed and deserialized.

As soon the response body is available we receive it through the `getSerializedPayload()` method of the `IncomingResponse`.

```java
Future<SerializedPayload> future = response.getSerializedPayload();

// Calling future.get() holds until the response body is received
SerializedPayload serializedPayload = future.get();
```

The `SerializedPayload` provides us access to the raw response **body** as bytes or string.

### 3. IncomingResponse.getPayload() : Future\<T\>

The third milestone occurs when the response passes through all [processors](#processors-middlewares)
and is finally made available to the caller.

After the response is completely processed, its body is already deserialized, and we receive it
through the `getPayload()` method of the `IncomingResponse`. This method automatically typecasts
to the desired type.

```java
// Automatically typecasts to the desired type
Future<String> future = response.getPayload();

// Calling future.get() holds until the response finishes processing
String ip = future.get();
```

Check a complete example of how to use Requestor's Future API.

```java
final Session session = Requestor.newSession();

try {
    // Make a request and get the response future
    Future<IncomingResponse> responseFuture =
            session.get("https://httpbin.org/ip", String.class)
                    .getResponse();

    // As soon the response header is received we get it
    IncomingResponse response = responseFuture.get();

    // Although the response was not completely received
    // we can promptly access its status, headers and store
    if (response.getStatus() == Status.OK) {
        // As soon the response body is read we get it
        Future<SerializedPayload> serializedPayloadFuture = response.getSerializedPayload();
        SerializedPayload serializedPayload = serializedPayloadFuture.get();

        // The raw payload is yet going to be deserialized
        // but we can already access its content as bytes or string
        System.out.println(serializedPayload.asString());

        // When the response is completely received it is submitted to the processors
        // After being processed the deserialized payload is made available
        Future<String> payloadFuture = response.getPayload();
        String ip = payloadFuture.get();
        System.out.println(ip);
    } else {
        System.out.println("Unsuccessful response received");
    }
} catch (InterruptedException | ExecutionException e) {
    System.out.println("An error occurred during the request");
    e.printStackTrace();
}

// Close all threads and finish the program
session.shutdown();
```


## Await

<sup><b>@GwtIncompatible</b></sup>

Requestor exposes a way to wait for the request to finish thus allowing the user to  code in a synchronous fashion.

By calling `Request.await()` we instruct the current thread to hold until the `Response` is available and returned.

```java
final Session session = Requestor.newSession();

try {
    Response response = session.req("https://httpbin.org/ip")
            .get(String.class)
            .await();

    if (response.getStatus() == Status.OK) {
        String ip = response.getPayload();
        System.out.println(ip);
    } else {
        System.out.println("Unsuccessful response received");
    }
} catch (RequestException e) {
    System.out.println("An error occurred during the request");
    e.printStackTrace();
}
```


## Serialization

Serialization is part of the [Request Processing](#processors-middlewares), and deserialization is 
part of the [Response Processing](#processors-middlewares).

Requestor exposes the `Serializer` interface responsible for serializing and deserializing a 
specific type while holding the **Media Types** it handles. Therefore, it is possible 
to have multiple Serializers for the same Java Type handling different Media Types, e.g., JSON and 
XML. Requestor's serialization engine is smart enough to **match** the appropriate **Serializer** 
according to the asked class and the request/response's **Content-Type**.

To enable serialization and deserialization, we must register a `Serializer` instance in the
[session](#session). Necessitating only the deserialization part, we can register a 
`Deserializer` implementation, since the Serializer extends from the Deserializer.

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
    public Serializer<?> getInstance() {
        return new MyTypeSerializer(); // return a disposable instance instead of a reference
    }
});

// Lambda syntax
session.register(MyTypeSerializer::new); // Same as `session.register(() -> new MyTypeSerializer())`
```

**💡 PRO TIP**: If you start having too many serializers, consider registering them with `Providers` to save memory.

Although it is possible to implement our custom Serializers, we often resort to **AUTO-SERIALIZATION**
provided by requestor extensions. The existing auto-serialization exts are listed following.


### Gson auto-serialization (JVM/Android)

The `requestor-gson` extension integrates [Gson](https://github.com/google/gson) with Requestor
to enable auto serialization.

First, register the `GsonSerializer` in the Session, then start requesting:

```java
session.register(new GsonSerializer());

session.post("/books", book);

session.get("/books", List.class, Book.class);

session.get("/booksById", Map.class, Long.class, Book.class);
```

We can define a custom `Gson` instance when instantiating the `GsonSerializer`:

```java
Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
session.register(new GsonSerializer(prettyGson));
```

In order to install requestor-gson extension, add the following dependency to your project:

```xml
<dependencies>
  <dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>${gson.version}</version>
  </dependency>
  <dependency>
    <groupId>io.reinert.requestor.ext</groupId>
    <artifactId>requestor-gson</artifactId>
    <version>${requestor.version}</version>
  <dependency>
</dependencies>
```


### Gwt-Jackson auto-serialization (GWT)

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
is automatically done under the hood if we instantiate a `JsonSession`.

```java
// All generated serializers will be automatically registered in this session
Session session = RequestorGwtJackson.newSession();
```

In order to install requestor-gwtjackson extension, add the following dependency to your project:

```xml
<dependencies>
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
</dependencies>
```

Then inherit the `RequestorGwtJackson` GWT module in your gwt.xml file:

```xml
<inherits name="io.reinert.requestor.gwtjackson.RequestorGwtJackson"/>
```

### AutoBean auto-serialization (GWT)

Similarly to the previous extension, `requestor-autobean` provides auto serialization for 
AutoBean interfaces. Likewise, we declare a SerializationModule with the classes and media types 
to generate the serializers.

```java
@MediaType({"application/json", "*/*"})
@AutoBeanSerializationModule({ Author.class, Book.class })
interface MySerializationModule extends SerializationModule {}
```

We do not need to register the auto-generated serializers in our session instance. This
is automatically done under the hood if we instantiate an `AutoBeanSession`.

```java
// All generated serializers will be automatically registered in this session
Session session = RequestorAutoBean.newSession();
```

Further, Requestor graciously enables us to create new AutoBean instances directly from the 
Session by calling `session.getInstance(<Class>)`.

```java
Book book = session.getInstance(Book.class);
```

The installation procedure is conventional.

```xml
<dependencies>
  <dependency>
    <groupId>io.reinert.requestor.ext</groupId>
    <artifactId>requestor-autobean</artifactId>
    <version>${requestor.version}</version>
  <dependency>
</dependencies>
```

Then inherit the `RequestorAutoBean` GWT module in your gwt.xml file:

```xml
<inherits name="io.reinert.requestor.autobean.RequestorAutoBean"/>
```

### Implementing a Serializer

Besides auto-serialization, Requestor's users can also write their own custom serializers. Check 
the following sample of a raw handwritten `Serializer`.

```java
class BookXmlSerializer implements Serializer<Book> {

    @Override
    public Class<Book> handledType() {
        return Book.class;
    }

    @Override
    public String[] mediaType() {
        // Return an array of media-type patterns; wildcards are accepted
        return new String[]{ "*/xml*" };
    }

    @Override
    public SerializedPayload serialize(Book book, SerializationContext ctx) {
        return new TextSerializedPayload(
                "<book><title>" + book.getTitle() + "</title>"
                + "<author>" + book.getAuthor() + "</author>"
                + "<pubDate>" + book.getPubDate().getTime() + "</pubDate></book>");
    }

    @Override
    public SerializedPayload serialize(Collection<Book> books, SerializationContext ctx) {
        StringBuilder sb = new StringBuilder("<array>");
        for (Book b : books) sb.append(serialize(b, ctx).asString());
        sb.append("</array>");
        return new TextSerializedPayload(sb.toString());
    }

    @Override
    public Book deserialize(SerializedPayload payload, DeserializationContext ctx) {
        String response = payload.asString();
        int titleStart = response.indexOf("<title>") + 7;
        int titleEnd = response.indexOf("</title>", titleStart);
        String title = response.substring(titleStart, titleEnd);

        int authorStart = response.indexOf("<author>", titleEnd) + 8;
        int authorEnd = response.indexOf("</author>", authorStart);
        int author = Integer.parseInt(response.substring(authorStart, authorEnd));

        int pubDateStart = response.indexOf("<pubDate>", authorEnd) + 9;
        int pubDateEnd = response.indexOf("</pubDate>", pubDateStart);
        Date pubDate = new Date(Long.parseLong(response.substring(pubDateStart, pubDateEnd)));

        return new Book(title, author, pubDate);
    }

    @Override
    public <C extends Collection<Book>> C deserialize(Class<C> collectionType, SerializedPayload payload, DeserializationContext ctx) {
        String response = payload.asString();
        C collection = ctx.getInstance(collectionType);

        int cursor = response.indexOf("<book>");
        while (cursor != -1) {
            int cursorEnd = response.indexOf("</book>", cursor);
            collection.add(deserialize(new TextSerializedPayload(response.substring(cursor + 6, cursorEnd)), ctx));
            cursor = response.indexOf("<book>", cursorEnd);
        }

        return collection;
    }
}
```

#### Inheritance

Additionally, when the `Serializer` should handle sub-types of the target type, we can extend 
the `HandlesSubTypes<T>` interface and implement the `handledSubTypes` method, like below: 


```java
// Implement the HandlesSubTypes interface
class BookXmlSerializer implements Serializer<Book>, HandlesSubTypes<Book> {

    @Override
    public Class<Book> handledType() {
        return Book.class;
    }

    @Override
    public List<Class<? extends Book>> handledSubTypes() {
        // Return other types that this serializer handles
        return Arrays.asList( AudioBook.class, PaperBook.class );
    }
    
    // rest of the serializer ...
}
```


## Authentication

Requestor features the `Auth` functional interface responsible for authenticating the requests as the last step in the [Request Processing](#request-processing). It delivers the credentials to the request and ultimately sends it. Like any other processor, the Auth interface is an **async callback**. Therefore, after performing the necessary changes in the request, it must call `request.send()` to really send it. Moreover, we may find it advantageous to use the Session's Store to retrieve credential info. Check the following example:

```java
session.req("/api/authorized-only")
        .auth(request -> {
            // Retrieve the token from the store
            String userToken = request.getValue("userToken");

            // Provide the credentials in the Authorization header
            request.setHeader("Authorization", "Bearer " + userToken);

            // Your request will be hanging forever if you do not call `send`
            request.send();
        });
```

This is an example of how to perform a usual authentication. Indeed, this logic is already provided
by the [`BearerAuth`](#bearer-token) implementation.

Notice that Auth's async nature enables us to do complex stuff before actually providing the credential data
to the request. We can perform other asynchronous tasks before properly configuring the request. If, for instance,
we need to ping another endpoint to grab some token data, we can easily do it. Check the example below:

```java
Auth myAuth = request -> {
    // We are reaching another endpoint sending a password to get an updated token
    request.getSession().req("/api/token")
            .payload("my-password") // Set `my-password` in the request body
            .skip(Process.AUTH_REQUEST) // Skip this auth so we don't stuck in an infinite loop
            .post(String.class) // Send a POST request and receive the body as String
            .onSuccess(token -> {
                 // After receiving the updated token, we set it into the request and send it
                request.setHeader("Authorization", "Bearer " + token);
                request.send();
            });
};
```

We may do any other useful async task, like performing heavy hash processes using *web workers*, before sending the request.

Additionally, Requestor allows us to register an Auth `Provider` instead of the `Auth` instance. The Provider is a **factory**
that returns an `Auth` instance for each request. It is really valuable when implementing authentication mechanisms
that require state management, like the `DigestAuth`. Check an example below of how to register an `Auth.Provider` in
the Session:

```java
session.setAuth(new Auth.Provider() {
    public Auth getInstance() {
        // Supposing you implemented MyAuth elsewhere
        return new MyAuth();
    }
});
    
// Lambda syntax
session.setAuth(MyAuth::new);
```


### Basic

In order to facilitate our development, Requestor provides standard Auth implementations. For instance, the `BasicAuth` performs the **basic access authentication** by putting a header field in the form of `Authorization: Basic <credentials>`, where credentials is the Base64 encoding of `username` and `password` joined by a single colon `:`. It might be helpful to retrieve credentials data from the Session Store, like in the following example:

```java
User user = session.getValue("user");

session.req("/api/authorized-only")
        .auth(new BasicAuth( user.getUsername(), user.getPassword() ));
```

BasicAuth optionally accepts a third boolean param called `withCredentials`. It will instruct the 
browser to allow cross-site requests. *(gwt only)*


### Bearer Token

Correspondingly, the `BearerAuth` performs the **bearer token authentication** by adding a header to the request in the form of `Authorization: Bearer <token>`.
See how you can enable this Auth in your Session to all requests using a `Provider`:

```java
session.setAuth(() -> {
    UserInfo userInfo = session.getValue("userInfo");
    return new BearerAuth(user.getToken());
});
```

BearerAuth optionally accepts a second boolean param called `withCredentials`. It will instruct the
browser to allow cross-site requests.


### Certificate Authentication

The `CertAuth` class implements certificate-based authentication to ensure secure communication by
setting up an SSL context with the provided certificate details. This method typically involves using
a private key and a certificate chain to authenticate the client to the server. The class can handle
certificates either from a file path or directly from an input stream, and it can optionally accept
a TrustPolicy to customize trust management during SSL communication.

To use CertAuth, you need to provide the path to your certificate or an input stream containing your
certificate, and the password for accessing the certificate's key store. The SSL context is set up to
use the TLSv1.2 protocol by default.

```java
// Using a certificate from a file path
session.setAuth(new CertAuth("/path/to/cert.pem", "password");

// Using a certificate from an InputStream
InputStream certStream = new FileInputStream("/path/to/cert.pem");
session.setAuth(new CertAuth(certStream, "password"));

// Including a custom TrustPolicy
TrustPolicy trustPolicy = new CustomTrustPolicy(); // Define your custom trust policy
session.setAuth(new CertAuth("/path/to/cert.pem", "password", trustPolicy));
```

**Parameters:**
- `certPath` or `certInputStream`: The path to the certificate file or the InputStream containing the certificate.
- `password`: The password to unlock the key store.
- trustPolicy (optional): A custom policy to modify the default trust management behavior.

**Exceptions:**
- `AuthException`: Thrown if there is any issue loading the certificate or setting up the SSL context.

This class supports loading certificates in the default key store format supported by Java.
Ensure that your certificates are prepared accordingly to avoid runtime issues.


### Digest

Requestor provides a ready-to-use `DigestAuth` supporting **qop** (*auth* or *auth-int*) and 
**md5** hash algorithm.

Instantiate `DigestAuth` in the requests, sessions or services passing the `username`, 
`password` and the `algorithm`. Optionally, there is a fourth boolean `withCredentials` param to
make cross-site requests:

```java
String username = "username";
String password = "password";
String hashAlgo = "md5"
boolean withCredentials = true;

session.req("/api/protected") 
        .auth(new DigestAuth(user, password, hashAlgo, withCredentials))
        .get();
```

When registering `DigestAuth` in the [Session](#session) or [Service](#services), it is recommended 
to do it through a `Provider` to avoid sharing the internal Auth state between requests:

```java
// register a Provider of DigestAuth in the session
session.setAuth(() -> new DigestAuth(session.getValue("username"), session.getValue("password"), "md5"));
```


### OAuth2 (GWT)

Requestor provides client-side OAuth2 authenticators supporting both *header* and *url query param* 
strategies. It is made available by the `requestor-oauth2gwt` extension.

To enable OAuth2 authentication, set the callback url in your oauth provider as `mydomain.com/oauthWindow.html`.  


In order to use it in you app, first install the extension dependency:

```xml
<dependency>
  <groupId>io.reinert.requestor.ext</groupId>
  <artifactId>requestor-oauth2gwt</artifactId>
  <version>${requestor.version}</version>
</dependency>
```

Next, inherit the `RequestorOAuth2` GWT module in your gwt.xml file:

```xml
<inherits name="io.reinert.requestor.gwt.oauth2.RequestorOAuth2"/>
```

Then, according to the required authorization strategy we can either instantiate 
`OAuth2ByHeader` or the `OAuth2ByQueryParam` passing the `authUrl` and the `appClientId`.
Optionally, we can also inform a sequence of `scopes`: 

```java
session.req("https://externaldomain.com/oauth2") 
        .auth(new OAuth2ByQueryParam(authUrl, appClientId, scope1, scope2, ...))
        .get(); 
```

When registering an OAuth2 Auth in a [Session](#session) or in a [Service](#services), it is 
recommended to do it through a `Provider` to avoid sharing the internal Auth state between requests:

```java
// register a Provider of OAuth2ByHeader in the session
session.setAuth(() -> new OAuth2ByHeader(session.getValue("authUrl"), session.getValue("appClientId")));
```

## Processors (middlewares)

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
10. User receives the processed Response

![request-lifecycle](https://user-images.githubusercontent.com/1285494/146391548-e44c0c83-5488-4455-a88e-3f7cdf7ff8a4.png)

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
    public void filter(RequestInProcess request) {
        // Access the Store bounded to this request/response lifecycle
        String encoding = request.getValue("encoding");

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
    public RequestFilter getInstance() {
        // Suposing you implemented MyRequestFilter elsewhere
        return new MyRequestFilter();
    }
});

// Lambda syntax
session.register(MyRequestFilter::new); // Same as `session.register(() -> new MyRequestFilter())`
```

Besides proceeding with the request, we can alternatively ***abort*** it by calling
`request.abort(<MockResponse>|<RequestAbortException>)`. Check below:

```java
session.register(new RequestFilter() {
    public void filter(RequestInProcess request) {
        // Abort the request with a fake response
        request.abort(new MockResponse(Status.BAD_REQUEST));
    }
});
```

If we abort with `MockResponse` then **load** callbacks are triggered,
as well **success** and **status** depending on the response status code.
Otherwise, if the request is aborted with `RequestAbortException`, then **abort** 
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
    public void intercept(SerializedRequestInProcess request) {
        // Access the Store bounded to this request lifecycle
        String encoding = request.getValue("encoding");

        // Modify the request headers
        request.setHeader("Accept-Encoding", encoding);

        // Modify any other request option, including the serialized payload
        String json = request.getSerializedPayload().asString();
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
    public RequestInterceptor getInstance() {
        // Suposing you implemented MyRequestInterceptor elsewhere
        return new MyRequestInterceptor();
    }
});

// Lambda syntax
session.register(MyRequestInterceptor::new); // Same as `session.register(() -> new MyRequestInterceptor())`
```

Besides proceeding with the request, we can alternatively ***abort*** it by calling
`request.abort(<MockResponse>|<RequestAbortException>)`. Check below:

```java
session.register(new RequestInterceptor() {
    public void intercept(SerializedRequestInProcess request) {
        // Abort the request with an exception
        request.abort(new RequestException(request, "Manually aborted"));
    }
});
```

If we abort with `MockResponse` then **load** callbacks are triggered,
as well **success** and **status** depending on the response status code.
Otherwise, if the request is aborted with `RequestAbortException`, then **abort** 
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
    public void intercept(SerializedResponseInProcess response) {
        // Modify the response headers
        response.setHeader(new ContentTypeHeader("application/json"));

        // Modify any other response option, including the serialized payload
        String jsonp = response.getSerializedPayload().asString();
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
    public ResponseInterceptor getInstance() {
        // Suposing you implemented MyResponseInterceptor elsewhere
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
    public void deserialize(DeserializableResponseInProcess response,
                            SerializationEngine engine) {
        // The engine is capable of deserializing the response
        engine.deserializeResponse(response);
        
        // It's possible to perform any async task during deserialization before proceeding
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
    public void filter(ResponseInProcess response) {
        if (!response.hasHeader("Custom-Header"))
            response.setHeader("Custom-Header", "Added after response was received");

        // Check if the caller requested to deserialize the payload as String
        if (response.getPayloadType().getType() == String.class) {
            String payload = response.getPayload().asObject();
            response.setPayload(payload + "\nWE JUST MODIFIED THE PAYLOAD!");
        }

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
    public ResponseFilter getInstance() {
        // Suposing you implemented MyResponseFilter elsewhere
        return new MyResponseFilter();
    }
});

// Lambda syntax
session.register(MyResponseFilter::new); // Same as `session.register(() -> new MyResponseFilter())`
```

## Session

Requestor is a session-based HTTP client. It means that a **Session** ties every configuration and action a user can take related to communication. Thus, the `Session` object is the baseline of every communication process in the application. What is better, Requestor does not restrict its users to having only one global Session. We can have ***as many sessions as it makes sense*** according to our business requirements. For example, if we are building a modern client app, we may communicate with different microservices. It may be reasonable to have one Session for each microservice with different configurations. This flexibility promotes a much more **reliable and maintainable code** since we can isolate different business logics in their own context, avoiding runtime conflicts and undesirable multi-path coding.

To instantiate a new `Session`, we can call `Requestor.newSession()`.

```java
Session session = Requestor.newSession();
```

Besides allowing registration of many [Processors](#processors-middlewares), the Session admits setting many default request options. Along with that, it is possible to reset the Session state.

```java
session.reset();
```

This method will reset all the Session's request options to their default values.

### Session's Thread Pool

The async-first aspect of Requestor in JVM runtime is enabled by the usage of multiple threads.
A `Session` is backed by an `AsyncRunner`. For JVM, Requestor provides the `ScheduledExecutorAsyncRunner`
which is powered by the `ScheduledExecutorService` interface. By default, the ScheduledExecutorAsyncRunner 
instantiates a `ScheduledThreadPoolExecutor` with 10 threads as the core pool size. When creating a new `Session`,
we can pass a `ScheduledExecutorAsyncRunner` instance with our own ScheduledExecutorService instance if we want a
different configuration.

Example creating a Session passing a custom Thread Pool:

```java
int corePoolSize = 20;
ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(corePoolSize);
AsyncRunner asyncRunner = new ScheduledExecutorAsyncRunner(threadPool);

Session session = Requestor.newSession(asyncRunner);
```

The `AsyncRunner` interface exposes the `shutdown()` method that allow us to shut down the underlying thread pool.
This method is also expose by the `Session` which delegates to the underlying `AsyncRunner`.

**💡 PRO TIP**: Often people question themselves what would be the optimal thread pool size.
There's no simple answer to it, and we recommend you read the article
["How to set an ideal thread pool size"](https://engineering.zalando.com/posts/2019/04/how-to-set-an-ideal-thread-pool-size.html)
from Anton Ilinchik. But if you want a quick formula to start, use the following:

```text
Number of Threads = Number of Available Cores * (1 + (Wait time / Service time))
```

* **Wait time** - is the time spent waiting for ***remote tasks*** to complete.
  * waiting for the HTTP response from a web service
  * waiting for the OS to process some task outside your app
* **Service time** - is the time spent processing ***local tasks*** in your app.
  * processing the received HTTP response or OS signal doing things like deserialization, transformations, etc.

Example (from the article): A worker thread makes a call to a microservice, serializes response into JSON and executes
some set of rules. The microservice response time is 50ms, processing time is 5ms. We deploy our application to a server
with a dual-core CPU

```text
ThreadPoolSize = 2 * (1 + (50 / 5)) = 22
```

**👀 HEADS UP**: Currently in the JVM platform we are limited regarding the number of threads we can work with
due to the hard link between the threads and the OS. But, there's a revolution in Java multi-threading coming soon!
The [Project Loom](https://blogs.oracle.com/javamagazine/post/going-inside-javas-project-loom-and-virtual-threads)
is working hard to bring into the Java world the concept of [**Virtual Threads**](https://en.wikipedia.org/wiki/Virtual_threads)
which don't demand the OS to allocate a hard resource for each thread. Once we have it available, we wil be able to
handle thousands (even millions) of co-living threads without overheading the OS and we won't need to worry about
the thread pool size anymore. Requestor is prepared to work with Virtual Threads. As soon they are delivered to the
JDK, Requestor users will experiment a drastic performance gain by acquiring the ability to fire numerous
requests concurrently at a much lower cost.

#### 🔥 How to use Virtual Threads with Requestor? (JDK19+)

**JDK 19** allows us to use **Virtual Threads** as a preview feature. In order to instantiate a `Session` backed by a pool
of virtual threads, see the code below:

```java
// Instantiate a ScheduledExecutorService with max core pool size and a virtual thread factory
ScheduledThreadPoolExecutor threadPool = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(
        Integer.MAX_VALUE,
        Thread.ofVirtual().factory());

// Force the core threads to be destroyed after they become idle (reach keep alive time)
threadPool.allowCoreThreadTimeOut(true);

// Optionally set the keep alive time for; default is 10ms
threadPool.setKeepAliveTime(10L, TimeUnit.MILLISECONDS);

// Instantiate a Requestor Session with this virtual thread pool wrapped in a ScheduledExecutorAsyncRunner
Session session = Requestor.newSession(new ScheduledExecutorAsyncRunner(threadPool));
```

**NOTE:** Add the VM option `--enable-preview` when running the JDK19 program in order to access the virtual threads.

### Session's Request Options

The same request options made available by the `Session` are also provided by in any [`Service`](#services).

#### Media-Type

This session configuration will be applied to every request's Content-Type and Accept headers.

```java
session.setMediaType("application/json");
```

#### Headers

We can set any header to be shared among all session's requests.

```java
// Set a header into the session
session.setHeader("Accept-Encoding", "gzip");

// Get a header from the session
Header header = session.getHeader("Accept-Encoding");

// Remove a header from the session
boolean removed = session.delHeader("Accept-Encoding");
```

#### Auth

This session configuration will be applied to every request's [`auth`](#auth) option. We can 
either register an `Auth` instance or a `Provider`.

```java
// The same Auth instance will be used by all requests
session.setAuth( new BearerAuth(session.getValue("token")) );

// By setting a Provider, each request will have a new Auth instance
// Helpful to restrict the Auth's internal state to the request lifecycle
session.setAuth( () -> new BearerAuth(session.getValue("token")) );
```

#### Timeout

This session configuration will be applied to every request's [`timeout`](#timeout) option.

```java
// Every request will have a timeout of 20s
session.setTimeout(20_000);
```

#### Delay

This session configuration will be applied to every request's [`delay`](#delay) option.

```java
// Every request will have a delay of 3s
session.setDelay(3_000);
```

#### Retry

This session configuration will be applied to every request's [`retry`](#retry) option.

```java
// Every request will have a retry policy of 5s, 10s and 30s on 429 responses
session.setRetry(DelaySequence.fixed(5, 10, 30), Status.TOO_MANY_REQUESTS);
```

```java
// Every request will have a new instance of MyRetryPolicy
session.setRetry(MyRetryPolicy::new);
```

### Requesting

The Session is the starting point to build requests. We access the request builder by calling `session.req(<Uri>)`. Since the builder is also an invoker, we can call an invoke method any time to send the request. The invoke methods are named according to the respective HTTP methods they claim. All those methods are chainable, as demonstrated below:

```java
// Start requesting informing the URI
RequestInvoker req = session.req("/api/book/1");

// Set up the request
req = req.timeout(5_000);

// Invoke the request
Request<Book> request = req.get(Book.class);

// All together
Request<Book> request = session.req("/api/book/1")
        .timeout(5_000)
        .get(Book.class);
```

In order to better understand the requesting mechanics, refer to the [Requesting Fluent API](#requesting-fluent-api) section.

In addition to the fluent request builder exposed by the `req` method, the Session features **direct invoker methods** that quickly dispatch requests.
These are named likewise the HTTP Methods they invoke.

```java
Book book = new Book("Clean Code", "Robert C. Martin");

// Same as `session.req("/api/books").payload(book).post(Book.class)`
Request<Book> postReq = session.post("/api/books", book, Book.class);

// Same as `session.req("/api/books").get(List.class, Book.class)`
Request<Collection<Book>> getReq = session.get("/api/books", List.class, Book.class);
```

### DeferredPool Factory

Another convenient feature is the possibility of instantiating a Session with a customized `DeferredPool.Factory`. This factory provides `Deferred` instances to the request dispatcher, returning a `Request` to the Session's user. Thus, we can immediately add some global callbacks to keep our code DRY when generating a Deferred instance.

The example below demonstrates a customized DeferredPool Factory that fires a `ShowLoadingEvent` right before the request is sent and fires a `HideLoadingEvent` once the request gets [loaded](#event-driven-callbacks) ('load' event) or [interrupted](#event-driven-callbacks) ('error' event).

```java
class AppDeferredPoolFactory implements DeferredPool.Factory {

    public <T> DeferredPool<T> create(SerializedRequest request, AsyncRunner asyncRunner) {
        final DeferredPollingRequest<T> deferred = new DeferredPollingRequest<T>(request, asyncRunner);

        // Show loading widget before sending the request
        APP_FACTORY.getEventBus().fireEvent(new ShowLoadingEvent());

        // Hide loading widget on load or abort
        deferred.onLoad(() -> APP_FACTORY.getEventBus().fireEvent(new HideLoadingEvent()))
                .onError(() -> APP_FACTORY.getEventBus().fireEvent(new HideLoadingEvent()));

        return deferred;
    }
}
```

Therefore, we can instantiate our Session with this customized Deferred Pool Factory, as demonstrated below:

```java
Session session = Requestor.newSession(new AppDeferredPoolFactory());
```


## Store

Requestor features a component to save and retrieve objects by key: the `Store`. The `Session`, the `Service`, the `Request`, and the `Response` types extend the `Store` interface so that all of them provide such functionality.

When a Store is generated from another store, it is linked to the previous Store in a tree structure. This structure allows the Stores to maintain their own scope of saved data while delegating queries to the parents when they do not find an object.

For example, if we create a Request from a Session, the Request is a Store linked to the Session Store. Differently, if we create a Request from a Service, the Request Store is linked to the Service Store. In its turn, since a Service is created from a Session, the Service Store is linked to the Session Store.

Also, note that when a Request produces a Response, they share the same Store. So there is one Store only in a Request-Response lifecycle.

Finally, a Store emits some important events that allow us to react when modifications happen to it.
* **Saved Event** - it's fired when a new data is saved into the store.
  * It provides us access to the `newData` that's being saved and also the `oldData` that was in the key's slot before (`null` if there was none).
  * We can listen to this event by registering a handler with the `onSaved` method.
* **Removed Event** - it's fired when a data is removed from the store.
    * It provides us access to the `oldData` that's being removed.
    * We can listen to this event by registering a handler with the `onRemoved` method.
* **Expired Event** - it's fired when a data TTL expires.
    * It provides us access to the `oldData` that has expired.
    * We can listen to this event by registering a handler with the `onExpired` method.
    * By default, after expiration, the data is removed, thus firing the removed event also.
      * Not wanting every data to be automatically removed, we can call `store.save(Store.REMOVE_ON_EXPIRED_DISABLED, true)`
      * We can alternatively refresh the data in the onExpired handler to avoid it getting deleted with `event.getStore().refresh(key)` or `event.getStore().refresh(key, ttl)` (to also set a new TTL)

All handlers provide a `cancel()` method that we can call to deregister (unsubscribe) the handler from the store.

A Store provides the following operations:

```java
interface Store {

    // Retrieves the value of the data saved with the given key.
    // If no data is found in the local store, the upstream stores are queried.
    <T> T getValue(String key);
    
    // Retrieves the data object saved with the given key.
    // If no data is found in the local store, the upstream stores are queried.
    Data getData(String key);

    // Saves the value into the store associating with the key.
    // Returns the same store to enable method chaining.
    // Fires the onSaved event.
    Store save(String key, Object value);

    // Saves the value associating with the key in an upstream store according to the specified level.
    // To persist in the immediate parent store, set the level param to Level.PARENT.
    // To persist in the root upstream store, set the level param to Level.ROOT.
    Store save(String key, Object value, Level level);

    // Saves data into the store with a time-to-live (TTL) period in milliseconds.
    // After the TTL expires, the onExpired event is fired and the data is removed.
    // Since the data is removed, the onRemoved event is also fired.
    Store save(String key, Object value, long ttlMillis);

    // Saves data with a time-to-live (TTL) period into an upstream store.
    Store save(String key, Object value, long ttlMillis, Level level);

    // Checks if there's an object associated with the given key.
    // If no data is found in the local store, the upstream stores are queried.
    boolean exists(String key);

    // Checks if there's an object associated with the given key and if it's equals to the given value.
    // If no data is found in the local store, the upstream stores are queried.
    boolean exists(String key, Object value);

    // Deletes the object associated with this key if it owns such record.
    // This method affects only the local store. (It's never delegated to the upstream stores)
    // Returns the data that was removed or `null` if there was no data associated with the given key.
    // Fires the onRemoved event.
    Data remove(String key);
    
    // Refreshes the data saved with this key extending its valid time for the given TTL.
    // It affects only the local store, i.e., it's not residually executed in the upstream stores.
    // Returns the data that was refreshed or `null` if there was no data associated with the given key.
    Data refresh(String key, long ttlMillis);
    
    // Refreshes the data saved with this key extending its valid time for its original TTL.
    Data refresh(String key);

    // Clears all data owned by this Store.
    void clear();

    // Clears all data owned by this Store.
    // If you don't want the onRemoved handlers to be triggered then set fireRemovedEvent to false.
    void clear(boolean fireRemovedEvent);

    // Registers a handler to be executed AFTER a new data is SAVED into the local store.
    Store onSaved(String key, Handler handler);

    // Registers a handler to be executed AFTER a new data is REMOVED from the local store.
    Store onRemoved(String key, Handler handler);

    // Registers a handler to be executed AFTER the data TTL EXPIRES.
    Store onExpired(String key, Handler handler);
}
```

In summary:
* A `Session` ***is*** a store (extends the `Store` interface) with no stores attached to it (i.e. a **Root Store**).
* A `Service` ***is*** a store derived from another store (specifically a `Session`). Hence, it is a **Leaf Store** with a **Session** as it's parent (and root) store. 
* A `Request` (and it's respective `Response`) ***is*** a **Leaf Store** derived either from a `Session` or a `Service`.
  * If a **Request** is created from a **Session** directly, then the **Session** is its **PARENT and ROOT** store.
  * If a **Request** is created from a **Service** instead, then
    * the **Service** is its **PARENT** store and
    * the **Session** is its **ROOT** store.

### Session Store

Since a Session is not linked to any parent, it is a Root Store.
With it, we can save, retrieve and remove objects by key.

```java
// Save an object in the session store
session.save("key", anyObject);

// Get an object from the session store
// Automatically typecasts to the requested type
AnyType object = session.getValue("key");
        
// Check if there's an object with that key
boolean isSaved = session.exists("key");

// Check if there's an object with that key equals to the given value
boolean isEquals = session.exists("key", anyObject);

// Remove the object from the session store
boolean isRemoved = session.remove("key");
```

### Request Store

Both the Request and the Response is a Store available during the [Request Lifecycle](#processors-middlewares) and accessed within the [Processors](#processors-middlewares).

Having a transient **Request Store** is helpful to share information among **Processors** without cluttering the **Session Store** or **Service Store** that generated it.

The Request Store provides access to the upstream Stores' data. We can even persist data from the Request Store into the parent Stores, though we cannot delete data from them.

When we call `request.getValue(<key>)`, the Request Store first tries to retrieve the associated object from its own scope. Not finding, it queries the parent Store, and so on until it reaches the Root Store. Also, the result is automatically typecasted to the requested type.

```java
// Get an object from the store or the deriving store
// Automatically typecasts the result
AnyType object = request.getValue("key");
```

The same underlying Store is shared by a Request and a Response. Hence, we can also access the Request scope Store from the Response, sharing data in a single Request-Response lifecycle.

```java
session.req("/server")
       .save("hidden", true) // save data in the request store
       .get()
       .onLoad(response -> {
            // retrieve data from the request store
            if (response.exists("hidden", true)) {
                // do something...
            }
        });
```

To save an object locally, we call `request.save(<key>, <object>)`.
Differently, to save an object in an upstream Store, we call `request.save(<key>, <object>, <level>)`.
If the request was originated from a [Service](#services) and we set the level as `Level.PARENT`, then the data is saved in the Service Store.
But if we set the level as `Level.ROOT`, the data is saved in the Session Store that is linked to the Service.
In the other hand, if the request was directly originated from a [Session](#session), then both `Level.PARENT` and `Level.ROOT` will cause the same effect of saving in the Session Store, because the Request's parent and root Stores are the same.

```java
/* SESSION scenario */
Session session = getSession();

session.get("/server")
       .onLoad(response -> {
            // Save an object in the parent store
            // Since this is a session's request, data is saved in the Session Store
            response.save("key", anyObject, Level.PARENT);

            // Save an object in the root store
            // Since this is a session's request, it has the same effect of the previous
            response.save("key", anyObject, Level.ROOT);
        });
```

```java
/* SERVICE scenario */
Service service = getService();

service.get()
       .onLoad(response -> {
           // Save an object in the parent store
           // Since this is a service's request, data is saved in the Service Store
           response.save("key", anyObject, Level.PARENT);

           // Save an object in the root store
           // It will save in the Session Store from which the Service was created
           response.save("key", anyObject, Level.ROOT);
       });
```

To delete a local record, we call `request.remove(<key>)`. We cannot remove objects from the upstream Stores.

```java
// Delete the record associated with the given key
request.remove("key");

// The response share the same store with the request
response.remove("key");
```

**💡 PRO TIP**: Request scope store is specially useful to deal with exceptional cases in [request/response processors](#processors-middlewares).
For instance, suppose you created processors to show a loading widget when requesting and hide when the response is received or an error occurs.
But, for some reason, you want to make 'hidden' requests, so that the loading widget is not shown.
You can then call `.save("hidden", true)` when building the request and check for this flag in the processors by calling `.exists("hidden", true)` to skip displaying the loading widget.
Requestor's [showcase app](https://reinert.github.io/requestor/latest/examples/showcase) implements such scenario.
[Here](https://github.com/reinert/requestor/blob/master/examples/requestor-showcase/src/main/java/io/reinert/requestor/examples/showcase/Showcase.java#L80) a hidden ping request is executed to wake-up the server,
and [here](https://github.com/reinert/requestor/blob/master/examples/requestor-showcase/src/main/java/io/reinert/requestor/examples/showcase/ShowcaseDeferredFactory.java#L44) the Request Store is queried to skip showing the loading widget.

### Service Store

A Service is a Store derived from the Session Store.

Having a **Service Store** is helpful to share information in the context of that Service only.

The Service Store provides access to the parent Session Store's data. We can even persist data from the Service Store into the Session Store, though we cannot delete Session's data from it.

When we call `service.getValue(<key>)`, the Service Store first tries to retrieve the associated object from its own scope. Not finding, it queries the parent Session Store. Also, the result is automatically typecasted to the requested type.

```java
// Get an object from the store or the deriving session store
// Automatically typecasts the result
AnyType object = service.getValue("key");
```

To save an object locally, we call `service.save(<key>, <object>)`.
Differently, to save an object in the parent Session Store, we call `service.save(<key>, <object>, <level>)`.

```java
Service service = getService();

// Save an object in service scope only
service.save("key", anyObject);

// Save an object in the parent session store
// Level.PARENT causes the same effect here
service.save("key", anyObject, Level.ROOT);
```

To delete a local record, we call `service.remove(<key>)`. We cannot delete records from the parent Session Store.

```java
// Delete the record associated with the given key
service.remove("key");
```


## Services

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

In order to create a **Service**, we need to extend the `BaseService` class and implement 
the network operations related to the server API's subject.

### Extending BaseService

Requestor follows a design principle of favoring good code design to code generation.
In this sense, a great effort is made in crafting classes that can cohesively be extended or 
composed into new richer components, so the user can quickly build the functionalities he needs.
Thus, to create a client service related to a server subject, we can extend the 
`BaseService` class and implement our calls, like below:

```java
public class BookService extends BaseService {

    public BookService(Session session) {
        super(session, "/api/books"); // Provide the root path of the REST resource or RPC group
    }

    public Request<Book> createBook(Book book) {
       Uri uri = getUriBuilder() // get UriBuilder provided by the parent
               .build(); // The UriBuilder starts in the root path, so here we built /api/books uri 
       return req(uri).payload(book).post(Book.class);
    }
   
    public Request<Collection<Book>> getBooks(String... authors) {
        Uri uri = getUriBuilder()
                .queryParam("author", authors) // append ?author={author} to the root path
                .build();
       return req(uri).get(List.class, Book.class);
    }

    public Request<Book> getBookById(Integer id) {
        Uri uri = getUriBuilder()
                .segment(id) // add a path segment with the book id like /api/books/123
                .build();
        return req(uri).get(Book.class);
    }

    public Request<Void> updateBook(Integer id, Book book) {
        Uri uri = getUriBuilder().segment(id).build();
        return req(uri).payload(book).put();
    }

    public Request<Void> deleteBook(Integer id) {
        Uri uri = getUriBuilder().segment(id).build();
        return req(uri).delete();
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
bookService.createBook(book).onSuccess( createdBook -> showBook(createdBook) ).onFail(...);

// GET all books from /api/books?author=Martin
bookService.getBooks("Martin").onSuccess( books -> showBooks(books) ).onFail(...);

// GET the book of id 123 from /api/books/123
bookService.getBookById(123).onSuccess( book -> showBook(book) ).onFail(...);

// PUT a book to /api/books/123
bookService.updateBook(123, updatedBook).onSuccess( () -> showSuccessMsg() ).onFail(...);

// DELETE the resource /api/books/123
bookService.deleteBook(123).onSuccess( () -> showSuccessMsg() ).onFail(...);
```

### Creating the app's BaseService

It is helpful to handle the errors inside the Service, so we do not always have to set fail callbacks.
Therefore, we recommend implementing an app's BaseService and extending the client services from it.
This way, it is feasible handle all non-happy paths in one place only. For example, check the 
`applyErrorCallbacks` method below. It adds some predefined callbacks to every request:

```java
public abstract class MyAppService<E> extends BaseService {

    final Class<E> entityClass;
    final EventBus eventBus;
    
    // Construct your Service with any other object that will allow you to properly handle errors
    public MyAppService(Session session, String baseUri, Class<E> entityClass, EventBus eventBus) {
        super(session, baseUri);
        this.entityClass = entityClass;
        this.eventBus = eventBus;
    }

    // Implement the basic operations common to every resource...
    public Request<E> create(E e) {
        Uri uri = getUriBuilder().build();
        // add the error handling callbacks in the request returned by the post method
        Request<E> request = request(uri).payload(e).post(entityClass);
        return applyErrorCallbacks(request);
    }
    
    // Implement all your service calls following the same pattern...
    private <T> Request<T> applyErrorCallbacks(Request<T> request) {
        return request
                .onStatus(404, response -> handleNotFound(request.getUri()))
                .onStatus(500, response -> handleServerError(response))
                .onTimeout(t -> handleTimeout(t))
                .onError(e -> log(e));
    }

    // Implement supporting methods...
    private void handleNotFound(Uri ui) {
        eventBus.fireEvent(new NotFoundEvent(uri));
    }
}
```

**💡 PRO TIP**: Create your own base **"AppService"** class and handle the errors in the superclass to save you coding and maintenance cost.
Use the above example as inspiration as also the [RestService](https://github.com/reinert/requestor/blob/master/requestor/core/requestor-core/src/main/java/io/reinert/requestor/core/RestService.java) class.


## Links (HATEOAS)

Requestor's `Response` interface afford helpful methods to easily grab links from the HTTP Link Header 
by their relations (`rel` attribute).

According to the [RFC 5988](https://tools.ietf.org/html/rfc5988), a link element has the following 
attributes: *anchor*, *media*, *hrefLang*, *rel*, *rev*, *title* and *type*. We can access all 
those attributes, and also the properly formed *uri*, through the `Link` interface.

A regular Link Header carries many Link elements, which are normally identified by their relations,
in the following manner:

```text
Link: </the-book/index>; rel="index"; title="Index"; hreflang="en"; type="text/html",
      </the-book/foreword>; rel="foreword"; title="Foreword"; hreflang="en"; type="text/html",
      </the-book/chapter4>; rel="next"; title="Next Chapter"; hreflang="en"; type="text/html",
      </the-book/chapter2>; rel="previous"; title="Previous Chapter"; hreflang="en"; type="text/html"
```

In order to get a specific `Link` from a `Response`, we can call `response.getLink(<rel>)`:

```java
// Get a link by its rel attribute
Link indexLink = response.getLink("index");

// Get the link title
String title = indexLink.getTitle();

// Get the link hrefLang
String lang = indexLink.getHrefLang();

// Get the link type
String type = indexLink.getType();

// Get the parsed link URI
Uri uri = indexLink.getUri();
```

We can also iterate over all links of a `Response` with `response.getLinks()`:

```java
for (Link link : response.getLinks()) {
    view.addLink(link);
}
```

Finally, when requesting, we can directly use the link by calling `session.req(<link>)`:

```java
Request<Void> nextRequest = session.req( response.getLink("next") ).get();
```

The proper usage of links is the key to enter the wonderland of leveraging *hypermedia as the 
engine of application state* (HATEOAS). Requestor encourages its users to heavily rely on links 
when interacting with HTTP APIs and let the server app dictate the paths that the client can take.   

There are some common scenarios that the usage of links reveals to be really valuable like 
**pagination** (*next*, *previous*, *first*, and *last* links), **distributed transactions** 
(*next* and *rollback* links) and **reversible commands** (*undo* link). 

See the pagination example below:

```java
// Supposing we correctly rendered the links in our view
void onLinkClicked(Link link) {
    session.req(link)
            .get(Document.class)
            .onSuccess((content, response) -> {
                view.renderContent(content);
                view.renderLinks(response.getLinks());
            });    
}
```

## Headers
\#TBD: write about the Headers map, the Header abstraction, existing Header implementations and how to extend it.


## URI
Requestor features the `Uri` type to facilitate accessing the URI parts.

Consider the following URI structure:

<img width="708" alt="uri-details" src="https://user-images.githubusercontent.com/1285494/145720707-02d772ea-5c51-4324-a7d5-30d7d8e2ecb1.png">

The `Uri` type allows us to access each part of this URI:

```java
Uri uri = Uri.create("https://alice:a1ef34df@example.com:123/discussion;subject=tech/questions/?order=newest&order=alphabetic#top");

// the whole URI as string
String uriString = uri.toString(); 

// main URI parts
String scheme = uri.getScheme();
String user = uri.getUser();
String password = uri.getPassword();
String host = uri.getHost();
int port = uri.getPort();
String path = uri.getPath();
String query = uri.getQuery();
String fragment = uri.getFragment();

// returns ["discussion", "questions"]
List<String> segments = uri.getSegments();

// matrix params by segment
Collection<Uri.Param> discussionParams = uri.getMatrixParams("discussion");
Uri.Param subjectParam = uri.getMatrixParam("discussion", "subject");
String tech = subjectParam.getValue();

// query params
Collection<Uri.Param> queryParams = uri.getQueryParams();
Uri.Param orderParam = uri.getQueryParam("order");
// returns ["newest", "alphabetic"]
List<String> values = orderParam.getValues();
```

When we call `Uri.create` a new Uri object is created with the URI string. However, since the parse operation may be
expensive, this string is not parsed prematurely. Instead, the `Uri.toString()` initially returns the given URI string.
Whenever any other Uri method is called, the string is parsed and the user can access each part individually.

Further, Requestor provides the `UriBuilder` to make it easier to build complex URIs.
The code below shows how to build the above URI:

```java
Uri uri = Uri.builder()
        .scheme("https")
        .user("alice")
        .password("a1ef34df")
        .host("example.com")
        .port(123)
        .segment("discussion")
        .matrixParam("subject", "tech")
        .segment("questions")
        .queryParam("order", "newest", "alphabetic")
        .fragment("top")
        .build();
```

Finally, notice that all URI parts are properly decoded when parsing and encoded when building by the `UriCodec`.


## Binary Data

Requestor properly handles binary types in the payload such as `File`, `InputStream`, `byte[]`, and `Byte`.

Example sending a POST request with a `InputStream` in the payload, tracking the upload progress.
```java
InputStream is = getInputStream();

session.req("/api/upload")
        .contentType("application/octet-stream")
        .payload(is) // Set the InputStream as the payload
        .post()
        .onWrite(progress -> print(progress.getCompletedFraction(100))) // Print the percent upload progress
```


GET request receiving a `byte[]` in the response payload, tracking the download progress.
```java
session.req("/api/download")
        .accept("application/octet-stream")
        .get(byte[].class) // Set byte[] as the expected type in the response payload
        .onRead(p -> print(p.getCompletedFraction(100))) // Print the percent download progress
        .onSuccess(bytes -> save(bytes)); // Handle the received byte[]
```

For GWT docs, see [Showcase](https://reinert.github.io/requestor/latest/examples/showcase/#binary-data).

### HTTP Streaming

<sup><b>@GwtIncompatible</b></sup>

In order to efficiently stream a response to another source, use the `onRead` callback to get early access to each chunk
of bytes that is received from network.

Example streaming a response directly to an OutputStream:
```java
final OutputStream os = getOutputStream();

session.req("/api/download")
        .save(Requestor.READ_CHUNKING_ENABLED, true) // Enable read chunking (a.k.a. streaming) on the request
        .get()
        .onRead(progress -> os.write(progress.getChunk().asBytes())) // Write each chunk of bytes directly to the OS
        .onLoad(os::close) // Close the OS when the request finishes
        .onError(os::close) // Close the OS when the request crashes
```


## Form Data

Requestor provides the `FormData` object with a builder to facilitate handling form requests.
Additionally, we can set the request **contentType** either as `"multipart/form-data"` or as
`"application/x-www-form-urlencoded"` and Requestor will properly serialize the payload accordingly.

Example POSTing a `"application/x-www-form-urlencoded"` payload:
```java
FormData data = FormData.builder()
        .append("string", "value")
        .append("int", 1)
        .append("long", 10L)
        .append("double", 1.5)
        .append("boolean", true)
        .build();

session.req("/api/form")
        .contentType("application/x-www-form-urlencoded") // Instruct the desired serialization
        .payload(data) // Set the FormData as the request payload
        .post() // Send a POST request
```

Example POSTing a `"multipart/form-data"` payload with binary contents:
```java
File file = getFile();
InputStream is = getInputStream();
byte[] bytes = getBytes();

FormData data = FormData.builder()
        .append("string", "value")
        .append("file", file) // Put a File in the FormData
        .append("inputStream", is) // Put a InputStream in the FormData
        .append("bytes", file) // Put a byte[] in the FormData
        .build();

session.req("/api/form")
        .contentType("multipart/form-data") // Instruct the desired serialization
        .payload(data) // Set the FormData as the request payload
        .post() // Send a POST request
```

For GWT docs, see [Showcase](https://reinert.github.io/requestor/latest/examples/showcase/#form-data).


## Customizations

<sup><b>@GwtIncompatible</b></sup>

Requestor exposes some keys that allow its users to customize request processing configurations
such as buffer sizes, enabling chunking (streaming), and others.

These customizations are saved in the Store. So the user is able to choose whether he wants to set
them in the Session, Service, or Request level.

### GZIP_ENCODING_ENABLED : Boolean

Requestor supports compressing both outgoing request and incoming response payloads.

Decoding a gzipped response body is automatic. Requestor checks the 'Content-Encoding' header and if it's set to gzip,
then the paylaod is uncompressed using gzip.

In order to compress the outgoing payloads, we need to set the `GZIP_ENCODING_ENABLED` flag to `true`. It can be done in the
Session, in a Service or in each Request. When gzip encoding is enabled, Requestor will set the headers 'Content-Encoding'
and 'Accept-Encoding' to 'gzip' and compress the request payload using gzip while sending it through the network. Also,
notice that since we don't know the byte length of the payload previously, the request will be sent in chunked stream mode.

See examples of setting the GZIP_ENCODING_ENABLED flag to true:
```java
// Setting GZIP_ENCODING_ENABLED in the Session level
session.save(Requestor.GZIP_ENCODING_ENABLED, Boolean.TRUE);

// Setting GZIP_ENCODING_ENABLED in the Service level
service.save(Requestor.GZIP_ENCODING_ENABLED, Boolean.TRUE);

// Setting GZIP_ENCODING_ENABLED in the Request level
session.req("endpoint")
        .save(Requestor.GZIP_ENCODING_ENABLED, Boolean.TRUE)
        .payload(object)
        .post()
```

### DEFAULT_CONTENT_TYPE : String

When the requests have no content type set, then Requestor will query for it in the store.

By default, the DEFAULT_CONTENT_TYPE is `"text/plain"`.

In case we want a different one, we can save it in the Store like below:
```java
// Setting DEFAULT_CONTENT_TYPE in the Session level
session.save(Requestor.DEFAULT_CONTENT_TYPE, "application/json");

// Setting DEFAULT_CONTENT_TYPE in the Service level
service.save(Requestor.DEFAULT_CONTENT_TYPE, "application/json");

// Setting DEFAULT_CONTENT_TYPE in the Request level
session.req("endpoint")
        .save(Requestor.DEFAULT_CONTENT_TYPE, "application/json")
        .payload(object)
        .post()
```

### INPUT_BUFFER_SIZE / OUTPUT_BUFFER_SIZE : Integer

Requestor reads the responses contents using a byte buffer in order to increase the performance and avoid memory issues.
The same goes for writing requests contents to the network stream.

The default INPUT_BUFFER_SIZE and OUTPUT_BUFFER_SIZE is `8192`.

In case we want a different one, we can save it in the Store like below:

```java
// Setting INPUT_BUFFER_SIZE in the Session level
session.save(Requestor.INPUT_BUFFER_SIZE, 16 * 1024);

// Setting INPUT_BUFFER_SIZE in the Service level
service.save(Requestor.INPUT_BUFFER_SIZE, 16 * 1024);

// Setting OUTPUT_BUFFER_SIZE in the Request level
session.req("endpoint")
        .save(Requestor.OUTPUT_BUFFER_SIZE, 16 * 1024)
        .payload(object)
        .post()
```

### READ_CHUNKING_ENABLED / WRITE_CHUNKING_ENABLED : Boolean

Requestor fires read/write progress events each time a byte chunk is received/sent from/to the network.
But these events don't carry the byte chunk by default to save memory. If we want to access those chunks,
we need to set READ_CHUNKING_ENABLED / WRITE_CHUNKING_ENABLED flags to `true` in the Store.
It's recommended to do it mostly in Request level though. We can set it also in a Service intended to do
HTTP Streaming only.


```java
// Setting READ_CHUNKING_ENABLED in the Service level
service.save(Requestor.READ_CHUNKING_ENABLED, Boolean.TRUE);

// Setting READ_CHUNKING_ENABLED in the Request level
session.req("endpoint")
        .save(Requestor.READ_CHUNKING_ENABLED, Boolean.TRUE)
        .get()
        .onRead(progress -> stream(progress.getChunk().asBytes()))
```

### CHUNKED_STREAMING_MODE_DISABLED : Boolean

When the request payload content length is not know in advance, Requestor will automatically
stream the content according to the OUTPUT_BUFFER_SIZE. But it may happen that some servers
don't support this kind of streaming. In this case we need to set CHUNKED_STREAMING_MODE_DISABLED
flag to `true` to force sending the request all at once after we finish reading the input stream.

```java
// Open a InputStream which we don't know the total length in advance
InputStream inputStream = getInputStream();

// Setting CHUNKED_STREAMING_MODE_DISABLED in the Request level
session.req("endpoint")
        .save(Requestor.CHUNKED_STREAMING_MODE_DISABLED, Boolean.TRUE)
        .payload(inputStream)
        .post() // Post this inputStream forcing it to be totally read before sending

// Requestor will take care of closing the inputStream after reading it
```

### FOLLOW_REDIRECTS_DISABLED : Boolean

By default, Requestor will automatically follow the redirections when receiving responses of the 3xx status family.
If we want to disable such behavior, then we need to set the flag `FOLLOW_REDIRECTS_DISABLED` to `true`.
We can do it in the Session, in a Service or in a single Request.

```java
// Setting FOLLOW_REDIRECTS_DISABLED in the Session level
// Every request done form this session will not follow the redirections
session.save(Requestor.FOLLOW_REDIRECTS_DISABLED, Boolean.TRUE);
        
// Setting FOLLOW_REDIRECTS_DISABLED in the Request level
// Only this specific request will not follow the redicertions
session.req("endpoint")
        .save(Requestor.FOLLOW_REDIRECTS_DISABLED, Boolean.TRUE)
        .get() // Make a GET request not redirecting automatically
```


## Logging

Requestor leverages a customizable request logger in each Session.
By default, every request caption (method + uri) is logged in FINE level.
If you need to customize the logging behavior, just call `session.getLogger()`.

```java
// Set the log level
session.getLogger().setLevel(Level.INFO);

// Set the parts you want to be logged besides the caption
session.getLogger().setParts(
        RequestLogger.Part.OPTIONS,
        RequestLogger.Part.HEADERS,
        RequestLogger.Part.PAYLOAD
);

// Disable logging
session.getLogger().setActive(false);
```

## Requesting Fluent API

The Fluent API was designed to provide an enjoyable coding experience while requesting through a chainable interface. Here is how it works:
1. The client `Session` is the starting point when requesting.
2. It exposes the `req(<uri>)` method that returns a `RequestInvoker`, which has request building and invoking capabilities.
3. `RequestInvoker` implements the chainable `RequestBuilder` interface, which allows us to set the request options.
4. Further, `RequestInvoker` implements the `Invoker` interface, which allows us to send the request by calling one of the HTTP Methods.
5. When invoking the request, we also need to specify the class type we expect as the response payload.
6. The request invoking methods return a `Request<T>` according to the expected type we specified.
7. The `Request<T>` interface enables callback chaining so that we can handle different results neatly.

In summary, these are the three requesting steps:
1. Build the request
2. Invoke an HTTP method
3. Chain callbacks

```java
//========================================
// 1. Build the request
//========================================

RequestInvoker req = session.req("/api/books")
        .timeout(10_000) // Set the request timeout in milliseconds
        .delay(2000) // Set the request delay in milliseconds (wait 2s before sending the request)
        .contentType("aplication/json") // Set the Content-Type header
        .accept("text/plain") // Set the Accept header
        .header("Accept-Encoding", "gzip") // Set a custom header 
        .auth(new BasicAuth("username", "password")) // Set the authentication (more on this later)
        .payload(book); // Set the payload to be serialized in the request body


//========================================
// 2. Invoke an HTTP method
//========================================

// Send a POST request expecting an Integer as response
Request<Integer> postReq = req.post(Integer.class);

// Send a GET request expecting a Set of Books as response
Request<Collection<Book>> getReq = req.get(Set.class, Book.class);

// Send a DELETE request ignoring the response body
Request<Void> delReq = req.delete();

// PUT, PATCH, HEAD and OPTIONS are also available but were omitted for didactic purposes


//========================================
// 3. Chain callbacks
//======================================== 

// You can chain single method callbacks (functional interfaces) to handle success, failure or both: 
postReq.onSuccess(payload -> showSuccess(payload)) // Response was 2xx and body was deserialized as Integer
       .onFail(response -> showError(response.getStatus())) // Response was unsuccessful (status ≠ 2xx)
       .onError(exception -> log(exception)); // Request was not performed

// If you requested a collection of objects, you can retrieve the deserialized payload as well:
getReq.onSuccess(books -> renderTable(books)); // Response was deserialized into a Set of Book objects

// If you want to access the Response in success, declare an additional argument in the callback
delReq.onSuccess((payload, response) -> { // Response returned 2xx
    HttpStatus status = response.getStatus(); // Get the response status
    Headers headers = response.getHeaders(); // Get all headers so you can iterate on them
    String hostHeader = response.getHeader("Host"); // Get the Host header
    String contentType = response.getContentType(); // Get the Content-Type header
    Link undoLink = response.getLink("undo"); // Get the 'undo' relation of the Link header
});

// You can even deal with specific responses status codes and timeout events
delReq.onStatus(400, response -> alert("Response was 400: " + response.getStatus().getReasonPhrase()))
      .onStatus(429, response -> alert("Response was 429: " + response.getStatus().getReasonPhrase()))
      .onStatus(500, response -> alert("Response was 500: " + response.getStatus().getReasonPhrase()))
      .onTimeout(e -> alert("Request timed out in " + e.getTimeoutMillis() / 1000 + "s."));
```

### **❤️ Write beautiful code**
Joining the three parts together you can write a clean code like below.

```java
// Post a book to the server and retrieve the created entity
session.req("/api/books")
       .payload(book)
       .post(Book.class)
       .onSuccess(book -> view.render(book))
       .onFail(Notifications::showError);
```


## Resources

### [Showcase](https://reinert.github.io/requestor/latest/examples/showcase)
A GWT web app demonstrating the usage of some features of Requestor. The source code is in the [examples](https://github.com/reinert/requestor/tree/master/examples/requestor-showcase) directory.

### [Javadocs](https://reinert.github.io/requestor/latest/javadoc/apidocs/index.html)
Requestor API documentation in Javadoc format.

### [Community Channel](https://gitter.im/requestor-project/community)
A place to interact with the projects users and contributors. Get quick answers for your questions.


## Snapshot installation
If you want to use the latest snapshot, you need to add the sonatype snapshot repository to your POM and set the dependency version to `1.5.0-SNAPSHOT`.

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
    <artifactId>requestor-javanet</artifactId>
    <version>1.5.0-SNAPSHOT</version>
  </dependency>
  ...
</dependencies>
```


### Latest Snapshot
1.5.0-SNAPSHOT


## License
Requestor is freely distributable under the [Apache 2.0 License](https://www.apache.org/licenses/LICENSE-2.0.html)
