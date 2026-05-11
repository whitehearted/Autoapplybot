package com.Job.applybot.Url;

public class UrlGenerator {

    public static String GetUrl(String role,String location,int exp,boolean wfh){
//        String url="https://www.naukri.com/"
//                +role+ "-jobs"
//                +"?k="+keyword
//                + "&l="+location
//                +"&experience="+exp
//                +"&wfhType=2";

      // String url="https://www.naukri.com/"+role+"-jobs-in-"+location+"?experience="+exp+"&wfhType=2&k="+keyword;
//            String url="https://www.naukri.com/java-jobs-in-chennai?experience=3"; //+
//                   // "&wfhType=2";
//
//
//        return url;

        String url="https://www.naukri.com/"
                +role+"-jobs-in-"+location
                +"?experience="+exp;
        if(wfh){
            url+="&wfhType=2";
        }

        // String url="https://www.naukri.com/"+role+"-jobs-in-"+location+"?experience="+exp+"&wfhType=2&k="+keyword;
        //  String url="https://www.naukri.com/java-developer-jobs-in-chennai?experience=3"; //+
        // "&wfhType=2";
        return url;
    }
}
