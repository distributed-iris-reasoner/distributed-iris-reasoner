IRIS DISTRIBUTED REASONER

=============
CONFIGURATION
=============

=======================================================
1. Defining a new RDF storage from where to import data
=======================================================

A new RDF storage is configured through the file facts-storage-configuration.properties, which is part of the jar archive of the application.
All the properties from this configuration file must begin with a prefix which tells the application to which storage the property is referring to.
For example if the storage id is "dbpedia" then each property name must be "dbpedia.{$property_name}".
 Currently only SESAME implementation of RDF repositories can be added and the supported types are HTTP and memory.

1.1 Sesame HTTP repository
--------------------------

The properties to define such a storage are (where the storage name will be "default") : 

default.rdf2go.adapter=SESAME			#fixed value for SESAME repositories
default.repository.type=REMOTE			# fixed value for sesame HTTP repositories
default.server.url=url_to_repository	#for example : http://voker:8080/openrdf-sesame	
default.repository.id=repository_id		# for exampe : univ

1.2 Sesame memory repository
----------------------------

The properties to define such a storage are (where the storage name will be "default") : 

default.rdf2go.adapter=SESAME			#fixed value for SESAME repositories
default.repository.type=MEMORY			# fixed value for sesame HTTP repositories

This will create an in-memory repository with the repository id having as value the storage name (in this case "default").

============================================
2. How to use an RDF repository in cascading
============================================

To access from cascading the data from an RDF repository you must create a facts tap.
The facts tap can behave as a source or a sink.
To create an instance of the facts tap you must first create an instance of facts factory, like this :

	eu.larkc.iris.storage.FactsFactory.FactsFactory factsFactory = eu.larkc.iris.storage.FactsFactory.getInstance(storageId);

where storage id is the identifier from the configuration file (for example "dbpedia"). If none provided then is assumed to be "default".
With the facts factory you can now create a facts tap :

	eu.larkc.iris.storage.FactsTap factsTap = factsFactory.getFacts();

This facts tap can now be used in cascading flows as a source or a sink.

=======================
3. Hadoop configuration
=======================

The configuration is done through 3 files : core-site.xml, hdfs-site.xml and mapred-site.xml by setting values for properties.
A property is defined as xml in the as follows :

  <property>
    <name>property_name</name>
    <value>property_value</value>
    <description>property_description</description>
  </property>

3.1 HDFS replication
====================
Configure hadoop for a number of replication of data. The recommended number is 3.
Set property "dfs.replication" to desired value in the hdfs-site.xml file

3.2 Maximum number of map tasks
===============================
To configure the number of map tasks to deploy on each machine set property "mapred.tasktracker.map.tasks.maximum" to the desired values.

3.3 Maximum number of reduce tasks
==================================
To configure the number of reduce tasks to deploy on each machine set property "mapred.tasktracker.reduce.tasks.maximum" to the desired values.


For more detailed configurations/optimization please check http://developer.yahoo.com/hadoop/tutorial/module7.html



=====
USAGE
=====

================================
4. Executing application modules
================================

The application is build as a jar using maven install, placed in the target folder having the name iris-impl-distributed-{$version}-hadoop-application.jar
There are 3 main modules of the application : importing, processing and exporting.

The general syntax for running an application operation is :

#./bin/hadoop -jar iris-impl-distributed-{$version}-hadoop-application.jar $project -importNTriple|importRdf|process|exportNTriple|exportRdf|test|viewConfig {application_parameters}

The project is a sting value used to identified for which set of data the application is run.
Depending on the module the application parameters change.

4.1 Import module
=================

There two types of import, from ntriple file and RDF storage.

4.1.1 Import from NTriple file
-------------------------------

To use it set the operation to "-importNTriple"
The parameters customer to this operation are :
	- file path : the path to the file to import
	- import name : identifier used to group all the data imported at this stage into one folder having the identifier's name
Example :
	#./bin/hadoop jar ./iris-impl-distributed-{$version}-hadoop-application.jar dbpedia -importNTriple ./instance_types_en.nt instance-types

4.1.2 Import from RDF storage
-------------------------------

To use it set the operation to "-importRdf"
The parameters customer to this operation are :
	- storage id : the storage name to use from the facts storage configuration file
	- import name : identifier used to group all the data imported at this stage into one folder having the identifier's name
Example :
	#./bin/hadoop jar ./iris-impl-distributed-{$version}-hadoop-application.jar dbpedia -importRdf dbpedia_instance_types instance-types

4.2 Processing module
=====================

To use the process module set operation to "-process"
The parameters of this module are :
	- rule types : the language the rules are defined with, two values, RIF or DATALOG
	- the rules : path to file containing the rules to evaluate
	- result name : identifier used to group all the data resulted from this evaluation. They will be stored in a folder having the name of this identifier.
Usage example :
	#./bin/hadoop jar ./iris-impl-distributed-{$version}-hadoop-application.jar dbpedia -process RIF ./rdfs.xml rdfs

4.3 Exporting module
====================

There two types of export, from ntriple file and RDF storage.

4.3.1 Export to NTriple file
-------------------------------

To use it set the operation to "-exportNTriple"
The parameters customer to this operation are :
	- file name : path to file where to export data
	- result name : the identifier used as result name in the process step. This will tell the exporter where from to take the data
Example :
	#./bin/hadoop jar ./iris-impl-distributed-{$version}-hadoop-application.jar dbpedia -exportNTriple ./rdfs_export.nt rdfs

4.3.2 Export to RDF storage
-------------------------------

To use it set the operation to "-exportRdf"
The parameters customer to this operation are :
	- storage id : the storage name where to export the data
	- result name : the identifier used as result name in the process step. This will tell the exporter where from to take the data
Example :
	#./bin/hadoop jar ./iris-impl-distributed-{$version}-hadoop-application.jar dbpedia -exportRdf rdfs_storage rdfs

4.4 Testing module
==================

This module is used just to check the results of a process operation. It displays the first 10 rows ofthe results.
To use it set the operation to "-test"
The parameters are :
	- result path : path to the file we want to display content
Usage example :
	#./bin/hadoop jar ./iris-impl-distributed-{$version}-hadoop-application.jar dbpedia -test rdfs/part-00000

4.5 View predicates config
==========================

This is only available if predicate indexing was enabled.
It is used to display the content of the predicates indexing configuration.
In that configuration file are stored the counts for each predicate and to which location the data for the predicate is stored.
Usage example :
	#./bin/hadoop jar ./iris-impl-distributed-{$version}-hadoop-application.jar dbpedia -viewConfig

=======================
5. THIRD-PARTY LICENSES
=======================

http://avro.apache.org/ : Apache License
http://hadoop.apache.org/ : Apache License
http://semanticweb.org/wiki/RDF2Go : RDF2GO is released under the new BSD license. Alternative licensing is possible, just ask us.
http://www.cascading.org/ : Cascading is Open Source and licensed under the GPL. Alternatively Standard or OEM Licenses, and Production and Developer Support can be obtained through Concurrent, Inc.
http://www.jgrapht.org/ : GNU LESSER GENERAL PUBLIC LICENSE
http://xmlenc.sourceforge.net/ : BSD Original
http://commons.apache.org/cli/ : Apache License
http://www.openrdf.org/ : Sesame 2.x is available under a BSD-style license
http://jackson.codehaus.org/ : Apache Software License
http://paranamer.codehaus.org/ : BSD
http://commons.apache.org/lang/ : Apache License
http://jetty.codehaus.org/jetty/ : Apache License
http://commons.apache.org/io/ : Apache License
http://www.junit.org/ : Common Public License - v 1.0
http://ccil.org/~cowan/XML/tagsoup/ : Apache License
http://www.slf4j.org/ : MIT license
https://github.com/cwensel/riffle/ : Apache License
http://ant.apache.org/ : Apache License
http://xircles.codehaus.org/projects/janino : Janino is distributed under the terms of the New BSD License
http://www.aduna-software.org/ : BSD-style license
http://logging.apache.org/log4j/ : Apache License
http://cglib.sourceforge.net/ : This library is free software, freely reusable for personal or commercial purposes.
http://qdox.codehaus.org/ : Apache License
http://jstl.java.net/ : Common Development and Distribution License (CDDL) version 1.0 + GNU General Public License (GPL) version 2
http://asm.ow2.org/ : http://asm.ow2.org/license.html
http://www.springsource.org/ : Apache License
http://tomcat.apache.org/taglibs/ : Apache License
http://commons.apache.org/ : Apache License
http://aopalliance.sourceforge.net/ : all the source code provided by AOP Alliance is Public Domain.

==============
6. OTHER OLD STUFF
==============


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
./bin/hadoop jar /home/valer/Projects/eu.larkc.reasoner/workspace/distributed-iris-reasoner/iris-impl-distributed/build/iris-impl-distributed-0.0.1.jar eu.larkc.iris.Main dbpedia -exportNTriple /home/valer/output.nt subclassof
./bin/hadoop jar /home/valer/Projects/eu.larkc.reasoner/workspace/distributed-iris-reasoner/iris-impl-distributed/build/iris-impl-distributed-0.0.1.jar eu.larkc.iris.Main dbpedia -exportRdf default subclassof
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
whirr.identity={amazon access key id}
whirr.credential={secret access key}
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
