---
layout:	tool
title:	"Abstract Syntax Tree"
date:	2014-01-28 10:00
categories: sourcecode-visualizer
desc:	"<strong>Abstract Syntax Tree</strong> is a view in the Dr. Garbage tool suite to represent the the abstract syntactic structure of a selected java class file."
---

General
-------

In the Abstract Syntax Tree (AST) each node denotes a
construct occuring in the source code. It can be used to
analyse the structure of java methods.

The AST view can be opened via:

`Window > Show View > Other ... > Dr. Garbage > Abstract Syntax Tree`






Features
--------

* show/hide nodes depending on their type
* collapse subtrees
* generate AST graphs directly from ASTView




Examples
--------

![screenshot]({{ site.imgurl }}{{ page.url }}ast1.png)

The source code of the class in the above screenshot:

{% highlight java %}
package test;

public class TestClass {
	int testabc (int a, int b) {
		if (a > b)
			return a + b;
		else
			return a + 2;
	}
}
{% endhighlight %}





Hide Nodes
----------

![screenshot]({{ site.imgurl }}{{ page.url }}ast2.png)

Click on the button corresponding to the type of nodes you want to hide (left to right: `package declarations`, `package imports`, `javadoc`, `fields`)




Refresh AST
-----------

The AST will refresh automatically when you save your java file. However, __all subtrees will collapse in the view__ 




Generate AST graphs
-------------------

`right`-click on the subtree of which you want to create and select *Generate AST tree Graph* from the context menu.

![MethodDeclaration: int testabc()]({{ site.imgurl }}{{ page.url }}graph.png)

Graph of `MethodDeclaration: int testabc()`
