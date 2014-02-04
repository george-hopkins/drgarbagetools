---
layout: page
title: "First Steps with Bytecode Visualizer"
date: 2014-01-21 12:10
categories: bytecode-visualizer
---

1. Open the Bytecode Visualizer for the first time
--------------------------------------------------

![screenshot]({{ site.imgurl }}{{ page.url }}openwithbcv.png)

__Note:__ You need to do thins in the __Navigator View__. The Package Explorer View does not show \*.class files.

To open the Navigator View, go to

`Window > Show View > Navigator`

or alternatively

`Window > Show View > Other... > General > Navigator`




2. Adjust file associations (optional)
--------------------------------------

There are many situations in which Eclipse opens a \*.class file: `CTRL`-Clicking into a class / method / field without source code, *Stepping Into* in Debugger, etc. In such situations, Eclipse is using the __default editor__ for the \*.class file type.

Bytecode Visualizer __is__ set as default editor for /*.class files during the installation.

If it does not suit your needs, you probably want to change the File Associations in Preferences:

`Window > Preferences > General > Editors > File Associations`

![Change the File Associations]({{ site.imgurl }}{{ page.url }}file_associations.png)

Select the file type __\*.class__ and set the editor of your choice as Default.




3. Review and adjust preferences
--------------------------------

The behavior of Bytecode Visualizer is adjustable in many respects, such as:

* where the control flow graph should be displayed: split pane in the editor or separate view
* displaying/not displaying Line Number Table and Local Variable Table
* rendering/not rendering try/catch blocks
* graph colors
* syntax highlighting
* which Bytecode Editor's tab should be selected by default: Bytecode or Source code (if available) 

Just go to

`Window > Preferences > Dr. Garbage > Bytecode Visualizer`

and change the settings to suit your needs.
