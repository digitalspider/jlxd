# Java LXD Application

This application is made up of 4 sub projects including:
* [jlxd-core|(https://github.com/digitalspider/jlxd/tree/master/jlxd-core)] - A Java based REST API for LXD
* [jlxd-cli|(https://github.com/digitalspider/jlxd/tree/master/jlxd-cli)] - A Simple Command Line application using the jlxd-core
* [jlxd-cli-boot|(https://github.com/digitalspider/jlxd/tree/master/jlxd-cli-boot)] - A Simple Command Line application using the jlxd-core, built using spring boot
* [jlxd-ui|(https://github.com/digitalspider/jlxd/tree/master/jlxd-ui)] - A Spring Boot Web Application that allows you to administor LXD through the web.

For more information about LXD see:
* https://www.stgraber.org/2016/04/18/lxd-api-direct-interaction
* http://www.ubuntu.com/cloud/lxd
* https://linuxcontainers.org/lxd/getting-started-cli
* https://github.com/lxc/lxd/blob/master/doc/rest-api.md

Notes:
* This project is still a work in progress, although much of the jlxd-core is pretty stable
* The UI part is actively being worked on at the moment