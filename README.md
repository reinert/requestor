Requestor [![Build Status](https://travis-ci.org/reinert/requestor.svg?branch=master)](https://travis-ci.org/reinert/requestor)
==
A convenient API for managing GWT client-server communication and performing requests fluently.

## Highlights

* Handle the requests results with Promises!
* Fluent `GET`, `POST`, `PUT`, `DELETE` and `HEAD` requests
* Easy construction of target URI with UriBuilder
* [Customizable multi-valued param composition](#multiple-value-parameters)
* [Nice support to form params](#sending-form-data)
* [Native Basic Authentication support](#basic-authentication)
* Customizable timeout
* [Customizable callback execution based on server response](#customizable-callback-execution)
* [Always executed callbacks](#always-executed-callbacks)
* [Handy header construction and application](#easier-header-construction)
* [Request and Response filtering (enhancement)](#requestresponse-filters)
* [Customizable `ServerConnection` implementation](#extensible-design) (default directs to XMLHttpRequest)
* Automatic JSON parsing into Overlay types
* [Easy De/Serialization and support to different content-types](#json-xml-and-whatever-living-together) (by pattern matching)

## Quick Start

TurboG proposes a fluent way of making http requests. It fits better the REST style communication. 
Just look how simple you can **GET** a book from server:

```java
requestor.request("/books/1").get(Book.class).done(new DoneCallback<Book>() {
    public void onDone(Book book) {
        Window.alert("My book title: " + book.getTitle());
    }
});
```

Java 8 Lambda syntax
```java
requestor.request("/books/1").get(Book.class)
        .done(book -> Window.alert("My book title: " + book.getTitle()));
```

For JSON **serializing/deserializing** your POJO *you just need to annotate it with `@Json`* or create a custom SerDes:

```java 
public class BookJsonSerdes extends JsonObjectSerdes<Book> {

    @Override
    public Book readJson(JsonRecordReader reader, DeserializationContext context) {
        return new Book(reader.readInteger("id"),
                reader.readString("title"),
                reader.readString("author"));
    }

    @Override
    public void writeJson(Book book, JsonRecordWriter writer, SerializationContext context) {
        writer.writeInt("id", book.getId())
                .writeString("title", book.getTitle())
                .writeString("author", book.getAuthor());
    }
}
```

One configuration step: just remember to register your SerDes in the [Requestor](#requestor).
<br />
If you are using *Overlays*, then you don't need any SerDes, serialization/deserialization is automatic.

To **POST** an object, use the payload method:

```java 
final Book data = new Book(1, "RESTful Web Services", "Leonard Richardson");

requestor.request("/books").payload(data).post().done(new DoneCallback<Void>() {
    public void onDone(Void result) {
        Window.alert("POST done!");
    }
}).fail(new FailCallback<Throwable>() {
    public void onFail(Throwable throwable) {
        Window.alert("Failed!");
    }
});
```

### Accumulate your result array in a container

```java
requestor.request("/books").get(Book.class, List.class).done(new DoneCallback<Collection<Book>>() {
    public void onDone(Collection<Book> books) {
        List<Book> bookList = (List<Book>) books;
    }
});
```

When deserializing, the Deserializer retrieves an instance of the collection (container) from the ContainerFactoryManager, managed by the Requestor.

You can create custom Factories of Containers and register them in the Requestor.

### Primitive access the Response

```java
requestor.request("/books").get(Response.class).done(new DoneCallback<Response>() {
    public void onDone(Response response) {
        Window.alert("Response status is: " + response.getStatusCode());
    }
});
```

### Always executed callbacks
Add callbacks to be called when request has either succeeded or failed.
```java 
requestor.request(uri).get(Book.class).always(new AlwaysCallback<Book, Throwable, ResponseContext>() {
    public void onAlways(Promise.State state, Book book, Throwable throwable) {
        if (state == Promise.State.RESOLVED) {
            // Do something with book
        } else {
            // Do something with throwable
        }
    }
});
```

### Basic Authentication
Request supports setting user and password.

```java
requestor.request("/user/auth").user(username).password(pwd)...
```

### Sending FORM data
TurboG HTTP provides two handful classes for dealing with Forms: *FormParam* and *FormData* (a collection of FormParams with a nice builder). You can use both of them to make a form post.

```java
FormData formData = FormData.builder().put("name", "John Doe").put("array", 1, 2.5).build();

requestor.request(uri)
        .contentType("application/x-www-form-urlencoded")
        .post(formData); // We optionally set no callback, disregarding the server response
```
 
### Requestor
[Requestor](http://reinert.github.io/requestor/javadoc/apidocs/io/reinert/requestor/Requestor.html) is the main component of TurboG HTTP. It is responsible for managing the various aggregate components for the requests (as SerdesManager, FilterManager, ContainerFactoryManager) and create [Requests](http://reinert.github.io/requestor/javadoc/apidocs/io/reinert/requestor/Request.html) supporting those. It should be used as a singleton over all your application.

### JSON, XML and whatever living together
The [Serializer](http://reinert.github.io/requestor/javadoc/apidocs/io/reinert/requestor/serialization/Serializer.html)
 and [Deserializer](http://reinert.github.io/requestor/javadoc/apidocs/io/reinert/requestor/serialization/Deserializer.html)
 interfaces requires you to inform the *content-type patterns* they handle.
 After registering them at the Requestor, when requesting it will look for the most specific Serializer for serializing
 outgoing data and the most specific Deserializer for deserializing incoming data.

The tests shows an example (see [this test](https://github.com/reinert/requestor/blob/master/src/test/java/io/reinert/requestor/MultipleSerdesByClassTest.java) and [the SerDes](https://github.com/reinert/requestor/tree/master/src/test/java/io/reinert/requestor/books))
 of having both SerDes for XML and JSON related to the same type.

Notice [Request](http://reinert.github.io/requestor/javadoc/apidocs/io/reinert/requestor/Request.html) (returned by Requestor) enables you to specify the exact content-type you want to serialize your
 outgoing data (Request#content-type(String)) and the content-type you want to receive from the server
 (Request#accept(String) or Request#accept(AcceptHeader)). Both default values are "application/json".
 
An abstract SerDes implementation for JSON would be like:

```
public abstract class JsonSerdes<T> implements Serdes<T> {

    /**
     * Method for accessing type of the Object this de/serializer can handle.
     *
     * @return The class which this deserializer can de/serialize
     */
    abstract Class<T> handledType();

    /**
     * Tells the content-type patterns which this deserializer handles.
     *
     * @return The content-type patterns handled by this deserializer.
     */
    @Override
    public String[] accept() {
        return new String[] { "application/json", "application/javascript", "*/json", "*/json+*, "*/*+json" };
    }

    /**
     * Tells the content-type patterns which this serializer handles.
     *
     * @return The content-type patterns handled by this serializer.
     */
    @Override
    public String[] contentType() {
        return new String[] { "application/json", "application/javascript", "*/json", "*/json+*, "*/*+json" };
    }
    
    // ... (omitted)
}
    
```

### Multiple value parameters
There's a feature called [MultivaluedParamStrategy](http://reinert.github.io/requestor/javadoc/apidocs/io/reinert/requestor/MultivaluedParamStrategy.html) that defines the way params with more than one value should be composed
 when building a URL or a FormParam. There are two strategies provided: RepeatedParam and CommaSeparated. The former
 repeats the param name with each value - this is the default and most practiced strategy -, the latter puts only
 once the parameter name and join the values separated by comma.

### Request/Response filters
You can easily enhance all your requests with [RequestFilter](http://reinert.github.io/requestor/javadoc/apidocs/io/reinert/requestor/RequestFilter.html) and your responses with [ResponseFilter](http://reinert.github.io/requestor/javadoc/apidocs/io/reinert/requestor/ResponseFilter.html).
 Suppose you want to add a custom authentication header in all requests after the user successfully authenticated.
 Just register a RequestFilter in the Requestor that performs this operation.
 If latter you want do undo this registration, you can hold the HandlerRegistration instance returned at the time of
 registering and execute HandlerRegistration#removeHandler().

### Easier header construction
TurboG HTTP provides Header classes facilitating complex header construction.
 E.g., you can create a [QualityFactorHeader](http://reinert.github.io/requestor/javadoc/apidocs/io/reinert/requestor/header/QualityFactorHeader.html) and pass it to your request.

### Extensible design
All Requests are created by an underlying abstraction called [Server](http://reinert.github.io/requestor/javadoc/apidocs/io/reinert/requestor/Server.html). The Server is primarily responsible for providing new [ServerConnections](http://reinert.github.io/requestor/javadoc/apidocs/io/reinert/requestor/ServerConnection.html). The ServerConnection is responsible for performing the requests, by receiving all necessary parameters.

This design allows you to implement how you want to communicate with your Server over all your application.
Suppose you are creating a mobile application and want to prevent data loss by poor connection. You can create a new implementation of Server that caches the data in memory or stores it in phone's browser if no internet connection is available, and sync the data when the signal is back. Or you can implement a Server with specific configuration which would permit you to send and store particular kinds of data in the browser for accelerating the application's re-opening. [GWT Storage API](http://www.gwtproject.org/doc/latest/DevGuideHtml5Storage.html) might be very helpful in this case.

The default implementation of Server ([ServerImpl](https://github.com/reinert/requestor/blob/master/src/main/java/io/reinert/requestor/ServerImpl.java)) creates the [ServerConnectionImpl](https://github.com/reinert/requestor/blob/master/src/main/java/io/reinert/requestor/ServerConnectionImpl.java) (default implementation of ServerConnection), which performs the communication by directly creating a request using RequestBuilder and sending it. The binding is done via DefferedBinding. 

### Tests
Take a look at the [tests](https://github.com/reinert/requestor/tree/master/src/test/java/io/reinert/requestor) for more examples.

## Documentation
* [Javadocs](http://reinert.github.io/requestor/javadoc/apidocs/index.html)

## Community
* [Turbo GWT Google Group](http://groups.google.com/d/forum/turbogwt) - Share ideas and ask for help.

## Downloads
Turbo GWT HTTP is currently available at maven central.

### Maven
```
<dependency>
    <groupId>io.reinert.requestor</groupId>
    <artifactId>requestor</artifactId>
    <version>0.1.0</version>
</dependency>
```

## License
Turbo GWT HTTP is freely distributable under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0.html)
