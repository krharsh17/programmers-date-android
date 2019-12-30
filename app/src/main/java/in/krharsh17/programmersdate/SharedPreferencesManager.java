package in.krharsh17.programmersdate;

import android.content.SharedPreferences;

public class SharedPreferencesManager {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public SharedPreferencesManager storeID(String teamId){

        editor.putString("teamID",teamId);
        editor.apply();

        return this;
    }

    public String getID(){

        String ID = sharedPreferences.getString("teamID", "NOT_FOUND");
        return ID;
    }

}
