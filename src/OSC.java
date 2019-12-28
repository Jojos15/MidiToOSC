import com.confusionists.mjdjApi.midi.MessageWrapper;
import com.confusionists.mjdjApi.midi.ShortMessageWrapper;
import com.confusionists.mjdjApi.morph.AbstractMorph;
import com.confusionists.mjdjApi.morph.DeviceNotFoundException;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.transport.udp.OSCPort;
import com.illposed.osc.transport.udp.OSCPortOut;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Collections;

import static java.lang.Math.round;

public class OSC extends AbstractMorph {

    OSCPortOut sender;
    boolean swap = false;
    boolean state[] = new boolean[64];

    @Override
    public String getName() {
        return "OSC to Midi";
    }

    @Override
    public void init() throws DeviceNotFoundException {

        try {
            sender = new OSCPortOut(InetAddress.getLocalHost(), 8000);
            getService().log(InetAddress.getLocalHost().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(int i=0; i<64; i++){
            state[i] = false;
        }
        int light = 1;
        for(int i=0; i<100; i++){
            if(i%8==0){
                light +=2;
            }

            try {
                getService().send(MessageWrapper.newInstance(new ShortMessage(ShortMessage.NOTE_ON, 0, i, light)));
            } catch (InvalidMidiDataException e) {
                e.printStackTrace();
            }
            if(light == 5){
                light = 1;
            }
        }
    }

    @Override
    public boolean process(MessageWrapper messageWrapper, String s) throws Throwable {
        ShortMessageWrapper shortMessageWrapper = messageWrapper.getAsShortMessageWrapper();

        if(shortMessageWrapper.isNoteOn()){
            if(shortMessageWrapper.getData1()>=64&&shortMessageWrapper.getData1()<=71){
                int pos = ((shortMessageWrapper.getData1()+6) % 10) + 1;
                OSCMessage msg = new OSCMessage("/pb/" + pos + "/pause");
                sender.send(msg);
                return false;
            }
            if(shortMessageWrapper.getData1()!=98) {
                int posy = shortMessageWrapper.getData1() / 8;
                int posx = shortMessageWrapper.getData1() % 8;
                int gridPos = (7 - posy) * 8 + posx + 1;
                /*if(!state[shortMessageWrapper.getData1()]) {
                    OSCMessage msg = new OSCMessage("/exec/2/" + gridPos + "/", Collections.singletonList(1));
                    sender.send(msg);
                    state[shortMessageWrapper.getData1()] = !state[shortMessageWrapper.getData1()];
                }
                else{
                    OSCMessage msg = new OSCMessage("/exec/2/" + gridPos + "/", Collections.singletonList(0));
                    sender.send(msg);
                    state[shortMessageWrapper.getData1()] = !state[shortMessageWrapper.getData1()];
                }*/
                OSCMessage msg = new OSCMessage("/exec/2/" + gridPos + "/", Collections.singletonList(1));
                sender.send(msg);
            }
            else {
                getService().log("SWAP");
                if(!swap) {
                    OSCMessage msg = new OSCMessage("/swap/", Collections.singletonList(1));
                    sender.send(msg);
                    swap = true;
                }
                else{
                    OSCMessage msg = new OSCMessage("/swap/", Collections.singletonList(0));
                    sender.send(msg);
                    swap = false;
                }
            }
        }
        else if(shortMessageWrapper.isControlChange()){
            int pos = ((shortMessageWrapper.getData1() + 2) % 10) + 65;

            int vol = (int) round((100.0/127.0) * shortMessageWrapper.getData2());

            OSCMessage msg = new OSCMessage("/exec/2/" + pos + "/", Collections.singletonList(vol));

            sender.send(msg);
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
