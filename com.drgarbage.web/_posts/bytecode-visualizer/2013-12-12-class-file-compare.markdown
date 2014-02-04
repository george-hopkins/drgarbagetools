---
layout:	tool
title:	"Class File Compare"
date: 	2013-12-12 06:07
categories: bytecode-visualizer
desc:	"<strong>Class File Compare</strong> is a feature of the Dr.Garbage tool suite, which brings additional functionalities to the default eclipse compare feature:"
---

* Comparing Java Classes from local filesystem or java archive in Dr. Garbage
  structural or hexadecimal format.
* Easily naviagte between those highlighted differences.

There are two options to start comparing class files:

* [Marking two class files and compare with each other](#firstway): in the
  context menu choose Compare With... > Each Other - Class File Compare.
* [Choose one class file and find another class file to compare with](#secondway):
  by choosing one class, from the context menu, choose Other Class File
  instead. After that you can search for and select the class you want to
  compare.

![screenshot]({{ site.imgurl }}{{ page.url }}firstway.png)

![screenshot]({{ site.imgurl }}{{ page.url }}secondway.png)




The Structure Compare view
--------------------------

A structure merge view presents the result in a hierarchical view, and lets the
user merge between the inputs.

<div class="example-screenshot" style="margin-top: 60px; height: 520px">
<img src="{{ site.imgurl }}{{ page.url }}structure-compare-view.png" alt="example screenshot" />
<div class="flag" style="top: -50px; left: 110px;">
	Names of two classes being compared
	<b class="notch-br"></b>
</div>
<div class="flag" style="top: 125px; left: 220px;">
	<strong>Single difference stepping</strong> buttons
	<b class="notch-r"></b>
</div>
<div class="flag" style="top: 460px; left: 135px;">
	The lines connected between compared sections.
	<b class="notch-tr"></b>
</div>
</div>

The line numbers of the current and the incoming change will be placed in the
notification section of eclipse.

![example screenshot]({{ site.imgurl }}{{ page.url }}menu-and-footer.png)




The hexadecimal view
--------------------

After choosing *Class File Compare* from the dropdown menu, you will be
presented with the hexadecimal comparison version. If you select any part of
the hex value, its corresponding changes and the connected line between them
will be marked as black. The yellow marked area contains the boxes with grey
borders on the right-hand side of the view will help you locate the differences
easily.

![example screenshot]({{ site.imgurl }}{{ page.url }}hexadecimal-compare-view.png)

When you reach the beginning or the end of the changes, you may receive a
dialog asking what you would like to do:

![example screenshot]({{ site.imgurl }}{{ page.url }}beginning-reached.png)
