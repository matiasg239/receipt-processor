To run:
1) install scala (https://www.scala-lang.org/download/) and SBT if not already installed (https://www.scala-sbt.org/1.x/docs/Setup.html). If using IntelliJ, you can just install the scala plugin
2) navigate to project root in terminal
3) run `sbt docker:stage`
4) run `sbt docker:packageLocal`
5) run `docker run --rm -tid -p 8080:8080  receiptprocessor:0.0.1`
6) the service should now be running on localhost:8080