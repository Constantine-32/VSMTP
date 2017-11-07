package Server;

import java.util.LinkedList;

public class Client {
    private String name;
    private Boolean registered= false;
    private LinkedList<Message> messages = new LinkedList<>();

    public Client(String name) {
        this.name = name;
        this.registered = true;
    }

    public boolean addMessage(Message t){
            return messages.add(t);
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


    @Override
    public String toString() {
        return "Client{" +
                "name='" + name + '\'' +
                ", registered=" + registered +
                ", messages=" + messages +
                '}';
    }
}
