package Support;

import java.util.*;

public class Group extends Client {
  private LinkedList<Client> clients;

  public Group(String name) {
    super(name);
    clients = new LinkedList<>();
  }

  @Override
  public void addMessage(Message message) {
    for (Client client : clients) client.addMessage(message.clone());
  }

  public void addClient(Client client) {
    clients.add(client);
  }

  public boolean hasClient(String username) {
    for (Client client : clients)
      if (client.getName().equals(username)) return true;
    return false;
  }
}
