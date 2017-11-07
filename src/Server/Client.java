package Server;

import java.util.LinkedList;

public class Client {
    private String ip;
    private Integer port;
    private String name;
    private Boolean registered= false;
    private LinkedList<Message> messages;

    public Client(String ip, Integer port, String name) {
        this.ip = ip;
        this.port = port;
        this.name = name;
        this.registered = true;
    }

    public boolean addMessage(Message t){
            return messages.add(t);
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getRegistered() {
        return registered;
    }

    public void setRegistered(Boolean registered) {
        this.registered = registered;
    }

    public LinkedList<Message> getUnreadMessages() {
        LinkedList<Message> result= new LinkedList<>();
        for (Message t: messages){
            if (!t.isRead()) result.add(t);
        }
        return result;
    }

    public LinkedList<Message> getAllMessages(){
        return messages;
    }
}
