package dk.alroe.apps.octopub;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import dk.alroe.apps.octopub.model.Thread;

public class ThreadAdapter extends android.support.v7.widget.RecyclerView.Adapter<ThreadAdapter.ViewHolder> {
    private ArrayList<Thread> dataset;
    private Context context;

    private OnItemClickListener onItemClickListener;

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
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Thread thread = dataset.get(position);
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        // Text in the id textview consists of the thread id separated in the middle by a newline.
        String idText = thread.getId().substring(0, 3) + "\n" + thread.getId().substring(3);
        holder.id.setText(idText);
        holder.title.setText(thread.getTitle());
        holder.length.setText(context.getString(R.string.thread_length_prefix) + thread.getLength());
        SharedPreferences lengthStore = context.getSharedPreferences("threadLength", 0);
        int lastLength = lengthStore.getInt(thread.getId(), -2);
        if (lastLength == -2) {
            holder.lengthConnector.setText(" - ");
            holder.lengthHighlight.setText("New thread!");
        } else if (lastLength != thread.getLength()) {
            holder.lengthConnector.setText(" - ");
            holder.lengthHighlight.setText((thread.getLength() - lastLength) + context.getString(R.string.thread_length_new));
        } else {
            holder.lengthConnector.setText("");
            holder.lengthHighlight.setText("");
        }
        int bgColor = Color.parseColor("#" + thread.getId());
        if (ColorHelper.isBrightColor(bgColor)) {
            holder.id.setTextColor(context.getResources().getColor(R.color.textDark));
        } else {
            holder.id.setTextColor(context.getResources().getColor(R.color.textBright));
        }
        holder.id.setBackgroundColor(bgColor);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(thread);
            }
        };
        holder.card.setOnClickListener(listener);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (dataset == null) {
            return 0;
        }
        return dataset.size();
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public LinearLayout card;
        public TextView title;
        public TextView id;
        public TextView length;
        public TextView lengthConnector;
        public TextView lengthHighlight;

        public ViewHolder(View v) {
            super(v);
            this.card = (LinearLayout) v.findViewById(R.id.thread_card);
            this.title = (TextView) v.findViewById(R.id.thread_title);
            this.id = (TextView) v.findViewById(R.id.thread_id);
            this.length = (TextView) v.findViewById(R.id.thread_length);
            this.lengthConnector = (TextView) v.findViewById(R.id.thread_length_connector);
            this.lengthHighlight = (TextView) v.findViewById(R.id.thread_length_highlight);
        }
    }

}
