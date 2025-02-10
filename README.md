To run:
1) navigate to project root in terminal
2) run `docker build -t receipt-processor .`
3) run `docker run -d -p 8080:8080 receipt-processor`
4) the service should be running on localhost:8080

Scala Notes:
* I used Scala because, well, I like it and think a lot of it is readable even if you're not familiar, but a few points
  * I used the http4s (https://http4s.org/) library for the request processing, because it's pretty lightweight and focuses on functional programming
  * The `IO{...]` monad used comes from the cats-effect library (https://typelevel.org/cats-effect/) and is used here to wrap "impure" functions (like accessing our "Database" or providing a response)

Other Notes:
* if Scala/SBT are already installed on your machine, it might be cleaner to do this instead:
 ```
  1) sbt docker:stage
  2) sbt docker:publishLocal
  3) docker run --rm -tid -p 8080:8080  receiptprocessor:0.0.1 
```
* The above uses sbt-native-packager(https://www.scala-sbt.org/sbt-native-packager/index.html) to generate a Dockerfile and build an image automatically. The plugin is already included in the project
* The generated image will just use `eclipse-temurin:23` as a base(while the provided Dockerfile uses https://hub.docker.com/r/sbtscala/scala-sbt), since it generates a bin from the jar locally and copies that to the container
* (Known) Assumptions made in the code are commented with `Assumption:` 
