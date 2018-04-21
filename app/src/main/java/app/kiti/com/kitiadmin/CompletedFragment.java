package app.kiti.com.kitiadmin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Ankit on 4/21/2018.
 */

public class CompletedFragment extends Fragment {

    @BindView(R.id.pending_list)
    ListView pendingList;
    private Context mContext;
    private Unbinder unbinder;
    SyncManager syncManager;
    private ArrayList<String> mCompletedList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        syncManager = new SyncManager();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pending_fragments, null);
        unbinder = ButterKnife.bind(this, view);
        fetchPendingList();
        attachListeners();
        return view;

    }

    private void attachListeners() {

        pendingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openSingleUserPage(position);
            }
        });

    }

    private void openSingleUserPage(int position) {

        Intent intent = new Intent(mContext, SingleUserCompletedActivity.class);
        intent.putExtra("userPhone", mCompletedList.get(position));
        mContext.startActivity(intent);

    }


    private void fetchPendingList() {

        syncManager.getCompletedNodeRef()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null) {
                            collectPendingList((Map<String, Object>) dataSnapshot.getValue());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void collectPendingList(Map<String, Object> map) {

        ArrayList<String> phones = new ArrayList<>();
        if (map != null) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                phones.add(entry.getKey());
            }
        }
        this.mCompletedList = phones;
        if(pendingList!=null) {
            pendingList.setAdapter(new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, phones));
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
