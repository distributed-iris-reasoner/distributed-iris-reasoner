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

./bin/hadoop jar /home/valer/Projects/eu.larkc.reasoner/workspace/distributed-iris-reasoner/iris-impl-distributed/build/iris-impl-distributed-0.0.1.jar eu.larkc.iris.Main dbpedia -process DATALOG ./rules.txt true subclassof
./bin/hadoop jar /home/valer/Projects/eu.larkc.reasoner/workspace/distributed-iris-reasoner/iris-impl-distributed/build/iris-impl-distributed-0.0.1.jar eu.larkc.iris.Main dbpedia -importNTriple /home/valer/Tasks/iris_distributed_reasoner/instance_types_en.nt instance-types
./bin/hadoop jar /home/valer/Projects/eu.larkc.reasoner/workspace/distributed-iris-reasoner/iris-impl-distributed/build/iris-impl-distributed-0.0.1.jar eu.larkc.iris.Main dbpedia -importRdf default ontology
./bin/hadoop jar /home/valer/Projects/eu.larkc.reasoner/workspace/distributed-iris-reasoner/iris-impl-distributed/build/iris-impl-distributed-0.0.1.jar eu.larkc.iris.Main dbpedia -test 