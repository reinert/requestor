REQUESTOR CHANGELOG
==

0.1.0 - 06 Jan 2015 (Initial Release)
* Requestor Core API
    * RequestBuilder
        * contentType
        * accept
        * header
        * timeout
        * payload
        * responseType
    * Request
        * SerializedRequest (Payload)
        * RequestOrder (final form)
    * Response
        * SerializedResponse (Payload)
    * Payload
        * String
        * JavaScriptObject
    * HttpMethod
        * GET
        * POST
        * PUT
        * DELETE
        * PATCH
        * HEAD 
        * OPTIONS
    * ResponseType
        * Default ("")
        * ArrayBuffer
        * Blob
        * Document (XML)
        * Json
        * Text
    * Requestor
        * Default MediaType
        * Serdes (Serializer and Deserializer)
        * Filters (Request and Response)
        * Interceptors (Request and Response)
        * Provider (and Instance)
        * req (request building)
        * RequestorInitializer
            * Clean
            * Json
        * GeneratedJsonSerdes
    * Filters
        * FilterManager
        * FilterEngine
        * Request and Response Filter Contexts
    * Interceptors
        * InterceptorManager
        * InterceptorEngine
        * Request and Response Interceptor Contexts
    * Authentication
        * Asynchronous (by RequestOrder)
        * BASIC
        * CORS
    * URI
        * UriBuilder
        * MultivaluedParamComposition
    * Serialization
        * Json
            * Basic Types
            * Overlay Types
            * Abstract Object Serdes
                * JsonRecordReader
                * JsonRecordWriter
        * Misc
            * Void
            * Text
        * Serializer
        * SerializationContext
            * HttpSerializationContext
        * Deserializer
        * DeserializationContext
            * HttpDeserializationContext
        * Serdes
    * Header
        * SimpleHeader
        * SimpleHeaderWithParameter
        * QualityFactorHeader
        * MultivaluedHeader
        * AcceptHeader
        * ContentTypeHeader
    * Form
        * FormData
        * FormDataSerializer
            * Native (based on browser's FormData)
            * UrlEncoded
    * Deferred (Promise)
        * Promise
        * Deferred
        * Callback
    * RequestDispatcher
        * XMLHttpRequest extension with support to progress event
            * ProgressEvent
        * com.google.gwt.http.client.Request copy exposing some members
        * RequestProgress
    * AutoBean Framework
        * JsonAutoBeanGenerator (@Json)
* Requestor Core Annotations
    * @Json
* Requestor Impl GDeferred
    * RequestPromise (exposes only GDeferred API)
        * done
        * fail
        * always
        * progress
        * upProgress
        * then
* Requestor Ext Gwt-Jackson
    * JsonGwtJacksonGenerator (@Json)
* Requestor Ext TurboGWT
    * TurboOverlaySerdes (uses JsArrayList)
    * Switch LightMap implementation