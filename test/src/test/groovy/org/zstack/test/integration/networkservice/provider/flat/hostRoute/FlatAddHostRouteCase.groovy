package org.zstack.test.integration.networkservice.provider.flat.hostRoute

import org.springframework.http.HttpEntity
import org.zstack.header.network.service.NetworkServiceConstants
import org.zstack.network.service.flat.FlatDhcpBackend
import org.zstack.sdk.ClusterInventory
import org.zstack.sdk.ImageInventory
import org.zstack.sdk.InstanceOfferingInventory
import org.zstack.sdk.L3NetworkHostRouteInventory
import org.zstack.sdk.L3NetworkInventory
import org.zstack.sdk.PrimaryStorageInventory
import org.zstack.sdk.HostInventory
import org.zstack.test.integration.networkservice.provider.NetworkServiceProviderTest
import org.zstack.test.integration.networkservice.provider.flat.FlatNetworkServiceEnv
import org.zstack.testlib.EnvSpec
import org.zstack.testlib.L3NetworkSpec
import org.zstack.testlib.SubCase
import org.zstack.utils.gson.JSONObjectUtil

/**
 * Created by shixin on 04/08/2018.
 */
class FlatAddHostRouteCase extends SubCase {
    EnvSpec env

    L3NetworkInventory l3

    @Override
    void clean() {
        env.cleanSimulatorHandlers();
        env.delete()
    }


    @Override
    void setup() {
        useSpring(NetworkServiceProviderTest.springSpec)
    }

    @Override
    void environment() {
        env = FlatNetworkServiceEnv.oneHostNoVmEnv()
    }

    void testAddDns() {
        List<FlatDhcpBackend.ApplyDhcpCmd> cmds = new ArrayList<>();
        env.afterSimulator(FlatDhcpBackend.APPLY_DHCP_PATH) { rsp, HttpEntity<String> e ->
            FlatDhcpBackend.ApplyDhcpCmd cmd = JSONObjectUtil.toObject(e.body, FlatDhcpBackend.ApplyDhcpCmd.class)
            cmds.add(cmd)
            return rsp
        }

        L3NetworkInventory l3Inv = addHostRouteToL3Network {
            l3NetworkUuid = l3.uuid
            prefix = "10.1.1.1/32"
            nexthop = "192.168.1.254"
        }
        assert l3Inv.getHostRoute().size() == 2
        for (L3NetworkHostRouteInventory route : l3Inv.getHostRoute()) {
            assert (route.prefix == "10.1.1.1/32" || route.prefix == NetworkServiceConstants.METADATA_HOST_PREFIX)
            if (route.prefix == "10.1.1.1/32") {
                assert route.nexthop == "192.168.1.254"
            }
        }

        assert cmds.size() == 3
        for (FlatDhcpBackend.ApplyDhcpCmd cmd : cmds) {
            assert cmd.l3NetworkUuid == l3.uuid
            for (FlatDhcpBackend.DhcpInfo dinfo: cmd.dhcp) {
                for (FlatDhcpBackend.HostRouteInfo rinfo : dinfo.hostRoutes) {
                    assert (rinfo.prefix == "10.1.1.1/32" || rinfo.prefix == NetworkServiceConstants.METADATA_HOST_PREFIX)
                    if (rinfo.prefix == "10.1.1.1/32") {
                        assert rinfo.nexthop == "192.168.1.254"
                    }
                }
            }
        }

        List<FlatDhcpBackend.ApplyDhcpCmd> cmds1 = new ArrayList<>();
        env.afterSimulator(FlatDhcpBackend.APPLY_DHCP_PATH) { rsp, HttpEntity<String> e ->
            FlatDhcpBackend.ApplyDhcpCmd cmd = JSONObjectUtil.toObject(e.body, FlatDhcpBackend.ApplyDhcpCmd.class)
            cmds1.add(cmd)
            return rsp
        }
        l3Inv = addHostRouteToL3Network {
            l3NetworkUuid = l3.uuid
            prefix = "10.0.1.0/24"
            nexthop = "192.168.1.253"
        }
        assert l3Inv.getHostRoute().size() == 3

        assert cmds1.size() == 3
        for (FlatDhcpBackend.ApplyDhcpCmd cmd : cmds1) {
            assert cmd.l3NetworkUuid == l3.uuid
            for (FlatDhcpBackend.DhcpInfo dinfo: cmd.dhcp) {
                assert dinfo.hostRoutes.size() == 3
                for (FlatDhcpBackend.HostRouteInfo rinfo : dinfo.hostRoutes) {
                    assert (rinfo.prefix == "10.1.1.1/32" || rinfo.prefix == "10.0.1.0/24"
                        || rinfo.prefix == NetworkServiceConstants.METADATA_HOST_PREFIX)
                    if (rinfo.prefix == "10.1.1.1/32") {
                        assert rinfo.nexthop == "192.168.1.254"
                    } else if (rinfo.prefix == "10.0.1.0/24") {
                        assert rinfo.nexthop == "192.168.1.253"
                    }

                }
            }
        }

        List<FlatDhcpBackend.ApplyDhcpCmd> cmds2 = new ArrayList<>();
        env.afterSimulator(FlatDhcpBackend.APPLY_DHCP_PATH) { rsp, HttpEntity<String> e ->
            FlatDhcpBackend.ApplyDhcpCmd cmd = JSONObjectUtil.toObject(e.body, FlatDhcpBackend.ApplyDhcpCmd.class)
            cmds2.add(cmd)
            return rsp
        }
        l3Inv = removeHostRouteFromL3Network {
            l3NetworkUuid = l3.uuid
            prefix = "10.1.1.1/32"
        }
        assert l3Inv.getHostRoute().size() == 2
        for (L3NetworkHostRouteInventory route : l3Inv.getHostRoute()) {
            assert (route.prefix == "10.0.1.0/24" || route.prefix == NetworkServiceConstants.METADATA_HOST_PREFIX)
            if (route.prefix == "10.0.1.0/24") {
                assert route.nexthop == "192.168.1.253"
            }
        }

        assert cmds2.size() == 3
        for (FlatDhcpBackend.ApplyDhcpCmd cmd : cmds2) {
            assert cmd.l3NetworkUuid == l3.uuid
            for (FlatDhcpBackend.DhcpInfo dinfo: cmd.dhcp) {
                for (FlatDhcpBackend.HostRouteInfo rinfo : dinfo.hostRoutes) {
                    assert (rinfo.prefix == "10.0.1.0/24" || rinfo.prefix == NetworkServiceConstants.METADATA_HOST_PREFIX)
                    if (rinfo.prefix == "10.0.1.0/24") {
                        assert rinfo.nexthop == "192.168.1.253"
                    }
                }
            }
        }

        List<FlatDhcpBackend.ApplyDhcpCmd> cmds3 = new ArrayList<>();
        env.afterSimulator(FlatDhcpBackend.APPLY_DHCP_PATH) { rsp, HttpEntity<String> e ->
            FlatDhcpBackend.ApplyDhcpCmd cmd = JSONObjectUtil.toObject(e.body, FlatDhcpBackend.ApplyDhcpCmd.class)
            cmds3.add(cmd)
            return rsp
        }
        l3Inv = removeHostRouteFromL3Network {
            l3NetworkUuid = l3.uuid
            prefix = "10.0.1.0/24"
        }
        assert l3Inv.getHostRoute().size() == 1

        assert cmds3.size() == 3
        for (FlatDhcpBackend.ApplyDhcpCmd cmd : cmds3) {
            assert cmd.l3NetworkUuid == l3.uuid
            for (FlatDhcpBackend.DhcpInfo dinfo: cmd.dhcp) {
                assert dinfo.hostRoutes.size() == 1
                assert dinfo.hostRoutes.get(0).prefix == NetworkServiceConstants.METADATA_HOST_PREFIX
            }
        }

    }

    @Override
    void test() {
        env.create {
            l3 = (env.specByName("l3") as L3NetworkSpec).inventory
            ClusterInventory cluster = env.inventoryByName("cluster")
            PrimaryStorageInventory ps = env.inventoryByName("local")
            HostInventory host1 = env.inventoryByName("kvm")
            InstanceOfferingInventory offering = env.inventoryByName("instanceOffering") as InstanceOfferingInventory
            ImageInventory image = env.inventoryByName("image") as ImageInventory

            HostInventory host2 = addKVMHost {
                name = "kvm-2"
                managementIp = "127.0.0.2"
                username = "root"
                password = "password"
                clusterUuid = cluster.uuid
            }

            HostInventory host3 = addKVMHost {
                name = "kvm-3"
                managementIp = "127.0.0.3"
                username = "root"
                password = "password"
                clusterUuid = cluster.uuid
            }

            createVmInstance {
                name = "test-1"
                l3NetworkUuids = [l3.uuid]
                instanceOfferingUuid = offering.uuid
                imageUuid = image.uuid
                hostUuid = host1.uuid
            }

            createVmInstance {
                name = "test-2"
                l3NetworkUuids = [l3.uuid]
                instanceOfferingUuid = offering.uuid
                imageUuid = image.uuid
                hostUuid = host2.uuid
            }

            createVmInstance {
                name = "test-3"
                l3NetworkUuids = [l3.uuid]
                instanceOfferingUuid = offering.uuid
                imageUuid = image.uuid
                hostUuid = host3.uuid
            }

            createVmInstance {
                name = "test-4"
                l3NetworkUuids = [l3.uuid]
                instanceOfferingUuid = offering.uuid
                imageUuid = image.uuid
                hostUuid = host3.uuid
            }

            testAddDns()
        }
    }
}
