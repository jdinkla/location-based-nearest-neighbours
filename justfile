# justfile for lbnn (location-based-nearest-neighbours) — common workflow shortcuts
# Run `just --list` to see all targets.
#
# Stack: Scala 2.13 + Apache Spark 3.5 on sbt 1.x.
# Local example:  just run sort-by-user   then   just run "neighbors 20091006 5"

home := env_var('HOME')

# Spark 3.5 does NOT run on this machine's default JDK (Java 25). Pin a JDK 17.
# Override with LBNN_JAVA_HOME=/path/to/jdk17 if yours lives elsewhere.
export JAVA_HOME := env_var_or_default('LBNN_JAVA_HOME', home / '.sdkman/candidates/java/17.0.19-tem')

# Use the sbt 1.x launcher (the SDKMAN default is sbt 2.0.0; this build targets sbt 1.x).
# Override with LBNN_SBT=/path/to/sbt.
sbt := env_var_or_default('LBNN_SBT', home / '.sdkman/candidates/sbt/1.12.13/bin/sbt')

# --- Dev ---

[group('dev')]
[doc("Show available commands")]
default:
    @just --list --unsorted

[group('dev')]
[doc("Show the JDK/sbt this build uses (Spark 3.5 needs JDK 17, not the default 25)")]
env:
    @echo "JAVA_HOME=$JAVA_HOME"
    @"$JAVA_HOME/bin/java" -version
    @echo "sbt: {{ sbt }}"

[group('dev')]
[doc("Resolve/refresh dependencies")]
install:
    {{ sbt }} update

[group('dev')]
[doc("Compile main + test sources")]
compile:
    {{ sbt }} Test/compile

[group('dev')]
[doc("Run the full test suite")]
test:
    {{ sbt }} test

[group('dev')]
[doc("Run only matching suites, e.g. just test-only '*KdTreeSuite'")]
test-only SUITE:
    {{ sbt }} "testOnly {{ SUITE }}"

[group('dev')]
[doc("Compile and test (all quality checks)")]
check: compile test

[group('dev')]
[doc("Remove build artifacts")]
clean:
    {{ sbt }} clean

# --- Build ---

[group('build')]
[doc("Build the slim spark-submit assembly jar (target/scala-2.13/lbnn-assembly-1.0.jar)")]
assembly:
    {{ sbt }} assembly

# --- App ---

[group('app')]
[doc("Run a command locally via Spark using local.properties, e.g. just run sort-by-user")]
run *ARGS:
    {{ sbt }} "run local.properties {{ ARGS }}"

[group('app')]
[doc("Submit to a Spark cluster via scripts/submit.sh (needs spark-submit + a built assembly)")]
submit *ARGS:
    scripts/submit.sh {{ ARGS }}
