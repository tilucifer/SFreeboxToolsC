package fr.scaron.sfreeboxtools.model;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import fr.scaron.sfreeboxtools.R;
import fr.scaron.sfreeboxtools.activity.AbstractActivity;
import fr.scaron.sfreeboxtools.connect.HttpConnect;
import fr.scaron.sfreeboxtools.contexte.Params;
import fr.scaron.sfreeboxtools.control.FreeboxControler;
import android.net.*;
import android.content.*;

public class FreeboxBoxAdapter extends ArrayAdapter<FreeboxBox> implements Filterable{


	public static Logger log = LoggerFactory.getLogger(FreeboxBoxAdapter.class);
	LayoutInflater inflater;
	AbstractActivity follower;
    View lastSelected;
	private List<FreeboxBox> freeboxBoxs = new ArrayList<FreeboxBox>();
	TinyDB myDB;


	public FreeboxBoxAdapter(AbstractActivity follower,
                             int ressource, List<FreeboxBox> freeboxBoxs){
		super(follower, ressource);
		myDB = new TinyDB(follower);
		inflater = LayoutInflater.from(follower.getApplicationContext());
		this.follower = follower;
		this.freeboxBoxs = freeboxBoxs;
        follower.updateTitle(String.valueOf(freeboxBoxs.size()-1));
		//setFreeboxBoxs(freeboxBoxs);
	}
	
	public void setFreeboxBoxs(List<FreeboxBox> freeboxBoxs){
		follower.updateTitle(String.valueOf(freeboxBoxs.size()-1));
		this.freeboxBoxs = freeboxBoxs;
		super.notifyDataSetChanged();
	}
	
	public void addFreeboxBox(FreeboxBox freeboxBox){
		freeboxBox.setIndex(freeboxBoxs.size());
		freeboxBoxs.add(freeboxBox);
		follower.updateTitle(String.valueOf(freeboxBoxs.size()-1));
		
		myDB = new TinyDB(follower);
        ArrayList<String> boxNames = myDB.getList("FreeboxBoxNames");
        ArrayList<String> boxIps = myDB.getList("FreeboxBoxIps");
        ArrayList<String> boxPorts = myDB.getList("FreeboxBoxPorts");
        ArrayList<String> boxTokens = myDB.getList("FreeboxBoxTokens");
        ArrayList<Boolean> boxSelecteds = myDB.getListBoolean("FreeboxBoxSelecteds");

        List<FreeboxBox> freeboxBoxes = new ArrayList<FreeboxBox>();

        log.info("listebbox null ? : "+(boxNames==null));
        if (boxNames !=null) {
            log.info("nb de box trouvees avant ajout de l adapter: " + boxNames.size());
            int index = 0;
            for (String boxName : boxNames) {
                FreeboxBox box = new FreeboxBox();
                box.setName(boxName);
                box.setIp_public(boxIps.get(index));
                box.setApp_token(boxTokens.get(index));
                box.setPort(boxPorts.get(index));
                box.setSelected(boxSelecteds.get(index));
                box.setIndex(index);
                freeboxBoxes.add(box);
                index++;
            }
        }
		
		super.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return freeboxBoxs.size();
	}

	@Override
	public FreeboxBox getItem(int position) {
		return freeboxBoxs.get(position);
	}
	
	private class ViewFreeboxBoxInfo {
		TextView freebox_box_name;
		TextView freebox_box_ip;
        TextView freebox_box_port;
        //boolean selected=false;
        FreeboxBox box;
	}
	private void updateListBoxes(List<FreeboxBox> freeboxBoxs){
        log.debug("Debut de updaeListBoxes");

        //Mise jour de la liste des freebox
        ArrayList<String> boxNames = new ArrayList<String>();
        ArrayList<String>  boxIps = new ArrayList<String>();
        ArrayList<String> boxPorts = new ArrayList<String>();
        ArrayList<String> boxTokens = new ArrayList<String>();
        ArrayList<Boolean> boxSelecteds = new ArrayList<Boolean>();

        for (FreeboxBox freeboxBox : freeboxBoxs) {
            boxNames.add(freeboxBox.getName());
            boxIps.add(freeboxBox.getIp_public());
            boxPorts.add(freeboxBox.getPort());
            boxTokens.add(freeboxBox.getApp_token());
            boxSelecteds.add(freeboxBox.isSelected());
            log.info("Freebox n°"+freeboxBox.getIndex()+";"+freeboxBox.getName()+";"+freeboxBox.getIp_public()+";"+freeboxBox.getPort()+";"+freeboxBox.getApp_token()+";"+freeboxBox.isSelected());

        
        }

        myDB.putList("FreeboxBoxNames", boxNames);
        myDB.putList("FreeboxBoxIps", boxIps);
        myDB.putList("FreeboxBoxPorts", boxPorts);
        myDB.putList("FreeboxBoxTokens", boxTokens);
        myDB.putListBoolean("FreeboxBoxSelecteds", boxSelecteds);

        //setFreeboxBoxs(freeboxBoxs);
        follower.updateTitle(String.valueOf(freeboxBoxs.size()-1));
        super.notifyDataSetChanged();
        log.debug("Fin de updaeListBoxes");
		follower.setRefreshVisible(false);
    }
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        final FreeboxBox freeboxBox = freeboxBoxs.get(position);
        log.debug("GetView pour box : "+freeboxBox.getName());
        ViewFreeboxBoxInfo freeboxBoxInfo;
        if (convertView == null) {
            freeboxBoxInfo = new ViewFreeboxBoxInfo();
            convertView = inflater.inflate(R.layout.freebox_list_box_item, null);
            freeboxBoxInfo.freebox_box_name = (TextView) convertView.findViewById(R.id.freebox_box_name);
            freeboxBoxInfo.freebox_box_ip = (TextView) convertView.findViewById(R.id.freebox_box_ip);
            freeboxBoxInfo.freebox_box_port = (TextView) convertView.findViewById(R.id.freebox_box_port);
            convertView.setTag(freeboxBoxInfo);
        } else {
            freeboxBoxInfo = (ViewFreeboxBoxInfo) convertView.getTag();
        }

        freeboxBoxInfo.box = freeboxBox;
        if (freeboxBox.isSelected()){
            log.info(freeboxBoxInfo.box.getName()+" avait été selectionnée");
            ImageView iconLastSelected = (ImageView) convertView.findViewById(R.id.list_icon_selected);
            iconLastSelected.setVisibility(View.VISIBLE);
            lastSelected = convertView;
            //freeboxBoxInfo.selected = true;
        }else{
            log.info(freeboxBoxInfo.box.getName()+" n'avait pas été selectionnée");
            ImageView iconLastSelected = (ImageView) convertView.findViewById(R.id.list_icon_selected);
            iconLastSelected.setVisibility(View.INVISIBLE);
            //freeboxBoxInfo.selected = false;

        }
        freeboxBoxInfo.freebox_box_name.setText(freeboxBox.getName());
        freeboxBoxInfo.freebox_box_ip.setText(freeboxBox.getIp_public());
        freeboxBoxInfo.freebox_box_port.setText(freeboxBox.getPort());

		
		OnClickListener yourClickListener = new OnClickListener() {
            public void onClick(View v) {
            	Object tag = v.getTag();
            	if (tag instanceof ViewFreeboxBoxInfo){
                    ViewFreeboxBoxInfo boxInfoClicked = (ViewFreeboxBoxInfo)tag;

                    String boxName = boxInfoClicked.box.getName();
                    log.debug("onClick pour box : "+boxName);
                    if ("Ajouter une freebox".equals(boxName)){
                        follower.sendMessage(Params.MSG_CNF, "Association avec nouvelle freebox");
                        //On vérifie que le wifi est activé
                        ConnectivityManager connManager = (ConnectivityManager) follower.getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                        if (mWifi.isConnected()) {
                            //On demande le nom de la freebox
							
							Params.FREEBOX_IP = "mafreebox.free.fr";
                            Params.FREEBOX_PORT = "80";
                            myDB.remove("app_token");
                            Params.reinitFreebox();
							
							FreeboxControler.delOldPrefs(follower);

                        	
                        	follower.sendMessage(Params.MSG_CNF, "Ajout du nom");
                        	final View alertDialogView = inflater.inflate(R.layout.freebox_list_addbox, null);
                        	 
                        	//Création de l'AlertDialog
                            AlertDialog.Builder adb = new AlertDialog.Builder(follower, AlertDialog.THEME_HOLO_DARK);
                     
                            //On affecte la vue personnalisé que l'on a crée à notre AlertDialog
                            adb.setView(alertDialogView);
                     
                            //On donne un titre à l'AlertDialog
                            adb.setTitle("Ajout d'une freebox");
                     
                            //On modifie l'icône de l'AlertDialog pour le fun ;)
                            adb.setIcon(android.R.drawable.ic_dialog_alert);
                     
                            //On affecte un bouton "OK" à notre AlertDialog et on lui affecte un évènement
                            adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                     
                                	//Lorsque l'on cliquera sur le bouton "OK", on récupère l'EditText correspondant à notre vue personnalisée (cad à alertDialogView)
                                	EditText et = (EditText)alertDialogView.findViewById(R.id.FreeboxListAddBox_name);
                     
                                	myDB.putString("FreeboxNameTmp", et.getText().toString());
                                	//On affiche dans un Toast le texte contenu dans l'EditText de notre AlertDialog
                                	follower.sendMessage(Params.MSG_TOAST, et.getText().toString());
                                	
                                	
                                	boolean boxAlreadyExists = false;
                                	
                                    //On valide que le nom n'est pas déjà utilisé
                                	for (FreeboxBox freeboxBox : freeboxBoxs) {
										if (et.getText().toString().equals(freeboxBox.getName())){
											boxAlreadyExists = true;
										}
									}
                                	if (boxAlreadyExists){
                                		follower.sendMessage(Params.MSG_ERR, "Une freebox existe déjà à ce nom, veuillez modifier votre saisie.");
                                	}else{
										freeboxBox.setSelected(false);
                                    	dialog.dismiss();
	                                    //On demande les informations de connexion pour l'ip remote et au préalable la demande d'association
                                    	FbxHttpRaster fbxHttpRaster = (FbxHttpRaster) FreeboxControler.createRequest(Params.FBX_REQ_CONFIG, follower);
                            			HttpConnect connection = HttpConnect.getInstance();
                            			follower.sendMessage(Params.MSG_IND, Params.getMessageAttente(fbxHttpRaster.requestLabel));
                            			connection.execute(fbxHttpRaster);
	                                    //On sauvegarde la nouvelle freebox
                            			//effecuté dans l'activity
                                	}
                              } });
                     
                            //On crée un bouton "Annuler" à notre AlertDialog et on lui affecte un évènement
                            adb.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                	//Lorsque l'on cliquera sur annuler on quittera l'application
                                	dialog.dismiss();
                              } });
                            adb.show();
                        }else{

                            follower.sendMessage(Params.MSG_ERR, "Vous devez être connecté à la freebox par le réseau wifi pour effectuer une nouvelle association");
                        }

                    }else {

                        myDB.putString("pref_ip_public_freebox", boxInfoClicked.box.getIp_public());
                        myDB.putString("pref_port_public_freebox", boxInfoClicked.box.getPort());
                        myDB.putString("app_token", boxInfoClicked.box.getApp_token());

						for(FreeboxBox freeboxBoxIt:freeboxBoxs){
							freeboxBoxIt.setSelected(false);
						}//TODO
                        /*if (lastSelected != null) {
                            FreeboxBox boxLast = freeboxBoxs.get(((ViewFreeboxBoxInfo) (lastSelected.getTag())).box.getIndex());
                            log.info(boxLast.getName() + " doit être déselectionnée");
                            boxLast.setSelected(false);
                        }*/
                        FreeboxBox box = freeboxBoxs.get(boxInfoClicked.box.getIndex());
                        log.info(box.getName() + " doit être selectionnée");
                        box.setSelected(true);
                        lastSelected = v;

                        updateListBoxes(freeboxBoxs);
                    }
                    log.debug("fin dy onClick pour box : "+boxInfoClicked.box.getName());
            	}
            }
        };
        
        convertView.setOnClickListener(yourClickListener);

        log.debug("Fin du GetView pour box : "+freeboxBox.getName());
		return convertView;
	}

}
