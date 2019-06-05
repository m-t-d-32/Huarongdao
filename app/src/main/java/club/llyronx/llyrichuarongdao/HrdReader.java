package club.llyronx.llyrichuarongdao;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

class HrdReader {
    static HrdMap readMapFromPlainText(InputStream istream) {
        Scanner in = new Scanner(istream);
        String mapName = in.next();
        int count = in.nextInt();
        ArrayList<HrdChess> chesses = new ArrayList<>();
        for (int i = 0; i < count; ++i){
            String name = in.next();
            int beginx = in.nextInt(), beginy = in.nextInt();
            int width = in.nextInt() - beginx, height = in.nextInt() - beginy;
            HrdChess chess = new HrdChess(name, beginx, beginy, width, height);
            chesses.add(chess);
        }
        int activeIndex = in.nextInt() - 1;
        return new HrdMap(mapName, chesses, activeIndex);
    }

    static HashMap<String, Integer> readRecordsFromPlainText(InputStream istream) {
        Scanner in = new Scanner(istream);
        HashMap<String, Integer> results = new HashMap<>();
        int lineCount = in.nextInt();
        for (int i = 0; i < lineCount; ++i) {
            String resultName = in.next();
            int resultTime = in.nextInt();
            results.put(resultName, resultTime);
        }
        return results;
    }

    static HrdMap readMapFromRawText(InputStream istream) throws IOException {
        ArrayList<HrdChess> chesses = new ArrayList<>();
        DataInputStream distream = new DataInputStream(istream);
        String mapName = distream.readUTF();
        int size = distream.readInt();
        for (int i = 0; i < size; ++i){
            HrdChess hrdChess = readChessFromRawText(istream);
            chesses.add(hrdChess);
        }
        int activeIndex = distream.readInt();
        return new HrdMap(mapName, chesses, activeIndex);
    }

    static HrdChess readChessFromRawText(InputStream istream) throws IOException {
        DataInputStream distream = new DataInputStream(istream);
        String name = distream.readUTF();
        int beginX = distream.readInt();
        int beginY = distream.readInt();
        int width = distream.readInt() - beginX;
        int height = distream.readInt() - beginY;
        return new HrdChess(name, beginX, beginY, width, height);
    }

    static HashMap<String, Integer> readRecordsFromRawText(InputStream istream) throws IOException {
        DataInputStream distream = new DataInputStream(istream);
        HashMap<String, Integer> results = new HashMap<>();
        int lineCount = distream.readInt();
        for (int i = 0; i < lineCount; ++i){
            String resultName = distream.readUTF();
            int resultTime = distream.readInt();
            results.put(resultName, resultTime);
        }
        return results;
    }
}
