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
* Right click at "Compare with: Each other" as depicted below

![sd]({{ site.imgurl }}{{ page.url }}how-to-compare-graphs.png)

Graphical user interface <a name="how"></a>
--------------

After first steps of graph comparison a new tab *Compare* pops up.
![sd]({{ site.imgurl }}{{ page.url }}compare-opened.png)

Two selected graphs are represented side by side in the opened tab. In the upper left corner the drop-down list indicates that current window presents **Graph compare**.
In the upper right corner the management panel is placed.
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
		//var currentMousePos = { x: -1, y: -1 };
		//currentMousePos.x = event.pageX;
        //currentMousePos.y = event.pageY;
		var left = event.pageX - $(this).offset().left;
        var top = event.pageY - $(this).offset().top;
		if(left < 33){
			location.hash = "#topdown";
		}
		if(left < 70 && left > 33){
			location.hash = "#bottomup";
		}
		if(left < 116 && left > 70){
			location.hash = "#resetgraphs";
		}
		if(left < 150 && left > 116){
			location.hash = "#swapgraphs";
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
			$('#used').html("Top Down");
		}
		if(left < 70 && left > 33){
			$('#used').html("Bottop up");
		}
		if(left < 116 && left > 70){
			$('#used').html("Reset compare graph");
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

Execute Top-Down Maximum Common SubTree Isomorphism <a name="topdown"></a>
--------------
After clicking the button "Execute Top-Down Maximum Common SubTree Isomorphism"
![sd]({{ site.imgurl }}{{ page.url }}top-down-executed.png)

Execute Bottom-Up Maximum Common SubTree Isomorphism <a name="bottomup"></a>
--------------
After clicking this button 

Reset compare graph <a name="resetgraphs"></a>
--------------
After clicking this button 

Swap graphs inputs <a name="swapgraphs"></a>
--------------
After clicking this button 



[//]: # (-------------Links used -------------)

[cfgf]: {{ site.url }}/control-flow-graph-factory/
[cfgc-tdmc]: {{ site.url }}/control-flow-graph-factory/graph-compare-tdmc/
[cfgc-bumc]: {{ site.url }}/control-flow-graph-factory/graph-compare-bumc/
