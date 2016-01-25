/**
 * @author Alvaro Martinez
 */

import java.util.ArrayList;

/**
 * Data class, represents a node in the graph
 */
public class Node {

	/**
	 * nodoID			Long identifier of twitter user
	 * Name				ScreenName of twitter user
	 * In_Links			Identifiers of In neighbors of the node 
	 * Out_Links		Identifiers of Out neighbors of the node
	 * Index_In_Links	Position of the edge in the list of edges of the graph
	 * Index_Out_Links	Position of the edge in the list of edges of the graph
	 */
	private Long nodoID;
	private String Name;
	private ArrayList<Long> In_Links;
	private ArrayList<Long> Out_Links;
	private ArrayList<Integer> Index_In_Links;
	private ArrayList<Integer> Index_Out_Links;
	
	
	public Node(Long id, String ScreenName)
	{
		this.nodoID = id;
		this.Name = ScreenName;
		this.In_Links = new ArrayList<Long>();
		this.Out_Links = new ArrayList<Long>();
		this.Index_In_Links = new ArrayList<Integer>();
		this.Index_Out_Links = new ArrayList<Integer>();
	}
	
	
	/**
	 * Set the identifier of the Node.
	 */
	public void setNodeId(Long id)
	{
		this.nodoID = id;
	}
	
	/**
	 * Add a new In neighbor
	 */
	public void setInLink(Long id, int index)
	{
		this.In_Links.add(id);
		this.Index_In_Links.add(index);
	}
	
	/**
	 * Add a new Out neighbor
	 */
	public void setOutLink(Long id, int index)
	{
		this.Out_Links.add(id);
		this.Index_Out_Links.add(index);
	}
	
	/**
	 * Returns the identifier of the In neighbor specified by his position in the list
	 */
	public Long getInLink(int i)
	{
		return this.In_Links.get(i);
	}
	
	/**
	 * Returns the identifier of the Out neighbor specified by its position in the list
	 */
	public Long getOutLink(int i)
	{
		return this.Out_Links.get(i);
	}
	
	/**
	 * Returns the position of the In neighbor specified by its identifier  
	 */
	public int indexInLink(Long id)
	{
		return In_Links.indexOf(id);
	}
	
	/**
	 * Returns the position of the Out neighbor specified by its identifier  
	 */
	public int indexOutLink(Long id)
	{
		return Out_Links.indexOf(id);
	}
	
	/**	
	 * Returns the size of the In neighbors list
	 */
	public int In_Links_size()
	{
		return this.In_Links.size();
	}
	
	/**	
	 * Returns the size of the Out neighbors list
	 */
	public int Out_Links_size()
	{
		return this.Out_Links.size();
	}
	
	/**
	 * Returns the identifier of the node
	 */
	public Long getId()
	{
		return this.nodoID;
	}
	
	/**
	 * Returns the ScreenName of the node
	 */
	public String getName()
	{
		return this.Name;
	}
	
	/**
	 * Returns the position of the edge in the list of edges of the graph, specified by its pos in the list of In neighbors of the node.
	 */
	public int getIndexIn(int pos)
	{
		return this.Index_In_Links.get(pos);
	}
	
	/**
	 * Returns the position of the edge in the list of edges of the graph, specified by its pos in the list of Out neighbors of the node.
	 */
	public int getIndexOut(int pos)
	{
		return this.Index_Out_Links.get(pos);
	}
	
}
