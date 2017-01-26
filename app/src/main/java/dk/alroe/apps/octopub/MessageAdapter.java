package dk.alroe.apps.octopub;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import dk.alroe.apps.octopub.model.Message;


/**
 * Created by Silas on 17-12-2016.
 */

public class MessageAdapter extends android.support.v7.widget.RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private ArrayList<Message> dataset = new ArrayList<>();
    private Context context;

    // Provide a suitable constructor (depends on the kind of dataset)
    public MessageAdapter(Context context, ArrayList<Message> nDataset) {
        this.context = context;
        dataset = nDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_card, parent, false);
        //View i = LayoutInflater.from(parent.getContext()).inflate(R.layout.)
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Message message = dataset.get(position);
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.id.setText(message.getId().substring(0, 3) + message.getId().substring(3));
        holder.text.loadMarkdown(message.getText(), "file:///android_res/raw/style.css");
        int bgColor = Color.parseColor("#" + message.getId());
        if (ColorHelper.isBrightColor(bgColor)) {
            holder.id.setTextColor(context.getResources().getColor(R.color.textDark));
        } else {
            holder.id.setTextColor(context.getResources().getColor(R.color.textBright));
        }
        holder.id.setBackgroundColor(bgColor);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataset.size();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public MarkdownViewRework text;
        public TextView id;

        public ViewHolder(View v) {
            super(v);
            this.text = (MarkdownViewRework) v.findViewById(R.id.message_markdown);
            this.id = (TextView) v.findViewById(R.id.message_id);
            text.getSettings().setJavaScriptEnabled(true);
        }
    }


}