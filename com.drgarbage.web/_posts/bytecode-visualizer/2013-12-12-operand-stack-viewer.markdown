---
layout:	tool
title:	"Operand Stack Viewer"
date:	2013-12-12 04:00
categories: bytecode-visualizer
desc:	"<strong>Operand Stack Viewer</strong> is a view in the Dr. Garbage tool suite to represent the operand stack of a selected method."
---

General
-------

All byte-code instructions of the JVM take operands from the stack, operate on
them and return results to the stack. Each method in a Java class file has a
stack frame. Each frame con- tains a last-in-first-out (LIFO) stack known as
its operand stack. The stack frame of a method in the JVM holds the method’s
local variables and the method’s operand stack. 




Features
--------

While in the bytecode visualizer, the operand stack view can be opened via:

`Window > Show View>Other ... >Dr. Garbage > Operand Stack`

![Operand stack view]({{ site.imgurl }}{{ page.url }}operand-stack-view.png)




Examples
--------

The source code of the method in the above screenshot:

{% highlight java %}
int testabc(int a, int b) {
	if (a > b)
		return a + b;
	else
		return a + 2;
}
{% endhighlight %}


The compiled *bytecode* for this class:

{% highlight java %}
int testabc(int b, int arg1) {
	/* L28 */
	0  iload_1;                /* a */
	1  iload_2;                /* b */
	2  if_icmple 7;
	/* L29 */
	5  iload_1;                /* a */
	6  iload_2;                /* b */
	7  iadd;
	8  ireturn;
	/* L31 */
	9  iload_1;                /* a */
	10 iconst_2;
	11 iadd;
	12 ireturn;
}
{% endhighlight %}

From the triangle dropdown menu in the top right corner of the Operand Stack
view, it is possible to:

* Change layout of the view:
	+ Tree view (default)
	+ Basic block view
	+ Instruction list view

	![view layout]({{ site.imgurl }}{{ page.url }}view-layout.png)

* Choose column(s) to be displayed:
	+ Operand Stack before
	+ Operand Stack after
	+ Operand Stack depth
	+ Description

	![show columns]({{ site.imgurl }}{{ page.url }}show-columns.png)

* Change the display properties of the view
	+ Simple (default)
	+ Type list
	+ All

	![operand stack format]({{ site.imgurl }}{{ page.url }}operand-stack-format.png)

* Run an [Operand Stack analysis report]({{  site.fileurl }}{{ page.url }}report.txt),
  which contains:
  	+ A size based analysis
	+ A type based analysis
	+ A content based analysis
	+ Some statistical information about the operand stack

	![operand stack analysis]({{ site.imgurl }}{{ page.url }}operand-stack-analysis.png)

There are also shortcut icons to change the display properties of the view or
run the report:<br />
left to right: *tree view*, *block view*, *instruction list view*, *open report*

![]({{ site.imgurl }}{{ page.url }}treeview-icon.png "Tree View")
![]({{ site.imgurl }}{{ page.url }}blockview-icon.png "Block View")
![]({{ site.imgurl }}{{ page.url }}instructionlistview-icon.png "Instruction List View")
![]({{ site.imgurl }}{{ page.url }}report-icon.png "Open Report")

The Operand Stack, the bytecode viewer and the bytecode graph visualizer are
**multi-directional synchronized**, if a node on any view is selected, the
corresponding nodes of other views will be also highlighted:

![synchonization example screenshot]({{ site.imgurl }}{{ page.url }}synchronized.png)

**Usability tip:** if you need to scroll through a very large method, it will
be faster and smoother to do the scrolling from the graph section rather than
scrolling from the bytecode viewer section.
