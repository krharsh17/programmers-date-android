package in.krharsh17.programmersdate;

import android.content.Context;
import android.content.SharedPreferences;

import static in.krharsh17.programmersdate.Constants.sharedPrefName;

public class SharedPrefManager {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public SharedPrefManager(Context context){
        sharedPreferences = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public SharedPrefManager setTeamId(String teamId) {
        editor.putString("teamID", teamId);
        editor.commit();
        return this;
    }

    public String getTeamId() {
        String ID = sharedPreferences.getString("teamID", "NOT_FOUND");
        return ID;
    }

    public Long getRollNumber(){
        return sharedPreferences.getLong("roll", 0);
    }

    public SharedPrefManager setRollNumber(Long roll){
        editor.putLong("roll", roll);
        editor.commit();
        return this;
    }

    public static boolean isLoggedIn(Context context){
        return context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE).getBoolean("loggedIn", false);
    }

    public static void setLoggedIn(Context context, boolean isLoggedIn){
        SharedPreferences.Editor editor = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE).edit();
        editor.putBoolean("loggedIn", true);
        editor.commit();
    }


}
