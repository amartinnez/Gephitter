/**
 * @author Alvaro Martinez
 */

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Communication to Twitter Rest API. Get all tweets of a given query
 */
public class Rest_API {
	/**
	 * twitter	Object from twitter4j to enable an authenticated communication. Allows to make requests and get returned data from the API
	 * salir	Variable to stop the process of getting tweets.
	 * reader	Read stdin.
	 * input	Store the stdin lecture
	 */
	private Twitter twitter;
	private boolean salir = false;
	private BufferedReader reader;
	private String input;

	/**
	 * @param cb	Needs a valid configuration builder to get an instance of the twitter object. This cb is associated to OAuth credentials.
	 * 				The method credentials in class Methods.java configure this cb from a file that must contain the 
	 * 				ConsumerKey, ConsumerSecret, AccesToken and AccesTokenSecet of your registered app in https://apps.twitter.com/			
	 */
	public Rest_API(ConfigurationBuilder cb)
	{
		this.twitter = new TwitterFactory(cb.build()).getInstance();
		this.reader = new BufferedReader(new InputStreamReader(System.in));
	}

	/**
	 * Returns a list containing all tweets of a given query or hashtag. Date options are available.
	 * 
	 * @param consulta	The query or hashtag.
	 * @param from		Date (yyyy-mm-dd) within last twos weeks. 
	 * @param to		To date
	 */
	public ArrayList<Status> getTweets(String consulta, String from, String to) {
		Query query = new Query(consulta);
		if(!to.equals(""))
		{
			query.since(from);
			query.until(to);
		}
		else if(!from.equals(""))
		{
			query.since(from);
		}
		int last = 0;
		long lastID = Long.MAX_VALUE;
		ArrayList<Status> tweets = new ArrayList<Status>();
		
		salir = false;
		System.out.println("\n");
		while (salir==false)
		{	
			query.setCount(100); //Twitter Rest API returns only 100 tweets per request
			try {
				
				QueryResult result = twitter.search(query);
				tweets.addAll(result.getTweets());
				System.out.print("\r("+consulta+") Got: "+ tweets.size()+" Tweets\t");
				
				if(last==tweets.size())
					salir = true;
				else
				{
					last = tweets.size();
					for (Status t: result.getTweets()) 
						if(t.getId() < lastID) 
							lastID = t.getId();//In next request, continue getting tweets before this id.

					query.setMaxId(lastID-1);
					this.check_quit();//Check if user wants to stop the process of getting tweets.
				}
								
			}
			catch (TwitterException te) 
			{
				this.check_error(te);//Check if error is about communication, credentials, rate limits...
			}; 
		}
		return tweets;
	}
	
	/**
	 * Check if user wants to stop without losing collected tweets. 
	 * 
	 * Press q and Enter Key to quit.
	 */
	public void check_quit()
	{
		try {
			if(reader.ready())
			{
				try {
					input = reader.readLine();
				} catch (IOException e) {
					System.exit(3);
				}
				if (input.equals("q"))
				{
					this.salir = true;

				}
			}
		} catch (IOException e) {
			System.exit(3);
		}
	}
	
	/**
	 * Errors with Twitter Rest API. To get all tweets, its necessary to handle some of these errors.
	 */
	public void check_error(TwitterException te)
	{

		if(te.exceededRateLimitation())//Rate Limits. Wait the remaining time until next rate window
		{
			System.out.print("\nError REST_API: Rate Limits . Continue after "+te.getRateLimitStatus().getSecondsUntilReset()+" seconds.");
			int remain = ((te.getRateLimitStatus().getSecondsUntilReset() +1)*1000 );
			while(remain > 0){
				System.out.print("\rError REST_API: Rate Limits . Continue after "+((remain/1000)-1)+" seconds.");
				this.check_quit();
				if(salir)
					remain = -1;				
				try {
					Thread.sleep(1000);
					remain = remain - 1000;
					continue;
				} catch (InterruptedException e) {
					System.exit(2);
				}
			}
			System.out.println("\n");
		}
		else if(te.isCausedByNetworkIssue())//Network Issue. Sleep 5 seconds and try again.
		{
			System.out.println("\nError REST_API: Conection failed. New Try after 5 seconds.");
			try {
				Thread.sleep(5000);
				this.check_quit();
			} catch (InterruptedException e) {
				System.exit(2);
			}
		}
		else if(te.getErrorCode() == 32)//Fatal error. Problem with credentials.
		{
			System.out.println("\nError REST_API: Credentials Fail");
			System.exit(1);
		}
		else if(te.getErrorCode() == 34)//Invalid query o hashtag
		{
			System.out.println("\nError REST_API: Not valid query");
			System.exit(1);
		}
		else
		{
			System.out.println("\nError REST_API:  "+te.getErrorCode()+"  "+te.getErrorMessage()+" New try after 5 seconds.");//Another kind of error
			try {
				Thread.sleep(5000);
				this.check_quit();
			} catch (InterruptedException e) {
				System.exit(2);
			}
		} 
	}
	
	/**
	 * Returns a list containing all retweets of a given query or hashtag. Date options are available.
	 * 
	 * @param consulta	The query or hashtag.
	 * @param from		Date (yyyy-mm-dd) in last twos weeks. 
	 * @param to		To date.
	 */
	public ArrayList<Status> getReTweets(String consulta, String from, String to) {
		Query query = new Query(consulta);
		if(!to.equals(""))
		{
			query.since(from);
			query.until(to);
		}
		else if(!from.equals(""))
		{
			query.since(from);
		}
		
		long lastID = Long.MAX_VALUE;
		ArrayList<Status> retweets = new ArrayList<Status>();
		
		ArrayList<Status> tw = new ArrayList<Status>();
		salir = false;
		System.out.println("\n");
		while (salir==false)
		{	
			query.setCount(100); //Twitter Rest API returns only 100 tweets per request
			try {
				
				QueryResult result = twitter.search(query);
				tw.clear();
				tw.addAll(result.getTweets());
	
				if(tw.size() == 0)
					salir = true;
				else
				{
					for(Status r: tw){
						if(r.isRetweet())
							retweets.add(r);
					}
					System.out.print("\r("+consulta+") Got: "+ retweets.size()+" ReTweets\t");

					for (Status t: tw) 
						if(t.getId() < lastID) 
							lastID = t.getId(); //In next request, continue getting tweets before this id.

					query.setMaxId(lastID-1);
					this.check_quit();//Check if user wants to stop the process of getting tweets.
				}

			}
			catch (TwitterException te) 
			{
				this.check_error(te);//Check if error is about communication, credentials, rate limits...
			}; 
		}
		return retweets;
	}
	
	/**
	 * Returns twitter user id from ScreenName. If there is no twitter user with that ScreenName, returns -1.
	 */
	public Long getId_From_ScreenName(String name)
	{
		salir = false;
		while(!salir)
		{
			try {
				User usuario = twitter.showUser(name);
				if(usuario!= null)
					return usuario.getId();
				else
					return (long) 0;
			} catch (TwitterException te) {
				this.check_error(te);
			}
		}
		return (long) -1;
	}

	
		
}
