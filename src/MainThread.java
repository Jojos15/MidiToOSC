import com.confusionists.mjdjApi.midi.MessageWrapper;
import com.confusionists.mjdjApi.midi.ShortMessageWrapper;
import com.confusionists.mjdjApi.morph.AbstractMorph;
import com.confusionists.mjdjApi.morph.DeviceNotFoundException;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.transport.udp.OSCPort;
import com.illposed.osc.transport.udp.OSCPortOut;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Collections;

public class MainThread extends AbstractMorph {

    OSCPortOut sender;
    boolean swap = false;

    @Override
    public String getName() {
        return "MainThread";
    }

    @Override
    public void init() throws DeviceNotFoundException {

        try {
            sender = new OSCPortOut(InetAddress.getLocalHost(), 8000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean process(MessageWrapper messageWrapper, String s) throws Throwable {
        ShortMessageWrapper shortMessageWrapper = messageWrapper.getAsShortMessageWrapper();

        if(shortMessageWrapper.isNoteOn()){
            if(shortMessageWrapper.getData1()!=98) {
                int posy = shortMessageWrapper.getData1() / 8;
                int posx = shortMessageWrapper.getData1() % 8;
                int gridPos = (7 - posy) * 8 + posx;

                getService().log("message sent");

                OSCMessage msg = new OSCMessage("/exec/1/" + gridPos + "/", Collections.singletonList(1));

                sender.send(msg);
            }
            else {
                if(swap) {
                    OSCMessage msg = new OSCMessage("/swap", Collections.singletonList(1));
                    swap = true;
                }
                else{
                    OSCMessage msg = new OSCMessage("/swap", Collections.singletonList(0));
                    swap = true;
                }
            }
        }
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
