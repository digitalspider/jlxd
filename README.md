# Java LXD Rest API Client

A java rest API client for LXD (Linux Container Daemon) that allows Java applications to interact with Linux based lxd/lxc commands
To test, use the CLI:
* bin/jlxd.sh

The main interface is here:
* [LxdService.java](https://github.com/digitalspider/jlxd/blob/master/src/main/java/au/com/jcloud/lxd/service/LxdService.java)

Sample code (and the command line interface (CLI)) is here:
* [App.java](https://github.com/digitalspider/jlxd/blob/master/src/main/java/au/com/jcloud/lxd/App.java)

To use run use [Maven](https://maven.apache.org):
* mvn install
* bin/jlxd.sh

See:
* https://www.stgraber.org/2016/04/18/lxd-api-direct-interaction
* http://www.ubuntu.com/cloud/lxd
* https://linuxcontainers.org/lxd/getting-started-cli
* https://github.com/lxc/lxd/blob/master/doc/rest-api.md

Note:
* This works on a local instance, but needs some work to work remotely
* Most GET operations are implemented, but only a few of the POST/PUT are
* Cannot create containers from existing images - some things are hard coded here
