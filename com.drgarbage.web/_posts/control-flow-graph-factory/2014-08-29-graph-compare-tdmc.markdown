---
layout:	page
title:	"Top-Down Maximum Common Subtree Isomorphism Algorithm"
date:	2014-08-29 13:18
categories: control-flow-graph-factory
---
Input graphs are transformed into trees using Spanning Tree Algorithm(See JavaDoc).
The algorithm is based to find a maximum common subtree between two different trees starting from root(See JavaDoc).


![sd]({{ site.imgurl }}{{ page.url }}top-down-maxcommon-tree-compare-updated.png)

Mapped nodes are green highlighted according to the algorithm.
In this algorithm the Hungarian method has been used
in order to find maximum weight cardinality  matching(See JavaDoc).