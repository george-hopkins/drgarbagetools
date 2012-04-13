/**
 * Copyright (c) 2008-2012, Dr. Garbage Community
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.drgarbage.dot;

import java.util.ArrayList;

public class DotValues {
	public static String true_ = "true";
	public static String false_ = "false";

	public static enum arrowType {
		normal, inv, dot, invdot, odot, invodot, none, tee, empty, invempty, diamond, odiamond, ediamond, crow, box, obox, open, halfopen, vee
	}

	public static enum clusterMode {
		local, global, none
	}

	public static enum dirType {
		forward, back, both, none
	}

	/**
	 * layerRange layerId or layerIdslayerId, where layerId = "all", a decimal
	 * integer or a layer name. (An integer i corresponds to layer i.) The
	 * string s consists of 1 or more separator characters specified by the
	 * layersep attribute.
	 */
	public static enum layerRange {
		all
	}

	public static enum outputMode {
		breadthfirst, nodesfirst, edgesfirst
	}

	public static enum nodeShapePolygon {
		box, polygon, ellipse, circle,
		point, egg, triangle, plaintext,
		diamond, trapezium, parallelogram, house,
		pentagon, hexagon, septagon, octagon,
		doublecircle, doubleoctagon, tripleoctagon, invtriangle,
		invtrapezium, invhouse, Mdiamond, Msquare,
		Mcircle, rect, rectangle, none,
		note, tab, folder, box3d, component
	}

	/**
	 * packMode "node", "clust" , "graph" , "array[_flags][%d]"
	 * 
	 * The modes "node", "clust" or "graph" specify that the components should
	 * be packed together tightly, using the specified granularity. A value of
	 * "node" causes packing at the node and edge level, with no overlapping of
	 * these objects. This produces a layout with the least area, but it also
	 * allows interleaving, where a node of one component may lie between two
	 * nodes in another component. A value of "graph" does a packing using the
	 * bounding box of the component. Thus, there will be a rectangular region
	 * around a component free of elements of any other component. A value of
	 * "clust" guarantees that top-level clusters are kept intact. What effect a
	 * value has also depends on the layout algorithm. For example, neato does
	 * not support clusters, so a value of "clust" will have the same effect as
	 * the default "node" value.
	 * 
	 * The mode "array[_flags][%d]" indicates that the components should be
	 * packed at the graph level into an array of graphs. By default, the
	 * components are in row-major order, with the number of columns roughly the
	 * square root of the number of components. If the optional flags contains
	 * "c", then column-major order is used. Finally, if the optional integer
	 * suffix is used, this specifies the number of columns for row-major or the
	 * number of rows for column-major. Thus, the mode "array_c4" indicates
	 * array packing, with 4 rows, starting in the upper left and going down the
	 * first column, then down the second column, etc., until all components are
	 * used.
	 * 
	 * If the optional flags contains "u", this causes the insertion order of
	 * elements in the array to be determined by user-supplied values. Each
	 * component can specify its sort value by a non-negative integer using the
	 * sortv attribute. Components are inserted in order, starting with the one
	 * with the smallest sort value. If no sort value is specified, zero is
	 * used.
	 * 
	 */
	public static enum packMode {
		node, clust, graph
	}

	public static enum pagedir {
		BL, BR, TL, TR, RB, RT, LB, LT
	}

	public static enum compass_point {
		n, ne, e, se, s, sw, w, nw, c, _
	}

	public static enum quadType {
		normal, fast, none
	}

	public static enum rankType {
		same, min, source, max, sink
	}

	public static enum rankdir {
		TB, LR, BT, RL
	}

	public static enum smoothType {
		none, avg_dist, graph_dist, power_dist, rng, spring, triangle
	}

	
	public static enum startTypeStyle {
		regular, self, random
	}
	
	public static enum style {
		filled, invisible, diagonals, rounded, dashed, dotted, solid, bold
	}

	public static final String[] ALL;

	static {
		ArrayList<String> all = new ArrayList<String>();

		all.add(true_);
		all.add(false_);

		addValues(arrowType.class, all);
		addValues(clusterMode.class, all);
		addValues(dirType.class, all);
		addValues(layerRange.class, all);
		addValues(nodeShapePolygon.class, all);
		addValues(outputMode.class, all);
		addValues(packMode.class, all);
		addValues(pagedir.class, all);
		addValues(compass_point.class, all);
		addValues(quadType.class, all);
		addValues(rankType.class, all);
		addValues(rankdir.class, all);
		addValues(smoothType.class, all);
		addValues(startTypeStyle.class, all);
		addValues(style.class, all);

		ALL = all.toArray(new String[all.size()]);
	}

	private static void addValues(Class<?> cl, ArrayList<String> a) {
		for (Object o : cl.getEnumConstants()) {
			a.add(o.toString());
		}
	}

	public static void main(String[] args) {
		for (String s : ALL) {
			System.out.println(s);
		}
	}

}
