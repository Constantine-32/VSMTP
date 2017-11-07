package Server;

import java.util.LinkedList;

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

  public LinkedList<Message> getUnreadMessages() {
    LinkedList<Message> result = new LinkedList<>();
    for (Message t : messageList) {
      if (!t.isRead()) result.add(t);
    }
    return result;
  }

  public LinkedList<Message> getAllMessages() {
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
