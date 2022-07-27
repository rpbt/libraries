# serve
Send resource packs from rpbt repositories.

```xml
<dependency>
    <groupId>com.github.rpbt</groupId>
    <artifactId>serve</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```
If you are creating a plugin/mod and shading the library, make sure to relocate all rpbt classes to avoid collisions. See the root README.md.

Follow the instructions in `rpbt-core` for getting an HTTP repository.

Serve works with any API as it only provides a url and hash for you to use. The following instructions are for the Bukkit API.
## Usage with the Bukkit API (`paper-api`)
Serve may perform HTTP operations if the pack was not cached, it is therefore recommended to use the async helper `AsyncServe`. This prevents lag spikes.

Create an instance somewhere and re-use it, as it will cache resources.
```java
Plugin plugin = ...;
AsyncServe serve = new AsyncServe(
    run -> plugin.getServer().getScheduler().runTaskAsynchronously(plugin, run),
    run -> plugin.getServer().getScheduler().runTask(plugin, run)
);
```
If you want to run everything synchronously you can instead use `Serve serve = new Serve()` and `serve.send` instead of `serve.sendAsync`.
```java
Player player = ...;
serve.sendAsync("example", "1.0", repository, player::setResourcePack);
```
or to send with other parameters
```java
Player player = ...;
serve.sendAsync("example", "1.0", repository,
    (url, hash) ->
        player.setResourcePack(url, hash,
            true, // required
            Component.text("Please accept the resource pack!")
        )
);
```
You should also handle the potential errors, `ResourceNotFoundException` and `IOException`. For example by chaining the returned future with `.exceptionally`
```java
serve.sendAsync("example", "1.0", repository, player::setResourcePack)
    .exceptionally(exception -> {
        if (exception instanceof ResourceNotFoundException) {
            player.sendMessage("The resource pack was not found!");
        } else {
            player.sendMessage("Internal error");
        }
    });
```
If you are using the synchronous version you can simply wrap `serve.send` in a try-catch.
## Usage with the Bukkit API (`spigot-api`/`bukkit-api`)
Without Paper API, you need to use the hash as bytes.
```java
Player player = ...;
serve.sendAsync("example", "1.0", repository,
    (url, hash) -> {
        byte[] bytes = BaseEncoding.base16().lowerCase().decode(hash.toLowerCase());
        player.setResourcePack(url, bytes,
            "Please accept the resource pack!",
            true // required
        );
    }
);
```