/**
 * @author Alvaro Martinez
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Communication to Twitter Streaming API. Get live tweets  of a given query.
 */
public class Stream_API{
	/**
	 * twitterStream	Object from twitter4j to enable an authenticated communication. Allows to get live data from the API.
	 * listener			Listener to twitter stream channel of a given query or hashtag.
	 * salir			Variable to stop the process of getting tweets.
	 * reader			Read stdin.
	 * input			Store the stdin lecture
	 */
	private TwitterStream twitterStream;
	private StatusListener listener;
	private ArrayList<Status> listaTweets;
	private BufferedReader reader;
	private String input;
	boolean Salir = false;

	/**
	 * @param cb	Needs a valid configuration builder to get an instance of the twitter object. This cb is associated to OAuth credentials.
	 * 				The method credentials in class Methods.java configure this cb from a file that must contain the 
	 * 				ConsumerKey, ConsumerSecret, AccesToken and AccesTokenSecet of your registered app in https://apps.twitter.com/			
	 */
	public Stream_API(ConfigurationBuilder cb){
		this.twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
		this.listaTweets = new ArrayList<Status>();
		this.reader = new BufferedReader(new InputStreamReader(System.in));
	}


	/**
	 * Returns a list containing live retweets of a given query or hashtag.
	 */
	public ArrayList<Status> getReTweets(final String etiqueta)
	{
		this.listener = new StatusListener() {
			@Override
			public void onStatus(Status status){
				if(status.isRetweet())
					listaTweets.add(status);

				if( (listaTweets.size() % 100) == 0)
				{
					System.out.print("\r("+etiqueta+") Got: "+ listaTweets.size()+" ReTweets\t");
				}
			}

			@Override
			public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
				System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
			}

			@Override
			public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
				System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
			}

			@Override
			public void onScrubGeo(long userId, long upToStatusId) {
				System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
			}

			@Override
			public void onStallWarning(StallWarning warning) {
				System.out.println("Got stall warning:" + warning);
			}

			@Override
			public void onException(Exception ex) {
				Salir = true;
			}

		};

		
		FilterQuery fq = new FilterQuery();
		fq.track(etiqueta);
		twitterStream.addListener(listener);//Set a listener 
		twitterStream.filter(fq);			//Connected to the stream channel of the query

		/**
		 * Block this procces until user decides to stop the stream connection.
		 */
		while(!Salir)
		{
			try {
				Thread.sleep(2000);
				this.check_quit();
			} catch (InterruptedException e) {
				System.exit(2);
			}
		}
		twitterStream.shutdown();//Connection finished.
		
		return listaTweets;
	}
	
	/**
	 * Returns a list containing live tweets of user mentions of a given hashtag .
	 */
	public ArrayList<Status> getTweets(final String consulta)
	{
		this.listener = new StatusListener() {
			@Override
			public void onStatus(Status status) {
				listaTweets.add(status);
				
				if( (listaTweets.size() % 100) == 0)
				{
					System.out.print("\r("+consulta+") Got: "+ listaTweets.size()+" Tweets\t");
				}
			}

			@Override
			public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
				System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
			}

			@Override
			public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
				System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
			}

			@Override
			public void onScrubGeo(long userId, long upToStatusId) {
				System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
			}

			@Override
			public void onStallWarning(StallWarning warning) {
				System.out.println("Got stall warning:" + warning);
			}

			@Override
			public void onException(Exception ex) {
				Salir = true;
			}

		};

		FilterQuery fq = new FilterQuery();
		fq.track(consulta);
		twitterStream.addListener(listener);//Set a listener 
		twitterStream.filter(fq);			//Connected to the stream channel of the query

		/**
		 * Block this procces until user decides to stop the stream connection.
		 */
		while(!Salir)
		{
			try {
				Thread.sleep(2000);
				this.check_quit();
			} catch (InterruptedException e) {
				System.exit(2);
			}
		}
		twitterStream.shutdown();
		
		return listaTweets;
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
					this.Salir = true;

				}
			}
		} catch (IOException e) {
			System.exit(3);
		}
	}
}
