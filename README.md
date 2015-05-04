Requestor [![Build Status](https://travis-ci.org/reinert/requestor.svg?branch=master)](https://travis-ci.org/reinert/requestor) [![Gitter](https://img.shields.io/badge/Gitter-Join%20Chat-blue.svg?style=flat)](https://gitter.im/reinert/requestor)
==
A Modern HTTP Client API for GWT, Requestor offers all features needed for a current robust AJAX application.
It is pleasant to use and fully configurable and extensible.

## Preview
Requesting is now simple and nice.

```java
requestor.req("http://httpbin.org/ip").get(String.class).done(new DoneCallback<String>() {
  public void onDone(String result) {
      Window.alert(result);
  }
});
```

With Java 8 support, it will be really cool!

```java
requestor.req("http://httpbin.org/ip").get(String.class).done(Window::alert);
```

The low-level RequestBuilder way would be...

```java
/* Original GWT RequestBuilder approach */
RequestBuilder rb = new RequestBuilder(RequestBuilder.GET, "http://httpbin.org/ip");
rb.setCallback(new RequestCallback() {
    @Override
    public void onResponseReceived(Request request, Response response) {
        String result = response.getText();
        Window.alert(result);
    }

    @Override
    public void onError(Request request, Throwable exception) {
        // ...
    }
});
try {
    rb.send();
} catch (RequestException e) {
    // ...
}
```

Make a request is a trivial task and now you can do it healthily. :)  
Requesting will never be boring again!

## Features
* Promises (integrate any promise library)
* Fluent API
* Full support to Form requests (urlencoded or multipart)
* Full support to raw binary data (File, Blob, ArrayBuffer, Json, Document)
* Progress monitoring of uploads and downloads
* Uri Parsing
* Uri Building
* Basic and Digest authentication (nicely supporting CORS)
* OAuth2
* Extremely easy to implement any custom authentication
* Headers API
* Filters (enhance requests and responses)
* Interceptors (transform payloads)
* Auto JSON serialization for POJOs, JavaBean Interfaces and Overlay types
* Integrated to the AutoBean Framework
* Integrated to gwt-jackson
* Integrated to TurboGWT
* Handle multiple serialization of the same java type for different media types
* Customizable request dispatching (support for caching and synchronization)

## Browser Support
Almost all features are working perfectly in all modern browsers, except:
* Binary data manipulation is not supported by IE9- and Opera Mini
* CORS is poorly supported by IE9- and Opera Mini

## Examples
* [Showcase (Latest Release)](http://reinert.github.io/requestor/latest/examples/showcase)
* [Showcase (0.3.0-SNAPSHOT)](http://reinert.github.io/requestor/0.3.0-SNAPSHOT/examples/showcase)

## Documentation
* [Wiki](https://github.com/reinert/requestor/wiki)
* [Javadoc](http://reinert.github.io/requestor/latest/javadoc/apidocs/index.html)
* [Project Site (Latest Release)](https://reinert.github.io/requestor/latest)
* [Project Site (0.3.0-SNAPSHOT)](https://reinert.github.io/requestor/0.3.0-SNAPSHOT)

## Community
* [Discussion Group](https://groups.google.com/forum/#!forum/requestor)
* [Chat](https://gitter.im/reinert/requestor)

## Latest Release
0.2.0 (18 Feb 2015)

## Installation

The minimal installation requires two dependencies, the core API and some implementation.
The default implementation is requestor-gdeferred, which exposes GDeferred's Promise API.

```xml
<dependency>
    <groupId>io.reinert.gdeferred</groupId>
    <artifactId>gdeferred</artifactId>
    <version>0.9.0</version>
</dependency>
<dependency>
    <groupId>io.reinert.requestor.impl</groupId>
    <artifactId>requestor-gdeferred</artifactId>
    <version>0.2.0</version>
</dependency>
<dependency>
    <groupId>io.reinert.requestor.core</groupId>
    <artifactId>requestor-api</artifactId>
    <version>0.2.0</version>
</dependency>
```

To make requestor available to your GWT project, import the implementation's module.

```xml
<inherits name="io.reinert.requestor.RequestorByGDeferred"/>
```

Read the [Getting Started](https://github.com/reinert/requestor/wiki/Getting-Started) wiki page for more information.

#### Using SNAPSHOT versions

If you want to use the latest snapshot, you need to add the sonatype snapshot repository to your POM.

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
```

## License
Requestor is freely distributable under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0.html)

<script>
  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
  })(window,document,'script','//www.google-analytics.com/analytics.js','ga');

  ga('create', 'UA-59721128-1', 'auto');
  ga('send', 'pageview');

</script>
