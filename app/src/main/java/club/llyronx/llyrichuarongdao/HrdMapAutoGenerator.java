package club.llyronx.llyrichuarongdao;

import java.util.ArrayList;
import java.util.Random;

public class HrdMapAutoGenerator {
    private static final int WIDTHCOUNT = 4;
    private static final int HEIGHTCOUNT = 5;
    private static final int MAXTRYTIME = 100;
    private static int MAXBEFOREMOVE = 1000;
    private static final int AFTERMOVE = 1000;
    private static String [] chessNames = {
            "曹操", "张飞", "黄忠", "赵云", "马超", "关羽", "卒", "卒", "卒", "卒",
    };
    private static int [] chessOccupys = {
            4, 2, 2, 2, 2, 2, 1, 1, 1, 1
    };
    private static int mainChessIndex = 0;
    private static Point mainChessWinPos = new Point(1, 3);
    private static Point []walkPaths = {
            new Point(1, 0),
            new Point(0, 1),
    };
    private static Point []walkDirections = {
            new Point(1, 1),
            new Point(-1, 1),
            new Point(1, -1),
            new Point(-1, -1),
    };

    public static void setMaxBefore(int maxBefore){
        MAXBEFOREMOVE = maxBefore;
    }
    private static void applyToBitMap(boolean [][]bitmap, HrdChess chess, boolean value){
        for (int i = 0; i < chess.getWidth(); ++i){
            for (int j = 0; j < chess.getHeight(); ++j){
                if (bitmap[i + chess.getBegin().x][j + chess.getBegin().y] == value){
                    throw new NullPointerException();
                }
                bitmap[i + chess.getBegin().x][j + chess.getBegin().y] = value;
            }
        }
    }
    private static boolean testInBitMap(boolean [][]bitmap, HrdChess chess, boolean testValue){
        for (int i = 0; i < chess.getWidth(); ++i){
            for (int j = 0; j < chess.getHeight(); ++j){
                if (bitmap[i + chess.getBegin().x][j + chess.getBegin().y] == testValue){
                    return true;
                }
            }
        }
        return false;
    }
    public static HrdMap randomGenerateFromWin(HrdMap winMap){
        int thisTimeWalkChessIndex = -1;
        int thisTimeWalkPathIndex = -1, thisTimeWalkDirectionIndex = -1;
        Random random = new Random();
        ArrayList<HrdChess> chesses = winMap.getChesses();
        //Prepare bitmap
        boolean [][]occupyBitMap = new boolean[WIDTHCOUNT][];
        for (int i = 0; i < WIDTHCOUNT; ++i){
            occupyBitMap[i] = new boolean[HEIGHTCOUNT];
            for (int j = 0; j < HEIGHTCOUNT; ++j){
                occupyBitMap[i][j] = false;
            }
        }
        for (int i = 0; i < chesses.size(); ++i){
            HrdChess chess = chesses.get(i);
            applyToBitMap(occupyBitMap, chess, true);
        }
        //Test and move
        //We say that they must move the main once:
        boolean mainMove = false;
        int moveCountBefore = 0, moveCountAfter = 0;
        while (true){
            int []testSeq = SelectActivity.generateRandomNumbers(chesses.size(), chesses.size());
            int []testPath = SelectActivity.generateRandomNumbers(walkPaths.length, walkPaths.length);
            int []testDirection = SelectActivity.generateRandomNumbers(walkDirections.length, walkDirections.length);
            boolean testpass = false;
            for (int j = 0; j < testSeq.length; ++j){
                //Every Time walk for 1 or 2 steps, up, down, left or right
                for (int k = 0; k < testPath.length; ++k){
                    for (int t = 0; t < testDirection.length; ++t){
                        if (testSeq[j] == thisTimeWalkChessIndex &&
                                testPath[k] == thisTimeWalkPathIndex &&
                                testDirection[t] == walkDirections.length - 1 - thisTimeWalkDirectionIndex){
                            continue;
                        }
                        HrdChess chess = chesses.get(testSeq[j]);
                        Point directTo = new Point(walkPaths[testPath[k]].x * walkDirections[testDirection[t]].x,
                                walkPaths[testPath[k]].y * walkDirections[testDirection[t]].y);
                        if (chess.getBegin().x + directTo.x < 0 || chess.getBegin().x + directTo.x + chess.getWidth() > WIDTHCOUNT ||
                            chess.getBegin().y + directTo.y < 0 || chess.getBegin().y + directTo.y + chess.getHeight() > HEIGHTCOUNT){
                            continue;
                        }
                        applyToBitMap(occupyBitMap, chess, false);
                        chess.setBegin(new Point(chess.getBegin().x + directTo.x, chess.getBegin().y + directTo.y));
                        testpass = !testInBitMap(occupyBitMap, chess, true);
                        if (testpass){
                            thisTimeWalkChessIndex = testSeq[j];
                            thisTimeWalkPathIndex = testPath[k];
                            thisTimeWalkDirectionIndex = testDirection[t];
                            applyToBitMap(occupyBitMap, chess, true);
                            if (chesses.get(thisTimeWalkChessIndex) == winMap.getMainChess()){
                                mainMove = chess.getBegin().x != mainChessWinPos.x;
                            }
                            if (mainMove){
                                ++moveCountAfter;
                                if (moveCountAfter >= AFTERMOVE){
                                    //System.out.println("Generate Success");
                                    return new HrdMap("随机关卡", chesses, chesses.indexOf(winMap.getMainChess()));
                                }
                            }
                            else {
                                ++moveCountBefore;
                                if (moveCountBefore >= MAXBEFOREMOVE){
                                    //System.out.println("Generate Fail");
                                    return null;
                                }
                            }
                            break;
                        }
                        else {
                            chess.setBegin(new Point(chess.getBegin().x - directTo.x, chess.getBegin().y - directTo.y));
                            applyToBitMap(occupyBitMap, chess, true);
                        }
                    }
                    if (testpass){
                        break;
                    }
                }
                if (testpass){
                    break;
                }
            }
        }
    }
    public static HrdMap randomGenerateWin(){
        Point [] chessPositions = new Point[chessNames.length];
        Point [] chessTrueOccupies = new Point[chessNames.length];
        chessPositions[mainChessIndex] = mainChessWinPos;
        chessTrueOccupies[mainChessIndex] = new Point(2, 2);
        Random random = new Random();
        boolean [][]occupyBitMap = new boolean[WIDTHCOUNT][];
        for (int i = 0; i < WIDTHCOUNT; ++i){
            occupyBitMap[i] = new boolean[HEIGHTCOUNT];
            for (int j = 0; j < HEIGHTCOUNT; ++j){
                occupyBitMap[i][j] = false;
            }
        }
        for (int i = mainChessWinPos.x; i < mainChessWinPos.x + 2; ++i){
            for (int j = mainChessWinPos.y; j < mainChessWinPos.y + 2; ++j){
                occupyBitMap[i][j] = true;
            }
        }
        //Now for 2-occupy ones
        for (int i = 0; i < chessNames.length; ++i){
            if (chessOccupys[i] == 2) {
                if (random.nextInt(2) == 0){
                    chessPositions[i] = generateEvery(occupyBitMap, MAXTRYTIME, 2, 1);
                    chessTrueOccupies[i] = new Point(2, 1);
                    if (chessPositions[i] == null){
                        chessPositions[i] = generateEvery(occupyBitMap, Integer.MAX_VALUE, 1, 2);
                        chessTrueOccupies[i] = new Point(1, 2);
                    }
                }
                else {
                    chessPositions[i] = generateEvery(occupyBitMap, MAXTRYTIME, 1, 2);
                    chessTrueOccupies[i] = new Point(1, 2);
                    if (chessPositions[i] == null){
                        chessPositions[i] = generateEvery(occupyBitMap, Integer.MAX_VALUE, 2, 1);
                        chessTrueOccupies[i] = new Point(2, 1);
                    }
                }
            }
        }
        //Now for 1-occupy ones
        for (int i = 0; i < chessNames.length; ++i){
            if (chessOccupys[i] == 1) {
                chessPositions[i] = generateEvery(occupyBitMap, Integer.MAX_VALUE, 1, 1);
                chessTrueOccupies[i] = new Point(1, 1);
            }
        }
        //Now generate
        ArrayList<HrdChess> chesses = new ArrayList<>();
        for (int i = 0; i < chessNames.length; ++i){
            chesses.add(new HrdChess(chessNames[i], chessPositions[i].x, chessPositions[i].y,
                    chessTrueOccupies[i].x, chessTrueOccupies[i].y));
        }
        //System.out.println("Generate Win");
        return new HrdMap("随机关卡", chesses, mainChessIndex);
    }

    private static Point generateEvery(boolean [][]occupyBitMap, int maxTryTime, int width, int height) {
        Random random = new Random();
        for (int time = 0; time < maxTryTime; ) {
            int x = random.nextInt(WIDTHCOUNT - width + 1);
            int y = random.nextInt(HEIGHTCOUNT - height + 1);
            HrdChess chess = new HrdChess("", x, y, width, height);
            if (!testInBitMap(occupyBitMap, chess, true)){
                applyToBitMap(occupyBitMap, chess, true);
                return new Point(x, y);
            }
            if (maxTryTime != Integer.MAX_VALUE){
                ++time;
            }
        }
        return null;
    }


}
