package si.um.feri.app.model;

public class OBUIdResponse {

    private String obuid;

    public OBUIdResponse() {
    }

    public String getObuid() {
        return obuid;
    }

    public void setObuid(String obuid) {
        this.obuid = obuid;
    }

    @Override
    public String toString() {
        return "OBUIdResponse{" +
                "obuid='" + obuid + '\'' +
                '}';
    }
}
