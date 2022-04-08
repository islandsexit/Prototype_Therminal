package com.example.prototype_therminal.model;

import java.util.ArrayList;
import java.util.List;

public class POST_PHOTO {

    private String result;
    private String msg;
    private String ID;
    private String img64;
    private String name;


    public POST_PHOTO() {
    }

    public String getId() {
        return ID;
    }

    public void setId(String result) {
        this.result = result;
    }




    /*public Map<String, Object> getAddress() {
        return address;
    }*/

    public List getResponse() {

        List response = new ArrayList<>();
        response.add(result);
        response.add(msg);

        return response;
    }

    /*public void setAddress(Map<String, Object> address) {
        this.address = address;
    }*/

//    public void setAddress(String address) {
//        this.code = address;
//    }
//
//    @Override
//    public String toString() {
//        return "Profile{" +
//                "RESULT='" + RESULT + '\'' +
//                ", code='" + code + '\'' +
//                '}';
//    }

}
