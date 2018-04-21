package app.kiti.com.kitiadmin;

import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;


/**
 * Created by Ankit on 4/12/2018.
 */

public class SyncManager {

    private final FirebaseDatabase database;

    public SyncManager() {
        database = FirebaseDatabase.getInstance();
    }

    public DatabaseReference getRedemptionNodeRef() {

        return database.getReference()
                .child(FirebaseDataField.REDEMPTION_REQ);
    }

    public DatabaseReference getCompletedNodeRef() {

        return database.getReference()
                .child(FirebaseDataField.COMPLETED_REQ);
    }

    public DatabaseReference getPendingRequestNode(String userPhone) {

        return database.getReference()
                .child(FirebaseDataField.REDEMPTION_REQ)
                .child(userPhone);

    }

    public DatabaseReference getCompletedRequestNode(String userPhone) {

        return database.getReference()
                .child(FirebaseDataField.COMPLETED_REQ)
                .child(userPhone);
    }

    public void settleUpTransaction(final String userPhone, final CompletedRequestModel completedRequestModel) {

        //1. deduct from "amount_under_process:
        database.getReference()
                .child(FirebaseDataField.USERS)
                .child(userPhone)
                .child(FirebaseDataField.AMOUNT_UNDER_REQUEST)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null) {
                            long amount_under_process = (long) dataSnapshot.getValue();
                            //deduct
                            amount_under_process -= completedRequestModel.amount;
                            updateAmountUnderProcess(userPhone, amount_under_process, completedRequestModel);

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


    }

    private void updateAmountUnderProcess(String userPhone, long updated_amount_under_process, CompletedRequestModel completedRequestModel) {

        database.getReference()
                .child(FirebaseDataField.USERS)
                .child(userPhone)
                .child(FirebaseDataField.AMOUNT_UNDER_REQUEST)
                .setValue(updated_amount_under_process);

        addAmountToRedeemNode(userPhone, completedRequestModel.amount , completedRequestModel);


    }

    private void addAmountToRedeemNode(final String userPhone, final long amount, final CompletedRequestModel completedRequestModel) {

        //2. Add amount to "redeemedAmount"
        database.getReference()
                .child(FirebaseDataField.USERS)
                .child(userPhone)
                .child(FirebaseDataField.REDEEMED_AMOUNT)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null) {
                            long redeemedAmount = (long) dataSnapshot.getValue();
                            //deduct
                            redeemedAmount += amount;
                            updateAmountRedeemed(userPhone, redeemedAmount, completedRequestModel);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    private void updateAmountRedeemed(String userPhone, long redeemedAmount, CompletedRequestModel completedRequestModel) {

        database.getReference()
                .child(FirebaseDataField.USERS)
                .child(userPhone)
                .child(FirebaseDataField.REDEEMED_AMOUNT)
                .setValue(redeemedAmount);

        addCompletionNode(userPhone, completedRequestModel);

    }

    private void addCompletionNode(String userPhone, CompletedRequestModel completedRequestModel) {
        //3. Add completion model to node
        database.getReference().child(FirebaseDataField.COMPLETED_REQ)
                .child(userPhone)
                .child(completedRequestModel.transactionId)
                .setValue(completedRequestModel);

        deleteRequestNode(userPhone, completedRequestModel.requestId);

    }

    private void deleteRequestNode(String userPhone, String requestId) {

        //4. Delete pending request node

        database.getReference()
                .child(FirebaseDataField.REDEMPTION_REQ)
                .child(userPhone)
                .child(requestId)
                .removeValue();

        if (firebaseSyncListener != null) {
            firebaseSyncListener.done();
        }

        //DONE!!!
    }

    private FirebaseSyncListener firebaseSyncListener;

    public void setFirebaseSyncListener(FirebaseSyncListener firebaseSyncListener) {
        this.firebaseSyncListener = firebaseSyncListener;
    }

    public interface FirebaseSyncListener {
        void done();
    }

}
