package com.utility.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="Server")
public class ServerBean {
    
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id_server")
    private int idServer;
    @Column(name="server_name")
    private String serverName;

   

    public int getIdServer() {
        return idServer;
    }

    public void setIdServer(int idServer) {
        this.idServer = idServer;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

}
