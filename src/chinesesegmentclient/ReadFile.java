/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chinesesegmentclient;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author zoo88115
 */
public class ReadFile {
    ArrayList<SegmentStatus> arrayList=new ArrayList<SegmentStatus>();
    
    public ReadFile(){
    }
    
    public ArrayList<SegmentStatus> getFile(String url){
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(url), "big5")); // 指定讀取文件的編碼格式，以免出現中文亂碼
            br.readLine();//略過開頭
            while (br.ready()) {
                String[] tempArray=br.readLine().split("\t");
                arrayList.add(new SegmentStatus(tempArray[0],Integer.valueOf(tempArray[1]),Integer.valueOf(tempArray[2])));
            }
            br.close();
            Collections.sort(arrayList);
            return arrayList;
        }
        catch(Exception e){
            System.out.println(e);
            return null;
        }
    }
    public String getFileString(String otherURL){
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(otherURL), "big5")); // 指定讀取文件的編碼格式，以免出現中文亂碼
            String temp="";
            while (br.ready()) {
                temp=temp+"\t"+br.readLine();
            }
            br.close();
            return temp;
        }
        catch(Exception e){
            System.out.println(e);
            return null;
        }  
    }
}
