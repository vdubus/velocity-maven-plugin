# README

## Introduction

The plugin name is velocity-maven-plugin and there is a single goal: `velocity`.

Original code is available on [google code project](https://code.google.com/p/velocity-maven-plugin/).

This version fix these issues:

* [ResourceManager unable to find resource in multi-module project](https://github.com/vdubus/velocity-maven-plugin/issues/8)
* [Update Velocity to version 1.7](https://github.com/vdubus/velocity-maven-plugin/issues/7)

## Example Addition to POM

```xml
<plugin>
	<groupId>com.github.vdubus</groupId>
	<artifactId>velocity-maven-plugin</artifactId>
	<version>1.1.2</version>
	<executions>
		<execution>
			<id>Generate source velocity</id>
			<phase>generate-sources</phase>
			<goals>
				<goal>velocity</goal>
			</goals>
			<configuration>
				<removeExtension>.vm</removeExtension>
				<templateFiles>
					<directory>src/main/resources</directory>
					<includes>
						<include>**/*.vm</include>
					</includes>
				</templateFiles>
				<templateValues>
					<test>testValue</test>
				</templateValues>
			</configuration>
		</execution>
	</executions>
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
