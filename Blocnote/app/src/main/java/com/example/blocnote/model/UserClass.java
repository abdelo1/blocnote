package com.example.blocnote.model;
/*
** Cree par Abdel Ibrahim 08/07/2019
 */
public class UserClass {

    private String  nom;
    private String  id;
    private String imageUrl;
    private String isDeleted;
    private String filiere;

    public String getFiliere() {
        return filiere;
    }

    public void setFiliere(String filiere) {
        this.filiere = filiere;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    private String token;


    public UserClass(String nom,String imageProfile,String id,String Deleted)
    {
        this.nom=nom;
        this.imageUrl=imageProfile;
        this.id=id;
        this.isDeleted=Deleted;
    }
public UserClass(){}
    public String getId() {
        return this.id;
    }
    public String getIsDeleted(){return  this.isDeleted;}
    public void setIsDeleted(String deleted) {
        this.isDeleted = deleted;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getNom() {
        return this.nom;
    }
    public void setNom(String nom) { this.nom = nom; }
    public String getImageUrl() {
        return this.imageUrl;
    }

    public void  setImageUrl(String imageProfile) {  this.imageUrl=imageProfile; }





}
