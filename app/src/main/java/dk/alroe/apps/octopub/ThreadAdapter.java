package dk.alroe.apps.octopub;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class ThreadAdapter extends android.support.v7.widget.RecyclerView.Adapter<ThreadAdapter.ViewHolder> {
    private ArrayList<Thread> dataset;
    private Context context;

    private OnItemClickListener onItemClickListener;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView title;
        public TextView id;
        public ViewHolder(View v) {
            super(v);
            this.title = (TextView) v.findViewById(R.id.thread_title);
            this.id = (TextView) v.findViewById(R.id.thread_id);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ThreadAdapter(Context context, ArrayList<Thread> nDataset) {
        this.context = context;
        dataset = nDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ThreadAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.thread_card, parent, false);
        //View i = LayoutInflater.from(parent.getContext()).inflate(R.layout.)
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Thread thread = dataset.get(position);
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.id.setText(thread.getId().substring(0,3)+"\n"+thread.getId().substring(3));
        holder.title.setText(thread.getTitle());
        int bgColor = Color.parseColor("#"+thread.getId());
        if (ColorHelper.isBrightColor(bgColor)){
            holder.id.setTextColor(context.getResources().getColor(R.color.textDark));
        }else {
            holder.id.setTextColor(context.getResources().getColor(R.color.textBright));
        }
        holder.id.setBackgroundColor(bgColor);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(thread);
            }
        };
        holder.title.setOnClickListener(listener);
        holder.id.setOnClickListener(listener);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (dataset == null){return 0;}
        return dataset.size();
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

}
