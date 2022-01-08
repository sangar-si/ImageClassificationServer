# ImageClassificationServer
Implementation of a image classification server where a client queries the server with an image to which the server replies with the result. The deep learning model is deployed in python3. Server is a Apache Thrift server written in Java. The motive of this project is to simulate a deployment of a Deep Learning Model. Client interacts with Front End Node or FENode. FENode may be supported by one or more Back End Node or BENode. Both FENode and BENode are ThreadPool servers, which means that they deploy one worker node for one client request. FEManager.java is the file that handles load balacing by distributing requests and consolidating responses from Back End servers.

## **How to Run?**
Execute build.sh to compile the java files. 

## **Run the servers by the following java code :**
/usr/lib/jvm/default-java/bin/java -cp .:gen-java/:"lib/*" FENode #portNumber
/usr/lib/jvm/default-java/bin/java -cp .:gen-java/:"lib/*" BENode localhost #FEPort #BEPort
/usr/lib/jvm/default-java/bin/java -cp .:gen-java/:"lib/*" Client localhost #FEPort "images"
