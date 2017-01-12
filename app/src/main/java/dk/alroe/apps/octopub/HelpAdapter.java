package dk.alroe.apps.octopub;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import us.feras.mdv.MarkdownView;

public class HelpAdapter extends android.support.v7.widget.RecyclerView.Adapter<HelpAdapter.ViewHolder> {
    private ArrayList<String> dataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public MarkdownView helpText;

        public ViewHolder(View v) {
            super(v);
            this.helpText = (MarkdownView) v.findViewById(R.id.help_view);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public HelpAdapter(Context context, ArrayList<String> nDataset) {
        dataset = nDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public HelpAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.help_card, parent, false);
        //View i = LayoutInflater.from(parent.getContext()).inflate(R.layout.)
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.helpText.loadMarkdown(dataset.get(position));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (dataset == null) {
            return 0;
        }
        return dataset.size();
    }
}
