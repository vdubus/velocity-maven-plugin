# README

This project is a simple export from original [google code project](https://code.google.com/p/velocity-maven-plugin/).

## Introduction

The plugin name is velocity-maven-plugin and there is a single goal velocity

## Example Addition to POM

```xml
<plugin>
        <groupId>com.github.vdubus</groupId>
        <artifactId>velocity-maven-plugin</artifactId>
        <version>1.1.2</version>
        <configuration>
                <templateFiles>
                        <directory>/src/main/resources</directory>
                        <includes>
                                <include>*.vm</include>
                        </includes>
                </templateFiles>
                <templateValues>
                        <test>foo</test>
                </templateValues>
        </configuration>
</plugin>
```

## Options

| Option Name     | Default                         | Notes                                                                                                                                                          |
|-----------------|---------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------|
| encoding        | ${project.build.sourceEncoding} | This option also has null check that sets the value to "UTF-8"                                                                                                 |
| outputDirectory | ${project.build.directory}      |                                                                                                                                                                |
| removeExtension | no default                      | Set this parameter if you want the plugin to remove an unwanted extension when saving result. For example foo.xml.vtl ==> foo.xml if removeExtension = '.vtl'. |
| templateFiles   | Required, no default.           | This is required, but a default may be added later                                                                                                             |
| templateValues  | Required, no default.           | This is the properties list you wish to have merged with your templates                                                                                        |

