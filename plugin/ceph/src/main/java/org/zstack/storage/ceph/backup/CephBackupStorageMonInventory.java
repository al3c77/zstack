package org.zstack.storage.ceph.backup;

import org.zstack.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by frank on 7/27/2015.
 */
@Inventory(mappingVOClass = CephBackupStorageMonVO.class)
public class CephBackupStorageMonInventory {
    private String hostname;
    private Integer monPort;
    private Timestamp createDate;
    private Timestamp lastOpDate;
    private String backupStorageUuid;


    private Integer sshPort;

    public static CephBackupStorageMonInventory valueOf(CephBackupStorageMonVO vo) {
        CephBackupStorageMonInventory inv = new CephBackupStorageMonInventory();
        inv.setHostname(vo.getHostname());
        inv.setMonPort(vo.getMonPort());
        inv.setCreateDate(vo.getCreateDate());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setBackupStorageUuid(vo.getBackupStorageUuid());
        inv.setSshPort(vo.getSshPort());
        return inv;
    }

    public static List<CephBackupStorageMonInventory> valueOf(Collection<CephBackupStorageMonVO> vos) {
        List<CephBackupStorageMonInventory> invs = new ArrayList<CephBackupStorageMonInventory>();
        for (CephBackupStorageMonVO vo : vos) {
            invs.add(valueOf(vo));
        }

        return invs;
    }

    public Integer getSshPort() {
        return sshPort;
    }

    public void setSshPort(Integer sshPort) {
        this.sshPort = sshPort;
    }
    public String getBackupStorageUuid() {
        return backupStorageUuid;
    }

    public void setBackupStorageUuid(String backupStorageUuid) {
        this.backupStorageUuid = backupStorageUuid;
    }

    public Integer getMonPort() {
        return monPort;
    }

    public void setMonPort(Integer monPort) {
        this.monPort = monPort;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public Timestamp getLastOpDate() {
        return lastOpDate;
    }

    public void setLastOpDate(Timestamp lastOpDate) {
        this.lastOpDate = lastOpDate;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }
}
