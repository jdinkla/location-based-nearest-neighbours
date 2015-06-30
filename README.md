
lbnn - Location based nearest neighbors
=======================================

This project shows how to use [kd-trees](https://en.wikipedia.org/wiki/K-d_tree)
with [Apache Spark](https://spark.apache.org/) and [Scala](http://www.scala-lang.org/)
for the analysis location based / geometric data.

The example data consists of records of "check ins" of a user with his mobile phone. For each checkin the user id,
the date and time and the location coordinates (latitude and longitude) are recorded.

```
[user]	[check-in time]		[latitude]	[longitude]	[location id]
196514  2010-07-24T13:45:06Z    53.3648119      -2.2723465833   145064
196514  2010-07-24T13:44:58Z    53.360511233    -2.276369017    1275991
```

The example data set is called ["loc-Gowalla"](https://snap.stanford.edu/data/loc-gowalla.html) and available at the
[Stanford Large Network Dataset Collection](https://snap.stanford.edu/data/#locnet).

With our app we can calculate the number of users that are checked in in his "neighborhood"
(specified in kilometers) for every user at some specified point in time.

Tools used
    * Apache Hadoop, Apache Spark, Scala, SBT, ScalaTest, Kryo.

Tools needed
    * Scala, SBT
    * If you edit the SBT you can use the standalone version of Spark, so a Spark cluster is not needed

Current state
-------------

The current state is first proof of concept.
The app works, but is not performance optimized, for example is partitioning of Apache Spark ignored at the moment.


Future work
-----------

* Partitioning in Spark
* Testing with larger data
    * Writing a random data generator or "multiplying" the current data
* Use other geometric data structures
    * Range trees


