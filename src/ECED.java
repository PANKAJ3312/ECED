
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;

public class ECED {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
//        String input = scanner.nextLine();
//        scanner.close();
//        String encodedText, simulatedText, decodedText;

//        File inputFile3 = new File("sample.txt");
//        try (FileOutputStream fileOutputStream = new FileOutputStream("received.txt")) {
//            BitLevelErrEmu bitLevelErrEmu = new BitLevelErrEmu(inputFile);
//            fileOutputStream.write(bitLevelErrEmu.getBitErrFile());
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }

        String command = scanner.next();
        switch (command) {
            case "encode":
                File inputFile = new File("send.txt");
//                BinaryEncode binaryEncode = new BinaryEncode(inputFile);
//                binaryEncode.binaryEnco();
                HamingEncodeDecode hamingEncodeDecode = new HamingEncodeDecode(inputFile);
                hamingEncodeDecode.hammingEncode();
                break;
            case "send":
                File inputFile1 = new File("encoded.txt");
                BitLevelErrEmu bitLevelErrEmu = new BitLevelErrEmu(inputFile1);
                File file = new File("received.txt");
                try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                    fileOutputStream.write(bitLevelErrEmu.getBitErrFile());
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                break;
            case "decode":
                File inputFile4 = new File("encoded.txt");
//                BinaryDecode binaryDecode = new BinaryDecode(inputFile4);
//                binaryDecode.biDeco();
                HamingEncodeDecode hamingEncodeDecode1 = new HamingEncodeDecode(inputFile4);
                hamingEncodeDecode1.hammingDecode();
                break;
            default:
                break;
        }

//        BinaryDecode binaryDecode = new BinaryDecode(inputFile3);
//        binaryDecode.biDeco();

//
//        System.out.println(input);
//
//        Encoder encoder = new Encoder(input);
//        encodedText = encoder.getTextToEncode();
//        System.out.println(encodedText);
//
//        ErrorSimulator errorSimulator = new ErrorSimulator(encodedText);
//        simulatedText = errorSimulator.getText();
//        System.out.println(simulatedText);
//
//        Decoder decoder = new Decoder(simulatedText);
//        decodedText = decoder.getTextToDecode();
//        System.out.println(decodedText);
    }
}

class HamingEncodeDecode {
    File file;

    public HamingEncodeDecode(File file) {
        this.file = file;
    }

    public void hammingEncode() {

        BinaryEncode binaryEncode = new BinaryEncode(file);
        String oriBinary = binaryEncode.binaryConversion();

        System.out.println("Original binary");
        System.out.println(oriBinary);

        int[] binaryArr = new int[oriBinary.length()];

        for (int i = 0; i < binaryArr.length; i++) {
            binaryArr[i] = Byte.parseByte(String.valueOf(oriBinary.charAt(i)));
        }

        List<Integer> hamEnco = new ArrayList<>();
        for (int i = 0; i < binaryArr.length; i += 4) {
            // 1st parity
            if ((binaryArr[i] + binaryArr[i + 1] + binaryArr[i + 3]) % 2 == 0) {
                hamEnco.add(0);
            } else {
                hamEnco.add(1);
            }
            // 2nd parity
            if ((binaryArr[i] + binaryArr[i + 2] + binaryArr[i + 3]) % 2 == 0) {
                hamEnco.add(0);
            } else hamEnco.add(1);
            //3rd original bit
            hamEnco.add(binaryArr[i]);
            //4th parity
            if ((binaryArr[i + 1] + binaryArr[i + 2] + binaryArr[i + 3]) % 2 == 0) {
                hamEnco.add(0);
            } else {
                hamEnco.add(1);
            }
            //5th 6th 7th original bit
            hamEnco.add(binaryArr[i + 1]);
            hamEnco.add(binaryArr[i + 2]);
            hamEnco.add(binaryArr[i + 3]);
            //8th add 0
            hamEnco.add(0);
        }

        //Grouping 8 bits
        List<String> inBinary = new ArrayList<>();
        for (int i = 0; i < hamEnco.size(); i += 8) {
            inBinary.add(String.valueOf(hamEnco.subList(i, i + 8)).replace(", ", "").
                    replace("[","").replace("]", ""));
        }

        System.out.println("EncodedBinary");
        System.out.println(inBinary);

        //Converting 8 bits to byte
        byte[] bytes = new byte[inBinary.size()];
        for (int i = 0; i < bytes.length; i++) {
            int abc = Integer.parseInt(inBinary.get(i), 2);
            bytes[i] = (byte) abc;
        }


        File fileOutput = new File("encoded.txt");
        try (FileOutputStream fileOutputStream = new FileOutputStream(fileOutput)) {
            fileOutputStream.write(bytes);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void hammingDecode() {
        String oriBinary = new BinaryEncode(file).binaryConversion();

        System.out.println("Original binary");
        System.out.println(oriBinary);

        int[] binaryArr = new int[oriBinary.length()];

        for (int i = 0; i < binaryArr.length; i++) {
            binaryArr[i] = Byte.parseByte(String.valueOf(oriBinary.charAt(i)));
        }

        List<Integer> hamDeco = new ArrayList<>();

        //Correcting bits in bytes array
        for (int i = 0; i < binaryArr.length; i += 8) {
            int badBit = 0;
            int a1 = (binaryArr[i + 2] + binaryArr[i + 4] + binaryArr[i + 6]) % 2;
            int a2 = (binaryArr[i + 2] + binaryArr[i + 5] + binaryArr[i + 6]) % 2;
            int a3 = (binaryArr[i + 4] + binaryArr[i + 5] + binaryArr[i + 6]) % 2;

            if (a1 != binaryArr[i]) {
                badBit += 1;
            }

            if (a2 != binaryArr[i + 1]) {
                badBit += 2;
            }

            if (a3 != binaryArr[i + 3]) {
                badBit += 4;
            }

            if (badBit != 0) {
                if (binaryArr[badBit - 1] == 0) {
                    binaryArr[badBit - 1] = 1;
                } else {
                    binaryArr[badBit - 1] = 0;
                }
            }
            hamDeco.add(binaryArr[i + 2]);
            hamDeco.add(binaryArr[i + 4]);
            hamDeco.add(binaryArr[i + 5]);
            hamDeco.add(binaryArr[i + 6]);
        }

        List<String> inBinary = new ArrayList<>();
        for (int i = 0; i < hamDeco.size(); i += 8) {
            inBinary.add(String.valueOf(hamDeco.subList(i, i + 8)).replace(", ", "").
                    replace("[","").replace("]", ""));
        }

        System.out.println("decoded binary");
        System.out.println(inBinary);

        byte[] bytes = new byte[inBinary.size()];
        for (int i = 0; i < bytes.length; i++) {
            int abc = Integer.parseInt(inBinary.get(i), 2);
            bytes[i] = (byte) abc;
        }

        System.out.println("Decoded Bytes");
        System.out.println(Arrays.toString(bytes).replace(", ", "").
                replace("[","").replace("]", ""));

        File fileOutput = new File("decoded.txt");
        try (FileOutputStream fileOutputStream = new FileOutputStream(fileOutput)) {
            fileOutputStream.write(bytes);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}

class BitLevelErrEmu {
    File input;

    public BitLevelErrEmu(File input) {
        this.input = input;
    }

    public byte[] getBitErrFile() {
        try (FileInputStream fileInputStream = new FileInputStream(input)) {
            byte[] bytes = fileInputStream.readAllBytes();
            System.out.println(Arrays.toString(bytes));
            Random random = new Random();
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] ^= 1<< random.nextInt(7); // Changing random bit of bytes variable.
            }
            return bytes;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}

class BinaryDecode {
    File input;

    public BinaryDecode(File input) {
        this.input = input;
    }

    public void biDeco() {
        BinaryEncode binaryEncode12 = new BinaryEncode(input);
        String str = binaryEncode12.binaryConversion();

        int[] binaryInt = new int[str.length()];

        for (int i = 0; i < binaryInt.length; i++) {
            binaryInt[i] = Byte.parseByte(String.valueOf(str.charAt(i)));
        }

        List<Integer> decodeco = new ArrayList<>();
        for (int i = 0; i < binaryInt.length; i += 8) {
            if (binaryInt[i] == binaryInt[i + 1]) {
                decodeco.add(binaryInt[i]);
            } else {
                decodeco.add(binaryInt[i + 2] ^ binaryInt[i + 4] ^ binaryInt[i + 6]);
            }
            if (binaryInt[i + 2] == binaryInt[i + 3]) {
                decodeco.add(binaryInt[i + 2]);
            } else {
                decodeco.add(binaryInt[i] ^ binaryInt[i + 4] ^ binaryInt[i + 6]);
            }
            if (binaryInt[i + 4] == binaryInt[i + 5]) {
                decodeco.add(binaryInt[i + 4]);
            } else {
                decodeco.add(binaryInt[i] ^ binaryInt[i + 2] ^ binaryInt[i + 6]);
            }
        }

        if (decodeco.size() % 8 != 0) {
            int a = decodeco.size() - 1;
            for (int i = 0; i < decodeco.size() % 8; i++) {
                decodeco.remove(a - i);
            }
        }

        List<String> inBina1 = new ArrayList<>();
        for (int i = 0; i < decodeco.size(); i += 8) {
            inBina1.add(String.valueOf(decodeco.subList(i, i + 8)).replace(", ", "").
                    replace("[","").replace("]", ""));
        }

        byte[] bytes = new byte[inBina1.size()];
        for (int i = 0; i < bytes.length; i++) {
            int abc = Integer.parseInt(inBina1.get(i), 2);
            bytes[i] = (byte) abc;
        }

        File file5 = new File("decoded.txt");
        try (FileOutputStream fileOutputStream = new FileOutputStream(file5)) {
            fileOutputStream.write(bytes);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        System.out.println(str);
        System.out.println(decodeco.toString().replace(", ", ""));
        System.out.println(str.length());
        System.out.println(decodeco.size());
    }
}

class BinaryEncode {
    File input;

    public BinaryEncode(File input) {
        this.input = input;
    }

    public String binaryConversion() {
        StringBuilder binaryTxt = new StringBuilder();
        try (FileInputStream fileInputStream = new FileInputStream(input)) {

            byte[] bytes = fileInputStream.readAllBytes();
            for (byte b : bytes) {
                StringBuilder stringBuilder = new StringBuilder("00000000");
                String temp = Integer.toBinaryString(b);
                String binaaa = null;
                if (temp.length() == 32) {
                    binaaa = temp.substring(24, 32);
                } else {
                    binaaa = temp;
                }
                binaryTxt.append(stringBuilder.replace(8 - binaaa.length(), 8, binaaa));
            }
            System.out.println(Arrays.toString(bytes));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return binaryTxt.toString();
    }

    public void binaryEnco() {
        String binaryStr = binaryConversion();
        int[] binaryInt = new int[binaryStr.length()];

        for (int i = 0; i < binaryInt.length; i++) {
            binaryInt[i] = Byte.parseByte(String.valueOf(binaryStr.charAt(i)));
        }

        List<Integer> binaryParity = new ArrayList<>();

        for (int i = 0; i < binaryInt.length; i++) {
            for (int j = 0; j < 2; j++) {
                binaryParity.add(binaryInt[i]);
            }
            if ((i + 1) % 3 == 0) {
                int a = (binaryInt[i - 2] ^ binaryInt[i - 1] ^ binaryInt[i]);
                binaryParity.add(a);
                binaryParity.add(a);
            }
        }

        int zzz = binaryParity.size() % 8;
        int xxx = binaryParity.size();

        if (zzz != 0) {
            if (zzz == 6) {
                int sscc = (binaryParity.get(xxx - 6) ^ binaryParity.get(xxx - 4) ^ binaryParity.get(xxx - 2));
                binaryParity.add(sscc);
                binaryParity.add(sscc);
            } else if (zzz == 4) {
                int aacc = (binaryParity.get(xxx - 4) ^ binaryParity.get(xxx - 2));
                binaryParity.add(0);
                binaryParity.add(0);
                binaryParity.add(aacc);
                binaryParity.add(aacc);
            } else if (zzz == 2) {
                binaryParity.add(0);
                binaryParity.add(0);
                binaryParity.add(0);
                binaryParity.add(0);
                binaryParity.add(binaryParity.get(xxx - 2));
                binaryParity.add(binaryParity.get(xxx - 2));
            }
//            int a = binaryParity.size() % 8;
//            for (int i = 0; i < 8 - a; i++) {
//                binaryParity.add(0);
//            }
        }


        List<String> inBina = new ArrayList<>();
        for (int i = 0; i < binaryParity.size(); i += 8) {
            inBina.add(String.valueOf(binaryParity.subList(i, i + 8)).replace(", ", "").
                    replace("[","").replace("]", ""));
        }
        System.out.println("-------");
        System.out.println(inBina);

        byte[] bytes = new byte[inBina.size()];
        for (int i = 0; i < bytes.length; i++) {
            int abc = Integer.parseInt(inBina.get(i), 2);
            bytes[i] = (byte) abc;
        }

/*        File file = new File("encoded.txt");
//        File file1 = new File("C:\\Users\\ambek\\OneDrive\\Desktop\\samOutput.txt");
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
//            FileOutputStream fileOutputStream1 = new FileOutputStream(file1);
            fileOutputStream.write(bytes);
//            fileOutputStream1.write(bytes);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }*/
        System.out.println(Arrays.toString(bytes));

        System.out.println(Arrays.toString(binaryInt));
        System.out.println(binaryParity);
        System.out.println(binaryInt.length);
        System.out.println(binaryParity.size());
        System.out.println((binaryInt.length * 2) + ((binaryInt.length / 3) * 2));
    }
}

//class Encoder {
//
//    String textToEncode;
//
//    public Encoder(String textToEncode) {
//        this.textToEncode = textToEncode;
//    }
//
//    public String getTextToEncode() {
//        StringBuilder encodedText = new StringBuilder();
//        for (int i = 0; i < textToEncode.length(); i++) {
//            char ch = textToEncode.charAt(i);
//            encodedText.append(ch).append(ch).append(ch);
//        }
//        return encodedText.toString();
//    }
//}
//
//class ErrorSimulator {
//    String text;
//    final int upper = 122;    //characters end ASCII code
//    final int lower = 65; //characters start ASCII code
//
//    public ErrorSimulator(String text) {
//        this.text = text;
//    }
//
//    Random random = new Random(528491);
//
//    public String getText() {
//        StringBuilder manipulatedText = new StringBuilder();
//        for (int i = 0; i < text.length(); i += 3) {
//            String ch = String.valueOf(text.charAt(i));
//            switch (random.nextInt(3) + 1) {
//                case 1:
//                    manipulatedText.append(
//                            (char) (random.nextInt(upper - lower) + lower)).append(ch + ch);
//                    break;
//                case 2:
//                    manipulatedText.append(ch).append(
//                            (char) (random.nextInt(upper - lower) + lower)).append(ch);
//                    break;
//                case 3:
//                    manipulatedText.append(ch + ch).append(
//                            (char) (random.nextInt(upper - lower) + lower));
//                    break;
//                default:
//                    break;
//            }
//        }
//        return manipulatedText.toString();
//    }
//}
//
//class Decoder {
//    String textToDecode;
//
//    public Decoder(String textToDecode) {
//        this.textToDecode = textToDecode;
//    }
//
//    public String getTextToDecode() {
//        StringBuilder decodedText = new StringBuilder();
//        for (int i = 0; i < textToDecode.length(); i += 3) {
//            if (textToDecode.charAt(i) == textToDecode.charAt(i + 1)) {
//                decodedText.append(textToDecode.charAt(i));
//            } else if (textToDecode.charAt(i + 1) == textToDecode.charAt(i + 2)) {
//                decodedText.append(textToDecode.charAt(i + 1));
//            } else if (textToDecode.charAt(i + 2) == textToDecode.charAt(i)) {
//                decodedText.append(textToDecode.charAt(i + 2));
//            }
//        }
//        return decodedText.toString();
//    }
//}
