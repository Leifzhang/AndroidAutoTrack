/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kronos.plugin.base.graph

import java.util.*
import kotlin.math.min


interface DirectedGraph<N, V> {
    fun getNodeValues(
        node: N,
        values: MutableCollection<in V>,
        connectedNodes: MutableCollection<in N>
    )
}

/**
 * A directed graph with nodes of type N. Each edge has a collection of values of type V
 */
interface DirectedGraphWithEdgeValues<N, V> :
    DirectedGraph<N, V> {
    fun getEdgeValues(from: N, to: N, values: MutableCollection<in V>)
}


/**
 * A graph walker which collects the values reachable from a given set of start nodes. Handles cycles in the graph. Can
 * be reused to perform multiple searches, and reuses the results of previous searches.
 *
 * Uses a variation of Tarjan's algorithm: http://en.wikipedia.org/wiki/Tarjan%27s_strongly_connected_components_algorithm
 */
class CachingDirectedGraphWalker<N, T> constructor(
    private val cleanCache: Boolean = false,
    private val graph: DirectedGraphWithEdgeValues<N, T>
) {
    private val startNodes = arrayListOf<N>()
    private val strongComponents = linkedSetOf<NodeDetails<N, T>>()
    private val cachedNodeValues = hashMapOf<N, MutableSet<T>>()

    constructor(cleanCache: Boolean, graph: DirectedGraph<N, T>) : this(
        cleanCache,
        GraphWithEmptyEdges(graph)
    )

    /**
     * Adds some start nodes.
     */
    fun add(vararg values: N): CachingDirectedGraphWalker<N, T> {
        startNodes += values
        return this
    }

    /**
     * Adds some start nodes.
     */
    fun add(values: Iterable<N>): CachingDirectedGraphWalker<*, *> {
        startNodes += values
        return this
    }

    /**
     * Calculates the set of values of nodes reachable from the start nodes.
     */
    fun findValues(): Set<T> {
        return try {
            val values = doSearch()
            if (strongComponents.isNotEmpty()) {
                val cycles = strongComponents.fold(arrayListOf<N>()) { list, nodeDetails ->
                    list += nodeDetails.componentMembers.mapTo(linkedSetOf<N>()) {
                        it.node
                    }
                    list
                }
                error("Found cycles: $cycles")
            }
            values
        } finally {
            startNodes.clear()
        }
    }

    /**
     * Returns the set of cycles seen in the graph.
     */
    fun findCycles(): List<Set<N>> {
        try {
            doSearch()
        } finally {
            startNodes.clear()
        }
        return strongComponents.fold(arrayListOf()) { list, nodeDetails ->
            list += nodeDetails.componentMembers.mapTo(linkedSetOf<N>()) {
                it.node
            }
            list
        }
    }

    private fun doSearch(): Set<T> {
        var componentCount = 0
        val seenNodes: MutableMap<N, NodeDetails<N, T>> =
            HashMap()
        val components: MutableMap<Int, NodeDetails<N, T>> =
            HashMap()
        val queue: Deque<N> = ArrayDeque(startNodes)
        while (!queue.isEmpty()) {
            val node = queue.first
            var details =
                seenNodes[node]
            if (details == null) { // Have not visited this node yet. Push its successors onto the queue in front of this node and visit
// them
                details =
                    NodeDetails(
                        node,
                        componentCount++
                    )
                seenNodes[node] = details
                components[details.component] = details
                val cacheValues = cachedNodeValues[node]
                if (cacheValues != null) { // Already visited this node
                    details.values = cacheValues
                    details.finished = true
                    queue.removeFirst()
                    continue
                }
                graph.getNodeValues(node, details.values, details.successors)
                for (connectedNode in details.successors) {
                    val connectedNodeDetails =
                        seenNodes[connectedNode]
                    if (connectedNodeDetails == null) { // Have not visited the successor node, so add to the queue for visiting
                        queue.addFirst(connectedNode)
                    } else if (!connectedNodeDetails.finished) { // Currently visiting the successor node - we're in a cycle
                        details.stronglyConnected = true
                    }
                    // Else, already visited
                }
            } else { // Have visited all of this node's successors
                queue.removeFirst()
                if (cachedNodeValues.containsKey(node)) {
                    continue
                }
                for (connectedNode in details.successors) {
                    val connectedNodeDetails =
                        seenNodes[connectedNode]
                    if (!connectedNodeDetails!!.finished) { // part of a cycle : use the 'minimum' component as the root of the cycle
                        val minSeen =
                            min(details.minSeen, connectedNodeDetails.minSeen)
                        details.minSeen = minSeen
                        connectedNodeDetails.minSeen = minSeen
                        details.stronglyConnected = true
                    }
                    details.values.addAll(connectedNodeDetails.values)
                    graph.getEdgeValues(node, connectedNode, details.values)
                }
                if (details.minSeen != details.component) { // Part of a strongly connected component (ie cycle) - move values to root of the component
// The root is the first node of the component we encountered
                    val rootDetails =
                        components[details.minSeen]
                    rootDetails!!.values.addAll(details.values)
                    details.values.clear()
                    rootDetails.componentMembers.addAll(details.componentMembers)
                } else { // Not part of a strongly connected component or the root of a strongly connected component
                    for (componentMember in details.componentMembers) {
                        cachedNodeValues[componentMember.node] = details.values
                        componentMember.finished = true
                        components.remove(componentMember.component)
                    }
                    if (details.stronglyConnected) {
                        strongComponents.add(details)
                    }
                }
            }
        }

        return startNodes.fold(linkedSetOf<T>()) { set, startNode ->
            set += cachedNodeValues[startNode] ?: emptySet()
            set
        }.also {
            if (cleanCache) {
                cachedNodeValues.clear()
            }
        }
    }

    private class NodeDetails<N, T>(val node: N, val component: Int) {
        var values: MutableSet<T> = LinkedHashSet()
        val successors: MutableList<N> = ArrayList()
        val componentMembers: MutableSet<NodeDetails<N, T>> = linkedSetOf(this)
        var minSeen: Int = component
        var stronglyConnected = false
        var finished = false
    }

    private class GraphWithEmptyEdges<N, T>(private val graph: DirectedGraph<N, T>) :
        DirectedGraphWithEdgeValues<N, T> {
        override fun getEdgeValues(
            from: N,
            to: N,
            values: MutableCollection<in T>
        ) {
        }

        override fun getNodeValues(
            node: N,
            values: MutableCollection<in T>,
            connectedNodes: MutableCollection<in N>
        ) {
            graph.getNodeValues(node, values, connectedNodes)
        }
    }
}