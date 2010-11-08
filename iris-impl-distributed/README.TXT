
Run tests
=========

Storage tests
-------------

The tests are located in the in src/test/java folder in the
eu.larkc.iris.rdf.storage folder
Currently there is one test in the rdf subpackage named RdfFactsTapTest

To run test tight-clink on the RdfFactsTapTest and choose Run as JUnit Test

If you get an error like the following one, when running the test, please do a Clean of all the projects Project/Clean/Clean All Projects
java.lang.NullPointerException
	at java.util.Properties$LineReader.readLine(Properties.java:418)
	at java.util.Properties.load0(Properties.java:337)
	at java.util.Properties.load(Properties.java:325)
	at eu.larkc.iris.storage.FactsFactory.<init>(FactsFactory.java:48)
