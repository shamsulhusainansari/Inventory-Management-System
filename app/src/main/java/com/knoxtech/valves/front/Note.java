package com.knoxtech.valves.front;

public class Note {
    String bar_number, bar_url, heatNo, mot, nom,size, type,name_of_material,material_of_const,cls_casting, work_num, pdf_url, docId, valveName,qr_url;

    public Note() {
    }

    public Note(String bar_number, String bar_url, String heatNo, String mot, String nom, String size, String type, String name_of_material, String material_of_const, String cls_casting, String work_num, String pdf_url, String docId, String valveName, String qr_url) {
        this.bar_number = bar_number;
        this.bar_url = bar_url;
        this.heatNo = heatNo;
        this.mot = mot;
        this.nom = nom;
        this.size = size;
        this.type = type;
        this.name_of_material = name_of_material;
        this.material_of_const = material_of_const;
        this.cls_casting = cls_casting;
        this.work_num = work_num;
        this.pdf_url = pdf_url;
        this.docId = docId;
        this.valveName = valveName;
        this.qr_url = qr_url;
    }

    public String getValveName() {
        return valveName;
    }

    public void setValveName(String valveName) {
        this.valveName = valveName;
    }

    public String getQr_url() {
        return qr_url;
    }

    public void setQr_url(String qr_url) {
        this.qr_url = qr_url;
    }

    public String getCls_casting() {
        return cls_casting;
    }

    public String getPdf_url() {
        return pdf_url;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public void setPdf_url(String pdf_url) {
        this.pdf_url = pdf_url;
    }

    public String getWork_num() {
        return work_num;
    }

    public void setWork_num(String work_num) {
        this.work_num = work_num;
    }

    public void setCls_casting(String cls_casting) {
        this.cls_casting = cls_casting;
    }

    public String getBar_number() {
        return bar_number;
    }

    public void setBar_number(String bar_number) {
        this.bar_number = bar_number;
    }

    public String getBar_url() {
        return bar_url;
    }

    public void setBar_url(String bar_url) {
        this.bar_url = bar_url;
    }

    public String getHeatNo() {
        return heatNo;
    }

    public void setHeatNo(String heatNo) {
        this.heatNo = heatNo;
    }

    public String getMot() {
        return mot;
    }

    public void setMot(String mot) {
        this.mot = mot;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName_of_material() {
        return name_of_material;
    }

    public void setName_of_material(String name_of_material) {
        this.name_of_material = name_of_material;
    }

    public String getMaterial_of_const() {
        return material_of_const;
    }

    public void setMaterial_of_const(String material_of_const) {
        this.material_of_const = material_of_const;
    }
}
