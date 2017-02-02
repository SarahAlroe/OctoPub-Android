package dk.alroe.apps.octopub.model;

/**
 * Created by silasa on 2/2/17.
 */

public class UploadResponse {
    public String jsonrpc;
    public String id;

    public UploadResult getResult() {
        return result;
    }

    public UploadResult result;

    public UploadResponse(UploadResult result) {
        this.result = result;
    }
}
