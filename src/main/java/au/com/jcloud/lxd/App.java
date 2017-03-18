package au.com.jcloud.lxd;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import au.com.jcloud.lxd.model.Container;
import au.com.jcloud.lxd.model.Image;
import au.com.jcloud.lxd.model.Operation;
import au.com.jcloud.lxd.service.LxdService;
import au.com.jcloud.lxd.service.LxdServiceImpl;
import au.com.jcloud.lxd.util.LXDUtil;

/**
 * Created by david on 16/07/16.
 */
public class App {

    private static final Logger LOG = Logger.getLogger(App.class);

    public static void main(String[] args) {
        LOG.info("LXC START. args="+args.length);
        try {
            if (args.length==0) {
                System.out.println("Usage: jlxd [host[:port]] <c|i|o> [name] [start|stop|create|delete|snaps|file]");
                System.out.println("");
                System.out.println("   c = list containers");
                System.out.println("   i = list images");
                System.out.println("   o = list operations");
                System.out.println("   name = a specific instance of one of the above");
                System.out.println("   start|stop|create|delete|snaps|file = only for containers");
                System.exit(1);
            }
            //LxdService service = new LxdServiceCliImpl(new LxdServiceImpl());
            LXDUtil.setKeypath("C:/opt/jcloud/");
            LxdService service = new LxdServiceImpl();
            String remoteHostAndPort = null;
            for (int i=0; i<args.length; i++) {
                if (args[i].equals("c")) {
                    LOG.info("");
                    if (args.length-1==i || args[i+1].equals("o") || args[i+1].equals("i")) {
                        List<Container> containers = service.getContainers();
                        LOG.info("containers=" + containers.size());
                        for (Container container : containers) {
                            LOG.info("container=" + container);
                        }
                    } else if (args.length>i+1 && !args[i+1].equals("o") && !args[i+1].equals("i")) {
                        String name = args[i+1];
                        Container container = service.getContainer(name);
                        if (container==null) {
                            LOG.error("container " + name + " does not exist");
                            System.exit(1);
                        }
                        i++;
                        if (args.length>i+1 && !args[i+1].equals("o") && !args[i+1].equals("i")) {
                            String operation = args[i+1];
                            switch (operation) {
                                case "start":
                                    LOG.info("starting container=" + container);
                                    service.startContainer(name);
                                    break;
                                case "stop":
                                    LOG.info("stopping container=" + container);
                                    service.stopContainer(name);
                                    break;
                                case "create":
                                    LOG.info("creating new container=" + name);
                                    service.createContainer(name, "alpine/edge/amd64"); // TODO: Alias not valid
                                    break;
                                case "delete":
                                    LOG.info("deleting container=" + container);
                                    service.deleteContainer(name);
                                    break;
                                case "snaps":
                                    LOG.info("Snapshots for container=" + container);
                                    LOG.info(service.getSnapshots(container));
                                    break;
                                case "file":
                                	if (args.length>i+2 && !args[i+2].equals("o") && !args[i+2].equals("i")) {
                                		String filepath = args[i+2];
	                                    LOG.info("Getting file "+filepath+" for container=" + container);
	                                    LOG.info(service.getFile(container.getName(), filepath));
                                	} else {
                                		LOG.warn("Cannot get file for container=" + container+". Filepath not provided");
                                	}
                                    break;
                                default:
                                    LOG.warn("Unknown container operation: " + operation);
                                    System.exit(1);
                            }
                        } else {
                            LOG.info("container=" + container);
                        }
                    }
                }
                else if (args[i].equals("i")) {
                    LOG.info("");
                    Map<String,Image> images = service.loadImages();
                    if (args.length-1==i || args[i+1].equals("o") || args[i+1].equals("c")) {
                        LOG.info("images=" + images.size());
                        for (Image image : images.values()) {
                            LOG.info("image=" + image);
                        }
                    } else if (args.length>i+1 && !args[i+1].equals("o") && !args[i+1].equals("c")) {
                        String name = args[i+1];
                        for (String key : images.keySet()) {
                            if (key.contains(name)) {
                                LOG.info("image=" + images.get(key));
                            }
                        }
                        i++;
                    }
                }
                else if (args[i].equals("o")) {
                    LOG.info("");
                    if (args.length-1==i || args[i+1].equals("i") || args[i+1].equals("c")) {
                        List<Operation> operations = service.getOperations();
                        LOG.info("operations=" + operations.size());
                        for (Operation operation: operations) {
                            LOG.info("operation=" + operation);
                        }
                    } else if (args.length>i+1 && !args[i+1].equals("i") && !args[i+1].equals("c")) {
                        String name = args[i+1];
                        Operation operation = service.getOperation(name);
                        LOG.info("operation=" + operation);
                        i++;
                    }
                }
                else {
                	if (StringUtils.isBlank(remoteHostAndPort)) {
                		remoteHostAndPort = args[i];
                		service.setRemoteHostAndPort(remoteHostAndPort);
                	}
                }
            }
        } catch (Exception e) {
            LOG.error(e, e);
        }
        LOG.info("LXC DONE");
    }

}
