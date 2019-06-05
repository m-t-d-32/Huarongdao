package club.llyronx.llyrichuarongdao;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

class HrdChess {
    private String mName;
    private Point mBegin;
    private int mOccupyWidth, mOccupyHeight;

    HrdChess(String name, int beginX, int beginY, int Width, int Height){
        mName = name;
        mBegin = new Point(beginX, beginY);
        mOccupyWidth = Width;
        mOccupyHeight = Height;
    }

    HrdChess(DataInputStream distream) throws IOException {
        mName = distream.readUTF();
        int m_beginx = distream.readInt();
        int m_beginy = distream.readInt();
        mBegin = new Point(m_beginx, m_beginy);
        mOccupyWidth = distream.readInt();
        mOccupyHeight = distream.readInt();
    }

    Point getBegin(){
        return new Point(mBegin.x, mBegin.y);
    }

    void setBegin(Point beginPos) {
        mBegin.x = beginPos.x;
        mBegin.y = beginPos.y;
    }

    int getWidth(){
        return mOccupyWidth;
    }

    int getHeight(){
        return mOccupyHeight;
    }

    String getName(){
        return mName;
    }

    void printToOstream(DataOutputStream dostream) throws IOException {
        dostream.writeUTF(mName);
        dostream.writeInt(mBegin.x);
        dostream.writeInt(mBegin.y);
        dostream.writeInt(mOccupyWidth);
        dostream.writeInt(mOccupyHeight);
    }
}

class Point{
    int x, y;
    Point(int x, int y){
        this.x = x;
        this.y = y;
    }
}
