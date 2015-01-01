package android.handyapps.gareth.animalalert;

/**
 * Created by Gareth on 2015-01-01.
 */
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


public class AlertMessageAdapter extends RecyclerView.Adapter<AlertMessageAdapter.ViewHolder> {

    ArrayList<String> timearr = new ArrayList<>();
    ArrayList<String> msgarr = new ArrayList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public View mTextView;

        public ViewHolder(View view) {
            super(view);
            mTextView = view;
        }
    }

    public AlertMessageAdapter(ArrayList<String> myDataSetTime,ArrayList<String>myDataSetMsg) {
        timearr = myDataSetTime;
        msgarr = myDataSetMsg;
    }

    @Override
    // Called when RecyclerView needs a new RecyclerView.ViewHolder of the given type to represent an item.
    public AlertMessageAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    // Called by RecyclerView to display the data at the specified position.
    public void onBindViewHolder(AlertMessageAdapter.ViewHolder viewHolder, int i) {
        TextView time  = (TextView)viewHolder.mTextView.findViewById(R.id.alertDateTime);
        TextView msg   = (TextView)viewHolder.mTextView.findViewById(R.id.alertMessage);
        time.setText(timearr.get(i));
        msg.setText(msgarr.get(i));

    }

    @Override
    // Returns the total number of items in the data set hold by the adapter.
    public int getItemCount() {
        return timearr.size();
    }
}

