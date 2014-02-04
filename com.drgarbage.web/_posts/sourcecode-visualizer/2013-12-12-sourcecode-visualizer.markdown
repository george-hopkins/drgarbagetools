---
layout: tool
title:	"Sourcecode Visualizer"
date:	2013-12-12 15:43
categories:
desc:	"<strong>Sourcecode Visualizer</strong> is an Eclipse plugin for visualizing Java sourcecode. It draws a control flow graph alongside of Java source code."
eclipsedl: "<a href='http://marketplace.eclipse.org/marketplace-client-intro?mpc_install=1163' title='Drag and drop into a running Eclipse Indigo workspace to install Sourcecode Visualizer'> <img src='http://marketplace.eclipse.org/misc/installbutton.png'/> </a>"
download: true
---

To install Sourcecode Visualizer via [Eclipse Marketplace Client][marketplace]
drag and drop the install button into a running Eclipse workspace. 

**Sourcecode Visualizer** is an inevitable tool for sourcecode review. Its 3
basic components are: 

* [Sourcecode Editor](#editor)
* [Control Flow Graph Panel](#cfgp)
* [Synchronization of control flow graph and sourcecode editor](#sync)
* [Abstract Syntax Tree View][ast]




Sourcecode Editor  <a name="editor"></a>
-----------------

The Dr. Garbage **Sourcecode Visualizer** supports common features of the
Eclipse Java editor.




Control Flow graph panel  <a name="cfgp"></a>
------------------------

The control flow graph can be placed in the editor window or in separate
window. The graph can be modified in the [Control Flow Graph Factory][cfgf].
Every node in the control flow graph shows the corresponding bytecode
instructions.




Synchronisation of control flow graph and sourcecode editor  <a name="sync"></a>
-----------------------------------------------------------

On every saved change of the sourcecode the graph is updated and synchronized
with lines of the sourcecode.

![example screenshot]({{ site.imgurl }}{{ page.url }}example1.png)




Typical use cases
-----------------

Check the efficiency of your implementation on the sourcecode by simply looking
at the graph.

![example screenshot]({{ site.imgurl }}{{ page.url }}example2.png)

Complex if-conditions are visualized for easy analysis on-the-fly while coding.

![example screenshot]({{ site.imgurl }}{{ page.url }}example3.png)

[ast]: {{ site.url }}/sourcecode-visualizer/abstract-syntax-tree/
[marketplace]: http://marketplace.eclipse.org/marketplace-client-intro
[cfgf]: {{ site.url }}/control-flow-graph-factory/
