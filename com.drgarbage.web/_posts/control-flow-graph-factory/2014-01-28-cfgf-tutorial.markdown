---
layout: page
title: "Control Flow Graph Factory Tutorial"
date: 2014-01-28 21:55
categories: howto
---

* [Prerequisites](#pre)
* [Generate a graph from a java class file](#gen)
* [Edit and export a graph](#editexport)




Prerequisites  <a name="pre"></a>
-------------

Please download and install the Control Flow Graph Factory plugin.

[Back to the top](#top)




Generate a graph from a java class file  <a name="gen"></a>
---------------------------------------

Let's get started with the first step. Most tutorials start out with a
simple Hello World program. We've created a program those graph includes
a branch element. Just copy the following sample to your eclipse project
and compile it.

{% highlight java %}
package test;

public class C2 {

	public static float abs(float a) {
		float b = 0.0F;
		if(a <= b){
			return (b - a);
		}

		return a;
	}
	
}
{% endhighlight %}

To generate the graph for the method main select the method in package
explorer and open the context menu "Create Control Flow Graph". Select
submenu "Sourcecode graph" to generate a source code graph for this
method.

![screenshot]({{ site.imgurl }}{{ page.url }}createcfg.png)

Now select the desired target folder in the export dialog. To save the
graph file click `OK`.

![screenshot]({{ site.imgurl }}{{ page.url }}savecfg.png)

The graph is generated and open in a Control Flow Graph Editor.

![screenshot]({{ site.imgurl }}{{ page.url  }}cfgeditor.png)

Now you can generate a bytecode or a basic code graph in the same way.
Use for that the context menu in the package explorer "Create Control
Flow Graph/Bytecode graph" or "Create Control Flow Graph/Basic block
graph.

[Back to the top](#top)




Edit and export graph  <a name="editexport"></a>
---------------------

The Control Flow Graph Factory editor window provides you with some
editing functions. You can move copy or delete the graph elements. You
can order layout your elements by using the layout algorithms or layout
the elements manually.

![screenshot]({{ site.imgurl }}{{ page.url }}align-left.png)
![screenshot]({{ site.imgurl }}{{ page.url }}resize-width.png)
![screenshot]({{ site.imgurl }}{{ page.url }}resize-result.png)

To export the modified graph in DOT, GraphXML format or to an image
please use the export functions provided by the Control flow graph
Factory. You can export the graph directly to DOT or GraphXML format by
selecting the format in the context menu.

![screenshot]({{ site.imgurl }}{{ page.url }}export-dot.png)

The Control Flow Graph Factory provides a simple DOT text editor for
editing the dot files.

![screenshot]({{ site.imgurl }}{{ page.url }}dot-editor.png)

You can generate graphs for the selected method, selected class or for
the whole package.

![screenshot]({{ site.imgurl }}{{ page.url }}select-folder.png)

Thank you for practicing with this tutorial. For questions and
discussion around the control flow graph topics please use our contact
form.

[Back to the top](#top)
