package com.utility.model;

import java.math.BigInteger;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="Filehash")
public class FilehashBean {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id_filehash")
    private Long idFilehash;
    @Column(name="fullpath")
    private String fullpath;
    @Column(name="lastmodifydate")
    private Timestamp lastmodifydate;
    @Column(name="lastscandate")
    private Timestamp lastscandate;
    @Column(name="filesize")
    private BigInteger filesize;
    @ManyToOne
    @JoinColumn(name="id_server")
    private ServerBean server;
    @Column(name="sha256sum")
    private String sha256sum;
    public Long getIdFilehash() {
        return idFilehash;
    }
    public void setIdFilehash(Long idFilehash) {
        this.idFilehash = idFilehash;
    }
    public String getFullpath() {
        return fullpath;
    }
    public void setFullpath(String fullpath) {
        this.fullpath = fullpath;
    }
    public Timestamp getLastmodifydate() {
        return lastmodifydate;
    }
    public void setLastmodifydate(Timestamp lastmodifydate) {
        this.lastmodifydate = lastmodifydate;
    }
    public Timestamp getLastscandate() {
        return lastscandate;
    }
    public void setLastscandate(Timestamp lastscandate) {
        this.lastscandate = lastscandate;
    }
    public BigInteger getFilesize() {
        return filesize;
    }
    public void setFilesize(BigInteger filesize) {
        this.filesize = filesize;
    }
    public ServerBean getServer() {
        return server;
    }
    public void setServer(ServerBean server) {
        this.server = server;
    }
    public String getSha256sum() {
        return sha256sum;
    }
    public void setSha256sum(String sha256sum) {
        this.sha256sum = sha256sum;
    }

    
}
