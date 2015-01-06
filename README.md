Requestor [![Build Status](https://travis-ci.org/reinert/requestor.svg?branch=master)](https://travis-ci.org/reinert/requestor)
==
A Modern HTTP Client API for GWT, Requestor offers all features needed for a current robust AJAX application.
It is pleasant to use and fully configurable and extensible.

## Preview (Java8 syntax)

```java
requestor.req("http://httpbin.org/ip").get(String.class).done(r -> Window.alert(r));
```

## Features

* Promises (integrate any promise library)
* Fluent API
* Full support to Form requests (urlencoded or multipart)
* Full support to raw binary data (File, Blob, ArrayBuffer, Json, Document)
* Progress monitoring of uploads and downloads
* BASIC and CORS authentication
* Support for implementing any custom authentication
* Nice header manipulation
* Uri Building
* Filters (enhance requests and responses)
* Interceptors (transform payloads)
* Auto JSON serialization for POJOs, JavaBean Interfaces and Overlay types
* Integrated to the AutoBean Framework
* Integrated to gwt-jackson
* Integrated to TurboGWT
* Handle multiple serialization of the same java type for different media types
* Customizable request dispatching (support for caching and synchronization)

## Examples
* [Showcase](http://reinert.github.io/requestor/latest/examples/showcase)

## Documentation
* [Wiki](/reinert/requestor/wiki)
* [Javadoc](http://reinert.github.io/requestor/javadoc/apidocs/index.html)
* [Project Site](https://reinert.github.io/requestor/latest)

## Community
* [Discussion Group](https://groups.google.com/forum/#!forum/requestor)

## Current Release
0.1.0 (06 Jan 2015)

## Installation

The minimal installation requires two dependencies, the core API and some implementation.
Currently, there's only one implementation available, requestor-gdeferred, which exposes GDeferred's Promise API.

```xml
<dependency>
    <groupId>io.reinert.gdeferred</groupId>
    <artifactId>gdeferred</artifactId>
    <version>0.9.0</version>
</dependency>
<dependency>
    <groupId>io.reinert.requestor.impl</groupId>
    <artifactId>requestor-gdeferred</artifactId>
    <version>0.1.0</version>
</dependency>
<dependency>
    <groupId>io.reinert.requestor.core</groupId>
    <artifactId>requestor-api</artifactId>
    <version>0.1.0</version>
</dependency>
```

To make requestor available to your GWT project, import the implementation's module.

```xml
<inherits name="io.reinert.requestor.RequestorByGDeferred"/>
```

Read the [Getting Started](/reinert/requestor/wiki/Getting-Started) wiki page for more information.

## License
Requestor is freely distributable under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0.html)
