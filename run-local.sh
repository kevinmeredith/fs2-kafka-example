helm repo add bitnami https://charts.bitnami.com/bitnami

helm install my-release bitnami/kafka

kubectl port-forward service/kafka 9092:9092

sbt it:test
