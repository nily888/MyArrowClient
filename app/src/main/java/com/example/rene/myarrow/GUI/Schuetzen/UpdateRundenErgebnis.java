package com.example.rene.myarrow.GUI.Schuetzen;

import android.content.Context;
import android.database.Cursor;

import com.example.rene.myarrow.Database.RundenSchuetzen.RundenSchuetzenSpeicher;
import com.example.rene.myarrow.Database.RundenZiel.RundenZielSpeicher;

/**
 * Created by rened on 27.03.2016.
 */
public class UpdateRundenErgebnis {

    Context mContext;
    Cursor cRundenSchuetzen;
    RundenSchuetzenSpeicher rundenSchuetzen;

    public UpdateRundenErgebnis(Context context){
        mContext = context;
        rundenSchuetzen = new RundenSchuetzenSpeicher(mContext);
        cRundenSchuetzen = rundenSchuetzen.loadRundenSchuetzenListe();
    }

    public void runUpdate(){
        RundenZielSpeicher rundenZiel = new RundenZielSpeicher(mContext);
        String mRundenGID;
        String mRundenSchuetzenGID;
        String mSchuetzenGID;
        int summe;
        while (cRundenSchuetzen.moveToNext()){
            mRundenGID = cRundenSchuetzen.getString(1);
            mRundenSchuetzenGID = cRundenSchuetzen.getString(0);
            mSchuetzenGID = cRundenSchuetzen.getString(2);
            summe = rundenZiel.getRundenZielPunkte(mRundenGID, mSchuetzenGID);
            rundenSchuetzen.updateGesamtergebnis(mRundenSchuetzenGID, summe);
        }
        cRundenSchuetzen.close();
    }
}
