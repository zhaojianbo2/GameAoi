package game.scene.msg;

/**
 * 发送消息
 */
public class SMessage {

    public SMessage(int id, byte[] data) {
        this.id = id;
        this.data = data;
    }
    
    public SMessage(int id, int status) {
        this.id = id;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public byte[] getData() {
        return data;
    }

    /** 消息id **/
    private int id;
    /** 消息内容 **/
    private byte[] data;
    /** 状态码 0-正常  非0-异常 **/
    private int status;


    public void setId(int id) {
        this.id = id;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public void clear() {
        this.data = null;
        this.status = 0;
    }
}
