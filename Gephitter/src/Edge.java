/**
 * @author Alvaro Martinez
 *
 * Data class, represents an edge in the graph
 *
 */
public class Edge {
	/**
	 * origen	Long identifier of the source node in the edge
	 * destino	Long identifier of the target node in the edge
	 * weight	The weight of the edge. 
	 */
	private Long origen;
	private Long destino;
	private int weight;
	
	public Edge(Long source, Long target)
	{
		this.origen = source;
		this.destino = target;
		this.weight = 1;
	}
	
	/**
	 * Returns the identifier of the source node
	 */
	public Long getSource()
	{
		return this.origen;
	}
	
	/**
	 * Returns the identifier of the target node
	 */
	public Long getTarget()
	{
		return this.destino;
	}
	
	/**
	 * Increase the weight of the edge
	 */
	public void add_Weight()
	{
		this.weight++;
	}
	
	/**
	 * Returns the weight of the edge
	 */
	public int getWeight()
	{
		return this.weight;
	}

}
