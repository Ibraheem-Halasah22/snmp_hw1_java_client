package classes_for_table_views;

public class TCPConnsTableRow {
    Integer no;
    String status, localIP, localPort, remoteIP, remotePort;

    public TCPConnsTableRow(Integer no, String status, String localIP, String localPort, String remoteIP, String remotePort) {
        this.no = no;
        this.status = status;
        this.localIP = localIP;
        this.localPort = localPort;
        this.remoteIP = remoteIP;
        this.remotePort = remotePort;
    }

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLocalIP() {
        return localIP;
    }

    public void setLocalIP(String localIP) {
        this.localIP = localIP;
    }

    public String getLocalPort() {
        return localPort;
    }

    public void setLocalPort(String localPort) {
        this.localPort = localPort;
    }

    public String getRemoteIP() {
        return remoteIP;
    }

    public void setRemoteIP(String remoteIP) {
        this.remoteIP = remoteIP;
    }

    public String getRemotePort() {
        return remotePort;
    }

    public void setRemotePort(String remotePort) {
        this.remotePort = remotePort;
    }
}
