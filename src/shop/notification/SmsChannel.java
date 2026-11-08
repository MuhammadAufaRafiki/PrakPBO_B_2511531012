package shop.notification;

public class SmsChannel extends AbstractChannel {
    private final int maxLength;

    public SmsChannel(int maxLength) {
        this.maxLength = maxLength;
    }

    @Override
    public String channelName() {
        return "SMS";
    }

    @Override
    protected String resolveRecipient(CustomerContact contact) {
        String phone = contact.getPhone();
        if (phone != null && phone.startsWith("08")) {
            return phone;
        }
        return null;
    }

    @Override
    protected String prepare(String message) {
        // T2.3: Memotong pesan jika melebihi maxLength, diakhiri "..."
        if (message.length() > maxLength) {
            return message.substring(0, maxLength - 3) + "...";
        }
        return message;
    }

    @Override
    protected boolean deliver(String recipient, String preparedMessage) {
        System.out.println("[SMS to " + recipient + "] " + preparedMessage);
        return true;
    }
}