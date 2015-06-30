
lbnn - Location based nearest neighbors
=======================================

This project shows how to use [kd-trees](https://en.wikipedia.org/wiki/K-d_tree)
with [Apache Spark](https://spark.apache.org/) and [Scala](http://www.scala-lang.org/)
for the analysis of location based / geometric data.

The example data consists of records of "check ins" of users with a mobile phone. For each checkin the user id,
the date and time and the location coordinates (latitude and longitude) are recorded.

```
[user]  [check-in time]	        [latitude]      [longitude]     [location id]
196514  2010-07-24T13:45:06Z    53.3648119      -2.2723465833   145064
196514  2010-07-24T13:44:58Z    53.360511233    -2.276369017    1275991
```

The example data set is called "[loc-Gowalla](https://snap.stanford.edu/data/loc-gowalla.html)" and available at the
[Stanford Large Network Dataset Collection](https://snap.stanford.edu/data/#locnet).

With this app we can calculate for every user at some specified point in time
the number of users that are checked in in his/her "neighborhood" (specified in kilometers).

Tools used:
* Apache Hadoop, Apache Spark, Scala, SBT, ScalaTest, Kryo.

Tools needed:
* Scala, SBT
* If you edit the SBT you can use the standalone version of Spark, so a Spark cluster is not needed

How to use
----------

Checkout the source

```
git clone https://github.com/jdinkla/location-based-nearest-neighbours.git
```

Change into the directory

```
cd location-based-nearest-neighbours
```

Create an assembly

```
$ sbt assembly
```

Download the example data, prepare the data and find the neighbors for the 6th of october 2009 within a 5 km range for
every user.

```
$ scripts/submit.sh download
$ scripts/submit.sh sort-by-user
$ scripts/submit.sh neighbors 20091006 5
```

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


