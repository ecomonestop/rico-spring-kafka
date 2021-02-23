

## Simple Hello World all the way to detail use case driven examples using Spring Cloud Stream, Kafka, and AWS

### Simple Hello World

This is just an example of how to use Spring Cloud Stream project with Kafka.  Spring Cloud Stream 3.0 is now functional based in its bindings.

I.e. depending on the type of Functional interface, it will create the following mapping:
	1. Consumer -> Sink -> Subscriber
	2. Supplier -> Source -> Publisher
	3. Function -> Processor -> Publish and Subscriber
	
This differs from the annotation approach in previous versions.  Annotation approach is officially deprecated 

This example is only design to run locally at the moment.  The following are some commentary on running tests and running locally

#### Test
The big change in Spring Cloud Stream 3.0 is that now you have a component that allows you to run a real binder (like kafka) w/o needing networking config (its in memory)
TestChannelBinderConfiguration is the new component.  Example of how to use TestChannelBinderConfiguration is in the project


#### Run locally
To run locally, do the following steps:

	1. Run docker-compose up -d
	2. Start the app.  Either from ide or command line
	3. Test the app is working by publishing messages to the "lowercase" topic and verifying it by consuming from "uppercase" topic:
		3.a docker exec -it kafka bash
			[appuser@kafka ~]$ kafka-console-producer --broker-list localhost:9092 --topic lowercase --property "parse.key=true" --property "key.separator=:"
			>1:hello
			>2:world
		3.b kafka-console-consumer --bootstrap-server localhost:9092 --from-beginning --topic uppercase --property "print.key=true"
			null    HELLO
			null    WORLD
			

### Detailed Use Case - Foreign Source

This is a detailed use case.  Imagine you have a file that gets dropped into an S3 bucket. You want to stream the contents of this file instead of doing a batch job
on it.  Spring Cloud Stream has a concept called "Foreign Source" where it brings in non native streams like the file being put in S3 and bring it in the Stream world.

The proposed architecture is as follows


![alt text](https://github.com/ecomonestop/rico-spring-kafka/blob/master/ForeignSource.PNG?raw=true)

#### Run locally
To run locally, do the following steps:

	1. Run docker-compose up -d
	2. Start the app.  Either from ide or command line using the foreign spring profile
	3. Test the app is working by simulating a S3 Object create event using sam local and verifying contents of file is now in the "toStream" topic:
		3.a sam local invoke "S3FileToStreamFunction" -e events/event-s3.json
		3.b docker exec -it kafka bash
		[appuser@kafka ~]$ kafka-console-consumer --bootstrap-server localhost:9092 --from-beginning --topic toStream --property "print.key=true"
		null    {"id":123,"name":"test"}

TODO:
- Describe how to make this app fault tolerant, "exactly once" semantics
- Give explanation on StreamBridge, Spring cloud stream API for "Foreign Source"
-  Add dead letter queue to lambda
-  deploy to a test environment in AWS


### Detailed Use Case - Do not compute what you can precompute

Lets think about how indexes work in relation databases.  When you create an index, the rdbms first scans the table and partitions the data in a different way so when you search on the newly indexed field, its much more optimal on read.  Second, the index is updated incrementally as records are added and deleted.  The key here is that it only has to scan  **the entire table ONCE** to compute the desired partitioned file that makes it fast for reads.  From then on, it can use the **precomputed**  solution 

When using **materialized views, you should precompute** (synonymous to the index operation of rdbms described above ) 

TODO
- Talk about Kafka abstractions KTable and KStreams and how they relate to the materialized view and precomputing.
- Go into "exactly once" and "in order" semantics and how you maintain them even in face of faults
- Use of Mongodb and MongoDB Connector for Apache Kafka









