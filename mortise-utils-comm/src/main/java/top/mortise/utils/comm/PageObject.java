package top.mortise.utils.comm;

import java.util.List;

/**
 * Created by Eric.Zhang on 2017/4/6.
 */
public class PageObject<T> {
    private int paeeSize;
    private  int pageIndex;
    private int allCount;
    private List<T> data;

    public int getPaeeSize() {
        return paeeSize;
    }

    public void setPaeeSize(int paeeSize) {
        this.paeeSize = paeeSize;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getAllCount() {
        return allCount;
    }

    public void setAllCount(int allCount) {
        this.allCount = allCount;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
