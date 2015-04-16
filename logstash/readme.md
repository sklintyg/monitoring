# Elasticsearch

### Installation
Elasticsearch is preferably installed through your favourite packagemanager.

### Configuration
No configuration is needed. Default port of 9200 is fine.

### Running
Start the daemon through (this is if systemd is used, else use upstart/SysVinit/..
in a similar fashion)

```
sudo systemctl start elasticsearch
```

# Logstash

### Installation
Logstash is preferably installed through your favourite packagemanager.

### Configuration
In the file the absolute path to the log files should be entered, not the
placeholder which exist today.

Logstash daemon is looking for a configuration file at `/etc/logstash/conf.d/`.
However, development has so far not used the daemon but are instead running
logstash in the foreground to leverage output to stdout as well as the flags
--testconfig and --debug

### Running
Start Logstash with the following command

```
logstash -f logstash-webcert.conf
```

# Kibana

### Installation
Kibana is preferably installed through your favourite packagemanager.

### Configuration
As we use Kibana 4 in the project there is no real export/import feature. (No
idea why this was removed.)

As a workaround we use the dump from the `.kibana` index in elasticsearch to
restore an old index.

To export and import we use the tool `elasticdump` (https://github.com/taskrabbit/elasticsearch-dump)

For instance to import use (for export swap input and output)
```
elasticdump --input=logstash/kibana/data --output=http://localhost:9200/.kibana  --type=data
elasticdump --input=logstash/kibana/mapping --output=http://localhost:9200/.kibana  --type=mapping
```

Then the dashboard should be available at http://localhost:5601


### Running
Start the daemon through (this is if systemD is used, else use upstart/SysVinit/..
in a similar fashion)

```
sudo systemctl start kibana
```
