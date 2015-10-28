package fr.scaron.sfreeboxtools.model;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.scaron.sfreeboxtools.R;
import fr.scaron.sfreeboxtools.activity.AbstractActivity;

/**
 * Created by tilucifer on 27/10/2015.
 */
public class FavSearchAdapter extends RecyclerView.Adapter<FavSearchAdapter.ViewHolder> {
    public static Logger log = LoggerFactory.getLogger(FavSearchAdapter.class);
    private List<FavSearch> itemsData;
    AbstractActivity follower;
    RecyclerView recyclerView;
    private TinyDB myDB;

    public FavSearchAdapter(AbstractActivity follower, RecyclerView recyclerView) {//, List<FavSearch> itemsData
        this.follower = follower;

        initItemsData();
        follower.updateTitle(String.valueOf(itemsData.size()));

        log.info("on set les données de l'adapteur avec une liste null ? "+(itemsData==null));
        if (itemsData==null||itemsData.size()==0){
            log.info("aucune donnée dans la liste :(");
            this.itemsData = new ArrayList<FavSearch>();
        }else{
            log.info("on ajoute "+itemsData.size()+" élément(s) dans la liste");
            //this.itemsData = itemsData;
        }
        follower.updateTitle(String.valueOf(itemsData.size()));

        super.notifyDataSetChanged();



        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT|ItemTouchHelper.DOWN|ItemTouchHelper.DOWN) {
                    @Override
                    public boolean onMove(
                            final RecyclerView recyclerView,
                            final RecyclerView.ViewHolder source,
                            final RecyclerView.ViewHolder target) {
                        if (source.getItemViewType() != target.getItemViewType()) {
                            return false;
                        }

                        // Notify the adapter of the move
                        Collections.swap(FavSearchAdapter.this.itemsData, source.getAdapterPosition(), target.getAdapterPosition());
                        //notifyItemMoved(source.getAdapterPosition(), target.getAdapterPosition());
                        notifyDataSetChanged();
                        return true;
                    }

                    @Override
                    public void onSwiped(
                            final RecyclerView.ViewHolder viewHolder,
                            final int swipeDir) {
                        switch (swipeDir){
                            case ItemTouchHelper.LEFT:
                                showDeleteDialog(viewHolder.getAdapterPosition());
                                break;
                            case ItemTouchHelper.RIGHT:
                                showModifyDialog(viewHolder.getAdapterPosition());
                                break;
                            default:
                                notifyDataSetChanged();
                                break;
                        }
                    }
                };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                simpleItemTouchCallback
        );
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }


    private void showDeleteDialog(final int itemPosition){

        final View alertDialogView = LayoutInflater.from(follower.getApplicationContext()).inflate(R.layout.favsearch_list_delete, null);

        TextView tv = (TextView)alertDialogView.findViewById(R.id.favsearchListDelete_title);
        tv.setText("Le favori "+FavSearchAdapter.this.itemsData.get(itemPosition).getName()+" sera supprimé !");
        //Création de l'AlertDialog
        AlertDialog.Builder adb = new AlertDialog.Builder(follower, AlertDialog.THEME_HOLO_DARK);

        //On affecte la vue personnalisé que l'on a crée à notre AlertDialog
        adb.setView(alertDialogView);

        //On donne un titre à l'AlertDialog
        adb.setTitle("Supprimer le favori ?");

        //On modifie l'icône de l'AlertDialog pour le fun ;)
        adb.setIcon(android.R.drawable.ic_dialog_alert);

        //On affecte un bouton "OK" à notre AlertDialog et on lui affecte un évènement
        adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                //Lorsque l'on cliquera sur le bouton "OK", on récupère l'EditText correspondant à notre vue personnalisée (cad à alertDialogView)
                FavSearchAdapter.this.itemsData.remove(itemPosition);
                sayDataSetChanged();
                //notifyItemRemoved(viewHolder.getAdapterPosition());

            }
        });

        //On crée un bouton "Annuler" à notre AlertDialog et on lui affecte un évènement
        adb.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //notifyItemChanged(itemPosition);
                sayDataSetChanged();
                //Lorsque l'on cliquera sur annuler on quittera l'application
                dialog.dismiss();
            } });
        adb.show();
    }

    private void showModifyDialog(final int itemPosition){

        final View alertDialogView = LayoutInflater.from(follower.getApplicationContext()).inflate(R.layout.favsearch_list_addmodify, null);

        EditText et = (EditText)alertDialogView.findViewById(R.id.favsearchListAddModify_name);
        et.setText(FavSearchAdapter.this.itemsData.get(itemPosition).getName());
        //Création de l'AlertDialog
        AlertDialog.Builder adb = new AlertDialog.Builder(follower, AlertDialog.THEME_HOLO_DARK);

        //On affecte la vue personnalisé que l'on a crée à notre AlertDialog
        adb.setView(alertDialogView);

        //On donne un titre à l'AlertDialog
        adb.setTitle("Modifier le favori ?");

        //On modifie l'icône de l'AlertDialog pour le fun ;)
        adb.setIcon(android.R.drawable.ic_dialog_alert);

        //On affecte un bouton "OK" à notre AlertDialog et on lui affecte un évènement
        adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                //Lorsque l'on cliquera sur le bouton "OK", on récupère l'EditText correspondant à notre vue personnalisée (cad à alertDialogView)
                EditText et = (EditText)alertDialogView.findViewById(R.id.favsearchListAddModify_name);
                FavSearchAdapter.this.itemsData.remove(itemPosition);
                FavSearchAdapter.this.itemsData.add(new FavSearch(FavSearchAdapter.this.itemsData.get(itemPosition).getIndex(), et.getText().toString()));
                sayDataSetChanged();
               //notifyItemChanged(itemPosition);

            } });

        //On crée un bouton "Annuler" à notre AlertDialog et on lui affecte un évènement
        adb.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //notifyItemChanged(itemPosition);
                sayDataSetChanged();
                //Lorsque l'on cliquera sur annuler on quittera l'application
                dialog.dismiss();
            } });
        adb.show();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public FavSearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        log.info("On cree la vue depuis le layout favsearch_list_item");
        // create a new view
        View itemLayoutView = LayoutInflater.from(follower.getApplicationContext())
                .inflate(R.layout.favsearch_list_item, null);
       /* View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.favsearch_list_item, null);*/

        log.info("La vue contenant le layout favsearch_list_item est vide ? "+(itemLayoutView==null));
        // create ViewHolder

        log.info("On cree la vue viewHolder");
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        log.info("La vue viewHolder est vide ? " + (viewHolder == null));
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        log.info("La vue viewHolder à remplir est vide ? "+(viewHolder==null));
        log.info("On remplie la vue viewHolder avec l'item position n°"+position);
        log.info("La liste itemsData contient "+itemsData.size()+" élément(s)");


        log.info("La vue viewHolder.favsearchItemId à remplir est vide ? "+(viewHolder.favsearchItemId==null));


        log.info("La vue viewHolder.favsearchItemTitle à remplir est vide ? "+(viewHolder.favsearchItemTitle==null));
        // - get data from your itemsData at this position
        // - replace the contents of the view with that itemsData

        viewHolder.favsearchItemId.setText((position+1) + " ) ");
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


    public void sayDataSetChanged(){
        super.notifyDataSetChanged();
        follower.updateTitle(String.valueOf(itemsData.size()));
        saveItemsData();
    }

    public void setItemsData(List<FavSearch> itemsData){
        this.itemsData = itemsData;
    }

    public void initItemsData(){
        myDB = new TinyDB(follower);
        ArrayList<String> t411Suggestions = myDB.getList("T411Suggestions");
        if (t411Suggestions == null) {
            t411Suggestions = new ArrayList<String>();
        }
        List<FavSearch> favSearches = new ArrayList<FavSearch>();

        log.info("t411Suggestions null ? : "+(t411Suggestions==null));
        if (t411Suggestions !=null) {
            log.info("nb de favsearch trouvees: " + t411Suggestions.size());
            int index = 0;

            // this is data fro recycler view
            for (String t411Suggestion : t411Suggestions) {
                FavSearch favSearch = new FavSearch(index+1, t411Suggestion);
                log.info("favsearch n°"+index+" : " + t411Suggestion);
                favSearches.add(favSearch);
                index++;
            }
        }
        setItemsData(favSearches);
    }


    public void saveItemsData(){

        ArrayList<String> t411Suggestions = new ArrayList<String>();
        myDB = new TinyDB(follower);
        log.info("t411Suggestions null ? : " + (t411Suggestions==null));
        if (t411Suggestions !=null) {
            log.info("nb de favsearch trouvees: " + t411Suggestions.size());
            // this is data from itemsData
            for (FavSearch favSearch : itemsData) {
                log.info("favsearch n°"+favSearch.getIndex()+" : " + favSearch.getName());
                t411Suggestions.add(favSearch.getName());
            }
        }
        myDB.putList("T411Suggestions", t411Suggestions);
    }

    public List<FavSearch> getItemsData(){
        return this.itemsData;
    }
}