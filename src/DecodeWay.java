import java.util.ArrayList;
import java.util.List;

public class DecodeWay {
    public int numDecodings(String s){
        int sLength = s.length();
        if(sLength == 0){
            return 0;
        }
        if (sLength == 1){
            return 1;
        }
        List<Integer> list = new ArrayList<>();
        List<String> sonArray = new ArrayList<>();
        for(int i = 0;i<sLength;i++){
            if(s.charAt(i) == '0'){
                list.add(i);
            }
        }
        if(list != null){
            sonArray.add(s.substring(0,list.get(0)-1));
            for(int i=1;i<list.size();i++){
                if (list.get(i-1)+1 < list.get(i)-1) {
                    sonArray.add(s.substring(list.get(i - 1) + 1, list.get(i) - 1));
                }
            }
            if (list.get(list.size()-1)+1 < sLength) {
                sonArray.add(s.substring(list.get(list.size() - 1) + 1));
            }
        }

        return 0;
    }
    private int splitString(String s){

        return 0;
    }
}
