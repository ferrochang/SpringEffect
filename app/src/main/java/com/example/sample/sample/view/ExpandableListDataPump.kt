package com.example.sample.sample.view

object ExpandableListDataPump {
    val data: HashMap<String, List<String>>
        get() {
            val expandableListDetail = HashMap<String, List<String>>()
            val cricket: MutableList<String> = ArrayList()
            cricket.add("India")
            cricket.add("Pakistan")
            cricket.add("Australia")
            cricket.add("England")
            cricket.add("South Africa")
            val football: MutableList<String> = ArrayList()
            football.add("Brazil")
            football.add("Spain")
            football.add("Germany")
            football.add("Netherlands")
            football.add("Italy")
            val basketball: MutableList<String> = ArrayList()
            basketball.add("United States")
            basketball.add("Spain")
            basketball.add("Argentina")
            basketball.add("France")
            basketball.add("Russia")
            val volleyball: MutableList<String> = ArrayList()
            volleyball.add("Italy")
            volleyball.add("Brazil")
            volleyball.add("Netherlands")
            volleyball.add("Cuba")
            val bowling: MutableList<String> = ArrayList()
            bowling.add("United States")
            bowling.add("Canada")
            bowling.add("Nigeria")
            bowling.add("Tunisia")
            expandableListDetail["CRICKET TEAMS"] = cricket
            expandableListDetail["FOOTBALL TEAMS"] = football
            expandableListDetail["BASKETBALL TEAMS"] = basketball
            expandableListDetail["VolleyBall"] = volleyball
            expandableListDetail["Bowling"] = bowling
            return expandableListDetail
        }
}