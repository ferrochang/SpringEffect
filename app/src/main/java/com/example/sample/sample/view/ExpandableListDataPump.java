package com.example.sample.sample.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableListDataPump {
    public static HashMap<String, List<String>> getData() {
        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();

        List<String> cricket = new ArrayList<String>();
        cricket.add("India");
        cricket.add("Pakistan");
        cricket.add("Australia");
        cricket.add("England");
        cricket.add("South Africa");

        List<String> football = new ArrayList<String>();
        football.add("Brazil");
        football.add("Spain");
        football.add("Germany");
        football.add("Netherlands");
        football.add("Italy");

        List<String> basketball = new ArrayList<String>();
        basketball.add("United States");
        basketball.add("Spain");
        basketball.add("Argentina");
        basketball.add("France");
        basketball.add("Russia");

        List<String> volleyball = new ArrayList<String>();
        volleyball.add("Italy");
        volleyball.add("Brazil");
        volleyball.add("Netherlands");
        volleyball.add("Cuba");

        List<String> bowling = new ArrayList<String>();
        bowling.add("United States");
        bowling.add("Canada");
        bowling.add("Nigeria");
        bowling.add("Tunisia");

        expandableListDetail.put("CRICKET TEAMS", cricket);
        expandableListDetail.put("FOOTBALL TEAMS", football);
        expandableListDetail.put("BASKETBALL TEAMS", basketball);
        expandableListDetail.put("VolleyBall", volleyball);
        expandableListDetail.put("Bowling", bowling);
        return expandableListDetail;
    }
}
