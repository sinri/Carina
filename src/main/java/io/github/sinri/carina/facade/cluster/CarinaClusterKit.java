package io.github.sinri.carina.facade.cluster;

import com.hazelcast.config.*;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.ConfigUtil;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @since 3.0.0
 * This interface provides a shortcut to create an instance of ClusterManager for SAE.
 */
public interface CarinaClusterKit {

    static ClusterManager createClusterManagerForSAE(
            String clusterName,
            List<String> members,
            int port, int portCount
    ) {
        TcpIpConfig tcpIpConfig = new TcpIpConfig()
                .setEnabled(true)
                .setConnectionTimeoutSeconds(1);
        members.forEach(tcpIpConfig::addMember);

        JoinConfig joinConfig = new JoinConfig()
                .setMulticastConfig(new MulticastConfig().setEnabled(false))
                .setTcpIpConfig(tcpIpConfig);

        List<Integer> outboundPorts = new ArrayList<>();
        outboundPorts.add(0);

        NetworkConfig networkConfig = new NetworkConfig()
                .setJoin(joinConfig)
                .setPort(port)
                .setPortCount(portCount)
                .setPortAutoIncrement(portCount > 1)
                .setOutboundPorts(outboundPorts);

        Config hazelcastConfig = ConfigUtil.loadConfig()
                .setClusterName(clusterName)
                .setNetworkConfig(networkConfig);

        return new HazelcastClusterManager(hazelcastConfig);
    }
}
