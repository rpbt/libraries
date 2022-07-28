# repo-write
An HTTP server that allows writing files to rpbt repositories.

## Usage
Build the jar with `mvn package` in this directory. Copy the `target/repo-write-<version>.jar` file to your repository. Start the jar with `java -jar repo-write-<version>.jar` in your repository.

```
--help, -h     Show this help message.
--writeOnly    Set to make repo-write only allow PUT and DELETE requests.
--port <port>  Set the port to listen on.
--host <host>  Set the host to listen on.
```