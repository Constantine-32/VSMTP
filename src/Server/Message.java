package Server;

public class Message {
  private String from;
  private String subject;
  private String text;
  private Boolean read;

  public Message(String from, String subject, String text) {
    this.from = from;
    this.subject = subject;
    this.text = text;
    this.read = false;
  }

  public boolean isRead() {
    return read;
  }

  public void read() {
    read = true;
  }

  public String getMessage() {
    return from+':'+subject+':'+text;
  }

  @Override
  public String toString() {
    return "Message{" +
        "from='" + from + '\'' +
        ", subject='" + subject + '\'' +
        ", text='" + text + '\'' +
        '}';
  }
}
