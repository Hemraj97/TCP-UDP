CLIENT_IMAGE='app-client'
PROJECT_NETWORK='TCP_UDP'
SERVER_CONTAINER='my-server'

if [ $# -ne 3 ]
then
  echo "Usage: ./run_client.sh <container-name> <port-number> <protocol>"
  exit
fi

# run client docker container with cmd args
docker run -it --rm --name "$1" \
 --network $PROJECT_NETWORK $CLIENT_IMAGE \
 java Client.UnifiedClient $SERVER_CONTAINER "$2" "$3"