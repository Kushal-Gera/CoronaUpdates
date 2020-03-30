package kushal.application.coronaupdates;


public class CountryItem {

	private String date;
	private int recovered;
	private int confirmed;
	private int deaths;

	public void setDate(String date){
		this.date = date;
	}

	public String getDate(){
		return date;
	}

	public void setRecovered(int recovered){
		this.recovered = recovered;
	}

	public int getRecovered(){
		return recovered;
	}

	public void setConfirmed(int confirmed){
		this.confirmed = confirmed;
	}

	public int getConfirmed(){
		return confirmed;
	}

	public void setDeaths(int deaths){
		this.deaths = deaths;
	}

	public int getDeaths(){
		return deaths;
	}

	@Override
 	public String toString(){
		return 
			"AfghanistanItem{" + 
			"date = '" + date + '\'' + 
			",recovered = '" + recovered + '\'' + 
			",confirmed = '" + confirmed + '\'' + 
			",deaths = '" + deaths + '\'' + 
			"}";
		}
}