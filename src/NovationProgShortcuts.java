import com.confusionists.mjdjApi.midi.MessageWrapper;
import com.confusionists.mjdjApi.midi.ShortMessageWrapper;
import com.confusionists.mjdjApi.morph.AbstractMorph;
import com.confusionists.mjdjApi.morph.DeviceNotFoundException;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.transport.udp.OSCPortOut;

import javax.sound.midi.ShortMessage;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Collections;

public class NovationProgShortcuts extends AbstractMorph{
        OSCPortOut sender;

        @Override
        public String getName() {
            return "Novation PROGRAMMING";
        }

        @Override
        public void init() throws DeviceNotFoundException {
            try {
                sender = new OSCPortOut(InetAddress.getLocalHost(), 8000);
                getService().log(InetAddress.getLocalHost().toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        public boolean process(MessageWrapper messageWrapper, String s) throws Throwable {
            ShortMessageWrapper shortMessageWrapper = messageWrapper.getAsShortMessageWrapper();
            ShortMessage miniMessage = shortMessageWrapper.getShortMessage();

            if (shortMessageWrapper.getData2() == 127 && miniMessage.getChannel() == 1) {
                OSCMessage msg = new OSCMessage("/exec/7/" + (shortMessageWrapper.getData1()-35) + "/", Collections.singletonList(1));
                //getService().log((shortMessageWrapper.getData1()-35) + " NOVATION");
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
