# I-TWEC

I-TWEC is an interactive clustering tool for Twitter. Using substring similarity as basis, I-TWEC is able to cluster tweets in linear space and time complexity. The resulting clusters have high lexical intra-cluster similarity and the end-user is able to merge these clusters based on semantic relatedness.

I-TWEC is composed of two parts: Suffix Tree basedTweet Clustering Tool (ST-TWEC) and its interactive part. TWEC is a stand-alone application which is able lexically cluster tweets and it is written in Java. The source code of ST-TWEC in the [GitHub page](https://github.com/merterpam/ST-TWEC).

I-TWEC is the interactive part of the algorithm and it works together with ST-TWEC. I-TWEC is written with Java Servlets and require TWEC as a dependency to work correctly. Using I-TWEC, the end-user is able to adjust lexical clustering and semantic relatedness thresholds, merge clusters and export clustering/evaluation results.

# Usage

In order to use this tool, you require Java Runtime Environment 8 and Apache Tomcat 9.0. You can use this tool by using Maven to downlaod the code as dependency or putting the War file into Tomcat:

### 1. Maven

  * Add the following to the <repositories> section of your pom.xml:

```
<repository>
       <id>jitpack.io</id>
       <url>https://jitpack.io</url>
</repository>
```

  * Add the following to the <dependencies> section of your pom.xml:

```
	<dependency>
	    <groupId>com.github.merterpam</groupId>
	    <artifactId>I-TWEC</artifactId>
	    <version>v1.0.0</version>
	</dependency>
```

### 2. War file only

  * Download the [latest .war file](https://github.com/merterpam/I-TWEC/releases) from the releases section.
  * Add it to the webapps directory inside Apache Tomcat directory. The application will be live on http://yourhost/I-TWEC . 
