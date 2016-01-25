/**
 * @author Alvaro Martinez
 */

import java.util.ArrayList;
import twitter4j.Status;
import twitter4j.conf.ConfigurationBuilder;

/**
 * This program tests the methods and classes implemented in the library gephitterlib.jar
 * 
 * You can also use this program to generate GEXF files of retweets or mentions graphs about Twitter hashtags. 
 * 
 * Help options are available to show the usage and utilities of this program.
 *
 */
public class Gephitter_Main {

	public static void main(String arg[]) {
	
		ArrayList<Status> listaTweets = new ArrayList<Status>();
		Methods operacion = new Methods();
		Long user_id;
		int i_node;
		ConfigurationBuilder cb;
		Node user;
		ArrayList<Node> users = new ArrayList<Node>();
		int n = arg.length;
		int max=n;
		String consulta = "";
		String from = "";
		String to = "";
		String file_name = "";
		Graph user_graph = new Graph();
		Graph union_graphs = new Graph();
		ArrayList<Graph> users_graphs = new ArrayList<Graph>();
		
		if(n==1 && (arg[0].equals("-h") || arg[0].equals("--help")))
		{
			System.out.println("\nUSAGE:\n\t    gephitterlib.jar  <-REST|-STREAM> <-RT|-ME> <Hashtag> [User ...] [-FROM <YYYY-MM-DD> [-TO <YYYY-MM-DD>]]" );
			System.out.println("\nOPTIONS:\n\t    -REST          Communication to Twitter REST API" );
			System.out.println("\n\t    -STREAM        Communication to Twitter Streaming API" );
			System.out.println("\n\t    -RT            Generate GEXF files that represent Nets of Retweets " );
			System.out.println("\n\t    -ME            Generate GEXF files that represent Nets of Mentions" );
			System.out.println("\n\t    -FROM          Specifies from date, in last two weeks, to get tweets" );
			System.out.println("\n\t    -TO            Specifies a final date to get tweets." );
			System.out.println("\nQuery:\n\t     Hashtag       The hashtag you want to get tweets" );
			System.out.println("\nSubnets:\n\t     USER          Twitter user ScreenName. Generate a GEXF file with the filtered net of this user\n" );
			

			System.exit(0);
		}
		
		if(n < 3)
		{
			System.err.print("Error: Incorrect number of arguments. At least 3 arguments.\n");
			System.exit(1);
		}

		String mode = arg[0];
		String net_type = arg[1];


		if(!mode.equals("-REST") && !mode.equals("-STREAM"))
		{
			System.err.print("Error: First argument must be -REST or -STREAM\n");
			System.exit(1);
		}

		if(!net_type.equals("-RT") && !net_type.equals("-ME"))
		{
			System.err.print("Error: Second argument must be -RT or -ME\n");
			System.exit(1);
		}

		if(n > 3)
		{
			if(arg[n-4].equals("-FROM") && arg[n-2].equals("-TO"))
			{
				if(!mode.equals("-STREAM"))
				{
					if(operacion.isDateValid(arg[n-3]) && operacion.isDateValid(arg[n-1]))
					{
						from = arg[n-3];
						to = arg[n-1];
						max = (n - 4);						
					}
					else
					{
						System.err.print("Error: Incorrect date (yyyy-mm-dd)\n");
						System.exit(1);
					}	
				}
				else
				{
					System.err.print("Error: Stream mode does not admit dates\n");
					System.exit(1);
				}
			}
			else if(arg[n-2].equals("-FROM"))
			{
				if(!mode.equals("-STREAM"))
				{
					if(operacion.isDateValid(arg[n-1]))
					{
						from = arg[n-1];
						to = "";
						max = (n - 2);
					}
					else
					{
						System.err.print("Error: Incorrect date (yyyy-mm-dd)\n");
						System.exit(1);
					}
				}
				else
				{
					System.err.print("Error: Streaming option does not admit dates\n");
					System.exit(1);
				}
			}
		}
		
		cb = operacion.Credentials("./auth_file.txt");
		
		if(mode.equals("-REST"))
		{
			Rest_API REST_API = new Rest_API(cb);
			for(int i = 3;i < max;i++)
			{
				user_id = REST_API.getId_From_ScreenName(arg[i]);
				if(user_id == 0)
				{
					System.err.print("Error: Twitter user "+arg[2]+" does not exist.\n");
					System.exit(1);
				}
				else if(user_id == -1)
				{
					System.err.print("Error: Twitter user "+arg[2]+" restrictions.\n");
					System.exit(1);
				}
				else
				{
					user = new Node(user_id,arg[i]);
					users.add(user);
				}
			}
			if(net_type.equals("-RT"))
			{
				consulta = consulta.concat(arg[2]);
				listaTweets = REST_API.getReTweets(consulta, from, to);				
				if(listaTweets.size() == 0) 
				{
					System.err.print("\nError REST_API: 0 Tweets\n");
					System.exit(1);
				}
				file_name = file_name.concat("R_RT_#"+consulta+"#");
				Graph grafo_retweets = new Graph();
				grafo_retweets = operacion.Net_Retweets(listaTweets, users);
				String tempName = "";
				tempName = file_name.concat("_"+listaTweets.size());
				operacion.Graph_To_GEXF(grafo_retweets, tempName);
				operacion.print_details(grafo_retweets.getNumberOfNodes(), grafo_retweets.getNumberOfEdges(), tempName);
				operacion.Ranking_Degree_Lists(operacion.Sort_Total_Degree(grafo_retweets), operacion.Sort_In_Degree(grafo_retweets),
											   operacion.Sort_Out_Degree(grafo_retweets), tempName);
				//operacion.Ranking_EigenVector(operacion.EigenVector(operacion.Adyacency_Matrix(grafo_retweets), grafo_retweets.getNumberOfNodes()), 
				//							  grafo_retweets, tempName);
				
				for(Node u: users)
				{
					user_graph = operacion.Subnet_Retweets_User(u, grafo_retweets);
					String tempName2 = new String();
					tempName2 = file_name.concat("_@"+user_graph.getNode(0).getName()+"@_"+listaTweets.size());
					operacion.Graph_To_GEXF(user_graph, tempName2);
					operacion.print_details(user_graph.getNumberOfNodes(), user_graph.getNumberOfEdges(), tempName2);
					operacion.Ranking_Degree_Lists(operacion.Sort_Total_Degree(user_graph), operacion.Sort_In_Degree(user_graph),
							                       operacion.Sort_Out_Degree(user_graph), tempName2);
					//operacion.Ranking_EigenVector(operacion.EigenVector(operacion.Adyacency_Matrix(user_graph), user_graph.getNumberOfNodes()), 
					//		                      user_graph, tempName2);
					users_graphs.add(user_graph);
				}

			}
			else if(net_type.equals("-ME"))
			{
				consulta = consulta.concat(arg[2]);

				if(users.size() > 0)
				{
					file_name = file_name.concat("R_ME_#"+consulta+"#");
					for(Node u: users)
					{
						String question = new String();
						question = consulta.concat(" "+u.getName());
						listaTweets.clear();
						listaTweets = REST_API.getTweets(question, from, to);
						if(listaTweets.size() == 0) 
						{
							System.err.print("\nError REST_API: 0 Tweets\n");
							System.exit(1);
						}
						user_graph = operacion.Net_Mentions_User(listaTweets, u);
						String tempName2 = new String();
						tempName2 = file_name.concat("_@"+user_graph.getNode(0).getName()+"@_"+listaTweets.size());
						operacion.Graph_To_GEXF(user_graph, tempName2);
						operacion.print_details(user_graph.getNumberOfNodes(), user_graph.getNumberOfEdges(), tempName2);
						operacion.Ranking_Degree_Lists(operacion.Sort_Total_Degree(user_graph), operacion.Sort_In_Degree(user_graph),
			                                           operacion.Sort_Out_Degree(user_graph), tempName2);
						//operacion.Ranking_EigenVector(operacion.EigenVector(operacion.Adyacency_Matrix(user_graph), user_graph.getNumberOfNodes()), 
			            //                              user_graph, tempName2);
						users_graphs.add(user_graph);
						
						users_graphs.add(user_graph);
					}
				}
				else
				{
					System.err.print("\nError:  In rest mode, mentions are about a hashtag and at least 1 user.\n");
					System.exit(1);
				}

			}
		}
		
		
		if(mode.equals("-STREAM"))
		{
			if(net_type.equals("-RT"))
			{
				consulta = consulta.concat(arg[2]);
				file_name = file_name.concat("S_RT_#"+consulta+"#");
				Stream_API STREAM_API = new Stream_API(cb);
				listaTweets = STREAM_API.getReTweets(consulta);
				if(listaTweets.size() == 0) 
				{
					System.err.print("\nError STREAM_API: Got 0 Tweets\n");
					System.exit(1);
				}
				Graph grafo_retweets = new Graph();
				grafo_retweets = operacion.Net_Retweets(listaTweets, users);
				String tempName = "";
				tempName = file_name.concat("_"+listaTweets.size());
				operacion.Graph_To_GEXF(grafo_retweets, file_name+"_"+listaTweets.size());
				operacion.print_details(grafo_retweets.getNumberOfNodes(), grafo_retweets.getNumberOfEdges(), tempName);
				operacion.Ranking_Degree_Lists(operacion.Sort_Total_Degree(grafo_retweets), operacion.Sort_In_Degree(grafo_retweets),
						                       operacion.Sort_Out_Degree(grafo_retweets), tempName);
				//operacion.Ranking_EigenVector(operacion.EigenVector(operacion.Adyacency_Matrix(grafo_retweets), grafo_retweets.getNumberOfNodes()), 
				//		                      grafo_retweets, tempName);
				

				for(int i=3;i<n;i++)
				{
					i_node = operacion.FindUserID(arg[i], grafo_retweets);
					if(i_node != -1)
					{
						user = grafo_retweets.getNode(i_node);
						users.add(user);
					}
				}
				
				for(Node u: users)
				{
					user_graph = operacion.Subnet_Retweets_User(u, grafo_retweets);
					String tempName2 = new String();
					tempName2 = file_name.concat("_@"+user_graph.getNode(0).getName()+"@_"+listaTweets.size());
					operacion.Graph_To_GEXF(user_graph, tempName2);
					operacion.print_details(user_graph.getNumberOfNodes(), user_graph.getNumberOfEdges(), tempName2);
					operacion.Ranking_Degree_Lists(operacion.Sort_Total_Degree(user_graph), operacion.Sort_In_Degree(user_graph),
							                       operacion.Sort_Out_Degree(user_graph), tempName2);
					//operacion.Ranking_EigenVector(operacion.EigenVector(operacion.Adyacency_Matrix(user_graph), user_graph.getNumberOfNodes()), 
					//		                      user_graph, tempName2);
					users_graphs.add(user_graph);
				}
			}
			else if(net_type.equals("-ME"))
			{
				if(n!=4)
				{
					System.err.print("\nError: In stream mode, mentions are about a hashtag and only 1 user.\n");
					System.exit(1);
				}
				else
				{
					Stream_API STREAM_API = new Stream_API(cb);
					consulta = consulta.concat(arg[2]+" "+arg[3]);
					listaTweets = STREAM_API.getTweets(consulta);
					if(listaTweets.size() == 0) 
					{
						System.err.print("\nError STREAM_API: Got 0 Tweets\n");
						System.exit(1);
					}
					file_name = file_name.concat("S_ME_#"+arg[2]+"#");
					user = new Node((long) 0,arg[3]);
					user_graph = operacion.Net_Mentions_User(listaTweets, user);
					String tempName2 = new String();
					tempName2 = file_name.concat("_@"+user_graph.getNode(0).getName()+"@_"+listaTweets.size());
					operacion.Graph_To_GEXF(user_graph, tempName2);
					operacion.print_details(user_graph.getNumberOfNodes(), user_graph.getNumberOfEdges(), tempName2);
					operacion.Ranking_Degree_Lists(operacion.Sort_Total_Degree(user_graph), operacion.Sort_In_Degree(user_graph),
		                                           operacion.Sort_Out_Degree(user_graph), tempName2);
					//operacion.Ranking_EigenVector(operacion.EigenVector(operacion.Adyacency_Matrix(user_graph), user_graph.getNumberOfNodes()), 
		            //                              user_graph, tempName2);
				}
			}
		}
		
		if(users.size()>1)
		{
			union_graphs = operacion.Graph_Union(users_graphs);
			String name_file = new String();
			name_file = name_file.concat(file_name);
			for(Node u: users)
				name_file = name_file.concat("_@"+u.getName()+"@");

			name_file = name_file.concat("_UNION");
			operacion.Graph_To_GEXF(union_graphs, name_file);
			operacion.print_details(union_graphs.getNumberOfNodes(), union_graphs.getNumberOfEdges(), name_file);
		}
		System.out.println("\n\n");
	}
}