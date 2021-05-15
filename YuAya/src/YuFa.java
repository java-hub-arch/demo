import java.util.*;

public class YuFa {

    private static String termiChar = "";
    private static String notTChar = "";
    private static ArrayList<String> arrayListString = new ArrayList<>();
    private static ArrayList<Node> arrayList;
    private static ArrayList<Follow> followArrayList;
    private static ArrayList<First> firstArrayList;
    private static ArrayList<AyalyseTable> ayalyseTableArrayList;
    private static String[] resultArray;

    public static void main(String[] args) {
        //语法分析程序

        Scanner scanner = new Scanner(System.in);

        System.out.print("请输入终结符：");
        termiChar = scanner.next();

        System.out.print("请输入非终结符：");
        notTChar = scanner.next();

        System.out.println("请输入语言文法(以#结束)：");

        String inputStr = scanner.next();
        arrayList = new ArrayList<>();


        while(!inputStr.equals("#")){

            //设置左边符号
            char leftChar = inputStr.charAt(0);

            inputStr = inputStr.substring(3);
            String []strings = inputStr.split("\\|");
            //如果该文法是直接左递归的,消除
            if(leftDiGui(leftChar,inputStr)){
                leftDiguiShiXian(strings,leftChar);

            }else if(publicChar(inputStr)){

                char first = strings[0].charAt(0);
                char newLeft = (char) (leftChar+32);

                int length = strings.length;

                //实现数组反转，让后面消除左递归时候不以leftChar开头的在前。
                for(int j = 0;j < length/2;j++){
                    String swap = strings[j];
                    strings[j] = strings[length-1-j];
                    strings[length-1-j] = swap;
                }

                String swap1 = leftChar+"->";
                String swap2 = newLeft+"->";
                swap1 += first+newLeft+'|';

                for(String str:strings){
                    if(first == str.charAt(0)){
                        swap2 += str.substring(1) + '|';
                    }else{
                        swap1 += str + '|';
                    }
                }
                arrayListString.add(swap1);
                arrayListString.add(swap2);

            } else{
                arrayListString.add(leftChar+"->"+inputStr);
            }

            inputStr = scanner.next();
        }

        System.out.println("请输入要分析的语句(以#结束):");

        inputStr = scanner.next();
        String targetStr = "";

        while (!inputStr.equals("#")){
            targetStr += inputStr + '#' + "$";

            inputStr = scanner.next();
        }

        //这里设置为null是为了充分利用内存，防止内存泄露，不设置为空可能在后面没有用到这个变量从而浪费内存。
        inputStr = null;

        //后面不用了设置空

        /*
        * 接下来看有没有完全左递归,有就消除完全左递归
        * */
        if(CompeleteDigui(arrayListString.get(0).charAt(3),1)){
            char []chars = new char[arrayListString.size()];
            int i = 0;

            //将非终结符排好序
            for (String str:arrayListString){
                chars[i] = str.charAt(0);
                i++;
            }

            //进行循环消除左递归
            for(int k = 0;k < arrayListString.size();k++){
                for(int j = 0;j < k-1;j++){

                    int index =  findIndexByfirst(chars[k],chars[j]);

                    //表示存在回路
                    if(index != -1){
                        int index1 = findIndex(arrayListString.get(index).charAt(3));
                        String str = arrayListString.get(index);
                        String str1 = arrayListString.get(index1);

                        String []strings = str.split("\\|");

                        String []strings1 = str1.split("\\|");

                        String swapStr = strings[0].substring(3);
                        String swapStr1 = "";

                        swapStr1 = arrayListString.get(index).charAt(0)+"->";
                        strings1[0] = strings1[0].substring(3);

                        if(swapStr.length() == 1){
                            swapStr = "";
                        }else{
                            swapStr.substring(1);
                        }

                        for(String str2:strings1){
                            swapStr1 += str2 + swapStr + "|";
                        }

                        for(int ii = 1;ii < strings.length;ii++){
                            swapStr1 += strings[ii]+"|";
                        }

                        arrayListString.remove(index);
                        arrayListString.add(swapStr1);
                        if(leftDiGui(swapStr1.charAt(0),swapStr1.substring(3))){
                            String []strings2 = swapStr1.substring(3).split("\\|");
                            leftDiguiShiXian(strings2,swapStr1.charAt(0));
                        }

                    }
                }
            }
        }

        for(String str:arrayListString){
            String []strings = str.split("\\|");

            char leftChar = strings[0].charAt(0);
            Node node = new Node();
            node.setLeftChar(leftChar);
            node.setRightChar(strings[0].substring(3));
            arrayList.add(node);

            for(int i = 1;i < strings.length;i++){
                Node node1 = new Node();
                node1.setLeftChar(leftChar);
                node1.setRightChar(strings[i]);
                arrayList.add(node1);
            }
        }

        System.out.println("消除左递归");
        for(Node node:arrayList){
            System.out.println(node.getLeftChar() + "  " + node.getRightChar());
        }

        //求FIRST集合
        firstArrayList = new ArrayList<>();
        followArrayList = new ArrayList<>();

        //初始化FIRST集合和FOLLOW集合
        char left = arrayList.get(0).getLeftChar();
        for (int i = 0;i < arrayList.size();i++){

            while(i < arrayList.size()-1 && left == arrayList.get(i+1).getLeftChar()){
                i++;
            }

            First first = new First();
            first.setLeft(left);
            first.setRightStr("");
            firstArrayList.add(first);

            Follow follow = new Follow();
            follow.setLeft(left);
            follow.setRightStr("");
            followArrayList.add(follow);

            //i是最后一个的话就结束了
            if(i < arrayList.size() - 1)
            left = arrayList.get(i+1).getLeftChar();
        }


        //求FIRST和FOLLOW集合
        for(First first:firstArrayList){

            if(isToNull(first.getLeft())){
                first.setRightStr(first.getRightStr()+'^');
            }
            //遍历整个文法产生式
            for(Node node:arrayList){
                if(node.getLeftChar() == first.getLeft()){

                    String rightStr = node.getRightChar();
                    //是终结符的话
                    if(isTer(rightStr.charAt(0))){
                        //不包含
                        if(noInclude(rightStr.charAt(0),first.getRightStr()))
                            first.setRightStr(first.getRightStr()+rightStr.charAt(0));

                    } else{
                        int i = 1;
                        int canToNullAmount = 0;

                        first.setRightStr(first.getRightStr()+rightStr.charAt(0));

                        if(isToNull(rightStr.charAt(0))){
                            canToNullAmount++;
                            while(i < rightStr.length() && ! isTer(rightStr.charAt(i))){
                                if(isToNull(rightStr.charAt(i))){
                                    canToNullAmount++;
                                }
                                i++;
                            }

                            if(canToNullAmount == i && i != 1 && i!=rightStr.length()){
                                for(int j = 1;j < i;j++){
                                    first.setRightStr(first.getRightStr()+rightStr.charAt(j));
                                }
                            }
                            if(canToNullAmount == i && i == rightStr.length()){
                                for(int j = 1;j < i-1;j++){
                                    first.setRightStr(first.getRightStr()+rightStr.charAt(j));
                                }
                                if(noInclude('^',first.getRightStr()))
                                first.setRightStr(first.getRightStr()+'^');
                            }
                        }
                    }
                }
            }
        }

        //求出first集合

        for(int k = 0;k < firstArrayList.size() ;k++){

            First first = firstArrayList.get(k);
            String result = "";
            String str = first.getRightStr();
            for(int i = 0;i < str.length();i++){
                if(!isTer(str.charAt(i))){
                    String resultStr = findFirst(str.charAt(i));

                    if(noInclude('^',resultStr)){
                        for(int j = 0;j < resultStr.length();j++){
                            if(noInclude(resultStr.charAt(j),result))
                                result += resultStr.charAt(j);
                        }
                    }else{
                        for(int j = 0;j < resultStr.length();j++){
                            if(resultStr.charAt(j) != '^' &&
                                    noInclude(resultStr.charAt(j),result)){
                                result += resultStr.charAt(j);
                            }
                        }
                    }
                }else{
                    if(noInclude(str.charAt(i),result))
                    result += str.charAt(i);
                }
            }
            first.setRightStr(result);
        }


        for(int i = 0;i < followArrayList.size();i++){
            Follow follow = followArrayList.get(i);

            if(follow.getLeft() == arrayList.get(0).getLeftChar()){
                follow.setRightStr("#");
            }
            char leftChar = follow.getLeft();
            //求follow集合
            for(Node node:arrayList){
                String rightStr = node.getRightChar();
                int length = rightStr.length();
                int index = rightStr.indexOf(leftChar);

                //这个产生式包含该符号
                if(index != -1){
                    if(index == length-1){

                        if(node.getLeftChar() !=leftChar && noInclude(node.getLeftChar(),follow.getRightStr()))
                            follow.setRightStr(follow.getRightStr()+node.getLeftChar());
                    }else{

                        char next = rightStr.charAt(index + 1);
                        if(isTer(next)){

                            if(noInclude(next,follow.getRightStr()))
                                follow.setRightStr(follow.getRightStr()+next);

                        }else {

                            if(isToNull(next)){
                                if(node.getLeftChar() !=leftChar && noInclude(next,follow.getRightStr()))
                                    follow.setRightStr(follow.getRightStr()+next);
                            }
                            //follow结合第二个方法
                            String str = "";
                            for(int j = 0;j < firstArrayList.size();j++){
                                if(firstArrayList.get(j).getLeft() == next){
                                    str = firstArrayList.get(j).getRightStr();
                                    break;
                                }
                            }

                            String result = "";
                            for(int j = 0;j < str.length();j++){
                                if(str.charAt(j) != '^' &&
                                        noInclude(str.charAt(j),result)){
                                    result += str.charAt(j);
                                }
                            }
                            follow.setRightStr(follow.getRightStr()+result);
                        }
                    }
                }
            }
        }

        //求follow集合
        for(int k = followArrayList.size()-1;k >=0;k--){

            Follow follow = followArrayList.get(k);

            String result = "";
            String str = follow.getRightStr();
            for(int i = 0;i < str.length();i++){
                if(!isTer(str.charAt(i))){
                    String resultStr = findFollow(str.charAt(i));

                    for(int j = 0;j < resultStr.length();j++){
                        if(noInclude(resultStr.charAt(j),result))
                            result += resultStr.charAt(j);
                    }

                }else{
                    if(noInclude(str.charAt(i),result))
                        result += str.charAt(i);
                }
            }
            follow.setRightStr(result);
        }

        //打印first集合
        System.out.println("first集合");
        for(First first:firstArrayList){
            System.out.println(first.getLeft() + " first number="+first.getRightStr().length()
                                +" "+"first="+first.getRightStr());
        }

        System.out.println("follow集合");
        for(Follow follow:followArrayList){
            System.out.println(follow.getLeft() + " follow number="+follow.getRightStr().length()
                    +" "+"follow="+follow.getRightStr());
        }

        //接下来构造分析表
        ayalyseTableArrayList = new ArrayList<>();
        for(First first:firstArrayList){
            AyalyseTable ayalyseTable = new AyalyseTable();
            ayalyseTable.setLeftchar(first.getLeft());

            Map<Character,String> map = new HashMap<>();

            for(int i = 0;i < termiChar.length();i++){
                map.put(termiChar.charAt(i),"");
            }
            ayalyseTable.setMap(map);

            ayalyseTableArrayList.add(ayalyseTable);
        }

        for(int i = 0;i < arrayList.size();i++){

            Node node = arrayList.get(i);
            char leftChar = node.getRightChar().charAt(0);

            if(isTer(leftChar) && leftChar != '^'){
                int index = findIndexInAyaList(node.getLeftChar());
                AyalyseTable ayalyseTable = ayalyseTableArrayList.get(index);
                ayalyseTable.getMap().replace(leftChar,i+1+"");
            }else if(leftChar == '^'){
                int index = findIndexInAyaList(node.getLeftChar());
                String followStr = followArrayList.get(index).getRightStr();

                AyalyseTable ayalyseTable = ayalyseTableArrayList.get(index);
                for(int j = 0;j < followStr.length();j++){
                    ayalyseTable.getMap().replace(followStr.charAt(j),i+1+"");
                }
            }else{
                int index = findIndexInAyaList(node.getLeftChar());
                int index1 = findIndexInFirst(leftChar);
                String firstStr = firstArrayList.get(index1).getRightStr();

                AyalyseTable ayalyseTable = ayalyseTableArrayList.get(index);
                for(int j = 0;j < firstStr.length();j++){
                    ayalyseTable.getMap().replace(firstStr.charAt(j),i+1+"");
                }
            }
        }

        System.out.println("预测分析表");
        System.out.print(" ");
        for(int i = 0;i < termiChar.length();i++){
            System.out.print(String.format("%6s",termiChar.charAt(i)));
        }

        for(AyalyseTable ayalyseTable:ayalyseTableArrayList){
            System.out.println();
            System.out.print(ayalyseTable.getLeftchar()+" ");
            Map<Character,String> map = ayalyseTable.getMap();
            for(int i = 0;i < map.size();i++){
                System.out.print(String.format("%6s",map.get(termiChar.charAt(i))));
            }
        }

        System.out.println();
        System.out.println("分析过程:");
        resultArray = targetStr.split("\\$");

        for(int i = 0;i < resultArray.length;i++){
            String str = resultArray[i];

            Stack<Character> stack = new Stack<>();
            stack.push('#');
            stack.push(arrayList.get(0).getLeftChar());
            System.out.print("分析栈 ");

            String strPin1 = "";
            for(int k = 0;k < stack.size();k++){
                strPin1 += stack.get(k);
            }

            System.out.print(String.format("%-10s",strPin1));
            System.out.print(" "+"剩余字符串 " + str);
            System.out.println();

            int j = 0;
            while(j < str.length()){
                char stackTop = stack.peek();
                char inputChar = str.charAt(j);

                if(stackTop != inputChar){
                    stack.pop();
                    int index = findIndexInAyaList(stackTop);
                    AyalyseTable ayalyseTable = ayalyseTableArrayList.get(index);
                    String str1 = ayalyseTable.getMap().get(inputChar);

                    if(str1.equals("")){
                        System.out.println(str + "非法输入,程序结束");
                        break;
                    }else{
                        int num = str1.charAt(0)-'0'-1;//得到式子的下标
                        String rightStr = arrayList.get(num).getRightChar();

                        if(rightStr.equals("^")){
                            System.out.print("分析栈 ");

                            String strPin = "";
                            for(int k = 0;k < stack.size();k++){
                                strPin += stack.get(k);
                            }

                            System.out.print(String.format("%-10s",strPin));
                            System.out.print(" "+"剩余字符串 " + String.format("%-10s",str.substring(j)));
                            System.out.println();
                            continue;
                        }

                        for(int ii = rightStr.length()-1;ii >= 0;ii--){
                            stack.push(rightStr.charAt(ii));
                        }

                        System.out.print("分析栈 ");

                        String strPin = "";
                        for(int k = 0;k < stack.size();k++){
                            strPin += stack.get(k);
                        }

                        System.out.print(String.format("%-10s",strPin));
                        System.out.print(" "+"剩余字符串 " + str.substring(j));
                        System.out.println();
                    }
                }else if(stackTop == inputChar && stackTop != '#'){
                    stack.pop();
                    j++;
                    System.out.print("分析栈 ");

                    String strPin = "";
                    for(int k = 0;k < stack.size();k++){
                        strPin += stack.get(k);
                    }

                    System.out.print(String.format("%-10s",strPin));
                    System.out.print(" "+"剩余字符串 " + str.substring(j));
                    System.out.println();
                }else if(stackTop == inputChar && stackTop == '#'){
                    System.out.print("分析栈 ");

                    String strPin = "";
                    for(int k = 0;k < stack.size();k++){
                        strPin += stack.get(k);
                    }

                    System.out.print(String.format("%-10s",strPin));
                    System.out.print(" "+"剩余字符串 " + str.substring(j));
                    System.out.println();
                    System.out.println("合法输入");
                    System.out.println("退出成功");
                    break;
                }
            }

        }
    }

    private static int findIndexInFirst(char leftChar) {
        for(int i = 0;i < firstArrayList.size();i++){
            if(firstArrayList.get(i).getLeft() == leftChar){
                return i;
            }
        }
        return 0;
    }

    private static int findIndexInAyaList(char leftChar) {
        for(int i = 0;i < ayalyseTableArrayList.size();i++){
            if(ayalyseTableArrayList.get(i).getLeftchar() == leftChar){
                return i;
            }
        }
        return 0;
    }

    private static String findFollow(char charAt) {
        int index = 0;
        for(int i = 0;i < followArrayList.size();i++){

            Follow follow = followArrayList.get(i);
            if(charAt == follow.getLeft()){
                index = i;
                break;
            }
        }
        String str = followArrayList.get(index).getRightStr();
        if(allTerChar(str)){
            return str;
        }else{
            String resultStr="";
            for(int i = 0;i < str.length();i++){
                if(isTer(str.charAt(i))){
                    resultStr += str.charAt(i);
                }else{
                    resultStr += findFollow(str.charAt(i));
                }
            }
            return resultStr;
        }
    }

    private static String findFirst(char charAt) {
        int index = 0;
        for(int i = 0;i < firstArrayList.size();i++){

            First first = firstArrayList.get(i);
            if(charAt == first.getLeft()){
                index = i;
                break;
            }
        }
        String str = firstArrayList.get(index).getRightStr();
        if(allTerChar(str)){
            return str;
        }else{
            String resultStr="";
            for(int i = 0;i < str.length();i++){
                if(isTer(str.charAt(i))){
                    resultStr += str.charAt(i);
                }else{
                    resultStr += findFirst(str.charAt(i));
                }
            }
            return resultStr;
        }
    }

    private static boolean allTerChar(String str) {
        for(int i = 0;i < str.length();i++){
            if(!isTer(str.charAt(i)))
                return false;
        }
        return true;
    }

    private static boolean noInclude(char charAt,String str) {
        if(str.indexOf(charAt) == -1){
            return true;
        }else
            return false;
    }

    private static boolean isToNull(char charAt) {
        for(Node node:arrayList){
            if(node.getLeftChar() == charAt){
                if(node.getRightChar().charAt(0) == '^'){
                    return true;
                }
            }
        }

        for(Node node:arrayList){
            if(node.getLeftChar() == charAt){
                if(!isTer(node.getRightChar().charAt(0))){
                    return isToNull(node.getRightChar().charAt(0));
                }
            }
        }
        return false;
    }

    private static boolean isTer(char charAt) {
        if(termiChar.contains(charAt + "")){
            return true;
        }else
            return false;
    }

    private static void leftDiguiShiXian(String []strings,char leftChar) {
        int length = strings.length;

        //实现数组反转，让后面消除左递归时候不以leftChar开头的在前。
        for(int i = 0;i < length/2;i++){
            String swap = strings[i];
            strings[i] = strings[length-1-i];
            strings[length-1-i] = swap;
        }

        char newLeft = (char) (leftChar+32);

        //用来存新产生的右边式子。
        String swap1 = leftChar+"->";
        String swap2 = newLeft+"->";

        for (String str:strings){
            if(leftChar == str.charAt(0)){
                swap2 += str.substring(1)+newLeft + '|';
            }else{
                swap1 += str+newLeft + '|';
            }
        }

        //加入空串
        swap2 += '^';

        //添加
        arrayListString.add(swap1);
        arrayListString.add(swap2);
    }

    private static int findIndex(char charAt) {
        for(int i = 0;i < arrayListString.size();i++){
            if(arrayListString.get(i).charAt(0) == charAt){
                return i;
            }
        }
        return 0;
    }

    private static int findIndexByfirst(char aChar, char aChar1) {

        for(int i = 0;i < arrayListString.size();i++){
            String str = arrayListString.get(i);
            if(str.charAt(0) == aChar && str.charAt(3) == aChar1)
                return i;
        }
        return -1;
    }

    private static boolean CompeleteDigui(char first,int index) {

        //回溯限定条件
        if(index == arrayListString.size()+1)
            return false;
        if(first == arrayListString.get(0).charAt(0)){
            return  true;
        }

        for(String str:arrayListString){
            if(str.charAt(0) == first){
                return CompeleteDigui(str.charAt(3),index+1);
            }
        }
        return false;
    }

    private static boolean publicChar(String inputStr) {
        String [] strings = inputStr.split("\\|");
        int mount = 0;
        char first = strings[0].charAt(0);

        for(String str : strings){
            if(first == str.charAt(0)) mount++;
        }

        if(mount > 1)
            return true;
        else
            return false;
    }

    private static boolean leftDiGui(char leftChar, String inputStr) {
        String [] strings = inputStr.split("\\|");

        for(String str : strings){
            if(leftChar == str.charAt(0)) return true;
        }

        return false;
    }
}

class Node{
    char leftChar;
    String rightChar;

    public char getLeftChar() {
        return leftChar;
    }

    public void setLeftChar(char leftChar) {
        this.leftChar = leftChar;
    }

    public String getRightChar() {
        return rightChar;
    }

    public void setRightChar(String rightChar) {
        this.rightChar = rightChar;
    }

    public Node(char leftChar, String rightChar) {
        this.leftChar = leftChar;
        this.rightChar = rightChar;
    }

    public Node(){

    }
}

class First{

    char left;
    String rightStr;

    public char getLeft() {

        return left;
    }

    public void setLeft(char left) {
        this.left = left;
    }

    public String getRightStr() {
        return rightStr;
    }

    public void setRightStr(String rightStr) {
        this.rightStr = rightStr;
    }
}
class Follow{

    char left;
    String rightStr;

    public char getLeft() {

        return left;
    }

    public void setLeft(char left) {
        this.left = left;
    }

    public String getRightStr() {
        return rightStr;
    }

    public void setRightStr(String rightStr) {
        this.rightStr = rightStr;
    }
}
class AyalyseTable{
    char leftchar;
    //形成终结符与产生式之间的映射
    Map<Character,String> map;

    public Map<Character, String> getMap() {
        return map;
    }

    public void setMap(Map<Character, String> map) {
        this.map = map;
    }

    public char getLeftchar() {
        return leftchar;
    }

    public void setLeftchar(char leftchar) {
        this.leftchar = leftchar;
    }

}