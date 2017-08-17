# I-TWEC

I-TWEC is an interactive clustering tool for Twitter. Using substring similarity as basis, I-TWEC is able to cluster tweets in linear space and time complexity. The resulting clusters have high lexical intra-cluster similarity and the end-user is able to merge these clusters based on semantic relatedness.

I-TWEC is composed of two parts: Suffix Tree basedTweet Clustering Tool (ST-TWEC) and its interactive part. TWEC is a stand-alone application which is able lexically cluster tweets and it is written in Java. The source code of ST-TWEC in the [GitHub page](https://github.com/merterpam/ST-TWEC).

I-TWEC is the interactive part of the algorithm and it works together with ST-TWEC. I-TWEC is written with Java Servlets and require TWEC as a dependency to work correctly. Using I-TWEC, the end-user is able to adjust lexical clustering and semantic relatedness thresholds, merge clusters and export clustering/evaluation results. I-TWEC can be found online [here](http://sky.sabanciuniv.edu:8080/I-TWEC/).

# Usage

In order to use this tool, you require [Java Runtime Environment 8](http://www.oracle.com/technetwork/java/javase), [Apache Tomcat 9.0](https://tomcat.apache.org) and a word embeddings vector model. An archived pre-trained model based on Google News can be found on [this link](https://drive.google.com/file/d/0B7XkCwpI5KDYNlNUTTlSS21pQmM/edit?usp=sharing) which is produced with [word2vec tool]( https://code.google.com/archive/p/word2vec/). 

You can use I-TWEC by using Maven to download the code as a dependency or putting the War file into Tomcat:

### 1. Cloning

You can clone the GitHub project and run it on Eclipse/IntelliJ. You can modify the static field WordEmbeddingsLoader#wordEmbeddingsPath to set the path of the word embeddings vector model.

### 2. Maven

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
	<type>war</type>
	<version>v1.0.2</version>
</dependency>
```

You can use the static method WordEmbeddingsLoader#setWordEmbeddingsPath to set the path of the word embeddings model.

### 3. War file only

  * Download the [latest .war and config file](https://github.com/merterpam/I-TWEC/releases) from the releases section.
  * Add the war file to the webapps directory inside Apache Tomcat directory($CATALINA_BASE/webapps). The application will be live on http://yourhost/I-TWEC . 
  * Modify the config file to set the path of the word embeddings model: Change the value of the environment (com.I-TWEC.wordEmbeddingsPath) to the path of the word embeddings model (bin file) in your server. Then, add the config file to the host directory inside the Apache Tomcat conf directory ($CATALINA_BASE/conf/Catalina/[yourhost]/). If the directory does not exist, you can either create it yourself, or run the web application once to let it be created automatically.
  
# Input/Output File Specifications

The input file should contain one tweet at a line. In the input file, tweets can have labels for evaluations purposes. These labels can be added at the end of each line, separated from the tweet by a \t character. Tweet labels are optional and not required for clustering. Because we use tab character as a seperator, tweets should not contain any \tBelow is an example of input data with tweet labels:

```
This is a sample tweet #sampleTweet \t SampleTweet
This is another sample tweet #sampleTweet \t SampleTweet 
This is a sample tweet #sampleTweet \t SampleTweet
Lorem ipsum dolor sit amet, consectetur adipiscing elit \t LoremIpsum
```

In the export section, you can download clustering results and evaluations. In the output file for clusters, each cluster is seperated by a new line and each cluster has a cluster label, cluster size and tweets which the cluster contains. If there are same tweets in the cluster, they are grouped and displayed in a single line. Below is an example of the output file for clusters:

```
Label: \t sample tweet \t 3
This is a sample tweet #sampleTweet \t 2
This is another sample tweet \t 1

Label: ...
```

In the output file for evaluations, each line represents a cluster. Each line has a cluster label, cluster size and intra-cluster evaluation score. Below is an example of the output file for evaluations:

```
sample tweet \t 3 \t 0.8
```

