package com.java.jmx;

import com.java.sentinel.demo.mbean.User;

import javax.management.*;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.rmi.registry.LocateRegistry;

public class MBeanTest {

    public static void main(String[] args) throws MalformedObjectNameException, NotCompliantMBeanException, InstanceAlreadyExistsException, MBeanRegistrationException, IOException, InterruptedException {
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        ObjectName username = new ObjectName("FengZheng:type=customer,name=customerUserBean");

        server.registerMBean(new User(), username);

        LocateRegistry.createRegistry(8999);
        JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:8999/jmxrmi");
        JMXConnectorServer jcs = JMXConnectorServerFactory.newJMXConnectorServer(url, null, server);
        System.out.println("Begin rmi start");
        jcs.start();
        System.out.println("Rmi start");

        Thread.sleep(3600 * 1000);
    }
}
