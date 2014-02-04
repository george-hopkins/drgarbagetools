---
layout:	tool
title:	"Class File Editor"
date:	2013-12-12 05:16
categories: bytecode-visualizer
desc:	"<strong>Class File Editor</strong> - one feature of the Dr.Garbage tools suite, can be used to"
---

* Open Java class file binaries
* Inspect their internal structure
* Edit the class file
* Save the changes back to the class file.

After compiling a Java source file (with the `.java` filename extention), the
Java Compiler generates a class file (with the `.class` filename extension),
which can be found in the `bin` folder of a Java project when you are in the
Navigator View of Eclipse.

`Window > Show View > Other... > General > Navigator`

![open with class file editor]({{ site.imgurl }}{{ page.url }}open-file.png)

An opened class file contains bytes of the class file in *hexadecimal format*
and green annotated comments in java similar syntax. These annotated comments
highlight the general sections of the class file. Every change that is made in
one line corresponds to the section that is shown on the right side of the
hexadecimal representation of the class file. From this view,it is clear to see
some basic sections of a Java Class File structure:


* [Magic](http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.1)
* [Version](http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.1)
* [Constant](http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.1)
* [Access](http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.1)
* [This](http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.1)
* [Super](http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.1)
* [Interfaces](http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.1)
* [Fields](http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.1)
* [Methods](http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.1)
* [Attributes](http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.1)
* ...

![]({{ site.imgurl }}{{ page.url }}magic-ver-const.png)

When start editing a class file, you will be asked if you want to edit the
derived class file. Click yes to proceed. 

![derived file popup]({{ site.imgurl }}{{ page.url }}derived-file.png)

After editing the class file. The changes that were made can be saved back to
the original class file. 
