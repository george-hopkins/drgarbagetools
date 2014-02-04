---
layout:	page
title:	"Visualizing classes as they appear in a running JVM"
date:	2013-12-12 14:55
categories: bytecode-visualizer
---

**Note: This feature is experimental!**

In addition to displaying class files from file system and JARs (which is the
default option), [Bytecode Visualizer][bv] is also able to visualize classes as
they are loaded in a running Java Virtual Machine (JVM).

This preference may be set under

```Window > Preferences > Dr. Garbage > Bytecode Visualizer > General```

![preferences screenshot]({{ site.imgurl }}{{ page.url }}preferences-general.png)

This may be important in situations when a class loaded in a running JVM
differs from its file system counterpart. There are several motivations why
this may be done: 

* Code optimization
* Logging and tracing
* Profiling (performance analysis)
* Obfuscation

And there are several ways how this can be accomplished:

* `-Xrun<module>[:<options>]` This option dynamically loads a library module
* Custom class loader
* [JVMTI][jvmti] or the older [JVMPI][jvmpi]

No matter how and why a class was changed after its compilation, Bytecode
Visualizer is able to display its actual appearance within a running JVM. To
achieve this, Bytecode Visualizer uses [Java Debug Interface (JDI)][jdi]. 

In the following, some limitations of JDI are addressed.




JDI vs. filesystem
------------------

* Some information cannot be accessed over JDI: Attributes, Exceptions in
  signatures
* Retrieving a class over JDI is only possible, when the given class has
  already been loaded into the JVM. If you are trying to view a class which has
  not been loaded yet, Bytecode Visualizer falls back to retrieving it from the
  file system. From where the class has actually been retrieved can be seen in
  the header of Bytecode Visualizer. The same holds for inner types: Only those
  ones can be listed which were already loaded. 
* Eclipse does not reopen the viewer if there is one already showing the given
  class. If the code of the given class has been changed inbetween, you will
  not see the actual state.




Capabilities of distinct JDI and JVM versions
---------------------------------------------

JDI capabilities of Bytecode Visualizer can be used when you run Eclipse with
Java 5 or Java 6. Dr. Garbage strongly recommends using Java 6.  If you are -
for some reason - forced to run Bytecode Visualizer with Java 5, you will have
to do without some information: 

* Class file format version
* Constant pool




Other limitations
-----------------

Bytecode Visualizer is currently not able to display classes which do not have
any counterpart in the build path of an Eclipse project. This holds e.g. for
synthetic classes which are completely generated at runtime. We are working on
a fix. Betatesters with real world class-generation scenarios are welcome -
please [contact us][contact].

The current release includes the export funtion for synthetic classes. For more
information see the topic: [Debugging classes generated at run time][rcfjvm]

[bv]: {{ site.url }}/bytecode-visualizer/
[jvmti]: http://java.sun.com/j2se/1.5.0/docs/guide/jvmti/
[jvmpi]: http://java.sun.com/j2se/1.5.0/docs/guide/jvmpi/
[jdi]: http://java.sun.com/j2se/1.5.0/docs/guide/jpda/architecture.html
[contact]: {{ site.url }}/contact/
[rcfjvm]: {{ site.url }}/bytecode-visualizer/read-class-from-jvm/
