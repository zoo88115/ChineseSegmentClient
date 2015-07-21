/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chinesesegmentclient;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import com.chenlb.mmseg4j.ComplexSeg;
import com.chenlb.mmseg4j.Dictionary;
import com.chenlb.mmseg4j.MMSeg;
import com.chenlb.mmseg4j.Seg;
import com.chenlb.mmseg4j.Word;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 *
 * @author zoo88115
 */
public class SegChinese {
    protected Dictionary dic;
	
    public SegChinese() {
            System.setProperty("mmseg.dic.path", "C:\\Users\\zoo88_000\\Documents\\NetBeansProjects\\ChineseSegmentClient\\build\\classes\\chinesesegmentclient\\data");	//這裡可以指定自訂詞庫
            dic = Dictionary.getInstance();
    }

    protected Seg getSeg() {
            return new ComplexSeg(dic);
    }
    
    public String segWords(String txt, String wordSpilt) throws IOException {
	Reader input = new StringReader(txt);
	StringBuilder sb = new StringBuilder();
	Seg seg = getSeg();
	MMSeg mmSeg = new MMSeg(input, seg);
	Word word = null;
	boolean first = true;
	while((word=mmSeg.next())!=null) {
		if(!first) {
			sb.append(wordSpilt);
		}
		String w = word.getString();
		sb.append(w);
		first = false;		
	}
        //System.out.println(sb.toString());
        //印出回傳字串
	return sb.toString();
    }
    
    protected ArrayList<SegmentStatus> getSegment(String[] args) throws IOException {//從字串開始一次到底
        ArrayList<SegmentStatus> arrayList = new ArrayList<SegmentStatus>();
	String txt = "執行失敗，如果未被取代";
	
	if(args.length > 0) {
		txt = args[0];
	}
	String temp=segWords(txt, "\t");
        String tempArray[]=temp.split("\t");
//        for(String d:tempArray)
//            System.out.print(d+"\t");
//        //印出切割後字串，查看是否正確
        Arrays.sort(tempArray);
        for(int i=0;i<tempArray.length;i++){
            int count=1;
            int k=i;
            for(int j=i+1;j<tempArray.length;j++){
                if(tempArray[i].equals(tempArray[j])==true){
                    count++;
                    k=j;//如果有重複  外層回圈可以跳過
                }
                else 
                    break;
            }
            arrayList.add(new SegmentStatus(tempArray[i],count,1));
            i=k;
        }
        Collections.sort(arrayList);//排序
        return arrayList;
    }
    protected ArrayList<SegmentStatus> getAfterSegment(String temp) throws IOException {//接收以tab分開的字串做斷詞
        ArrayList<SegmentStatus> arrayList = new ArrayList<SegmentStatus>();

        String tempArray[]=temp.split("\t");

        Arrays.sort(tempArray);
        for(int i=0;i<tempArray.length;i++){
            int count=1;
            int k=i;
            for(int j=i+1;j<tempArray.length;j++){
                if(tempArray[i].equals(tempArray[j])==true){
                    count++;
                    k=j;//如果有重複  外層回圈可以跳過
                }
                else 
                    break;
            }
            arrayList.add(new SegmentStatus(tempArray[i],count,1));
            i=k;
        }
        Collections.sort(arrayList);//排序
        return arrayList;
    }
}
