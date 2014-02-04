---
layout: page
title: "How to debug bytecode with Bytecode Visualizer"
date: 2013-12-12 15:16
categories: howto
---

Have you seen this warning dialog?
----------------------------------

![Debug functionality is unavailable in this context]({{ site.imgurl }}{{ page.url }}warning-dialog.png)




Prerequisites
-------------

The debug-related functionality such as setting breakpoints and displaying
classes from the stack in the debug view is available for classes which are
both:

1. included in the build path of a java project
2. opened using Package Explorer, Open Declaration (F3) or similar feature of
   Java Development Tools (JDT) plugin.

(2) is normally impossible without (1).




How to include classes into Build Path
--------------------------------------

To achieve prerequisite 1 the steps are different for the following 3
situations:

1.	Your .class files are in a JAR archive.
	In Project Explorer, go to

	```bytecode-debug-project > context menu > Properties > Java Build Path >
	Libraries (Tab) > Add JARs... (Button)```

	and add your JAR (which is preferably located in the workspace)

2.	Your .class files are in a build output folder (usually called 'bin')
	of some Java project (call it 'project-1') in your Eclipse workspace.

	You need to use project distinct from project-1 to debug the classes
	from project-1's build output folder. Create a new Java project, name it
	e.g.  'bytecode-debug-project' and copy recursively project-1/bin to
	bytecode-debug-project/classes. Proceed with (3.2)
	
3.	Your .class files are somewhere in your filesystem and (2) is not the
	case.  

	1.	Create a new Java project, name it e.g.
		'bytecode-debug-project' and copy your .class files (together
		with their package folders) to bytecode-debug-project/classes. 
	2.	In Project Explorer, go to

		```bytecode-debug-project > context menu > Properties > Java
		Build Path > Libraries (Tab) > Add Class Folder (Button)```

		and add the bytecode-debug-project/classes folder.

After you have included your classes in build path of a Java project, they are
visible in Package Explorer under bytecode-debug-project/Referenced Libraries/
If you open some of your classes now, you can set breakpoints and when you
start a debug session, you see your bytecode upon suspend. 




Setting breakpoints
-------------------

The following breakpoint types are supported in Bytecode Visualizer:

* Class Load breakpoint
* Field Watchpoint
* Method Entry breakpoint

Breakpoints can be set by double clicking on the left ruler or through the
context menu of the left ruler.




Adjust preferences
------------------

You may want to adjust some preferences before you start your first debug
session. Just go to

`Window > Preferences > Dr. Garbage > Bytecode Visualizer > General`

If you are debugging classes which are modified at runtime, you may consider
activating the JDI class retrieval option; see also [Visualizing classes as they
appear in a running JVM][vis-running].

If Java bytecode is the only thing you want to see during your debug session,
you probably want check the appropriate preference on

`Window > Preferences > Dr. Garbage > Bytecode Visualizer > Source Code`




Start debug session
-------------------

Start debug session in the same way as you would start a debug session for
debugging source code.

Bytecode Debugger will suspend when a breakpoint is reached and bytecode will
be displayed in Bytecode Tab of Bytecode Viewer.

Use bytecode stepping buttons to step through bytecode instructions:

![icon]({{ site.imgurl }}{{ page.url }}step-into-bytecode.png)
Step Into Bytecode

![icon]({{ site.imgurl }}{{ page.url }}step-over-single-instruction.png)
Step Over Singe Instruction


[vis-running]: {{ site.url }}/bytecode-visualizer/visualizing-from-running-jvm/
