# Java LXD API Client

A Java LXD (Linux Container Daemon) Client allows Java applications to interact with Linux based lxd/lxc commands
To test you can run:
* bin/jlxd.sh

See:
* https://www.stgraber.org/2016/04/18/lxd-api-direct-interaction
* http://www.ubuntu.com/cloud/lxd
* https://linuxcontainers.org/lxd/getting-started-cli
* https://github.com/lxc/lxd/blob/master/doc/rest-api.md

Note:
* This works on a local instance, but needs some work to work remotely
* Proflies and Certificates are not yet implemented
* Cannot create containers from existing images - some things are hard coded here
