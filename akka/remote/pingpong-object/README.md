ref: http://alvinalexander.com/scala/akka-remote-sending-objects-messages

```sh
% mkdir client/lib
% mkdir server/lib
% cd types
% sbt package
% cp target/scala-2.11/types* ../client/lib
% cp target/scala-2.11/types* ../server/lib

split terminal window, then
% cd ../server && sbt run
and
% cd ../client && sbt run
```
