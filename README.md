# I-TWEC

I-TWEC is an interactive clustering tool for Twitter. Using substring similarity as basis, I-TWEC is able to cluster tweets in linear space and time complexity. The resulting clusters have high lexical intra-cluster similarity and the end-user is able to merge these clusters based on semantic relatedness.

I-TWEC is composed of two parts: Tweet Clustering Tool (TWEC) and its interactive part. TWEC is a stand-alone application which is able lexically cluster tweets and it is written in Java. It uses abahgat's <a href="https://github.com/abahgat/suffixtree">Generalized Suffix Tree</a> implementation as its underlying structure.

I-TWEC is the interactive part of the algorithm and it works together with TWEC. I-TWEC is written with Java Servlets and require TWEC as a dependency to work correctly. Using I-TWEC, the end-user is able to adjust lexical clustering and semantic relatedness thresholds, merge clusters and export clustering/evaluation results.

To set up I-TWEC, the easiest approach is to use an IDE such as Eclipse to import the projects. After adding TWEC to the Java Build Path, you require a Web Server (such as Tomcat) to run I-TWEC.
