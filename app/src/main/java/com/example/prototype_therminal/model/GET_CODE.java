package com.example.prototype_therminal.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class GET_CODE {
        private String RESULT;
        private String name;
        @SerializedName("id")
        private String code;

        public GET_CODE() {
        }


        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return RESULT;
        }

        public void setId(String RESULT) {
            this.RESULT = RESULT;
        }





        public List getData() {

            List data = new ArrayList<>();
            data.add(RESULT);
            data.add(code);
            data.add(name);

            return data;
        }


        public void setAddress(String code) {
            this.code = code;
        }

        @Override
        public String toString() {
            return "Profile{" +
                    "RESULT='" + RESULT + '\'' +
                    ", code='" + code + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }
    }





