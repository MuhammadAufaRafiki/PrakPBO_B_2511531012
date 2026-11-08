package shop.notification;

public class EmailChannel extends AbstractChannel {

    @Override
    public String channelName() {
        return "EMAIL";
    }

    @Override
    protected String resolveRecipient(CustomerContact contact) {
        String email = contact.getEmail();
        // T2.4: Menolak kontak tanpa email yang valid (harus mengandung '@')
        if (email != null && email.contains("@")) {
            return email;
        }
        return null;
    }

    @Override
    protected boolean deliver(String recipient, String preparedMessage) {
        System.out.println("[EMAIL to " + recipient + "] " + preparedMessage);
        return true;
    }
}