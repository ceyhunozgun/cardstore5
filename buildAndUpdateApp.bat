call mvn package
call aws s3 cp target/cardstore-0.0.1-SNAPSHOT.war s3://cardstoredeploy/

