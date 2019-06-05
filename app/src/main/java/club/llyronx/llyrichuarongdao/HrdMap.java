package club.llyronx.llyrichuarongdao;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

class HrdMap{
    private static final Point WINPOS = new Point(1, 3);

    private ArrayList<HrdChess> mChesses;
    private HrdChess mMainChess;
    private String mName;

    HrdMap(DataInputStream distream) throws IOException {
        mName = distream.readUTF();
        int size = distream.readInt();
        mChesses = new ArrayList<>();
        for (int i = 0; i < size; ++i){
            mChesses.add(new HrdChess(distream));
        }
        mMainChess = mChesses.get(distream.readInt());
    }

    ArrayList<HrdChess> getChesses(){
        return mChesses;
    }

    HrdChess getMainChess() {
        return mMainChess;
    }

    HrdMap(String name, ArrayList<HrdChess> chesses, int activeIndex){
        mName = name;
        mChesses = chesses;
        mMainChess = chesses.get(activeIndex);
    }

    boolean judgeWin(){
        return mMainChess.getBegin().x == WINPOS.x && mMainChess.getBegin().y == WINPOS.y;
    }

    void printToOstream(DataOutputStream dostream) throws IOException {
        dostream.writeUTF(mName);
        dostream.writeInt(mChesses.size());
        for (int i = 0; i < mChesses.size(); ++i){
            mChesses.get(i).printToOstream(dostream);
        }
        dostream.writeInt(mChesses.indexOf(mMainChess));
    }

    public String getName() {
        return mName;
    }
}
