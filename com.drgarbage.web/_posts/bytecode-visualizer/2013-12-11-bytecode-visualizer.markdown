---
layout: tool
title:  "Bytecode Visualizer"
date:   2013-12-11 19:00
categories:
desc:	"Inspect, understand and debug Java bytecode, no matter if you have the corresponding source."
eclipsedl: "<a href='http://marketplace.eclipse.org/marketplace-client-intro?mpc_install=678' title='Drag and drop into a running Eclipse workspace to install Bytecode Visualizer'><img src='http://marketplace.eclipse.org/misc/installbutton.png'/></a>"
download: true 

---

To install Bytecode Visualizer via [Eclipse Marketplace Client](http://marketplace.eclipse.org/marketplace-client-intro) drag and drop the install button into a running Eclipse workspace.




Typical Use Cases
-----------------

* Inspect the result of your own bytecode instrumentation
* Check the efficiency of your implementation on the bytecode level 
* If you have a third-party or legacy Java application code for which you — for whatever reason — do not have the corresponding source code:
	+ Analyze the implementation details
	+ Identify error conditions and find bugs using [Bytecode Debugger](#bytecode-debugger)




Features Overview
-----------------

* [General](#general)
* [Bytecode Viewer](#bytecode-viewer)
* [Control Flow Graph Panel](#control-flow-graph-panel)
* [Class Outline](#class-outline)
* [Bytecode Debugger for instruction-by-instruction debugging](#bytecode-debugger)
* [Customization through preferences](#preferences)
* [Operand stack viewer]({{ site.url }}/bytecode-visualizer/operand-stack-viewer/)
* [Class file editor]({{ site.url }}/bytecode-visualizer/class-file-editor/)
* [Class file compare]({{ site.url }}/bytecode-visualizer/class-file-compare/)
* [Debugging classes generated at run time (NEW)]({{ site.url }}/bytecode-visualizer/read-class-from-jvm/)




General  <a name="general"></a>
-------

* Reading bytecode from both *.class files or *.jar archives located in the filesystem.
* [Classes can be also read from a running JVM over Java Debug Interface (JDI)]({{ site.url }}/bytecode-visualizer/visualizing-from-running-jvm/)
* Thorough Eclipse Integration (see also [File Associations](#preferences)) 
	+ Opening class files from Package Explorer, Navigator, stack trace of an Exception in the Console view, clicking the stack and stepping into in Debugger, etc.
	+ Open Declaration (CTRL-Click or F3) of a class or method without source code from Java Editor




Bytecode Viewer  <a name="bytecode-viewer"></a>
---------------
<div class="example-screenshot" style="height: 620px;">
<img style="float: right;" src="{{ site.imgurl }}/bytecode-visualizer/example-bytecode-view.png" alt="Example class visualized in Bytecode Visualizer" />
<div class="clear-r"></div>
<div class="flag" style="float: right; top: 35px; right: 10px;">
	<strong>Control Flow Graph Panel</strong> helps to understand the
	execution paths of the given method
	<b class="notch-br"></b>
</div>
<div class="flag" style="top: 100px;">
	<strong>Signatures</strong> of classes, fields and methods rendered as in java source
	<b class="notch-r"></b>
</div>
<div class="flag" style="top: 180px;">
	The <strong>opcodes</strong> of instructions represented by their mnemonics as specified in <a href="http://java.sun.com/docs/books/jvms/">The Java Virtual Machine Specification</a>
	<b class="notch-r"></b>
</div>
<div class="flag" style="top: 280px;">
	<strong>Comments</strong> make it easy to understand the instructions and their parameters
	<b class="notch-r"></b>
</div>
<div class="flag" style="top: 360px;">
	Easy-to-follow scope of <strong>exception handlers</strong> (q.v. <a
	href="#preferences">preferences</a>)
	<b class="notch-r"></b>
</div>
<div class="flag" style="top: 450px;">
	<strong>Line number table</strong> and <strong>local variable
	table</strong> as a comment. (q.v. <a
	href="#preferences">preferences</a>)
	<b class="notch-r"></b>
</div>
<div class="flag" style="float: right; top: 540px; right: 225px;">
	Two tabs for easy switching between <strong>bytecode</strong> and
	<strong>source code</strong> (q.v. <a
	href="#preferences">preferences</a>)
	<b class="notch-tl"></b>
</div>
</div>




<img style="float: right; margin-left: 0.5em;" src="{{ site.imgurl }}/bytecode-visualizer/example-control-flow-graph-view.png" alt="Control Flow Graph View" />

Control Flow Graph Panel  <a name="control-flow-graph-panel"></a>
------------------------

Control Flow Graph Panel can be displayed in a split pane (picture above) or in
a freely dockable view as shown in the picture on the right (q.v.
[preferences](#preferences))

Two alternative representations of Control Flow Graphs are supported:

* single instructions (as in the above picture)
* basic blocks (picture on the right)

<div class="clear"></div>
<br />




Class File Outline  <a name="class-outline"></a>
------------------

<div class="example-screenshot" style="height: 370px;">
<img src="{{ site.imgurl }}/bytecode-visualizer/example-outline.png" alt="Bytecode Visualizer with Outline window" />
<div class="flag" style="left: 400px; top: 130px;">
	The <strong>Class File Outline</strong> is both ways synchronized with
	the Bytecode Viewer: by clicking the method in outline, the viewer
	scrolls to the given method and vice versa
	<b class="notch-l"></b>
</div>
</div>




Bytecode Debugger for instruction-by-instruction debugging  <a name="bytecode-debugger"></a>
----------------------------------------------------------

See also: [How to Debug Bytecode with Bytecode Visualizer]({{ site.url }}/howto/debug-bytecode/).

<div class="example-screenshot" style="height: 650px;">
<img style="float: right;" src="{{ site.imgurl }}/bytecode-visualizer/example-debugger.png" alt="Example class visualized with Bytecode Visualizer in Debug Perspective" />
<div class="flag" style="float: right; left: 400px; top: 60px;">
	<strong>Single instruction stepping</strong> Buttons
	<b class="notch-bl"></b>
</div>
<div class="flag" style="top: 230px; width: 160px;">
	Supported breakpoints:
	<ul>
	<li><strong>Class load</strong> breakpoint</li>
	<li>Field <strong>watchpoint</strong></li>
	<li><strong>Method entry</strong> breakpoint</li>
	</ul>
	<b class="notch-r"></b>
</div>
<div class="flag" style="top: 350px; width: 160px;">
	Breakpoints can be set by <strong>double clicking</strong> the left ruler
	<b class="notch-r"></b>
</div>
<div class="flag" style="float: right; left: 300px; top: 550px;">
	Dr. Garbage Bytecode Debugger works even for classes <strong>without
	line number table</strong> 
	<b class="notch-tl"></b>
</div>
</div>




Customization through Preferences  <a name="preferences"></a>
---------------------------------

The behavior of Bytecode Visualizer is adjustable in many respects, just go to

`Window > Preferences > Dr. Garbage > Bytecode Visualizer`

and adjust the preferences to suit your needs:

* Where the control flow graph should be displayed: split pane in the editor or separate view
* **Appearance**
	+ Displaying/not displaying Line Number Table
	+ Displaying/not displaying Local Variable Table
	+ Rendering/not rendering try/catch blocks
* **Source Code** - which editor tab should be active when opening a class file
	+ Sourcecode if available
	+ Always Source
	+ Always Bytecode
* **Graph Colors**
* **Syntax Highlighting**
* **File Associations:** Bytecode Visualizer can be set/unset as a default
  editor for *.class files on the File Associations preference page:<br />
  `Window > Preferences > General > Editors > File Associations`<br />
  This influences the behavior of several Eclipse actions, e.g. Open Declaration (CTRL-Click or F3), Step Into in Debugger, etc.  
