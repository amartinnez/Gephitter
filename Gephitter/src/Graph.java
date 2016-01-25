/**
 * @autor Alvaro Martinez
 */

import java.util.ArrayList;

/**
 * Data class, represents the graph
 */
public class Graph {
	/**
	 * Nodes_List		List of nodes in the graoh
	 * Edges_List		List of edges in the graph
	 * Id_Nodes_List	List of indentifiers of the nodes.
	 * nNodes		Number of nodes
	 * nEdges		Number of edges
	 */
	private ArrayList<Node> Nodes_List;
	private ArrayList<Edge> Edges_List;
	private ArrayList<Long> Id_Nodes_List;
	private int nNodes;
	private int nEdges;
	
	public Graph()
	{
		this.Nodes_List = new ArrayList<Node>();
		this.Edges_List = new ArrayList<Edge>();
		this.Id_Nodes_List = new ArrayList<Long>();
		
		this.nNodes = 0;
		this.nEdges = 0; 
	}
	
	/**
	 * Add a new node to the graph.
	 */
	public void addNode(Node node)
	{
		this.Nodes_List.add(node);
		this.nNodes++;
		this.Id_Nodes_List.add(node.getId()); //Node and identifier at same position in both lists.
	}
	
	/**
	 * Add a new Edge to the graph.
	 */
	public void addEdge(Edge arc)
	{
		this.Edges_List.add(arc);
		this.nEdges++;
	}
	
	/**
	 * Returns the position of the node in nodes list specified by its identifier
	 */
	public int indexId(Long id)
	{
		return Id_Nodes_List.indexOf(id);
	}
	
	/**
	 * Returns the node specified by its position in nodes list
	 */
	public Node getNode(int i)
	{
		
		return Nodes_List.get(i);
	}
	
	/**
	 * Returns the number of nodes in the graph
	 */
	public int getNumberOfNodes()
	{
		return this.nNodes;
	}
	
	/**
	 * Returns the edge specified by its position in edges list
	 */
	public Edge getEdge(int i)
	{
		return this.Edges_List.get(i);
	}
	
	/**
	 * Returns the number of edges in the graph
	 */
	public int getNumberOfEdges()
	{
		return this.nEdges;
	}
}
