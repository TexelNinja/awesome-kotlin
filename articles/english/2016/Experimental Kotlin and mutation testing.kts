
import link.kotlin.scripts.Article
import link.kotlin.scripts.LanguageCodes.EN
import link.kotlin.scripts.LinkType.article
import java.time.LocalDate

// language=Markdown
val body = """
[![](https://blog.frankel.ch/wp-content/resources/experimental-kotlin-mutation-testing/icon_Kotlin.png)](https://kotlinlang.org/)Since about a year and a half, I do a [lot](https://player.vimeo.com/video/105758362) [of](https://youtu.be/uC_8l69ArXs) [presentations](https://youtu.be/biLyXaJwO3c) on [Mutation Testing](https://en.wikipedia.org/wiki/Mutation_testing). In those, my point is to show that [Code Coverage](https://en.wikipedia.org/wiki/Code_coverage)‘s only benefit is that it’s easy to compute but that it’s meaningless – hence Mutation Testing.

Since some time, I’ve been interested in [Kotlin](http://kotlinlang.org), a language from JetBrains that runs on the JVM. It’s only natural that I wanted to check how Mutation Testing could be applied to the Kotlin language. As Kotlin is too young to have its own dedicated mutation testing tool, I used [Pit](http://pitest.org), a Java dedicated tool. I didn’t expected much, here are my findings.

I started with a [Kotlin-port](https://github.com/nfrankel/mutationtesting-kotlin) of my demo project. It has two simple classes and an associated test for each. In the first one, assertion is missing and in the second, the boundary condition of the < operator is not tested. This is the perfect use-case for Mutation Testing. It’s a Maven project, so the command is quite straightforward:

```bash
mvn org.pitest:pitest-maven:mutationCoverage
```

Interestingly enough, this works perfectly well, in terms of mutation coverage execution, but also regarding referencing the lines that are the source of the problem.

![](https://blog.frankel.ch/wp-content/resources/experimental-kotlin-mutation-testing/duplicatemath.png)
![](https://blog.frankel.ch/wp-content/resources/experimental-kotlin-mutation-testing/lowpredicate.png)

I wanted to go further, to use a _real_ project. The Kotlin folks were kind enough to redirect me to [KTor](https://github.com/Kotlin/ktor), a Kotlin-based web framework. I tried the same command, but limited on a single module – ktor-features/ktor-server-sessions (I have no clue what it does, that is not relevant anyway). Aye, there’s the rub.

First, Pit cannot correctly parse the generated bytecode at some places:

```
PIT >> WARNING : Found more than one mutation similar on same line in a finally block. Can't correct for inlining.
```

Worse, there are a lot of timeout errors. Since there’s some threading involved, that’s not really a blocker.

```
PIT >> WARNING : Slave exited abnormally due to TIMED_OUT
```

For this project, reports are really long, but some errors are really similar to what you’d expect from Java code, for example:

```
removed call to kotlin/jvm/internal/Intrinsics::checkParameterIsNotNull → NO_COVERAGE
```

Here’s a sample of the report:

![](https://blog.frankel.ch/wp-content/resources/experimental-kotlin-mutation-testing/pullablelinkedlist.png)

Again, Pit is able to bind the real lines to the problems found. Isn’t life good? If you stop at this point, it probably is. But running Pit on another project – say, ktor-features/ktor-locations fails miserably.

```
The class org.jetbrains.ktor.locations.Locations${"$"}WhenMappings does not contain a source debug information. All classes must be compiled with source and line number debug information
```

It seems using the `when` construct in Kotlin generates an inner class that doesn’t contain debug information, which Pit need to works its magic. Let’s exclude the offending class and its inner mappings class:

```
mvn org.pitest:pitest-maven:mutationCoverage -DexcludedClasses=org.jetbrains.ktor.locations.Locations*
```

It works again and produces expected results:

![](https://blog.frankel.ch/wp-content/resources/experimental-kotlin-mutation-testing/conversionservice.png)

There are no mutation testing tools for Kotlin (yet), and considering Java ecosystem’s history, there might never be one. However, mutation testing is an invaluable tool to assert the real quality of your tests. Even if Pit is not a perfect match for Kotlin, it would be foolish to discard it.

"""

Article(
  title = "Experimental: Kotlin and mutation testing",
  url = "https://blog.frankel.ch/experimental-kotlin-mutation-testing",
  categories = listOf(
    "Testing",
    "Mutation Testing",
    "Kotlin"
  ),
  type = article,
  lang = EN,
  author = "Nicolas Frankel",
  date = LocalDate.of(2016, 4, 3),
  body = body
)
