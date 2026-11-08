package shop.payment;

public interface RefundablePayment extends PaymentMethod {
    boolean refund(long amount);

    //Policy default untuk refund
    default String policy() {
        return "Refund maksimal 7 hari";
    }
    
}