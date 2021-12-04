# Requestor

**Request like a boss.** 😎

*Ask more. Do less. Keep track of everything.*

Requestor is a powerful HTTP Client API for cutting-edge Java/GWT client apps. It offers plenty of
carefully designed features that enable developers to rule the network communication process smoothly:

- **Requesting Fluent API** - code as you think, read as you code.
- **Event-Driven Callbacks** - chain callbacks for different results and statuses.
- **Serialization** - serialize and deserialize payloads integrating any library.
- **Authentication** - make complex async authentication procedures in a breeze.
- **Middlewares** - asynchronously filter and intercept requests and responses.
- **HTTP Polling** - make long or short polling with a single command.
- **Retry** - define a retry policy with a single command.
- **Session** - set default options to all requests.
- **Store** - save and retrieve data both in session and request scope.
- **Services** - break down the API consumption into smaller independent contexts.
- **Links** - navigate through an API interacting with its links (HATEOAS for real).
- **Headers** - directly create and parse complex headers.
- **URIs** - build and parse complicated URIs easily.
- **Binary Data** - upload and download files tracking the progress.

It is compatible with GWT2 and Java 5+. Implementations for JVM/Android and J2CL are in the roadmap without
breaking API compatibility.

Requestor is an **HTTP Client API** intended to provide several features related to network communication.
Its scope is broader than popular (and often misunderstood) REST patterns. Requestor precisely models each entity in the
HTTP client-side context to enable its users to handle any requirement in this boundary. It values good **code readability
and maintainability** for the user by providing carefully designed interfaces and abstractions that others can extend and
add their logic with **low or zero integration effort**. Workarounds and hacks are not welcome here. Developers should be able
to implement their requirements keeping **high cohesion** through all their codebase.

Additionally, Requestor was crafted from the Client perspective instead of the Server's (like other rest libraries were thought).
In that fashion, developers have a more **consistent and intuitive experience** consuming HTTP services while coding. We do not
need to pre-declare Server API's façades. We can just consume them on demand. This approach empower us to build *micro clients*
that interact with many different *micro services*.

Besides, we value **code traceability**. So code generation is the last option in design decisions. Whenever a new requirement appears,
we strive to develop a good design solution that allows the user to write less code and achieve the desired results. If something proves
to be inevitably repetitive on the user side, after reaching the best possible design, then code generation is used to save the user
from repetitive work. Still, leveraging Requestor's components, people will probably automate most of their work using fundamental
object-oriented techniques like inheritance and composition. This way, they will better comprehend what is going on and have complete
control of the coding flow.

Requestor was inspired by successful HTTP Client APIs in other ecosystems like Python Requests, Angular HttpClient, Ruby Http.rb, and JAX-RS Client.

With Requestor, we can:

- Quickly make offhand requests writing as little code as possible.
- Communicate with different HTTP APIs keeping the same client communication pattern, thus improving the codebase maintainability.
- Handle multiple media types (JSON and XML, for instance) for the same java type without hacks.
- Deserialize different types according to the response status, properly modeling error messages in our app.
- Navigate through discoverable REST API links, fully leveraging HATEOAS.
- Build different and complex queries on demand, not having to map each possible iteration with Server APIs previously.
- Add new logic requirements not needing to change existing classes, instead of creating new small units, avoiding code conflict between co-workers.


<script>
  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
  })(window,document,'script','//www.google-analytics.com/analytics.js','ga');

  ga('create', 'UA-59721128-1', 'auto');
  ga('send', 'pageview');
</script>
