import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

public class Test {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String a = "www";

        Character character = 'a';
        System.out.println(character);
        Stack<Character> stack = new Stack<>();
        stack.add('1');
        stack.add('2');
        for(int i = 0;i < stack.size();i++){
            System.out.print(stack.get(i));
        }
//        User user = new User();
//        user.setUsername("1");
//        user.setPassword("123");
//
//        ArrayList<User> arrayList = new ArrayList<>();
//        arrayList.add(user);
//        System.out.println(arrayList);
    }
}
class User{
    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
