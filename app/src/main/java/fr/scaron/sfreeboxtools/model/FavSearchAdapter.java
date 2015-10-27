package fr.scaron.sfreeboxtools.model;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import fr.scaron.sfreeboxtools.R;

/**
 * Created by tilucifer on 27/10/2015.
 */
public class FavSearchAdapter extends RecyclerView.Adapter<FavSearchAdapter.ViewHolder> {
    private List<FavSearch> itemsData;

    public FavSearchAdapter(List<FavSearch> itemsData) {
        this.itemsData = itemsData;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public FavSearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.favsearch_list_item, null);

        // create ViewHolder

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        // - get data from your itemsData at this position
        // - replace the contents of the view with that itemsData

        viewHolder.favsearchItemId.setText(itemsData.get(position).getIndex());
        viewHolder.favsearchItemTitle.setText(itemsData.get(position).getName());


    }

    // inner class to hold a reference to each item of RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView favsearchItemId;
        public TextView favsearchItemTitle;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            favsearchItemId = (TextView) itemLayoutView.findViewById(R.id.favsearch_item_id);
            favsearchItemTitle = (TextView) itemLayoutView.findViewById(R.id.favsearch_item_title);
        }
    }


    // Return the size of your itemsData (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return itemsData.size();
    }
}