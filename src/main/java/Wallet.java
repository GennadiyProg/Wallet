import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Wallet {
    private static ConcurrentMap<String, Long> wallet = new ConcurrentHashMap<>();

    public static void addPayment(String payment){
        Matcher matcher = Pattern.compile("(?<currency>\\w+)(?:\\s+(?<money>-?\\w+))?").matcher(payment);
        if (matcher.find()){
            if (matcher.group("currency").equals("quit")) System.exit(1);
            if (matcher.group("currency").length() != 3){
                System.out.printf("Incorrect currency: %s\n", matcher.group("currency"));
                return;
            }
            try{
                long money = Long.parseLong(matcher.group("money"));
                String currency = matcher.group("currency").toUpperCase();
                if (wallet.containsKey(currency)) {
                    if (wallet.get(currency) + money < 0) {
                        System.out.println("Not enough money!");
                    } else {
                        wallet.computeIfPresent(currency, (k, v) -> v + money);
                    }
                } else {
                    if (money < 0){
                        System.out.println("Not enough money!");
                    } else {
                        wallet.put(currency, money);
                    }
                }
            } catch (NumberFormatException e){
                System.out.printf("Wrong number: %s\n", matcher.group("money"));
            }
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length > 0) {
            String inputFileName = args[0];
            BufferedReader reader = new BufferedReader(new FileReader(inputFileName));
            String line;
            while ((line = reader.readLine()) != null) {
                addPayment(line);
            }
        }
        Runnable printBalance = () -> {
            while(true) {
                System.out.println();
                wallet.forEach((k, v) -> {
                    if (v != 0) System.out.println(k + " " + v);
                });
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        Thread output = new Thread(printBalance);
        output.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while(true){
            String console = reader.readLine();
            addPayment(console);
        }
    }
}
