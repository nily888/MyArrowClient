package com.example.rene.myarrow.misc;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class BerechneErgebnis {

	/** lokale Variablen initialisieren */
	private int startwert;
	private int abzug;
	private int bonus;
	private int bonus_plus;

	public BerechneErgebnis(Context c) {
        SharedPreferences einstellungen = PreferenceManager.getDefaultSharedPreferences(c);
		startwert = Integer.valueOf(einstellungen.getString("startwert", "16"));
		abzug = Integer.valueOf(einstellungen.getString("abzug", "6"));
		bonus = Integer.valueOf(einstellungen.getString("bonus", "4"));
		bonus_plus = Integer.valueOf(einstellungen.getString("bonus_plus", "4"));
	}

	public int getErgebnis(int schuss, int kill) {

        /** initialisieren */
		int summe;

		/** man hat getroffen */
		if (schuss < 1 || schuss > 3) {
			summe = 0;
		} else {
			 summe = startwert - (abzug*(schuss -1));
		}

		/** und sogar das Kill */
		switch (kill) {
			case 1:
				summe = summe + bonus;
				break;
			case 2:
				summe = summe + bonus_plus;
				break;
		}
		return summe;
	}

	public int maxPunkte() {
		return startwert+bonus_plus;
	}
}
