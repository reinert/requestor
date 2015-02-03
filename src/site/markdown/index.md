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
* BASIC and DIGEST authentication (nicely supporting CORS)
* Extremely easy to implement any custom authentication
* Nice header manipulation
* Filters (enhance requests and responses)
* Interceptors (transform payloads)
* Auto JSON serialization for POJOs, JavaBean Interfaces and Overlay types
* Integrated to the AutoBean Framework
* Integrated to gwt-jackson
* Integrated to TurboGWT
* Handle multiple serialization of the same java type for different media types
* Customizable request dispatching (support for caching and synchronization)
