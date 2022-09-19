1. `aws ecr get-login-password --region us-east-2 | docker login --username AWS --password-stdin 767348005071.dkr.ecr.us-east-2.amazonaws.com`
1. `docker build -t samplr/java-api-workflow .`
1. `docker tag samplr/java-api-workflow:latest 767348005071.dkr.ecr.us-east-2.amazonaws.com/samplr/java-api-workflow:latest`
1. `docker push 767348005071.dkr.ecr.us-east-2.amazonaws.com/samplr/java-api-workflow:latest`  
1. `sam build`
1. `sam deploy --guided`
1. `sam delete`