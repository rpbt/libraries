# rpbt-core
Core library used by other rpbt libraries.

*Not to be confused with the rpbt website which does the building of packs. That is arguably the core of rpbt.*
```xml
<dependency>
    <groupId>com.github.rpbt</groupId>
    <artifactId>rpbt-core</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```
If you are creating a plugin/mod and shading the library, make sure to relocate all rpbt classes to avoid collisions. See the root README.md.
## Usage
Create an rpbt repository from a url.
```java
HttpRepository repository = RpbtRepository.http("https://example.com/my-rpbt-repo/");
```
You can then resolve resources using `RpbtRepository#getResource(ResourceType type, String id, String version)`. This returns a `Resource` instance.

The zip file of the resource can be read using `RpbtRepository#readResource(Resource resource)`.

For HTTP repositories, you can get the url to the zip file using `HttpRepository#getUrl(Resource resource)`.