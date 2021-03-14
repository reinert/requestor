# Requestor

Requesting is the most common operation in AJAX applications.
GWT is an excellent platform for developing AJAX applications, but it still bother us when it comes to requesting.
Using RequestBuilder, the default GWT way of requesting, is a low-level approach which requires too many steps and manual processing to achieve meaningful data for our application.
Requestor solves theses issues with a full featured and fluent API for requesting.
A real HTTP Client API for GWT, it is made upon modern programming concepts like Promises and Method Chaining, and is designed to be as extensible as possible.
Carefully crafted, Requestor provides many features while allows the easy configuration and customization of them all.


# Features

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
* Handle multiple serialization of the same java type for different media types
* Customizable request dispatching (support for caching and synchronization)

<script>
  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
  })(window,document,'script','//www.google-analytics.com/analytics.js','ga');

  ga('create', 'UA-59721128-1', 'auto');
  ga('send', 'pageview');
</script>
