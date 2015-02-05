package fr.scaron.sfreeboxtools.model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.loopj.android.http.RequestParams;

import fr.scaron.sfreeboxtools.Follower;
import fr.scaron.sfreeboxtools.activity.AbstractActivity;
import fr.scaron.sfreeboxtools.contexte.Params;
import fr.scaron.sfreeboxtools.task.AbstractTask;

import org.slf4j.*;

public class HttpRaster {

	public static Logger log = LoggerFactory.getLogger(HttpRaster.class);
	private int nbTentatives = 0;
	private String charset;
	private String url;
	private RequestParams params;
	private JSONObject paramsJson;
	private String paramsString;
	private boolean flagParamsJson;
	private boolean flagParamsString;
	private boolean flagParamFile;
	private File paramFile;
	private String response;
	private JSONObject responseJson;
	private boolean flagResponseError;
	private boolean flagResponseJson;
	private boolean flagResponseFile;
	private byte[] responseByteArrayOS;
	private String errorCode;
	private Throwable errorException;
	
	private List<Follower> followers;
	private Map<String, String> headers = new HashMap<String, String>();
	private int method;
	
	public HttpRaster(String url, int method){
		setUrl(url);
		setMethod(method);
	}
	public HttpRaster(String url, int method, AbstractActivity activity){
		setUrl(url);
		addFollower(activity);
		setMethod(method);
	}
	public HttpRaster(String url, int method, AbstractTask task){
		setUrl(url);
		addFollower(task);
		setMethod(method);
	}
	public HttpRaster(String url, int method, Follower follower){
		setUrl(url);
		addFollower(follower);
		setMethod(method);
	}
	
	public int getNbTentatives(){
		return nbTentatives;
	}
	
	public void incNbTentatives(){
		nbTentatives ++;
	}
	
	public Throwable getErrorException() {
		return errorException;
	}
	public void setErrorException(Throwable errorException) {
		this.errorException = errorException;
	}
	public String getCharset() {
		return charset;
	}
	public void setCharset(String charset) {
		this.charset = charset;
	}
	public boolean isFlagParamFile() {
		return flagParamFile;
	}
	public void setFlagParamFile(boolean flagParamFile) {
		this.flagParamFile = flagParamFile;
	}
	public File getParamFile() {
		return paramFile;
	}
	public void setParamFile(File paramFile) {
		this.paramFile = paramFile;
	}
	public boolean isFlagResponseFile() {
		return flagResponseFile;
	}
	public void setFlagResponseFile(boolean flagResponseFile) {
		this.flagResponseFile = flagResponseFile;
	}
	public byte[] getResponseFile() {
		return responseByteArrayOS;
	}
	public void setResponseFile(byte[] responseFile) {
		this.responseByteArrayOS = responseFile;
	}
	public void setMethod(int method){
		this.method=method;
	}
	
	public int getMethod(){
		return method;
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public RequestParams getParams() {
		return params;
	}
	public void setParams(RequestParams params) {
		this.params = params;
	}
	public JSONObject getParamsJson() {
		return paramsJson;
	}
	public void setParamsJson(JSONObject paramsJson) {
		this.paramsJson = paramsJson;
	}
	public String getParamsString() {
		return paramsString;
	}
	public void setParamsString(String paramsString) {
		this.paramsString = paramsString;
	}
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
	public JSONObject getResponseJson() {
		return responseJson;
	}
	public void setResponseJson(JSONObject responseJson) {
		this.responseJson = responseJson;
	}
	
	public boolean isFlagResponseError() {
		return flagResponseError;
	}

	public void setFlagResponseError(boolean flagResponseError) {
		this.flagResponseError = flagResponseError;
	}

	public boolean isFlagResponseJson() {
		return flagResponseJson;
	}

	public void setFlagResponseJson(boolean flagResponseJson) {
		this.flagResponseJson = flagResponseJson;
	}
	

	public boolean isFlagParamsJson() {
		return flagParamsJson;
	}
	public void setFlagParamsJson(boolean flagParamsJson) {
		this.flagParamsJson = flagParamsJson;
	}
	public boolean isFlagParamsString() {
		return flagParamsString;
	}
	public void setFlagParamsString(boolean flagParamsString) {
		this.flagParamsString = flagParamsString;
	}
	public int addFollower(Follower follower){
		if (followers==null){
			followers = new ArrayList<Follower>();
		}
		if (follower!=null){
			followers.add(follower);
			return Params.RET_OK;
		}
		return Params.RET_KO;
	}
	
	public List<Follower> getFollowers(){
		return followers;
	}
	
	
	
	public Map<String, String> getHeaders() {
		return headers;
	}
	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}
	
	public void addHeader(String key, String value){
		if (headers==null){
			headers = new HashMap<String, String>();
		}
		headers.put(key, value);
	}
	
	public void notifyFollowers(){
		log.debug(followers.size()+" followers to notify");
		for (Follower follower : followers){
			log.debug("notifying follower type "+follower.getClass().getName());
			follower.updateView(this);
		}
		
//		for (Object follower : followers) {
//			boolean notified = false;
//			try{
//				notifyFollower((AbstractActivity)follower);
//				notified = true;
//			}catch(Throwable thro){};
//			try{
//				notifyFollower((AbstractTask)follower);
//				notified = true;
//			}catch(Throwable thro){};
//			if (!notified){
//				log.debug("not notifying follower type "+follower.getClass().getName());
//			}
//		}
	}
	
//	private void notifyFollower(AbstractActivity follower){
//		log.debug("notifying follower type AbstractActivity");
//		((AbstractActivity)follower).updateView(this);
//	}
//	private void notifyFollower(AbstractTask follower){
//		log.debug("notifying follower type AbstractTask");
//		((AbstractTask)follower).updateView(this);
//	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder("\n"+this.getClass().getName()+"\n-----\n");		
		sb.append("url:"+url);
		sb.append("\nparams:"+params);
		sb.append("\nparamsJson:"+paramsJson);
		sb.append("\nparamsString:"+paramsString);
		sb.append("\nflagParamsJson:"+flagParamsJson);
		sb.append("\nflagParamsString:"+flagParamsString);
		sb.append("\nresponse:"+response);
		sb.append("\nresponseJson:"+responseJson);
		sb.append("\nflagResponseError:"+flagResponseError);
		sb.append("\nflagResponseJson:"+flagResponseJson);
		sb.append("\nmethod:"+method);
		sb.append("\n-----");
		return sb.toString();
		
	}
	public void setErrorCode(String code) {
		this.errorCode = code;
	}
	
	public String getErrorCode(){
		return errorCode;
	}
}
