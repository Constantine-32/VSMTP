package Support;

import java.util.*;
import java.util.stream.*;

public class Client {
  private String name;
  private LinkedList<Message> messages;
  private boolean active;

  public Client(String name) {
    this.name = name;
    messages = new LinkedList<>();
    active = true;
  }

  public void addMessage(Message message) {
    messages.add(message);
  }

  public String getName() {
    return name;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public List<Message> getUnreadMessages() {
    return messages.stream().filter(m -> !m.isRead()).collect(Collectors.toList());
  }

  public List<Message> getAllMessages() {
    return messages;
  }

  @Override
  public String toString() {
    return "Client{" +
        "name='" + name + '\'' +
        ", active=" + active +
        ", messages=" + messages +
        '}';
  }
}
