package coppelia;

public class IntWA
{
    int[] w;

    /**
     * 创建int型数组
     * @param i 数组长度
     */
    public IntWA(int i)
    {
        w = new int[i];
    }

    /**
     * 初始化数组
     * @param i 数组长度
     */
    public void initArray(int i)
    {
        w = new int[i];
    }

    /**
     * 获取数组长度
     * @return 长度
     */
    public int getLength()
    {
        return w.length;
    }

    /**
     * 设置新数组
     * @param i 数组长度
     * @return 新的数组
     */
    public int[] getNewArray(int i)
    {
        w = new int[i];
        return w;
    }

    /**
     * 获取数组
     * @return 数组
     */
    public int[] getArray()
    {
        return w;
    }

    /**
     * 从int型数组获取char型数组
     * @return char数组
     */
    public char[] getCharArrayFromArray()
    {
        char[] a=new char[4*w.length];
        for (int i=0;i<w.length;i++)
        {
            a[4*i+0]=(char)(w[i]&0xff); 
            a[4*i+1]=(char)((w[i] >>> 8)&0xff);
            a[4*i+2]=(char)((w[i] >>> 16)&0xff);
            a[4*i+3]=(char)((w[i] >>> 24)&0xff);
        }
        return a;
    }

    /**
     * 从char数组初始化int数组
     * @param a char数组
     */
    public void initArrayFromCharArray(char[] a)
    {
        w = new int[a.length/4];
        for (int i=0;i<a.length/4;i++)
            w[i]=(int)(((a[4*i+3]&0xff) << 24) + ((a[4*i+2]&0xff) << 16) + ((a[4*i+1]&0xff) << 8) + (a[4*i+0]&0xff));
    }
}
