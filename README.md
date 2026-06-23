
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

Requirements:
* A **JDK 17** — Spark 3.5 does not run on newer JDKs (e.g. Java 21/25).
* **SBT 1.x** — the pinned version is fetched automatically from `project/build.properties`.
* No Spark cluster is needed: the app runs locally out of the box
  (`spark.master=local[*]` in `local.properties`).

How to use
----------

Clone the repository and change into it:

```
git clone https://github.com/jdinkla/location-based-nearest-neighbours.git
cd location-based-nearest-neighbours
```

### Run locally

This needs only a JDK 17 and SBT — no Spark cluster. The repo ships a `justfile`
with the common commands (`just --list` to see them all); the equivalent raw SBT
call is shown in the comment next to each.

Download the example data, sort it by user, and find the neighbors for the 6th of
October 2009 within a 5 km range for every user:

```
just run download                  # sbt "run local.properties download"
just run sort-by-user              # sbt "run local.properties sort-by-user"
just run "neighbors 20091006 5"    # sbt "run local.properties neighbors 20091006 5"
```

Result and intermediate files are written under `temp/` (configurable in
`local.properties`); the neighbors CSV lands in `temp/results/`.

### Run on a Spark cluster

Build a self-contained assembly jar:

```
just assembly                      # sbt assembly  ->  target/scala-2.13/lbnn-assembly-1.0.jar
```

Adapt `cluster.properties` to your situation and set the Spark hostname in
`scripts/submit.sh` (or `.cmd` if you are a Windows user). Make sure you use the
correct Hadoop user name, otherwise set the environment variable
`HADOOP_USER_NAME`. Then:

```
scripts/submit.sh download
scripts/submit.sh sort-by-user
scripts/submit.sh neighbors 20091006 5
```

On a JDK 17 cluster, Spark also needs the `--add-opens` JVM flags (see
`sparkJava17Options` in `build.sbt`) supplied via the driver/executor Java options.

The output will be a file `num_neighbors_20091006_5.0.csv` that contains data like the following:

```
CustomerId;number of neighbors
4904;34
5812;3
6520;0
31744;0
6248;33
```

Current state
-------------

Originally written in 2015 against Scala 2.10 / Spark 1.4 / Hadoop 2.6, the project
was modernized in 2026 to **Scala 2.13, Spark 3.5, Hadoop 3.3** (built with SBT 1.x
on a JDK 17). The algorithm is unchanged.

The app is a first proof of concept.
The app works, but is not performance optimized. Partitioning of Apache Spark is ignored at the moment.

![Example analysis](http://dinkla.net/images/lbnn/lbnn_histogram.png)

Future work
-----------

* Partitioning in Spark
* Testing with larger data
    * Writing a random data generator or "multiplying" the current data
* Use other geometric data structures
    * Range trees


Keywords
--------
kd tree, kd-tree, k-d-tree, range queries, range query, latitude, longitude


Author
------

Written by [Jörn Dinkla](http://www.dinkla.net).
See [my homepage for further information](http://dinkla.net/en/datascience/lbnn.html)

