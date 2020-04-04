
package kushal.application.covaupdates.International;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ExampleInter {

    @SerializedName("Countries")
    @Expose
    private List<Country> countries = null;
    @SerializedName("Date")
    @Expose
    private String date;

    public List<Country> getCountries() {
        return countries;
    }

    public void setCountries(List<Country> countries) {
        this.countries = countries;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
