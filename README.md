# gephitterlib

 Get tweets about a hashtag from Twitter APIs and generate graphs representing nets of retweets or nets of mentions.
 Export graphs to GEXF files and visualize them with the Gephi platform. 
 
 This library contains a program to test all its features, but you can also use it to get tweets and generate
 GEXF files. It is necessary to create a file named auth_file.txt with the OAuth credentials of your registered
 app in https://apps.twitter.com/. This file must contain 4 lines, each line corresponding with:
 
	1. ConsumerKey
	2. ConsumerSecret
	3. AccesToken
	4. AccesTokenSecret
 
 
 To execute the library, in Linux terminal, create a directory containning these two files:
 
	1. auth_file.txt
	2. gephitterlib.jar
 
 Next command shows the usage of the program:
 
 	    $ java -jar gephitterlib.jar -h 
 
 USAGE:

	  gephitterlib.jar  <-REST|-STREAM> <-RT|-ME> <Hashtag> [User ...] [-FROM <YYYY-MM-DD> [-TO <YYYY-MM-DD>]]
 
 	  OPTIONS:

	    	    -REST          Communication to Twitter REST API
 
	    	    -STREAM        Communication to Twitter Streaming API
 
 	    	    -RT            Generate GEXF files that represent Nets of Retweets 
 
 	    	    -ME            Generate GEXF files that represent Nets of Mentions

 	    	    -FROM          Specifies from date, in last two weeks, to get tweets
 
 	    	    -TO            Specifies a final date to get tweets.
 
	  Query:

 	    	    Hashtag       The hashtag you want to get tweets
 
 	  Subnets:

 	     	    USER          Twitter user ScreenName. Generate a GEXF file with the filtered net of this user
 
 
 If you want to quit the program without losing collected tweets, please press "q and Enter Key".

