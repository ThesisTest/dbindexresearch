package cn.luoyulingfeng.dbindexresearch.common;

public class InfoJsonResult extends JsonResult{
    private Object data;

    public InfoJsonResult(){

    }

    public InfoJsonResult(int code, String msg, Object data) {
        super(code, msg);
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
