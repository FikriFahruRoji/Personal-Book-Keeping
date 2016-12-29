package id.barkost.personalbookkeeping.model;

/**
 * Created by fikri on 21/12/16.
 */

public class ModelTransaction {
    private String transaction_date, transaction_type, transaction_detail;
    private int transaction_id, transaction_amount;

    public ModelTransaction() {}

    public ModelTransaction(int transaction_id, String transaction_date, String transaction_type, String transaction_detail, int transaction_amount) {
        this.transaction_id = transaction_id;
        this.transaction_date = transaction_date;
        this.transaction_type = transaction_type;
        this.transaction_detail = transaction_detail;
        this.transaction_amount = transaction_amount;
    }

    public int getTransaction_id() {return transaction_id;}
    public void setTransaction_id(int transaction_id) {this.transaction_id = transaction_id;}

    public String getTransaction_date() {return transaction_date;}
    public void setTransaction_date(String transaction_date) {this.transaction_date = transaction_date;}

    public String getTransaction_type() {return transaction_type;}
    public void setTransaction_type(String transaction_type) {this.transaction_type = transaction_type;}

    public String getTransaction_detail() {return transaction_detail;}
    public void setTransaction_detail(String transaction_detail) {this.transaction_detail = transaction_detail;}

    public int getTransaction_amount() {return transaction_amount;}
    public void setTransaction_amount(int transaction_amount) {this.transaction_amount = transaction_amount;}
}
