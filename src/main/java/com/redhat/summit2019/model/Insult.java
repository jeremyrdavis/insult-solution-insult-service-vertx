package com.redhat.summit2019.model;

import io.vertx.core.json.JsonObject;

public class Insult {

    String adjective1;

    String adjective2;

    String noun;

    public Insult(String adjective1, String adjective2, String noun) {
        this.adjective1 = adjective1;
        this.adjective2 = adjective2;
        this.noun = noun;
    }

    public Insult(JsonObject adjective1, JsonObject adjective2, JsonObject noun) {
        this.adjective1 = adjective1.getString("adjective");
        this.adjective2 = adjective2.getString("adjective");;
        this.noun = noun.getString("noun");
    }

    public String getInsult() {
        StringBuilder builder = new StringBuilder();
        builder.append("Verily, ye be a ");
        builder.append(adjective1);
        builder.append(", ");
        builder.append(adjective2);
        builder.append(" ");
        builder.append(noun);
        builder.append("!");
        return builder.toString();
    }

    public Insult() {
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append("\"insult\":\"");
        builder.append(getInsult());
        builder.append("\"");
        builder.append("}");
        return builder.toString();

    }

    public String getAdjective1() {
        return adjective1;
    }

    public void setAdjective1(String adjective1) {
        this.adjective1 = adjective1;
    }

    public String getAdjective2() {
        return adjective2;
    }

    public void setAdjective2(String adjective2) {
        this.adjective2 = adjective2;
    }

    public String getNoun() {
        return noun;
    }

    public void setNoun(String noun) {
        this.noun = noun;
    }
}

