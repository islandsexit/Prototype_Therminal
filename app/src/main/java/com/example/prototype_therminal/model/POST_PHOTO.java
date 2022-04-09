package com.example.prototype_therminal.model;

import java.util.ArrayList;
import java.util.List;

public class POST_PHOTO {

    private String RESULT;
    private String DESC;
    private String ID;
    private String img64;
    private String name;


    public POST_PHOTO() {
    }

    public void setMsg(String msg) {
        this.DESC = msg;
    }

    public void setRESULT(String RESULT) {
        this.RESULT = RESULT;
    }

    public String getId() {
        return ID;
    }

    public void setId(String result) {
        this.RESULT = result;
    }

    public String getMsg() {
        return DESC;
    }

    public String getRESULT() {
        return RESULT;
    }
    /*public Map<String, Object> getAddress() {
        return address;
    }*/

    public List getResponse() {


        List<Object> response = new ArrayList<>();
        response.add(RESULT);
        response.add(DESC);

        return response;
    }

    /*public void setAddress(Map<String, Object> address) {
        this.address = address;
    }*/

//    public void setAddress(String address) {
//        this.code = address;
//    }

    @Override
    public String toString() {
        return "Profile{" +
                "RESULT='" + RESULT + '\'' +
                ", msg='" + DESC + '\'' +
                '}';
    }

}
