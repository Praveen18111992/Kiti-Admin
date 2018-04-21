package app.kiti.com.kitiadmin;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ankit on 4/19/2018.
 */

public class PendingTransactionListAdapter extends ArrayAdapter<RedeemRequestModel> {

    private Context mContext;
    private ArrayList<RedeemRequestModel> redeemRequestModels;

    public PendingTransactionListAdapter(@NonNull Context context) {
        super(context, 0);
        this.mContext = context;
        redeemRequestModels = new ArrayList<>();
    }

    public void setRedeemRequestModels(ArrayList<RedeemRequestModel> requestModels) {
        this.redeemRequestModels = requestModels;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.pending_transaction_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.bind(redeemRequestModels.get(position) , settleTransactionListener);
        return convertView;

    }

    @Override
    public int getCount() {
        return redeemRequestModels.size();
    }

    static class ViewHolder {
        @BindView(R.id.month_date)
        TextView monthDate;
        @BindView(R.id.via)
        TextView via;
        @BindView(R.id.phone_number)
        TextView phoneNumber;
        @BindView(R.id.request_id)
        TextView requestId;
        @BindView(R.id.info_container)
        LinearLayout infoContainer;
        @BindView(R.id.requestedAmount)
        TextView requestedAmount;
        @BindView(R.id.settleBtn)
        TextView settleBtn;
        @BindView(R.id.transaction_id)
        EditText transactionId;
        private Context context;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        public void bind(final RedeemRequestModel redeemRequestModel, final SettleTransactionListener settleTransactionListener) {
            this.context = context;
            //set date month
            long millis = TimeUtils.getMillisFrom(redeemRequestModel.requestedAt);
            String month = TimeUtils.getMonthFrom(millis);
            String date = TimeUtils.getDateFrom(millis);
            String year = TimeUtils.getYearFrom(millis);

            monthDate.setText(String.format("%s %s %s", date, month, year));
            via.setText(redeemRequestModel.requestedVia);
            phoneNumber.setText(redeemRequestModel.requestOnNumber);
            requestId.setText(redeemRequestModel.requestId);
            requestedAmount.setText(String.format("\u20B9 %d", redeemRequestModel.amount));

            settleBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String txnId = transactionId.getText().toString();

                    if (txnId.length() != 0) {

                        CompletedRequestModel completedRequestModel = new CompletedRequestModel(redeemRequestModel.amount,
                                TimeUtils.getTime(),
                                "PAYTM",
                                redeemRequestModel.requestOnNumber,
                                redeemRequestModel.requestId,
                                txnId
                        );

                        if(settleTransactionListener!=null){
                            settleTransactionListener.onSettleTransaction(completedRequestModel);
                        }

                    }

                }
            });

        }

    }

    private SettleTransactionListener settleTransactionListener;

    public void setSettleTransactionListener(SettleTransactionListener settleTransactionListener) {
        this.settleTransactionListener = settleTransactionListener;
    }

    public interface SettleTransactionListener{
        void onSettleTransaction(CompletedRequestModel completedRequestModel);
    }

}

