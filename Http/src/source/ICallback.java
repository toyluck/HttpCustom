package source;

/**
 * Created by hyc on 16-11-9.
 */
public interface ICallback {

    void onSuccessed(String response);

    void onFailure(Exception err);
}
