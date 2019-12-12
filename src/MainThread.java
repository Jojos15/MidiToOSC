import com.confusionists.mjdjApi.midi.MessageWrapper;
import com.confusionists.mjdjApi.morph.AbstractMorph;
import com.confusionists.mjdjApi.morph.DeviceNotFoundException;

public class MainThread extends AbstractMorph {
    @Override
    public String getName() {
        return "MainThread";
    }

    @Override
    public void init() throws DeviceNotFoundException {
        
    }

    @Override
    public boolean process(MessageWrapper messageWrapper, String s) throws Throwable {
        return false;
    }

    @Override
    public String diagnose() {
        return null;
    }

    @Override
    public Object getSerializable() {
        return null;
    }

    @Override
    public void setSerializable(Object o) {

    }
}
