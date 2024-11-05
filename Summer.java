import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import swiftbot.*;



public class Summer {
    public static void main(String[] args) throws IOException {
        Title();
        SwiftBotAPI API = new SwiftBotAPI();
        AtomicBoolean buttonPressed = new AtomicBoolean(false);
        AtomicBoolean continueScanning = new AtomicBoolean(false);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

      
        while (true) {
            try {
                API.disableButton(Button.A);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //Button A
            API.enableButton(Button.A, () -> {
                System.out.println("Button A pressed!");
                System.out.println("");
                buttonPressed.set(true);
            });

            //press Button A
            System.out.println("To continue program press button A");
            System.out.println("");

            //Wait for Button A to be pressed
            while (!buttonPressed.get()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            
           //description on the task
            System.out.println("The QR Code you will scan must consist of 2 Values.");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        
            System.out.println("'NumberValue:ColourValue'");
            
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            System.out.println("NumberValue must be between 0 - 100");
            
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            System.out.println("ColourValue can only be these colours; Red, Blue, Green and White.");

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
          
            //QR code
            System.out.println("Scan QR Code....");
            System.out.println("");

            BufferedImage img = API.getQRImage();
            String decodedText = null;
            try {
                decodedText = API.decodeQRImage(img);
            } catch (IllegalArgumentException e) {
                System.out.println("No QR code Detected... Please try again...");
                System.out.println("");
                continue;
            }

            if (decodedText != null && !decodedText.isEmpty()) {
                System.out.println("QR code: " + decodedText);

                //validate QR code
                String regex = "(\\d{1,3}):([a-zA-Z]+)";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(decodedText);

                if (matcher.matches()) {
                    int value1 = Decimal(matcher.group(1));
                    String value2 = matcher.group(2).toLowerCase();

                    //validate values
                    if (value1 >= 0 && value1 <= 100 &&
                            (value2.equals("red") || value2.equals("green") || value2.equals("blue") || value2.equals("white"))) {
                        System.out.println("Number Value: " + value1);
                        System.out.println("Colour Value: " + value2.substring(0, 1).toUpperCase() + value2.substring(1).toLowerCase());
                        System.out.println("");

                        //convert
                        String octalValue = Octal(value1);
                        String hexValue = Hexadecimal(value1);
                        String binaryValue = Binary(value1);

                        //converted values
                        System.out.println("Octal: " + octalValue);
                        System.out.println("Hexadecimal: " + hexValue);
                        System.out.println("Binary: " + binaryValue);
                        System.out.println("");

                        //speed
                        int speed = (Integer.parseInt(octalValue, 8) < 50) ? Integer.parseInt(octalValue, 8) + 50 : Math.min(Integer.parseInt(octalValue, 8), 100);
                        System.out.println("Speed: " + speed);

                        //duration
                        int duration = (hexValue.length() == 1) ? 1000 : 2000;
                        System.out.println("Duration: " + duration + " ms");
                        System.out.println("");
                        
                        //underlight colour
                        int[] color = Color(value2);
                        API.fillUnderlights(color);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        API.disableUnderlights();

                        //current time BEFORE
                        String startTime = LocalTime.now().format(timeFormatter);
                        System.out.println("Start Time: " + startTime);

                        //binary value
                        for (int i = binaryValue.length() - 1; i >= 0; i--) {
                            char bit = binaryValue.charAt(i);
                            if (bit == '1') {
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                API.move(speed, speed, duration); //move forward
                                API.move(85, -85, 500); //90 degrees clockwise
                            } else if (bit == '0') {
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                API.move(speed, speed, duration); //move forward
                                API.move(-85, 85, 500); //90 degrees anti-clockwise
                            }
                            API.stopMove(); //stop after each move
                        }

                        //current time AFTER
                        String endTime = LocalTime.now().format(timeFormatter);
                        System.out.println("End Time: " + endTime);
                        System.out.println("");

                        API.fillUnderlights(color);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        API.disableUnderlights();


                        API.disableUnderlights();


                        System.out.println("Do you want to do another journey? 'Y' = yes, 'X' = no");
                        System.out.println("");

                        try {
                            API.disableButton(Button.Y);
                            API.disableButton(Button.X);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        //Y button
                        API.enableButton(Button.Y, () -> {
                            System.out.println("Button Y pressed! Restarting...");
                            System.out.println("");
                            continueScanning.set(true);
                        });

                        //X button
                        API.enableButton(Button.X, () -> {
                            System.out.println("Button X pressed! terminating program...");
                            System.exit(0);
                        });

                        while (!continueScanning.get()) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        }

                        //reset buttonPressed and continueScanning for the nextcycle
                        buttonPressed.set(false);
                        continueScanning.set(false);

                    } else {
                        System.out.println("Invalid values in QR code. Please scan again.");
                        System.out.println("");
                    }
                } else {
                    System.out.println("Invalid QR code. Please scan again.");
                    System.out.println("");
                }
            } else {
                System.out.println("Invalid QR code. Please scan again.");
                System.out.println("");
            }
        }
    }

    private static int Decimal(String value) {
        int result = 0;
        for (int i = 0; i < value.length(); i++) {
            char digit = value.charAt(i);
            if (digit >= '0' && digit <= '9') {
                result = result * 10 + (digit - '0');
            } else {
                throw new IllegalArgumentException("Invalid input: " + value);
            }
        }
        return result;
    }

    private static String Octal(int decimalValue) {
        StringBuilder result = new StringBuilder();
        if (decimalValue == 0) {
            return "0";
        }
        while (decimalValue != 0) {
            int remainder = decimalValue % 8;
            result.insert(0, remainder);
            decimalValue = decimalValue / 8;
        }
        return result.toString();
    }
    
    private static String Binary(int decimalValue) {
        StringBuilder result = new StringBuilder();
        if (decimalValue == 0) {
            return "0";
        }
        while (decimalValue != 0) {
            int remainder = decimalValue % 2;
            result.insert(0, remainder);
            decimalValue = decimalValue / 2;
        }
        return result.toString();
    }

    private static String Hexadecimal(int decimalValue) {
        StringBuilder result = new StringBuilder();
        if (decimalValue == 0) {
            return "0";
        }
        while (decimalValue != 0) {
            int remainder = decimalValue % 16;
            if (remainder < 10) {
                result.insert(0, remainder);
            } else {
                result.insert(0, (char) ('a' + remainder - 10));
            }
            decimalValue = decimalValue / 16;
        }
        return result.toString();
    }
    
    private static int[] Color(String color) {
        switch (color.toLowerCase()) {
            case "red":
                return new int[]{255, 0, 0};
            case "green":
                return new int[]{0, 255, 0};
            case "blue":
                return new int[]{0, 0, 255};
            case "white":
                return new int[]{255, 255, 255};
            default:
                throw new IllegalArgumentException("Invalid color: " + color);
                
        }
    }





    public static void Title() {
        System.out.println("");
        System.out.println(" __              ___       ");
        System.out.println("(_     _  _  _ _  | _  _| |");
        System.out.println("__)|_|||||||(-|   |(_|_)|(.");
        System.out.println("");
        System.out.println("");
        
    }
}