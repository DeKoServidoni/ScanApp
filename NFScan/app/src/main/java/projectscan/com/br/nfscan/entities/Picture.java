package projectscan.com.br.nfscan.entities;

/**
 * Class responsible to hold the text filtered information from the picture
 * by the tesseract
 *
 * Created by DeKoServidoni on 10/15/15.
 */
public class Picture {

    private String mCpf = null;
    private String mValue = null;
    private String mCode = null;

    public String getCpf() {
        return mCpf;
    }

    public void setCpf(String cpf) {
        mCpf = cpf;
    }

    public String getValue() {
        return mValue;
    }

    public void setValue(String value) {
        mValue = value;
    }

    public String getCode() {
        return mCode;
    }

    public void setCode(String code) {
        mCode = code;
    }
}
