package model.enums;

public enum TransactionType {
    DEPOSIT("Deposit"),
    WITHDRAW("Withdraw"),
    TRANSFER_SENT("Transfer Sent"),
    TRANSFER_RECEIVED("Transfer Received"),
    BILL_PAYMENT("Bill Payment"),
    PIX_PAYMENT("Pix Payment"),
    TED("Ted"),
    DOC("Doc"),
    REVERSAL("Reversal"),
    INTEREST("Interest"),
    FEE("Fee");

    private final String value;

    TransactionType(String description) {
        this.value = description;
    }

    public String getValue() {
        return value;
    }
}
