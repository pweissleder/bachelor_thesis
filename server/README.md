
## Startup

start Docker containers
```
docker-compose up
```
### Setup DB 
Connect to the timescaledb database

To setup the device_state table as a hypertable run the following command in the timescaledb container
```
SELECT create_hypertable('device_state', by_range('timestamp'));
```
Then create the following index on timestamp and nodeID (id_integration_type and id_persistent_attribute)
in order to improve performance of queries.
```
CREATE INDEX ix_nodeid_time ON device_state (id_integration_type, id_persistent_attribute, timestamp DESC);
```

### Setup Initial User
Create admin user directly in the database
````
INSERT INTO users (name, email, password, role) VALUES ('Admin', 'admin@example.com', 'admin123', 0);
````

### Setup Gateway
Register gateway at the server with Admin user
````
http://[server_adress]:8080/gateways/register
// on local 
http://localhost:8080/gateways/register
````
- Copy id and auth_token from the response body and enter both into the respective fields in the auth part 
of the gateway_configuration.yml file. 

- Start the gateway


## General information

BrokerAccounts are the following:
````
GatewayUser:gateway_pass
ServerUser:server_pass
UIUser:ui_pass
MonitorUser:monitor_pass
````
