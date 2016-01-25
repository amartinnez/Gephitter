/**
 * @author Alvaro Martinez
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import twitter4j.Status;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

/**
 * This class implements all methods to generate graphs and GEXF files from a list of tweets. 
 * Includes a method to read the Oauth credentials from a file and set the configuration builder.
 */
public class Methods {

	public Methods(){}
	
	/**
	 * Returns the configuration builder to enable an instance of Twitter object.
	 * 
	 * @param file_name		Name of the file that contains the ConsumerKey, ConsumerSecret, AccesToken 
	 * 						and AccessTokenSecret of your registered app in https://apps.twitter.com/
	 */
	public ConfigurationBuilder Credentials(String file_name) 
	{
		ConfigurationBuilder configBuilder = new ConfigurationBuilder();
		String Consumer = new String();
		String ConsumerSecret = new String();
		String Token = new String();
		String TokenSecret = new String();
		File archivo = null;
		FileReader fileR = null;
		BufferedReader lectura = null;
		try {
			archivo = new File(file_name);
			fileR = new FileReader(archivo);
			lectura = new BufferedReader(fileR);
			String linea = new String();
			int n = 1;
			while ((linea = lectura.readLine()) != null) {
				if (n == 1)
					Consumer = linea;
				else if (n == 2) 
					ConsumerSecret = linea;
				else if(n == 3)
					Token = linea;
				else if(n == 4)
					TokenSecret = linea;			
				n++;
			}
		} catch (Exception e) {
			System.out.println("Error opening "+file_name);
			System.exit(2);
		} finally {
			try {
				if (null != fileR) {
					fileR.close();
				}
			} catch (Exception e2) {
				System.out.println("Error closing "+file_name);
				System.exit(2);

			};
		}
		configBuilder.setDebugEnabled(true)
		.setOAuthConsumerKey(Consumer)
		.setOAuthConsumerSecret(ConsumerSecret)
		.setOAuthAccessToken(Token)
		.setOAuthAccessTokenSecret(TokenSecret);
		return configBuilder;
	}

	/**
	 * Returns the retweets graph from the list of retweets of a given query or hashtag.
	 * 
	 * @param retweets		List of collected retweets. 
	 * @param users			List of filtered nodes.		
	 */
	public Graph Net_Retweets(ArrayList<Status> retweets, ArrayList<Node> users)
	{
		Graph grafo = new Graph();
		if(users.size()>0)
		{
			for(Node usuario: users)
			{
				grafo.addNode(usuario);//Add filtered users to the graph
			}
		}

		Node nodo, nodo2;
		Edge arista;

		for(Status t: retweets)
		{
				int i_node_RT;
				int i_node_T;
				int i_in;
				int i_edge;
				User RT_user = t.getUser();
				User T_user = t.getRetweetedStatus().getUser();

				/**
				 * Its necessary to avoid duplicated nodes or edges in the graph.
				 */
				if( (i_node_T = grafo.indexId(T_user.getId())) == -1)
				{
					nodo = new Node(T_user.getId(),T_user.getScreenName());
					grafo.addNode(nodo);//Add a new node to the graph
				}
				else
				{
					nodo = grafo.getNode(i_node_T);//Get this node from the graph
				}

				if( (i_node_RT = grafo.indexId(RT_user.getId())) == -1)
				{
					nodo2 = new Node(RT_user.getId(), RT_user.getScreenName());
					grafo.addNode(nodo2);//Add a new node to the graph
				}
				else
					nodo2 = grafo.getNode(i_node_RT);//Get this node from the graph

				if( (i_in = nodo.indexInLink(RT_user.getId())) == -1 )
				{
					arista = new Edge(RT_user.getId(),T_user.getId());
					grafo.addEdge(arista);//Add a new edge to the graph
					i_edge = grafo.getNumberOfEdges() - 1;
					nodo.setInLink(RT_user.getId(), i_edge);
					nodo2.setOutLink(T_user.getId(), i_edge);
				}
				else
				{
					grafo.getEdge(nodo.getIndexIn(i_in)).add_Weight();//Increase the weight of this edge
				}
			}
		return grafo;
	}
	
	/**
	 * Returns the mentions graph of a given user, from the list of tweets.
	 * 
	 * @param tweets		List of collected tweets. This tweets are about an user in a given query or hashtag.
	 * @param usuario		Node of the mentioned user.	
	 */
	public Graph Net_Mentions_User(ArrayList<Status> tweets , Node usuario)
	{
		Graph grafo = new Graph();
		grafo.addNode(usuario);
		Node nodo;
		Edge arista;

		for(Status t: tweets)
		{		
			if( !(usuario.getId() == t.getUser().getId()) )
			{
				int i_in;
				int i_edge;
				User T_user = t.getUser();
				
				/**
				 * Its necessary to avoid duplicated nodes or edges in the graph.
				 */
				if( (i_in = usuario.indexInLink(T_user.getId())) ==-1 )
				{
					nodo = new Node(T_user.getId(),T_user.getScreenName());					
					arista = new Edge(T_user.getId(),usuario.getId());
					grafo.addEdge(arista);//Add a new edge to the graph
					i_edge = grafo.getNumberOfEdges() - 1;
					nodo.setOutLink(usuario.getId(),i_edge);
					usuario.setInLink(nodo.getId(),i_edge);				
					grafo.addNode(nodo);//Add a new node to the graph
				}
				else
				{
					i_edge = usuario.getIndexIn(i_in);
					arista = grafo.getEdge(i_edge);
					arista.add_Weight();//Increase the weight of this edge			
				}
			}
		}
		return grafo;
	}
	
	/**
	 * Returns the retweets subgraph of a given user, from the retweets graph. Filter the subgraph of the user.
	 * 
	 * @param usuario		Given user node.
	 * @param grafo			Retweets graph.
	 */
	public Graph Subnet_Retweets_User(Node usuario, Graph grafo)
	{
		Graph grafo_usuario = new Graph();
		grafo_usuario.addNode(usuario);
		
		for(int i=0; i<usuario.In_Links_size();i++)
		{
			grafo_usuario.addNode(grafo.getNode(grafo.indexId(usuario.getInLink(i))));
			grafo_usuario.addEdge(grafo.getEdge(usuario.getIndexIn(i)));
		}
		
		for(int i=0; i<usuario.Out_Links_size();i++)
		{
			grafo_usuario.addNode(grafo.getNode(grafo.indexId(usuario.getOutLink(i))));
			grafo_usuario.addEdge(grafo.getEdge(usuario.getIndexOut(i)));
		}
		
		return grafo_usuario;
	}
	
	/**
	 * Returns a graph generated from the union of filtered subgraphs.
	 * 
	 * @param graphs	List of filtered subgraphs.
	 */
	public Graph Graph_Union(ArrayList<Graph> graphs)
	{
		Graph grafo = new Graph();
		
		for(int j = 0; j<graphs.get(0).getNumberOfNodes();j++)
		{
			grafo.addNode(graphs.get(0).getNode(j));
		}
		
		for(int k=0;k<graphs.get(0).getNumberOfEdges();k++)
		{
			grafo.addEdge(graphs.get(0).getEdge(k));
		}
		
		for(int i=1;i<graphs.size();i++)
		{
			for(int j=0;j<graphs.get(i).getNumberOfNodes();j++)
			{
				if( grafo.indexId(graphs.get(i).getNode(j).getId()) == -1)
				{
					grafo.addNode(graphs.get(i).getNode(j));
				}
			}
			for(int k = 0; k<graphs.get(i).getNumberOfEdges();k++)
			{
				grafo.addEdge(graphs.get(i).getEdge(k));
			}
		}
		return grafo;
	}
	
	/**
	 * Generate the GEXF file from the graph.
	 */
	public void Graph_To_GEXF(Graph grafo, String nombreFichero)
	{
		Node  nodo;
		Edge arista;
		FileWriter fichero = null;
        PrintWriter writer = null;
        try
        {
            fichero = new FileWriter("./"+nombreFichero+".gexf");
            writer = new PrintWriter(fichero);
            writer.println("<gexf >\n<meta >\n<creator>socialGephi</creator>\n</meta>");
            writer.println("<graph defaultedgetype=\"directed\" idtype=\"string\" type=\"static\">");
            writer.println("<nodes count=\""+grafo.getNumberOfNodes()+"\">");
    		
    		for(int i = 0 ; i < grafo.getNumberOfNodes() ; i++)
    		{
    			nodo = grafo.getNode(i);
    			writer.println("<node id=\""+nodo.getId()+"\" label=\"@"+nodo.getName()+"\"/>");
    		}
    		writer.println("</nodes>");
    		
    		writer.println("<edges count=\""+grafo.getNumberOfEdges()+"\">");
    		for(int i = 0; i < grafo.getNumberOfEdges() ; i++)
    		{
    			arista = grafo.getEdge(i);
    			writer.println("<edge id=\""+i+"\" source=\""+arista.getSource()+"\" target=\""+arista.getTarget()+"\" weight=\""+arista.getWeight()+"\"/>");
    		}
    		writer.println("</edges>\n</graph>\n</gexf>");
 
        } catch (Exception e) {
        	System.out.println("Error al crear el fichero "+nombreFichero+".gexf");
        	System.exit(2);
        } finally {
        	try {
        		if (null != fichero)
        			fichero.close();
        	} catch (Exception e2) {
        		System.out.println("Error al cerrar el fichero "+nombreFichero+".gexf");
        		System.exit(2);
        	}
        }	
	}

	/**
	 * Returns the list of nodes of the graph sorted by in degree value of each node.
	 */
	public ArrayList<Node> Sort_In_Degree(Graph graph)
	{
		ArrayList<Node> list = new ArrayList<Node>();
		list.add(graph.getNode(0));
		
		
		for(int i=1;i<graph.getNumberOfNodes();i++)
		{
			int j;
			for(j=0;j<list.size();j++)
			{
				if(graph.getNode(i).In_Links_size() > list.get(j).In_Links_size())
				{
					list.add(j, graph.getNode(i));
					j= list.size() + 1;
				}
			}
			if( j == list.size())
			{
				list.add(graph.getNode(i));
			}
		}
		
		return list;
	}
	
	/**
	 * Returns the list of nodes of the graph sorted by out degree value of each node.
	 */
	public ArrayList<Node> Sort_Out_Degree(Graph graph)
	{
		ArrayList<Node> list = new ArrayList<Node>();
		list.add(graph.getNode(0));
		
		
		for(int i=1;i<graph.getNumberOfNodes();i++)
		{
			int j;
			for(j=0;j<list.size();j++)
			{
				if(graph.getNode(i).Out_Links_size() > list.get(j).Out_Links_size())
				{
					list.add(j, graph.getNode(i));
					j= list.size() + 1;
				}
			}
			if( j == list.size())
			{
				list.add(graph.getNode(i));
			}
		}
		
		return list;
	}
	
	/**
	 * Returns the list of nodes of the graph, sorted by total degree value of each node.
	 */
	public ArrayList<Node> Sort_Total_Degree(Graph graph)
	{
		ArrayList<Node> list = new ArrayList<Node>();
		list.add(graph.getNode(0));
		
		
		for(int i=1;i<graph.getNumberOfNodes();i++)
		{
			int j;
			for(j=0;j<list.size();j++)
			{
				if( (graph.getNode(i).In_Links_size() + graph.getNode(i).Out_Links_size())  > (list.get(j).In_Links_size() + list.get(j).Out_Links_size()) )
				{
					list.add(j, graph.getNode(i));
					j= list.size() + 1;
				}
			}
			if( j == list.size())
			{
				list.add(graph.getNode(i));
			}
		}		
		return list;
	}
	
	/**
	 * Create a file with information about the rankings of nodes by their degree values. Shows the degree centrality of the graph.
	 * 
	 * @param Total_List	Sorted list of nodes by total degree.
	 * @param In_List		Sorted list of nodes by in degree.
	 * @param Out_List		Sorted list of nodes by out degree.
	 * @param fileName		Name of the new file.
	 */
	public void Ranking_Degree_Lists(ArrayList<Node> Total_List, ArrayList<Node> In_List, ArrayList<Node> Out_List, String fileName)
	{
		FileWriter File = null;
		PrintWriter writer = null;
		String nameFile = "DC_";
		nameFile = nameFile.concat(fileName+".txt");

		try
		{
			File = new FileWriter(nameFile);
			writer = new PrintWriter(File);

			writer.println("Degree Sorted Rankings\n");
			writer.format("%10s%35s%35s%35s","Position", "Total Degree", "In Degree", "Out Degree");
			writer.println();
			writer.println();


			for(int i=0;i<Total_List.size();i++)
			{
				writer.format("%10s%35s%35s%35s", (i+1), Total_List.get(i).getName(), In_List.get(i).getName(), Out_List.get(i).getName());
				writer.println();
			}
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != File)
					File.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

		System.out.println("DegreeCentrality:  \t "+nameFile);
	}
	
	/**
	 * Gets and returns the adjacency matrix of the graph.
	 */
	public int[][] Adyacency_Matrix(Graph graph)
	{
		int size = graph.getNumberOfNodes();
		int[][] matrix = new int[size][size];		
		for(int i=0; i<size ;i++)
		{
			for(int j=0; j<size ;j++)
			{
				matrix[i][j] = 0;
			}
		}
		
		for(int i=0;i<size;i++)
		{
			Node nodo = graph.getNode(i);
			for(int j=0;j< nodo.In_Links_size();j++)
			{
				int index = graph.indexId(nodo.getInLink(j));
				try
				{
					matrix[i][index] = 1;
				} catch (Exception e) {
					System.err.print("Error with the Adyacency Matrix of the graph");
					System.exit(5);
				}
			}
		}	
		return matrix;
	}
	
	/**
	 * Returns the eigenvector of the adjacency matrix of the graph. Implements the Power iteration method.
	 */
	public Double[] EigenVector(int[][] matrix, int size)
	{
		Double norm0 = 0.0;
		Double norm1 = 1.0;
		Double eps = 0.0000000001;
		Double[] vector = new Double[size];
		Double[] temp = new Double[size];
		Double acc1=(double) 0;
		Double acc2=(double) 0;
		for(int i=0;i<size;i++)
		{
			vector[i] =  (double) (1.0/size);
			temp[i] =(double) 0.0;
		}		
		while( Math.abs(norm0 - norm1) > eps)
		{
			norm0 = norm1;
			acc2 = 0.0;
			for(int i=0;i<size;i++)
			{	acc1 = 0.0;
				for(int j=0;j<size;j++)
				{
					acc1 = acc1+matrix[i][j]*vector[j];
				}
				temp[i] = acc1;
				acc2 = acc2 + temp[i]*temp[i];
			}			
			norm1 = Math.sqrt(acc2);
			for(int i=0;i<size;i++)
			{
				vector[i] = temp[i]/norm1;
			}
		}	
		return vector;
	}
	
	/**
	 * Create a file with information about the ranking of nodes by their eigenvector values.
	 */
	public void Ranking_EigenVector(Double[] eigenVector, Graph graph,String fileName)
	{
		ArrayList<Double> sorted = new ArrayList<Double>();
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		sorted.add(eigenVector[0]);
		indexes.add(0);
		Double acc=0.0;
		FileWriter File = null;
		PrintWriter writer = null;
		for(int i=1;i<eigenVector.length;i++)
		{
			int j;
			for(j=0;j<sorted.size();j++)
			{
				if(eigenVector[i] > sorted.get(j))
				{
					sorted.add(j, eigenVector[i]);
					indexes.add(j, i);
					j = sorted.size() + 1;
				}
			}
			if(j == sorted.size())
			{
				sorted.add(eigenVector[i]);
				indexes.add(i);
			}
		}
		for(Double v:eigenVector)
		{
			acc = acc + v;
		}
		
		String nameFile = "EC_";
		nameFile = nameFile.concat(fileName+".txt");
		
		try
		{
			File = new FileWriter(nameFile);
			writer = new PrintWriter(File);

			writer.println("Sorted List of Influencers\n");
			writer.format("%10s%35s%10s","Position", "ScreenName", "(%)");
			writer.println();
			writer.println();
			for(int j = 0; j < sorted.size() ; j++)
			{
				Double percent = ((sorted.get(j)/acc)*100);
				writer.format("%10s%35s%10s\n", (j+1),graph.getNode(indexes.get(j)).getName(),String.format("%.2f",percent));
			}
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != File)
					File.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		System.out.println("EigenCentrality:   \t "+nameFile+"\n");
	}
	
	/**
	 * Show information when a GEXF file is created.
	 */
	public void print_details(int nNodes, int nEdges, String file_name)
	{
		System.out.println("\n\nGephi File:        \t "+file_name+".gexf");
		System.out.println("Number of Nodes:   \t "+nNodes);
		System.out.println("Number of Edges:   \t "+nEdges);
	}
	
	/**
	 * Check valid format date.
	 */
	public boolean isDateValid(String date) 
	{
		final String DATE_FORMAT = "yyyy-mm-dd";
	        try {
	            DateFormat df = new SimpleDateFormat(DATE_FORMAT);
	            df.setLenient(false);
	            df.parse(date);
	            return true;
	        } catch (ParseException e) {
	            return false;
	        }
	}
	
	/**
	 * Returns the position in the graph of a given node by its ScreenName. If there is no node in the graph with that ScreenName, returns -1.
	 */
	public int FindUserID(String ScreenName, Graph graph)
	{
		for(int i=0;i<graph.getNumberOfNodes();i++)
		{
			if(ScreenName.equals(graph.getNode(i).getName()))
			{
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Shows information about a tweet. 
	 */
	public void Print_Tweets(ArrayList<Status> tweets)
	{
		for(Status t: tweets)
		{
			System.out.println("@" + t.getUser().getScreenName() + ":" + t.getText());
		}
	}

}

