package com.example.myapplication;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = Nekretnina.TABLE_NAME_USERS)
public class Nekretnina {



        public static final String TABLE_NAME_USERS = "nekretnina";
        public static final String FIELD_NAME_ID = "id";
        public static final String FIELD_NAME_NAZIV = "naziv";
        public static final String FIELD_NAME_OPIS = "opis";
        public static final String FIELD_NAME_IMAGE   = "image";
        public static final String FIELD_NAME_TELEFON   = "telefon";
        public static final String FIELD_NAME_ADRESA = "adresa";
        public static final String FIELD_NAME_KVADRATURA = "kvadratura";
        public static final String FIELD_NAME_CENA = "cena";
        public static final String FIELD_NAME_BROJSOBA = "broj soba";



        @DatabaseField(columnName = FIELD_NAME_ID, generatedId = true)
        private int mId;

        @DatabaseField(columnName = FIELD_NAME_NAZIV)
        private String mNaziv;

        @DatabaseField(columnName = FIELD_NAME_OPIS)
        private String mOpis;

        @DatabaseField(columnName = FIELD_NAME_IMAGE)
        private String mImage;


        @DatabaseField(columnName = FIELD_NAME_TELEFON)
        private String mTelefon;

        @DatabaseField(columnName = FIELD_NAME_ADRESA)
        private String mAdresa;

        @DatabaseField(columnName = FIELD_NAME_KVADRATURA)
        private String mKvadratura;

        @DatabaseField(columnName = FIELD_NAME_CENA)
        private String mCena;

        @DatabaseField(columnName = FIELD_NAME_BROJSOBA)
        private String mBrojSoba;

    public Nekretnina(int mId, String mNaziv, String mOpis, String mImage, String mTelefon, String mAdresa, String mKvadratura, String mCena, String mBrojSoba) {
        this.mId = mId;
        this.mNaziv = mNaziv;
        this.mOpis = mOpis;
        this.mImage = mImage;
        this.mTelefon = mTelefon;
        this.mAdresa = mAdresa;
        this.mKvadratura = mKvadratura;
        this.mCena = mCena;
        this.mBrojSoba = mBrojSoba;
    }

    public Nekretnina() {
    }



    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public String getmNaziv() {
        return mNaziv;
    }

    public void setmNaziv(String mNaziv) {
        this.mNaziv = mNaziv;
    }

    public String getmOpis() {
        return mOpis;
    }

    public void setmOpis(String mOpis) {
        this.mOpis = mOpis;
    }

    public String getmImage() {
        return mImage;
    }

    public void setmImage(String mImage) {
        this.mImage = mImage;
    }

    public String getmTelefon() {
        return mTelefon;
    }

    public void setmTelefon(String mTelefon) {
        this.mTelefon = mTelefon;
    }

    public String getmAdresa() {
        return mAdresa;
    }

    public void setmAdresa(String mAdresa) {
        this.mAdresa = mAdresa;
    }

    public String getmKvadratura() {
        return mKvadratura;
    }

    public void setmKvadratura(String mKvadratura) {
        this.mKvadratura = mKvadratura;
    }

    public String getmCena() {
        return mCena;
    }

    public void setmCena(String mCena) {
        this.mCena = mCena;
    }

    public String getmBrojSoba() {
        return mBrojSoba;
    }

    public void setmBrojSoba(String mBrojSoba) {
        this.mBrojSoba = mBrojSoba;
    }
}
