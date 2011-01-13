FACTS TAP - HOW IT WORKS
========================

The facts tap is designed to connect to different storages and provide data to be used by a flow running a map/reduce algorithm on Hadoop.

Facts tap can behave as sources and also as sinks. When used as a source, the tap is created based on an input atom. 
The atom is an interface derived from Iris, and actually it is a literal of a rule. 

Fact taps are created using a factory eu.larkc.iris.storage.FactsFactory
A factory is created for a storage and all the taps created with that factory are using that storage

The facts tap are configured through two property files :
- facts-configuration.properties
- facts-storage-configuration.properties

facts-configuration.properties
------------------------------
Here, one can define which class is used to configure the facts tap.
The configuration class is used to tell cascading which classes to use when reading and writing data to storages.
e.g.
facts.configuration.class=eu.larkc.iris.storage.rdf.RdfFactsConfiguration

facts-storage-configuration.properties
--------------------------------------
Contains the storages which can be used to create facts factories.
Here is an example of a storage :

humans.rdf2go.adapter=SESAME
humans.repository.type=MEMORY


Run tests
=========

Storage tests
-------------

The tests are located in the in src/test/java folder in the
eu.larkc.iris.rdf.storage folder
Currently there is one test in the rdf subpackage named RdfFactsTapTest

To run test right-click on the RdfFactsTapTest and choose Run as JUnit Test

If you get an error like the following one, when running the test, please do a Clean of all the projects Project/Clean/Clean All Projects
java.lang.NullPointerException
	at java.util.Properties$LineReader.readLine(Properties.java:418)
	at java.util.Properties.load0(Properties.java:337)
	at java.util.Properties.load(Properties.java:325)
	at eu.larkc.iris.storage.FactsFactory.<init>(FactsFactory.java:48)

	
Execution
=========

./bin/hadoop jar /home/valer/Projects/eu.larkc.reasoner/workspace/distributed-iris-reasoner/iris-impl-distributed/build/iris-impl-distributed-0.0.1.jar eu.larkc.iris.Main dbpedia -process DATALOG ./rules.txt subclassof
./bin/hadoop jar /home/valer/Projects/eu.larkc.reasoner/workspace/distributed-iris-reasoner/iris-impl-distributed/build/iris-impl-distributed-0.0.1.jar eu.larkc.iris.Main dbpedia -process RIF ./rules.xml subclassof

./bin/hadoop jar /home/valer/Projects/eu.larkc.reasoner/workspace/distributed-iris-reasoner/iris-impl-distributed/build/iris-impl-distributed-0.0.1.jar eu.larkc.iris.Main dbpedia -importNTriple /home/valer/Tasks/iris_distributed_reasoner/instance_types_en.nt instance-types
./bin/hadoop jar /home/valer/Projects/eu.larkc.reasoner/workspace/distributed-iris-reasoner/iris-impl-distributed/build/iris-impl-distributed-0.0.1.jar eu.larkc.iris.Main dbpedia -importRdf default ontology
./bin/hadoop jar /home/valer/Projects/eu.larkc.reasoner/workspace/distributed-iris-reasoner/iris-impl-distributed/build/iris-impl-distributed-0.0.1.jar eu.larkc.iris.Main dbpedia -test 


Execute with Amazon EC2
=======================

1. Create an Amazon EC2 account
2. Install whirr http://incubator.apache.org/whirr/
3. Write an haddop.properties file like this one :

whirr.service-name=hadoop
whirr.cluster-name=myhadoopcluster
whirr.instance-templates=1 jt+nn,1 dn+tt
whirr.provider=ec2
whirr.identity=AKIAIGLBFMZRZWL5A3TQ
whirr.credential=K+Lqb23cDj7V3W9WYXmENWC5udlLAqWXahQKV6C1
whirr.private-key-file=/home/valer/.ec2/keypair/APKAJHUH3LALVR23LTJQ 
#whirr.private-key-file=/home/valer/.ec2/keypair/APKAJHUH3LALVR23LTJQ.pub
whirr.hardware-id=t1.micro

The authentication values are from the amazon account.

4. Run the cluster with

./bin/whirr launch-cluster --config ../hadoop.properties

5. To check the hadoop namenode and job tracker use :

http://ec2-184-72-188-205.compute-1.amazonaws.com

6. To ssh to master node use :

ssh -i /home/valer/.ec2/keypair/APKAJHUH3LALVR23LTJQ ec2-user@ec2-184-72-188-205.compute-1.amazonaws.com

7. To use hadoop tools do the following :

export HADOOP_CONF_DIR=~/.whirr/myhadoopcluster/
export JAVA_HOME=/usr/lib/jvm/java-6-sun/

start the script :

~/.whirr/myhadoopcluster/hadoop-proxy.sh

then you may use from the haddop installation dir (same version as on amazon, in our case 0.20.2)

./bin/hadoop fs -ls /
./bin/hadoop fs -mkdir input
./bin/hadoop fs -put LICENSE.txt input
./bin/hadoop jar hadoop-0.20.2-examples.jar wordcount input output
./bin/hadoop fs -cat output/part-*

8. Stop the cluster with

./bin/whirr destroy-cluster --config ../hadoop.properties
