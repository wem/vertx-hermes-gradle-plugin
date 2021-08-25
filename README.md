# vertx-hermes-gradle-plugin

This is a Kotlin code generation plugin around dtos, Vert.x message codec and communication specifications (Swagger / OpenAPI like event bus "connections").

https://plugins.gradle.org/plugin/ch.sourcemotion.gradle.vertx.hermes

## Installation

**Groovy**
```groovy
plugins {
  id "ch.sourcemotion.gradle.vertx.hermes" version "[VERSION]"
}
```

**Kotlin**
```kotlin
plugins {
    id("ch.sourcemotion.gradle.vertx.hermes") version "[VERSION]"
}
```

## Configuration

### Extension

There is on extension to configure all aspects of the plugin

```kotlin
hermes {
    
}
```

#### properties / configuration

There is one property: sourceSet. Default, the first `main` source set will be used. Any generated code will get added to
this source set.

### Dto generation

The dto generator is based on [Kotlin JSON schema codegen](https://github.com/pwall567/json-kotlin-schema-codegen). Please visit
that page for informations about limitations etc.

```kotlin
hermes {
    dto {
        ...
    }
}
```

#### properties

Name | Description | Default
------------ | -------------
outputDir | Where the sources go | `$buildDir/generated/sources/hermes/kotlin/$sourceSet` 
inputDir | Dto JSON schema source | `$projectDir/src/$sourceSet/hermes/dto`
enable | Enable the task | `true`
packageName | Package of the generated dtos (mandatory) | `null` 

#### Task
The plugin will register a task called `generateDto`

### Vert.x Message codec generation

Generates Vert.x message codec. If you combine this task with `generateDto`, message codec for the generated dtos
are directly generated

```kotlin
hermes {
    codec {
        ...
    }
}
```

#### properties

Name | Description | Default
------------ | -------------
outputDir | Where the sources go | `$buildDir/generated/sources/hermes/kotlin/$sourceSet`
classesInfo | Class info to generate message codecs | `empty`
enable | Enable the task | `true`
messageCodecsFileName | Generated file name | "HermesMessageCodecs"
messageCodecNameSupplier | Supplier of each codec name | `${simpleClassName}MessageCodec`
packageName | Package of the generated message codecs (mandatory) | `null` 

##### Class info

ch.sourcemotion.vertx.gradle.hermes.generate.DtoClassInfo - Consists of package and simple class name

#### Task
The plugin will register a task called `generateMessageCodec`

### Vert.x Communication

Generates Vert.x event bus consumers and senders, based on a JSON schema. The sense behind this approach is a description
of communication participants / interfaces like Swagger / OpenAPI but for Vert.x. So are not forced to copy & paste some source code between
projects and nevertheless you get a clear communication (interface) description

```kotlin
hermes {
    communication {
        ...
    }
}
```

#### properties

Name | Description | Default
------------ | -------------
inputDir | Dto JSON schema sources | `$projectDir/src/$sourceSet/hermes/communication`
outputDir | Where the sources go | `$buildDir/generated/sources/hermes/kotlin/$sourceSet`
enable | Enable the task | `true`
generateSender | Sender generated | `true`
generateConsumer | Sender generated | `true`
packageName | Package of the generated message codecs (mandatory) | `null` 

##### JSON schema

On sendMessageType / replyMessageType nullable types are supported like `kotlin.String?`

```json
{
  "$schema": "http://json-schema.org/draft/2019-09/schema",
  "$id": "https://sourcemotion.ch.vertx.hermes/communication",
  "type": "object",
  "properties": {
    "communications": {
      "types": "array",
      "items": {
        "$ref": "#/$defs/communication"
      }
    }
  },
  "$defs": {
    "communication": {
      "type": "object",
      "required": [
        "name",
        "address"
      ],
      "properties": {
        "name": {
          "type": "string",
          "description": "Name of the communication."
        },
        "address": {
          "type": "string",
          "description": "The Vert.x Eventbus address of the communication."
        },
        "sendType": {
          "type": "string",
          "description": "Either send, publish or request. If not defined, send will be used."
        },
        "sendMessageType": {
          "type": "string",
          "description": "Full qualified class name of the dto sent to the consumer. If not defined kotlin.Unit will used."
        },
        "replyMessageType": {
          "type": "string",
          "description": "Full qualified class name of the dto replied from the consumer. If not defined kotlin.Unit will used."
        },
        "localOnly": {
          "type": "boolean",
          "description": "Communication is local, means sent / published only within same Vert.x instance."
        }
      }
    }
  }
}
```

##### Generated sender
The output would be like:

```kotlin
public suspend fun EventBus.requestsomeName(message: Dto? = null): Message<Unit> =
    request<Unit>("some-address", message, deliveryOptionsOf(localOnly = false)).await()
```

##### Generated consumer
The output would be like:

```kotlin
public fun EventBus.someNameConsumer(block: Message<Dto?>.() -> Unit): Unit {
    consumer<Dto?>("some-address") {
        it.block()
        it.reply(null)
    }
}
```

#### Task
The plugin will register a task called `generateCommunication`