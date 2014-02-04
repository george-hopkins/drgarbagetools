---
layout:	page
title:	"Debugging classes generated at run time"
date:	2013-12-12 13:54
categories: bytecode-visualizer
---

Debug, inspect and understand Java bytecode from classes that are completely
generated at runtime. Dr. Garbage does visualization of classes retrieved from
Java Virtual Machine via [Java™ Debug Interface][jdi].

Synthetic classes which are completely generated at runtime can now be
visualized thanks to this new feature of Dr. Garbage Tool Suite. 

To learn more about the debugging feature download [this java
project][testcase] and go through the tutorial steps. 

1.	[Importing the project into eclipse work space](#1)
2.	[Setting break points](#2)
3.	[Running the test application](#3)
4.	[Reading loaded classes from the JVM](#4)





Importing the project into eclipse work space  <a name="1"></a>
---------------------------------------------

Open your Eclipse (with the newest Dr. Garbage Tool Suite installed), right
click in the *Package Explorer* and then choose `Import...`

![example screenshot]({{ site.imgurl }}{{ page.url }}right-click-import.png)

The import window opens up, choose Existing Projects into Workspace and then
click `Next`.

![example screenshot]({{ site.imgurl }}{{ page.url }}import-window.png)

Browse for the unzipped folder you downloaded in Step 1 

![example screenshot]({{ site.imgurl }}{{page.url }}browse-for-project.png)

In the Project Explorer, you should see the structure of the imported Project
like this: 

![example screenshot]({{ site.imgurl }}{{page.url }}project-structure.png)

Now you can switch to the Debug View from the top right corner of Eclipse:

![example screenshot]({{ site.imgurl }}{{page.url }}switch-to-debug-view.png)

[back to top](#top)




Setting break points  <a name="2"></a>
--------------------

Open the main class `TestCaseForByteCodeVisualizer.java`, set two breakpoints at
line 18 and 66: 

![example screenshot]({{ site.imgurl }}{{page.url }}set-breakpoints.png)

[back to top](#top)




Running the test application  <a name="3"></a>
----------------------------

Click on the triangle on the right of the Debug symbol and choose the class
`TestCaseForByteCodeVisualizer.java`.

![example screenshot]({{ site.imgurl }}{{page.url }}start-debugging.png)

At first you may only see one frame for
`TestCaseForByteCodeVisualizer.callback() line: 66`. After pressing *F8* or
click on the resume button (marked on the picture), you will be able to see
this:

![example screenshot]({{ site.imgurl }}{{page.url }}f8-or-resume.png)

The GeneratedRunnableImpl.run() is the method of the class which is generated
at runtime and also the class we want to debug or visualize. But when you click
on the frame for this class, in the source window you can only see *"Source not
found"* and a button `Edit Source Lookup Path...`. Because it does not have any
counter part in the build path of our Eclipse Project.

![example screenshot]({{ site.imgurl }}{{page.url }}source-not-found.png)

[back to top](#top)




Reading loaded classes from the JVM  <a name="4"></a>
-----------------------------------

Now we will copy this generated class from the JVM and export into our Project
build path. Click on the button `Read classes from JVM ...`

![example screenshot]({{ site.imgurl }}{{page.url }}read-from-jvm.png)

A search dialog opens up, and as you can search for the `GeneratedRunnableImpl`
class. We need to specify a path to copy to 

![example screenshot]({{ site.imgurl }}{{page.url }}read-from-jvm-dialog.png)

Click on `browse...`, you will be able to create a new folder inside the
current project.

![example screenshot]({{ site.imgurl }}{{page.url }}copy-to-build-path.png)

And give it a name

![example screenshot]({{ site.imgurl }}{{page.url }}name-folder.png)

After that, the path to this class will be displayed in the textbox in the
dialog (You cannot modify that path inside this textbox). On the left, you can
see the *test* folder is added to the project folder and also to build path. At
this step, you may also choose more than one class in the JVM to export,
sometimes it will take a little bit longer, a progress dialog will also be
displayed. 

![example screenshot]({{ site.imgurl }}{{page.url }}test-in-build-path.png)

![example screenshot]({{ site.imgurl }}{{page.url }}progress.png)

Click on the `Copy to Build Path` button, the class inside JVM will be exported
and copied into the test folder we created before, the class can now be
visualized and debugged. With two marked buttons you can step over single
instruction or step into byte code of this generated class. 

![example screenshot]({{ site.imgurl }}{{page.url }}class-file-in-debug-window.png)

To retrieve the *GeneratedRunnableImpl* class from the JVM via JDI we need to
go to the Dr. Garbage Preferences and choose the option *"JVM via JDI (Java Debug
Interface)"* when visualizing a class: 

![example screenshot]({{ site.imgurl }}{{page.url }}change-view-option.png)

It is easy to recognize the differences between 2 Visualizations. The
constructor from the exported class (inside the *test* folder) contains no
codes (`nop`) and the exported class (at this time) does not contain all
attributes, because the complete content will only be available at runtime. 

![example screenshot]({{ site.imgurl }}{{page.url }}file-system-vs-jvm.png)

[back to top](#top)




[jdi]: http://docs.oracle.com/javase/1.5.0/docs/guide/jpda/jdi/ 
[testcase]: {{ site.fileurl }}{{ page.url }}TestCaseForBytecodeVisualizer.zip
