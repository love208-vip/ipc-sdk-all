package org.ipc.dahua.dto;

/**
 * @Author 洋芋_Sir
 * @Date 2020/6/30
 * @description
 **/
public class Config {
    private String ip;
    private int port;
    private String name;
    private String passwd;
    private int channels;
    private String similar;

    public int getChannels() {
        return channels;
    }

    public void setChannels(int channels) {
        this.channels = channels;
    }

    public String getSimilar() {
        return similar;
    }

    public void setSimilar(String similar) {
        this.similar = similar;
    }

    public Config(String ip, Integer port, String name, String passwd, String similar, int channels) {
        this.ip = ip;
        this.port = port;
        this.name = name;
        this.passwd = passwd;
        this.similar= similar;
        this.channels=channels;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

}
