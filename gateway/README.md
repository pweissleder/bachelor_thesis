# Install requirements and start environment
Adapt the configuration files in the configuration folder to your needs.
Mainly set the server address and the MQTT broker address as well as the gateway_id and authentication token. 
The latter can be obtained from the server.

Run the configuration/scripts/activate.sh script to install the required packages. 
```bash configuration/scripts/activate.sh```

Execute the following command to start the application :
``` python3 src/app.py ```

Or run the Docker command
```docker-compose up```
or
```
docker build -t iot-gateway .
docker run --name iot-gateway --network host --restart unless-stopped -d iot-gateway
```
 (Docker has networking issues during the device discovery process, so it is 
recommended to run the application within the venv environment)

# Local Testing with Server
- Uncomment the network part in the compose file 
- Change the server and mqtt address in the configuration file by changing to the uncommented lines
- Run the docker-compose file, ensure the image and container is rebuilt 





