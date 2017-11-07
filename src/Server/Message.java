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
        this.read = true;
    }

}
