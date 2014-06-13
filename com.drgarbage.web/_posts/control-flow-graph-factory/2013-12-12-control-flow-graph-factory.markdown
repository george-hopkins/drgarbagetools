---
layout:	tool
title:	"Control Flow Graph Factory"
date:	2013-12-12 16:23
categories:
desc:	"Generate control flow graphs from java bytecode, edit them and export to GraphXML, DOT or several image formats."
eclipsedl: "<a href='http://marketplace.eclipse.org/marketplace-client-intro?mpc_install=679' title='Drag and drop into a running Eclipse Indigo workspace to install Control Flow Graph Factory'> <img src='http://marketplace.eclipse.org/misc/installbutton.png'/> </a>"
download: true
---

To install Bytecode Visualizer via [Eclipse Marketplace Client][marketplace]
drag and drop the install button into a running Eclipse workspace.




Features
--------

* Automatic generation of several [types of control flow graphs][terminology]
  from Java bytecode: 
  	+ [bytecode graphs][term-bcg]
	+ [soure code graphs][term-scg]
	+ [basic block graphs][term-bbg]
	

* Editing of control flow graphs 
	+ Move, create, delete, rename, ... nodes

* Multiple algorithms for automatic layout (serial, hierarchical)
* Export in GraphXML, DOT format or as an image (JPEG, BMP, ICO, PNG)
* Possibility to compare trees and investigate the subtree similarity (See more: [graph comparison][cfgc-compare])
* Printing support




Control flow graph examples
---------------------------

![example graphs]({{ site.imgurl }}{{ page.url }}example-graphs.png)

See our [control flow graph gallery][gallery] for some more examples.




Export to Graphviz in DOT fromat
--------------------------------

[Graphviz][graphviz] (short for Graph Visualization Software) is a program for
drawing graphs specified in DOT language scripts. Control Flow Graph Factory
exports graphs with or without visual information (node color and shape) and
geometry information (node size). For more details see [DOT Export
Examples][dotexamples]. The exported graphs can be modified via simple DOT
Editor directly in the eclipse workspace. 

![DOT editor screenshot]({{ site.imgurl }}{{ page.url }}dot-editor.png)




Export as GraphXML
------------------

GraphXML is an XML-based graph description language. It covers not only the
pure, mathematical description of a graph, but also its visual aspects. [Read
more about GraphXML][graphxml]. 

With Control Flow Graph Factory the graphs can be exported with or without
visual information (node color and shape) and geometry information (node size
and position). For more details see [GraphXML Export Examples][graphxmlexport]. 




Tutorial
--------

* [Prerequisites][tut-pre]
* [Generate a graph from Java class file][tut-gen]
* [Edit and export Graph][tut-editexport]


[marketplace]: http://marketplace.eclipse.org/marketplace-client-intro
[terminology]: {{ site.url }}{{ page.url }}terminology/
[term-bcg]: {{ site.url }}{{ page.url }}terminology/#bcg
[term-scg]: {{ site.url }}{{ page.url }}terminology/#scg
[term-bbg]: {{ site.url }}{{ page.url }}terminology/#bbg
[gallery]: {{ site.url }}{{ page.url }}gallery/
[graphviz]: http://www.graphviz.org/
[dotexamples]: {{ site.url }}{{ page.url }}dot-examples/
[graphxml]: http://projects.cwi.nl/InfoVisu/GraphXML/GraphXML.pdf
[graphxmlexport]: {{ site.url }}{{ page.url }}graphxml-examples/
[tut-pre]: {{ site.url }}/howto/cfgf-tutorial/#pre
[tut-gen]: {{ site.url }}/howto/cfgf-tutorial/#gen
[tut-editexport]: {{ site.url }}/howto/cfgf-tutorial/#editexport
[cfgc-compare]: {{ site.url }}/control-flow-graph-factory/graph-compare/