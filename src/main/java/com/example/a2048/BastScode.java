package com.example.a2048;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by hasee on 2018/3/18.
 */

public class BastScode {
    private SharedPreferences s;
    BastScode(Context context){
        s = context.getSharedPreferences("bestscode",context.MODE_PRIVATE);

    }

    public int getBestScode(){
        int bestscode = s.getInt("bestscode",0);
        return bestscode;
    }
    public void setBestScode(int bestScode){
        SharedPreferences.Editor editor = s.edit();
        editor.putInt("bestscode",bestScode);
        editor.commit();
    }
}
