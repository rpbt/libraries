# rpbt libraries
Libraries and code for interacting with rpbt repositories.

- [rpbt-core](./rpbt-core/README.md) - Core library used by other rpbt libraries.
- [serve](./serve/README.md) - Send resource packs from rpbt repositories.

## Relocating
If you are creating a plugin/mod and shading the libraries, make sure to relocate all rpbt classes to avoid collisions.
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-shade-plugin</artifactId>
    <version>3.3.0</version>
    <executions>
        <execution>
            <phase>package</phase>
            <goals>
                <goal>shade</goal>
            </goals>
            <configuration>
                <relocations>
                    <relocation>
                        <pattern>com.github.rpbt</pattern>
                        <shadedPattern>com.example.myproject.libs.com.github.rpbt</shadedPattern>
                    </relocation>
                </relocations>
            </configuration>
        </execution>
    </executions>
</plugin>
```
replace `com.example.myproject` with your package.