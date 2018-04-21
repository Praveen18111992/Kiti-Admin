package app.kiti.com.kitiadmin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SingleUserCompletedActivity extends AppCompatActivity {


    @BindView(R.id.transactions_listView)
    ListView transactionsListView;
    private SyncManager syncManager;
    private CompletedTransactionListAdapter completedListAdapter;
    private String userPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_user_completed);
        ButterKnife.bind(this);
        userPhone = getIntent().getStringExtra("userPhone");
        syncManager = new SyncManager();
        completedListAdapter = new CompletedTransactionListAdapter(this);

        if (transactionsListView != null) {
            transactionsListView.setAdapter(completedListAdapter);
        }

        fetchCompletedTransaction();

    }

    private void fetchCompletedTransaction() {

        syncManager.getCompletedRequestNode(userPhone).addValueEventListener(new ValueEventListener() {
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

    private void collectPendingTransaction(Map<String, Object> completedTransaction) {

        ArrayList<CompletedRequestModel> completedRequestModels = new ArrayList<>();

        for (Map.Entry<String, Object> entry : completedTransaction.entrySet()) {
            //set map
            Map map = (Map) entry.getValue();

            CompletedRequestModel redeemRequestModels = new CompletedRequestModel(
                    (long) map.get(FirebaseDataField.AMOUNT),
                    (String) map.get(FirebaseDataField.COMPLETED_AT),
                    (String) map.get(FirebaseDataField.COMPLETED_VIA),
                    (String) map.get(FirebaseDataField.COMPLETED_ON_NUMBER),
                    (String) map.get(FirebaseDataField.REQUEST_ID),
                    (String) map.get(FirebaseDataField.TRANSACTION_ID)
            );
            completedRequestModels.add(redeemRequestModels);

        }

        setRedeemRequestList(completedRequestModels);

    }

    private void setRedeemRequestList(ArrayList<CompletedRequestModel> redeemRequestModels) {

        if (completedListAdapter != null) {
            completedListAdapter.setCompletedRequestModels(redeemRequestModels);
        }
    }

}
