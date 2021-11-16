package com.cu1.community.Utils;

/**
 * 封装处理分页相关的信息
 */
@SuppressWarnings("all")
public class PagePaginationUtils {

    //当前的页码
    private int current = 1;
    //项式数据条数的上限
    private int limit = 10;
    //数据的总数 (用于计算总的页数)
    private int rows;
    //查询路径 (用来复用分页的链接)
    private String path;

    /**
     * 获取当前页的起始行
     * @return
     */
    public int getOffset() {
        // current * limit - limit 从 0 计数
        return (current - 1) * limit;
    }

    /**
     * 获取总页数
     * @return
     */
    public int getTotal() {
        //总行数 / 每一页数据 + 1;
        if (rows % limit == 0) {
            return rows / limit;
        }
        return rows / limit + 1;
    }

    /**
     * 获取起始页码
     * @return
     */
    public int getFrom() {
        int from = current - 2;
        return from < 1 ? 1 : from;
    }

    /**
     * 获取结束页码
     * @return
     */
    public int getTo() {
        int to = current + 2;
        return to > getTotal() ? getTotal() : to;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if (current >= 1) {
            this.current = current;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if (limit >= 1 && limit <= 100) {
            this.limit = limit;
        }
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if (rows >= 0) {
            this.rows = rows;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
