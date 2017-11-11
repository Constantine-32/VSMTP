package Support;

public class Message implements Cloneable {
  private String from;
  private String subject;
  private String text;
  private boolean read;

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
    return "From: " + from + "\nSubject: " + subject + "\nMessage: " + text;
  }

  @Override
  public Message clone() {
    return new Message(from, subject, text);
  }
}
