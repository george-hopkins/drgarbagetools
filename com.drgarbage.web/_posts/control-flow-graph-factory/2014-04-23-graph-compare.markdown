---
layout:	tool
title:	"Control Flow Graph Factory - Graph Comparison"
date:	2014-04-23 15:52
categories: control-flow-graph-factory
desc:	"<strong>Graph Comparison</strong> is a feature in the Dr. Garbage tool which provides a graphical user interface to investigate isomorphism between graphs."
---

How to compare graphs <a name="how"></a>
--------------
Two graphs produced by [Control Flow Graph Factory][cfgf] can be compared by the following way:

* Select two graphs in Eclipse Package Explorer 
* Right click over selected items, then **Compare with** > **Each other** as depicted below

![sd]({{ site.imgurl }}{{ page.url }}how-to-compare-graphs.png)

Graphical user interface <a name="how"></a>
--------------

After the first steps of graph comparison a new Eclipse tab *Compare* pops up.
![sd]({{ site.imgurl }}{{ page.url }}compare-opened.png)

The graphical user interface represents two selected graphs side by side in the new opened tab. On the upper-left corner the drop-down list indicates that the current window presents **Graph compare**. In case the graphs keep any meta information, it makes possible to choose text-to-text compare.

The management panel of actions is placed in the upper-right corner and looks as follows:
<div style="height: 20px; margin-top: -10px;" >
	<center>
		<h3>This button executes:  <a id="used"></a> </h3>
	<center>
</div>
<div id = "panelDiv" >
	<center><img id = "panel" src="{{ site.imgurl }}/control-flow-graph-factory/graph-compare/image3023.png" /></center>
</div>
<div id = "coord"></div>
<script src="http://code.jquery.com/jquery-latest.min.js" type="text/javascript">
</script>

<script type="text/javascript">
$("#panel" ).click(function(event) {
		var left = event.pageX - $(this).offset().left;
        var top = event.pageY - $(this).offset().top;
		if(left < 33){			
			$('html, body').animate({ scrollTop: $( $("#topdown") ).offset().top }, 1000);
			return false;
		}
		if(left < 70 && left > 33){
			$('html, body').animate({ scrollTop: $( $("#bottomup") ).offset().top }, 1000);
			return false;
		}
		if(left < 116 && left > 70){
			//location.hash = "#resetgraphs";
			$('html, body').animate({ scrollTop: $( $("#resetgraphs") ).offset().top }, 1500);
			return false;
		}
		if(left < 150 && left > 116){
			//location.hash = "#swapgraphs";
			$('html, body').animate({ scrollTop: $( $("#swapgraphs") ).offset().top }, 1500);
			return false;	
		}
		if(left < 230 && left > 150){
			//location.hash = "#swapgraphs";
			$('html, body').animate({ scrollTop: $( $("#zoomzoom") ).offset().top }, 1500);
			return false;	
		}
});


 $('#panel').mouseover(function(event) { 
            var left = event.pageX - $(this).offset().left;
            var top = event.pageY - $(this).offset().top;
        });
        
$('#panel').mouseout(function() {
           // $('#used').html("Reset compare graph");
        });
$('#panel').mousemove(function(event) {
        var currentMousePos = { x: -1, y: -1 };
		currentMousePos.x = event.pageX;
        currentMousePos.y = event.pageY;
		var left = event.pageX - $(this).offset().left;
        var top = event.pageY - $(this).offset().top;
		//$('#coord').html("left:" + left + " top:" + top );
		if(left < 33){
			$('#used').html("Execute Top-Down Algorithm");
		}
		if(left < 70 && left > 33){
			$('#used').html("Execute Bottom-Up Algorithm");
		}
		if(left < 116 && left > 70){
			$('#used').html("Reset comparison of input graphs");
		}
		if(left < 150 && left > 116){
			$('#used').html("Swap graphs inputs");
		}
		if(left < 193 && left > 150){
			$('#used').html("Zoom in");
		}
		if(left < 230 && left > 193){
			$('#used').html("Zoom out");
		}
    });
</script>

Execute Top-Down Maximum Common SubTree Isomorphism <a id = "topdown"></a>
--------------

The algorithm described in book *Algorithms on Trees and Graphs* from Gabriel Valiente in is able to define isomorphism under tree structures. Therefore after clicking on the button execute *Top-Down Algorithm* the input graph structures firstly converted into
trees using **Spanning Tree Algorithm**. Thus all backward edges are being removed that ensures algorithm's execution on trees. 

The algorithm finds the largest common subtree between two **unordered** trees starting from the root. This maximum common subtree is green highlighted.
Notice that this subtree may not be unique, since there are different combinations of maximum common subtrees. However the number of nodes of this subtree is constant.

In order to define the largest subtree the algorithm uses an auxiliary *Maximum Weight Bipartite Matching* sub-algorithm. The *Maximum Weight Bipartite Matching* implements remarkable *Hungarian Method* that promotes to find edges in a weighted bipartite graph so that the sum of the weights in the matching has a maximal value.
The *Hungarian Method* solves the assignment problem and uses a modified shortest path search in the augmenting path algorithm.

![sd]({{ site.imgurl }}{{ page.url }}top-down-executed.png)
<div style=" margin-top: -10px; margin-bottom: 5px;" > <center> The picture above demonstrates a result of the algorithm's execution.</center></div>
<div class = "backtop" style="text-align: right; margin-bottom: 30px;"> 
	 <a href="#top"> back to top</a>
</div>
Execute Bottom-Up Maximum Common SubTree Isomorphism <a id="bottomup"></a>
--------------
This button is responsible for execution of *Bottom-Up Algorithm*. The algorithm is described from Gabriel Valiente in his book *Algorithms on Trees and Graphs*.
The algorithm functions only with tree structures thereby the input graph are converted into trees using **Spanning Tree Algorithm**.  

Unlike Top-Down Algorithm, the search for the largest subtree is performed starting from leaves between two **unordered** trees. 

![sd]({{ site.imgurl }}{{ page.url }}bottom-up-compared.png)
<div style=" margin-top: -10px; margin-bottom: 5px;" >  <center> The picture above demonstrates a result of the algorithm's execution.</center></div>
<div class = "backtop" style="text-align: right;"> 
	 <a href="#top"> back to top</a>
</div>
Compared graphs are reset from comparison<a id="resetgraphs"></a>
--------------
The reset button hides green highlighted subtrees after the algorithm's execution and recreates the original colour of nodes on the represented trees.
![sd]({{ site.imgurl }}{{ page.url }}compare-opened.png)
<div style=" margin-top: -10px; margin-bottom: 5px;" >  <center> Reset input graphs. </center></div>
<div class = "backtop" style="text-align: right; margin-bottom: 30px;"> 
	 <a href="#top"> back to top</a>
</div>
Swap inputs of the graphs  <a id="swapgraphs"></a>
--------------
The button firstly resets the colour of the graphs to original and then replaces them. The algorithms described above can be also applied for swapped graphs.
![sd]({{ site.imgurl }}{{ page.url }}graphs-swaped.png)
<div style=" margin-top: -10px; margin-bottom: 35px;" >  <center> The graphs are swapped.</center></div>

Zoom in / Zoom out <a id="zoomzoom"></a>
--------------
If the input graphs are too small or large, this feature provides synchronized zoom it/out of input diagrams. 
It allows to view the maximum common subtrees when the input graphs are large, for example **[Abstract Syntax Tree][ast]**.

![sd]({{ site.imgurl }}{{ page.url }}zoom-graphs.png)
<div style=" margin-top: -10px; margin-bottom: 5px;" >  <center> The picture above demonstrates a result of the *Top-Down algorithm* in fitted zoomed out view.</center></div>
<div class = "backtop" style="text-align: right; margin-bottom: 30px;"> 
	 <a href="#top"> back to top</a>
</div>


<script type="text/javascript">
	$( ".backtop" ).click(function(event) {
			$('html, body').animate({ scrollTop: $( $("#head") ).offset().top }, 1300);
			return false;
});
</script>

For detailed information about used algorithms please check our <a href="http://www.drgarbage.com/javadoc/core/doc/index.html">JavaDoc</a>
[//]: # (-------------Links used -------------)
[cfgf]: {{ site.url }}/control-flow-graph-factory/
[cfgc-tdmc]: {{ site.url }}/control-flow-graph-factory/graph-compare-tdmc/
[cfgc-bumc]: {{ site.url }}/control-flow-graph-factory/graph-compare-bumc/
[ast]: {{ site.url }}/sourcecode-visualizer/abstract-syntax-tree/
