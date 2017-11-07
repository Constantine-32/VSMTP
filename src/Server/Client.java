package Server;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.*;

public class Client {
  private String name;
  private Boolean registered;
  private LinkedList<Message> messageList;

  public Client(String name) {
    this.name = name;
    registered = true;
    messageList = new LinkedList<>();
  }

  public boolean addMessage(Message message) {
    return messageList.add(message);
  }

  public String getName() {
    return name;
  }

  public Boolean isRegistered() {
    return registered;
  }

  public void setRegistered(Boolean registered) {
    this.registered = registered;
  }

  public List<Message> getUnreadMessages() {
    List<Message> result;
    result= messageList.stream().filter(m -> !m.isRead()).collect(Collectors.toList());
    return result;
  }

  public List<Message> getAllMessages() {
    return messageList;
  }

  @Override
  public String toString() {
    return "Client{" +
        "name='" + name + '\'' +
        ", registered=" + registered +
        ", messageList=" + messageList +
        '}';
  }
}
