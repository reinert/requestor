# Requestor [![Build Status](https://travis-ci.org/reinert/requestor.svg?branch=master)](https://travis-ci.org/reinert/requestor) [![Gitter](https://img.shields.io/badge/Gitter-Join%20Chat-blue.svg?style=flat)](https://gitter.im/reinert/requestor)

A powerful HTTP Client API for bullet proof GWT SPAs.

Requestor offers a plenty of carefully designed features that enables you to manage the HTTP communication process through all your application:
* **Requesting Fluent API** *(makes you feel so good! 😌)*
* **Promises** (integrate any promise library)
* **Serialization** (integrate any serialization library)
* **Authentication** (make complex auths smoothly)
* **Request and Response Hooking** (Filters and Interceptors)
* **Session** (set default parameters to all requests)
* **Data Store** (store aux data both in session and request scope)
* **Links API** (HATEOAS for real connoisseurs)
* **Headers API** (build and parse complex headers easily)
* **URI API** (build and parse complex URIs easily)
* **Binary Data** upload and download
* **Form Data** handling

It supports GWT 2.9 and Java 8+ while maintains backwards compatibility with GWT 2.7 and Java 1.5.
GWT3 and J2CL support is in the roadmap.


## Preview

Make a GET request and deserialize the response body as String:

```java
Requestor r = Requestor.newInstance();
r.get("http://httpbin.org/ip", String.class).success( Window::alert );
```

Make a POST request sending a serialized a object in the body:

```java
Book book = new Book("Clean Code", "Robert C. Martin", new Date(1217552400000L));
r.post("/api/books", book).success( () -> showSuccessMsg() ).fail( () -> showErrorMsg() );
```

GET collections of objects:

```java
r.get("/api/books", Book.class, List.class).success( books -> renderTable(books) );
```

The above examples are aliases in Requestor class to make quick requests.
Additionally, you can access a fluent API to build and send more complex requests.

### Requesting Fluent API

Requesting can be better understood in three parts:
1) Access the request builder by calling `Requestor#req(String uri)`, and **start building your request**.
2) Then, you get access to the `RequestInvoker` interface, which allows you to **invoke any HTTP method**.
   Here, you also define what type you expect to receive in the response body.
3) Finally, by calling any HTTP invoke method, you receive a Promise instance, which allows you to **chain callbacks** nicely.   

```java
//========================================
// 1. Build your request
//========================================

RequestInvoker req = r.req("/api/books")
        .timeout(10000) // Set the request timeout in milliseconds
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
Promise<Integer> postReq = req.post(Integer.class);

// Send a GET request expecting a Set of Books as response
Promise<Collection<Book>> getReq = req.get(Book.class, Set.class);

// Send a DELETE request ignoring the response body
Promise<Void> delReq = req.delete();

// PUT, PATCH, HEAD and OPTIONS are also available but were omitted for didactic purposes

        
//========================================
// 3. Handle promise results
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
    // Store and access any object by key in session or request scope (more on this later)
    Store store = response.getStore();
});

// You can even deal with specific responses status codes and timeout events
delReq.status(400, response -> alert("Response was 400: " + response.getStatus().getReasonPhrase()))
      .status(429, response -> alert("Response was 429: " + response.getStatus().getReasonPhrase()))
      .status(500, response -> alert("Response was 500: " + response.getStatus().getReasonPhrase()))
      .timeout(e -> alert("Request timed out in " + e.getTimeoutMillis()/1000 + "s."));
```

#### **❤️ Write beautiful code**
Joining the three parts together you can write a clean code like below.

```java
// Post a book to the server and retrieve the created entity
r.req("/api/books")
    .payload(book);
    .post(Book.class)
    .success(book -> view.renderBook(book))
    .fail(Notifications::showError);
```

#### **💡 Set up your client session**
The Requestor object is a **SESSION** where in can configure default request parameters to avoid code repetition as also save and retrieve any data by key through the **Store**.

```java
Requestor r = Requestor.newInstance();
r.setTimeout(10000);
r.setContentType("aplication/json");
r.setAuth(new BasicAuth("username", "password"));

// Now all requests will have 10s timeout, 'application/json' Content-Type and the BasicAuth

r.post("/api/books", book);
...

// Clear the session if desired
r.reset();

// Now all requests will have the default parameters
r.post("/api/books", book);
...
```


### Looking for some REST?

Requestor offers a built-in REST service facade so you can perform CRUD operations around an Entity model.
Create a new `RestService` by calling `Requestor#newRestService(String uri, Class<E> entityClass, Class<I> idClass, Class<C> collectionClass)`.

```java
bookService = r.newRestService("/api/books", Book.class, Integer.class, List.class);

// Configure your service to always set Content-Type and Accept headers as 'application/json'
bookService.setMediaType("application/json");

// POST a book to '/api/books' and receive the created book from server
Book book = new Book("RESTful Web Services", "Leonard Richardson", new Date(1179795600000L));
bookService.post(book).success(createdBook -> renderBook(createdBook));

// GET all books from '/api/books'
bookService.get().success(books -> render(books));

// GET all books of author Richardson and year 2006 from '/api/books?author=Richardson&year=2006'
bookService.get("author", "Richardson", "year", 2006).success(books -> render(books));

// GET the book of ID 123 from '/api/books/123'
bookService.get(123).success(books -> render(books));

// PUT a book in the resource with ID 123 from '/api/books/123' and receive the updated book
bookService.put(123, book).success(updatedBook -> renderBook(updatedBook));

// DELETE the book of ID 123 from '/api/books/123' (returns void)
bookService.delete(123).success(() -> showSuccess('Book was deleted.'));
```

Although Requestor provides this ready-to-use REST facade, you may find more useful to extend `AbstractService` class (which RestService inherits) and implement your own service clients.
Don't worry, you'll write less code than existing code generation solutions while keeping full control of the request flow.

**AbstractService** gives you the advantage of little coding while allows you to have full control of requesting logic if you need to implement specific rules. Additionally, you are benefited with better testing capabilities and bug tracking.
See more details in [REST](#rest) section.


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

Requestor core is decoupled from the Promise API it exposes.

Thus, a requestor impl integrates the core to some promise library.

Currently, there's one implementation available: **requestor-gdeferred**. It binds requestor with [gdeferred](https://github.com/reinert/gdeferred) promise API.
Furthermore, an implementation binding requestor-core with [elemental2](https://github.com/google/elemental2) promise API is on the way.

### Latest Release
0.2.0 (18 Feb 2015)


## Yet another REST Client library for GWT?
*No. Not at all.* Requestor is an **HTTP Client API**, intended to provide several features related to HTTP communication. It's scope is broader than popular (and often misunderstood) REST patterns.
Requestor precisely models each entity in the HTTP client side context to enable its users to handle any requirement in this boundary.
It values good **code readability and maintainability** for the user by providing carefully designed interfaces and abstractions that others can extend and add its own logic with **low or zero integration effort**.
Workarounds and hacks are not welcome here. You should be able to implement your requirements keeping **high cohesion around all your application**.

Besides, we value **code traceability**. So code generation is the last option in design decisions.
Whenever a new issue arises, we strive to come up with a good design solution that allows the user to **write less code** and achieve the desired results.
If something proves to be inevitably repetitive on the user side, then code generation is used to save the user from repetitive work.

Requestor was inspired in many successful HTTP Client APIs in other ecosystems like Python Requests, Angular HttpClient, Ruby Http.rb and JAX-RS Client.

With Requestor, you can:
* Communicate with any HTTP API elegantly while writing as little code as possible.
* Deal with different server HTTP APIs keeping the same client communication pattern and facilitating the maintenance of your code base.
* Handle multiple media types (json and xml for instance) for the same java type without hacks.
* Deserialize different types according to the response status, allowing you to properly model error messages in you app.
* Navigate through discoverable REST API links, full leveraging HATEOAS.
* Build different and complex queries on demand, not having to map each possible iteration with Server APIs previously.
* Add new logic requirements not needing to change existing classes, but instead creating new small units, avoiding code conflict between co-workers.


## Promises

Requestor declares its own Promise contract which can be implemented by any Promise library easily.
Currently, there's one integration available: requestor-gdeferred.
Additionally, an integration with [elemental2](https://github.com/google/elemental2) promise API is in the roadmap.

A `Promise<T>` will deserialize the response body as `T` if it's successful (2xx). Check the available callbacks:
  * **success**( (T payload [, Response\<T\> r]) -> void )
    * executed when the response *is successful* (status = 2xx)
    * gives you access to the *deserialized payload* and the *response* (optional)
  * **fail**( (Response\<?\> r) -> void )
    * executed if the response *is unsuccessful* (status ≠ 2xx)
    * gives you access to the whole *response*
  * **load**( (Response\<?\> r) -> void )
    * executed if the response *is completed*, regardless of *success or failure*
    * gives you access both to the *response*
  * **status**(int statusCode, (Response\<?\> r) -> void )
    * executed when the response *returned the given status code*
    * gives you access to the *response* object
  * **progress**( (RequestProgress p) -> void )
    * executed many times while the request is being sent
    * allows you to track the *download progress*
  * **upProgress**( (RequestProgress p) -> void )
    * executed many times while the response is being received
    * allows you to track the *upload progress*
  * **timeout**( (TimeoutException e) -> void )
    * executed when a timeout occurs
    * gives you access to the request object
  * **abort**( (RequestException e) -> void )
    * executed if the request *could not be performed* due to any exception (even timeout)
    * gives you access to the original *exception*

Check how you can use them below:

```java

// You can chain single method callbacks (functional interfaces) to handle success, failure or both: 
requestor.get('/httpbin.org/ip', String.class).success(new PayloadCallback<String>() {
    public void execute(String ip) {
        // This is executed if the request was successful (status = 2xx)
        view.showIp(ip);
    }
}).success(new PayloadResponseCallback<String>() {
    public void execute(String ip, Response<String> r) {
        Window.alert("Response status was " + r.getStatus.toString());
    }
}).fail(new ResponseCallback<Object>() {
    public void execute(Response<Object> r) {
        // This is executed if the request was unsuccessful (status ≠ 2xx)
        view.showError("Request failed. Server message: " + r.getPayload().toString());
    }
}).load(new ResponseCallback<Object>() {
    public void execute(Response<Object> r) {
        // This is always executed, regardless of success or failure
        Window.alert("Response status was " + r.getStatus.toString());
    }
}).status(429, new ResponseCallback<Object>() {
    public void execute(Response<Object> r) {
        // This is executed if the response status code 429
        view.showError("Too many requests. Please try again in a few seconds.");
    }
}).status(StatusFamily.SERVER_ERROR, new ResponseCallback<Object>() {
    public void execute(Response<Object> r) {
        // This is executed if the response status code was 5xx (server error)
        view.showError("Request failed. Server message: " + r.getPayload().toString());
    }
}).progress(new ProgressCallback() {
    public void onProgress(RequestProgress progress) {
        // This is executed many times while the response is being received
        if (progress.isLengthComputable())
            view.setDownloadProgress( (progress.getLoaded() / progress.getTotal()) * 100 );
    }
}).upProgress(new ProgressCallback() {
    public void onProgress(RequestProgress progress) {
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
    public void onFail(RequestException e) {
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

When requesting any collection of objects, you will always receive a `Promise<Collection<T>>` despite the collection class you requested.
This is a design limitation of the Java language, which does not allow "generics of generics".
But you can pass a callback that extends from what is expected by the Promise.
See example below:

```java
// An ArrayList was requested, but the get method returned a Promise<Collection<Book>>
Promise<Collection<Book>> promise = requestor.req("/server/books").get(Book.class, ArrayList.class);

// Even though, you can pass a PayloadCallback<List<Book>> and it will be automatically typecasted
promise.success(new PayloadCallback<List<Book>>() {
    public void execute(List<Book> payload) {
        ...
    }
});
```

ℹ️ When using **lambda expressions**, if you want to access the interface than Collection, you need to explicitly declare the type in the signature. See:

```java
// An ArrayList was requested, but the get method returned a Promise<Collection<Book>>
Promise<Collection<Book>> promise = requestor.req("/server/books").get(Book.class, ArrayList.class);

// The payload parameter in callback is a Collection<Book>
promise.success( books -> books.get(0) ); // COMPILATION ERROR: books is Collection<Book> and .get belongs to List

// You can explicitly declare the type in lambda signature to typecast
promise.success( (List<Book> books) -> books.get(0) ); // OK: Now it works
```


## REST

As stated before, Requestor provides the [RestService](#looking-for-some-rest) to handle basic CRUD operations against a REST resource.
But you are likely to end up implementing your own abstract service class by extending the AbstractService with the peculiarities of your server API to avoid code repetition.

🧐 It's worth noting that ***AbstractService is a branch session derived from the main session*** (Requestor).
Then, the default parameters defined in the main session are also applied in your AbstractService.
Everything you set as a default in an AbstractService will only affect that service and will have preference over those defined in the main session.

### Extending AbstractService

Requestor has a design principle of favoring good code design to code generation.
All the pieces were well modeled, so you can easily extend the basic types and create you own client services easily.
Thus, to create a client service related to a server side REST resource, you can extend the `AbstractService` class and
implement your calls with little coding. See example below:

```java
public class BookService extends AbstractService {

    public BookService(Requestor requestor) {
        super(requestor, "/api/books"); // Provide the root path of the REST resource
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
       return request(uri).get(Book.class, List.class);
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

Now use your service:

```java
// It's a good practice to use Requestor object as a singleton since it's your main client session
Requestor requestor = getMyRequestor();

// Create your service passing the Requestor instance.
// The service then takes advantage of all the configurations present in the Requestor session
BookService bookService = new BookService(requestor);

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

### Create your own Abstract Service

👌 It's a **good practice** to handle the errors inside your service, so you don't have to always set fail callbacks.
Check the `handleErrors` below. It adds some predefined callbacks to promises:

```java
public class MyBookService extends AbstractService {

    MyPlaceManager myPlaceManager;
    
    // Construct you Service with any other object that will allow you to properly handle errors
    public MyBookService(Requestor requestor, MyPlaceManager myPlaceManager) {
        super(requestor, "/api/books");
        this.myPlaceManager = myPlaceManager;
    }

    public Promise<Book> createBook(Book book) {
        Uri uri = getUriBuilder().build();
        // add the error handling callbacks in the promise returned by the post method
        return handleErrors(request(uri).payload(book).post(Book.class));
    }
    
    // Implement all your service calls following the same pattern...

    private <T> Promise<T> handleErrors(Promise<T> promise) {
        return promise
                .status(404, response -> goToNotFound(response.getRequest().getUri()))
                .status(500, response -> goToServerError(response))
                .timeout(t -> showTimeoutMessage(t))
                .abort(e -> log(e));
    }

    // Implement the other methods...
}
```

**💡 PRO TIP**: Create your own **"AppAbstractService"** and handle the errors in the superclass to save you coding and maintenance cost.
Use the above example as inspiration as also the [RestService](https://github.com/reinert/requestor/blob/master/requestor/core/requestor-api/src/main/java/io/reinert/requestor/RestService.java) class.

## Request Processing
### Filter
### Serialize
#### Bypassing serialization with Payload object
### Intercept
### Authenticate

## Serialization
### Jackson
### AutoBeans
### Custom
#### JSON
#### XML
#### SubTypes

## Auth
### Basic
### Digest
### CORS
### OAuth2
### Token (Custom)

## Session

## Data Store
### Session Store
### Request Store

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
