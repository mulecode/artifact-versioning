# Simple Project versioning


### Setup

**Plugin configuration:**

build.gradle.kts

Minimum configuration:
```kotlin
versionConfig {
    versionIncrementer = "minor"
    tagSuffix = "RELEASE"
}
```

Full configuration:
```kotlin
versionConfig {
    versionIncrementer = "minor"
    tagSuffix = "RELEASE"
    initialVersion = "1.0.0"
    applyVersion = true
    tagLatest = false
}
```
If the property `applyVersion` is set to `false`, you might need to set 
the project version manually by retrieving generated version and 
applying to gradle version

```kotlin
val scm by tasks.registering(ScmVersion::class)

version = scm.get().nextVersion
```

or just:

```kotlin
version = file("build/versioning/next.txt").readText()
```

#### Configuration Variables

**versionIncrementer**

- Required: no
- Default: patch
- Values: [patch | minor | major | skip]
- Description: Tells the plugin how the semantic version should be updated.

```
- patch
Version when you make backwards compatible bug fixes

- minor 
Version when you add functionality in a backwards compatible manner

- patch
Version when you make incompatible API changes

- skip
Skip versioning plugin during ci pipeline
```

**tagSuffix**

- Required: no
- Default: SNAPSHOT
- Values: [SNAPSHOT | M | RC | RELEASE]
- Description: Adds version suffix to the artifact.

```
- SNAPSHOT
Snapshot is the early build of one or a set of features. 
considerable unstable for changing the binary for each build. 
is only recommended for early system integration.  

- M (MILESTONE)
Milestone versions include specific sets of functions and are released as 
soon as the functionality is complete.

- RC (RELEASE_CANDIDATE)
Is a beta version with potential to be a final product, 
which is ready to release unless significant bugs emerge.

- RELEASE
Is the final product of a set of features. Considerable stable and readu 
for production. Still a small possibility of bugs.
```

**initialVersion**

- Required: no
- Default: 1.0.0
- Description: Used to set up a new project.

**applyVersion**

- Required: no
- Type: Boolean
- Default: true
- Description: Apply the generated version to gradle project and sub-projects.

**tagLatest**

- Required: no
- Type: Boolean
- Default: true
- Description: Will tag `latest` in conjunction with the semantic version.

#### Overview and usage

**Setting up a new project:**

```kotlin
versionConfig {
    versionIncrementer = "minor"
    tagSuffix = "SNAPSHOT"
    initialVersion = "1.0.0"
}
```

Having the above configuration set, after executing the command:

```shell script
gradle clean scmVersion build
```

Output:
```shell script
Project successfully initialised
Tags Created:
- '1.0.0.BUILD-SNAPSHOT'

[ACTION] Push the tags to remote branch with command:
'scmVersionPush'
```
The command will create two tags:
- version: the current version of the commit.

For the second commit after the project already set.
The next configuration could be:

```kotlin
versionConfig {
    versionIncrementer = "minor"
    tagSuffix = "M"
}
```
the above configuration will output:
```shell script
1.0.0.M1
```

any others sub sequent commits preserving the `M` that 
stands for `MILESTONE`, the version sequencer will be increased:

e.g:
```shell script
1.0.0.M1 -> 1.0.0.M2 -> 1.0.0.M3
```

### Tag sequence example

```
1.0.0.BUILD-SNAPSHOT
->
1.0.0.M1
->
1.0.0.M2
->
1.0.0.RC1
->
1.0.0.RELEASE
```