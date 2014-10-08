---
layout:	tool
title:	"Control Flow Graph Factory - Graph Comparison"
date:	2014-04-23 15:52
categories: control-flow-graph-factory
desc:	"<strong>Graph Comparison</strong> is a feature in the Dr. Garbage tool which provides a graphical user interface to investigate isomorphism between graphs."
---

How to compare graphs <a name="how"></a>
--------------
Two graphs produced by [Control Flow Graph Factory][cfgf] can be compared by following way:

* Select two graphs in Eclipse Package Explorer 
* Right click at "Compare with: Each other" as depicted below

![sd]({{ site.imgurl }}{{ page.url }}how-to-compare-graphs.png)

Graphical user interface <a name="how"></a>
--------------

After first steps of graph comparison a new tab *Compare* pops up.
![sd]({{ site.imgurl }}{{ page.url }}tool-opened-after.png)

Two selected graphs are represented side by side in the opened tab. In the upper left corner the drop-down list indicates that current window presents **Graph compare**.
In the upper right corner the management panel is placed.

Algorithms used for the graph comparison: <a name="tda"></a>
--------------

* [Top-Down MaxCommon Subtree Isomorphism Algorithm][cfgc-tdmc]
* [Bottom-Up MaxCommon Subtree Isomorphism Algorithm][cfgc-bumc]


[//]: # (-------------Links used -------------)

[cfgf]: {{ site.url }}/control-flow-graph-factory/
[cfgc-tdmc]: {{ site.url }}/control-flow-graph-factory/graph-compare-tdmc/
[cfgc-bumc]: {{ site.url }}/control-flow-graph-factory/graph-compare-bumc/
