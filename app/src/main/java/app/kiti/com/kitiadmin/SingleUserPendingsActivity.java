package app.kiti.com.kitiadmin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SingleUserPendingsActivity extends AppCompatActivity implements PendingTransactionListAdapter.SettleTransactionListener, SyncManager.FirebaseSyncListener {


    @BindView(R.id.transactions_listView)
    ListView transactionsListView;
    private SyncManager syncManager;
    private PendingTransactionListAdapter pendingTransactionListAdapter;
    private String userPhone;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_user_pendings);
        ButterKnife.bind(this);
        userPhone = getIntent().getStringExtra("userPhone");
        syncManager = new SyncManager();
        syncManager.setFirebaseSyncListener(this);
        pendingTransactionListAdapter = new PendingTransactionListAdapter(this);
        pendingTransactionListAdapter.setSettleTransactionListener(this);
        if (transactionsListView != null) {
            transactionsListView.setAdapter(pendingTransactionListAdapter);
        }

        fetchRequestedTransaction();

    }

    private void fetchRequestedTransaction() {

        syncManager.getPendingRequestNode(userPhone).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //collect earnings
                    collectPendingTransaction((Map<String, Object>) dataSnapshot.getValue());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void collectPendingTransaction(Map<String, Object> pendingTransaction) {

        ArrayList<RedeemRequestModel> redeemRequestModels = new ArrayList<>();
        for (Map.Entry<String, Object> entry : pendingTransaction.entrySet()) {
            //set map
            Map map = (Map) entry.getValue();

            RedeemRequestModel redeemRequestModel = new RedeemRequestModel(
                    (long) map.get(FirebaseDataField.AMOUNT),
                    (String) map.get(FirebaseDataField.REQUEST_ID),
                    (String) map.get(FirebaseDataField.REQUESTED_AT),
                    (String) map.get(FirebaseDataField.REQUESTED_VIA),
                    (String) map.get(FirebaseDataField.REQUESTED_ON_NUMBER)
            );

            redeemRequestModels.add(redeemRequestModel);

        }

        setRedeemRequestList(redeemRequestModels);

    }

    private void setRedeemRequestList(ArrayList<RedeemRequestModel> redeemRequestModels) {

        if (pendingTransactionListAdapter != null) {
            pendingTransactionListAdapter.setRedeemRequestModels(redeemRequestModels);
        }
    }

    @Override
    public void onSettleTransaction(CompletedRequestModel completedRequestModel) {
        showAlert(completedRequestModel);
    }


    private void showAlert(final CompletedRequestModel completedRequestModel) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Settle")
                .setCancelable(false)
                .setMessage("Amount is sent to user`s account ?")
                .setPositiveButton("Yes , Settle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        settleTransaction(completedRequestModel);
                    }
                })
                .setNegativeButton("No", null);

        builder.show();
    }

    private void settleTransaction(CompletedRequestModel completedRequestModel) {

        showProgress(completedRequestModel.transactionId);
        syncManager.settleUpTransaction(userPhone, completedRequestModel);

    }

    private void showProgress(String msg) {

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("Processing..." + msg);
        dialog.show();

    }

    private void hideProgres() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    public void done() {
        hideProgres();
        fetchRequestedTransaction();
    }
}
